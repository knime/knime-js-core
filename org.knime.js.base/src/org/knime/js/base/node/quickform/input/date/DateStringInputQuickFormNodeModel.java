package org.knime.js.base.node.quickform.input.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class DateStringInputQuickFormNodeModel extends QuickFormFlowVariableNodeModel
        <DateStringInputQuickFormRepresentation, DateStringInputQuickFormValue,
        DateStringInputQuickFormViewRepresentation, DateStringInputQuickFormValue> {

    /**
     * Format for the date to string and string to date operations.
     */
    static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected DateStringInputQuickFormRepresentation createEmptyDialogRepresentation() {
        return new DateStringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateStringInputQuickFormValue createEmptyDialogValue() {
        return new DateStringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateStringInputQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new DateStringInputQuickFormViewRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DateStringInputQuickFormValue createEmptyViewValue() {
        return new DateStringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_date";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        Date value = getViewValue().getDate();
        Date min = getDialogRepresentation().getMin();
        Date max = getDialogRepresentation().getMax();
        if (getDialogRepresentation().getUseMin() && value.before(min)) {
            throw new InvalidSettingsException("The set date " + value
                    + " is before the earliest allowed date " + min);
        }
        if (getDialogRepresentation().getUseMax() && value.after(max)) {
            throw new InvalidSettingsException("The set date " + value
                    + " is after the latest allowed date " + max);
        }
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(),
                FORMAT.format(getViewValue().getDate()));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(
            final DateStringInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
