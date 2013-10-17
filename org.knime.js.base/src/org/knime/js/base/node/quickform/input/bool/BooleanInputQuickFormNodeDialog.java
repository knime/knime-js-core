package org.knime.js.base.node.quickform.input.bool;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

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
public class BooleanInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JCheckBox m_defaultField;
    
    private final JCheckBox m_valueField;

    /** Constructors, inits fields calls layout routines. */
    BooleanInputQuickFormNodeDialog() {
        m_defaultField = new JCheckBox();
        m_valueField = new JCheckBox();
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Boolean Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        BooleanInputQuickFormRepresentation representation = new BooleanInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_defaultField.setSelected(representation.getDefaultValue());
        BooleanInputQuickFormValue value = new BooleanInputQuickFormValue(getFlowVariableName());
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.setSelected(value.getBoolean());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        BooleanInputQuickFormRepresentation representation = new BooleanInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultValue(m_defaultField.isSelected());
        representation.saveToNodeSettings(settings);
        BooleanInputQuickFormValue value = new BooleanInputQuickFormValue(getFlowVariableName());
        value.setBoolean(m_valueField.isSelected());
        value.saveToNodeSettings(settings);
    }

}
