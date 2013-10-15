package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class StringInputQuickFormNodeFactory extends NodeFactory<StringInputQuickFormNodeModel> implements
        WizardNodeFactoryExtension<StringInputQuickFormNodeModel, StringInputQuickFormViewContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInputQuickFormNodeModel createNodeModel() {
        return new StringInputQuickFormNodeModel();
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
    public NodeView<StringInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final StringInputQuickFormNodeModel nodeModel) {
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
        return new StringInputQuickFormNodeDialog();
    }
}
