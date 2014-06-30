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
package org.knime.js.base.node.quickform.input.date;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * The dialog for the date input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class DateInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JCheckBox m_useMin;

    private final JCheckBox m_useMax;

    private final JSpinner m_min;

    private final JSpinner m_max;

    private final JSpinner m_defaultField;

    private final ButtonGroup m_withTime;

    private final JRadioButton m_date;

    private final JRadioButton m_dateAndTime;

    private DateInputQuickFormConfig m_config;

    /** Constructors, inits fields calls layout routines. */
    DateInputQuickFormNodeDialog() {
        m_config = new DateInputQuickFormConfig();
        m_date = new JRadioButton("Date");
        m_date.setActionCommand("date");
        m_dateAndTime = new JRadioButton("Date and Time");
        m_dateAndTime.setActionCommand("dateandtime");
        m_date.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateFormat();
            }
        });
        m_dateAndTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateFormat();
            }
        });
        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();
        m_withTime = new ButtonGroup();
        m_min = new JSpinner(new SpinnerDateModel());
        m_max = new JSpinner(new SpinnerDateModel());
        m_defaultField = new JSpinner(new SpinnerDateModel());
        m_useMin.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                m_min.setEnabled(m_useMin.isSelected());
            }
        });
        m_useMax.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                m_max.setEnabled(m_useMax.isSelected());
            }
        });
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        m_withTime.add(m_date);
        m_withTime.add(m_dateAndTime);
        JPanel dateAndTimePanel = new JPanel();
        dateAndTimePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new Insets(0, 0, 0, 0);
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.weightx = 0;
        gbc4.weighty = 0;
        gbc4.gridx = 0;
        gbc4.gridy = 0;
        dateAndTimePanel.add(m_date, gbc4);
        gbc4.gridx++;
        gbc4.weightx = 1;
        dateAndTimePanel.add(m_dateAndTime, gbc4);
        JPanel minPanel = new JPanel();
        minPanel.setLayout(new GridBagLayout());
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
        JPanel maxPanel = new JPanel();
        maxPanel.setLayout(new GridBagLayout());
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_useMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_max, gbc2);
        GridBagConstraints gbc3 = (GridBagConstraints)gbc.clone();
        gbc3.insets = new Insets(0, 0, 0, 0);
        addPairToPanel("Selection: ", dateAndTimePanel, panelWithGBLayout, gbc3);
        addPairToPanel("Earliest: ", minPanel, panelWithGBLayout, gbc3);
        addPairToPanel("Latest: ", maxPanel, panelWithGBLayout, gbc3);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
    }

    /**
     * Updates the format used to select time to either {@link DateInputQuickFormNodeModel.DATE_FORMAT} or
     * {@link DateInputQuickFormNodeModel.DATE_TIME_FORMAT}, depending on the selected option.
     */
    private void updateFormat() {
        String format =
                m_dateAndTime.isSelected() ? DateInputQuickFormNodeModel.DATE_TIME_FORMAT
                        : DateInputQuickFormNodeModel.DATE_FORMAT;
        m_min.setEditor(new JSpinner.DateEditor(m_min, format));
        m_max.setEditor(new JSpinner.DateEditor(m_max, format));
        m_defaultField.setEditor(new JSpinner.DateEditor(m_defaultField, format));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_defaultField.setValue(m_config.getDefaultValue().getDate());
        m_useMin.setSelected(m_config.getUseMin());
        m_useMax.setSelected(m_config.getUseMax());
        m_min.setValue(m_config.getMin());
        m_max.setValue(m_config.getMax());
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
        if (m_config.getWithTime()) {
            m_dateAndTime.setSelected(true);
        } else {
            m_date.setSelected(true);
        }
        updateFormat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        m_config.getDefaultValue().setDate((Date)m_defaultField.getValue());
        m_config.setUseMin(m_useMin.isSelected());
        m_config.setUseMax(m_useMax.isSelected());
        m_config.setMin((Date)m_min.getValue());
        m_config.setMax((Date)m_max.getValue());
        m_config.setWithTime(m_dateAndTime.isSelected());
        m_config.saveSettings(settings);
    }

}
