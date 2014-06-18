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
package org.knime.js.base.node.quickform.filter.value;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 *
 */
public class ValueFilterQuickFormNodeModel
        extends
        QuickFormNodeModel<ValueFilterQuickFormRepresentation, ValueFilterQuickFormValue, ValueFilterQuickFormConfig> {

    /** Creates a new value selection node model. */
    public ValueFilterQuickFormNodeModel(final ValueFilterQuickFormConfig config) {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{BufferedDataTable.TYPE}, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_filter_value";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterQuickFormRepresentation createEmptyViewRepresentation() {
        return new ValueFilterQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterQuickFormValue createEmptyViewValue() {
        return new ValueFilterQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        getConfig().setFromSpec(((DataTableSpec)inSpecs[0]));
        createAndPushFlowVariable();
        copyConfigToDialog();
        return new DataTableSpec[]{(DataTableSpec)inSpecs[0]};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        getConfig().setFromSpec(((DataTable)inObjects[0]).getDataTableSpec());
        createAndPushFlowVariable();
        BufferedDataTable inTable = (BufferedDataTable)inObjects[0];
        BufferedDataContainer container =
                exec.createDataContainer(inTable.getDataTableSpec(), false);
        String column = isReexecute() ? getViewValue().getColumn() : getConfig().getColumn();
        List<String> values = Arrays.asList(isReexecute() ? getViewValue().getValues() : getConfig().getValues());
        int colIndex;
        for (colIndex = 0; colIndex < inTable.getDataTableSpec().getNumColumns(); colIndex++) {
            if (inTable.getDataTableSpec().getColumnSpec(colIndex).getName()
                    .equals(column)) {
                break;
            }
        }
        inTable.getDataTableSpec().getColumnSpec(column);
        for (DataRow row : inTable) {
            if (values.contains(row.getCell(colIndex).toString())) {
                container.addRowToTable(row);
            }
        }
        container.close();
        copyConfigToView();
        setExecuted();
        return new PortObject[]{container.getTable()};
    }

    private void createAndPushFlowVariable() throws InvalidSettingsException {
        checkSelectedValues();
        String[] values = isReexecute() ? getViewValue().getValues() : getConfig().getValues();
        pushFlowVariableString(getConfig().getFlowVariableName(),
                StringUtils.join(values, ","));
    }

    private void checkSelectedValues() throws InvalidSettingsException {
        String column = isReexecute() ? getViewValue().getColumn() : getConfig().getColumn();
        List<String> possibleValues = getConfig().getPossibleValues().get(column);
        String[] selectedValues = isReexecute() ? getViewValue().getValues() : getConfig().getValues();
        for (String value : selectedValues) {
            if (!possibleValues.contains(value)) {
                throw new InvalidSettingsException("The selected value '"
                        + value
                        + "' is not among the possible values in the column '"
                        + column + "'");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyConfigToView() {
        super.copyConfigToView();
        getViewRepresentation().setDefaultColumn(getConfig().getDefaultColumn());
        getViewRepresentation().setDefaultValues(getConfig().getDefaultValues());
        getViewRepresentation().setLockColumn(getConfig().getLockColumn());
        getViewRepresentation().setType(getConfig().getType());
        getViewRepresentation().setPossibleValues(getConfig().getPossibleValues());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().setColumn(getViewValue().getColumn());
        getConfig().setValues(getViewValue().getValues());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyConfigToDialog() {
        super.copyConfigToDialog();
        getDialogRepresentation().setDefaultColumn(getConfig().getDefaultColumn());
        getDialogRepresentation().setDefaultValues(getConfig().getDefaultValues());
        getDialogRepresentation().setLockColumn(getConfig().getLockColumn());
        getDialogRepresentation().setType(getConfig().getType());
    }

}
