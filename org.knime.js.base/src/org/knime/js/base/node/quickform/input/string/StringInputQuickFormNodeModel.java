package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class StringInputQuickFormNodeModel extends QuickFormFlowVariableNodeModel<StringInputQuickFormRepresentation, 
        StringInputQuickFormValue, StringInputQuickFormViewContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected StringInputQuickFormRepresentation createNodeRepresentation() {
        return new StringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StringInputQuickFormValue createNodeValue() {
        return new StringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInputQuickFormViewContent createViewContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInputQuickFormViewContent createEmptyInstance() {
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
    public void loadViewContent(final StringInputQuickFormViewContent viewContent) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableString(getNodeRepresentation().getFlowVariableName(), getNodeValue().getString());
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
