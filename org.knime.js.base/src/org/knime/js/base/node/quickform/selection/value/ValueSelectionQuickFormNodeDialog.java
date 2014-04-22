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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class ValueSelectionQuickFormNodeDialog extends QuickFormNodeDialog {
    
    private final JComboBox<ColumnType> m_columnType;

    private final JCheckBox m_lockColumn;

    private final ColumnSelectionPanel m_defaultColumnField;
    
    private final JComboBox m_defaultField;

    private final DefaultComboBoxModel m_defaultModel = new DefaultComboBoxModel();

    private final ColumnSelectionPanel m_columnField;

    private final JComboBox m_valueField;
    
    private final DefaultComboBoxModel m_valueModel = new DefaultComboBoxModel();
    
    private DataTableSpec m_tableSpec;
    
    private final JComboBox<String> m_type;

    /** Constructors, inits fields calls layout routines. */
    ValueSelectionQuickFormNodeDialog() {
        m_type = new JComboBox<String>(SingleSelectionComponentFactory.listSingleSelectionComponents());
        m_lockColumn = new JCheckBox();
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
                        updateValues(column, m_valueModel);
                    }
                }
                if (m_lockColumn.isSelected()) {
                    m_defaultColumnField.setSelectedColumn(m_columnField.getSelectedColumn());
                }
            }
        });
        m_defaultColumnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_defaultColumnField.addItemListener(new ItemListener() {
            /** {@inheritDoc} */
            @Override
            public void itemStateChanged(final ItemEvent ie) {
                Object o = ie.getItem();
                if (o != null) {
                    final String column = m_defaultColumnField.getSelectedColumn();
                    if (column != null) {
                        updateValues(column, m_defaultModel);
                    }
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
    
    private void updateAvailableColumns() {
        List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();
        switch ((ColumnType)m_columnType.getSelectedItem()) {
        case String:
            for (DataColumnSpec colSpec : m_tableSpec) {
                if (colSpec.getDomain().hasValues() && colSpec.getType().isCompatible(StringValue.class)) {
                    specs.add(colSpec);
                }
            }
            break;
        case Integer:
            for (DataColumnSpec colSpec : m_tableSpec) {
                if (colSpec.getDomain().hasValues() && colSpec.getType().isCompatible(IntValue.class)) {
                    specs.add(colSpec);
                }
            }
            break;
        case Double:
            for (DataColumnSpec colSpec : m_tableSpec) {
                if (colSpec.getDomain().hasValues() && colSpec.getType().isCompatible(DoubleValue.class)) {
                    specs.add(colSpec);
                }
            }
            break;
        default:
            for (DataColumnSpec colSpec : m_tableSpec) {
                if (colSpec.getDomain().hasValues()) {
                    specs.add(colSpec);
                }
            }
        }
        final DataTableSpec newDTS = new DataTableSpec(specs.toArray(new DataColumnSpec[0]));
        try {
            m_columnField.update(newDTS, null);
            // If no exception has been thrown there is min 1 column available
            m_columnField.setSelectedIndex(0);
        } catch (NotConfigurableException e) {
            // newDTS is empty
            m_valueModel.removeAllElements();
        }
        try {
            m_defaultColumnField.update(newDTS, null);
            // If no exception has been thrown there is min 1 column available
            m_defaultColumnField.setSelectedIndex(0);
        } catch (NotConfigurableException e) {
            // newDTS is empty
            m_defaultModel.removeAllElements();
        }
    }

    private void updateValues(final String column, final DefaultComboBoxModel<String> model) {
        final DataTableSpec spec = m_columnField.getDataTableSpec();
        DataColumnSpec dcs = spec.getColumnSpec(column);
        model.removeAllElements();
        if (dcs != null) {
            final Set<DataCell> vals = dcs.getDomain().getValues();
            for (final DataCell cell : vals) {
                String value = cell.toString();
                model.addElement(value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Selection type: ", m_type, panelWithGBLayout, gbc);
        addPairToPanel("Column type: ", m_columnType, panelWithGBLayout, gbc);
        addPairToPanel("Lock column: ", m_lockColumn, panelWithGBLayout, gbc);
        addPairToPanel("Default column selection: ", m_defaultColumnField, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Column selection: ", m_columnField, panelWithGBLayout, gbc);
        addPairToPanel("Variable Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_tableSpec = (DataTableSpec) specs[0];
        boolean hasValues = false;
        for (DataColumnSpec cspec : m_tableSpec) {
            if (cspec.getDomain().hasValues()) {
                hasValues = true;
                break;
            }
        }
        if (!hasValues) {
            throw new NotConfigurableException("Data does not contain any column with domain values.");
        }
        ValueSelectionQuickFormRepresentation representation = new ValueSelectionQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_columnType.setSelectedItem(representation.getColumnType());
        ValueSelectionQuickFormValue value = new ValueSelectionQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        String selectedColumn = value.getColumn();
        if (!selectedColumn.isEmpty()) {
            m_columnField.setSelectedColumn(selectedColumn);
        }
        String selectedDefaultColumn = representation.getDefaultColumn();
        if (!selectedDefaultColumn.isEmpty()) {
            m_defaultColumnField.setSelectedColumn(selectedDefaultColumn);
        }
        if (representation.getDefaultValue() != null) {
            m_defaultField.setSelectedItem(representation.getDefaultValue());
        }
        if (value.getValue() != null) {
            m_valueField.setSelectedItem(value.getValue());
        }
        m_lockColumn.setSelected(representation.getLockColumn());
        m_type.setSelectedItem(representation.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        if (m_defaultColumnField.getSelectedColumn() == null) {
            throw new InvalidSettingsException("No default column selected");
        }
        if (m_columnField.getSelectedColumn() == null) {
            throw new InvalidSettingsException("No column selected");
        }
        ValueSelectionQuickFormRepresentation representation = new ValueSelectionQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setColumnType((ColumnType)m_columnType.getSelectedItem());
        representation.setDefaultValue((String) m_defaultField.getSelectedItem());
        representation.setLockColumn(m_lockColumn.isSelected());
        representation.setFromSpec(m_tableSpec);
        representation.setDefaultColumn(m_defaultColumnField.getSelectedColumn());
        representation.setType((String)m_type.getSelectedItem());
        representation.saveToNodeSettings(settings);
        ValueSelectionQuickFormValue value = new ValueSelectionQuickFormValue();
        value.setValue((String) m_valueField.getSelectedItem());
        value.setColumn(m_columnField.getSelectedColumn());
        value.saveToNodeSettings(settings);
    }

}
