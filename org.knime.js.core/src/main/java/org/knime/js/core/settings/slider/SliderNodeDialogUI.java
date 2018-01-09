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
 *   17 Oct 2016 (albrecht): created
 */
package org.knime.js.core.settings.slider;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.js.core.settings.DialogUtil;
import org.knime.js.core.settings.numberFormat.NumberFormatNodeDialogUI;
import org.knime.js.core.settings.numberFormat.NumberFormatSettings;
import org.knime.js.core.settings.slider.SliderPipsSettings.PipMode;
import org.knime.js.core.settings.slider.SliderSettings.Direction;
import org.knime.js.core.settings.slider.SliderSettings.Orientation;

/**
 * Utility class to create different Swing panels for slider settings.
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class SliderNodeDialogUI {

    /** The key to store/load domain column settings. */
    public static final String CFG_DOMAIN_COLUMN = "DOMAIN_COLUMN";

    private static final int DEFAULT_SPINNER_WIDTH = 20;
    private static final String[] CONNECT_NAMES = new String[]{"Connect Lower", "Connect Middle", "Connect Upper"};
    private static final String[] HANDLE_NAMES = new String[]{"Minimum", "Middle", "Maximum"};
    private static final double DEFAULT_MINIMUM = 0;
    private static final double DEFAULT_MAXIMUM = 100;

    private DataTableSpec m_currentSpec;

    private final int m_numHandles;

    private boolean m_rangePanelUsed = false;
    private boolean m_startValuePanelUsed = false;
    private boolean m_sliderPanelUsed = false;
    private boolean m_ticksPanelUsed = false;

    /*Range and start values */
    private final DialogComponentColumnNameSelection m_domainColumnSelection;
    private final JCheckBox m_customRangeMinCheckbox;
    private final JCheckBox m_customRangeMaxCheckbox;
    private final JSpinner m_rangeMinValueSpinner;
    private final JSpinner m_rangeMaxValueSpinner;
    private final JSpinner[] m_startValueSpinners;
    private final JCheckBox[] m_startDomainExtendsCheckboxes;

    /*Slider panel*/
    private final JCheckBox m_useStepCheckbox;
    private final JSpinner m_stepSpinner;
    private final JCheckBox[] m_connectCheckboxes;
    private final JRadioButton m_orientationHorizontalButton;
    private final JRadioButton m_orientationVerticalButton;
    private final JRadioButton m_directionLTRButton;
    private final JRadioButton m_directionRTLButton;
    private final JCheckBox[] m_tooltipsCheckboxes;
    private final JCheckBox[] m_tooltipsFormatCheckboxes;
    private final NumberFormatNodeDialogUI[] m_tooltipsFormats;
    private final JButton[] m_tooltipsButtons;

    /*Pip panel*/
    private final JCheckBox m_ticksEnableCheckbox;
    private final JComboBox<SliderPipsSettings.PipMode> m_ticksModeComboBox;
    private final JSpinner m_ticksDensitySpinner;
    private final JTextField m_ticksValuesTextField;
    private final JCheckBox m_ticksSteppedCheckbox;
    private final NumberFormatNodeDialogUI m_pipsFormat;
    private final JPanel m_ticksFormatPanel;

    /**
     * Creates a new slider dialog UI utility component
     * @param numHandles the number of handles to display options for
     * @param handlesOptional true, if handles can be optional
     * @param domainColumnRequired true, if setting the domain column is required
     *
     */
    @SuppressWarnings("unchecked")
    public SliderNodeDialogUI(final int numHandles, final boolean handlesOptional, final boolean domainColumnRequired) {
        m_numHandles = numHandles;
        /*Range and start values*/
        SettingsModelString domainColumnModel = new SettingsModelString(CFG_DOMAIN_COLUMN, null);
        m_domainColumnSelection = new DialogComponentColumnNameSelection(domainColumnModel, "", 0, domainColumnRequired, !domainColumnRequired, DoubleValue.class);
        domainColumnModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDomainValues(true);
            }
        });
        m_customRangeMinCheckbox = new JCheckBox("Custom");
        m_customRangeMinCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDomainValues(false);
            }
        });
        m_customRangeMaxCheckbox = new JCheckBox("Custom");
        m_customRangeMaxCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDomainValues(false);
            }
        });
        m_rangeMinValueSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_MINIMUM, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        setSpinnerWidth(m_rangeMinValueSpinner);
        m_rangeMinValueSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                for (int i = 0; i < m_startValueSpinners.length; i++) {
                    setDomainExtendOnStartValue(i);
                }
            }
        });
        m_rangeMaxValueSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_MAXIMUM, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        setSpinnerWidth(m_rangeMaxValueSpinner);
        m_rangeMaxValueSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                for (int i = 0; i < m_startValueSpinners.length; i++) {
                    setDomainExtendOnStartValue(i);
                }
            }
        });
        m_startValueSpinners = new JSpinner[numHandles];
        m_startDomainExtendsCheckboxes = new JCheckBox[numHandles];
        for (int i = 0; i < numHandles; i++) {
            final int fI = i;
            double min = (double)m_rangeMinValueSpinner.getValue();
            double max = (double)m_rangeMaxValueSpinner.getValue();
            double startValue = calculateDomainExtendsStartValue(min, max, m_numHandles, i);
            m_startValueSpinners[i] = new JSpinner(new SpinnerNumberModel(startValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
            setSpinnerWidth(m_startValueSpinners[i]);
            m_startDomainExtendsCheckboxes[i] = new JCheckBox("Use domain extent");
            m_startDomainExtendsCheckboxes[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    setDomainExtendOnStartValue(fI);
                }
            });
        }

        /*Slider panel*/
        m_useStepCheckbox = new JCheckBox("Use Stepping");
        m_useStepCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_stepSpinner.setEnabled(m_useStepCheckbox.isSelected());
            }
        });
        m_stepSpinner = new JSpinner(new SpinnerNumberModel(1, 0, Double.POSITIVE_INFINITY, 1));
        setSpinnerWidth(m_stepSpinner);
        m_connectCheckboxes = new JCheckBox[numHandles+1];
        for (int i = 0; i <= numHandles; i++) {
            StringBuilder connectLabel = new StringBuilder(i == 0 ? CONNECT_NAMES[0] : i == numHandles ? CONNECT_NAMES[2] : CONNECT_NAMES[1]);
            if (numHandles > 2 && i != 0 && i != numHandles) {
                connectLabel.append(" ");
                connectLabel.append(i);
            }
            m_connectCheckboxes[i] = new JCheckBox(connectLabel.toString());
        }
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
        m_tooltipsCheckboxes = new JCheckBox[numHandles];
        m_tooltipsFormatCheckboxes = new JCheckBox[numHandles];
        m_tooltipsFormats = new NumberFormatNodeDialogUI[numHandles];
        m_tooltipsButtons = new JButton[numHandles];
        for (int i = 0; i < numHandles; i++) {
            final int fI = new Integer(i);
            StringBuilder tooltipLabel = new StringBuilder("Show Tooltip");
            if (numHandles > 1) {
                tooltipLabel.append(" for ");
                tooltipLabel.append(i == 0 ? HANDLE_NAMES[0] : i == numHandles - 1 ? HANDLE_NAMES[2] : HANDLE_NAMES[1]);
                if (numHandles > 3 && i != 0 && i != numHandles -1) {
                    tooltipLabel.append(" ");
                    tooltipLabel.append(i);
                }
            }
            m_tooltipsCheckboxes[i] = new JCheckBox(tooltipLabel.toString());
            m_tooltipsCheckboxes[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    enableTooltipFields(fI);
                }
            });
            m_tooltipsFormatCheckboxes[i] = new JCheckBox("Use Formatter For Tooltips");
            m_tooltipsFormatCheckboxes[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    enableTooltipFields(fI);
                }
            });
            m_tooltipsFormats[i] = new NumberFormatNodeDialogUI();
            m_tooltipsButtons[i] = new JButton("Format Options");
            m_tooltipsButtons[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    JDialog dialog = m_tooltipsFormats[fI].createDialog(getParentFrame(m_tooltipsButtons[fI]), "Format Options");
                    dialog.setLocationRelativeTo(m_tooltipsButtons[fI]);
                    dialog.setVisible(true);
                }
            });
        }

        /*Pips panel*/
        m_ticksEnableCheckbox = new JCheckBox("Enable Labels and Ticks");
        m_ticksEnableCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enablePipFields(false);
            }
        });
        m_ticksModeComboBox = new JComboBox<>(PipMode.values());
        m_ticksModeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                enablePipFields(true);
            }
        });
        m_ticksDensitySpinner = new JSpinner(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        m_ticksValuesTextField = new JTextField();
        m_ticksSteppedCheckbox = new JCheckBox("Stepped");
        m_pipsFormat = new NumberFormatNodeDialogUI();
        m_ticksFormatPanel = m_pipsFormat.createPanel();
    }

    private void setSpinnerWidth(final JSpinner spinner) {
        final JSpinner.DefaultEditor editor =
                (JSpinner.DefaultEditor)spinner.getEditor();
        editor.getTextField().setColumns(DEFAULT_SPINNER_WIDTH);
        editor.getTextField().setFocusLostBehavior(JFormattedTextField.COMMIT);
    }

    /**
     * Calculates a slider handle start value given a slider range, and a handle index.
     * The first and last index are set to the minimum, resp. maximum values of the range, all intermediate indices are interpolated.
     * @param minimum the slider range minimum
     * @param maximum the slider range maximum
     * @param numHandles the number of handles on the slider
     * @param index the index of the handle to calculate the start value for
     * @return the calculated start value
     */
    public static double calculateDomainExtendsStartValue(final double minimum, final double maximum, final int numHandles, final int index) {
        double startValue = (maximum - minimum) / 2 + minimum;
        if (numHandles > 1) {
            if (index == 0) {
                startValue = minimum;
            } else if (index == numHandles - 1) {
                startValue = maximum;
            } else {
                startValue = ((maximum - minimum) / (numHandles - 1) * index) + minimum;
            }
        }
        return startValue;
    }

    private void setDomainExtendOnStartValue(final int i) {
        boolean calcDomain = m_startDomainExtendsCheckboxes[i].isSelected();
        m_startValueSpinners[i].setEnabled(!calcDomain);
        if (calcDomain) {
            double min = (double)m_rangeMinValueSpinner.getValue();
            double max = (double)m_rangeMaxValueSpinner.getValue();
            m_startValueSpinners[i].setValue(calculateDomainExtendsStartValue(min, max, m_numHandles, i));
        }
    }

    private void setDomainValues(final boolean forceDomain) {
        String domainColumn = m_domainColumnSelection.getSelected();
        DataColumnSpec colSpec = null;
        if (domainColumn != null && m_currentSpec != null) {
            colSpec = m_currentSpec.getColumnSpec(domainColumn);
        }
        m_customRangeMinCheckbox.setEnabled(domainColumn != null);
        m_customRangeMaxCheckbox.setEnabled(domainColumn != null);
        if (domainColumn == null) {
            m_customRangeMinCheckbox.setSelected(true);
            m_customRangeMaxCheckbox.setSelected(true);
        } else if (forceDomain) {
            m_customRangeMinCheckbox.setSelected(false);
            m_customRangeMaxCheckbox.setSelected(false);
        }
        m_rangeMinValueSpinner.setEnabled(m_customRangeMinCheckbox.isSelected());
        m_rangeMaxValueSpinner.setEnabled(m_customRangeMaxCheckbox.isSelected());
        if (m_currentSpec != null) {
            if (domainColumn != null && !m_customRangeMinCheckbox.isSelected()) {
                if (colSpec != null) {
                    DataCell lowerBound = colSpec.getDomain().getLowerBound();
                    if (lowerBound != null && lowerBound.getType().isCompatible(DoubleValue.class)) {
                        m_rangeMinValueSpinner.setValue(((DoubleValue)lowerBound).getDoubleValue());
                    }
                }
            }
            if (domainColumn != null && !m_customRangeMaxCheckbox.isSelected()) {
                if (colSpec != null) {
                    DataCell upperBound = colSpec.getDomain().getUpperBound();
                    if (upperBound != null && upperBound.getType().isCompatible(DoubleValue.class)) {
                        m_rangeMaxValueSpinner.setValue(((DoubleValue)upperBound).getDoubleValue());
                    }
                }
            }
        }
        if (domainColumn != null) {
            for (int i = 0; i < m_startValueSpinners.length; i++) {
                if (forceDomain) {
                    m_startDomainExtendsCheckboxes[i].setSelected(true);
                }
                setDomainExtendOnStartValue(i);
            }
            if (forceDomain && colSpec != null) {
                Integer decimalValues = null;
                if (colSpec.getType().isCompatible(IntValue.class)) {
                    decimalValues = 0;
                    m_useStepCheckbox.setSelected(true);
                    m_stepSpinner.setValue(1);
                } else if (colSpec.getType().isCompatible(DoubleValue.class)) {
                    decimalValues = 2;
                    m_useStepCheckbox.setSelected(false);
                }
                if (decimalValues != null) {
                    for (int i = 0; i < m_tooltipsFormats.length; i++) {
                        m_tooltipsFormatCheckboxes[i].setSelected(true);
                        try {
                            m_tooltipsFormats[i].setDecimals(decimalValues);
                        } catch (InvalidSettingsException e) { /* do nothing, never thrown */ }
                    }
                    try {
                        m_pipsFormat.setDecimals(decimalValues);
                    } catch (InvalidSettingsException e) { /* do nothing, never thrown */ }
                }
            }
        }
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

    private void enableTooltipFields(final int i) {
        boolean enableAll = m_tooltipsCheckboxes[i].isSelected();
        boolean enableFormat = m_tooltipsFormatCheckboxes[i].isSelected();
        m_tooltipsFormatCheckboxes[i].setEnabled(enableAll);
        m_tooltipsButtons[i].setEnabled(enableAll && enableFormat);
        m_tooltipsFormats[i].setEnabled(enableAll && enableFormat);
    }

    private void enablePipFields(final boolean fillDefaultValues) {
        boolean enableAll = m_ticksEnableCheckbox.isSelected();
        PipMode mode = (PipMode)m_ticksModeComboBox.getSelectedItem();
        m_ticksModeComboBox.setEnabled(enableAll);
        m_ticksDensitySpinner.setEnabled(enableAll);
        boolean enableValues = (mode == PipMode.POSITIONS || mode == PipMode.VALUES || mode == PipMode.COUNT);
        m_ticksValuesTextField.setEnabled(enableAll && enableValues);
        m_ticksSteppedCheckbox.setEnabled(enableAll && enableValues);
        m_ticksFormatPanel.setEnabled(enableAll);
        m_pipsFormat.setEnabled(enableAll);

        if (fillDefaultValues) {
            String defaultValues = "";
            if (mode == PipMode.COUNT) {
                defaultValues = "6";
            } else if (mode == PipMode.POSITIONS) {
                defaultValues = "0,25,50,75,100";
            }
            m_ticksValuesTextField.setText(defaultValues);
        }
    }

    private double[] getPipValues() throws InvalidSettingsException{
        String vString = m_ticksValuesTextField.getText();
        if (!m_ticksValuesTextField.isEnabled() || vString == null || vString == "") {
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
     * Returns the dialog component for the domain column selection, as this is not part of {@link SliderSettings}
     * @return the dialog component for domain column selection
     */
    public DialogComponentColumnNameSelection getDomainColumnSelection() {
        return m_domainColumnSelection;
    }

    /**
     * Returns the checkbox used, if a custom minimum is set (not part of {@link SliderSettings})
     * @return the checkbox for a custom minimum
     */
    public JCheckBox getCustomMinCheckbox() {
        return m_customRangeMinCheckbox;
    }

    /**
     * Returns the checkbox used, if a custom maximum is set (not part of {@link SliderSettings})
     * @return the checkbox for a custom maximum
     */
    public JCheckBox getCustomMaxCheckbox() {
        return m_customRangeMaxCheckbox;
    }

    /**
     * Returns an array of checkboxes used if a start value is supposed to be calculated from the domain extends
     * (not part of {@link SliderSettings}
     * @return the array of checkboxes
     */
    public JCheckBox[] getStartDomainExtendsCheckboxes() {
        return m_startDomainExtendsCheckboxes;
    }

    /**
     * Creates a panel which contains components for range selection to be included in a dialog
     * @return a new panel containing range selection components
     */
    public JPanel createRangePanel() {
        m_rangePanelUsed = true;
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        DialogUtil.addPairToPanel("Range Column: ", m_domainColumnSelection.getComponentPanel(), panel, gbc);

        JPanel minPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(0, 0, 0, 0);
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 0;
        gbc2.weighty = 0;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        minPanel.add(m_customRangeMinCheckbox, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        minPanel.add(m_rangeMinValueSpinner, gbc2);
        DialogUtil.addPairToPanel("Range Minimum:", minPanel, panel, gbc);

        JPanel maxPanel = new JPanel(new GridBagLayout());
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_customRangeMaxCheckbox, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_rangeMaxValueSpinner, gbc2);
        DialogUtil.addPairToPanel("Range Maximum:", maxPanel, panel, gbc);

        return panel;
    }

    /**
     * Creates a new panel which contains settings for the start values to be included in a dialog
     * @return a new panel containing start value components
     */
    public JPanel createStartValuePanel() {
        m_startValuePanelUsed = true;
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < m_startValueSpinners.length; i++) {
            JPanel startPanel = new JPanel(new GridBagLayout());
            gbc2.weightx = gbc2.weighty = 0;
            gbc2.gridx = gbc2.gridy = 0;
            gbc2.insets = new Insets(0, 0, 0, 0);
            startPanel.add(m_startDomainExtendsCheckboxes[i], gbc2);
            gbc2.gridx++;
            gbc2.weightx = 1;
            gbc2.insets = new Insets(0, 5, 0, 0);
            startPanel.add(m_startValueSpinners[i], gbc2);
            StringBuilder startLabel = new StringBuilder("Default ");
            if (m_numHandles > 1) {
                startLabel.append(i == 0 ? HANDLE_NAMES[0] : i == m_numHandles - 1 ? HANDLE_NAMES[2] : HANDLE_NAMES[1]);
                if (m_numHandles > 3) {
                    startLabel.append(" ");
                    startLabel.append(i);
                }
                startLabel.append(" ");
            }
            startLabel.append("Value:");
            DialogUtil.addPairToPanel(startLabel.toString(), startPanel, panel, gbc);
        }

        return panel;
    }

    /**
     * Creates a panel containing advanced slider settings.
     * @return the slider settings panel
     */
    public JPanel createSliderPanel() {
        m_sliderPanelUsed = true;
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        DialogUtil.addPairToPanel("", m_useStepCheckbox, panel, gbc);
        DialogUtil.addPairToPanel("Step Size", m_stepSpinner, panel, gbc);

        JPanel connectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = gbc2.gridy = 0;
        gbc2.weightx = 0;
        for (int i = 0; i < m_connectCheckboxes.length; i++) {
        connectPanel.add(m_connectCheckboxes[i], gbc2);
            gbc2.gridx++;
            if (i == m_connectCheckboxes.length - 1) {
                gbc2.weightx = 1;
            }
        }

        DialogUtil.addPairToPanel("Connect", connectPanel, panel, gbc);

        ButtonGroup orientationGroup = new ButtonGroup();
        orientationGroup.add(m_orientationHorizontalButton);
        orientationGroup.add(m_orientationVerticalButton);
        JPanel orientationPanel = new JPanel(new GridBagLayout());
        gbc2.gridx = gbc2.gridy = 0;
        gbc2.weightx = 0;
        orientationPanel.add(m_orientationHorizontalButton, gbc2);
        gbc2.gridx++;
        gbc2.weightx = 1;
        orientationPanel.add(m_orientationVerticalButton, gbc2);
        DialogUtil.addPairToPanel("Orientation", orientationPanel, panel, gbc);

        ButtonGroup directionGroup = new ButtonGroup();
        directionGroup.add(m_directionLTRButton);
        directionGroup.add(m_directionRTLButton);
        JPanel directionPanel = new JPanel(new GridBagLayout());
        gbc2.gridx = gbc2.gridy = 0;
        gbc2.weightx = 0;
        directionPanel.add(m_directionLTRButton, gbc2);
        gbc2.gridx++;
        gbc2.weightx = 1;
        directionPanel.add(m_directionRTLButton, gbc2);
        DialogUtil.addPairToPanel("Direction", directionPanel, panel, gbc);

        JPanel tooltipsPanel = new JPanel(new GridBagLayout());
        gbc2.gridx = gbc2.gridy = 0;
        gbc2.weightx = 0;
        gbc2.gridwidth = 1;
        for (int i = 0; i < m_tooltipsCheckboxes.length; i++) {
            tooltipsPanel.add(m_tooltipsCheckboxes[i], gbc2);
            gbc2.gridx++;
            tooltipsPanel.add(m_tooltipsFormatCheckboxes[i], gbc2);
            gbc2.gridx++;
            tooltipsPanel.add(m_tooltipsButtons[i], gbc2);
            gbc2.gridx = 0;
            gbc2.gridy++;
        }
        tooltipsPanel.setBorder(BorderFactory.createTitledBorder("Tooltips"));
        panel.add(tooltipsPanel, gbc);

        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(panel, BorderLayout.CENTER);

        return borderPanel;
    }

    /**
     * Creates a panel containing all settings concerning labels and ticks.
     * @return the labels/ticks panel
     */
    public JPanel createTicksPanel() {
        m_ticksPanelUsed = true;
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        DialogUtil.addPairToPanel("", m_ticksEnableCheckbox, panel, gbc);
        DialogUtil.addPairToPanel("Ticks Mode", m_ticksModeComboBox, panel, gbc);
        DialogUtil.addPairToPanel("Density", m_ticksDensitySpinner, panel, gbc);
        DialogUtil.addPairToPanel("Values", m_ticksValuesTextField, panel, gbc);
        DialogUtil.addPairToPanel("", m_ticksSteppedCheckbox, panel, gbc);

        m_ticksFormatPanel.setBorder(BorderFactory.createTitledBorder("Label Format Options"));
        panel.add(m_ticksFormatPanel, gbc);

        return panel;
    }

    private Frame getParentFrame(final Component comp) {
        Frame f = null;
        Container c = comp.getParent();
        while (c != null) {
            if (c instanceof Frame) {
                f = (Frame)c;
                break;
            }
            c = c.getParent();
        }
        return f;
    }

    /**
     * Loads settings into the dialog components. Components that can not be filled by the given {@link SliderSettings} need to be load separately.
     * @param settings the settings to load from
     * @param spec the {@link DataTableSpec} to use for domain range calculation
     * @throws NotConfigurableException on load error
     */
    public void loadSettingsFrom(final SliderSettings settings, final DataTableSpec spec) throws NotConfigurableException {
        loadSettingsFrom(settings, spec, false);
    }

    /**
     * Loads settings into the dialog components. Components that can not be filled by the given {@link SliderSettings} need to be load separately.
     * @param settings the settings to load from
     * @param spec the {@link DataTableSpec} to use for domain range calculation
     * @param forceDomainUpdate true, if a domain update should be forced, this can be used for previously unconfigured settings
     * @throws NotConfigurableException on load error
     */
    public void loadSettingsFrom(final SliderSettings settings, final DataTableSpec spec, final boolean forceDomainUpdate) throws NotConfigurableException {
        m_currentSpec = spec;
        if (settings != null) {
            if (settings.getRangeMinValue() != null) {
                m_rangeMinValueSpinner.setValue(settings.getRangeMinValue());
            }
            if (settings.getRangeMaxValue() != null) {
                m_rangeMaxValueSpinner.setValue(settings.getRangeMaxValue());
            }

            double[] startValues = settings.getStart();
            if (startValues != null) {
                for (int i = 0; i < m_startValueSpinners.length; i++) {
                    m_startValueSpinners[i].setValue(startValues[i]);
                }
            }

            Double step = settings.getStep();
            if (step != null) {
                m_useStepCheckbox.setSelected(true);
                m_stepSpinner.setValue(step);
            } else {
                m_useStepCheckbox.setSelected(false);
            }
            boolean[] connect = settings.getConnect();
            if (connect != null) {
                for (int i = 0; i < connect.length; i++) {
                    m_connectCheckboxes[i].setSelected(connect[i]);
                }
            } else {
                // enable connect for all odd numbers of connect fields
                if (m_connectCheckboxes.length % 2 == 1) {
                    for (int i = 0; i < m_connectCheckboxes.length; i++) {
                        m_connectCheckboxes[i].setSelected(i % 2 == 1);
                    }
                }
                // no sensible default for even number of connect fields -> leave all unchecked
            }

            boolean vertical = settings.getOrientation() == Orientation.VERTICAL;
            m_orientationHorizontalButton.setSelected(!vertical);
            m_orientationVerticalButton.setSelected(vertical);

            boolean rtl = settings.getDirection() == Direction.RTL;
            m_directionLTRButton.setSelected(!rtl);
            m_directionRTLButton.setSelected(rtl);
            Object[] tooltips = settings.getTooltips();
            if (tooltips != null) {
                for (int i = 0; i < tooltips.length; i++) {
                    Object tip = tooltips[i];
                    boolean tIsBoolean = tip instanceof Boolean;
                    boolean tIsFormat = tip instanceof NumberFormatSettings;
                    m_tooltipsCheckboxes[i].setSelected(tIsFormat || (tIsBoolean && (Boolean)tip));
                    m_tooltipsFormatCheckboxes[i].setSelected(tIsFormat);
                    m_tooltipsFormats[i].loadSettingsFrom(tIsFormat ? (NumberFormatSettings)tip : null);
                }
            }

            SliderPipsSettings pips = settings.getPips();
            if (pips != null) {
                m_ticksEnableCheckbox.setSelected(true);
                m_ticksModeComboBox.setSelectedItem(pips.getMode());
                m_ticksDensitySpinner.setValue(pips.getDensity());
                m_ticksValuesTextField.setText(formatPipValues(pips.getValues()));
                boolean stepped = pips.getStepped() == null ? false : pips.getStepped();
                m_ticksSteppedCheckbox.setSelected(stepped);
                m_pipsFormat.loadSettingsFrom(pips.getFormat());
            } else {
                m_ticksEnableCheckbox.setSelected(false);
                m_pipsFormat.loadSettingsFrom(null);
            }
        }

        setDirectionLabels();
        m_stepSpinner.setEnabled(m_useStepCheckbox.isSelected());
        for (int i = 0; i < m_tooltipsCheckboxes.length; i++) {
            enableTooltipFields(i);
        }
        enablePipFields(false);
        setDomainValues(forceDomainUpdate);
    }

    /**
     * Validates the current settings
     * @throws InvalidSettingsException on validation error
     */
    public void validateSettings() throws InvalidSettingsException {
        Double rangeMin = null;
        Double rangeMax = null;
        if (m_rangePanelUsed) {
            rangeMin = (double)m_rangeMinValueSpinner.getValue();
            rangeMax = (double)m_rangeMaxValueSpinner.getValue();
            if (rangeMin >= rangeMax) {
                throw new InvalidSettingsException("Range minimum must be smaller than range maximum");
            }
        }
        if (m_startValuePanelUsed) {
            Double previousValue = null;
            for (int i = 0; i < m_startValueSpinners.length; i++) {
                double currentValue = (double)m_startValueSpinners[i].getValue();
                if (rangeMin != null && currentValue < rangeMin) {
                    throw new InvalidSettingsException("Default value can not be smaller than range minimum.");
                }
                if (rangeMax != null && currentValue > rangeMax) {
                    throw new InvalidSettingsException("Default value can not be larger than range maximum");
                }
                if (previousValue != null && currentValue < previousValue) {
                    throw new InvalidSettingsException("Default values have to be in ascending order");
                }
                previousValue = currentValue;
            }
        }
        if (m_sliderPanelUsed) {
            if (m_useStepCheckbox.isSelected()) {
                double stepSize = (double)m_stepSpinner.getValue();
                if (rangeMin != null && rangeMax != null && stepSize > (rangeMax - rangeMin)) {
                    throw new InvalidSettingsException("Step size needs to be smaller than slider range.");
                }
            }
        }
        if (m_ticksPanelUsed) {
            if (m_ticksEnableCheckbox.isSelected()) {
                getPipValues();
            }
        }
    }

    /**
     * Saves the current settings to a given {@link SliderSettings} object
     * @param settings the settings object to save to
     * @throws InvalidSettingsException on validation error
     */
    public void saveSettings(final SliderSettings settings) throws InvalidSettingsException {
        validateSettings();

        if (m_rangePanelUsed) {
            settings.setRangeMinValue((double)m_rangeMinValueSpinner.getValue());
            settings.setRangeMaxValue((double)m_rangeMaxValueSpinner.getValue());
        }

        if (m_startValuePanelUsed) {
            double[] startValues = new double[m_startValueSpinners.length];
            for (int i = 0; i < m_startValueSpinners.length; i++) {
                startValues[i] = (double)m_startValueSpinners[i].getValue();
            }
            settings.setStart(startValues);
        }

        if (m_sliderPanelUsed) {
            if (m_useStepCheckbox.isSelected()) {
                settings.setStep((Double)m_stepSpinner.getValue());
            }
            boolean[] connectArray = new boolean[m_connectCheckboxes.length];
            for (int i = 0; i < m_connectCheckboxes.length; i++) {
                connectArray[i] = m_connectCheckboxes[i].isSelected();
            }
            settings.setConnect(connectArray);
            settings.setOrientation(m_orientationVerticalButton.isSelected() ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            settings.setDirection(m_directionRTLButton.isSelected() ? Direction.RTL : Direction.LTR);
            Object[] tooltipArray = new Object[m_tooltipsCheckboxes.length];
            boolean tipSet = false;
            for (int i = 0; i < m_tooltipsCheckboxes.length; i++) {
                tooltipArray[i] = m_tooltipsCheckboxes[i].isSelected();
                if (m_tooltipsCheckboxes[i].isSelected()) {
                    tipSet = true;
                    if (m_tooltipsFormatCheckboxes[i].isSelected()) {
                        tooltipArray[i] = m_tooltipsFormats[i].saveSettingsTo();
                    }
                }
            }
            if (tipSet) {
                settings.setTooltips(tooltipArray);
            } else {
                settings.setTooltips(null);
            }
        }

        if (m_ticksPanelUsed) {
            if (m_ticksEnableCheckbox.isSelected()) {
                SliderPipsSettings pipsSettings = new SliderPipsSettings();
                pipsSettings.setMode((PipMode)m_ticksModeComboBox.getSelectedItem());
                pipsSettings.setDensity((Integer)m_ticksDensitySpinner.getValue());
                if (m_ticksValuesTextField.isEnabled()) {
                    pipsSettings.setValues(getPipValues());
                }
                if (m_ticksSteppedCheckbox.isEnabled()) {
                    pipsSettings.setStepped(m_ticksSteppedCheckbox.isSelected());
                }
                NumberFormatSettings pipsFormat = m_pipsFormat.saveSettingsTo();
                pipsSettings.setFormat(pipsFormat);
                settings.setPips(pipsSettings);
            } else {
                settings.setPips(null);
            }
        }
    }

}
