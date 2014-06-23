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
package org.knime.js.base.node.quickform.input.integer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class IntInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JCheckBox m_useMin;

    private final JCheckBox m_useMax;

    private final JSpinner m_min;

    private final JSpinner m_max;

    private final JSpinner m_defaultSpinner;

    private IntInputQuickFormConfig m_config;

    /** Constructors, inits fields calls layout routines. */
    IntInputQuickFormNodeDialog() {
        m_config = new IntInputQuickFormConfig();
        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();
        m_min = new JSpinner(getSpinnerModel());
        m_max = new JSpinner(getSpinnerModel());
        m_defaultSpinner = new JSpinner(getSpinnerModel());
        m_useMin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_min.setEnabled(m_useMin.isSelected());
            }
        });
        m_useMax.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_max.setEnabled(m_useMax.isSelected());
            }
        });
        m_min.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                int min = (Integer)m_min.getValue();
                if (((Integer)m_max.getValue()) < min) {
                    m_max.setValue(min);
                }
            }
        });
        m_max.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                int max = (Integer)m_max.getValue();
                if (((Integer)m_min.getValue()) > max) {
                    m_min.setValue(max);
                }
            }
        });
        createAndAddTab();
    }

    private SpinnerNumberModel getSpinnerModel() {
        return new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        JPanel minPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(0, 0, 0, 0);
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 0;
        gbc2.weighty = 0;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        minPanel.add(m_useMin, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        minPanel.add(m_min, gbc2);
        JPanel maxPanel = new JPanel(new GridBagLayout());
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_useMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_max, gbc2);
        addPairToPanel("Minimum: ", minPanel, panelWithGBLayout, gbc);
        addPairToPanel("Maximum: ", maxPanel, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultSpinner, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_defaultSpinner.setValue(m_config.getDefaultValue());
        m_useMin.setSelected(m_config.getUseMin());
        m_useMax.setSelected(m_config.getUseMax());
        m_min.setValue(m_config.getMin());
        m_max.setValue(m_config.getMax());
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        m_config.getDefaultValue().setInteger((Integer)m_defaultSpinner.getValue());
        m_config.setUseMin(m_useMin.isSelected());
        m_config.setUseMax(m_useMax.isSelected());
        m_config.setMin((Integer)m_min.getValue());
        m_config.setMax((Integer)m_max.getValue());
        m_config.saveSettings(settings);
    }

}
