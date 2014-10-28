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
package org.knime.js.base.node.quickform.input.fileupload;

import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * The dialog for the file upload quick form node.
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class FileUploadQuickFormNodeDialog extends QuickFormNodeDialog {

    private final FilesHistoryPanel m_fileHistoryPanel;
    private final JTextField m_validExtensionsField;

    private FileUploadQuickFormConfig m_config;

    /** Constructors, inits fields calls layout routines. */
    FileUploadQuickFormNodeDialog() {
        m_config = new FileUploadQuickFormConfig();
        m_fileHistoryPanel =
                new FilesHistoryPanel("file_upload_quick_form", false);
            m_validExtensionsField = new JTextField(DEF_TEXTFIELD_WIDTH);
            m_validExtensionsField.addFocusListener(new FocusListener() {
                @Override
                public void focusLost(final FocusEvent e) {
                    String t = m_validExtensionsField.getText();
                    if (t == null) {
                        t = "";
                    }
                    try {
                        m_fileHistoryPanel.setSuffixes(t.split(","));
                    } catch (Exception exc) {
                        NodeLogger.getLogger(
                            FileUploadQuickFormNodeDialog.class).debug(
                                        "Unable to update file suffixes", exc);
                    }
                }

                @Override
                public void focusGained(final FocusEvent e) {
                    // nothing to do
                }
            });
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Valid File Extensions:",
            m_validExtensionsField, panelWithGBLayout, gbc);
        addPairToPanel("Default File:",
            m_fileHistoryPanel, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        String[] fileExtensions = m_config.getFileTypes();
        if (fileExtensions == null) {
            m_validExtensionsField.setText("");
        } else {
            StringBuilder b = new StringBuilder();
            for (String s : fileExtensions) {
                b.append(b.length() > 0 ? "," : "").append(s);
            }
            m_validExtensionsField.setText(b.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        m_config.getDefaultValue().setPath(m_fileHistoryPanel.getSelectedFile());
        m_config.setFileTypes(m_validExtensionsField.getText().split(","));
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        FileUploadQuickFormValue value = new FileUploadQuickFormValue();
        value.loadFromNodeSettings(settings);
        return value.getPath();
    }
}