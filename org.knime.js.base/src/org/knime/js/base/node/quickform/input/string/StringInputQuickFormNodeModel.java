package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class StringInputQuickFormNodeModel extends QuickFormFlowVariableNodeModel<StringInputQuickFormRepresentation, 
        StringInputQuickFormValue, StringInputQuickFormViewRepresentation, StringInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected StringInputQuickFormRepresentation createEmptyDialogRepresentation() {
        return new StringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StringInputQuickFormValue createEmptyDialogValue() {
        return new StringInputQuickFormValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_string";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getDialogValue().getString());
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
    public StringInputQuickFormRepresentation createEmptyRepresentationInstance() {
        return new StringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    public StringInputQuickFormValue createEmptyValueInstance() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(StringInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInputQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new StringInputQuickFormViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInputQuickFormValue createEmptyViewValue() {
        return new StringInputQuickFormValue(null);
    }
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(StringInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
