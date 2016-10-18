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
 *   17 Oct 2016 (albrecht): created
 */
package org.knime.js.core.settings.slider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.core.settings.DialogUtil;
import org.knime.js.core.settings.numberFormat.NumberFormatNodeDialogUI;
import org.knime.js.core.settings.slider.SliderPipsSettings.PipMode;

/**
 * Utility class to create different Swing panels for slider settings.
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class SliderNodeDialogUI {

    /*Slider panel*/
    private final JCheckBox m_useStepCheckbox;
    private final JSpinner m_stepSpinner;
    private final JCheckBox m_lowerConnectCheckbox;
    private final JCheckBox m_upperConnectCheckbox;
    private final JRadioButton m_orientationHorizontalButton;
    private final JRadioButton m_orientationVerticalButton;
    private final JRadioButton m_directionLTRButton;
    private final JRadioButton m_directionRTLButton;
    private final JCheckBox m_tooltipsCheckbox;
    private final JCheckBox m_tooltipsFormatCheckbox;
    private final NumberFormatNodeDialogUI m_tooltipsFormat;
    private final JPanel m_tooltipsFormatPanel;

    /*Pip panel*/
    private final JCheckBox m_pipsEnableCheckbox;
    private final JComboBox<SliderPipsSettings.PipMode> m_pipsModeComboBox;
    private final JSpinner m_pipsDensitySpinner;
    private final JTextField m_pipsValuesTextField;
    private final JCheckBox m_pipsSteppedCheckbox;
    private final NumberFormatNodeDialogUI m_pipsFormat;
    private final JPanel m_pipsFormatPanel;

    /**
     *
     */
    public SliderNodeDialogUI(final int numHandles, final boolean handlesOptional) {

        /*Slider panel*/
        m_useStepCheckbox = new JCheckBox("Use Stepping");
        m_useStepCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_stepSpinner.setEnabled(m_useStepCheckbox.isSelected());
            }
        });
        m_stepSpinner = new JSpinner(new SpinnerNumberModel(1, 0, Double.POSITIVE_INFINITY, 1));
        m_lowerConnectCheckbox = new JCheckBox("Lower");
        m_upperConnectCheckbox = new JCheckBox("Upper");
        m_orientationHorizontalButton = new JRadioButton("Horizontal");
        m_orientationHorizontalButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDirectionLabels();
            }
        });
        m_orientationVerticalButton = new JRadioButton("Vertical");
        m_orientationVerticalButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDirectionLabels();
            }
        });
        m_directionLTRButton = new JRadioButton("LTR");
        m_directionRTLButton = new JRadioButton("RTL");
        m_tooltipsCheckbox = new JCheckBox("Show Tooltip");
        m_tooltipsCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableTooltipFields();
            }
        });
        m_tooltipsFormatCheckbox = new JCheckBox("Use Formatter For Tooltips");
        m_tooltipsFormatCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableTooltipFields();
            }
        });
        m_tooltipsFormat = new NumberFormatNodeDialogUI();
        m_tooltipsFormatPanel = m_tooltipsFormat.createPanel();

        /*Pips panel*/
        m_pipsEnableCheckbox = new JCheckBox("Enable Labels/Pips");
        m_pipsEnableCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enablePipFields(false);
            }
        });
        m_pipsModeComboBox = new JComboBox<>(PipMode.values());
        m_pipsModeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                enablePipFields(true);
            }
        });
        m_pipsDensitySpinner = new JSpinner(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        m_pipsValuesTextField = new JTextField();
        m_pipsSteppedCheckbox = new JCheckBox("Stepped");
        m_pipsFormat = new NumberFormatNodeDialogUI();
        m_pipsFormatPanel = m_pipsFormat.createPanel();
    }

    private void setDirectionLabels() {
        if (m_orientationHorizontalButton.isSelected()) {
            m_directionLTRButton.setText("Left To Right");
            m_directionRTLButton.setText("Right To Left");
        } else {
            m_directionLTRButton.setText("Top To Bottom");
            m_directionRTLButton.setText("Bottom To Top");
        }
    }

    private void enableTooltipFields() {
        boolean enableAll = m_tooltipsCheckbox.isSelected();
        boolean enableFormat = m_tooltipsFormatCheckbox.isSelected();
        m_tooltipsFormatCheckbox.setEnabled(enableAll);
        m_tooltipsFormatPanel.setEnabled(enableAll && enableFormat);
        m_tooltipsFormat.setEnabled(enableAll && enableFormat);
    }

    private void enablePipFields(final boolean fillDefaultValues) {
        boolean enableAll = m_pipsEnableCheckbox.isSelected();
        PipMode mode = (PipMode)m_pipsModeComboBox.getSelectedItem();
        m_pipsModeComboBox.setEnabled(enableAll);
        m_pipsDensitySpinner.setEnabled(enableAll);
        boolean enableValues = (mode == PipMode.POSITIONS || mode == PipMode.VALUES || mode == PipMode.COUNT);
        m_pipsValuesTextField.setEnabled(enableAll && enableValues);
        m_pipsSteppedCheckbox.setEnabled(enableAll && enableValues);
        m_pipsFormatPanel.setEnabled(enableAll);
        m_pipsFormat.setEnabled(enableAll);

        if (fillDefaultValues) {
            String defaultValues = "";
            if (mode == PipMode.COUNT) {
                defaultValues = "6";
            } else if (mode == PipMode.POSITIONS) {
                defaultValues = "0,25,50,75,100";
            }
            m_pipsValuesTextField.setText(defaultValues);
        }
    }

    private double[] getPipValues() throws InvalidSettingsException{
        String vString = m_pipsValuesTextField.getText();
        if (!m_pipsValuesTextField.isEnabled() || vString == null || vString == "") {
            return null;
        }
        String[] splitted = vString.split(",");
        double[] values = new double[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            try {
                values[i] = Double.parseDouble(splitted[i].trim());
            } catch (NumberFormatException e) {
                throw new InvalidSettingsException(e);
            }
        }
        return values;
    }

    private String formatPipValues(final double[] values) {
        if (values == null || values.length < 1) {
            return null;
        }
        StringBuilder builder = new StringBuilder(formatDoubleAndInt(values[0]));
        for (int i = 1; i < values.length; i++) {
            builder.append(",");
            builder.append(formatDoubleAndInt(values[i]));
        }
        return builder.toString();
    }

    //Format double as int if no decimals, otherwise as regular double
    private String formatDoubleAndInt(final double value) {
        if(value == (long)value) {
            return String.format("%d",(long)value);
        }
        else {
            return String.format("%s",value);
        }
    }

    /**
     * Creates a panel containing all settings concerning labels and ticks.
     * @return the labels/ticks panel
     */
    public JPanel createTicksTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        DialogUtil.addPairToPanel("", m_pipsEnableCheckbox, panel, gbc);
        DialogUtil.addPairToPanel("Ticks Mode", m_pipsModeComboBox, panel, gbc);
        DialogUtil.addPairToPanel("Density", m_pipsDensitySpinner, panel, gbc);
        DialogUtil.addPairToPanel("Values", m_pipsValuesTextField, panel, gbc);
        DialogUtil.addPairToPanel("", m_pipsSteppedCheckbox, panel, gbc);

        m_pipsFormatPanel.setBorder(BorderFactory.createTitledBorder("Label Format Options"));
        panel.add(m_pipsFormatPanel, gbc);

        return panel;
    }

}
