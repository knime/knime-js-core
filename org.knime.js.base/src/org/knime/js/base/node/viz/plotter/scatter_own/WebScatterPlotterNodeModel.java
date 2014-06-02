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
 * ---------------------------------------------------------------------
 *
 * Created on 08.08.2013 by Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
package org.knime.js.base.node.viz.plotter.scatter_own;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.knime.base.data.filter.column.FilterColumnTable;
import org.knime.base.node.viz.plotter.node.DefaultVisualizationNodeModel;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONDataTable;
import org.knime.core.node.web.JSONDataTableSpec.JSTypes;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class WebScatterPlotterNodeModel extends DefaultVisualizationNodeModel implements
    WizardNode<WebScatterPlotterViewRepresentation, WebScatterPlotterViewValue> {

    private JSONDataTable m_input;
    private WebScatterPlotterViewValue m_viewValue = createEmptyViewValue();

    /**
     *
     */
    public WebScatterPlotterNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        super.configure(inSpecs);
        return inSpecs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
        throws Exception {
        // generate list of excluded columns, suppressing warning
        findCompatibleColumns(inData[0].getDataTableSpec(), false);
        DataTable filter = new FilterColumnTable(inData[0], false, getExcludedColumns());
        int maxRows = getEndIndex();
        m_input = new JSONDataTable(filter, 1, maxRows, exec);
        int numRows = m_input.getSpec().getNumRows();
        Object[][] extensions = new Object[numRows][1];
        for (int i = 0; i < numRows; i++) {
            extensions[i][0] = false;
        }
        m_input.setExtensions(extensions);
        m_input.getSpec().addExtension("hilite", JSTypes.BOOLEAN);
        if (maxRows < inData[0].getRowCount()) {
            setWarningMessage("Only the first " + maxRows + " rows are displayed.");
        }
        BufferedDataContainer container = exec.createDataContainer(inData[0].getDataTableSpec());
        Set<Long> selected = new HashSet<Long>(java.util.Arrays.asList(ArrayUtils.toObject(m_viewValue.getSelections())));
        long i = 0;
        for (DataRow row : inData[0]) {
            if (selected.contains(i++)) {
                container.addRowToTable(row);
            }
        }
        container.close();
        return new BufferedDataTable[]{container.getTable()};
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
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
     // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        /*File f = new File(nodeInternDir, FILE_NAME);
        ContainerTable table = DataContainer.readFromZip(f);
        m_input = new JSONDataTable(table, 1, getEndIndex(), exec);*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "knime_scatter_plotter_own";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final WebScatterPlotterViewValue viewValue, final boolean useAsDefault) {
        m_viewValue = viewValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebScatterPlotterViewRepresentation getViewRepresentation() {
        return new WebScatterPlotterViewRepresentation(m_input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebScatterPlotterViewValue getViewValue() {
        return m_viewValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebScatterPlotterViewRepresentation createEmptyViewRepresentation() {
        try {
            return WebScatterPlotterViewRepresentation.class.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebScatterPlotterViewValue createEmptyViewValue() {
        return new WebScatterPlotterViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final WebScatterPlotterViewValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
