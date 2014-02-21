package org.knime.js.base.node.quickform.input.bool;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class BooleanInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<BooleanInputQuickFormRepresentation, BooleanInputQuickFormValue, BooleanInputQuickFormViewRepresentation, BooleanInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BooleanInputQuickFormRepresentation createEmptyDialogRepresentation() {
        return new BooleanInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BooleanInputQuickFormValue createEmptyDialogValue() {
        return new BooleanInputQuickFormValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanInputQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new BooleanInputQuickFormViewRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanInputQuickFormValue createEmptyViewValue() {
        return new BooleanInputQuickFormValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_bool";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableInt(getDialogRepresentation().getFlowVariableName(), getViewValue().getBoolean() ? 1 : 0);
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
    public ValidationError validateViewValue(BooleanInputQuickFormValue viewContent) {
        return null;
    }

    

}
