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
package org.knime.js.base.node.viz.plotter.line;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.core.node.util.StringHistory;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class LinePlotNodeDialogPane extends NodeDialogPane {

    /**
     * Key for the string history to re-use user entered date formats.
     */
    public static final String FORMAT_HISTORY_KEY = "javascript-date-formats";
    /** Set of predefined date and time formats for JavaScript. */
    public static final LinkedHashSet<String> PREDEFINED_FORMATS
    = createPredefinedFormats();

    private static final int TEXT_FIELD_SIZE = 20;

    private final JCheckBox m_hideInWizardCheckBox;
    private final JCheckBox m_generateImageCheckBox;
    private final JCheckBox m_showLegendCheckBox;
    private final JCheckBox m_autoRangeAxisCheckBox;
    private final JCheckBox m_useDomainInformationCheckBox;
    private final JCheckBox m_showGridCheckBox;
    private final JCheckBox m_showCrosshairCheckBox;
    private final JCheckBox m_snapToPointsCheckBox;
    private final JCheckBox m_resizeViewToWindow;
    private final JCheckBox m_enableViewConfigCheckBox;
    private final JCheckBox m_enableTitleChangeCheckBox;
    private final JCheckBox m_enableSubtitleChangeCheckBox;
    private final JCheckBox m_enableXColumnChangeCheckBox;
    private final JCheckBox m_enableYColumnChangeCheckBox;
    private final JCheckBox m_enableXAxisLabelEditCheckBox;
    private final JCheckBox m_enableYAxisLabelEditCheckBox;
    private final JCheckBox m_allowMouseWheelZoomingCheckBox;
    private final JCheckBox m_allowDragZoomingCheckBox;
    private final JCheckBox m_allowPanningCheckBox;
    private final JCheckBox m_showZoomResetCheckBox;
    private final JCheckBox m_enableDotSizeChangeCheckBox;
    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_allowRectangleSelectionCheckBox;
    private final JCheckBox m_allowLassoSelectionCheckBox;

    private final JSpinner m_maxRowsSpinner;
    private final JTextField m_appendedColumnName;
    private final JTextField m_chartTitleTextField;
    private final JTextField m_chartSubtitleTextField;
    private final ColumnSelectionPanel m_xColComboBox;
    private final DataColumnSpecFilterPanel m_yColFilter;
    private final JTextField m_xAxisLabelField;
    private final JTextField m_yAxisLabelField;
    private final JSpinner m_dotSize;
    private final DialogComponentStringSelection m_dateFormatChooser;
    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_imageHeightSpinner;
    private final DialogComponentColorChooser m_gridColorChooser;
    private final DialogComponentColorChooser m_dataAreaColorChooser;
    private final DialogComponentColorChooser m_backgroundColorChooser;

    /**
     * Creates a new dialog pane.
     */
    public LinePlotNodeDialogPane() {
        m_hideInWizardCheckBox = new JCheckBox("Hide in wizard");
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_showLegendCheckBox = new JCheckBox("Show color legend");
        m_autoRangeAxisCheckBox = new JCheckBox("Auto range axes");
        m_useDomainInformationCheckBox = new JCheckBox("Use domain information");
        m_showGridCheckBox = new JCheckBox("Show grid");
        m_showCrosshairCheckBox = new JCheckBox("Enable mouse crosshair");
        m_snapToPointsCheckBox = new JCheckBox("Snap to data pionts");
        m_resizeViewToWindow = new JCheckBox("Resize view to fill window");
        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableXColumnChangeCheckBox = new JCheckBox("Enable column chooser for x-axis");
        m_enableYColumnChangeCheckBox = new JCheckBox("Enable column chooser for y-axis");
        m_enableXAxisLabelEditCheckBox = new JCheckBox("Enable label edit for x-axis");
        m_enableYAxisLabelEditCheckBox = new JCheckBox("Enable label edit for y-axis");
        m_enableDotSizeChangeCheckBox = new JCheckBox("Enable dot size edit");
        m_allowMouseWheelZoomingCheckBox = new JCheckBox("Enable mouse wheel zooming");
        m_allowDragZoomingCheckBox = new JCheckBox("Enable drag zooming");
        m_allowPanningCheckBox = new JCheckBox("Enable panning");
        m_showZoomResetCheckBox = new JCheckBox("Show zoom reset button");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_allowRectangleSelectionCheckBox = new JCheckBox("Enable rectangular selection");
        m_allowLassoSelectionCheckBox = new JCheckBox("Enable lasso selection");

        m_maxRowsSpinner = new JSpinner();
        m_appendedColumnName = new JTextField(TEXT_FIELD_SIZE);
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        Border xColBoxBorder = BorderFactory.createTitledBorder("Choose column for x axis");
        @SuppressWarnings("unchecked")
        DataValueColumnFilter xColFilter = new DataValueColumnFilter(DoubleValue.class, StringValue.class);
        m_xColComboBox = new ColumnSelectionPanel(xColBoxBorder, xColFilter, false, true);
        m_yColFilter = new DataColumnSpecFilterPanel();
        m_xAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_yAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_dotSize = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_gridColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("gridColor", null), "Grid color: ", true);
        m_dataAreaColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("dataAreaColor", null), "Data area color: ", true);
        m_backgroundColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("backgroundColor", null), "Background color: ", true);

        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
               enableViewControls();
            }
        });
        m_showCrosshairCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableCrosshairControls();
            }
        });
        m_enableSelectionCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelectionControls();
            }
        });
        m_showGridCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

            }
        });

        m_dateFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(LinePlotViewConfig.DATE_FORMAT, null),
                "Date format: ", PREDEFINED_FORMATS, true);

        addTab("Options", initOptionsPanel());
        addTab("Axis Configuration", initAxisPanel());
        addTab("General Plot Options", initGeneralPanel());
        addTab("View Controls", initControlsPanel());
    }

    /**
     * @return
     */
    private Component initOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(m_hideInWizardCheckBox, c);
        c.gridx += 2;
        panel.add(m_generateImageCheckBox, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        panel.add(new JLabel("Maximum number of rows: "), c);
        c.gridx += 1;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        panel.add(m_maxRowsSpinner, c);
        c.gridx++;
        panel.add(new JLabel("Selection column name: "), c);
        c.gridx++;
        panel.add(m_appendedColumnName, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        m_xColComboBox.setPreferredSize(new Dimension(260, 50));
        panel.add(m_xColComboBox, c);
        c.gridy++;
        c.gridwidth = 4;
        panel.add(m_yColFilter, c);

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

        JPanel labelsPanel = new JPanel(new GridBagLayout());
        labelsPanel.setBorder(BorderFactory.createTitledBorder("Labels"));
        panel.add(labelsPanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        labelsPanel.add(new JLabel("Label for x axis: "), cc);
        cc.gridx++;
        labelsPanel.add(m_xAxisLabelField, cc);
        cc.gridx = 0;
        cc.gridy++;
        labelsPanel.add(new JLabel("Label for y axis: "), cc);
        cc.gridx++;
        labelsPanel.add(m_yAxisLabelField, cc);
        c.gridx = 0;
        c.gridy++;

        JPanel formatPanel = new JPanel(new GridBagLayout());
        formatPanel.setBorder(BorderFactory.createTitledBorder("Formatter"));
        panel.add(formatPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        formatPanel.add(m_dateFormatChooser.getComponentPanel(), cc);
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

        JPanel rangePanel = new JPanel(new GridBagLayout());
        rangePanel.setBorder(BorderFactory.createTitledBorder("Axes ranges"));
        panel.add(rangePanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        rangePanel.add(m_autoRangeAxisCheckBox, cc);
        cc.gridx++;
        rangePanel.add(m_useDomainInformationCheckBox, cc);

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

        JPanel titlesPanel = new JPanel(new GridBagLayout());
        titlesPanel.setBorder(BorderFactory.createTitledBorder("Titles"));
        panel.add(titlesPanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        titlesPanel.add(new JLabel("Chart title: "), cc);
        cc.gridx++;
        titlesPanel.add(m_chartTitleTextField, cc);
        cc.gridx = 0;
        cc.gridy++;
        titlesPanel.add(new JLabel("Chart subtitle: "), cc);
        cc.gridx++;
        titlesPanel.add(m_chartSubtitleTextField, cc);
        c.gridx = 0;
        c.gridy++;

        JPanel sizesPanel = new JPanel(new GridBagLayout());
        sizesPanel.setBorder(BorderFactory.createTitledBorder("Sizes"));
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
        cc.anchor = GridBagConstraints.CENTER;
        sizesPanel.add(m_resizeViewToWindow, cc);

        c.gridx = 0;
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

        /*c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Dot size: "), c);
        c.gridx++;
        m_dotSize.setPreferredSize(new Dimension(100, 20));
        panel.add(m_dotSize, c);
        c.gridx++;
        c.gridwidth = 2;
        panel.add(m_enableDotSizeChangeCheckBox, c);*/

        return panel;
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
        viewControlsPanel.add(m_enableXColumnChangeCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableYColumnChangeCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        viewControlsPanel.add(m_enableXAxisLabelEditCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableYAxisLabelEditCheckBox, cc);

        c.gridx = 0;
        c.gridy++;
        JPanel crosshairControlPanel = new JPanel(new GridBagLayout());
        crosshairControlPanel.setBorder(BorderFactory.createTitledBorder("Crosshair"));
        panel.add(crosshairControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        crosshairControlPanel.add(m_showCrosshairCheckBox, cc);
        cc.gridx += 2;
        crosshairControlPanel.add(m_snapToPointsCheckBox, cc);

        c.gridx = 0;
        c.gridy++;
        JPanel selectionControlPanel = new JPanel(new GridBagLayout());
        selectionControlPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        panel.add(selectionControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        selectionControlPanel.add(m_enableSelectionCheckBox, cc);
        cc.gridx++;
        selectionControlPanel.add(m_allowRectangleSelectionCheckBox, cc);
        cc.gridx += 2;
        selectionControlPanel.add(m_allowLassoSelectionCheckBox, cc);

        c.gridx = 0;
        c.gridy++;
        JPanel panControlPanel = new JPanel(new GridBagLayout());
        panControlPanel.setBorder(BorderFactory.createTitledBorder("Panning"));
        panel.add(panControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        panControlPanel.add(m_allowPanningCheckBox, cc);

        c.gridy++;
        JPanel zoomControlPanel = new JPanel(new GridBagLayout());
        zoomControlPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        panel.add(zoomControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        zoomControlPanel.add(m_allowMouseWheelZoomingCheckBox, cc);
        cc.gridx++;
        zoomControlPanel.add(m_allowDragZoomingCheckBox, cc);
        cc.gridx++;
        zoomControlPanel.add(m_showZoomResetCheckBox, cc);

        return panel;
    }

    private void enableViewControls() {
        boolean enable = m_enableViewConfigCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enable);
        m_enableSubtitleChangeCheckBox.setEnabled(enable);
        m_enableXColumnChangeCheckBox.setEnabled(enable);
        m_enableYColumnChangeCheckBox.setEnabled(enable);
        m_enableXAxisLabelEditCheckBox.setEnabled(enable);
        m_enableYAxisLabelEditCheckBox.setEnabled(enable);
        m_enableDotSizeChangeCheckBox.setEnabled(enable);
    }

    private void enableSelectionControls() {
        boolean enable = m_enableSelectionCheckBox.isSelected();
        m_allowRectangleSelectionCheckBox.setEnabled(enable);
        m_allowLassoSelectionCheckBox.setEnabled(enable);
    }

    private void enableCrosshairControls() {
        boolean enable = m_showCrosshairCheckBox.isSelected();
        m_snapToPointsCheckBox.setEnabled(enable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
            throws NotConfigurableException {
        LinePlotViewConfig config = new LinePlotViewConfig();
        config.loadSettingsForDialog(settings, specs[0]);
        m_hideInWizardCheckBox.setSelected(config.getHideInWizard());
        m_generateImageCheckBox.setSelected(config.getGenerateImage());

        m_showLegendCheckBox.setSelected(config.getShowLegend());
        m_autoRangeAxisCheckBox.setSelected(config.getAutoRangeAxes());
        m_useDomainInformationCheckBox.setSelected(config.getUseDomainInfo());
        m_showGridCheckBox.setSelected(config.getShowGrid());
        m_showCrosshairCheckBox.setSelected(config.getShowCrosshair());
        m_snapToPointsCheckBox.setSelected(config.getSnapToPoints());
        m_resizeViewToWindow.setSelected(config.getResizeToWindow());

        m_appendedColumnName.setText(config.getSelectionColumnName());
        m_enableViewConfigCheckBox.setSelected(config.getEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(config.getEnableTitleChange());
        m_enableSubtitleChangeCheckBox.setSelected(config.getEnableSubtitleChange());
        m_enableXColumnChangeCheckBox.setSelected(config.getEnableXColumnChange());
        m_enableYColumnChangeCheckBox.setSelected(config.getEnableYColumnChange());
        m_enableXAxisLabelEditCheckBox.setSelected(config.getEnableXAxisLabelEdit());
        m_enableYAxisLabelEditCheckBox.setSelected(config.getEnableYAxisLabelEdit());
        m_enableDotSizeChangeCheckBox.setSelected(config.getEnableDotSizeChange());
        m_allowMouseWheelZoomingCheckBox.setSelected(config.getEnableZooming());
        m_allowDragZoomingCheckBox.setSelected(config.getEnableDragZooming());
        m_allowPanningCheckBox.setSelected(config.getEnablePanning());
        m_showZoomResetCheckBox.setSelected(config.getShowZoomResetButton());
        m_enableSelectionCheckBox.setSelected(config.getEnableSelection());
        m_allowRectangleSelectionCheckBox.setSelected(config.getEnableRectangleSelection());
        m_allowLassoSelectionCheckBox.setSelected(config.getEnableLassoSelection());

        m_chartTitleTextField.setText(config.getChartTitle());
        m_chartSubtitleTextField.setText(config.getChartSubtitle());
        String xCol = config.getxColumn();
        m_xColComboBox.update(specs[0], xCol, true);
        m_yColFilter.loadConfiguration(config.getyColumnsConfig(), specs[0]);
        m_xAxisLabelField.setText(config.getxAxisLabel());
        m_yAxisLabelField.setText(config.getyAxisLabel());
        m_dotSize.setValue(config.getDotSize());
        m_maxRowsSpinner.setValue(config.getMaxRows());

        m_dateFormatChooser.replaceListItems(createPredefinedFormats(), config.getDateFormat());
        m_imageWidthSpinner.setValue(config.getImageWidth());
        m_imageHeightSpinner.setValue(config.getImageHeight());
        m_backgroundColorChooser.setColor(config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(config.getDataAreaColor());
        m_gridColorChooser.setColor(config.getGridColor());
        m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

        enableViewControls();
        enableCrosshairControls();
        enableSelectionControls();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        LinePlotViewConfig config = new LinePlotViewConfig();
        config.setHideInWizard(m_hideInWizardCheckBox.isSelected());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());

        config.setShowLegend(m_showLegendCheckBox.isSelected());
        config.setAutoRangeAxes(m_autoRangeAxisCheckBox.isSelected());
        config.setUseDomainInfo(m_useDomainInformationCheckBox.isSelected());
        config.setShowGrid(m_showGridCheckBox.isSelected());
        config.setShowCrosshair(m_showCrosshairCheckBox.isSelected());
        config.setSnapToPoints(m_snapToPointsCheckBox.isSelected());
        config.setResizeToWindow(m_resizeViewToWindow.isSelected());

        config.setSelectionColumnName(m_appendedColumnName.getText());
        config.setEnableViewConfiguration(m_enableViewConfigCheckBox.isSelected());
        config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        config.setEnableSubtitleChange(m_enableSubtitleChangeCheckBox.isSelected());
        config.setEnableXColumnChange(m_enableXColumnChangeCheckBox.isSelected());
        config.setEnableYColumnChange(m_enableYColumnChangeCheckBox.isSelected());
        config.setEnableXAxisLabelEdit(m_enableXAxisLabelEditCheckBox.isSelected());
        config.setEnableYAxisLabelEdit(m_enableYAxisLabelEditCheckBox.isSelected());
        config.setEnableDotSizeChange(m_enableDotSizeChangeCheckBox.isSelected());
        config.setEnableZooming(m_allowMouseWheelZoomingCheckBox.isSelected());
        config.setEnableDragZooming(m_allowDragZoomingCheckBox.isSelected());
        config.setEnablePanning(m_allowPanningCheckBox.isSelected());
        config.setShowZoomResetButton(m_showZoomResetCheckBox.isSelected());
        config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        config.setEnableRectangleSelection(m_allowRectangleSelectionCheckBox.isSelected());
        config.setEnableLassoSelection(m_allowLassoSelectionCheckBox.isSelected());

        config.setChartTitle(m_chartTitleTextField.getText());
        config.setChartSubtitle(m_chartSubtitleTextField.getText());
        config.setxColumn(m_xColComboBox.getSelectedColumn());
        m_yColFilter.saveConfiguration(config.getyColumnsConfig());
        config.setxAxisLabel(m_xAxisLabelField.getText());
        config.setyAxisLabel(m_yAxisLabelField.getText());
        config.setDotSize((Integer)m_dotSize.getValue());
        config.setMaxRows((Integer)m_maxRowsSpinner.getValue());

        config.setDateFormat(((SettingsModelString)m_dateFormatChooser.getModel()).getStringValue());
        config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        config.setBackgroundColor(m_backgroundColorChooser.getColor());
        config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        config.setGridColor(m_gridColorChooser.getColor());

        config.saveSettings(settings);
    }

    public static LinkedHashSet<String> createPredefinedFormats() {
        // unique values
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        formats.add("yyyy-mm-dd");
        formats.add("ddd mmm dd yyyy HH:MM:ss");
        formats.add("m/d/yy");
        formats.add("mmm d, yyyy");
        formats.add("mmmm d, yyyy");
        formats.add("dddd, mmmm d, yyyy");
        formats.add("h:MM TT");
        formats.add("h:MM:ss TT");
        formats.add("h:MM:ss TT Z");
        formats.add("HH:MM:ss");
        formats.add("yyyy-mm-dd'T'HH:MM:ss");
        // check also the StringHistory....
        String[] userFormats = StringHistory.getInstance(FORMAT_HISTORY_KEY)
            .getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }
        return formats;
    }

}
