package org.knime.js.base.node.quickform.input.listbox;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
public class ListBoxInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private static final int TEXT_AREA_HEIGHT = 5;

    private final JTextField m_separatorField;

    private final JTextArea m_defaultArea;
    
    private final JTextArea m_valueArea;
    
    /** Constructors, inits fields calls layout routines. */
    ListBoxInputQuickFormNodeDialog() {
        m_separatorField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_defaultArea = new JTextArea(TEXT_AREA_HEIGHT, DEF_TEXTFIELD_WIDTH);
        m_valueArea = new JTextArea(TEXT_AREA_HEIGHT, DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Separator: ", m_separatorField, panelWithGBLayout, gbc);
        addPairToPanel("Default List: ", new JScrollPane(m_defaultArea), panelWithGBLayout, gbc);
        addPairToPanel("String List: ", new JScrollPane(m_valueArea), panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        ListBoxInputQuickFormRepresentation representation = new ListBoxInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_separatorField.setText(representation.getSeparator());
        m_defaultArea.setText(representation.getDefaultValue());
        ListBoxInputQuickFormValue value = new ListBoxInputQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueArea.setText(value.getString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ListBoxInputQuickFormRepresentation representation = new ListBoxInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setSeparator(m_separatorField.getText());
        representation.setDefaultValue(m_defaultArea.getText());
        representation.saveToNodeSettings(settings);
        ListBoxInputQuickFormValue value = new ListBoxInputQuickFormValue();
        value.setString(m_valueArea.getText());
        value.saveToNodeSettings(settings);
    }

}
