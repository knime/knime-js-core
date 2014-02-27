package org.knime.js.base.node.quickform.selection.column;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
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
@SuppressWarnings("unchecked")
public class ColumnSelectionQuickFormNodeDialog extends QuickFormNodeDialog {

    private final ColumnSelectionPanel m_defaultField;

    private final ColumnSelectionPanel m_columnField;

    /** Constructors, inits fields calls layout routines. */
    ColumnSelectionQuickFormNodeDialog() {
        m_defaultField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_columnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Default column: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Column selection: ", m_columnField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_defaultField.update((DataTableSpec) specs[0], null);
        m_columnField.update((DataTableSpec) specs[0], null);
        ColumnSelectionQuickFormRepresentation representation = new ColumnSelectionQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_defaultField.setSelectedColumn(representation.getDefaultValue());
        ColumnSelectionQuickFormValue value = new ColumnSelectionQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_columnField.setSelectedColumn(value.getColumn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ColumnSelectionQuickFormRepresentation representation = new ColumnSelectionQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultValue(m_defaultField.getSelectedColumn());
        representation.saveToNodeSettings(settings);
        ColumnSelectionQuickFormValue value = new ColumnSelectionQuickFormValue();
        value.setColumn(m_columnField.getSelectedColumn());
        value.saveToNodeSettings(settings);
    }

}