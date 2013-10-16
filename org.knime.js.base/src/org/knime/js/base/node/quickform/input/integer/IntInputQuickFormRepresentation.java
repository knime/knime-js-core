package org.knime.js.base.node.quickform.input.integer;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class IntInputQuickFormRepresentation extends QuickFormFlowVariableRepresentation<IntInputQuickFormValue> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<IntInputQuickFormValue> createDialogPanel() {
        IntInputQuickFormDialogPanel panel = new IntInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

}
