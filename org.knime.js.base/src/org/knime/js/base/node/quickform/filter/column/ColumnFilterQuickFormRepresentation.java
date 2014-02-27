package org.knime.js.base.node.quickform.filter.column;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class ColumnFilterQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ColumnFilterQuickFormValue> {
    
    private static final String CFG_DEFAULT = "default";
    
    private String[] m_defaultColumns = new String[0];
    
    private String[] m_possibleColumns;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT, new String[0]);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_DEFAULT, m_defaultColumns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<ColumnFilterQuickFormValue> createDialogPanel() {
        ColumnFilterQuickFormDialogPanel panel = new ColumnFilterQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultColumns
     */
    public String[] getDefaultColumns() {
        return m_defaultColumns;
    }

    /**
     * @param defaultColumns the defaultColumns to set
     */
    public void setDefaultColumns(final String[] defaultColumns) {
        m_defaultColumns = defaultColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final ColumnFilterQuickFormValue value) {
        value.setColumns(m_defaultColumns);        
    }
    
    /**
     * @return the possibleColumns
     */
    public String[] getPossibleColumns() {
        return m_possibleColumns;
    }
    
    /**
     * @param possibleColumns the possibleColumns to set
     */
    public void setPossibleColumns(final String[] possibleColumns) {
        m_possibleColumns = possibleColumns;
    }

}
