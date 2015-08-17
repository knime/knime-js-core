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

import org.knime.base.data.xml.SvgCell;
import org.knime.base.node.viz.liftchart.LiftCalculator;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.NominalValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
final class LiftChartNodeModel extends AbstractSVGWizardNodeModel<LiftChartViewRepresentation, LiftChartPlotViewValue> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LiftChartNodeModel.class);

    private final LiftChartViewConfig m_config;

    private BufferedDataTable m_table;

    /**
     * Creates a new model instance.
     */
    LiftChartNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{
            ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new LiftChartViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {

        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];

        if (m_config.getResponseColumn() == null
                || m_config.getResponseColumn().trim().length() == 0
                || !tableSpec.containsName(m_config.getResponseColumn())
                || tableSpec.getColumnSpec(
                    m_config.getResponseColumn()) == null) {
            for (int i = 0; i < tableSpec.getNumColumns(); i++) {
                DataColumnSpec cs = tableSpec.getColumnSpec(i);
                if (cs.getType().isCompatible(NominalValue.class)
                        && cs.getDomain().hasValues()) {
                    m_config.setResponseColumn(cs.getName());
                    setWarningMessage("Auto-selected column " + cs.getName()
                            + " as response column.");
                    break;
                }
            }
        }
        if (tableSpec.getColumnSpec(m_config.getResponseColumn()) == null
                || !tableSpec.getColumnSpec(m_config.getResponseColumn())
                .getType().isCompatible(NominalValue.class)) {
            // auto-configure makes no sense here, since guessing the true label
            // is maybe a bit too much
            throw new InvalidSettingsException(
                    "Selected response column contains no string values or"
                            + " domain is not set."
                            + " You might have to use a domain calculator.");
        }
        if (m_config.getProbabilityColumn() == null
                || m_config.getProbabilityColumn().trim().length() == 0
                || !tableSpec.containsName(
                    m_config.getProbabilityColumn())
                || tableSpec.getColumnSpec(
                    m_config.getProbabilityColumn()) == null) {
            for (int i = 0; i < tableSpec.getNumColumns(); i++) {
                if (tableSpec.getColumnSpec(i).getType().isCompatible(
                        DoubleValue.class)) {
                    m_config.setProbabilityColumn(tableSpec
                            .getColumnSpec(i).getName());
                    setWarningMessage("Auto-selected column "
                            + tableSpec.getColumnSpec(i).getName()
                            + " as probability column.");
                    break;
                }
            }
        }
        if (!tableSpec.getColumnSpec(m_config.getProbabilityColumn())
                .getType().isCompatible(DoubleValue.class)) {
            throw new InvalidSettingsException(
                    "Selected probability column contains no double values.");
        }

        if (m_config.getResponseLabel() == null) {
            throw new InvalidSettingsException("Response label must be set.");
        }

        DataTableSpec out = tableSpec;

        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        return new PortObjectSpec[]{imageSpec, out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiftChartViewRepresentation createEmptyViewRepresentation() {
        return new LiftChartViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiftChartPlotViewValue createEmptyViewValue() {
        return new LiftChartPlotViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.plotter.lift";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final LiftChartPlotViewValue viewContent) {
        synchronized (getLock()) {
            // validate value, nothing to do atm
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inData, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            BufferedDataTable table = (BufferedDataTable)inData[0];
            LiftChartViewRepresentation representation = getViewRepresentation();

            // don't use staggered rendering and resizing for image creation
            representation.setEnableStaggeredRendering(false);
            representation.setResizeToWindow(false);

            if (representation.getLiftValues() == null) {
                copyConfigToView();
                LiftCalculator calc = new LiftCalculator(m_config.getResponseColumn(), m_config.getProbabilityColumn(),
                                                     m_config.getResponseLabel(), m_config.getIntervalWidth());
                calc.calculateLiftTables(table, exec);
                m_table = calc.getSortedInput();
                representation.setBaseline(1.0);
                double[] lift = new double[calc.getLiftTable().getRowCount()];
                double[] cumLift = new double[calc.getLiftTable().getRowCount()];
                double[] response = new double[calc.getResponseTable().getRowCount()];

                int counter = 0;
                for (DataRow row : calc.getLiftTable()) {
                    lift[counter] = ((DoubleValue)row.getCell(0)).getDoubleValue();
                    cumLift[counter] = ((DoubleValue)row.getCell(2)).getDoubleValue();
                    counter++;
                }
                counter = 0;
                for (DataRow row : calc.getResponseTable()) {
                    response[counter] = ((DoubleValue)row.getCell(0)).getDoubleValue();
                    counter++;
                }
                representation.setResponse(response);
                representation.setCumulativeLift(cumLift);
                representation.setLiftValues(lift);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView,
        final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out = m_table;
        synchronized (getLock()) {
            LiftChartViewRepresentation representation = getViewRepresentation();
            // enable staggered rendering and resizing for interactive view
            representation.setEnableStaggeredRendering(true);
            representation.setResizeToWindow(m_config.getResizeToWindow());
        }
        exec.setProgress(1);
        return new PortObject[]{svgImageFromView, out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new LiftChartViewConfig().loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            copyValueToConfig();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    private void copyConfigToView() {
        LiftChartViewRepresentation representation = getViewRepresentation();
        representation.setShowGrid(m_config.getShowGrid());
        representation.setImageWidth(m_config.getImageWidth());
        representation.setImageHeight(m_config.getImageHeight());
        representation.setBackgroundColor(m_config.getBackgroundColorString());
        representation.setDataAreaColor(m_config.getDataAreaColorString());
        representation.setGridColor(m_config.getGridColorString());
        representation.setIntervalWidth(m_config.getIntervalWidth());
        representation.setLineWidth(m_config.getLineWidth());
        representation.setShowLegend(m_config.getShowLegend());
        representation.setEnableControls(m_config.getEnableControls());
        representation.setEnableEditSubtitle(m_config.getEnableEditSubtitle());
        representation.setEnableEditTitle(m_config.getEnableEditTitle());
        representation.setEnableEditXAxisLabel(m_config.getEnableEditXAxisLabel());
        representation.setEnableEditYAxisLabel(m_config.getEnableEditYAxisLabel());
        representation.setEnableViewToggle(m_config.getEnableViewToggle());
        representation.setEnableSmoothingEdit(m_config.getEnableSmoothing());
        LiftChartPlotViewValue value = getViewValue();
        value.setTitleLift(m_config.getTitleLift());
        value.setSubtitleLift(m_config.getSubtitleLift());
        value.setxAxisTitleLift(m_config.getxAxisTitleLift());
        value.setyAxisTitleLift(m_config.getyAxisTitleLift());

        value.setTitleGain(m_config.getTitleGain());
        value.setSubtitleGain(m_config.getSubtitleGain());
        value.setxAxisTitleGain(m_config.getxAxisTitleGain());
        value.setyAxisTitleGain(m_config.getyAxisTitleGain());
        value.setShowGainChart(m_config.getShowGainChart());
        value.setSmoothing(m_config.getSmoothing());
    }

    /**
    *
    */
   private void copyValueToConfig() {
       LiftChartPlotViewValue val = getViewValue();
       m_config.setTitleLift(val.getTitleLift());
       m_config.setSubtitleLift(val.getSubtitleLift());
       m_config.setxAxisTitleLift(val.getxAxisTitleLift());
       m_config.setyAxisTitleLift(val.getyAxisTitleLift());

       m_config.setTitleLift(val.getTitleGain());
       m_config.setSubtitleLift(val.getSubtitleGain());
       m_config.setxAxisTitleLift(val.getxAxisTitleGain());
       m_config.setyAxisTitleLift(val.getyAxisTitleGain());

       m_config.setShowGainChart(val.getShowGainChart());
       m_config.setSmoothing(val.getSmoothing());
   }
}
