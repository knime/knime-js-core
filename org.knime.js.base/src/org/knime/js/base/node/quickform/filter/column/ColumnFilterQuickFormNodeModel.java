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
 */
package org.knime.js.base.node.quickform.filter.column;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * Model for the column filter quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class ColumnFilterQuickFormNodeModel
        extends QuickFormNodeModel
        <ColumnFilterQuickFormRepresentation,
        ColumnFilterQuickFormValue,
        ColumnFilterQuickFormConfig>
        implements BufferedDataTableHolder {

    private DataTableSpec m_spec = new DataTableSpec();

    private BufferedDataTable m_inTable = null;

    /** Creates a new value selection node model. */
    public ColumnFilterQuickFormNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_filter_column";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterQuickFormValue createEmptyViewValue() {
        return new ColumnFilterQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        m_spec = (DataTableSpec) inSpecs[0];
        updateValuesFromSpec((DataTableSpec) inSpecs[0]);
        updateColumns((DataTableSpec) inSpecs[0]);
        createAndPushFlowVariable();
        return new DataTableSpec[]{createSpec((DataTableSpec) inSpecs[0])};
    }

    private void updateValuesFromSpec(final DataTableSpec spec) {
        getConfig().getDefaultValue().updateFromSpec(spec);
        if (getDialogValue() != null) {
            getDialogValue().updateFromSpec(spec);
        }
    }

    /**
     * @param inSpec The input spec
     * @return The output spec for the given input spec and the current settings
     * @throws InvalidSettingsException If the current settings can not be applied to the given input spec
     */
    private DataTableSpec createSpec(final DataTableSpec inSpec) throws InvalidSettingsException {
        final String[] values = getRelevantValue().getColumns();
        final List<DataColumnSpec> cspecs = new ArrayList<DataColumnSpec>();
        List<String> unknownCols = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            String column = values[i];
            if (column != null && inSpec.containsName(column)) {
                cspecs.add(inSpec.getColumnSpec(column));
            } else {
                unknownCols.add(column);
            }
        }
        if (!unknownCols.isEmpty()) {
            throw new InvalidSettingsException("Unknown columns " + unknownCols
                    + " selected.");
        }
        return new DataTableSpec(cspecs.toArray(new DataColumnSpec[cspecs.size()]));
    }

    /**
     * Update the possible columns in the config.
     *
     * @param spec The input spec
     */
    private void updateColumns(final DataTableSpec spec) {
        getConfig().setPossibleColumns(spec.getColumnNames());
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_inTable = (BufferedDataTable) inObjects[0];
        DataTableSpec inSpec = (DataTableSpec) inObjects[0].getSpec();
        updateColumns(inSpec);
        createAndPushFlowVariable();
        DataTableSpec outSpec = createSpec((DataTableSpec) inObjects[0].getSpec());
        ColumnRearranger rearranger = new ColumnRearranger(inSpec);
        rearranger.keepOnly(outSpec.getColumnNames());
        BufferedDataTable outTable = exec.createColumnRearrangeTable((BufferedDataTable)inObjects[0],
                rearranger, exec);
        return new BufferedDataTable[]{outTable};
    }

    /**
     * Pushes the current value as flow variable.
     */
    private void createAndPushFlowVariable() {
        final String[] values = getRelevantValue().getColumns();
        pushFlowVariableString(getConfig().getFlowVariableName(),
                StringUtils.join(values, ","));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setColumns(getViewValue().getColumns());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterQuickFormConfig createEmptyConfig() {
        return new ColumnFilterQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnFilterQuickFormRepresentation getRepresentation() {
        return new ColumnFilterQuickFormRepresentation(getRelevantValue(), getConfig(), getSpec());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        super.reset();
        m_inTable = null;
    }

    /**
     * @return The spec of the input table
     */
    private DataTableSpec getSpec() {
        return m_inTable != null ? m_inTable.getDataTableSpec() : m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return m_inTable != null ? new BufferedDataTable[]{m_inTable} : new BufferedDataTable[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        if (tables.length > 0) {
            m_inTable = tables[0];
        }
    }

}
