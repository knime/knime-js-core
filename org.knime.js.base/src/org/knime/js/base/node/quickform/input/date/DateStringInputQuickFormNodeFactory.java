package org.knime.js.base.node.quickform.input.date;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class DateStringInputQuickFormNodeFactory extends NodeFactory<DateStringInputQuickFormNodeModel> implements
        WizardNodeFactoryExtension<DateStringInputQuickFormNodeModel, DateStringInputQuickFormRepresentation,
        DateStringInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DateStringInputQuickFormNodeModel createNodeModel() {
        return new DateStringInputQuickFormNodeModel();
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
    public NodeView<DateStringInputQuickFormNodeModel> createNodeView(final int viewIndex,
            final DateStringInputQuickFormNodeModel nodeModel) {
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
        return new DateStringInputQuickFormNodeDialog();
    }
}
