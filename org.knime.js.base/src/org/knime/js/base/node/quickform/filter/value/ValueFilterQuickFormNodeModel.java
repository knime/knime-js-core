package org.knime.js.base.node.quickform.filter.value;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ValueFilterQuickFormNodeModel
        extends
        QuickFormNodeModel<ValueFilterQuickFormRepresentation, ValueFilterQuickFormValue> {

    /** Creates a new value selection node model. */
    public ValueFilterQuickFormNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{BufferedDataTable.TYPE});
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
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
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

    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(
            final ValueFilterQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        updateValues((DataTableSpec)inSpecs[0]);
        createAndPushFlowVariable();
        return new DataTableSpec[]{(DataTableSpec)inSpecs[0]};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        updateValues(((DataTable)inObjects[0]).getDataTableSpec());
        createAndPushFlowVariable();
        BufferedDataTable inTable = (BufferedDataTable)inObjects[0];
        BufferedDataContainer container =
                exec.createDataContainer(inTable.getDataTableSpec(), false);
        List<String> values = Arrays.asList(getViewValue().getValues());
        int colIndex;
        for (colIndex = 0; colIndex < inTable.getDataTableSpec().getNumColumns(); colIndex++) {
            if (inTable.getDataTableSpec().getColumnSpec(colIndex).getName()
                    .equals(getViewRepresentation().getColumn())) {
                break;
            }
        }
        inTable.getDataTableSpec().getColumnSpec(getViewRepresentation().getColumn());
        for (DataRow row : inTable) {
            if (values.contains(row.getCell(colIndex).toString())) {
                container.addRowToTable(row);
            }
        }
        container.close();
        return new PortObject[]{container.getTable()};
    }

    private void updateValues(final DataTableSpec spec) {
        String column = getDialogRepresentation().getColumn();
        DataColumnSpec dcs = spec.getColumnSpec(column);
        String[] values;
        if (dcs == null) {
            values = new String[0];
        } else {
            final Set<DataCell> vals = dcs.getDomain().getValues();
            if (vals == null) {
                values = new String[0];
            } else {
                values = new String[vals.size()];
                int i = 0;
                for (final DataCell cell : vals) {
                    values[i++] = cell.toString();
                }
            }
        }
        getDialogRepresentation().setPossibleValues(values);
        getViewRepresentation().setPossibleValues(values);
    }

    private void createAndPushFlowVariable() throws InvalidSettingsException {
        checkSelectedValues();
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(),
                StringUtils.join(getViewValue().getValues(), ","));
    }

    private void checkSelectedValues() throws InvalidSettingsException {
        List<String> possibleValues =
                Arrays.asList(getDialogRepresentation().getPossibleValues());
        String[] selectedValues = getViewValue().getValues();
        for (String value : selectedValues) {
            if (!possibleValues.contains(value)) {
                throw new InvalidSettingsException("The selected value '"
                        + value
                        + "' is not among the possible values in the column '"
                        + getDialogRepresentation().getColumn() + "'");
            }
        }
    }

}
