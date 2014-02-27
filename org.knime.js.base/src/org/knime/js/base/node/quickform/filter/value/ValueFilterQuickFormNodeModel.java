package org.knime.js.base.node.quickform.filter.value;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.knime.base.node.io.filereader.DataCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
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
public class ValueFilterQuickFormNodeModel extends QuickFormNodeModel<ValueFilterQuickFormRepresentation, 
        ValueFilterQuickFormValue, ValueFilterQuickFormViewRepresentation, ValueFilterQuickFormValue> {
    
    /** Creates a new value selection node model. */
    public ValueFilterQuickFormNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueFilterQuickFormRepresentation createEmptyDialogRepresentation() {
        return new ValueFilterQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueFilterQuickFormValue createEmptyDialogValue() {
        return new ValueFilterQuickFormValue();
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
    public ValueFilterQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new ValueFilterQuickFormViewRepresentation();
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
    public ValidationError validateViewValue(final ValueFilterQuickFormValue viewContent) {
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
        return new DataTableSpec[]{createSpec((DataTableSpec) inSpecs[0])};
    }
    
    private DataTableSpec createSpec(final DataTableSpec inSpec) throws InvalidSettingsException {
        String column = getDialogRepresentation().getColumn();
        if (column != null && inSpec.containsName(column)) {
            return new DataTableSpec(inSpec.getColumnSpec(column));
        } else {
            throw new InvalidSettingsException("Unknown column "
                    + column + " selected.");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        updateValues(((DataTable)inObjects[0]).getDataTableSpec());
        createAndPushFlowVariable();
        // TODO
        DataTableSpec outSpec = createSpec((DataTableSpec) inObjects[0].getSpec());
        BufferedDataContainer container = exec.createDataContainer(outSpec, false);
        String[] values = getViewValue().getValues();
        DataType type = outSpec.getColumnSpec(0).getType();
        DataCellFactory cellFactory = new DataCellFactory();
        for (int i = 0; i < values.length; i++) {
            DataCell result = cellFactory.createDataCellOfType(
                    type, values[i]);
            container.addRowToTable(new DefaultRow(RowKey.createRowKey(i), result));
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
            values = new String[vals.size()];
            int i = 0;
            for (final DataCell cell : vals) {
                values[i++] = cell.toString();
            }
        }
        getDialogRepresentation().setPossibleValues(values);
        getViewRepresentation().setPossibleValues(values);
    }
    
    private void createAndPushFlowVariable() {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(),
                StringUtils.join(getViewValue().getValues(), ","));
    }

}
