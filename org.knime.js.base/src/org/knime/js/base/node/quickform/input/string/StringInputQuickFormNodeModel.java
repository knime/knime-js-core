package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 *
 */
public class StringInputQuickFormNodeModel
		extends QuickFormFlowVariableNodeModel<StringInputQuickFormConfigurationContent, String> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void validateSettings(NodeSettingsRO settings)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createAndPushFlowVariable() throws InvalidSettingsException {
		// TODO Auto-generated method stub
		
	}

    /**
     * {@inheritDoc}
     */
    public DialogNodePanel<StringInputQuickFormConfigurationContent, String> createDialogPanel() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public StringInputQuickFormConfigurationContent createViewContent() {
        return new StringInputQuickFormConfigurationContent();
    }

    /**
     * {@inheritDoc}
     */
    public StringInputQuickFormConfigurationContent createEmptyInstance() {
        return new StringInputQuickFormConfigurationContent();
    }

    
}
