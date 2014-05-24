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
package org.knime.js.base.node.quickform.selection.column;

import java.util.Arrays;

import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ColumnSelectionQuickFormNodeModel extends QuickFormNodeModel<ColumnSelectionQuickFormRepresentation, 
        ColumnSelectionQuickFormValue> {
    
    /** Creates a new value selection node model. */
    public ColumnSelectionQuickFormNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{FlowVariablePortObject.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_selection_column";
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
    protected void reset() {
        // not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnSelectionQuickFormRepresentation createEmptyViewRepresentation() {
        return new ColumnSelectionQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnSelectionQuickFormValue createEmptyViewValue() {
        return new ColumnSelectionQuickFormValue();
    }
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final ColumnSelectionQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        updateColumns((DataTableSpec)inSpecs[0]);
        createAndPushFlowVariable();
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        updateColumns(((DataTable)inObjects[0]).getDataTableSpec());
        createAndPushFlowVariable();
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
    }
    
    private void updateColumns(final DataTableSpec spec) {
        getViewRepresentation().setPossibleColumns(spec.getColumnNames());
    }
    
    private void createAndPushFlowVariable() throws InvalidSettingsException {
        if (!Arrays.asList(getDialogRepresentation().getPossibleColumns()).contains(getViewValue().getColumn())) {
            throw new InvalidSettingsException("The selected column '"
                    + getViewValue().getColumn() + "' is not available");
        }
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getViewValue().getColumn());
    }

}
