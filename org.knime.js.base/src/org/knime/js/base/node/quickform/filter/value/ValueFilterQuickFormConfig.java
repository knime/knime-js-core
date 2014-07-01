/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Jun 13, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.filter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Config for the value filter quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class ValueFilterQuickFormConfig extends QuickFormFlowVariableConfig<ValueFilterQuickFormValue> {

    private static final String CFG_LOCK_COLUMN = "lockColumn";

    private static final boolean DEFAULT_LOCK_COLUMN = false;

    private boolean m_lockColumn = DEFAULT_LOCK_COLUMN;

    private static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";

    private Map<String, List<String>> m_possibleValues = new TreeMap<String, List<String>>();

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = MultipleSelectionsComponentFactory.TWINLIST;

    private String m_type = DEFAULT_TYPE;

    /**
     * @return the lockColumn
     */
    boolean getLockColumn() {
        return m_lockColumn;
    }

    /**
     * @param lockColumn the lockColumn to set
     */
    void setLockColumn(final boolean lockColumn) {
        m_lockColumn = lockColumn;
    }

    /**
     * @return the possibleValues
     */
    Map<String, List<String>> getPossibleValues() {
        return m_possibleValues;
    }

    /**
     * @param possibleValues the possibleValues to set
     */
    void setPossibleValues(final Map<String, List<String>> possibleValues) {
        m_possibleValues = possibleValues;
    }

    /**
     * @return the type
     */
    String getType() {
        return m_type;
    }

    /**
     * @param type the type to set
     */
    void setType(final String type) {
        m_type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addBoolean(CFG_LOCK_COLUMN, m_lockColumn);
        settings.addStringArray(CFG_POSSIBLE_COLUMNS,
                m_possibleValues.keySet().toArray(new String[m_possibleValues.keySet().size()]));
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
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN);
        m_possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(CFG_POSSIBLE_COLUMNS);
        for (String column : columns) {
            m_possibleValues.put(column, Arrays.asList(settings.getStringArray(column)));
        }
        m_type = settings.getString(CFG_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN, DEFAULT_LOCK_COLUMN);
        m_possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(CFG_POSSIBLE_COLUMNS, new String[0]);
        for (String column : columns) {
            m_possibleValues.put(column, Arrays.asList(settings.getStringArray(column, new String[0])));
        }
        m_type = settings.getString(CFG_TYPE, DEFAULT_TYPE);
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
     * {@inheritDoc}
     */
    @Override
    protected ValueFilterQuickFormValue createEmptyValue() {
        return new ValueFilterQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("lockColumn=");
        sb.append(m_lockColumn);
        sb.append(", ");
        sb.append("possibleValues=");
        sb.append("{");
        sb.append(m_possibleValues);
        sb.append("}");
        sb.append(", ");
        sb.append("type=");
        sb.append(m_type);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_lockColumn)
                .append(m_possibleValues)
                .append(m_type)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ValueFilterQuickFormConfig other = (ValueFilterQuickFormConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_lockColumn, other.m_lockColumn)
                .append(m_possibleValues, other.m_possibleValues)
                .append(m_type, other.m_type)
                .isEquals();
    }

}
