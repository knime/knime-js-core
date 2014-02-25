package org.knime.js.base.node.quickform.selection.value;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class ValueSelectionQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ValueSelectionQuickFormValue> {
    
    private static final String CFG_COLUMN = "column";
    
    private static final String DEFAULT_COLUMN = "";
    
    private String m_column = DEFAULT_COLUMN;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
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
        m_defaultValue = settings.getString(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_column = settings.getString(CFG_COLUMN, DEFAULT_COLUMN);
        m_defaultValue = settings.getString(CFG_DEFAULT, "");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_COLUMN, m_column);
        settings.addString(CFG_DEFAULT, m_defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<ValueSelectionQuickFormValue> createDialogPanel() {
        ValueSelectionQuickFormDialogPanel panel = new ValueSelectionQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final ValueSelectionQuickFormValue value) {
        value.setValue(m_defaultValue);        
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
