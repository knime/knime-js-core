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
 * ---------------------------------------------------------------------
 *
 * Created on 28.01.2014 by Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
package org.knime.js.core;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.json.stream.JsonGenerationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jface.preference.IPreferenceStore;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONWebNode {

    private JSONWebNodeInfo m_nodeInfo;
    private List<String> m_javascriptLibraries;
    private List<String> m_stylesheets;
    private String m_namespace;
    private String m_initMethodName;
    private String m_validateMethodName;
    private String m_setValidationErrorMethodName;
    private String m_getViewValueMethodName;

    private JSONViewContent m_viewRepresentation;
    private JSONViewContent m_viewValue;

    private String m_customCSS;

    /**
     * @return the nodeInfo
     */
    @JsonProperty("nodeInfo")
    public JSONWebNodeInfo getNodeInfo() {
        return m_nodeInfo;
    }

    /**
     * @param nodeInfo the nodeInfo to set
     */
    @JsonProperty("nodeInfo")
    public void setNodeInfo(final JSONWebNodeInfo nodeInfo) {
        m_nodeInfo = nodeInfo;
    }

    /**
     * @return the javascript libraries
     */
    @JsonProperty("javascriptLibraries")
    public List<String> getJavascriptLibraries() {
        return m_javascriptLibraries;
    }

    /**
     * @param javascriptLibraries
     */
    @JsonProperty("javascriptLibraries")
    public void setJavascriptLibraries(final List<String> javascriptLibraries) {
        m_javascriptLibraries = javascriptLibraries;
    }

    /**
     * @return the stylesheets
     */
    @JsonProperty("stylesheets")
    public List<String> getStylesheets() {
        return m_stylesheets;
    }

    /**
     * @param stylesheets
     */
    @JsonProperty("stylesheets")
    public void setStylesheets(final List<String> stylesheets) {
        m_stylesheets = stylesheets;
    }

    /**
     * @return the namespace
     */
    @JsonProperty("namespace")
    public String getNamespace() {
        return m_namespace;
    }

    /**
     * @param namespace
     */
    @JsonProperty("namespace")
    public void setNamespace(final String namespace) {
        m_namespace = namespace;
    }

    /**
     * @return the init method name
     */
    @JsonProperty("initMethodName")
    public String getInitMethodName() {
        return m_initMethodName;
    }

    /**
     * @param initMethodName
     */
    @JsonProperty("initMethodName")
    public void setInitMethodName(final String initMethodName) {
        m_initMethodName = initMethodName;
    }

    /**
     * @return the validate method name
     */
    @JsonProperty("validateMethodName")
    public String getValidateMethodName() {
        return m_validateMethodName;
    }

    /**
     * @param validateMethodName
     */
    @JsonProperty("validateMethodName")
    public void setValidateMethodName(final String validateMethodName) {
        m_validateMethodName = validateMethodName;
    }

    /**
     * @return the setValidationErrorMethodName
     */
    @JsonProperty("setValidationErrorMethodName")
    public String getSetValidationErrorMethodName() {
        return m_setValidationErrorMethodName;
    }

    /**
     * @param setValidationErrorMethodName the setValidationErrorMethodName to set
     */
    @JsonProperty("setValidationErrorMethodName")
    public void setSetValidationErrorMethodName(final String setValidationErrorMethodName) {
        m_setValidationErrorMethodName = setValidationErrorMethodName;
    }

    /**
     * @return the get view value method name
     */
    @JsonProperty("getViewValueMethodName")
    public String getGetViewValueMethodName() {
        return m_getViewValueMethodName;
    }

    /**
     * @param getViewValueMethodName
     */
    @JsonProperty("getViewValueMethodName")
    public void setGetViewValueMethodName(final String getViewValueMethodName) {
        m_getViewValueMethodName = getViewValueMethodName;
    }

    /**
     * @return the view representation
     */
    @JsonProperty("viewRepresentation")
    @JSONWebNodeSerializer.JsonSanitize
    public JSONViewContent getViewRepresentation() {
        return m_viewRepresentation;
    }

    /**
     * @param viewRepresentation
     */
    @JsonProperty("viewRepresentation")
    public void setViewRepresentation(final JSONViewContent viewRepresentation) {
        m_viewRepresentation = viewRepresentation;
    }

    /**
     * @return the view value
     */
    @JsonProperty("viewValue")
    @JSONWebNodeSerializer.JsonSanitize
    public JSONViewContent getViewValue() {
        return m_viewValue;
    }

    /**
     * @param viewValue
     */
    @JsonProperty("viewValue")
    public void setViewValue(final JSONViewContent viewValue) {
        m_viewValue = viewValue;
    }

    /**
     * @return the custom css
     */
    @JsonProperty("customCSS")
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the custom css to set
     */
    @JsonProperty("customCSS")
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        JSONWebNode other = (JSONWebNode)obj;
        return new EqualsBuilder()
                .append(m_nodeInfo, other.m_nodeInfo)
                .append(m_javascriptLibraries, other.m_javascriptLibraries)
                .append(m_stylesheets, other.m_stylesheets)
                .append(m_namespace, other.m_namespace)
                .append(m_initMethodName, other.m_initMethodName)
                .append(m_validateMethodName, other.m_validateMethodName)
                .append(m_setValidationErrorMethodName, other.m_setValidationErrorMethodName)
                .append(m_getViewValueMethodName, other.m_getViewValueMethodName)
                .append(m_viewRepresentation, other.m_viewRepresentation)
                .append(m_viewValue, other.m_viewValue)
                .append(m_customCSS, other.m_customCSS)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_nodeInfo)
                .append(m_javascriptLibraries)
                .append(m_stylesheets)
                .append(m_namespace)
                .append(m_initMethodName)
                .append(m_validateMethodName)
                .append(m_setValidationErrorMethodName)
                .append(m_getViewValueMethodName)
                .append(m_viewRepresentation)
                .append(m_viewValue)
                .append(m_customCSS)
                .toHashCode();
    }

    /**
     * A necessary serialization implementation required to properly (and conditionally) sanitize user data which
     * may be rendered in the browser and still allow configuration. We must have access to the Node Info to allow
     * specific nodes to be "sanitized" (as the {@link viewRepresentation} and {@link viewValue} fields do not contain
     * information which is necessarily node-specific or useful to the end user for configuration purposes).
     *
     * Otherwise, we could might use the {@link JSONWebNodeModifier} and override {@link BeanSerializerModifier.changeProperties}
     * to conditionally assign {@link JSONSanitizationSerializer} to the {@link viewRepresentation} and {@link viewValue} fields.
     *
     * @author ben.laney
     * @since 4.4
     */
    @JsonIgnoreType
    private static class JSONWebNodeSerializer extends StdSerializer<JSONWebNode> {

        private static final long serialVersionUID = 1L;

        /**
        * If the user preference calls for sanitization during serialization.
        */
        private boolean m_sanitize;

        /**
        * User-provided node names (human readable; e.g. "Table View", "Text Output Widget", etc.) defining which WebNodes should
        * be sanitized. Names are derived from the ...NodeFactory.xml name because this can the only uniquely identifying information
        * about the node contained in the {@link JSONWebNode} (as many serializable assets share class names, etc.).
        */
        private String[] m_sanitizedNodeNames;

        /**
         * User-defined, comma-separated list of valid HTML elements which should be allowed. If none is defined,
         * OWASP defaults are used.
         */
        private String m_allowElem;

        /**
         * User-defined, comma-separated list of valid HTML attributes which should be allowed. If none is defined,
         * OWASP defaults are used.
         */
        private String m_allowAttr;

        /**
         * User-defined override of the default OWASP approved CSS styles. If none is defined, OWASP styles are used.
         */
        private boolean m_allowStyles;

        private final JsonSerializer<Object> m_defaultSerializer;

        private final BeanDescription m_beanDescription;

        /**
         * @param serializer - default serializer
         * @param beanDesc - JSONWebNode serialization description
         */
        private JSONWebNodeSerializer(final JsonSerializer<Object> serializer, final BeanDescription beanDesc) {
            super(JSONWebNode.class);

            m_defaultSerializer = serializer;
            m_beanDescription = beanDesc;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final void serializeWithType(final JSONWebNode value, final JsonGenerator jgen, final SerializerProvider provider,
                final TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForObject(value, jgen);
            serialize(value, jgen, provider);
        };

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(final JSONWebNode value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {

            // check each invocation for changes
            updatePreferences();

            // check if node is configured to be sanitized
            String nodeName = value.getNodeInfo().getNodeName();
            List<String> sanitizeList = Arrays.asList(m_sanitizedNodeNames);
            boolean isSanitaryNode = sanitizeList.stream().anyMatch(sanitizedNodeName ->
                StringUtils.equals(StringUtils.trim(sanitizedNodeName), nodeName));

            Set<String> ignoredProperties = m_beanDescription.getIgnoredPropertyNames();

            Iterator<PropertyWriter> nodeProperties = m_defaultSerializer.properties();

            // Copy of existing mapper with custom String serializer module
            ObjectMapper mapper = ((ObjectMapper)jgen.getCodec()).copy();
            SimpleModule module = new SimpleModule();
            module.addSerializer(new StringSanitizationSerializer(m_allowElem, m_allowAttr, m_allowStyles));
            mapper.registerModule(module);

            while (nodeProperties.hasNext()) {
                PropertyWriter writer = nodeProperties.next();
                String jsonPropertyName = writer.getName();
                JsonSanitize sanitizeProperty = writer.getMember().getAnnotation(JsonSanitize.class);

                // skip missing/ignored properties
                if (jsonPropertyName == null || ignoredProperties.contains(jsonPropertyName)) {
                    return;
                }
                /*
                 * Sanitize if JS Preference set and current property annotation (JsonSanitize) and current node is on the
                 * user-defined node list; else default serialization.
                 */
                if (m_sanitize && isSanitaryNode && sanitizeProperty != null) {
                    jgen.writeFieldName(jsonPropertyName);
                    mapper.writeValue(jgen, writer.getMember().getValue(value));
                } else {
                    // Use default serializer.
                    try {
                        writer.serializeAsField(value, jgen, provider);
                    } catch (Exception ex) {
                        throw new JsonGenerationException("Default serialization for JSONWebNodePage failed.", ex);
                    }
                }
            }

            jgen.writeEndObject();

        }

        /**
         * Check the JS Preference page for user-defined serialization options and update internal members.
         */
        private void updatePreferences() {
            IPreferenceStore jsStore = JSCorePlugin.getDefault().getPreferenceStore();
            m_sanitize = jsStore.getBoolean(JSCorePlugin.P_SANITIZE_HTML_CONTENT);
            m_sanitizedNodeNames = jsStore.getString(JSCorePlugin.P_SANITIZED_NODES).split(",");
            m_allowElem = jsStore.getString(JSCorePlugin.P_ALLOW_ELEM);
            m_allowAttr = jsStore.getString(JSCorePlugin.P_ALLOW_ATTR);
            m_allowStyles = jsStore.getBoolean(JSCorePlugin.P_ALLOW_STYLES);
        }

        /**
         * Annotation for outer class fields which should be sanitized.
         *
         * @author ben.laney
         * @since 4.4
         */
        @Retention(RetentionPolicy.RUNTIME)
        @JacksonAnnotationsInside
        public @interface JsonSanitize {
            @SuppressWarnings("javadoc")
            boolean value() default true;
        }
    }

    /**
     * A serializer-provider modifier to be registered with the {@link ObjectMapper} module for the serialization parent
     * of the {@link JSONWebNode} class. The modifier intercepts the default serializer for the class and creates a custom
     * serializer which can perform both default and field-specific serializations.
     *
     * Importantly, by intercepting via modifier, we can access the default serializer at runtime and choose based on the current KNIME Node
     * identity if we want to use the default or custom serializer.
     *
     * @author ben.laney
     * @since 4.4
     */
    @JsonIgnoreType
    protected static class JSONWebNodeModifier extends BeanSerializerModifier {

        @SuppressWarnings("unchecked")
        @Override
        public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc,
            final JsonSerializer<?> serializer) {
            if (beanDesc.getBeanClass().equals(JSONWebNode.class)) {
                return new JSONWebNodeSerializer((JsonSerializer<Object>)serializer, beanDesc);
            }
            return serializer;
        }
    }
}
