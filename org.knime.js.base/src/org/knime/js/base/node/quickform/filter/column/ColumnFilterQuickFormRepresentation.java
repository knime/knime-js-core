package org.knime.js.base.node.quickform.filter.column;

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
public class ColumnFilterQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ColumnFilterQuickFormValue> {
    
    private static final String CFG_DEFAULT = "default";
    
    private String[] m_defaultColumns = new String[0];
    
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
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT, new String[0]);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_DEFAULT, m_defaultColumns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<ColumnFilterQuickFormValue> createDialogPanel() {
        ColumnFilterQuickFormDialogPanel panel = new ColumnFilterQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultColumns
     */
    @JsonProperty("defaultvalue")
    public String[] getDefaultColumns() {
        return m_defaultColumns;
    }

    /**
     * @param defaultColumns the defaultColumns to set
     */
    @JsonProperty("defaultvalue")
    public void setDefaultColumns(final String[] defaultColumns) {
        m_defaultColumns = defaultColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final ColumnFilterQuickFormValue value) {
        value.setColumns(m_defaultColumns);        
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
