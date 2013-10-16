package org.knime.js.base.node.quickform.input.bool;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class BooleanInputQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<BooleanInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<BooleanInputQuickFormValue> createDialogPanel() {
        BooleanInputQuickFormDialogPanel panel = new BooleanInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

}
