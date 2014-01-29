package org.knime.js.base.node.quickform.input.listbox;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class ListBoxInputQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_SEPARATOR = "separator";
    
    private static final String DEFAULT_SEPARATOR = ",";
    
    private String m_separator = DEFAULT_SEPARATOR;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    /**
     * @return the separator
     */
    public String getSeparator() {
        return m_separator;
    }
    
    /**
     * @param separator the separator to set
     */
    public void setSeparator(final String separator) {
        m_separator = separator;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_separator = settings.getString(CFG_SEPARATOR);
        setDefaultValue(settings.getString(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_SEPARATOR, m_separator);
        settings.addString(CFG_DEFAULT, getDefaultValue());
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
    public void validateSettings(NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO Auto-generated method stub
        
    }

}
