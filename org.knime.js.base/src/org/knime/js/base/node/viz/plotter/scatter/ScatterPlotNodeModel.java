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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONDataTable;
import org.knime.core.node.web.JSONDataTable.JSONDataTableRow;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;
import org.knime.js.core.datasets.JSONKeyedValues3DDataset;
import org.knime.js.core.datasets.JSONKeyedValues3DDataset.KeyedValues3DSeries;
import org.knime.js.core.datasets.JSONKeyedValuesRow;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class ScatterPlotNodeModel extends NodeModel implements
    WizardNode<ScatterPlotViewRepresentation, ScatterPlotViewValue> {

    private final static NodeLogger LOGGER = NodeLogger.getLogger(ScatterPlotNodeModel.class);

    private final Object m_lock = new Object();
    private final ScatterPlotViewConfig m_config;
    private ScatterPlotViewRepresentation m_representation;
    private ScatterPlotViewValue m_viewValue;

    /**
     * @param config
     */
    protected ScatterPlotNodeModel(final ScatterPlotViewConfig config) {
        super(1, 0);
        m_config = config;
        m_representation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        // do nothing?
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
            throws Exception {
        synchronized (m_lock) {
            ColumnRearranger c = createNumericColumnRearranger(inData[0].getDataTableSpec());
            BufferedDataTable filteredTable = exec.createColumnRearrangeTable(inData[0], c, exec.createSubProgress(0.5));
            //construct dataset
            if (m_config.getMaxRows() < filteredTable.getRowCount()) {
                setWarningMessage("Only the first " + m_config.getMaxRows() + " rows are displayed.");
            }
            JSONDataTable table = new JSONDataTable(filteredTable, 1, m_config.getMaxRows(), exec.createSubProgress(0.5));
            int numColumns = table.getSpec().getNumColumns();
            String[] rowKeys = new String[table.getSpec().getNumRows()];
            JSONKeyedValuesRow[] rowValues = new JSONKeyedValuesRow[table.getSpec().getNumRows()];
            JSONDataTableRow[] tableRows = table.getRows();
            for (int rowID = 0; rowID < rowValues.length; rowID++) {
                JSONDataTableRow currentRow = tableRows[rowID];
                rowKeys[rowID] = currentRow.getRowKey();
                double[] rowData = new double[numColumns];
                Object[] tableData = currentRow.getData();
                for (int colID = 0; colID < numColumns; colID++) {
                    if (tableData[colID] instanceof Double) {
                        rowData[colID] = (double)tableData[colID];
                    }
                }
                rowValues[rowID] = new JSONKeyedValuesRow(currentRow.getRowKey(), rowData);
                rowValues[rowID].setColor(table.getSpec().getRowColorValues()[rowID]);
            }
            KeyedValues3DSeries series = new KeyedValues3DSeries("series", rowValues);
            JSONKeyedValues3DDataset dataset = new JSONKeyedValues3DDataset(table.getSpec().getColNames(),
                rowKeys, new KeyedValues3DSeries[]{series});

            m_representation.setKeyedDataset(dataset);
            copyConfigToView();
        }
        return null;
    }

    private ColumnRearranger createNumericColumnRearranger(final DataTableSpec in) {
        ColumnRearranger c = new ColumnRearranger(in);
        for (DataColumnSpec colSpec : in) {
            if (!colSpec.getType().isCompatible(DoubleValue.class)) {
                c.remove(colSpec.getName());
            }
        }
        return c;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final ScatterPlotViewValue viewContent) {
        synchronized (m_lock) {
            // validate value
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final ScatterPlotViewValue viewValue) {
        synchronized (m_lock) {
            m_viewValue = viewValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScatterPlotViewRepresentation getViewRepresentation() {
        synchronized (m_lock) {
            return m_representation;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScatterPlotViewValue getViewValue() {
        synchronized (m_lock) {
            return m_viewValue;
        }
    }

    private void copyConfigToView() {
        m_representation.setAllowViewConfiguration(m_config.getAllowViewConfiguration());
        m_representation.setEnableXColumnChange(m_config.getEnableXColumnChange());
        m_representation.setEnableYColumnChange(m_config.getEnableYColumnChange());
        m_representation.setEnableXAxisLabelEdit(m_config.getEnableXAxisLabelEdit());
        m_representation.setEnableYAxisLabelEdit(m_config.getEnableYAxisLabelEdit());
        m_representation.setAllowDotSizeChange(m_config.getAllowDotSizeChange());
        m_representation.setAllowZooming(m_config.getAllowZooming());
        m_representation.setAllowPanning(m_config.getAllowPanning());

        m_viewValue.setxColumn(m_config.getxColumn());
        m_viewValue.setyColumn(m_config.getyColumn());
        m_viewValue.setxAxisLabel(m_config.getxAxisLabel());
        m_viewValue.setyAxisLabel(m_config.getyAxisLabel());
        m_viewValue.setxAxisMin(m_config.getxAxisMin());
        m_viewValue.setxAxisMax(m_config.getxAxisMax());
        m_viewValue.setyAxisMin(m_config.getyAxisMin());
        m_viewValue.setyAxisMax(m_config.getyAxisMax());
        m_viewValue.setDotSize(m_config.getDotSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScatterPlotViewRepresentation createEmptyViewRepresentation() {
        return new ScatterPlotViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScatterPlotViewValue createEmptyViewValue() {
        return new ScatterPlotViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_viz_plotter_scatter";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        File repFile = new File(nodeInternDir, "representation.xml");
        File valFile = new File(nodeInternDir, "value.xml");
        NodeSettingsRO repSettings = NodeSettings.loadFromXML(new FileInputStream(repFile));
        NodeSettingsRO valSettings = NodeSettings.loadFromXML(new FileInputStream(valFile));
        m_representation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
        try {
            m_representation.loadFromNodeSettings(repSettings);
            m_viewValue.loadFromNodeSettings(valSettings);
        } catch (InvalidSettingsException e) {
            // what to do?
            LOGGER.error("Error loading internals: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        NodeSettings repSettings = new NodeSettings("scatterPlotViewRepresentation");
        NodeSettings valSettings = new NodeSettings("scatterPlotViewValue");
        if (m_representation != null) {
            m_representation.saveToNodeSettings(repSettings);
        }
        if (m_viewValue != null) {
            m_viewValue.saveToNodeSettings(valSettings);
        }
        File repFile = new File(nodeInternDir, "representation.xml");
        File valFile = new File(nodeInternDir, "value.xml");
        repSettings.saveToXML(new FileOutputStream(repFile));
        valSettings.saveToXML(new FileOutputStream(valFile));
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
        // TODO Auto-generated method stub

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
    protected void reset() {
        synchronized (m_lock) {
            m_representation = createEmptyViewRepresentation();
            m_viewValue = createEmptyViewValue();
        }
    }

}
