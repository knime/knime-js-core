package org.knime.js.base.node.quickform.selection.single;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
@SuppressWarnings({"rawtypes", "unchecked" })
public class SingleSelectionQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JList m_defaultField;

    private final JList m_valueField;

    private final JTextArea m_possibleChoicesField;

    private final JComboBox m_type;

    /**
     * Constructors, inits fields calls layout routines.
     */
    SingleSelectionQuickFormNodeDialog() {
        m_defaultField = new JList();
        m_defaultField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_valueField = new JList();
        m_valueField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_possibleChoicesField = new JTextArea();
        m_possibleChoicesField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(final DocumentEvent e) {
                refreshChoices();
            }
            @Override
            public void insertUpdate(final DocumentEvent e) {
                refreshChoices();
            }
            @Override
            public void changedUpdate(final DocumentEvent e) {
                refreshChoices();
            }
        });
        m_type = new JComboBox(SingleSelectionComponentFactory.listSingleSelectionComponents());
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        GridBagConstraints gbc2 = (GridBagConstraints)gbc.clone();
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weighty = 1;
        Dimension prefSize = new Dimension(DEF_TEXTFIELD_WIDTH, 70);
        addPairToPanel("Selection Type: ", m_type, panelWithGBLayout, gbc);
        JScrollPane choicesPane = new JScrollPane(m_possibleChoicesField);
        choicesPane.setPreferredSize(prefSize);
        addPairToPanel("Possible Choices: ", choicesPane, panelWithGBLayout, gbc2);
        JScrollPane defaultPane = new JScrollPane(m_defaultField);
        defaultPane.setPreferredSize(prefSize);
        addPairToPanel("Default Variable Value: ", defaultPane, panelWithGBLayout, gbc2);
        JScrollPane valuePane = new JScrollPane(m_valueField);
        valuePane.setPreferredSize(prefSize);
        addPairToPanel("Variable Value: ", valuePane, panelWithGBLayout, gbc2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        SingleSelectionQuickFormRepresentation representation = new SingleSelectionQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_possibleChoicesField.setText(StringUtils.join(representation.getPossibleChoices(), "\n"));
        m_type.setSelectedItem(representation.getType());
        SingleSelectionQuickFormValue value = new SingleSelectionQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_defaultField.setSelectedValue(representation.getDefaultValue(), true);
        m_valueField.setSelectedValue(value.getVariableValue(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        SingleSelectionQuickFormRepresentation representation = new SingleSelectionQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultValue((String)m_defaultField.getSelectedValue());
        String possibleChoices = m_possibleChoicesField.getText();
        representation.setPossibleChoices(possibleChoices.isEmpty() ? new String[0] : possibleChoices.split("\n"));
        representation.setType((String)m_type.getItemAt(m_type.getSelectedIndex()));
        representation.saveToNodeSettings(settings);
        SingleSelectionQuickFormValue value = new SingleSelectionQuickFormValue();
        value.setVariableValue((String)m_valueField.getSelectedValue());
        value.saveToNodeSettings(settings);
    }

    
    /**
     * Refreshes the default and value fields based on changes in the current
     * choices, while keeping the selection.
     */
    private void refreshChoices() {
        refreshChoices(m_defaultField);
        refreshChoices(m_valueField);
    }

    /**
     * Refreshes the given list based on changes in the current
     * choices, while keeping the selection.
     * 
     * @param list The list that will be refreshed
     */
    private void refreshChoices(final JList list) {
        String selection = (String)list.getSelectedValue();
        list.setListData(m_possibleChoicesField.getText().split("\n"));
        list.setSelectedValue(selection, false);
        if (list.getSelectedValue() == null && list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
    }

}
