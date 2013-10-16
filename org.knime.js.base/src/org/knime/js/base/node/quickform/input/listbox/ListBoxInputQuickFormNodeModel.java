package org.knime.js.base.node.quickform.input.listbox;

import java.util.ArrayList;
import java.util.Arrays;

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
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ListBoxInputQuickFormNodeModel
        extends
        QuickFormNodeModel<ListBoxInputQuickFormRepresentation, ListBoxInputQuickFormValue, ListBoxInputQuickFormViewContent> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ListBoxInputQuickFormNodeModel.class);

    /**
     * Creates a list box input node model.
     */
    protected ListBoxInputQuickFormNodeModel() {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE});
    }

    private void createAndPushFlowVariable() {
        final String variableName = getNodeRepresentation().getFlowVariableName();
        final String value = getNodeValue().getString();
        pushFlowVariableString(variableName, value);
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) {
        final String variableName = getNodeRepresentation().getFlowVariableName();
        createAndPushFlowVariable();
        return new PortObjectSpec[]{createSpec(variableName)};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        final String variableName = getNodeRepresentation().getFlowVariableName();
        final String value = getNodeValue().getString();
        String separator = getNodeRepresentation().getSeparator();
        final ArrayList<String> values = new ArrayList<String>();
        if (separator == null || separator.isEmpty()) {
            values.add(value);
        } else {
            StringBuilder sepString = new StringBuilder();
            for (int i = 0; i < separator.length(); i++) {
                if (i > 0) {
                    sepString.append('|');
                }
                char c = separator.charAt(i);
                if (c == '|') {
                    sepString.append("\\|");
                } else if (c == '\\') {
                    if (i + 1 < separator.length()) {
                        if (separator.charAt(i + 1) == 'n') {
                            sepString.append("\\n");
                            i++;
                        } else if (separator.charAt(i + 1) == 't') {
                            sepString.append("\\t");
                            i++;
                        } else {
                            // not supported
                            LOGGER.assertLog(false,
                                    "A back slash must not be followed by a char other than n or t; ignoring it.");
                        }
                    } else {
                        // not supported
                        LOGGER.assertLog(false, "A back slash must be followed by either a n or t; ignoring it.");
                    }
                } else {
                    // a real, non-specific char
                    sepString.append(c);
                }
            }
            values.addAll(Arrays.asList(value.split(sepString.toString())));
        }
        DataTableSpec outSpec = createSpec(variableName);
        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
        for (int i = 0; i < values.size(); i++) {
            cont.addRowToTable(new DefaultRow(RowKey.createRowKey(i), new StringCell(values.get(i))));
        }
        cont.close();
        createAndPushFlowVariable();
        return new PortObject[]{cont.getTable()};
    }

    private DataTableSpec createSpec(final String variableName) {
        final DataColumnSpec cspec = new DataColumnSpecCreator(variableName, StringCell.TYPE).createSpec();
        return new DataTableSpec(cspec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListBoxInputQuickFormRepresentation createNodeRepresentation() {
        return new ListBoxInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListBoxInputQuickFormValue createNodeValue() {
        return new ListBoxInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxInputQuickFormViewContent createViewContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxInputQuickFormViewContent createEmptyInstance() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewContent(final ListBoxInputQuickFormViewContent viewContent) {
        // TODO Auto-generated method stub

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

}
