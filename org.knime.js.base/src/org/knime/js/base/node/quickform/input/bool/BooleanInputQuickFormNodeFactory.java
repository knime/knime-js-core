package org.knime.js.base.node.quickform.input.bool;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class BooleanInputQuickFormNodeFactory extends NodeFactory<BooleanInputQuickFormNodeModel> implements
        WizardNodeFactoryExtension<BooleanInputQuickFormNodeModel, BooleanInputQuickFormRepresentation,
        BooleanInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanInputQuickFormNodeModel createNodeModel() {
        return new BooleanInputQuickFormNodeModel();
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
    public NodeView<BooleanInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final BooleanInputQuickFormNodeModel nodeModel) {
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
        return new BooleanInputQuickFormNodeDialog();
    }
}
