package org.knime.js.base.node.quickform.selection.value;

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
        ValueSelectionQuickFormValue, ValueSelectionQuickFormViewRepresentation, ValueSelectionQuickFormValue> {
    
    /** Creates a new value selection node model. */
    public ValueSelectionQuickFormNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{FlowVariablePortObject.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueSelectionQuickFormRepresentation createEmptyDialogRepresentation() {
        return new ValueSelectionQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueSelectionQuickFormValue createEmptyDialogValue() {
        return new ValueSelectionQuickFormValue();
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
        createEmptyDialogRepresentation().loadFromNodeSettings(settings);
        createEmptyDialogValue().loadFromNodeSettings(settings);
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
    public ValueSelectionQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new ValueSelectionQuickFormViewRepresentation();
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
        final Set<DataCell> vals = dcs.getDomain().getValues();
        String[] values = new String[vals.size()];
        int i = 0;
        for (final DataCell cell : vals) {
            values[i++] = cell.toString();
        }
        getDialogRepresentation().setPossibleValues(values);
        getViewRepresentation().setPossibleValues(values);
    }
    
    private void createAndPushFlowVariable() {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getViewValue().getValue());
    }

}
