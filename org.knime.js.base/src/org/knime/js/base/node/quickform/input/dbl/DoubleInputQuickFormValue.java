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
package org.knime.js.base.node.quickform.input.dbl;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
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
 * The value for the double input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DoubleInputQuickFormValue extends JSONViewContent implements DialogNodeValue {

    private static final String CFG_DOUBLE = "double";

    private static final double DEFAULT_DOUBLE = 0;

    private double m_double = DEFAULT_DOUBLE;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addDouble(CFG_DOUBLE, m_double);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_double = settings.getDouble(CFG_DOUBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        m_double = settings.getDouble(CFG_DOUBLE, DEFAULT_DOUBLE);
    }

    /**
     * @return the string
     */
    @JsonProperty("double")
    public double getDouble() {
        return m_double;
    }

    /**
     * @param dbl the string to set
     */
    @JsonProperty("double")
    public void setDouble(final double dbl) {
        m_double = dbl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("double=");
        sb.append(m_double);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_double)
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
        DoubleInputQuickFormValue other = (DoubleInputQuickFormValue)obj;
        return new EqualsBuilder()
                .append(m_double, other.m_double)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        Double number = null;
        try {
            number = Double.parseDouble(fromCmdLine);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Could not parse '" + fromCmdLine + "' as double type.");
        }
        setDouble(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonNumber) {
            m_double = ((JsonNumber)json).doubleValue();
        } else if (json instanceof JsonString) {
            loadFromString(((JsonString)json).getString());
        } else if (json instanceof JsonObject) {
            try {
                m_double = ((JsonObject) json).getJsonNumber(CFG_DOUBLE).doubleValue();
            } catch (Exception e) {
                throw new JsonException("Expected double value for key '" + CFG_DOUBLE + "'."  , e);
            }
        } else {
            throw new JsonException("Expected JSON object or JSON number, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        JsonObject value = Json.createObjectBuilder()
                .add(CFG_DOUBLE, m_double)
                .build();
        return value;
    }

}
