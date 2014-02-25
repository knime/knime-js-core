package org.knime.js.base.node.quickform.selection.value;

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
public class ValueSelectionQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_COLUMN = "column";
    
    private static final String DEFAULT_COLUMN = "";
    
    private String m_column = DEFAULT_COLUMN;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    private String[] m_possibleValues;
    
    /**
     * @return the column
     */
    @JsonProperty("column")
    public String getColumn() {
        return m_column;
    }
    
    /**
     * @param column the column to set
     */
    @JsonProperty("column")
    public void setColumn(final String column) {
        m_column = column;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_column = settings.getString(CFG_COLUMN);
        m_defaultValue = settings.getString(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_COLUMN, m_column);
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
    
    /**
     * @return the possibleValues
     */
    @JsonProperty("possibleValues")
    public String[] getPossibleValues() {
        return m_possibleValues;
    }
    
    /**
     * @param possibleValues the possibleValues to set
     */
    @JsonProperty("possibleValues")
    public void setPossibleValues(final String[] possibleValues) {
        m_possibleValues = possibleValues;
    }

}
