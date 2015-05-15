/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   21.10.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.output.filedownload;

import java.awt.GridBagConstraints;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FlowVariableListCellRenderer;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.js.base.util.LabeledViewNodeDialog;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class FileDownloadNodeDialog extends LabeledViewNodeDialog {

    private final JComboBox<FlowVariable> m_filePathVariableNameCombo;
    private final JTextField m_linkTitle;
    private final JTextField m_resourceName;

    /**
     * Create new dialog.
     */
    public FileDownloadNodeDialog() {
        m_filePathVariableNameCombo = new JComboBox<FlowVariable>(new DefaultComboBoxModel<FlowVariable>());
        m_filePathVariableNameCombo.setRenderer(new FlowVariableListCellRenderer());
        m_linkTitle = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_resourceName = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    private String getFlowVariableName() {
        FlowVariable v = (FlowVariable)m_filePathVariableNameCombo.getSelectedItem();
        if (v != null) {
            return v.getName();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Link Title", m_linkTitle, panelWithGBLayout, gbc);
        addPairToPanel("Output resource name", m_resourceName, panelWithGBLayout, gbc);
        addPairToPanel("File Path Variable", m_filePathVariableNameCombo, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        FileDownloadConfig config = new FileDownloadConfig();
        config.loadSettingsInDialog(settings);
        loadSettingsFrom(config);
        m_linkTitle.setText(config.getLinkTitle());
        m_resourceName.setText(config.getResourceName());
        String flowVariableName = config.getFlowVariable();

        FlowVariable selectedVar = null;
        DefaultComboBoxModel<FlowVariable> m =
            (DefaultComboBoxModel<FlowVariable>)m_filePathVariableNameCombo.getModel();
        m.removeAllElements();
        for (FlowVariable v : getAvailableFlowVariables().values()) {
            if (v.getType().equals(FlowVariable.Type.STRING)) {
                m.addElement(v);
                if (v.getName().equals(flowVariableName)) {
                    selectedVar = v;
                }
            }
        }
        if (selectedVar != null) {
            m_filePathVariableNameCombo.setSelectedItem(selectedVar);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        FileDownloadConfig config = new FileDownloadConfig();
        saveSettingsTo(config);
        config.setLinkTitle(m_linkTitle.getText());
        config.setResourceName(m_resourceName.getText());
        config.setFlowVariable(getFlowVariableName());
        config.saveSettings(settings);
    }
}
