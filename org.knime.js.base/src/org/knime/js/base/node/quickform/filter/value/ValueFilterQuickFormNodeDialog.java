package org.knime.js.base.node.quickform.filter.value;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.Border;

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

    private final ColumnSelectionPanel m_columnField;
    
    private final StringFilterPanel m_defaultField;

    private final StringFilterPanel m_valueField;
    
    private String[] m_possibleValues;

    /** Constructors, inits fields calls layout routines. */
    ValueFilterQuickFormNodeDialog() {
        m_columnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_defaultField = new StringFilterPanel(true);
        m_valueField = new StringFilterPanel(true);
        m_columnField.addItemListener(new ItemListener() {
            /** {@inheritDoc} */
            @Override
            public void itemStateChanged(final ItemEvent ie) {
                Object o = ie.getItem();
                if (o != null) {
                    final String column = m_columnField.getSelectedColumn();
                    if (column != null) {
                        updateValues(column);
                    }
                }
            }
        });
        createAndAddTab();
    }

    private void updateValues(final String column) {
        final DataTableSpec spec = m_columnField.getDataTableSpec();
        DataColumnSpec dcs = spec.getColumnSpec(column);
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
        m_defaultField.update(new ArrayList<String>(0), excludes,
                m_possibleValues);
        m_valueField.update(new ArrayList<String>(0), excludes,
                m_possibleValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Column selection: ", m_columnField, panelWithGBLayout, gbc);
        addPairToPanel("Default Values: ", m_defaultField, panelWithGBLayout, gbc);
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
        final DataTableSpec newDTS = new DataTableSpec(filteredSpecs.toArray(new DataColumnSpec[0]));
        m_columnField.update(newDTS, null);
        ValueFilterQuickFormRepresentation representation = new ValueFilterQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        String selectedColumn = representation.getColumn();
        if (selectedColumn.isEmpty()) {
            List<DataColumnSpec> cspecs = m_columnField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedColumn = cspecs.get(0).getName();
            }
        }
        m_columnField.setSelectedColumn(selectedColumn);
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
        value.loadFromNodeSettingsInDialog(settings);
        List<String> valueIncludes = Arrays.asList(value.getValues());
        List<String> valueExcludes = new ArrayList<String>(Math.max(0, m_possibleValues.length - valueIncludes.size()));
        for (String string : m_possibleValues) {
            if (!valueIncludes.contains(string)) {
                valueExcludes.add(string);
            }
        }
        m_valueField.update(valueIncludes, valueExcludes, m_possibleValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ValueFilterQuickFormRepresentation representation = new ValueFilterQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setColumn(m_columnField.getSelectedColumn());
        Set<String> defaultIncludes = m_defaultField.getIncludeList();
        representation.setDefaultValues(defaultIncludes.toArray(new String[defaultIncludes.size()]));
        representation.saveToNodeSettings(settings);
        ValueFilterQuickFormValue value = new ValueFilterQuickFormValue();
        Set<String> valueIncludes = m_valueField.getIncludeList();
        value.setValues(valueIncludes.toArray(new String[valueIncludes.size()]));
        value.saveToNodeSettings(settings);
    }

}
