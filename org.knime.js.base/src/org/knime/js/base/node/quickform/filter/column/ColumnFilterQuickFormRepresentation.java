package org.knime.js.base.node.quickform.filter.column;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
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
    
    private static final String CFG_DEFAULT_COLUMNS = "defaultColumns";
    
    private static final String[] DEFAULT_DEFAULT_COLUMNS = new String[0];
    
    private String[] m_defaultColumns = DEFAULT_DEFAULT_COLUMNS;
    
    private static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";
    
    private static final String[] DEFAULT_POSSIBLE_COLUMNS = new String[0];
    
    private String[] m_possibleColumns = DEFAULT_POSSIBLE_COLUMNS;

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = MultipleSelectionsComponentFactory.TWINLIST;

    private String m_type = DEFAULT_TYPE;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT_COLUMNS);
        m_possibleColumns = settings.getStringArray(CFG_POSSIBLE_COLUMNS);
        setType(settings.getString(CFG_TYPE));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultColumns = settings.getStringArray(CFG_DEFAULT_COLUMNS, DEFAULT_DEFAULT_COLUMNS);
        m_possibleColumns = settings.getStringArray(CFG_POSSIBLE_COLUMNS, DEFAULT_POSSIBLE_COLUMNS);
        setType(settings.getString(CFG_TYPE, DEFAULT_TYPE));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_DEFAULT_COLUMNS, m_defaultColumns);
        settings.addStringArray(CFG_POSSIBLE_COLUMNS, m_possibleColumns);
        settings.addString(CFG_TYPE, m_type);
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
    @JsonProperty("defaultColumns")
    public String[] getDefaultColumns() {
        return m_defaultColumns;
    }

    /**
     * @param defaultColumns the defaultColumns to set
     */
    @JsonProperty("defaultColumns")
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

    /**
     * @return the type
     */
    @JsonProperty("type")
    public String getType() {
        return m_type;
    }

    /**
     * @param type the type to set
     */
    @JsonProperty("type")
    public void setType(final String type) {
        m_type = type;
    }

}
