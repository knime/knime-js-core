package org.knime.js.base.node.quickform.selection.single;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class SingleSelectionQuickFormNodeFactory extends NodeFactory<SingleSelectionQuickFormNodeModel> implements
        WizardNodeFactoryExtension<SingleSelectionQuickFormNodeModel, SingleSelectionQuickFormViewRepresentation, SingleSelectionQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSelectionQuickFormNodeModel createNodeModel() {
        return new SingleSelectionQuickFormNodeModel();
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
    public NodeView<SingleSelectionQuickFormNodeModel> createNodeView(final int viewIndex,
            final SingleSelectionQuickFormNodeModel nodeModel) {
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
        return new SingleSelectionQuickFormNodeDialog();
    }
}
