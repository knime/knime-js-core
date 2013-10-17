package org.knime.js.base.node.quickform.input.dbl;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DoubleInputQuickFormRepresentation extends QuickFormFlowVariableRepresentation<DoubleInputQuickFormValue> {
    
    private static final String CFG_DEFAULT = "default";

    private static final double DEFAULT_DOUBLE = 0.0;

    private double m_defaultValue = DEFAULT_DOUBLE;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        setDefaultValue(settings.getDouble(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        setDefaultValue(settings.getDouble(CFG_DEFAULT, DEFAULT_DOUBLE));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addDouble(CFG_DEFAULT, getDefaultValue());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<DoubleInputQuickFormValue> createDialogPanel() {
        DoubleInputQuickFormDialogPanel panel = new DoubleInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }
    
    /**
     * @return the defaultValue
     */
    public double getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(final double defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final DoubleInputQuickFormValue value) {
        value.setDouble(getDefaultValue());
        
    }

}
