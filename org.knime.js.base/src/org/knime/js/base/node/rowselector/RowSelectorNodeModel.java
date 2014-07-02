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
 *
 * History
 *   Feb 3, 2014 ("Patrick Winter"): created
 */
package org.knime.js.base.node.rowselector;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;
import org.knime.js.core.JSONDataTable;

/**
 *
 * @author "Patrick Winter", University of Konstanz
 */
public class RowSelectorNodeModel extends NodeModel implements WizardNode<RowSelectorRepresentation, RowSelectorValue> {

    private JSONDataTable m_input;

    private final RowSelectorRepresentation m_representation = createEmptyViewRepresentation();

    private RowSelectorValue m_value = createEmptyViewValue();

    /**
     *
     */
    public RowSelectorNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
        throws Exception {
        m_input = new JSONDataTable(inData[0], 1, inData[0].getRowCount(), exec);
        m_representation.setTable(m_input);
        BufferedDataContainer container = exec.createDataContainer(inData[0].getDataTableSpec());
        Set<Long> selected = new HashSet<Long>(java.util.Arrays.asList(ArrayUtils.toObject(m_value.getSelections())));
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
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        return inSpecs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "knime_row_selector";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final RowSelectorValue viewContent, final boolean useAsDefault) {
        m_value = viewContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RowSelectorRepresentation getViewRepresentation() {
        return m_representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RowSelectorValue getViewValue() {
        return m_value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RowSelectorRepresentation createEmptyViewRepresentation() {
        return new RowSelectorRepresentation(m_input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RowSelectorValue createEmptyViewValue() {
        return new RowSelectorValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final RowSelectorValue viewContent) {
        // TODO Auto-generated method stub
        return null;
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
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_representation.saveToNodeSettings(settings);
        m_value.saveToNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        createEmptyViewRepresentation().loadFromNodeSettings(settings);
        createEmptyViewValue().loadFromNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_representation.loadFromNodeSettings(settings);
        m_value.loadFromNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // TODO Auto-generated method stub

    }

}
