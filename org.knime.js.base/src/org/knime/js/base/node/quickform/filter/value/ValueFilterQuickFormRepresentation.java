package org.knime.js.base.node.quickform.filter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
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
public class ValueFilterQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ValueFilterQuickFormValue> {

    private static final String CFG_LOCK_COLUMN = "lockColumn";

    private static final boolean DEFAULT_LOCK_COLUMN = false;

    private boolean m_lockColumn = DEFAULT_LOCK_COLUMN;

    private static final String CFG_DEFAULT_COLUMN = "defaultColumn";

    private static final String DEFAULT_DEFAULT_COLUMN = "";

    private String m_defaultColumn = DEFAULT_DEFAULT_COLUMN;

    private static final String CFG_DEFAULT_VALUES = "default";
    
    private static final String[] DEFAULT_DEFAULT_VALUES = new String[0];
    
    private String[] m_defaultValues = DEFAULT_DEFAULT_VALUES;
    
    private static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";
    
    private Map<String, List<String>> m_possibleValues = new TreeMap<String, List<String>>();

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
        m_defaultValues = settings.getStringArray(CFG_DEFAULT_VALUES, DEFAULT_DEFAULT_VALUES);
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN);
        m_defaultColumn = settings.getString(CFG_DEFAULT_COLUMN);
        m_possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(CFG_POSSIBLE_COLUMNS);
        for (String column : columns) {
            m_possibleValues.put(column, Arrays.asList(settings.getStringArray(column)));
        }
        setType(settings.getString(CFG_TYPE));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_defaultValues = settings.getStringArray(CFG_DEFAULT_VALUES, DEFAULT_DEFAULT_VALUES);
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN, DEFAULT_LOCK_COLUMN);
        m_defaultColumn = settings.getString(CFG_DEFAULT_COLUMN, DEFAULT_DEFAULT_COLUMN);
        m_possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(CFG_POSSIBLE_COLUMNS, new String[0]);
        for (String column : columns) {
            m_possibleValues.put(column, Arrays.asList(settings.getStringArray(column, new String[0])));
        }
        setType(settings.getString(CFG_TYPE, DEFAULT_TYPE));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_DEFAULT_VALUES, m_defaultValues);
        settings.addBoolean(CFG_LOCK_COLUMN, m_lockColumn);
        settings.addStringArray(CFG_POSSIBLE_COLUMNS,
                m_possibleValues.keySet().toArray(new String[m_possibleValues.keySet().size()]));
        settings.addString(CFG_DEFAULT_COLUMN, m_defaultColumn);
        for (String key : m_possibleValues.keySet()) {
            List<String> values = m_possibleValues.get(key);
            settings.addStringArray(key, values.toArray(new String[values.size()]));
        }
        settings.addString(CFG_TYPE, m_type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<ValueFilterQuickFormValue> createDialogPanel() {
        ValueFilterQuickFormDialogPanel panel = new ValueFilterQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final ValueFilterQuickFormValue value) {
        value.setValues(m_defaultValues);        
    }

    /**
     * @return the lockColumn
     */
    @JsonProperty("lockColumn")
    public boolean getLockColumn() {
        return m_lockColumn;
    }

    /**
     * @param lockColumn the lockColumn to set
     */
    @JsonProperty("lockColumn")
    public void setLockColumn(final boolean lockColumn) {
        m_lockColumn = lockColumn;
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
     * @return the defaultValues
     */
    @JsonProperty("defaultValues")
    public String[] getDefaultValues() {
        return m_defaultValues;
    }

    /**
     * @param defaultValues the defaultValues to set
     */
    @JsonProperty("defaultValues")
    public void setDefaultValues(final String[] defaultValues) {
        m_defaultValues = defaultValues;
    }

    /**
     * @return the possibleColumns
     */
    @JsonProperty("possibleColumns")
    public String[] getPossibleColumns() {
        return m_possibleValues.keySet().toArray(new String[m_possibleValues.keySet().size()]);
    }

    /**
     * @return the possibleValues
     */
    @JsonProperty("possibleValues")
    public Map<String, List<String>> getPossibleValues() {
        return m_possibleValues;
    }

    /**
     * @param spec the spec to set
     */
    @JsonIgnore
    public void setFromSpec(final DataTableSpec spec) {
        // Only add column specs for columns that have values and are of the selected type
        List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();
        for (DataColumnSpec cspec : spec) {
            if (cspec.getDomain().hasValues()) {
                specs.add(cspec);
            }
        }
        DataTableSpec filteredSpec = new DataTableSpec(specs.toArray(new DataColumnSpec[specs.size()]));
        Map<String, List<String>> values = new TreeMap<String, List<String>>();
        for (DataColumnSpec colSpec : filteredSpec) {
            final Set<DataCell> vals = colSpec.getDomain().getValues();
            if (vals != null) {
                List<String> v = new ArrayList<String>();
                for (final DataCell cell : vals) {
                    v.add(cell.toString());
                }
                values.put(colSpec.getName(), v);
            }
        }
        m_possibleValues = values;
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
