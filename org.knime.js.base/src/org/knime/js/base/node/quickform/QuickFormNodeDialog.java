
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
 */
package org.knime.js.base.node.quickform;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.dialog.ValueControlledDialogPane;
import org.knime.core.node.port.PortObjectSpec;

/**
 * Dialog to node.
 *
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
public abstract class QuickFormNodeDialog
        extends NodeDialogPane implements ValueControlledDialogPane {

    /** Default width (#columns) of text field elements. */
    public static final int DEF_TEXTFIELD_WIDTH = 20;

    private final JTextField m_labelField;

    private final JTextArea m_descriptionArea;

    private final JTextField m_variableNameField;

    private final JTextField m_parameterNameField;

    private final JCheckBox m_hideInWizard;

    private final JCheckBox m_hideInDialog;

    private final JCheckBox m_required;

    private final JLabel m_statusBarLabel;

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
        m_parameterNameField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_hideInWizard = new JCheckBox((Icon)null, false);
        m_hideInWizard.setToolTipText("If selected, this QuickForm elements is not visible in the wizard.");
        m_hideInDialog = new JCheckBox((Icon)null, false);
        m_hideInDialog.setToolTipText("If selected, this QuickForm elements is not visible in the dialog.");
        m_required = new JCheckBox((Icon)null, false);
        m_required
            .setToolTipText("If selected, filling this QuickForm element is required, otherwise it can be left empty.");
        m_statusBarLabel = new JLabel("", NodeView.WARNING_ICON, SwingConstants.LEFT);
        Font font = m_statusBarLabel.getFont().deriveFont(Font.BOLD);
        m_statusBarLabel.setFont(font);
        m_statusBarLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        m_statusBarLabel.setBackground(Color.WHITE);
        m_statusBarLabel.setOpaque(true);
        m_statusBarLabel.setVisible(false);
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

        addPairToPanel("Hide in Dialog: ", m_hideInDialog, panel, gbc);

        // TODO enable once functionality is in
        // addPairToPanel("Required: ", m_required, panel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPairToPanel("Variable Name: ", m_variableNameField, panel, gbc);

        addPairToPanel("Parameter Name: ", m_parameterNameField, panel, gbc);

        fillPanel(panel, gbc);

        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(panel, BorderLayout.CENTER);
        borderPanel.add(m_statusBarLabel, BorderLayout.SOUTH);

        addTab("Control", borderPanel);
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
     * @return The parameter name
     */
    protected String getParameterName() {
        return m_parameterNameField.getText();
    }

    /**
     * @return true if this node should be hidden in the wizard, false otherwise
     */
    protected boolean getHideInWizard() {
        return m_hideInWizard.isSelected();
    }

    /**
     * @param hideInWizard If true this node will be hidden in the wizard.
     */
    protected void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard.setSelected(hideInWizard);
    }

    /**
     * @return true if this node should be hidden in the sub node dialog, false otherwise
     */
    protected boolean getHideInDialog() {
        return m_hideInDialog.isSelected();
    }

    /**
     * @param hideInDialog If true this node will be hidden in the sub node dialog.
     */
    protected void setHideInDialog(final boolean hideInDialog) {
        m_hideInDialog.setSelected(hideInDialog);
    }

    /**
     * @return true if this node is required to be configured from the sub node and wizard, false otherwise
     */
    protected boolean getRequired() {
        return m_required.isSelected();
    }

    /**
     * @param required If true this node will be required to be configured from the sub node and wizard.
     */
    protected void setRequired(final boolean required) {
        m_required.setSelected(required);
    }

    /**
     * @param flowVariableName The flow variable name
     */
    protected void setFlowVariableName(final String flowVariableName) {
        m_variableNameField.setText(flowVariableName);
    }

    /**
     * @param parameterName The parameter name to set
     */
    protected void setParameterName(final String parameterName) {
        m_parameterNameField.setText(parameterName);
    }

    private boolean validateParameterName() {
        String name = getParameterName();
        if (name == null) {
            return false;
        }
        if (name.isEmpty()) {
            return true; // for backward compatibility reasons
        }
        Matcher matcher = DialogNode.PARAMETER_NAME_PATTERN.matcher(name);
        return matcher.matches();
    }

    /**
     * @param config The {@link QuickFormFlowVariableConfig} to load from
     */
    protected void loadSettingsFrom(
            final QuickFormFlowVariableConfig<? extends DialogNodeValue> config) {
        setLabel(config.getLabel());
        setDescription(config.getDescription());
        setFlowVariableName(config.getFlowVariableName());
        setParameterName(config.getParameterName());
        setHideInWizard(config.getHideInWizard());
        setHideInDialog(config.getHideInDialog());
        setRequired(config.getRequired());
    }

    /**
     * @param config The {@link QuickFormFlowVariableConfig} to save to
     * @throws InvalidSettingsException
     */
    protected void saveSettingsTo(final QuickFormFlowVariableConfig<? extends DialogNodeValue> config)
        throws InvalidSettingsException {
        config.setLabel(getLabel());
        config.setDescription(getDescription());
        config.setFlowVariableName(getFlowVariableName());
        if (!validateParameterName()) {
            throw new InvalidSettingsException(
                "Parameter name not valid.\nMust only consist of word characters or "
                + "dashes - no spaces no special characters. Name must start with a "
                + "letter, then it may contain any word character (including '-' and '_') "
                + "and ends with a word character (no '-' or '_'),");
        }
        config.setParameterName(getParameterName());
        config.setHideInWizard(getHideInWizard());
        config.setHideInDialog(getHideInDialog());
        config.setRequired(getRequired());
    }

    /** {@inheritDoc} */
    @Override
    protected abstract void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException;

    /** {@inheritDoc} */
    @Override
    protected abstract void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException;

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadCurrentValue(final NodeSettingsRO value) throws InvalidSettingsException {
        ValueOverwriteMode mode = ValueOverwriteMode.valueOf(value.getString(QuickFormNodeModel.CFG_OVERWRITE_MODE));
        NodeSettingsRO valueSettings = value.getNodeSettings(QuickFormNodeModel.CFG_CURRENT_VALUE);
        if (mode == ValueOverwriteMode.NONE) {
            m_statusBarLabel.setVisible(false);
        } else {
            String overwrittenBy = "";
            switch (mode) {
                case DIALOG:
                    overwrittenBy = "dialog";
                    break;
                case WIZARD:
                    overwrittenBy = "wizard";
                    break;
                default:
                    overwrittenBy = "unknown";
            }
            String fullText =
                "Value overwritten by " + overwrittenBy + ", current value:\n" + getValueString(valueSettings);
            m_statusBarLabel.setText("<html>" + fullText.replace("\n", "<br>") + "</html>");
            m_statusBarLabel.setVisible(true);
        }
    }

    /**
     * Loads a value with the current setting and creates a string displaying the contained values.
     *
     * Is used for the overwrite label.
     *
     * @param settings Object containing the settings of the value
     * @return String representing the value
     * @throws InvalidSettingsException If the settings are invalid
     */
    protected abstract String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException;

}
