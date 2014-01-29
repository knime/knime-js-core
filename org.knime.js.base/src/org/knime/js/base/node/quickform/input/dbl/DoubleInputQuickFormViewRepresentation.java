package org.knime.js.base.node.quickform.input.dbl;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DoubleInputQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_DEFAULT = "default";

    private static final double DEFAULT_DOUBLE = 0.0;

    private double m_defaultValue = DEFAULT_DOUBLE;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        setDefaultValue(settings.getDouble(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addDouble(CFG_DEFAULT, getDefaultValue());
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

}
