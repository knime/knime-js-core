package org.knime.js.base.node.quickform.input.listbox;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class ListBoxInputQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ListBoxInputQuickFormValue> {
    
    private static final String CFG_REGEX = "regex";
    
    private static final String DEFAULT_REGEX = "";
    
    private String m_regex = DEFAULT_REGEX;
    
    private static final String CFG_ERROR_MESSAGE = "error_message";
    
    private static final String DEFAULT_ERROR_MESSAGE = "";
    
    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;
    
    private static final String CFG_SEPARATOR = "separator";
    
    private static final String DEFAULT_SEPARATOR = "\\n";
    
    private String m_separator = DEFAULT_SEPARATOR;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
    private static final String CFG_OMIT_EMPTY = "omit_empty";
    
    private static final boolean DEFAULT_OMIT_EMPTY = true;
    
    private boolean m_omitEmpty = DEFAULT_OMIT_EMPTY;
    
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
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return m_errorMessage;
    }
    
    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }
    
    /**
     * @return the separator
     */
    public String getSeparator() {
        return m_separator;
    }
    
    /**
     * @param separator the separator to set
     */
    public void setSeparator(final String separator) {
        m_separator = separator;
    }
    
    /**
     * @return separatorRegex
     */
    public String getSeparatorRegex() {
        if (m_separator == null || m_separator.isEmpty()) {
            return m_separator;
        }
        StringBuilder sepString = new StringBuilder();
        for (int i = 0; i < m_separator.length(); i++) {
            if (i > 0) {
                sepString.append('|');
            }
            char c = m_separator.charAt(i);
            if (c == '|') {
                sepString.append("\\|");
            } else if (c == '\\') {
                if (i + 1 < m_separator.length()) {
                    if (m_separator.charAt(i + 1) == 'n') {
                        sepString.append("\\n");
                        i++;
                    } else if (m_separator.charAt(i + 1) == 't') {
                        sepString.append("\\t");
                        i++;
                    }
                }
            } else {
                // a real, non-specific char
                sepString.append("[" + c + "]");
            }
        }
        return sepString.toString();
    }
    
    /**
     * @return the omitEmpty
     */
    public boolean getOmitEmpty() {
        return m_omitEmpty;
    }
    
    /**
     * @param omitEmpty the omitEmpty to set
     */
    public void setOmitEmpty(final boolean omitEmpty) {
        m_omitEmpty = omitEmpty;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_regex = settings.getString(CFG_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE);
        m_separator = settings.getString(CFG_SEPARATOR);
        m_omitEmpty = settings.getBoolean(CFG_OMIT_EMPTY);
        setDefaultValue(settings.getString(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_regex = settings.getString(CFG_REGEX, DEFAULT_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
        m_separator = settings.getString(CFG_SEPARATOR, DEFAULT_SEPARATOR);
        m_omitEmpty = settings.getBoolean(CFG_OMIT_EMPTY, DEFAULT_OMIT_EMPTY);
        setDefaultValue(settings.getString(CFG_DEFAULT, ""));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_REGEX, m_regex);
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
        settings.addString(CFG_SEPARATOR, m_separator);
        settings.addBoolean(CFG_OMIT_EMPTY, m_omitEmpty);
        settings.addString(CFG_DEFAULT, getDefaultValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<ListBoxInputQuickFormValue> createDialogPanel() {
        ListBoxInputQuickFormDialogPanel panel = new ListBoxInputQuickFormDialogPanel();
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
    public void resetNodeValueToDefault(final ListBoxInputQuickFormValue value) {
        value.setString(getDefaultValue());        
    }

}
