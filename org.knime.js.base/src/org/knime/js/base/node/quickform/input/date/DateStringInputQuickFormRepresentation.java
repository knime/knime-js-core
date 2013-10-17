package org.knime.js.base.node.quickform.input.date;

import java.util.Date;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DateStringInputQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<DateStringInputQuickFormValue> {

    private static final String CFG_DEFAULT = "default";
    
    private static final Date DEFAULT_DATE = new Date();
    
    private Date m_defaultValue;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        String value = settings.getString(CFG_DEFAULT);
        try {
            setDefaultValue(DateStringInputQuickFormNodeModel.FORMAT.parse(value));
        } catch (Exception e) {
            throw new InvalidSettingsException("Can't parse date: " + value, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        String value = settings.getString(CFG_DEFAULT, DateStringInputQuickFormNodeModel.FORMAT.format(DEFAULT_DATE));
        try {
            setDefaultValue(DateStringInputQuickFormNodeModel.FORMAT.parse(value));
        } catch (Exception e) {
            m_defaultValue = DEFAULT_DATE;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_DEFAULT, DateStringInputQuickFormNodeModel.FORMAT.format(getDefaultValue()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<DateStringInputQuickFormValue> createDialogPanel() {
        DateStringInputQuickFormDialogPanel panel = new DateStringInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the default
     */
    public Date getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the default to set
     */
    public void setDefaultValue(final Date defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNodeValueToDefault(final DateStringInputQuickFormValue value) {
        value.setDate(getDefaultValue());        
    }

}
