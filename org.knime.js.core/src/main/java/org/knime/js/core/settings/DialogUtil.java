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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   14 Oct 2016 (albrecht): created
 */
package org.knime.js.core.settings;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A utility class offering helper methods for dialog creation and layout
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class DialogUtil {

    /** Default width (#columns) of text field elements. */
    public static final int DEF_TEXTFIELD_WIDTH = 20;

    /**
     * Adds a panel sub-component to the dialog.
     *
     * @param label The label (left hand column)
     * @param c The component (right hand column)
     * @param panelWithGBLayout Panel to add
     * @param gbc constraints.
     */
    public static final void addPairToPanel(final String label, final JComponent c, final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        int fill = gbc.fill;
        Insets insets = gbc.insets;

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelWithGBLayout.add(new JLabel(label), gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.weightx = 1;
        panelWithGBLayout.add(c, gbc);
        gbc.weightx = 0;
    }

    /**
     * Adds a panel sub-component to the dialog containing two components and a label.
     *
     * @param label The label (left hand column)
     * @param middle The component (middle column)
     * @param right The component (right hand column)
     * @param panelWithGBLayout Panel to add
     * @param gbc constraints.
     */
    public static final void addTripelToPanel(final String label,
            final JComponent middle, final JComponent right,
            final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        int fill = gbc.fill;
        Insets insets = gbc.insets;

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelWithGBLayout.add(new JLabel(label), gbc);

        gbc.gridwidth = 1;
        gbc.fill = fill;
        gbc.weightx = 1;
        panelWithGBLayout.add(middle, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;
        gbc.weightx = 0;
        panelWithGBLayout.add(right, gbc);
        gbc.weightx = 0;
        gbc.fill = fill;
    }

}
