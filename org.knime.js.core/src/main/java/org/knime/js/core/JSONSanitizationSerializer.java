/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.js.core;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Custom HTML santization serializer.
 *
 * @author ben.laney
 * @param <T>
 * @since 4.4
 */
public abstract class JSONSanitizationSerializer<T> extends StdSerializer<T> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(JSONSanitizationSerializer.class);

    /** */
    private static final long serialVersionUID = 1027138718748213L;

    /**
     * The HTML Policy which is used to sanitize user data.
     */
    protected HtmlPolicyBuilder m_policyBuilder;

    /**
     * User-defined, comma-separated list of valid HTML elements which should be allowed. If none is defined,
     * OWASP defaults are used.
     */
    protected String m_allowElem;

    /**
     * User-defined, comma-separated list of valid HTML attributes which should be allowed. If none is defined,
     * OWASP defaults are used.
     */
    protected String m_allowAttr;

    /**
     * User-defined override of the default OWASP approved CSS styles. If none is defined, OWASP styles are used.
     */
    protected boolean m_allowStyles;

    /**
     * Custom HTML santization serializer.
     *
     * Reads in the current user-defined HTML sanitization config from the JS Core Plugin preferences and creates
     * an internal sanitization policy for creating a JSON representation with "safe" text field which can be more
     * safely rendered client side.
     *
     * This may be desired whenever there is data from an unknown source being displayed in a visualization or web
     * application of some kind. No browser or data is 100% safe, but the parsing provided by this class can improve
     * data security in highly sensitive application.
     *
     * The added security comes with a trade-off in performance (parsing large data may be significantly slower
     * than default serialization), styling (output strings are often mangled and altered to prevent even the chance
     * of malicious behavior when delivered to the client) and functionality (some data may completely break the
     * application as its format is sanitized beyond recognition).
     *
     * For these reasons, this serialization method should be used sparingly (i.e. only on nodes which are expected
     * to pose a potential risk or in certain applications where security concerns take precedent over the trade-offs
     * previously mentioned).
     *
     * @param t
     */
    protected JSONSanitizationSerializer(final Class<T> t) {
        super(t);
        m_allowElem = JSCorePlugin.getDefault().getPreferenceStore().getString(JSCorePlugin.P_ALLOW_ELEM);
        m_allowAttr = JSCorePlugin.getDefault().getPreferenceStore().getString(JSCorePlugin.P_ALLOW_ATTR);
        m_allowStyles = JSCorePlugin.getDefault().getPreferenceStore().getBoolean(JSCorePlugin.P_ALLOW_STYLES);
        m_policyBuilder = createPolicy();
    }

    /**
     * Recursive method to process, write and sanitize {@link JsonNode} trees. At each recursion, the node
     * type is checked before primitive values are written to the provided generator. Primitive values of
     * textual content are sanitized according to the member {@link HtmlPolicyBuilder}.
     *
     * @param jgen
     * @param node
     * @param mapper
     * @throws IOException
     */
    protected void serializeNode(final JsonGenerator jgen, final JsonNode node, final ObjectMapper mapper)
            throws IOException {

        Iterator<Entry<String, JsonNode>> nodeFieldIterator = node.fields();

        while(nodeFieldIterator.hasNext()) {
            Entry<String, JsonNode> nodeEntry = nodeFieldIterator.next();
            String nodeKey = nodeEntry.getKey();
            JsonNode nodeValue = nodeEntry.getValue();

            if (nodeValue.isNull()) {
                continue;
            }

            if (nodeValue.isValueNode()) {
                writeNodeAsObjectValue(jgen, nodeKey, nodeValue);
            } else { // handle complex types
                writeObjectNode(jgen, mapper, nodeKey, nodeValue);
            }
        }

        // close last JSON item
        if (jgen.getOutputContext().inArray()) {
            jgen.writeEndArray();
        } else {
            jgen.writeEndObject();
        }
    }

    private void serializeListItems(final JsonGenerator jgen, final JsonNode nodeList, final ObjectMapper mapper,
        final String nodeKey) throws IOException {
        Iterator<JsonNode> nodeIterator = nodeList.iterator();

        while(nodeIterator.hasNext()) {
            JsonNode node = nodeIterator.next();
            if (node.isValueNode()) {
                writeNodeAsArrayValue(jgen, node);
            } else { // handle complex types
                writeArrayNode(jgen, mapper, nodeKey, node);
            }
        }
    }

    /**
     * Helper method to write value nodes ({@link JsonNode} of primitive types) into current object context
     * of the provided {@link JsonGenerator}.
     *
     * @param jgen
     * @param nodeKey
     * @param nodeValue
     * @throws IOException
     */
    private void writeNodeAsObjectValue(final JsonGenerator jgen, final String nodeKey,
        final JsonNode nodeValue) throws IOException {
        if (!StringUtils.equals(nodeKey, "@class") && nodeValue.isTextual()) {
            // Sanitize text values (except for the class)
            jgen.writeStringField(nodeKey, sanitize(nodeValue.toString()));
        } else { // non-textual or class (no need to sanitize)
            jgen.writeObjectField(nodeKey, nodeValue);
        }
    }

    /**
     * Helper method to write complex {@link JsonNode} types ({@link ArrayNode} or {@link ObjectNode})
     * into current object context of the provided {@link JsonGenerator}.
     *
     * @param jgen
     * @param mapper
     * @param nodeKey
     * @param nodeValue
     * @throws IOException
     */
    private void writeObjectNode(final JsonGenerator jgen, final ObjectMapper mapper, final String nodeKey,
        final JsonNode nodeValue) throws IOException {
        if (nodeValue.isArray()) {
            jgen.writeArrayFieldStart(nodeKey);
            serializeListItems(jgen, nodeValue, mapper, nodeKey);
            jgen.writeEndArray();
        } else { // is Object node
            if (!jgen.getOutputContext().inArray()) {
                // ignore object field names nested in Arrays
                jgen.writeObjectFieldStart(nodeKey);
            }
            serializeNode(jgen, nodeValue, mapper);
        }
    }

    /**
     * Helper method to write value nodes ({@link JsonNode} of primitive types) into current array context
     * of the provided {@link JsonGenerator}.
     *
     * @param jgen
     * @param node
     * @throws IOException
     */
    private void writeNodeAsArrayValue(final JsonGenerator jgen, final JsonNode node) throws IOException {
        if (node.isTextual()) {
            jgen.writeString(sanitize(node.toString()));
        } else {
            jgen.writeTree(node);
        }
    }

    /**
     * Helper method to write complex {@link JsonNode} types ({@link ArrayNode} or {@link ObjectNode})
     * into current array context of the provided {@link JsonGenerator}.
     *
     * @param jgen
     * @param mapper
     * @param nodeKey
     * @param node
     * @throws IOException
     */
    private void writeArrayNode(final JsonGenerator jgen, final ObjectMapper mapper, final String nodeKey,
        final JsonNode node) throws IOException {
        if (node.isArray()) {
            jgen.writeStartArray();
            serializeListItems(jgen, node, mapper, nodeKey);
            jgen.writeEndArray();
        } else { // is Object node
            if (node.isObject()) {
                jgen.writeStartObject();
            }
            serializeNode(jgen, node, mapper);
        }
    }

    /**
     * Use the member {@link HtmlPolicyBuilder} to attempt to sanitize the input string. Sanitization is greedy
     * and if pre-processing of strings is possible to reduce or remove the risk of Cross-Site-Script injection,
     * then it's recommended to try this approach (as functionality or aesthetic details may be lost during the
     * serialization process).
     *
     * @param dirtyString
     * @return cleanString
     */
    private String sanitize(final String dirtyString) {
        StringBuilder sanitizedValueBuilder = new StringBuilder();

        HtmlStreamRenderer streamRenderer = HtmlStreamRenderer.create(sanitizedValueBuilder, stringViolation ->
            LOGGER.debug("Sanitization policy violation detected: " + stringViolation)
        );

        HtmlSanitizer.sanitize(dirtyString, m_policyBuilder.build(streamRenderer));

        String newString = sanitizedValueBuilder.toString();

        // filter out empty attributes which are quoted
        return StringUtils.equals("&#34;&#34;", newString) ? "" : newString;
    }

    /**
     * Create a sanitization policy based on the user defined configuration.
     *
     * @return built policy
     */
    private HtmlPolicyBuilder createPolicy() {
        HtmlPolicyBuilder policyBuilder = new HtmlPolicyBuilder();

        if (StringUtils.isNotEmpty(m_allowElem)) {
            try {
                policyBuilder.allowElements(m_allowElem.split(","));
            } catch (Exception ex) {
                LOGGER.error("Could not apply allowed elements to sanitization policy.", ex);
            }
        } else {
            // Use OWASP default policies
            policyBuilder
                .allowCommonInlineFormattingElements()
                .allowStandardUrlProtocols()
                .allowCommonBlockElements();
        }

        if (StringUtils.isNotEmpty(m_allowAttr)) {
            try {
                policyBuilder.allowAttributes(m_allowAttr.split(","));
            } catch (Exception ex) {
                LOGGER.error("Could not apply allowed attributes to sanitization policy.", ex);
            }
        }

        if (m_allowStyles) {
            policyBuilder.allowStyling();
        }

        return policyBuilder;
    }

}
