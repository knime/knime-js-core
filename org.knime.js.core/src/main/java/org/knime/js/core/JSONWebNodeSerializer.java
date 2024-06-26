/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   Mar 16, 2021 (ben.laney): created
 */
package org.knime.js.core;

import static org.knime.js.core.SanitizationUtils.ALLOW_ATTRIBUTES;
import static org.knime.js.core.SanitizationUtils.ALLOW_CSS;
import static org.knime.js.core.SanitizationUtils.ALLOW_ELEMENTS;
import static org.knime.js.core.SanitizationUtils.shouldSanitizeNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.knime.core.node.KNIMEConstants;
import org.knime.js.core.StringSanitizationSerializer.JsonSanitize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import jakarta.json.stream.JsonGenerationException;

/**
 * A {@link JSONWebNode} specific serialization implementation required to properly (and conditionally) sanitize user
 * data which may be rendered in the browser and still allow configuration of a node-allow list via
 * {@link KNIMEConstants} system properties. We must have access to the {@link JSONWebNode.nodeInfo} (which contains the
 * node name as found in the title of the node description) to allow selective node sanitization
 * {@link viewRepresentation} and {@link viewValue} fields do not contain information which is necessarily unique (as
 * there are no other node UUID's the user/admin might have knowledge of available to our serialization logic here, at
 * this late stage of flight processing)
 *
 * Otherwise, we could might use the {@link JSONWebNodeModifier} and override
 * {@link BeanSerializerModifier.changeProperties} to conditionally assign {@link JSONSanitizationSerializer} to the
 * {@link JSONWebNode.viewRepresentation} and {@link JSONWebNode.viewValue} fields or just define the custom serializers
 * directly on those member properties.
 *
 * @author ben.laney
 * @since 4.4
 */
@SuppressWarnings("java:S1948")
class JSONWebNodeSerializer extends StdSerializer<JSONWebNode> {

    private static final long serialVersionUID = 3247239167142L;

    private final JsonSerializer<JSONWebNode> m_defaultSerializer;

    private final BeanDescription m_beanDescription;

    /**
     * @param serializer - default serializer
     * @param beanDesc - JSONWebNode serialization description
     */
    JSONWebNodeSerializer(final JsonSerializer<JSONWebNode> serializer, final BeanDescription beanDesc) {
        super(JSONWebNode.class);

        m_defaultSerializer = serializer;
        m_beanDescription = beanDesc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void serializeWithType(final JSONWebNode value, final JsonGenerator jgen,
        final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        typeSer.writeTypePrefixForObject(value, jgen);
        serialize(value, jgen, provider);
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final JSONWebNode value, final JsonGenerator jgen, final SerializerProvider provider)
        throws IOException {

        // check if node is configured to be sanitized
        boolean shouldSanitizeNode = shouldSanitizeNode(value.getNodeInfo());

        Set<String> ignoredProperties = m_beanDescription.getIgnoredPropertyNames();

        Iterator<PropertyWriter> nodeProperties = m_defaultSerializer.properties();

        // Copy of existing mapper with custom String serializer module
        ObjectMapper mapper = ((ObjectMapper)jgen.getCodec()).copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new StringSanitizationSerializer(ALLOW_ELEMENTS, ALLOW_ATTRIBUTES, ALLOW_CSS));
        mapper.registerModule(module);

        while (nodeProperties.hasNext()) {
            PropertyWriter writer = nodeProperties.next();
            String jsonPropertyName = writer.getName();
            JsonSanitize sanitizeAnnotation = writer.getMember().getAnnotation(JsonSanitize.class);

            // skip missing/ignored properties
            if (jsonPropertyName == null || ignoredProperties.contains(jsonPropertyName)) {
                return;
            }
            /* Sanitize if current node has not been explicitly excluded and current property has the sanitize
             annotation (JsonSanitize); else default serialization. */
            if (shouldSanitizeNode && sanitizeAnnotation != null) {
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
}
