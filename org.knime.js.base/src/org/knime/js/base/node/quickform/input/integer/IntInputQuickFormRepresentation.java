package org.knime.js.base.node.quickform.input.integer;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class IntInputQuickFormRepresentation extends QuickFormFlowVariableRepresentation<IntInputQuickFormValue> {
    
    private static final String CFG_DEFAULT = "default";

    private static final int DEFAULT_INTEGER = 0;

    private int m_defaultValue = DEFAULT_INTEGER;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_defaultValue = settings.getInt(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultValue = settings.getInt(CFG_DEFAULT, DEFAULT_INTEGER);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addInt(CFG_DEFAULT, m_defaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<IntInputQuickFormValue> createDialogPanel() {
        IntInputQuickFormDialogPanel panel = new IntInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultValue
     */
    public int getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(final int defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final IntInputQuickFormValue value) {
        value.setInteger(m_defaultValue);        
    }

}
