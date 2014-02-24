package org.knime.js.base.node.quickform.input.listbox;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ListBoxInputQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_SEPARATOR = "separator";
    
    private static final String DEFAULT_SEPARATOR = ",";
    
    private String m_separator = DEFAULT_SEPARATOR;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    /**
     * @return the separator
     */
    @JsonProperty("separator")
    public String getSeparator() {
        return m_separator;
    }
    
    /**
     * @param separator the separator to set
     */
    @JsonProperty("separator")
    public void setSeparator(final String separator) {
        m_separator = separator;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_separator = settings.getString(CFG_SEPARATOR);
        setDefaultValue(settings.getString(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_SEPARATOR, m_separator);
        settings.addString(CFG_DEFAULT, getDefaultValue());
    }

    /**
     * @return the defaultValue
     */
    @JsonProperty("default")
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    @JsonProperty("default")
    public void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

}
