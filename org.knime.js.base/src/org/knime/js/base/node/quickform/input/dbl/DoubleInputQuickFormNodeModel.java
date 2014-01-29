package org.knime.js.base.node.quickform.input.dbl;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class DoubleInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<DoubleInputQuickFormRepresentation, DoubleInputQuickFormValue, DoubleInputQuickFormViewRepresentation, DoubleInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleInputQuickFormRepresentation createEmptyDialogRepresentation() {
        return new DoubleInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleInputQuickFormValue createEmptyDialogValue() {
        return new DoubleInputQuickFormValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleInputQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new DoubleInputQuickFormViewRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleInputQuickFormValue createEmptyViewValue() {
        return new DoubleInputQuickFormValue(null);
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
        pushFlowVariableDouble(getDialogRepresentation().getFlowVariableName(), getDialogValue().getDouble());
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
    public void loadViewValue(DoubleInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        
    }

}
