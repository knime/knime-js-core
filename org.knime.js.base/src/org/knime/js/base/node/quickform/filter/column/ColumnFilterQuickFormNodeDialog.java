package org.knime.js.base.node.quickform.filter.column;

import java.awt.GridBagConstraints;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnFilterPanel;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
public class ColumnFilterQuickFormNodeDialog extends QuickFormNodeDialog {
    
    private final ColumnFilterPanel m_defaultField;

    private final ColumnFilterPanel m_valueField;

    /** Constructors, inits fields calls layout routines. */
    ColumnFilterQuickFormNodeDialog() {
        m_defaultField = new ColumnFilterPanel(true);
        m_valueField = new ColumnFilterPanel(true);
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Default columns: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Column: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        ColumnFilterQuickFormRepresentation representation = new ColumnFilterQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_defaultField.update((DataTableSpec) specs[0], false, Arrays.asList(representation.getDefaultColumns()));
        ColumnFilterQuickFormValue value = new ColumnFilterQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.update((DataTableSpec) specs[0], false, Arrays.asList(value.getColumns()));
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
        representation.saveToNodeSettings(settings);
        ColumnFilterQuickFormValue value = new ColumnFilterQuickFormValue();
        Set<String> valueIncludes = m_valueField.getIncludedColumnSet();
        value.setColumns(valueIncludes.toArray(new String[valueIncludes.size()]));
        value.saveToNodeSettings(settings);
    }

}
