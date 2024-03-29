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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.owasp.html.HtmlChangeListener;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

/**
 * Custom HTML sanitization serializer.
 *
 * @author ben.laney
 * @since 4.4
 */
public class StringSanitizationSerializer extends StdSerializer<String> implements ContextualSerializer {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(StringSanitizationSerializer.class);

    private static final long serialVersionUID = 1027138718748213L;

    private static final String EMPTY_QUOTES_MARKER_STRING = "&#34;&#34;";

    private static final StringSerializer DEFAULT_STR_SERIALIZER = new StringSerializer();

    private static final List<String> ALLOW_ELEMENTS =
        getSysPropertyOrDefault(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ELEMS);

    private static final List<String> ALLOW_ATTRS =
        getSysPropertyOrDefault(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ATTRS);

    private static final boolean ALLOW_CSS = BooleanUtils.isNotTrue(
        StringUtils.equalsIgnoreCase(System.getProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_CSS), "false"));

    /**
     * The HTML Policy which is used to sanitize user data.
     */
    @SuppressWarnings("java:S1948")
    private final HtmlPolicyBuilder m_policyBuilder;

    /**
     * Custom String HTML sanitization serializer.
     *
     * @see #StringSanitizationSerializer(List, List, boolean)
     *
     * Uses the available system properties (if set) for allowElems, allowAttrs and allowCSS
     * @since 5.2
     *
     */
    public StringSanitizationSerializer() {
        this(ALLOW_ELEMENTS, ALLOW_ATTRS, ALLOW_CSS);
    }

    /**
     * Custom String HTML sanitization serializer.
     *
     * With the provided parameters, the class creates an internal sanitization policy to process string input and
     * output strings which which are compliant with the configured policy for the purpose of safely rendering in a
     * WebBrowser. The default policy is configured using OWASP standards and recommendations, but these can be
     * overwritten with said parameters.
     *
     * This may be desired whenever there is data from an unknown source being displayed in a visualization or web
     * application of some kind. No browser or data is 100% safe, but the parsing provided by this class can improve
     * data security in highly sensitive applications.
     *
     * This process is not cost-free. Possible trade-offs include: - *performance* parsing large data may be
     * significantly slower than default serialization. - *styling* output strings are often mangled and altered to
     * prevent even the chance of malicious behavior when delivered to the client. - *functionality* some data may
     * completely break the application as its format is sanitized beyond recognition.
     *
     * For these reasons, this serialization method should be used sparingly (i.e. only on nodes which are expected to
     * pose a potential risk or in certain applications where security concerns take precedent over the trade-offs
     * previously mentioned). Finding the right balance for the application is essential to building modern, secure web
     * applications.
     *
     * @param allowElems - HTML element tags which should be allowed in sanitized output (overrides OWASP suggested)
     * @param allowAttrs - HTML attributes which should be allowed in sanitized output (overrides OWASP suggested)
     * @param allowCSS - allow limited CSS styles in sanitized output
     */
    public StringSanitizationSerializer(final List<String> allowElems, final List<String> allowAttrs,
        final boolean allowCSS) {
        super(String.class);
        m_policyBuilder = createPolicy(allowElems, allowAttrs, allowCSS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final String value, final JsonGenerator gen, final SerializerProvider provider)
        throws IOException {
        gen.writeString(sanitize(value));
    }

    /**
     * Use the member {@link HtmlPolicyBuilder} to attempt to sanitize the input string. Sanitization is greedy and if
     * pre-processing of strings is possible to reduce or remove the risk of Cross-Site-Script injection, then it's
     * recommended to try this approach (as functionality or aesthetic details may be lost during the serialization
     * process).
     *
     * @param dirtyString
     * @return cleanString
     * @since 5.2
     */
    public String sanitize(final String dirtyString) {
        StringBuilder sanitizedValueBuilder = new StringBuilder();

        var streamRenderer = HtmlStreamRenderer.create(sanitizedValueBuilder,
            stringViolation -> LOGGER.debug("Sanitization policy violation detected: " + stringViolation));

        var changeListener = new HtmlChangeListener<StringSanitizationSerializer>() {
            @Override
            public void discardedTag(final StringSanitizationSerializer context, final String elementName) {
                LOGGER.debug("HTML sanitization policy violation detected, discarded tag " + elementName);
            }

            @Override
            public void discardedAttributes(final StringSanitizationSerializer context, final String elementName,
                final String... attributeNames) {
                LOGGER.debug("HTML sanitization policy violation detected, discarded attribute(s) "
                    + Arrays.toString(attributeNames) + " on tag " + elementName);
            }
        };

        HtmlSanitizer.sanitize(dirtyString, m_policyBuilder.build(streamRenderer, changeListener, this));

        String newString = sanitizedValueBuilder.toString();

        /*
         * Filter out empty attributes which are quoted. These are encoded empty quoted strings (either from the
         * POJO field being an empty string or from the "safe" sanitization output being completely eliminated.
         * We filter to allow the front-end (web) to ignore them properly- otherwise lots of JS View configuration
         * options which may not have been desired will render (e.g. empty "" for labels in all the widgets).
         */
        return StringUtils.equals(EMPTY_QUOTES_MARKER_STRING, newString) ? "" : newString;
    }

    /**
     * Create a new sanitization policy.
     *
     * @return built policy
     */
    private static HtmlPolicyBuilder createPolicy(final List<String> allowElems, final List<String> allowAttrs,
        final boolean allowCSS) {
        HtmlPolicyBuilder policyBuilder = new HtmlPolicyBuilder();

        if (allowElems.isEmpty()) {
            // Use OWASP default policies
            policyBuilder
                .allowCommonInlineFormattingElements()
                .allowStandardUrlProtocols()
                .allowCommonBlockElements();
        } else {
            try {
                policyBuilder.allowElements(allowElems.toArray(new String[0]));
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Could not apply allowed elements to sanitization policy.", ex);
            }
        }

        if (!allowAttrs.isEmpty()) {
            try {
                policyBuilder.allowAttributes(allowAttrs.toArray(new String[0])).globally();
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Could not apply allowed attributes to sanitization policy.", ex);
            }
        }

        if (allowCSS) {
            policyBuilder.allowStyling();
        }

        return policyBuilder;
    }

    /**
     * Helper to retrieve the value of a comma-separated {@literal String} system property (if it has been defined) and
     * return an array of strings; else an empty array.
     *
     * @param propertyName - system property name to retrieve and parse
     * @return array of parsed strings from the defined property value else an empty string array
     */
    private static List<String> getSysPropertyOrDefault(final String propertyName) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(propertyValue.split(",")).stream().map(String::trim).collect(ArrayList::new,
            ArrayList::add, ArrayList::addAll);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property)
        throws JsonMappingException {
        JsonSanitizeIgnore ignoreAnnotation = property.getMember().getAnnotation(JsonSanitizeIgnore.class);
        if (ignoreAnnotation != null) {
            return DEFAULT_STR_SERIALIZER;
        }
        return this;
    }

    /**
     * Annotation for class fields which should be sanitized.
     *
     * @author ben.laney
     * @since 4.4
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsonSanitize {
        //
    }

    /**
     * Annotation for class fields which should be explicitly ignored during sanitization.
     *
     * @author ben.laney
     * @since 4.4
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsonSanitizeIgnore {
        //
    }
}
