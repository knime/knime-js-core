package org.knime.js.base.node.quickform.input.dbl;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class DoubleInputQuickFormNodeFactory extends NodeFactory<DoubleInputQuickFormNodeModel> implements
        WizardNodeFactoryExtension<DoubleInputQuickFormNodeModel, DoubleInputQuickFormViewRepresentation,
        DoubleInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleInputQuickFormNodeModel createNodeModel() {
        return new DoubleInputQuickFormNodeModel();
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
    public NodeView<DoubleInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final DoubleInputQuickFormNodeModel nodeModel) {
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
        return new DoubleInputQuickFormNodeDialog();
    }
}
