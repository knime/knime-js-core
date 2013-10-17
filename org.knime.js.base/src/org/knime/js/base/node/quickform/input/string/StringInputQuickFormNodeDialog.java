package org.knime.js.base.node.quickform.input.string;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;

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
public class StringInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JTextField m_regexField;
    
    private final JTextField m_defaultField;

    private final JTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    StringInputQuickFormNodeDialog() {
        m_regexField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_defaultField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Regular Expression: ", m_regexField, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
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
        loadSettingsFrom(representation);
        m_regexField.setText(representation.getRegex());
        m_defaultField.setText(representation.getDefaultValue());
        StringInputQuickFormValue value = new StringInputQuickFormValue(getFlowVariableName());
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.setText(value.getString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        StringInputQuickFormRepresentation representation = new StringInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setRegex(m_regexField.getText());
        representation.setDefaultValue(m_defaultField.getText());
        representation.saveToNodeSettings(settings);
        StringInputQuickFormValue value = new StringInputQuickFormValue(getFlowVariableName());
        value.setString(m_valueField.getText());
        value.saveToNodeSettings(settings);
    }

}
