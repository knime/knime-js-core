package org.knime.js.base.node.quickform.input.date;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DateStringInputQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<DateStringInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<DateStringInputQuickFormValue> createDialogPanel() {
        DateStringInputQuickFormDialogPanel panel = new DateStringInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

}
