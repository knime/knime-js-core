package org.knime.js.base.node.quickform.selection.value;

import java.util.Arrays;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
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
public class ValueSelectionQuickFormNodeModel extends QuickFormNodeModel<ValueSelectionQuickFormRepresentation, 
        ValueSelectionQuickFormValue> {
    
    /** Creates a new value selection node model. */
    public ValueSelectionQuickFormNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{FlowVariablePortObject.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_selection_value";
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
    public ValueSelectionQuickFormRepresentation createEmptyViewRepresentation() {
        return new ValueSelectionQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueSelectionQuickFormValue createEmptyViewValue() {
        return new ValueSelectionQuickFormValue();
    }
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final ValueSelectionQuickFormValue viewContent) {
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
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        updateValues(((DataTable)inObjects[0]).getDataTableSpec());
        createAndPushFlowVariable();
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
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
        if (!Arrays.asList(getDialogRepresentation().getPossibleValues()).contains(getViewValue().getValue())) {
            throw new InvalidSettingsException("The selected value '"
                    + getViewValue().getValue()
                    + "' is not among the possible values in the column '"
                    + getDialogRepresentation().getColumn() + "'");
        }
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getViewValue().getValue());
    }

}
