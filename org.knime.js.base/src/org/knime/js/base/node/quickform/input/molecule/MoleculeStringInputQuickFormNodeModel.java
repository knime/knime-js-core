package org.knime.js.base.node.quickform.input.molecule;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class MoleculeStringInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<MoleculeStringInputQuickFormRepresentation, MoleculeStringInputQuickFormValue, MoleculeStringInputQuickFormViewRepresentation, MoleculeStringInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeStringInputQuickFormRepresentation createEmptyDialogRepresentation() {
        return new MoleculeStringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeStringInputQuickFormValue createEmptyDialogValue() {
        return new MoleculeStringInputQuickFormValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new MoleculeStringInputQuickFormViewRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormValue createEmptyViewValue() {
        return new MoleculeStringInputQuickFormValue(null);
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
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getDialogValue().getMoleculeString());
        pushFlowVariableString("molecule_format", getDialogRepresentation().getFormat());
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
    public void loadViewContent(
            MoleculeStringInputQuickFormRepresentation viewContent) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(MoleculeStringInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        
    }
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(MoleculeStringInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
