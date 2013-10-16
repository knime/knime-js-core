package org.knime.js.base.node.quickform.input.listbox;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ListBoxInputQuickFormNodeFactory extends NodeFactory<ListBoxInputQuickFormNodeModel> implements
        WizardNodeFactoryExtension<ListBoxInputQuickFormNodeModel, ListBoxInputQuickFormViewContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxInputQuickFormNodeModel createNodeModel() {
        return new ListBoxInputQuickFormNodeModel();
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
    public NodeView<ListBoxInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final ListBoxInputQuickFormNodeModel nodeModel) {
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
        return new ListBoxInputQuickFormNodeDialog();
    }
}
