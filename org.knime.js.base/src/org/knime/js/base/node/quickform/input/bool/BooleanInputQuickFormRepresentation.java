package org.knime.js.base.node.quickform.input.bool;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class BooleanInputQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<BooleanInputQuickFormValue> {
    
    private static final String CFG_DEFAULT = "default";
    
    private boolean m_defaultValue;

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        setDefaultValue(settings.getBoolean(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        setDefaultValue(settings.getBoolean(CFG_DEFAULT, false));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addBoolean(CFG_DEFAULT, getDefaultValue());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<BooleanInputQuickFormValue> createDialogPanel() {
        BooleanInputQuickFormDialogPanel panel = new BooleanInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultValue
     */
    public boolean getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(final boolean defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final BooleanInputQuickFormValue value) {
        value.setBoolean(getDefaultValue());        
    }

}
