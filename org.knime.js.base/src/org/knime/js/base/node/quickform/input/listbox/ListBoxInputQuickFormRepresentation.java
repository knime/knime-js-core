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
    
    private static final String CFG_SEPARATOR = "separator";
    
    private static final String DEFAULT_SEPARATOR = ",";
    
    private String m_separator = DEFAULT_SEPARATOR;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue;
    
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
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_separator = settings.getString(CFG_SEPARATOR);
        setDefaultValue(settings.getString(CFG_DEFAULT));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_separator = settings.getString(CFG_SEPARATOR, DEFAULT_SEPARATOR);
        setDefaultValue(settings.getString(CFG_DEFAULT, ""));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_SEPARATOR, m_separator);
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
