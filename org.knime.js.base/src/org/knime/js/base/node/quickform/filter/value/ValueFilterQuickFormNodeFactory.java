package org.knime.js.base.node.quickform.filter.value;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ValueFilterQuickFormNodeFactory extends NodeFactory<ValueFilterQuickFormNodeModel> implements
        WizardNodeFactoryExtension<ValueFilterQuickFormNodeModel, ValueFilterQuickFormRepresentation,
        ValueFilterQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterQuickFormNodeModel createNodeModel() {
        return new ValueFilterQuickFormNodeModel();
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
    public NodeView<ValueFilterQuickFormNodeModel> createNodeView(final int viewIndex,
            final ValueFilterQuickFormNodeModel nodeModel) {
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
        return new ValueFilterQuickFormNodeDialog();
    }
}
