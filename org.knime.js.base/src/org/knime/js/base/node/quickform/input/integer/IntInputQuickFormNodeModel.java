package org.knime.js.base.node.quickform.input.integer;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class IntInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<IntInputQuickFormRepresentation, IntInputQuickFormValue,
        IntInputQuickFormViewRepresentation, IntInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntInputQuickFormRepresentation createEmptyDialogRepresentation() {
        return new IntInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntInputQuickFormValue createEmptyDialogValue() {
        return new IntInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntInputQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new IntInputQuickFormViewRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IntInputQuickFormValue createEmptyViewValue() {
        return new IntInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_integer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        int value = getViewValue().getInteger();
        int min = getDialogRepresentation().getMin();
        int max = getDialogRepresentation().getMax();
        if (getDialogRepresentation().getUseMin() && value < min) {
            throw new InvalidSettingsException("The set integer " + value
                    + " is smaller than the required minimum " + min);
        }
        if (getDialogRepresentation().getUseMax() && value > max) {
            throw new InvalidSettingsException("The set integer " + value
                    + " is bigger than the required maximum " + max);
        }
        pushFlowVariableInt(getDialogRepresentation().getFlowVariableName(), getViewValue().getInteger());
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
    public ValidationError validateViewValue(final IntInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
