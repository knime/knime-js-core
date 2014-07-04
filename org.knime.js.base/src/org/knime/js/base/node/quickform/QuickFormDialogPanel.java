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
 *
 * History
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.DialogNodeValue;

/**
 * The panel of a node that is displayed in the sub node's dialog.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 * @param <VAL> The type of value that is handled by this dialog
 */
@SuppressWarnings("serial")
public abstract class QuickFormDialogPanel
        <VAL extends DialogNodeValue>
        extends DialogNodePanel<VAL> {

    private JLabel m_label = new JLabel();

    private JCheckBox m_checkBox = new JCheckBox("Use defaults");

    private final JPanel m_contentPanel;

    private VAL m_defaultValue;

    /**
     * Creates a {@link QuickFormDialogPanel}.
     *
     * @param defaultValue The default value
     */
    public QuickFormDialogPanel(final VAL defaultValue) {
        m_defaultValue = defaultValue;
        m_checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                setEnabled(!m_checkBox.isSelected());
                if (m_checkBox.isSelected()) {
                    resetToDefault();
                }
            }
        });
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(m_label, gbc);

        gbc.gridx += 1;
        /** Enable to get "use default" checker
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(ViewUtils.getInFlowLayout(FlowLayout.RIGHT, 0, 0, m_checkBox), gbc);
        gbc.gridy += 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
         */
        m_contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        gbc.weightx = 1;
        add(m_contentPanel, gbc);
    }

    /**
     * @param label The label of this quick form
     */
    public void setLabel(final String label) {
        m_label.setText(label);
    }

    /**
     * @param description The description of this quick form
     */
    public void setDescription(final String description) {
        m_label.setToolTipText(description);
    }

    /**
     * @param component The component that will be added to this quick form
     */
    protected void setComponent(final JComponent component) {
        m_contentPanel.removeAll();
        m_contentPanel.add(component);
    }

    /**
     * @return the defaultValue
     */
    protected VAL getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * Resets the currently set value back to the default.
     */
    protected abstract void resetToDefault();

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getNodeValue() throws InvalidSettingsException {
        if (m_checkBox.isSelected()) {
            return null;
        } else {
            return createNodeValue();
        }
    }

    /**
     * @return Value containing the current settings
     * @throws InvalidSettingsException If the current settings are invalid
     */
    protected abstract VAL createNodeValue() throws InvalidSettingsException;

}
