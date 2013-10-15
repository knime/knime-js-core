package org.knime.js.base.node.quickform.input.string;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialogPane;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
public class StringInputQuickFormNodeDialog extends QuickFormNodeDialogPane {

    private final JTextField m_regexField;

    private final JTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    StringInputQuickFormNodeDialog() {
        m_regexField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Regular Expression: ", m_regexField, panelWithGBLayout, gbc);
        addPairToPanel("String Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        StringInputQuickFormRepresentation representation = new StringInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        setLabel(representation.getLabel());
        setDescription(representation.getDescription());
        setFlowVariableName(representation.getFlowVariableName());
        m_regexField.setText(representation.getRegex());
        // TODO add value
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        StringInputQuickFormRepresentation representation = new StringInputQuickFormRepresentation();
        // TODO add value
        representation.setLabel(getLabel());
        representation.setDescription(getDescription());
        representation.setFlowVariableName(getFlowVariableName());
        representation.setRegex(m_regexField.getText());
        representation.saveToNodeSettings(settings);
    }

}
