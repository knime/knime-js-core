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
package org.knime.js.base.node.output.image;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.util.LabeledViewNodeDialog;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class ImageOutputNodeDialog extends LabeledViewNodeDialog {

    private final JCheckBox m_maxWidthChecker;
    private final JSpinner m_maxWidthSpinner;
    private final JCheckBox m_maxHeightChecker;
    private final JSpinner m_maxHeightSpinner;

    /**
     * Create new dialog.
     */
    public ImageOutputNodeDialog() {
        m_maxWidthChecker = new JCheckBox("Maximum Width", true);
        m_maxHeightChecker = new JCheckBox("Maximum Height", true);
        m_maxWidthSpinner = new JSpinner(new SpinnerNumberModel(300, 20, Integer.MAX_VALUE, 50));
        m_maxHeightSpinner = new JSpinner(new SpinnerNumberModel(300, 20, Integer.MAX_VALUE, 50));
        m_maxWidthChecker.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_maxWidthSpinner.setEnabled(m_maxWidthChecker.isSelected());
            }
        });
        m_maxHeightChecker.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_maxHeightSpinner.setEnabled(m_maxHeightChecker.isSelected());
            }
        });
        m_maxWidthChecker.doClick();
        m_maxWidthChecker.doClick();
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addPairToPanel(m_maxWidthChecker, m_maxWidthSpinner, panelWithGBLayout, gbc);
        addPairToPanel(m_maxHeightChecker, m_maxHeightSpinner, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        ImageOutputConfig config = new ImageOutputConfig();
        config.loadSettingsInDialog(settings);
        loadSettingsFrom(config);
        int maxWidth = config.getMaxWidth();
        int maxHeight = config.getMaxHeight();
        if ((maxHeight > 0) != m_maxHeightChecker.isSelected()) {
            m_maxHeightChecker.doClick();
        }
        if ((maxWidth > 0) != m_maxWidthChecker.isSelected()) {
            m_maxWidthChecker.doClick();
        }
        m_maxWidthSpinner.setValue(maxWidth > 0 ? maxWidth : 300);
        m_maxHeightSpinner.setValue(maxHeight > 0 ? maxHeight : 300);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ImageOutputConfig config = new ImageOutputConfig();
        saveSettingsTo(config);
        int maxWidth = m_maxWidthChecker.isSelected() ? (Integer)m_maxWidthSpinner.getValue() : -1;
        int maxHeight = m_maxHeightChecker.isSelected() ? (Integer)m_maxHeightSpinner.getValue() : -1;
        config.setMaxWidth(maxWidth);
        config.setMaxHeight(maxHeight);
        config.saveSettings(settings);
    }
}
