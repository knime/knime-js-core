package org.knime.js.base.node.quickform.filter.column;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ColumnFilterQuickFormNodeFactory extends NodeFactory<ColumnFilterQuickFormNodeModel> implements
        WizardNodeFactoryExtension<ColumnFilterQuickFormNodeModel, ColumnFilterQuickFormViewRepresentation,
        ColumnFilterQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterQuickFormNodeModel createNodeModel() {
        return new ColumnFilterQuickFormNodeModel();
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
    public NodeView<ColumnFilterQuickFormNodeModel> createNodeView(final int viewIndex,
            final ColumnFilterQuickFormNodeModel nodeModel) {
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
        return new ColumnFilterQuickFormNodeDialog();
    }
}
