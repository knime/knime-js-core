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
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * The model for the list box input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class ListBoxInputQuickFormNodeModel
        extends QuickFormNodeModel
        <ListBoxInputQuickFormRepresentation,
        ListBoxInputQuickFormValue,
        ListBoxInputQuickFormConfig> {

    /**
     * Creates a list box input node model.
     * @param viewName the view name
     */
    protected ListBoxInputQuickFormNodeModel(final String viewName) {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /**
     * Pushes the current value as flow variable.
     */
    private void createAndPushFlowVariable() {
        final String variableName = getConfig().getFlowVariableName();
        final String value = getRelevantValue().getString();
        pushFlowVariableString(variableName, value);
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        getValidatedValues();
        final String variableName = getConfig().getFlowVariableName();
        createAndPushFlowVariable();
        return new PortObjectSpec[]{createSpec(variableName)};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        final String variableName = getConfig().getFlowVariableName();
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

    /**
     * @return List of validated values
     * @throws InvalidSettingsException If one of the values is invalid
     */
    private List<String> getValidatedValues() throws InvalidSettingsException {
        boolean omitEmpty = getConfig().getOmitEmpty();
        final String value = getRelevantValue().getString();
        String separator = getConfig().getSeparator();
        final ArrayList<String> values = new ArrayList<String>();
        if (separator == null || separator.isEmpty()) {
            if (!(omitEmpty && value.isEmpty())) {
                values.add(value);
            }
        } else {
            String[] splitValue = value.split(getConfig().getSeparatorRegex(), -1);
            for (String val : splitValue) {
                if (!(omitEmpty && val.isEmpty())) {
                    values.add(val);
                }
            }
        }
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        return values;
    }

    /**
     * @param variableName Name of the created column
     * @return Output spec
     */
    private DataTableSpec createSpec(final String variableName) {
        final DataColumnSpec cspec = new DataColumnSpecCreator(variableName, StringCell.TYPE).createSpec();
        return new DataTableSpec(cspec);
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
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setString(getViewValue().getString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxInputQuickFormConfig createEmptyConfig() {
        return new ListBoxInputQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListBoxInputQuickFormRepresentation getRepresentation() {
        return new ListBoxInputQuickFormRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final ListBoxInputQuickFormValue viewContent) {
        boolean omitEmpty = getConfig().getOmitEmpty();
        final String value = viewContent.getString();
        String separator = getConfig().getSeparator();
        final ArrayList<String> values = new ArrayList<String>();
        if (separator == null || separator.isEmpty()) {
            if (!(omitEmpty && value.isEmpty())) {
                values.add(value);
            }
        } else {
            String[] splitValue = value.split(getConfig().getSeparatorRegex(), -1);
            for (String val : splitValue) {
                if (!(omitEmpty && val.isEmpty())) {
                    values.add(val);
                }
            }
        }
        String regex = getConfig().getRegex();
        if (regex != null && !regex.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                if (!values.get(i).matches(regex)) {
                    return new ValidationError("Value " + (i + 1)
                            + " is not valid:\n"
                            + getConfig().getErrorMessage().replaceAll("[?]", values.get(i)));
                }
            }
        }
        return super.validateViewValue(viewContent);
    }

}
