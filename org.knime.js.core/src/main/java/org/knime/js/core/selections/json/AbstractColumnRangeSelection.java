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
 * ---------------------------------------------------------------------
 *
 * History
 *   18 Aug 2016 (albrecht): created
 */
package org.knime.js.core.selections.json;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"/*,
    defaultImpl = NumericColumnRangeSelection.class*/
    // can't use defaultImpl, see https://github.com/FasterXML/jackson-databind/issues/1488
    )
@JsonSubTypes({
    @Type(value = NumericColumnRangeSelection.class, name = "numeric"),
    @Type(value = NominalColumnRangeSelection.class, name = "nominal")
    })
public abstract class AbstractColumnRangeSelection {

    private String m_columnName;

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return m_columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(final String columnName) {
        m_columnName = columnName;
    }

    /**
     * Saves the current state to the given settings object.
     * @param settings the settings to save to
     */
    @JsonIgnore
    public abstract void saveToNodeSettings(NodeSettingsWO settings);

    /**
     * Loads the configuration from the given settings object.
     * @param settings the settings to load from
     * @throws InvalidSettingsException on load error
     */
    @JsonIgnore
    public abstract void loadFromNodeSettings(NodeSettingsRO settings) throws InvalidSettingsException;

    /**
     * Loads the configuration from the given settings object for a dialog.
     * @param settings the settings to load from
     */
    @JsonIgnore
    public abstract void loadFromNodeSettingsInDialog(NodeSettingsRO settings);

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
        AbstractColumnRangeSelection other = (AbstractColumnRangeSelection)obj;
        return new EqualsBuilder()
                .append(m_columnName, other.m_columnName)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_columnName)
                .toHashCode();
    }

}
