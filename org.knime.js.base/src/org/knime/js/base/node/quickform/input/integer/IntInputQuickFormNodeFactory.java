package org.knime.js.base.node.quickform.input.integer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class IntInputQuickFormNodeFactory extends NodeFactory<IntInputQuickFormNodeModel> implements
        WizardNodeFactoryExtension<IntInputQuickFormNodeModel, IntInputQuickFormViewContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IntInputQuickFormNodeModel createNodeModel() {
        return new IntInputQuickFormNodeModel();
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
    public NodeView<IntInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final IntInputQuickFormNodeModel nodeModel) {
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
        return new IntInputQuickFormNodeDialog();
    }
}
