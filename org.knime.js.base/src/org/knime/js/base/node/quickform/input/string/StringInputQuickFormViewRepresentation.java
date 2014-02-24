package org.knime.js.base.node.quickform.input.string;

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
public class StringInputQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_REGEX = "regex";
    
    private static final String DEFAULT_REGEX = "";
    
    private String m_regex = DEFAULT_REGEX;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    /**
     * @return the regex
     */
    @JsonProperty("regex")
    public String getRegex() {
        return m_regex;
    }
    
    /**
     * @param regex the regex to set
     */
    @JsonProperty("regex")
    public void setRegex(final String regex) {
        m_regex = regex;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_regex = settings.getString(CFG_REGEX);
        m_defaultValue = settings.getString(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_REGEX, m_regex);
        settings.addString(CFG_DEFAULT, m_defaultValue);
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
