package org.knime.js.base.node.quickform.selection.column;

import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
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
@SuppressWarnings("unchecked")
public class ColumnSelectionQuickFormNodeDialog extends QuickFormNodeDialog {

    private final ColumnSelectionPanel m_defaultField;

    private final ColumnSelectionPanel m_columnField;
    
    private final JComboBox<String> m_type;
    
    private String[] m_possibleColumns;

    /** Constructors, inits fields calls layout routines. */
    ColumnSelectionQuickFormNodeDialog() {
        m_type = new JComboBox<String>(SingleSelectionComponentFactory.listSingleSelectionComponents());
        m_defaultField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_columnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Selection type: ", m_type, panelWithGBLayout, gbc);
        addPairToPanel("Default column: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Column selection: ", m_columnField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        DataTableSpec spec = (DataTableSpec) specs[0];
        m_defaultField.update(spec, null);
        m_columnField.update(spec, null);
        m_possibleColumns = spec.getColumnNames();
        ColumnSelectionQuickFormRepresentation representation = new ColumnSelectionQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        String selectedDefault = representation.getDefaultColumn();
        if (selectedDefault.isEmpty()) {
            List<DataColumnSpec> cspecs = m_defaultField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedDefault = cspecs.get(0).getName();
            }
        }
        m_defaultField.setSelectedColumn(selectedDefault);
        ColumnSelectionQuickFormValue value = new ColumnSelectionQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        String selectedColumn = value.getColumn();
        if (selectedColumn.isEmpty()) {
            List<DataColumnSpec> cspecs = m_columnField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedColumn = cspecs.get(0).getName();
            }
        }
        m_columnField.setSelectedColumn(selectedColumn);
        m_type.setSelectedItem(representation.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ColumnSelectionQuickFormRepresentation representation = new ColumnSelectionQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultColumn(m_defaultField.getSelectedColumn());
        representation.setType((String)m_type.getSelectedItem());
        representation.setPossibleColumns(m_possibleColumns);
        representation.saveToNodeSettings(settings);
        ColumnSelectionQuickFormValue value = new ColumnSelectionQuickFormValue();
        value.setColumn(m_columnField.getSelectedColumn());
        value.saveToNodeSettings(settings);
    }

}
