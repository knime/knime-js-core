/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 * ---------------------------------------------------------------------
 *
 * History
 *   10 Oct 2016 (albrecht): created
 */
package org.knime.js.core.settings.numberFormat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.js.core.settings.DialogUtil;

/**
 * Utility class to create a Swing panel for number format settings.
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class NumberFormatNodeDialogUI {

    private static final int SINGLE_CHAR_WIDTH = 3;
    private static final int MULTIPLE_CHAR_WIDTH = 20;

    private boolean m_enabled = true;

    private final JSpinner m_decimalSpinner;
    private final JTextField m_markField;
    private final JTextField m_thousandField;
    private final JTextField m_prefixField;
    private final JTextField m_postfixField;
    private final JTextField m_negativeField;
    private final JTextField m_negativeBeforeField;
    //TODO add functions (encoder, decoder, edit, undo)
    private JPanel m_panel;

    /** Creates a new utility object, initializes fields */
    public NumberFormatNodeDialogUI() {
        m_decimalSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 7, 1));
        m_markField = new JTextField(SINGLE_CHAR_WIDTH);
        m_thousandField = new JTextField(SINGLE_CHAR_WIDTH);
        m_prefixField = new JTextField(MULTIPLE_CHAR_WIDTH);
        m_postfixField = new JTextField(MULTIPLE_CHAR_WIDTH);
        m_negativeField = new JTextField(MULTIPLE_CHAR_WIDTH);
        m_negativeBeforeField = new JTextField(MULTIPLE_CHAR_WIDTH);
        try {
            //load defaults
            loadSettingsFrom(null);
        } catch (NotConfigurableException e) { /* do nothing */ }
    }

    /**
     * Sets a new value for the number of decimal points
     * @param decimals the number of decimal points to set.
     * Integer needs to be between 0 and 7.
     * @throws InvalidSettingsException if decimals are out of bounds
     */
    public void setDecimals(final int decimals) throws InvalidSettingsException {
        if (decimals < 0) {
            throw new InvalidSettingsException("Number of decimals must be a positive integer.");
        } else if (decimals > 7) {
            throw new InvalidSettingsException("JavaScript floating points are only stable up to 7 decimals");
        }
        m_decimalSpinner.setValue(decimals);
    }

    /**
     * Returns a panel that can be included in Swing based dialogs.
     * @return A {@link JPanel}
     */
    public JPanel createPanel() {
        if (m_panel == null) {
            m_panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            DialogUtil.addPairToPanel("Decimal digits", m_decimalSpinner, m_panel, gbc);
            DialogUtil.addPairToPanel("Decimal separator", m_markField, m_panel, gbc);
            DialogUtil.addPairToPanel("Thousands separator", m_thousandField, m_panel, gbc);
            DialogUtil.addPairToPanel("Custom prefix", m_prefixField, m_panel, gbc);
            DialogUtil.addPairToPanel("Custom postfix", m_postfixField, m_panel, gbc);
            DialogUtil.addPairToPanel("Negative sign", m_negativeField, m_panel, gbc);
            DialogUtil.addPairToPanel("Negative before string", m_negativeBeforeField, m_panel, gbc);
        }
        return m_panel;
    }

    /**
     * Returns a modal dialog that can be used from Swing based dialogs.
     * @param owner the {@link Frame} from which the dialog is displayed
     * @param title the {@link String} to display in the dialog's title bar
     * @return A {@link JDialog}
     */
    public JDialog createDialog(final Frame owner, final String title) {
        JDialog dialog = new JDialog(owner, title, true);
        dialog.setResizable(false);
        Container container = dialog.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(createPanel(), BorderLayout.CENTER);

        final JButton okButton = new JButton("OK");
        final ActionListener okActionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    saveSettingsTo().validateSettings();
                    dialog.setVisible(false);
                    dialog.dispose();
                } catch (InvalidSettingsException ex) { /* TODO: show error? */ }
            }
        };
        okButton.addActionListener(okActionListener);
        container.add(okButton, BorderLayout.SOUTH);

        dialog.pack();
        return dialog;
    }

    /**
     * Enables or disables all components
     * @param enabled true, if components should be enabled, false otherwise
     */
    public void setEnabled(final boolean enabled) {
        m_enabled = enabled;
        m_decimalSpinner.setEnabled(enabled);
        m_markField.setEnabled(enabled);
        m_thousandField.setEnabled(enabled);
        m_prefixField.setEnabled(enabled);
        m_postfixField.setEnabled(enabled);
        m_negativeField.setEnabled(enabled);
        m_negativeBeforeField.setEnabled(enabled);
    }

    /**
     * @return true if the panel is enabled, false otherwise
     */
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Loads the settings contained in a {@link NumberFormatSettings} object (can be null) into the corresponding input fields
     * @param settings The settings to load from, loads default settings if null
     * @throws NotConfigurableException
     */
    public void loadSettingsFrom(final NumberFormatSettings settings) throws NotConfigurableException {
        NumberFormatSettings load = settings;
        if (settings == null) {
            load = new NumberFormatSettings();
        }
        if (load.getDecimals() != null) {
            m_decimalSpinner.setValue(load.getDecimals());
        }
        m_markField.setText(load.getMark());
        m_thousandField.setText(load.getThousand());
        m_prefixField.setText(load.getPrefix());
        m_postfixField.setText(load.getPostfix());
        m_negativeField.setText(load.getNegative());
        m_negativeBeforeField.setText(load.getNegativeBefore());
    }

    /**
     * Creates a new {@link NumberFormatSettings} object, validates and fills the current settings
     * @return A new {@link NumberFormatSettings} object reflecting the current settings, never null
     * @throws InvalidSettingsException on validation error
     */
    public NumberFormatSettings saveSettingsTo() throws InvalidSettingsException {
        NumberFormatSettings settings = new NumberFormatSettings();
        settings.setDecimals((Integer)m_decimalSpinner.getValue());
        settings.setMark(m_markField.getText());
        settings.setThousand(m_thousandField.getText());
        settings.setPrefix(m_prefixField.getText());
        settings.setPostfix(m_postfixField.getText());
        settings.setNegative(m_negativeField.getText());
        settings.setNegativeBefore(m_negativeBeforeField.getText());
        settings.validateSettings();
        return settings;
    }

}
