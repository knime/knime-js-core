package org.knime.js.base.node.quickform.selection.value;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class ValueSelectionQuickFormNodeDialog extends QuickFormNodeDialog {
    
    private final JComboBox<ColumnType> m_columnType;

    private final ColumnSelectionPanel m_columnField;
    
    private final JComboBox m_defaultField;

    private final JComboBox m_valueField;
    
    private final DefaultComboBoxModel m_defaultModel = new DefaultComboBoxModel();
    
    private final DefaultComboBoxModel m_valueModel = new DefaultComboBoxModel();
    
    private final List<DataColumnSpec> m_specs = new ArrayList<DataColumnSpec>();

    /** Constructors, inits fields calls layout routines. */
    ValueSelectionQuickFormNodeDialog() {
        m_columnType = new JComboBox<ColumnType>(ColumnType.values());
        m_columnType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateAvailableColumns();
            }
        });
        m_columnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_defaultField = new JComboBox(m_defaultModel);
        m_valueField = new JComboBox(m_valueModel);
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
    
    private void updateAvailableColumns() {
        List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();
        switch ((ColumnType)m_columnType.getSelectedItem()) {
        case String:
            for (DataColumnSpec colSpec : m_specs) {
                if (colSpec.getType().isCompatible(StringValue.class)) {
                    specs.add(colSpec);
                }
            }
            break;
        case Integer:
            for (DataColumnSpec colSpec : m_specs) {
                if (colSpec.getType().isCompatible(IntValue.class)) {
                    specs.add(colSpec);
                }
            }
            break;
        case Double:
            for (DataColumnSpec colSpec : m_specs) {
                if (colSpec.getType().isCompatible(DoubleValue.class)) {
                    specs.add(colSpec);
                }
            }
            break;
        default:
            specs = m_specs;
        }
        final DataTableSpec newDTS = new DataTableSpec(specs.toArray(new DataColumnSpec[0]));
        try {
            m_columnField.update(newDTS, null);
            // If no exception has been thrown there is min 1 column available
            m_columnField.setSelectedIndex(0);
        } catch (NotConfigurableException e) {
            // newDTS is empty
            m_defaultModel.removeAllElements();
            m_valueModel.removeAllElements();
        }
    }

    private void updateValues(final String column) {
        final DataTableSpec spec = m_columnField.getDataTableSpec();
        DataColumnSpec dcs = spec.getColumnSpec(column);
        m_defaultModel.removeAllElements();
        m_valueModel.removeAllElements();
        if (dcs != null) {
            final Set<DataCell> vals = dcs.getDomain().getValues();
            for (final DataCell cell : vals) {
                String value = cell.toString();
                m_defaultModel.addElement(value);
                m_valueModel.addElement(value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Column type: ", m_columnType, panelWithGBLayout, gbc);
        addPairToPanel("Column selection: ", m_columnField, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Variable Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        final DataTableSpec spec = (DataTableSpec) specs[0];
        for (DataColumnSpec cspec : spec) {
            if (cspec.getDomain().hasValues()) {
                m_specs.add(cspec);
            }
        }
        if (m_specs.size() == 0) {
            throw new NotConfigurableException("Data does not contain any column with domain values.");
        }
        ValueSelectionQuickFormRepresentation representation = new ValueSelectionQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        String selectedColumn = representation.getColumn();
        m_columnType.setSelectedItem(representation.getColumnType());
        if (!selectedColumn.isEmpty()) {
            m_columnField.setSelectedColumn(selectedColumn);
        }
        if (representation.getDefaultValue() != null) {
            m_defaultField.setSelectedItem(representation.getDefaultValue());
        }
        ValueSelectionQuickFormValue value = new ValueSelectionQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        if (value.getValue() != null) {
            m_valueField.setSelectedItem(value.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        if (m_columnField.getSelectedColumn() == null) {
            throw new InvalidSettingsException("No column selected");
        }
        ValueSelectionQuickFormRepresentation representation = new ValueSelectionQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setColumnType((ColumnType)m_columnType.getSelectedItem());
        representation.setColumn(m_columnField.getSelectedColumn());
        representation.setDefaultValue((String) m_defaultField.getSelectedItem());
        representation.saveToNodeSettings(settings);
        ValueSelectionQuickFormValue value = new ValueSelectionQuickFormValue();
        value.setValue((String) m_valueField.getSelectedItem());
        value.saveToNodeSettings(settings);
    }

}
