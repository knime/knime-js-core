package org.knime.js.base.node.quickform.input.integer;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
public class IntInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JSpinner m_valueSpinner;

    /** Constructors, inits fields calls layout routines. */
    IntInputQuickFormNodeDialog() {
        m_valueSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Integer Value: ", m_valueSpinner, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        IntInputQuickFormRepresentation representation = new IntInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        IntInputQuickFormValue value = new IntInputQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueSpinner.setValue(value.getInteger());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        IntInputQuickFormRepresentation representation = new IntInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.saveToNodeSettings(settings);
        IntInputQuickFormValue value = new IntInputQuickFormValue();
        value.setInteger((Integer)m_valueSpinner.getValue());
        value.saveToNodeSettings(settings);
    }

}
