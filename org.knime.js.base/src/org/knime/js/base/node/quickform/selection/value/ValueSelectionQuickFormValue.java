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
package org.knime.js.base.node.quickform.selection.value;

import javax.json.Json;
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
 * The value for the value selection quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ValueSelectionQuickFormValue extends JSONViewContent implements DialogNodeValue {

    private static final String CFG_COLUMN = "column";

    private static final String DEFAULT_COLUMN = "";

    private String m_column = DEFAULT_COLUMN;

    private static final String CFG_VALUE = "value";

    private static final String DEFAULT_VALUE = "";

    private String m_value = DEFAULT_VALUE;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_column = settings.getString(CFG_COLUMN);
        setValue(settings.getString(CFG_VALUE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        m_column = settings.getString(CFG_COLUMN, DEFAULT_COLUMN);
        setValue(settings.getString(CFG_VALUE, DEFAULT_VALUE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_COLUMN, m_column);
        settings.addString(CFG_VALUE, getValue());
    }

    /**
     * @return the column
     */
    @JsonProperty("column")
    public String getColumn() {
        return m_column;
    }

    /**
     * @param column the column to set
     */
    @JsonProperty("column")
    public void setColumn(final String column) {
        m_column = column;
    }

    /**
     * @return the value
     */
    @JsonProperty("value")
    public String getValue() {
        return m_value;
    }

    /**
     * @param value the value to set
     */
    @JsonProperty("value")
    public void setValue(final String value) {
        m_value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("column=");
        sb.append(m_column);
        sb.append(", ");
        sb.append("value=");
        sb.append(m_value);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_column)
                .append(m_value)
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
        ValueSelectionQuickFormValue other = (ValueSelectionQuickFormValue)obj;
        return new EqualsBuilder()
                .append(m_column, other.m_column)
                .append(m_value, other.m_value)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Parameterization of ValueSelection not supported!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonObject) {
            try {
                JsonValue val = ((JsonObject) json).get(CFG_COLUMN);
                if (JsonValue.NULL.equals(val)) {
                    m_column = null;
                } else {
                    m_column = ((JsonObject) json).getString(CFG_COLUMN);
                }
            } catch (Exception e) {
                throw new JsonException("Expected column name for key '" + CFG_COLUMN + ".", e);
            }

            try {
                JsonValue val = ((JsonObject) json).get(CFG_VALUE);
                if (JsonValue.NULL.equals(val)) {
                    m_value = null;
                } else {
                    m_value = ((JsonObject) json).getString(CFG_VALUE);
                }
            } catch (Exception e) {
                throw new JsonException("Expected string value for key '" + CFG_VALUE + ".", e);
            }
        } else {
            throw new JsonException("Expected JSON object, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (m_column == null) {
            builder.addNull(CFG_COLUMN);
        } else {
            builder.add(CFG_COLUMN, m_column);
        }
        if (m_value == null) {
            builder.addNull(CFG_VALUE);
        } else {
            builder.add(CFG_VALUE, m_value);
        }
        return builder.build();
    }

}
