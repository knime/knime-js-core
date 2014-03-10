package org.knime.js.base.node.quickform.input.listbox;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
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
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ListBoxInputQuickFormNodeModel
        extends
        QuickFormNodeModel<ListBoxInputQuickFormRepresentation, ListBoxInputQuickFormValue> {

    /**
     * Creates a list box input node model.
     */
    protected ListBoxInputQuickFormNodeModel() {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE});
    }

    private void createAndPushFlowVariable() {
        final String variableName = getDialogRepresentation().getFlowVariableName();
        final String value = getViewValue().getString();
        pushFlowVariableString(variableName, value);
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        getValidatedValues();
        final String variableName = getDialogRepresentation().getFlowVariableName();
        createAndPushFlowVariable();
        return new PortObjectSpec[]{createSpec(variableName)};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        final String variableName = getDialogRepresentation().getFlowVariableName();
        DataTableSpec outSpec = createSpec(variableName);
        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
        List<String> values = getValidatedValues();
        for (int i = 0; i < values.size(); i++) {
            cont.addRowToTable(new DefaultRow(RowKey.createRowKey(i), new StringCell(values.get(i))));
        }
        cont.close();
        createAndPushFlowVariable();
        return new PortObject[]{cont.getTable()};
    }
    
    private List<String> getValidatedValues() throws InvalidSettingsException {
        boolean omitEmpty = getDialogRepresentation().getOmitEmpty();
        final String value = getViewValue().getString();
        String separator = getDialogRepresentation().getSeparator();
        final ArrayList<String> values = new ArrayList<String>();
        if (separator == null || separator.isEmpty()) {
            if (!(omitEmpty && value.isEmpty())) {
                values.add(value);
            }
        } else {
            String[] splitValue = value.split(getDialogRepresentation().getSeparatorRegex(), -1);
            for (String val : splitValue) {
                if (!(omitEmpty && val.isEmpty())) {
                    values.add(val);
                }
            }
        }
        String regex = getDialogRepresentation().getRegex();
        if (regex != null && !regex.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                if (!values.get(i).matches(regex)) {
                    throw new InvalidSettingsException("Value " + (i + 1)
                            + " is not valid:\n"
                            + getDialogRepresentation().getErrorMessage());
                }
            }
        }
        return values;
    }

    private DataTableSpec createSpec(final String variableName) {
        final DataColumnSpec cspec = new DataColumnSpecCreator(variableName, StringCell.TYPE).createSpec();
        return new DataTableSpec(cspec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxInputQuickFormRepresentation createEmptyViewRepresentation() {
        return new ListBoxInputQuickFormRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxInputQuickFormValue createEmptyViewValue() {
        return new ListBoxInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_listbox";
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

    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final ListBoxInputQuickFormValue viewContent) {
        return null;
    }

}
