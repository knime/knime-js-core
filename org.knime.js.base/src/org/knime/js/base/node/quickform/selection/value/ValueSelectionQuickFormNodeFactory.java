package org.knime.js.base.node.quickform.selection.value;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ValueSelectionQuickFormNodeFactory extends NodeFactory<ValueSelectionQuickFormNodeModel> implements
        WizardNodeFactoryExtension<ValueSelectionQuickFormNodeModel, ValueSelectionQuickFormRepresentation,
        ValueSelectionQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueSelectionQuickFormNodeModel createNodeModel() {
        return new ValueSelectionQuickFormNodeModel();
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
    public NodeView<ValueSelectionQuickFormNodeModel> createNodeView(final int viewIndex,
            final ValueSelectionQuickFormNodeModel nodeModel) {
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
        return new ValueSelectionQuickFormNodeDialog();
    }
}
