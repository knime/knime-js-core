/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *
 * History
 *   14.10.2013 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.selection.multiple;

import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The value for the muliple selections quick form node.
 *
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MultipleSelectionQuickFormValue extends JSONViewContent implements DialogNodeValue {

    private static final String CFG_VARIABLE_VALUE = "variable_value";

    private static final String[] DEFAULT_VARIABLE_VALUE = new String[0];

    private String[] m_variableValue = DEFAULT_VARIABLE_VALUE;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addStringArray(CFG_VARIABLE_VALUE, m_variableValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setVariableValue(settings.getStringArray(CFG_VARIABLE_VALUE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        setVariableValue(settings.getStringArray(CFG_VARIABLE_VALUE, DEFAULT_VARIABLE_VALUE));
    }

    /**
     * @return the variableValue
     */
    @JsonProperty("value")
    public String[] getVariableValue() {
        return m_variableValue;
    }

    /**
     * @param variableValue the variableValue to set
     */
    @JsonProperty("value")
    public void setVariableValue(final String[] variableValue) {
        m_variableValue = variableValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("variableValue=");
        sb.append(Arrays.toString(m_variableValue));
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_variableValue)
                .toHashCode();
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
        MultipleSelectionQuickFormValue other = (MultipleSelectionQuickFormValue)obj;
        return new EqualsBuilder()
                .append(m_variableValue, other.m_variableValue)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Parameterization of MultipleSelection not supported!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonArray) {
            JsonArray array = (JsonArray) json;
            m_variableValue = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                m_variableValue [i] = array.getString(i);
            }
        } else if (json instanceof JsonObject) {
            try {
                JsonValue val = ((JsonObject) json).get(CFG_VARIABLE_VALUE);
                if (JsonValue.NULL.equals(val)) {
                    m_variableValue = null;
                } else {
                    JsonArray array = ((JsonObject) json).getJsonArray(CFG_VARIABLE_VALUE);
                    m_variableValue = new String[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        m_variableValue [i] = array.getString(i);
                    }
                }
            } catch (Exception e) {
                throw new JsonException("Expected valid string array for key '" + CFG_VARIABLE_VALUE + ".", e);
            }
        } else {
            throw new JsonException("Expected JSON object or JSON array, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (m_variableValue == null) {
            builder.addNull(CFG_VARIABLE_VALUE);
        } else {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String value : m_variableValue) {
                arrayBuilder.add(value);
                }
                builder.add(CFG_VARIABLE_VALUE, arrayBuilder);
            }
            return builder.build();
        }

}
