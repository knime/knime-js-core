package org.knime.js.base.node.quickform.input.date;

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
public class DateInputQuickFormNodeModel extends QuickFormFlowVariableNodeModel
        <DateInputQuickFormRepresentation, DateInputQuickFormValue> {
    
    /**
     * Format string for the date to string and string to date operations.
     */
    static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Format string for the date to string and string to date operations.
     */
    static final String DATE_TIME_FORMAT = "yyyy-MM-dd;HH:mm:ss.S";

    /**
     * {@inheritDoc}
     */
    @Override
    public DateInputQuickFormRepresentation createEmptyViewRepresentation() {
        return new DateInputQuickFormRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DateInputQuickFormValue createEmptyViewValue() {
        return new DateInputQuickFormValue();
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
        SimpleDateFormat sdf =
                new SimpleDateFormat(getDialogRepresentation().getWithTime() ? DATE_TIME_FORMAT : DATE_FORMAT);
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(),
                sdf.format(getViewValue().getDate()));
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
            final DateInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
