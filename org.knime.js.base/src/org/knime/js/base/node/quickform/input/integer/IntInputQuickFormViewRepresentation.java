package org.knime.js.base.node.quickform.input.integer;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class IntInputQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_DEFAULT = "default";

    private static final int DEFAULT_INTEGER = 0;

    private int m_defaultValue = DEFAULT_INTEGER;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_defaultValue = settings.getInt(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addInt(CFG_DEFAULT, m_defaultValue);
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

}
