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
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class MultipleSelectionQuickFormNodeModel
        extends
        QuickFormNodeModel<MultipleSelectionQuickFormRepresentation, MultipleSelectionQuickFormValue,
        MultipleSelectionQuickFormViewRepresentation, MultipleSelectionQuickFormValue> {

    /**
     * 
     */
    public MultipleSelectionQuickFormNodeModel() {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE});
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(),
                StringUtils.join(getViewValue().getVariableValue(), ","));
        return new PortObjectSpec[]{createSpec()};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(),
                StringUtils.join(getViewValue().getVariableValue(), ","));
        final DataTableSpec outSpec = createSpec();
        BufferedDataContainer container = exec.createDataContainer(outSpec, false);
        String[] values = getViewValue().getVariableValue();
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
    protected MultipleSelectionQuickFormRepresentation createEmptyDialogRepresentation() {
        return new MultipleSelectionQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MultipleSelectionQuickFormValue createEmptyDialogValue() {
        return new MultipleSelectionQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_selection_multiple";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Auto-generated method stub

    }

    private DataTableSpec createSpec() throws InvalidSettingsException {
        String strColumnName = getDialogRepresentation().getFlowVariableName();
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
    public MultipleSelectionQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new MultipleSelectionQuickFormViewRepresentation();
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
    public ValidationError validateViewValue(
            final MultipleSelectionQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}