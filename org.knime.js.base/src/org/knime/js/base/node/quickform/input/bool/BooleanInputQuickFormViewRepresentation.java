package org.knime.js.base.node.quickform.input.bool;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class BooleanInputQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_DEFAULT = "default";
    
    private boolean m_defaultValue;

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        setDefaultValue(settings.getBoolean(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_DEFAULT, getDefaultValue());
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

}
