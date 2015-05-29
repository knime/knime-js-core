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
package org.knime.js.base.node.viz.plotter.roc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.NominalValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.util.ColumnFilterPanel;
import org.knime.core.node.util.ColumnSelectionComboxBox;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class ROCCurveNodeDialogPane extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final JCheckBox m_hideInWizardCheckBox;
    private final JCheckBox m_generateImageCheckBox;
    private final JCheckBox m_showArea;

    private final JCheckBox m_showGridCheckBox;
    private final JCheckBox m_resizeViewToWindow;

    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_lineWidthSpinner;
    private final JSpinner m_imageHeightSpinner;
    private final DialogComponentColorChooser m_gridColorChooser;
    private final DialogComponentColorChooser m_dataAreaColorChooser;
    private final DialogComponentColorChooser m_backgroundColorChooser;

    private DataTableSpec m_spec;

    @SuppressWarnings("unchecked")
    private final ColumnSelectionComboxBox m_classColumn =
            new ColumnSelectionComboxBox((Border)null, NominalValue.class);

    private final JComboBox<DataCell> m_positiveClass =
            new JComboBox<>(new DefaultComboBoxModel<DataCell>());

    private final JSpinner m_maxPoints = new JSpinner(new SpinnerNumberModel(2000, -1, Integer.MAX_VALUE, 10));

    @SuppressWarnings("unchecked")
    private final ColumnFilterPanel m_sortColumns =
            new ColumnFilterPanel(false, DoubleValue.class);

    private final JLabel m_warningLabel = new JLabel();


    /**
     * Creates a new dialog pane.
     */
    public ROCCurveNodeDialogPane() {
        m_hideInWizardCheckBox = new JCheckBox("Hide in wizard");
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_showArea = new JCheckBox("Show area under curve");
        m_showGridCheckBox = new JCheckBox("Show grid");
        m_resizeViewToWindow = new JCheckBox("Resize view to fill window");

        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_lineWidthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

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


        addTab("Options", initOptionsPanel());
        addTab("General Plot Options", initGeneralPanel());

        final JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.NORTHWEST;

        p.add(new JLabel("Class column   "), c);
        c.gridx++;
        p.add(m_classColumn, c);
        m_classColumn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                changeClassColumn(p);
            }
        });

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Positive class value   "), c);
        c.gridx++;
//        m_positiveClass.setMinimumSize(new Dimension(100, m_positiveClass
//                .getHeight()));
        p.add(m_positiveClass, c);

        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        p.add(m_warningLabel, c);
        c.anchor = GridBagConstraints.NORTHWEST;


        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Limit data points for each curve to   "), c);
        c.gridx++;
        p.add(m_maxPoints, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 3;
        p.add(
                new JLabel(
                        "Columns containing the positive class probabilities"),
                c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 3;
        p.add(m_sortColumns, c);

        addTab("Standard Settings", p);
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
        sizesPanel.add(m_showArea, cc);
        cc.gridy++;
        cc.anchor = GridBagConstraints.CENTER;
        sizesPanel.add(m_resizeViewToWindow, cc);
        c.gridy = 1;
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


    /**
     * Called if the user changed the class column.
     *
     * @param parent the panel which is the parent for message boxes
     */
    private void changeClassColumn(final JComponent parent) {
        String selCol = m_classColumn.getSelectedColumn();
        ((DefaultComboBoxModel<DataCell>)m_positiveClass.getModel()).removeAllElements();
        if ((selCol != null) && (m_spec != null)) {
            DataColumnSpec cs = m_spec.getColumnSpec(selCol);
            Set<DataCell> values = cs.getDomain().getValues();
            if (values == null) {
                m_warningLabel.setForeground(Color.RED);
                m_warningLabel.setText(" Column '" + selCol
                        + "' contains no possible values");
                return;
            }

            if (values.size() > 2) {
                m_warningLabel.setText(" Column '" + selCol
                        + "' contains more than two possible values");
            } else {
                m_warningLabel.setText("");
            }
            for (DataCell cell : values) {
                m_positiveClass.addItem(cell);
            }
            parent.revalidate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
            throws NotConfigurableException {
        ROCCurveViewConfig config = new ROCCurveViewConfig();
        config.loadSettingsForDialog(settings, specs[0]);
        m_hideInWizardCheckBox.setSelected(config.getHideInWizard());
        m_generateImageCheckBox.setSelected(config.getGenerateImage());

        m_showArea.setSelected(config.getShowArea());
        m_showGridCheckBox.setSelected(config.getShowGrid());
        m_resizeViewToWindow.setSelected(config.getResizeToWindow());

        m_lineWidthSpinner.setValue(config.getLineWidth());
        m_imageWidthSpinner.setValue(config.getImageWidth());
        m_imageHeightSpinner.setValue(config.getImageHeight());
        m_backgroundColorChooser.setColor(config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(config.getDataAreaColor());
        m_gridColorChooser.setColor(config.getGridColor());
        m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

        m_spec = specs[0];
        m_classColumn.update(specs[0], config.getRocSettings().getClassColumn());
        m_positiveClass.setSelectedItem(config.getRocSettings().getPositiveClass());
        m_sortColumns.update(specs[0], false, config.getRocSettings().getCurves());

        m_maxPoints.setValue(config.getRocSettings().getMaxPoints());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ROCCurveViewConfig config = new ROCCurveViewConfig();
        config.setHideInWizard(m_hideInWizardCheckBox.isSelected());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());

        config.setShowArea(m_showArea.isSelected());
        config.setShowGrid(m_showGridCheckBox.isSelected());
        config.setResizeToWindow(m_resizeViewToWindow.isSelected());

        config.setLineWidth((Integer)m_lineWidthSpinner.getValue());
        config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        config.setBackgroundColor(m_backgroundColorChooser.getColor());
        config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        config.setGridColor(m_gridColorChooser.getColor());

        config.getRocSettings().setClassColumn(m_classColumn.getSelectedColumn());
        config.getRocSettings()
                .setPositiveClass((DataCell)m_positiveClass.getSelectedItem());
        config.getRocSettings().getCurves().clear();
        config.getRocSettings().getCurves().addAll(m_sortColumns.getIncludedColumnSet());
        config.getRocSettings().setMaxPoints((Integer) m_maxPoints.getValue());

        config.saveSettings(settings);
    }
}
