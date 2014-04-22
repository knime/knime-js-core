package org.knime.js.base.node.quickform.selection.column;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
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
    
    private static final String CFG_DEFAULT_COLUMN = "defaultColumn";
    
    private static final String DEFAULT_DEFAULT_COLUMN = "";
    
    private String m_defaultColumn = DEFAULT_DEFAULT_COLUMN;
    
    private static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";
    
    private static final String[] DEFAULT_POSSIBLE_COLUMNS = new String[0];
    
    private String[] m_possibleColumns = DEFAULT_POSSIBLE_COLUMNS;

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = SingleSelectionComponentFactory.DROPDOWN;

    private String m_type = DEFAULT_TYPE;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_defaultColumn = settings.getString(CFG_DEFAULT_COLUMN);
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
        m_defaultColumn = settings.getString(CFG_DEFAULT_COLUMN, DEFAULT_DEFAULT_COLUMN);
        setType(settings.getString(CFG_TYPE, DEFAULT_TYPE));
        m_possibleColumns = settings.getStringArray(CFG_POSSIBLE_COLUMNS, DEFAULT_POSSIBLE_COLUMNS);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_DEFAULT_COLUMN, m_defaultColumn);
        settings.addString(CFG_TYPE, m_type);
        settings.addStringArray(CFG_POSSIBLE_COLUMNS, m_possibleColumns);
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
     * @return the defaultColumn
     */
    @JsonProperty("defaultColumn")
    public String getDefaultColumn() {
        return m_defaultColumn;
    }

    /**
     * @param defaultColumn the defaultColumn to set
     */
    @JsonProperty("defaultColumn")
    public void setDefaultColumn(final String defaultColumn) {
        m_defaultColumn = defaultColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final ColumnSelectionQuickFormValue value) {
        value.setColumn(m_defaultColumn);        
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
