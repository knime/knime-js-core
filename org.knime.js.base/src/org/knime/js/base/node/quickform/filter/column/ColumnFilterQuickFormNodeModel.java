package org.knime.js.base.node.quickform.filter.column;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataColumnSpec;
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
public class ColumnFilterQuickFormNodeModel extends QuickFormNodeModel<ColumnFilterQuickFormRepresentation, 
        ColumnFilterQuickFormValue, ColumnFilterQuickFormViewRepresentation, ColumnFilterQuickFormValue> {
    
    /** Creates a new value selection node model. */
    public ColumnFilterQuickFormNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnFilterQuickFormRepresentation createEmptyDialogRepresentation() {
        return new ColumnFilterQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnFilterQuickFormValue createEmptyDialogValue() {
        return new ColumnFilterQuickFormValue();
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
    public ColumnFilterQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new ColumnFilterQuickFormViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterQuickFormValue createEmptyViewValue() {
        return new ColumnFilterQuickFormValue();
    }
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final ColumnFilterQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        updateColumns((DataTableSpec) inSpecs[0]);
        createAndPushFlowVariable();
        return new DataTableSpec[]{createSpec((DataTableSpec) inSpecs[0])};
    }
    
    private DataTableSpec createSpec(final DataTableSpec inSpec) throws InvalidSettingsException {
        final String[] values = getViewValue().getColumns();
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
    
    private void updateColumns(final DataTableSpec spec) {
        getDialogRepresentation().setPossibleColumns(spec.getColumnNames());
        getViewRepresentation().setPossibleColumns(spec.getColumnNames());
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        updateColumns((DataTableSpec) inObjects[0].getSpec());
        createAndPushFlowVariable();
        DataTableSpec outSpec = createSpec((DataTableSpec) inObjects[0].getSpec());
        BufferedDataContainer container = exec.createDataContainer(outSpec, false);
        container.close();
        return new PortObject[]{container.getTable()};
    }
    
    private void createAndPushFlowVariable() {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(),
                StringUtils.join(getViewValue().getColumns(), ","));
    }

}
