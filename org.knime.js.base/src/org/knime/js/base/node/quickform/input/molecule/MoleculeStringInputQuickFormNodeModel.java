package org.knime.js.base.node.quickform.input.molecule;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class MoleculeStringInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<MoleculeStringInputQuickFormRepresentation, MoleculeStringInputQuickFormValue, MoleculeStringInputQuickFormViewContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeStringInputQuickFormRepresentation createNodeRepresentation() {
        return new MoleculeStringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeStringInputQuickFormValue createNodeValue() {
        return new MoleculeStringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormViewContent createViewContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormViewContent createEmptyInstance() {
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
    public void loadViewContent(final MoleculeStringInputQuickFormViewContent viewContent) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableString(getNodeRepresentation().getFlowVariableName(), getNodeValue().getString());
        pushFlowVariableString("molecule_format", getNodeRepresentation().getFormat());
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
