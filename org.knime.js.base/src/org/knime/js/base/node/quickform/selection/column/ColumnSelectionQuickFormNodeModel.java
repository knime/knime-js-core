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
        ColumnSelectionQuickFormValue, ColumnSelectionQuickFormViewRepresentation, ColumnSelectionQuickFormValue> {
    
    /** Creates a new value selection node model. */
    public ColumnSelectionQuickFormNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{FlowVariablePortObject.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnSelectionQuickFormRepresentation createEmptyDialogRepresentation() {
        return new ColumnSelectionQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnSelectionQuickFormValue createEmptyDialogValue() {
        return new ColumnSelectionQuickFormValue();
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
    public ColumnSelectionQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new ColumnSelectionQuickFormViewRepresentation();
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
        getDialogRepresentation().setPossibleColumns(spec.getColumnNames());
        getDialogRepresentation().setSpec(spec);
        getViewRepresentation().setPossibleColumns(spec.getColumnNames());
    }
    
    private void createAndPushFlowVariable() throws InvalidSettingsException {
        if (!Arrays.asList(getDialogRepresentation().getPossibleColumns()).contains(getViewValue().getColumn())) {
            throw new InvalidSettingsException("The selected column '" + getViewValue().getColumn() + "' is not available");
        }
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getViewValue().getColumn());
    }

}
