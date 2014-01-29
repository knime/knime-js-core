package org.knime.js.base.node.quickform.input.integer;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class IntInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<IntInputQuickFormRepresentation, IntInputQuickFormValue, IntInputQuickFormViewRepresentation, IntInputQuickFormValue> {

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
        return new IntInputQuickFormValue(null);
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
        return new IntInputQuickFormValue(null);
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
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableInt(getDialogRepresentation().getFlowVariableName(), getDialogValue().getInteger());
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
    public void loadViewContent(IntInputQuickFormRepresentation viewContent) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    public void loadViewValue(IntInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        
    }

}
