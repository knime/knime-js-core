package org.knime.js.base.node.quickform.input.date;

import java.util.Date;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DateStringInputQuickFormViewRepresentation extends JSONViewContent {

    private static final String CFG_DEFAULT = "default";
    
    private Date m_defaultValue;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
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
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_DEFAULT, DateStringInputQuickFormNodeModel.FORMAT.format(getDefaultValue()));
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

}
