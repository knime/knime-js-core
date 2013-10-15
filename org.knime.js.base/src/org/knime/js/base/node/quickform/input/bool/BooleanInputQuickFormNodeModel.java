package org.knime.js.base.node.quickform.input.bool;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class BooleanInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<BooleanInputQuickFormRepresentation, BooleanInputQuickFormValue, BooleanInputQuickFormViewContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BooleanInputQuickFormRepresentation createNodeRepresentation() {
        return new BooleanInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BooleanInputQuickFormValue createNodeValue() {
        return new BooleanInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanInputQuickFormViewContent createViewContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanInputQuickFormViewContent createEmptyInstance() {
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
    public void loadViewContent(final BooleanInputQuickFormViewContent viewContent) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableInt(getNodeRepresentation().getFlowVariableName(), getNodeValue().getBoolean() ? 1 : 0);
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
