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
    
    private static final String CFG_REGEX = "regex";
    
    private static final String DEFAULT_REGEX = "";
    
    private String m_regex = DEFAULT_REGEX;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    private StringInputQuickFormValue m_value = new StringInputQuickFormValue(null);
    
    /**
     * @return the regex
     */
    public String getRegex() {
        return m_regex;
    }
    
    /**
     * @param regex the regex to set
     */
    public void setRegex(final String regex) {
        m_regex = regex;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_regex = settings.getString(CFG_REGEX);
        m_defaultValue = settings.getString(CFG_DEFAULT);
        m_value = new StringInputQuickFormValue(null);
        m_value.loadFromNodeSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_regex = settings.getString(CFG_REGEX, DEFAULT_REGEX);
        m_defaultValue = settings.getString(CFG_DEFAULT, "");
        m_value = new StringInputQuickFormValue(null);
        m_value.loadFromNodeSettingsInDialog(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_REGEX, m_regex);
        settings.addString(CFG_DEFAULT, m_defaultValue);
        m_value.saveToNodeSettings(settings);
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

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final StringInputQuickFormValue value) {
        value.setString(m_defaultValue);        
    }
    
    /**
     * @return the value
     */
    public StringInputQuickFormValue getValue() {
        return m_value;
    }
    
    /**
     * @param value the value to set
     */
    public void setValue(final StringInputQuickFormValue value) {
        m_value = value;
    }

}
