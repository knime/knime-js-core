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
    
    private static final String CFG_USE_MIN = "use_min";
    
    private static final boolean DEFAULT_USE_MIN = false;
    
    private boolean m_useMin = DEFAULT_USE_MIN;
    
    private static final String CFG_USE_MAX = "use_max";
    
    private static final boolean DEFAULT_USE_MAX = false;
    
    private boolean m_useMax = DEFAULT_USE_MAX;
    
    private static final String CFG_MIN = "min";
    
    private static final int DEFAULT_MIN = 0;
    
    private int m_min = DEFAULT_MIN;
    
    private static final String CFG_MAX = "max";
    
    private static final int DEFAULT_MAX = 100;
    
    private int m_max = DEFAULT_MAX;
    
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
        m_useMin = settings.getBoolean(CFG_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX);
        m_min = settings.getInt(CFG_MIN);
        m_max = settings.getInt(CFG_MAX);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultValue = settings.getInt(CFG_DEFAULT, DEFAULT_INTEGER);
        m_useMin = settings.getBoolean(CFG_USE_MIN, DEFAULT_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX, DEFAULT_USE_MAX);
        m_min = settings.getInt(CFG_MIN, DEFAULT_MIN);
        m_max = settings.getInt(CFG_MAX, DEFAULT_MAX);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addInt(CFG_DEFAULT, m_defaultValue);
        settings.addBoolean(CFG_USE_MIN, m_useMin);
        settings.addBoolean(CFG_USE_MAX, m_useMax);
        settings.addInt(CFG_MIN, m_min);
        settings.addInt(CFG_MAX, m_max);
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

    /**
     * @return the useMin
     */
    public boolean getUseMin() {
        return m_useMin;
    }

    /**
     * @param useMin the useMin to set
     */
    public void setUseMin(final boolean useMin) {
        m_useMin = useMin;
    }

    /**
     * @return the useMax
     */
    public boolean getUseMax() {
        return m_useMax;
    }

    /**
     * @param useMax the useMax to set
     */
    public void setUseMax(final boolean useMax) {
        m_useMax = useMax;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return m_min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(final int min) {
        m_min = min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return m_max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(final int max) {
        m_max = max;
    }

}
