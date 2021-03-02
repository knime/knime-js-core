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

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Custom HTML sanitization serializer.
 *
 * @author ben.laney
 * @since 4.4
 */
public class StringSanitizationSerializer extends StdSerializer<String> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(StringSanitizationSerializer.class);

    private static final long serialVersionUID = 1027138718748213L;

    /**
     * The HTML Policy which is used to sanitize user data.
     */
    private HtmlPolicyBuilder m_policyBuilder;

    /**
     * Custom String HTML sanitization serializer.
     *
     * With the provided parameters, the class creates an internal sanitization policy to process string input
     * and output strings which which are compliant with the configured policy for the purpose of safely rendering
     * in a WebBrowser. The default policy is configured using OWASP standards and recommendations, but these can
     * be overwritten with said parameters.
     *
     * This may be desired whenever there is data from an unknown source being displayed in a visualization or web
     * application of some kind. No browser or data is 100% safe, but the parsing provided by this class can improve
     * data security in highly sensitive applications.
     *
     * This process is not cost-free. Possible trade-offs include:
     *      - *performance* parsing large data may be significantly slower than default serialization.
     *      - *styling* output strings are often mangled and altered to prevent even the chance of malicious behavior
     *        when delivered to the client.
     *      - *functionality* some data may completely break the application as its format is sanitized beyond recognition.
     *
     * For these reasons, this serialization method should be used sparingly (i.e. only on nodes which are expected
     * to pose a potential risk or in certain applications where security concerns take precedent over the trade-offs
     * previously mentioned). Finding the right balance for the application is essential to building modern, secure
     * web applications.
     *
     * @param allowElem - HTML element tags which should be allowed in sanitized output (overrides OWASP suggested)
     * @param allowAttr - HTML attributes which should be allowed in sanitized output (overrides OWASP suggested)
     * @param allowStyles - allow limited CSS styles in sanitized output
     */
    public StringSanitizationSerializer(final String allowElem, final String allowAttr, final boolean allowStyles) {
        super(String.class);
        m_policyBuilder = createPolicy(allowElem, allowAttr, allowStyles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final String value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeString(sanitize(value));
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

        /*
         * Filter out empty attributes which are quoted. These are encoded empty quoted strings (either from the
         * POJO field being an empty string or from the "safe" sanitization output being completely eliminated.
         * We filter to allow the front-end (web) to ignore them properly- otherwise lots of JS View configuration
         * options which may not have been desired will render (e.g. empty "" for labels in all the widgets).
         */
        return StringUtils.equals("&#34;&#34;", newString) ? "" : newString;
    }

    /**
     * Create a new sanitization policy.
     *
     * @return built policy
     */
    private static HtmlPolicyBuilder createPolicy(final String allowElem, final String allowAttr, final boolean allowStyles) {
        HtmlPolicyBuilder policyBuilder = new HtmlPolicyBuilder();

        if (StringUtils.isNotEmpty(allowElem)) {
            try {
                policyBuilder.allowElements(allowElem.split(","));
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

        if (StringUtils.isNotEmpty(allowAttr)) {
            try {
                policyBuilder.allowAttributes(allowAttr.split(","));
            } catch (Exception ex) {
                LOGGER.error("Could not apply allowed attributes to sanitization policy.", ex);
            }
        }

        if (allowStyles) {
            policyBuilder.allowStyling();
        }

        return policyBuilder;
    }

}
