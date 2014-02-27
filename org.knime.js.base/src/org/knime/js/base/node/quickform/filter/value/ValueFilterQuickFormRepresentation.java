package org.knime.js.base.node.quickform.filter.value;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class ValueFilterQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ValueFilterQuickFormValue> {
    
    private static final String CFG_COLUMN = "column";
    
    private static final String DEFAULT_COLUMN = "";
    
    private String m_column = DEFAULT_COLUMN;
    
    private static final String CFG_DEFAULT = "default";
    
    private String[] m_defaultValues;
    
    private String[] m_possibleValues;
    
    /**
     * @return the column
     */
    public String getColumn() {
        return m_column;
    }
    
    /**
     * @param column the column to set
     */
    public void setColumn(final String column) {
        m_column = column;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_column = settings.getString(CFG_COLUMN);
        m_defaultValues = settings.getStringArray(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_column = settings.getString(CFG_COLUMN, DEFAULT_COLUMN);
        m_defaultValues = settings.getStringArray(CFG_DEFAULT, new String[0]);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_COLUMN, m_column);
        settings.addStringArray(CFG_DEFAULT, m_defaultValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<ValueFilterQuickFormValue> createDialogPanel() {
        ValueFilterQuickFormDialogPanel panel = new ValueFilterQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultValues
     */
    public String[] getDefaultValues() {
        return m_defaultValues;
    }

    /**
     * @param defaultValues the defaultValues to set
     */
    public void setDefaultValues(final String[] defaultValues) {
        m_defaultValues = defaultValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final ValueFilterQuickFormValue value) {
        value.setValues(m_defaultValues);        
    }
    
    /**
     * @return the possibleValues
     */
    public String[] getPossibleValues() {
        return m_possibleValues;
    }
    
    /**
     * @param possibleValues the possibleValues to set
     */
    public void setPossibleValues(final String[] possibleValues) {
        m_possibleValues = possibleValues;
    }

}
