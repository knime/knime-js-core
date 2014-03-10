package org.knime.js.base.node.quickform.input.molecule;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class MoleculeStringInputQuickFormNodeFactory extends NodeFactory<MoleculeStringInputQuickFormNodeModel>
        implements
        WizardNodeFactoryExtension<MoleculeStringInputQuickFormNodeModel,
        MoleculeStringInputQuickFormRepresentation, MoleculeStringInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormNodeModel createNodeModel() {
        return new MoleculeStringInputQuickFormNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<MoleculeStringInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final MoleculeStringInputQuickFormNodeModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new MoleculeStringInputQuickFormNodeDialog();
    }
}
