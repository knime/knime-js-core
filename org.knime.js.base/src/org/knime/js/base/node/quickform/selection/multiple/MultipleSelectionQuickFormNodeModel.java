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
package org.knime.js.base.node.quickform.selection.multiple;

import org.apache.commons.lang.StringUtils;
import org.knime.base.node.io.filereader.DataCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * The model for the muliple selections quick form node.
 *
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class MultipleSelectionQuickFormNodeModel
        extends QuickFormNodeModel
        <MultipleSelectionQuickFormRepresentation,
        MultipleSelectionQuickFormValue,
        MultipleSelectionQuickFormConfig> {

    /**
     * Creates the node model.
     * @param viewName the view name
     */
    public MultipleSelectionQuickFormNodeModel(final String viewName) {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        pushFlowVariableString(getConfig().getFlowVariableName(),
                StringUtils.join(getRelevantValue().getVariableValue(), ","));
        return new PortObjectSpec[]{createSpec()};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        pushFlowVariableString(getConfig().getFlowVariableName(),
                StringUtils.join(getRelevantValue().getVariableValue(), ","));
        final DataTableSpec outSpec = createSpec();
        BufferedDataContainer container = exec.createDataContainer(outSpec, false);
        String[] values = getRelevantValue().getVariableValue();
        DataCellFactory cellFactory = new DataCellFactory();
        DataType type = outSpec.getColumnSpec(0).getType();
        for (int i = 0; i < values.length; i++) {
            DataCell result = cellFactory.createDataCellOfType(type, values[i]);
            container.addRowToTable(new DefaultRow(RowKey.createRowKey(i), result));
        }
        container.close();
        return new PortObject[]{container.getTable()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_selection_multiple";
    }

    private DataTableSpec createSpec() throws InvalidSettingsException {
        String strColumnName = getConfig().getFlowVariableName();
        if (strColumnName != null) {
            DataColumnSpecCreator creator = new DataColumnSpecCreator(strColumnName, StringCell.TYPE);
            return new DataTableSpec(creator.createSpec());
        } else {
            throw new InvalidSettingsException("Invalid column name specified for user selections.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultipleSelectionQuickFormValue createEmptyViewValue() {
        return new MultipleSelectionQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setVariableValue(getViewValue().getVariableValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultipleSelectionQuickFormConfig createEmptyConfig() {
        return new MultipleSelectionQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MultipleSelectionQuickFormRepresentation getRepresentation() {
        return new MultipleSelectionQuickFormRepresentation(getRelevantValue(), getConfig());
    }

}
