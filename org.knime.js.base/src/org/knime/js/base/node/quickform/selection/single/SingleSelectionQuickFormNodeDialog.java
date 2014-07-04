/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
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
 * The dialog for the single selection quick form node.
 *
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
@SuppressWarnings({"rawtypes", "unchecked" })
public class SingleSelectionQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JList m_defaultField;

    private final JTextArea m_possibleChoicesField;

    private final JComboBox m_type;

    private SingleSelectionQuickFormConfig m_config;

    /**
     * Constructors, inits fields calls layout routines.
     */
    SingleSelectionQuickFormNodeDialog() {
        m_config = new SingleSelectionQuickFormConfig();
        m_defaultField = new JList();
        m_defaultField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        addPairToPanel("Default Value: ", defaultPane, panelWithGBLayout, gbc2);
    }


    /**
     * Refreshes the default and value fields based on changes in the current
     * choices, while keeping the selection.
     */
    private void refreshChoices() {
        refreshChoices(m_defaultField);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_possibleChoicesField.setText(StringUtils.join(m_config.getPossibleChoices(), "\n"));
        m_type.setSelectedItem(m_config.getType());
        m_defaultField.setSelectedValue(m_config.getDefaultValue().getVariableValue(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        m_config.getDefaultValue().setVariableValue((String)m_defaultField.getSelectedValue());
        String possibleChoices = m_possibleChoicesField.getText();
        m_config.setPossibleChoices(possibleChoices.isEmpty() ? new String[0] : possibleChoices.split("\n"));
        m_config.setType((String)m_type.getItemAt(m_type.getSelectedIndex()));
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        SingleSelectionQuickFormValue value = new SingleSelectionQuickFormValue();
        value.loadFromNodeSettings(settings);
        return value.getVariableValue();
    }

}
