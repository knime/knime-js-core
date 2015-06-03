/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
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
 * History
 *   13.05.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.lift;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.base.node.viz.liftchart.LiftChartNodeModel;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.NominalValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class LiftChartNodeDialogPane extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final JCheckBox m_hideInWizardCheckBox;
    private final JCheckBox m_generateImageCheckBox;
    private final JCheckBox m_showGridCheckBox;
    private final JCheckBox m_resizeViewToWindow;
    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_imageHeightSpinner;
    private final DialogComponentColorChooser m_gridColorChooser;
    private final DialogComponentColorChooser m_dataAreaColorChooser;
    private final DialogComponentColorChooser m_backgroundColorChooser;

    private SettingsModelString m_responseColumn =
            LiftChartNodeModel.createResponseColumnModel();

    private SettingsModelString m_probabilityColumn =
            LiftChartNodeModel.createProbabilityColumnModel();

    private SettingsModelString m_responseLabel =
            LiftChartNodeModel.createResponseLabelModel();

    private SettingsModelString m_intervalWidth =
            LiftChartNodeModel.createIntervalWidthModel();

    private DataTableSpec m_dataTableSpec;

    private DialogComponentStringSelection m_signDC;
    private DialogComponentColumnNameSelection m_responseColumnElement;
    private DialogComponentColumnNameSelection m_probabilityColumnElement;

    private JCheckBox m_showLegendCheckBox;

    private JTextField m_xAxisLiftLabelField;
    private JTextField m_yAxisLiftLabelField;
    private JTextField m_chartLiftTitleTextField;
    private JTextField m_chartLiftSubtitleTextField;

    private JTextField m_xAxisGainLabelField;
    private JTextField m_yAxisGainLabelField;
    private JTextField m_chartGainTitleTextField;
    private JTextField m_chartGainSubtitleTextField;

    private JSpinner m_lineWidthSpinner;

    private JRadioButton m_showLift;
    private JRadioButton m_showGain;

    private final JCheckBox m_enableViewConfigCheckBox;
    private final JCheckBox m_enableXAxisLabelEditCheckBox;
    private final JCheckBox m_enableYAxisLabelEditCheckBox;
    private final JCheckBox m_enableTitleChangeCheckBox;
    private final JCheckBox m_enableSubtitleChangeCheckBox;
    private final JCheckBox m_enableViewToggleCheckBox;
    private final JCheckBox m_enableSmoothingCheckBox;

    private final JComboBox<String> m_smoothing;

    /**
     * Creates a new dialog pane.
     */
    public LiftChartNodeDialogPane() {
        m_hideInWizardCheckBox = new JCheckBox("Hide in wizard");
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_showGridCheckBox = new JCheckBox("Show grid");
        m_resizeViewToWindow = new JCheckBox("Resize view to fill window");
        m_lineWidthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableXAxisLabelEditCheckBox = new JCheckBox("Enable label edit for x-axis");
        m_enableYAxisLabelEditCheckBox = new JCheckBox("Enable label edit for y-axis");
        m_enableViewToggleCheckBox = new JCheckBox("Enable toggle between views");
        m_enableSmoothingCheckBox = new JCheckBox("Enable selection of smoothing");

        m_smoothing = new JComboBox<String>(LiftChartViewConfig.getSmoothingOptions());

        m_chartLiftTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartLiftSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_xAxisLiftLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_yAxisLiftLabelField = new JTextField(TEXT_FIELD_SIZE);

        m_chartGainTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartGainSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_xAxisGainLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_yAxisGainLabelField = new JTextField(TEXT_FIELD_SIZE);

        m_showLegendCheckBox = new JCheckBox("Show color legend");

        ButtonGroup bg = new ButtonGroup();
        m_showLift = new JRadioButton("Show Lift");
        m_showGain = new JRadioButton("Show Cumulative Gain");
        bg.add(m_showLift);
        bg.add(m_showGain);

        m_signDC =
                new DialogComponentStringSelection(m_responseLabel,
                        "Positive label (hits):",
                        getPossibleLabels(m_responseColumn.getStringValue()));

        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
               enableViewControls();
            }
        });

        m_responseColumn.addChangeListener(new ChangeListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_signDC.replaceListItems(getPossibleLabels(m_responseColumn
                        .getStringValue()), null);
            }
        });

        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_gridColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("gridColor", null), "Grid color: ", true);
        m_dataAreaColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("dataAreaColor", null), "Data area color: ", true);
        m_backgroundColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("backgroundColor", null), "Background color: ", true);

        m_showGridCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

            }
        });

        addTab("Data Options", initLiftSettingsPanel());
        addTab("General Plot Options", initGeneralPanel());
        addTab("Axis Configuration", initAxisPanel());
        addTab("View Controls", initControlsPanel());
    }

    private void enableViewControls() {
        boolean enable = m_enableViewConfigCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enable);
        m_enableSubtitleChangeCheckBox.setEnabled(enable);
        m_enableViewToggleCheckBox.setEnabled(enable);
        m_enableXAxisLabelEditCheckBox.setEnabled(enable);
        m_enableYAxisLabelEditCheckBox.setEnabled(enable);
        m_enableSmoothingCheckBox.setEnabled(enable);
    }

    private Component initControlsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = 20;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel viewControlsPanel = new JPanel(new GridBagLayout());
        viewControlsPanel.setBorder(BorderFactory.createTitledBorder("View edit controls"));
        panel.add(viewControlsPanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        viewControlsPanel.add(m_enableViewConfigCheckBox, cc);
        cc.gridy++;
        viewControlsPanel.add(m_enableTitleChangeCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableSubtitleChangeCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        viewControlsPanel.add(m_enableXAxisLabelEditCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableYAxisLabelEditCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        viewControlsPanel.add(m_enableViewToggleCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        viewControlsPanel.add(m_enableSmoothingCheckBox, cc);
        return panel;
    }

    private Component initAxisPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        JPanel labelsPanel1 = new JPanel(new GridBagLayout());
        labelsPanel1.setBorder(BorderFactory.createTitledBorder("Labels Lift Chart"));
        panel.add(labelsPanel1, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        labelsPanel1.add(new JLabel("Label for x axis: "), cc);
        cc.gridx++;
        labelsPanel1.add(m_xAxisLiftLabelField, cc);
        cc.gridx = 0;
        cc.gridy++;
        labelsPanel1.add(new JLabel("Label for y axis: "), cc);
        cc.gridx++;
        labelsPanel1.add(m_yAxisLiftLabelField, cc);
        c.gridx = 0;
        c.gridy++;

        JPanel labelsPanel2 = new JPanel(new GridBagLayout());
        labelsPanel2.setBorder(BorderFactory.createTitledBorder("Labels Gains Chart"));
        panel.add(labelsPanel2, c);
        cc.gridx = 0;
        cc.gridy = 0;
        labelsPanel2.add(new JLabel("Label for x axis: "), cc);
        cc.gridx++;
        labelsPanel2.add(m_xAxisGainLabelField, cc);
        cc.gridx = 0;
        cc.gridy++;
        labelsPanel2.add(new JLabel("Label for y axis: "), cc);
        cc.gridx++;
        labelsPanel2.add(m_yAxisGainLabelField, cc);
        c.gridx = 0;
        c.gridy++;

        JPanel legendPanel = new JPanel(new GridBagLayout());
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legends"));
        panel.add(legendPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        legendPanel.add(m_showLegendCheckBox, cc);
        c.gridx = 0;
        c.gridy++;

        return panel;
    }

    private Component initLiftSettingsPanel() {

        m_responseColumnElement
        = new DialogComponentColumnNameSelection(m_responseColumn,
                                                    "Response Column", 0, NominalValue.class);
        m_probabilityColumnElement
        = new DialogComponentColumnNameSelection(m_probabilityColumn,
                                                     "Probability Column", 0, DoubleValue.class);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(m_responseColumnElement.getComponentPanel(), c);
        c.gridy++;
        panel.add(m_probabilityColumnElement.getComponentPanel(), c);
        c.gridy++;
        panel.add(m_signDC.getComponentPanel(), c);
        c.gridy++;
        DialogComponentStringSelection intervalWidth = new DialogComponentStringSelection(m_intervalWidth,
            "Interval width in %:",
            "0.5", "1", "2", "2.5", "5", "10", "12.5", "20", "25");
        panel.add(intervalWidth.getComponentPanel(), c);

        return panel;
    }

    private Component initGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;

        JPanel genPanel = new JPanel(new GridBagLayout());
        genPanel.setBorder(BorderFactory.createTitledBorder("General"));
        panel.add(genPanel, c);
        cc.gridwidth = 2;
        genPanel.add(m_hideInWizardCheckBox, cc);
        cc.gridx = 2;
        genPanel.add(m_generateImageCheckBox, cc);
        cc.gridy++;

        cc.gridx = 0;
        genPanel.add(m_showLift, cc);
        cc.gridx = 2;
        genPanel.add(m_showGain, cc);
        c.gridy++;

        cc.gridx = 0;
        cc.gridy = 0;
        cc.gridwidth = 1;

        JPanel sizesPanel = new JPanel(new GridBagLayout());
        sizesPanel.setBorder(BorderFactory.createTitledBorder("Display"));
        panel.add(sizesPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        sizesPanel.add(new JLabel("Width of image (in px): "), cc);
        cc.gridx++;
        m_imageWidthSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_imageWidthSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        sizesPanel.add(new JLabel("Height of image (in px): "), cc);
        cc.gridx++;
        m_imageHeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_imageHeightSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        sizesPanel.add(new JLabel("Line width (in px): "), cc);
        cc.gridx++;
        m_lineWidthSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_lineWidthSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        sizesPanel.add(new JLabel("Smoothing: "), cc);
        cc.gridx++;
        sizesPanel.add(m_smoothing, cc);
        cc.gridx = 0;
        cc.gridy++;
        cc.anchor = GridBagConstraints.CENTER;
        sizesPanel.add(m_resizeViewToWindow, cc);
        c.gridy++;

        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBorder(BorderFactory.createTitledBorder("Background"));
        panel.add(backgroundPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        backgroundPanel.add(m_backgroundColorChooser.getComponentPanel(), cc);
        cc.gridy++;
        backgroundPanel.add(m_dataAreaColorChooser.getComponentPanel(), cc);
        cc.gridy++;
        backgroundPanel.add(m_showGridCheckBox, cc);
        cc.gridy++;
        backgroundPanel.add(m_gridColorChooser.getComponentPanel(), cc);

        return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
            throws NotConfigurableException {
        if (specs == null || specs.length == 0 || specs[0] == null) {
            throw new NotConfigurableException("No column specs given.");
        }

        LiftChartViewConfig config = new LiftChartViewConfig();
        config.loadSettingsForDialog(settings, specs[0]);
        m_hideInWizardCheckBox.setSelected(config.getHideInWizard());
        m_generateImageCheckBox.setSelected(config.getGenerateImage());

        m_showGridCheckBox.setSelected(config.getShowGrid());
        m_resizeViewToWindow.setSelected(config.getResizeToWindow());

        m_imageWidthSpinner.setValue(config.getImageWidth());
        m_imageHeightSpinner.setValue(config.getImageHeight());
        m_backgroundColorChooser.setColor(config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(config.getDataAreaColor());
        m_gridColorChooser.setColor(config.getGridColor());
        m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());
        m_lineWidthSpinner.setValue(config.getLineWidth());

        DataTableSpec specNull = specs[0];
        if (specNull.getNumColumns() == 0) {
            throw new NotConfigurableException("No column specs given.");
        }
        boolean foundColumn = false;
        for (int col = 0; col < specNull.getNumColumns(); col++) {
            DataColumnSpec cs = specNull.getColumnSpec(col);
            if (cs.getType().isCompatible(NominalValue.class)
                    && cs.getDomain().hasValues()) {
                foundColumn = true;
                break;
            }
        }
        if (!foundColumn) {
            throw new NotConfigurableException(
                    "No nominal column with domain values found."
                            + " Please use the domain calculator first.");
        }

        m_responseColumnElement.loadSettingsFrom(settings, specs);
        m_probabilityColumnElement.loadSettingsFrom(settings, specs);
        m_signDC.loadSettingsFrom(settings, specs);
        m_responseColumn.setStringValue(config.getResponseColumn());
        m_probabilityColumn.setStringValue(config.getProbabilityColumn());
        m_responseLabel.setStringValue(config.getResponseLabel());

        String intervalWidth = Double.toString(config.getIntervalWidth());
        if (intervalWidth.endsWith(".0")) {
            intervalWidth = intervalWidth.split("\\.")[0];
        }
        m_intervalWidth.setStringValue(intervalWidth);
        m_dataTableSpec = specNull;

        m_signDC.replaceListItems(getPossibleLabels(m_responseColumn
                .getStringValue()), null);

        m_showLegendCheckBox.setSelected(config.getShowLegend());

        m_xAxisLiftLabelField.setText(config.getxAxisTitleLift());
        m_yAxisLiftLabelField.setText(config.getyAxisTitleLift());
        m_chartLiftTitleTextField.setText(config.getTitleLift());
        m_chartLiftSubtitleTextField.setText(config.getSubtitleLift());

        m_xAxisGainLabelField.setText(config.getxAxisTitleGain());
        m_yAxisGainLabelField.setText(config.getyAxisTitleGain());
        m_chartGainTitleTextField.setText(config.getTitleGain());
        m_chartGainSubtitleTextField.setText(config.getSubtitleGain());
        m_showGain.setSelected(config.getShowGainChart());
        m_showLift.setSelected(!config.getShowGainChart());

        m_enableViewConfigCheckBox.setSelected(config.getEnableControls());
        m_enableTitleChangeCheckBox.setSelected(config.getEnableEditTitle());
        m_enableSubtitleChangeCheckBox.setSelected(config.getEnableEditSubtitle());
        m_enableXAxisLabelEditCheckBox.setSelected(config.getEnableEditXAxisLabel());
        m_enableYAxisLabelEditCheckBox.setSelected(config.getEnableEditYAxisLabel());
        m_enableViewToggleCheckBox.setSelected(config.getEnableViewToggle());
        m_enableSmoothingCheckBox.setSelected(config.getEnableSmoothing());
        m_smoothing.setSelectedItem(LiftChartViewConfig.mapSmoothingValueToInput(config.getSmoothing()));
    }

    private List<String> getPossibleLabels(final String resColumn) {
        List<String> labels = new LinkedList<String>();

        if (m_dataTableSpec == null) {
            labels.add("No values given null");
            return labels;
        }
        DataColumnSpec cs = m_dataTableSpec.getColumnSpec(resColumn);

        if (cs == null) {
            labels.add("Column doesn't exist");
            return labels;
        }

        if (!cs.getDomain().hasValues()) {
            labels.add("No values given no val");
            return labels;
        }
        for (DataCell cell : cs.getDomain().getValues()) {
            labels.add(((StringValue)cell).getStringValue());
        }
        return labels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        LiftChartViewConfig config = new LiftChartViewConfig();
        config.setHideInWizard(m_hideInWizardCheckBox.isSelected());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());

        config.setResizeToWindow(m_resizeViewToWindow.isSelected());

        config.setTitleLift(m_chartLiftTitleTextField.getText());
        config.setSubtitleLift(m_chartLiftSubtitleTextField.getText());
        config.setxAxisTitleLift(m_xAxisLiftLabelField.getText());
        config.setyAxisTitleLift(m_yAxisLiftLabelField.getText());

        config.setTitleGain(m_chartGainTitleTextField.getText());
        config.setSubtitleGain(m_chartGainSubtitleTextField.getText());
        config.setxAxisTitleGain(m_xAxisGainLabelField.getText());
        config.setyAxisTitleGain(m_yAxisGainLabelField.getText());

        config.setShowLegend(m_showLegendCheckBox.isSelected());

        config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        config.setBackgroundColor(m_backgroundColorChooser.getColor());
        config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        config.setGridColor(m_gridColorChooser.getColor());
        config.setShowGrid(m_showGridCheckBox.isSelected());
        config.setResponseColumn(m_responseColumn.getStringValue());
        config.setResponseLabel(m_responseLabel.getStringValue());
        config.setProbabilityColumn(m_probabilityColumn.getStringValue());
        config.setIntervalWidth(Double.parseDouble(m_intervalWidth.getStringValue()));
        config.setLineWidth((int)m_lineWidthSpinner.getValue());
        config.setShowGainChart(m_showGain.isSelected());

        config.setEnableControls(m_enableViewConfigCheckBox.isSelected());
        config.setEnableEditTitle(m_enableTitleChangeCheckBox.isSelected());
        config.setEnableEditSubtitle(m_enableSubtitleChangeCheckBox.isSelected());
        config.setEnableEditXAxisLabel(m_enableXAxisLabelEditCheckBox.isSelected());
        config.setEnableEditYAxisLabel(m_enableYAxisLabelEditCheckBox.isSelected());
        config.setEnableViewToggle(m_enableViewToggleCheckBox.isSelected());
        config.setEnableSmoothing(m_enableSmoothingCheckBox.isSelected());
        config.setSmoothing(LiftChartViewConfig.mapSmoothingInputToValue(m_smoothing.getSelectedItem().toString()));
        config.saveSettings(settings);
    }
}
