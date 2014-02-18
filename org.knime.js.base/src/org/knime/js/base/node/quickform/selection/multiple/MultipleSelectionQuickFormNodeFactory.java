package org.knime.js.base.node.quickform.selection.multiple;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class MultipleSelectionQuickFormNodeFactory extends NodeFactory<MultipleSelectionQuickFormNodeModel> implements
        WizardNodeFactoryExtension<MultipleSelectionQuickFormNodeModel, MultipleSelectionQuickFormViewRepresentation, MultipleSelectionQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MultipleSelectionQuickFormNodeModel createNodeModel() {
        return new MultipleSelectionQuickFormNodeModel();
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
    public NodeView<MultipleSelectionQuickFormNodeModel> createNodeView(final int viewIndex,
            final MultipleSelectionQuickFormNodeModel nodeModel) {
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
        return new MultipleSelectionQuickFormNodeDialog();
    }
}
