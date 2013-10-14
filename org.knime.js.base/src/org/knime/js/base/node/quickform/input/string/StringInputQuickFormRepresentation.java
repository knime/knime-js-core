package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class StringInputQuickFormRepresentation extends QuickFormFlowVariableRepresentation<StringInputQuickFormValue> {

	@Override
	public void saveToNodeSettings(NodeSettingsWO settings) {
		super.saveToNodeSettings(settings);
	}
	
	@Override
	public void loadFromNodeSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		super.loadFromNodeSettings(settings);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<StringInputQuickFormValue> createDialogPanel() {
        StringInputQuickFormDialogPanel panel = new StringInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

}
