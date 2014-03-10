package org.knime.js.base.node.quickform.selection.column;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class ColumnSelectionQuickFormNodeFactory extends NodeFactory<ColumnSelectionQuickFormNodeModel> implements
        WizardNodeFactoryExtension<ColumnSelectionQuickFormNodeModel, ColumnSelectionQuickFormRepresentation,
        ColumnSelectionQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnSelectionQuickFormNodeModel createNodeModel() {
        return new ColumnSelectionQuickFormNodeModel();
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
    public NodeView<ColumnSelectionQuickFormNodeModel> createNodeView(final int viewIndex,
            final ColumnSelectionQuickFormNodeModel nodeModel) {
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
        return new ColumnSelectionQuickFormNodeDialog();
    }
}
