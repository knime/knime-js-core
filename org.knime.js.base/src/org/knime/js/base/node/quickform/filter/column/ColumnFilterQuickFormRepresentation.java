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
 */
package org.knime.js.base.node.quickform.filter.column;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ColumnFilterQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ColumnFilterQuickFormValue> {

    private static final String CFG_DEFAULT_COLUMNS = "defaultColumns";

    private static final String[] DEFAULT_DEFAULT_COLUMNS = new String[0];

    private String[] m_defaultColumns = DEFAULT_DEFAULT_COLUMNS;

    private static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";

    private static final String[] DEFAULT_POSSIBLE_COLUMNS = new String[0];

    private String[] m_possibleColumns = DEFAULT_POSSIBLE_COLUMNS;

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = MultipleSelectionsComponentFactory.TWINLIST;

    private String m_type = DEFAULT_TYPE;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT_COLUMNS);
        m_possibleColumns = settings.getStringArray(CFG_POSSIBLE_COLUMNS);
        setType(settings.getString(CFG_TYPE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT_COLUMNS, DEFAULT_DEFAULT_COLUMNS);
        m_possibleColumns = settings.getStringArray(CFG_POSSIBLE_COLUMNS, DEFAULT_POSSIBLE_COLUMNS);
        setType(settings.getString(CFG_TYPE, DEFAULT_TYPE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_DEFAULT_COLUMNS, m_defaultColumns);
        settings.addStringArray(CFG_POSSIBLE_COLUMNS, m_possibleColumns);
        settings.addString(CFG_TYPE, m_type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<ColumnFilterQuickFormValue> createDialogPanel() {
        ColumnFilterQuickFormDialogPanel panel = new ColumnFilterQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultColumns
     */
    @JsonProperty("defaultColumns")
    public String[] getDefaultColumns() {
        return m_defaultColumns;
    }

    /**
     * @param defaultColumns the defaultColumns to set
     */
    @JsonProperty("defaultColumns")
    public void setDefaultColumns(final String[] defaultColumns) {
        m_defaultColumns = defaultColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final ColumnFilterQuickFormValue value) {
        value.setColumns(m_defaultColumns);
    }

    /**
     * @return the possibleColumns
     */
    @JsonProperty("possibleColumns")
    public String[] getPossibleColumns() {
        return m_possibleColumns;
    }

    /**
     * @param possibleColumns the possibleColumns to set
     */
    @JsonProperty("possibleColumns")
    public void setPossibleColumns(final String[] possibleColumns) {
        m_possibleColumns = possibleColumns;
    }

    /**
     * @return the type
     */
    @JsonProperty("type")
    public String getType() {
        return m_type;
    }

    /**
     * @param type the type to set
     */
    @JsonProperty("type")
    public void setType(final String type) {
        m_type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("defaultColumns=");
        sb.append(m_defaultColumns);
        sb.append(", ");
        sb.append("possibleColumns=");
        sb.append(m_possibleColumns);
        sb.append(", ");
        sb.append("type=");
        sb.append(m_type);
        return sb.toString();
    }

}
