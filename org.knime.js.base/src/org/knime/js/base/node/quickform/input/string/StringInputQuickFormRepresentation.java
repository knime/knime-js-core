package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.DialogNodeRepresentation;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class StringInputQuickFormRepresentation extends DialogNodeRepresentation<StringInputQuickFormValue> {

	private String m_value;

	/**
	 * @return the value
	 */
	public String getValue() {
		return m_value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		m_value = value;
	}
	
	@Override
	public void saveToNodeSettings(NodeSettingsWO settings) {
		super.saveToNodeSettings(settings);
		settings.addString("value", m_value);
	}
	
	@Override
	public void loadFromNodeSettings(NodeSettingsRO settings) {
		super.loadFromNodeSettings(settings);
		m_value = settings.getString("value", null);
	}

    /**
     * {@inheritDoc}
     */
    public DialogNodePanel createDialogPanel() {
        return null;
    }

}
