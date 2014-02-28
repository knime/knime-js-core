package org.knime.js.base.node.quickform.input.string;

import java.awt.GridBagConstraints;
import java.awt.Insets;

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

    private final RegexPanel m_regexField;
    
    private final JTextField m_defaultField;

    private final JTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    StringInputQuickFormNodeDialog() {
        m_regexField = new RegexPanel();
        m_defaultField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Regular Expression: ", m_regexField.getRegexPanel(), panelWithGBLayout, gbc);
        addPairToPanel("Validation error message: ", m_regexField.getErrorMessagePanel(), panelWithGBLayout, gbc);
        GridBagConstraints gbcClone = (GridBagConstraints) gbc.clone();
        gbcClone.insets = new Insets(0, 0, 0, 0);
        addPairToPanel("Common Regular Expressions: ",
                m_regexField.getCommonRegexesPanel(), panelWithGBLayout,
                gbcClone);
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
        m_regexField.setRegex(representation.getRegex());
        m_regexField.setErrorMessage(representation.getErrorMessage());
        m_defaultField.setText(representation.getDefaultValue());
        StringInputQuickFormValue value = new StringInputQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.setText(value.getString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_regexField.commitRegexHistory();
        StringInputQuickFormRepresentation representation = new StringInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setRegex(m_regexField.getRegex());
        representation.setErrorMessage(m_regexField.getErrorMessage());
        representation.setDefaultValue(m_defaultField.getText());
        representation.saveToNodeSettings(settings);
        StringInputQuickFormValue value = new StringInputQuickFormValue();
        value.setString(m_valueField.getText());
        value.saveToNodeSettings(settings);
    }

}
