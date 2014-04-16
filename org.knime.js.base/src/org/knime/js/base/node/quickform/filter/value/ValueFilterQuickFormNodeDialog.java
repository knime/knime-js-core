package org.knime.js.base.node.quickform.filter.value;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.filter.StringFilterPanel;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
@SuppressWarnings("unchecked")
public class ValueFilterQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JCheckBox m_lockColumn;

    private final ColumnSelectionPanel m_defaultColumnField;
    
    private final StringFilterPanel m_defaultField;

    private final ColumnSelectionPanel m_columnField;

    private final StringFilterPanel m_valueField;
    
    private String[] m_possibleValues;
    
    private DataTableSpec m_spec;

    /** Constructors, inits fields calls layout routines. */
    ValueFilterQuickFormNodeDialog() {
        m_lockColumn = new JCheckBox();
        m_defaultColumnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_columnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_defaultField = new StringFilterPanel(true);
        m_valueField = new StringFilterPanel(true);
        m_defaultColumnField.addItemListener(new ItemListener() {
            /** {@inheritDoc} */
            @Override
            public void itemStateChanged(final ItemEvent ie) {
                Object o = ie.getItem();
                if (o != null) {
                    final String column = m_defaultColumnField.getSelectedColumn();
                    if (column != null) {
                        updateValues(column, m_defaultField);
                    }
                }
            }
        });
        m_columnField.addItemListener(new ItemListener() {
            /** {@inheritDoc} */
            @Override
            public void itemStateChanged(final ItemEvent ie) {
                Object o = ie.getItem();
                if (o != null) {
                    final String column = m_columnField.getSelectedColumn();
                    if (column != null) {
                        updateValues(column, m_valueField);
                    }
                }
                if (m_lockColumn.isSelected()) {
                    m_defaultColumnField.setSelectedColumn(m_columnField.getSelectedColumn());
                }
            }
        });
        m_lockColumn.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_defaultColumnField.setEnabled(!m_lockColumn.isSelected());
                if (m_lockColumn.isSelected()) {
                    m_defaultColumnField.setSelectedColumn(m_columnField.getSelectedColumn());
                }
            }
        });
        createAndAddTab();
    }

    private void updateValues(final String column, final StringFilterPanel panel) {
        DataColumnSpec dcs = m_spec.getColumnSpec(column);
        if (dcs == null) {
            m_possibleValues = new String[0];
        } else {
            final Set<DataCell> vals = dcs.getDomain().getValues();
            m_possibleValues = new String[vals.size()];
            int i = 0;
            for (final DataCell cell : vals) {
                m_possibleValues[i++] = cell.toString();
            }
        }
        List<String> excludes = Arrays.asList(m_possibleValues);
        panel.update(new ArrayList<String>(0), excludes,
                m_possibleValues);
        panel.update(new ArrayList<String>(0), excludes,
                m_possibleValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Lock column: ", m_lockColumn, panelWithGBLayout, gbc);
        addPairToPanel("Default column selection: ", m_defaultColumnField, panelWithGBLayout, gbc);
        addPairToPanel("Default Values: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Column selection: ", m_columnField, panelWithGBLayout, gbc);
        addPairToPanel("Variable Values: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        final DataTableSpec spec = (DataTableSpec) specs[0];
        final List<DataColumnSpec> filteredSpecs = new ArrayList<DataColumnSpec>();
        for (DataColumnSpec cspec : spec) {
            if (cspec.getDomain().hasValues()) {
                filteredSpecs.add(cspec);
            }
        }
        if (filteredSpecs.size() == 0) {
            throw new NotConfigurableException("Data does not contain any column with domain values.");
        }
        m_spec = new DataTableSpec(filteredSpecs.toArray(new DataColumnSpec[0]));
        m_defaultColumnField.update(m_spec, null);
        m_columnField.update(m_spec, null);
        ValueFilterQuickFormRepresentation representation = new ValueFilterQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        String selectedDefaultColumn = representation.getDefaultColumn();
        if (selectedDefaultColumn.isEmpty()) {
            List<DataColumnSpec> cspecs = m_columnField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedDefaultColumn = cspecs.get(0).getName();
            }
        }
        m_defaultColumnField.setSelectedColumn(selectedDefaultColumn);
        List<String> defaultIncludes = Arrays.asList(representation.getDefaultValues());
        List<String> defaultExcludes =
                new ArrayList<String>(Math.max(0, m_possibleValues.length
                        - defaultIncludes.size()));
        for (String string : m_possibleValues) {
            if (!defaultIncludes.contains(string)) {
                defaultExcludes.add(string);
            }
        }
        m_defaultField.update(defaultIncludes, defaultExcludes, m_possibleValues);
        ValueFilterQuickFormValue value = new ValueFilterQuickFormValue();
        String selectedColumn = value.getColumn();
        if (selectedColumn.isEmpty()) {
            List<DataColumnSpec> cspecs = m_columnField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedColumn = cspecs.get(0).getName();
            }
        }
        m_columnField.setSelectedColumn(selectedColumn);
        value.loadFromNodeSettingsInDialog(settings);
        List<String> valueIncludes = Arrays.asList(value.getValues());
        List<String> valueExcludes = new ArrayList<String>(Math.max(0, m_possibleValues.length - valueIncludes.size()));
        for (String string : m_possibleValues) {
            if (!valueIncludes.contains(string)) {
                valueExcludes.add(string);
            }
        }
        m_valueField.update(valueIncludes, valueExcludes, m_possibleValues);
        m_lockColumn.setSelected(representation.getLockColumn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ValueFilterQuickFormRepresentation representation = new ValueFilterQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setLockColumn(m_lockColumn.isSelected());
        representation.setDefaultColumn(m_defaultColumnField.getSelectedColumn());
        Set<String> defaultIncludes = m_defaultField.getIncludeList();
        representation.setDefaultValues(defaultIncludes.toArray(new String[defaultIncludes.size()]));
        representation.setFromSpec(m_spec);
        representation.saveToNodeSettings(settings);
        ValueFilterQuickFormValue value = new ValueFilterQuickFormValue();
        Set<String> valueIncludes = m_valueField.getIncludeList();
        value.setValues(valueIncludes.toArray(new String[valueIncludes.size()]));
        value.setColumn(m_columnField.getSelectedColumn());
        value.saveToNodeSettings(settings);
    }

}
