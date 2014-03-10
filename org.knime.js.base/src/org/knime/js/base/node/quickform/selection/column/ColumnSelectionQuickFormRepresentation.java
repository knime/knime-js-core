package org.knime.js.base.node.quickform.selection.column;

import org.knime.core.data.DataTableSpec;
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
public class ColumnSelectionQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ColumnSelectionQuickFormValue> {
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    private String[] m_possibleColumns;
    
    private DataTableSpec m_spec;
    
    /**
     * @return the spec
     */
    @JsonIgnore
    public DataTableSpec getSpec() {
        return m_spec;
    }
    
    /**
     * @param spec the spec to set
     */
    @JsonIgnore
    public void setSpec(final DataTableSpec spec) {
        m_spec = spec;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_defaultValue = settings.getString(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultValue = settings.getString(CFG_DEFAULT, "");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_DEFAULT, m_defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<ColumnSelectionQuickFormValue> createDialogPanel() {
        ColumnSelectionQuickFormDialogPanel panel = new ColumnSelectionQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
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
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final ColumnSelectionQuickFormValue value) {
        value.setColumn(m_defaultValue);        
    }
    
    /**
     * @return the possibleColumns
     */
    @JsonProperty("possibleColumns")
    public String[] getPossibleColumns() {
        return m_possibleColumns;
    }
    
    /**
     * @param possibleColumns the possibleColumns to set
     */
    @JsonProperty("possibleColumns")
    public void setPossibleColumns(final String[] possibleColumns) {
        m_possibleColumns = possibleColumns;
    }

}
