package org.knime.js.base.node.quickform.selection.value;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

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
public class ValueSelectionQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ValueSelectionQuickFormValue> {
    
    private static final String CFG_COLUMN = "column";
    
    private static final String DEFAULT_COLUMN = "";
    
    private String m_column = DEFAULT_COLUMN;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    private String[] m_possibleValues;
    
    private static final String CFG_COLUMN_TYPE = "columnType";
    
    private static final ColumnType DEFAULT_COLUMN_TYPE = ColumnType.All;
    
    private ColumnType m_columnType = DEFAULT_COLUMN_TYPE;
    
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
        super.loadFromNodeSettings(settings);
        m_column = settings.getString(CFG_COLUMN);
        m_defaultValue = settings.getString(CFG_DEFAULT);
        m_columnType = ColumnType.valueOf(settings.getString(CFG_COLUMN_TYPE));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_column = settings.getString(CFG_COLUMN, DEFAULT_COLUMN);
        m_defaultValue = settings.getString(CFG_DEFAULT, "");
        m_columnType = ColumnType.valueOf(settings.getString(CFG_COLUMN_TYPE, DEFAULT_COLUMN_TYPE.name()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_COLUMN, m_column);
        settings.addString(CFG_DEFAULT, m_defaultValue);
        settings.addString(CFG_COLUMN_TYPE, m_columnType.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<ValueSelectionQuickFormValue> createDialogPanel() {
        ValueSelectionQuickFormDialogPanel panel = new ValueSelectionQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultValue
     */
    @JsonProperty("defaultvalue")
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    @JsonProperty("defaultvalue")
    public void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final ValueSelectionQuickFormValue value) {
        value.setValue(m_defaultValue);        
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
    
    /**
     * @return the columnType
     */
    public ColumnType getColumnType() {
        return m_columnType;
    }
    
    /**
     * @param columnType the columnType to set
     */
    public void setColumnType(final ColumnType columnType) {
        m_columnType = columnType;
    }

}
