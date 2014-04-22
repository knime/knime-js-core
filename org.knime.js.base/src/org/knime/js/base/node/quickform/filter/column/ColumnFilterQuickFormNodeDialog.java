package org.knime.js.base.node.quickform.filter.column;

import java.awt.GridBagConstraints;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnFilterPanel;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
public class ColumnFilterQuickFormNodeDialog extends QuickFormNodeDialog {
    
    private final ColumnFilterPanel m_defaultField;

    private final ColumnFilterPanel m_valueField;
    
    private final JComboBox<String> m_type;
    
    private String[] m_possibleColumns;

    /** Constructors, inits fields calls layout routines. */
    ColumnFilterQuickFormNodeDialog() {
        m_type = new JComboBox<String>(MultipleSelectionsComponentFactory.listMultipleSelectionsComponents());
        m_defaultField = new ColumnFilterPanel(true);
        m_valueField = new ColumnFilterPanel(true);
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Selection type: ", m_type, panelWithGBLayout, gbc);
        addPairToPanel("Default Columns: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Columns: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        DataTableSpec spec = (DataTableSpec) specs[0];
        m_possibleColumns = spec.getColumnNames();
        ColumnFilterQuickFormRepresentation representation = new ColumnFilterQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_defaultField.update(spec, false, Arrays.asList(representation.getDefaultColumns()));
        ColumnFilterQuickFormValue value = new ColumnFilterQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.update(spec, false, Arrays.asList(value.getColumns()));
        m_type.setSelectedItem(representation.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ColumnFilterQuickFormRepresentation representation = new ColumnFilterQuickFormRepresentation();
        saveSettingsTo(representation);
        Set<String> defaultIncludes = m_defaultField.getIncludedColumnSet();
        representation.setDefaultColumns(defaultIncludes.toArray(new String[defaultIncludes.size()]));
        representation.setType((String)m_type.getSelectedItem());
        representation.setPossibleColumns(m_possibleColumns);
        representation.saveToNodeSettings(settings);
        ColumnFilterQuickFormValue value = new ColumnFilterQuickFormValue();
        Set<String> valueIncludes = m_valueField.getIncludedColumnSet();
        value.setColumns(valueIncludes.toArray(new String[valueIncludes.size()]));
        value.saveToNodeSettings(settings);
    }

}
