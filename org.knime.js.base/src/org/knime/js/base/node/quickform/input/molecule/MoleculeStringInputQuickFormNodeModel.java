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
        QuickFormFlowVariableNodeModel<MoleculeStringInputQuickFormRepresentation,
        MoleculeStringInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormRepresentation createEmptyViewRepresentation() {
        return new MoleculeStringInputQuickFormRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormValue createEmptyViewValue() {
        return new MoleculeStringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_molecule";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getViewValue().getMoleculeString());
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
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final MoleculeStringInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
