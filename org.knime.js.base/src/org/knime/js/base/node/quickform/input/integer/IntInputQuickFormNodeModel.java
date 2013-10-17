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
        QuickFormFlowVariableNodeModel<IntInputQuickFormRepresentation, IntInputQuickFormValue, IntInputQuickFormViewContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntInputQuickFormRepresentation createNodeRepresentation() {
        return new IntInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntInputQuickFormValue createNodeValue() {
        return new IntInputQuickFormValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntInputQuickFormViewContent createViewContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntInputQuickFormViewContent createEmptyInstance() {
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
    public void loadViewContent(final IntInputQuickFormViewContent viewContent) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableInt(getNodeRepresentation().getFlowVariableName(), getNodeValue().getInteger());
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
