package org.knime.js.base.node.quickform.input.date;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class DateInputQuickFormNodeFactory extends NodeFactory<DateInputQuickFormNodeModel> implements
        WizardNodeFactoryExtension<DateInputQuickFormNodeModel, DateInputQuickFormRepresentation,
        DateInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DateInputQuickFormNodeModel createNodeModel() {
        return new DateInputQuickFormNodeModel();
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
    public NodeView<DateInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final DateInputQuickFormNodeModel nodeModel) {
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
        return new DateInputQuickFormNodeDialog();
    }
}
