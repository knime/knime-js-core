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
 *   25 Apr 2017 (albrecht): created
 */
package org.knime.core.wizard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * View value for combined subnode view, contains of map of contained view values. <br/>
 *
 * A note on the custom de-/serializers: This value reads/writes from/into generic json-objects internally represented
 * as a string-to-string map. The map's keys are the top-level fields (i.e., the node-ids) of the json-object and the
 * map's values are the 'sub-json-objects' as string (i.e., the nodes view values, without any escaping!).
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonSerialize(using = SubnodeViewValue.CustomSerializer.class)
@JsonDeserialize(using = SubnodeViewValue.CustomDeserializer.class)
public class SubnodeViewValue extends JSONViewContent {

    private static final String CLASS_KEY = "@class";

    private static final NodeLogger LOGGER = NodeLogger.getLogger(SubnodeViewValue.class);

    private Map<String, String> m_viewValues = new HashMap<>();

    /**
     * @return the viewValues
     */
    public Map<String, String> getViewValues() {
        return m_viewValues;
    }

    /**
     * @param viewValues the viewValues to set
     */
    public void setViewValues(final Map<String, String> viewValues) {
        m_viewValues = viewValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) { /* nothing to save */ }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException { /* nothing to load */ }

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
        EqualsBuilder builder = new EqualsBuilder();
        ObjectMapper mapper = new ObjectMapper();
        SubnodeViewValue other = (SubnodeViewValue)obj;
        for (String key : m_viewValues.keySet()) {
            try {
                Map<String, JsonNode> localValueMap = mapWithoutClass(mapper.readTree(m_viewValues.get(key)));
                // Check if the incoming object returned a value for the nodeId key.
                if (other.m_viewValues.containsKey(key)) {
                    Map<String, JsonNode> otherValueMap = mapWithoutClass(mapper.readTree(other.m_viewValues.get(key)));
                    builder.append(localValueMap, otherValueMap);
                } else {
                    // Check for "false" inequality caused by a missing node view value. If the local value only contains a
                    // "@class" attribute (and is empty after removing that), we can assume the missing view value was expected.
                    if (!localValueMap.isEmpty()) {
                        throw new IllegalArgumentException(
                            "Missing client-side view value for a node where one was expected (non-output).");
                    }
                }
            } catch (Exception e) {
                LOGGER.debug("Can't compare JsonNode in #equals", e);
                //compare strings on exception
                builder.append(m_viewValues.get(key), other.m_viewValues.get(key));
            }
        }
        return builder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_viewValues)
                .toHashCode();
    }

    /**
     *
     * Create a map from a JsonNode while removing the "@class" attribute.
     *
     * @param node the JsonNode to map.
     * @return a map (depth of 1) of the provided JsonNode with the "@class" property removed.
     */
    private static Map<String, JsonNode> mapWithoutClass(final JsonNode node) {
        Map<String, JsonNode> nodeMap = new HashMap<>();
        node.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            if (!CLASS_KEY.contentEquals(key)) {
                nodeMap.put(key, entry.getValue());
            }
        });
        return nodeMap;
    }

    private static class CustomSerializer extends StdSerializer<SubnodeViewValue> {

        private static final long serialVersionUID = 1L;

        protected CustomSerializer() {
            super(SubnodeViewValue.class);
        }

        @Override
        public void serializeWithType(final SubnodeViewValue value, final JsonGenerator gen, final SerializerProvider serializers,
            final TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }

        @Override
        public void serialize(final SubnodeViewValue value, final JsonGenerator gen,
            final SerializerProvider serializers) throws IOException {
            // TODO write type id?
            gen.writeStartObject();
            for (Entry<String, String> entry : value.m_viewValues.entrySet()) {
                gen.writeFieldName(entry.getKey());
                gen.writeRawValue(entry.getValue());
            }
            gen.writeEndObject();
        }
    }

    private static class CustomDeserializer extends StdDeserializer<SubnodeViewValue> {

        private static final long serialVersionUID = 1L;

        protected CustomDeserializer() {
            super(SubnodeViewValue.class);
        }

        @Override
        public SubnodeViewValue deserializeWithType(final JsonParser p, final DeserializationContext ctxt,
            final TypeDeserializer typeDeserializer, final SubnodeViewValue intoValue) throws IOException {
            JsonNode tree = p.readValueAs(JsonNode.class);
            tree.fields().forEachRemaining(e -> intoValue.m_viewValues.put(e.getKey(), e.getValue().toString()));
            return intoValue;
        }

        @Override
        public SubnodeViewValue deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return deserializeWithType(p, ctxt, null, new SubnodeViewValue());
        }

        @Override
        public SubnodeViewValue deserialize(final JsonParser p, final DeserializationContext ctxt, final SubnodeViewValue intoValue)
            throws IOException {
            return deserializeWithType(p, ctxt, null, intoValue);
        }

    }

}