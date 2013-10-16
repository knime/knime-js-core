package org.knime.js.base.node.quickform.input.dbl;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DoubleInputQuickFormRepresentation extends QuickFormFlowVariableRepresentation<DoubleInputQuickFormValue> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<DoubleInputQuickFormValue> createDialogPanel() {
        DoubleInputQuickFormDialogPanel panel = new DoubleInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

}
