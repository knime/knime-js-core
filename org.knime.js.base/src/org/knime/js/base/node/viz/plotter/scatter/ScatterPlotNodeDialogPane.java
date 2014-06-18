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
package org.knime.js.base.node.viz.plotter.scatter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
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
import org.knime.core.node.util.ColumnSelectionPanel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class ScatterPlotNodeDialogPane extends NodeDialogPane {

    private final static int TEXT_FIELD_SIZE = 20;

    private final ScatterPlotViewConfig m_config;

    private final JCheckBox m_enableViewConfigCheckBox;
    private final JCheckBox m_enableTitleChangeCheckBox;
    private final JCheckBox m_enableSubtitleChangeCheckBox;
    private final JCheckBox m_enableXColumnChangeCheckBox;
    private final JCheckBox m_enableYColumnChangeCheckBox;
    private final JCheckBox m_enableXAxisLabelEditCheckBox;
    private final JCheckBox m_enableYAxisLabelEditCheckBox;
    private final JCheckBox m_allowZoomingCheckBox;
    private final JCheckBox m_allowPanningCheckBox;
    private final JCheckBox m_enableDotSizeChangeCheckBox;

    private final JSpinner m_maxRowsSpinner;
    private final JTextField m_chartTitleTextField;
    private final JTextField m_chartSubtitleTextField;
    private final ColumnSelectionPanel m_xColComboBox;
    private final ColumnSelectionPanel m_yColComboBox;
    private final JTextField m_xAxisLabelField;
    private final JTextField m_yAxisLabelField;
    private final JSpinner m_dotSize;

    /**
     * @param config The config to read/write from.
     *
     */
    public ScatterPlotNodeDialogPane(final ScatterPlotViewConfig config) {
        m_config = config;
        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableXColumnChangeCheckBox = new JCheckBox("Enable column chooser for x-axis");
        m_enableYColumnChangeCheckBox = new JCheckBox("Enable column chooser for y-axis");
        m_enableXAxisLabelEditCheckBox = new JCheckBox("Enable label edit for x-axis");
        m_enableYAxisLabelEditCheckBox = new JCheckBox("Enable label edit for y-axis");
        m_enableDotSizeChangeCheckBox = new JCheckBox("Enable dot size edit");
        m_allowZoomingCheckBox = new JCheckBox("Enable zooming");
        m_allowPanningCheckBox = new JCheckBox("Enable panning");

        m_maxRowsSpinner = new JSpinner();
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        // Change to include string columns when JS library supports it
        m_xColComboBox = new ColumnSelectionPanel("Choose column for x axis", DoubleValue.class, StringValue.class);
        m_yColComboBox = new ColumnSelectionPanel("Choose column for y axis", DoubleValue.class, StringValue.class);
        m_xAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_yAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_dotSize = new JSpinner();

        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
               enableViewControls();
            }
        });
        addTab("Options", initDialog());
    }

    /**
     * @return
     */
    private Component initDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Maximum number of rows: "), c);
        c.gridx += 1;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        panel.add(m_maxRowsSpinner, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        panel.add(m_enableViewConfigCheckBox, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        panel.add(new JLabel("Chart Title: "), c);
        c.gridx++;
        panel.add(m_chartTitleTextField, c);
        c.gridx++;
        panel.add(new JLabel("Chart Subtitle: "), c);
        c.gridx++;
        panel.add(m_chartSubtitleTextField, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        panel.add(m_enableTitleChangeCheckBox, c);
        c.gridx += 2;
        panel.add(m_enableSubtitleChangeCheckBox, c);
        c.gridx = 0;
        c.gridy++;
        m_xColComboBox.setPreferredSize(new Dimension(260, 50));
        panel.add(m_xColComboBox, c);
        c.gridx += 2;
        m_yColComboBox.setPreferredSize(new Dimension(260, 50));
        panel.add(m_yColComboBox, c);
        c.gridx = 0;
        c.gridy++;
        panel.add(m_enableXColumnChangeCheckBox, c);
        c.gridx += 2;
        panel.add(m_enableYColumnChangeCheckBox, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        panel.add(new JLabel("Label for x axis: "), c);
        c.gridx++;
        panel.add(m_xAxisLabelField, c);
        c.gridx++;
        panel.add(new JLabel("Label for y axis: "), c);
        c.gridx++;
        panel.add(m_yAxisLabelField, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        panel.add(m_enableXAxisLabelEditCheckBox, c);
        c.gridx += 2;
        panel.add(m_enableYAxisLabelEditCheckBox, c);
        c.gridwidth = 1;
        /*c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Dot size: "), c);
        c.gridx++;
        m_dotSize.setPreferredSize(new Dimension(100, 20));
        panel.add(m_dotSize, c);
        c.gridx++;
        c.gridwidth = 2;
        panel.add(m_enableDotSizeChangeCheckBox, c);*/
        c.gridx = 0;
        c.gridy++;
        panel.add(m_allowZoomingCheckBox, c);
        c.gridx += 2;
        panel.add(m_allowPanningCheckBox, c);

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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsForDialog(settings);
        m_enableViewConfigCheckBox.setSelected(m_config.getEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(m_config.getEnableTitleChange());
        m_enableSubtitleChangeCheckBox.setSelected(m_config.getEnableSubtitleChange());
        m_enableXColumnChangeCheckBox.setSelected(m_config.getEnableXColumnChange());
        m_enableYColumnChangeCheckBox.setSelected(m_config.getEnableYColumnChange());
        m_enableXAxisLabelEditCheckBox.setSelected(m_config.getEnableXAxisLabelEdit());
        m_enableYAxisLabelEditCheckBox.setSelected(m_config.getEnableYAxisLabelEdit());
        m_enableDotSizeChangeCheckBox.setSelected(m_config.getEnableDotSizeChange());
        m_allowZoomingCheckBox.setSelected(m_config.getEnableZooming());
        m_allowPanningCheckBox.setSelected(m_config.getEnablePanning());

        // disable zooming/panning controls TODO remove when zooming/panning does not freeze UI
        m_allowZoomingCheckBox.setSelected(false);
        m_allowPanningCheckBox.setSelected(false);
        m_allowZoomingCheckBox.setEnabled(false);
        m_allowPanningCheckBox.setEnabled(false);

        m_chartTitleTextField.setText(m_config.getChartTitle());
        m_chartSubtitleTextField.setText(m_config.getChartSubtitle());
        String xCol = m_config.getxColumn();
        if (xCol == null || xCol.isEmpty()) {
            xCol = specs[0].getColumnNames()[0];
        }

        String yCol = m_config.getyColumn();
        if (yCol == null || yCol.isEmpty()) {
            yCol = specs[0].getColumnNames()[specs[0].getNumColumns() > 1 ? 1 : 0];
        }

        m_xColComboBox.update(specs[0], xCol);
        m_yColComboBox.update(specs[0], yCol);
        m_xAxisLabelField.setText(m_config.getxAxisLabel());
        m_yAxisLabelField.setText(m_config.getyAxisLabel());
        m_dotSize.setValue(m_config.getDotSize());
        m_maxRowsSpinner.setValue(m_config.getMaxRows());
        enableViewControls();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.setEnableViewConfiguration(m_enableViewConfigCheckBox.isSelected());
        m_config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        m_config.setEnableSubtitleChange(m_enableSubtitleChangeCheckBox.isSelected());
        m_config.setEnableXColumnChange(m_enableXColumnChangeCheckBox.isSelected());
        m_config.setEnableYColumnChange(m_enableYColumnChangeCheckBox.isSelected());
        m_config.setEnableXAxisLabelEdit(m_enableXAxisLabelEditCheckBox.isSelected());
        m_config.setEnableYAxisLabelEdit(m_enableYAxisLabelEditCheckBox.isSelected());
        m_config.setEnableDotSizeChange(m_enableDotSizeChangeCheckBox.isSelected());
        m_config.setEnableZooming(m_allowZoomingCheckBox.isSelected());
        m_config.setEnablePanning(m_allowPanningCheckBox.isSelected());

        m_config.setChartTitle(m_chartTitleTextField.getText());
        m_config.setChartSubtitle(m_chartSubtitleTextField.getText());
        m_config.setxColumn(m_xColComboBox.getSelectedColumn());
        m_config.setyColumn(m_yColComboBox.getSelectedColumn());
        m_config.setxAxisLabel(m_xAxisLabelField.getText());
        m_config.setyAxisLabel(m_yAxisLabelField.getText());
        m_config.setDotSize((Integer)m_dotSize.getValue());
        m_config.setMaxRows((Integer)m_maxRowsSpinner.getValue());
        m_config.saveSettings(settings);
    }

}
