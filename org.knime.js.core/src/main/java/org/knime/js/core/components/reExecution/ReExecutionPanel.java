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
 *   Sep 15, 2021 (konrad-amtenbrink): created
 */
package org.knime.js.core.components.reExecution;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Konrad Amtenbrink, KNIME GmbH, Berlin, Germany
 *
 */
public class ReExecutionPanel extends JPanel {

    private final JCheckBox m_reExecuteDownstreamNodesCheckBox;

    /**
     * Constructor, initializes base panel
     */
    public ReExecutionPanel() {
        super(new GridBagLayout());

        m_reExecuteDownstreamNodesCheckBox = new JCheckBox("Re-execution of downstream nodes if widget value is changed");
        initBasePanel();
    }

    /**
     * Constructor, initializes base panel with custom title for re-execution check box
     * @param title the title of the re-execution check box
     */
    public ReExecutionPanel(final String title) {
        super(new GridBagLayout());

        m_reExecuteDownstreamNodesCheckBox = new JCheckBox(title);
        initBasePanel();
    }

    private void initBasePanel() {
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        this.add(m_reExecuteDownstreamNodesCheckBox, gbc);
    }

    /**
     * @return the value of m_reExecuteDownstreamNodesCheckBox
     */
    public Boolean getReExecuteDownstreamNodesValue() {
        return m_reExecuteDownstreamNodesCheckBox.isSelected();
    }

    /**
     * @param reExecuteDownstreamNodes the value for m_reExecuteDownstreamNodesCheckBox to set
     */
    public void setReExecuteDownstreamNodesValue(final Boolean reExecuteDownstreamNodes) {
        m_reExecuteDownstreamNodesCheckBox.setSelected(reExecuteDownstreamNodes);
    }

    private static GridBagConstraints createConfiguredGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }

}
