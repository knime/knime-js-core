
/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by 
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 */
package org.knime.js.base.node.quickform;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.port.PortObjectSpec;

/**
 * Dialog to node.
 *
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
public abstract class QuickFormNodeDialog extends NodeDialogPane {

    /** Default width (#columns) of text field elements. */
    public static final int DEF_TEXTFIELD_WIDTH = 20;

    private final JTextField m_labelField;

    private final JTextArea m_descriptionArea;

    private final JTextField m_variableNameField;

    private final JSpinner m_weightSpinner;

    private final JCheckBox m_hideInWizard;

    /**
     * Inits fields, sub-classes should call the {@link #createAndAddTab()}
     * method when they are done initializing their fields.
     */
    public QuickFormNodeDialog() {
        m_labelField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_descriptionArea = new JTextArea(1, DEF_TEXTFIELD_WIDTH);
        m_descriptionArea.setLineWrap(true);
        m_descriptionArea.setPreferredSize(new Dimension(100, 50));
        m_descriptionArea.setMinimumSize(new Dimension(100, 30));
        m_variableNameField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_weightSpinner = new JSpinner(new SpinnerNumberModel(1, -100, 100, 1));
        m_hideInWizard = new JCheckBox((Icon)null, false);
        m_hideInWizard.setToolTipText("If selected, this QuickForm elements is not visible in the wizard.");
    }

    /**
     * To be called from subclasses as last line in their constructor. It
     * initializes the panel, call the
     * {@link #fillPanel(JPanel, GridBagConstraints)} method and adds the tab to
     * the dialog.
     */
    protected final void createAndAddTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPairToPanel("Label: ", m_labelField, panel, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        JScrollPane sp = new JScrollPane(m_descriptionArea);
        sp.setPreferredSize(m_descriptionArea.getPreferredSize());
        sp.setMinimumSize(m_descriptionArea.getMinimumSize());
        addPairToPanel("Description: ", sp, panel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        addPairToPanel("Hide in Wizard: ", m_hideInWizard, panel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        addPairToPanel("Weight: ", m_weightSpinner, panel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPairToPanel("Variable Name: ", m_variableNameField, panel, gbc);

        fillPanel(panel, gbc);

        addTab("Control", panel);
    }

    /**
     * Called from {@link #createAndAddTab()}. Subclasses should add their own
     * controls to the argument panel.
     *
     * @param panelWithGBLayout To add to.
     * @param gbc The current constraints.
     */
    protected abstract void fillPanel(final JPanel panelWithGBLayout, GridBagConstraints gbc);

    /**
     * Adds a panel sub-component to the dialog.
     *
     * @param label The label (left hand column)
     * @param c The component (right hand column)
     * @param panelWithGBLayout Panel to add
     * @param gbc constraints.
     */
    protected final void addPairToPanel(final String label, final JComponent c, final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        int fill = gbc.fill;

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelWithGBLayout.add(new JLabel(label), gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = fill;
        gbc.weightx = 1;
        panelWithGBLayout.add(c, gbc);
        gbc.weightx = 0;
    }

    /**
     * @return The label
     */
    protected String getLabel() {
        return m_labelField.getText();
    }

    /**
     * @param label The label
     */
    protected void setLabel(final String label) {
        m_labelField.setText(label);
    }

    /**
     * @return The description
     */
    protected String getDescription() {
        return m_descriptionArea.getText();
    }

    /**
     * @param description The description
     */
    protected void setDescription(final String description) {
        m_descriptionArea.setText(description);
    }

    /**
     * @return The flow variable name
     */
    protected String getFlowVariableName() {
        return m_variableNameField.getText();
    }

    /**
     * @param flowVariableName The flow variable name
     */
    protected void setFlowVariableName(final String flowVariableName) {
        m_variableNameField.setText(flowVariableName);
    }

    /**
     * @return The weight
     */
    protected int getWeight() {
        return (Integer)m_weightSpinner.getValue();
    }

    /**
     * @param weight The weight
     */
    protected void setWeight(final int weight) {
        m_weightSpinner.setValue(weight);
    }

    /**
     * @param representation Representation holding the content for the
     *            components
     */
    protected void loadSettingsFrom(
            final QuickFormFlowVariableRepresentation<? extends DialogNodeValue> representation) {
        setLabel(representation.getLabel());
        setDescription(representation.getDescription());
        setWeight(representation.getWeight());
        setFlowVariableName(representation.getFlowVariableName());
    }

    /**
     * @param representation The representation to put the content of the
     *            components into
     */
    protected void saveSettingsTo(final QuickFormFlowVariableRepresentation<? extends DialogNodeValue> representation) {
        representation.setLabel(getLabel());
        representation.setDescription(getDescription());
        representation.setWeight(getWeight());
        representation.setFlowVariableName(getFlowVariableName());
    }

    /** {@inheritDoc} */
    @Override
    protected abstract void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException;

    /** {@inheritDoc} */
    @Override
    protected abstract void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException;

}
