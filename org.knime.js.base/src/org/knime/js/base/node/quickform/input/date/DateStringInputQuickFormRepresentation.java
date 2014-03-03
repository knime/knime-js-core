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
    
    private static final String CFG_USE_MIN = "use_min";
    
    private static final boolean DEFAULT_USE_MIN = false;
    
    private boolean m_useMin = DEFAULT_USE_MIN;
    
    private static final String CFG_USE_MAX = "use_max";
    
    private static final boolean DEFAULT_USE_MAX = false;
    
    private boolean m_useMax = DEFAULT_USE_MAX;
    
    private static final String CFG_MIN = "min";
    
    private static final Date DEFAULT_MIN = DateStringInputQuickFormValue.DEFAULT_DATE;
    
    private Date m_min = DEFAULT_MIN;
    
    private static final String CFG_MAX = "max";
    
    private static final Date DEFAULT_MAX = DateStringInputQuickFormValue.DEFAULT_DATE;
    
    private Date m_max = DEFAULT_MAX;

    private static final String CFG_DEFAULT = "default";
    
    private static final Date DEFAULT_DATE = DateStringInputQuickFormValue.DEFAULT_DATE;
    
    private Date m_defaultValue = DEFAULT_DATE;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_useMin = settings.getBoolean(CFG_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX);
        String value = settings.getString(CFG_DEFAULT);
        String min = settings.getString(CFG_MIN);
        String max = settings.getString(CFG_MAX);
        try {
            setDefaultValue(DateStringInputQuickFormNodeModel.FORMAT.parse(value));
            m_min = DateStringInputQuickFormNodeModel.FORMAT.parse(min);
            m_max = DateStringInputQuickFormNodeModel.FORMAT.parse(max);
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
        m_useMin = settings.getBoolean(CFG_USE_MIN, DEFAULT_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX, DEFAULT_USE_MAX);
        String value = settings.getString(CFG_DEFAULT, DateStringInputQuickFormNodeModel.FORMAT.format(DEFAULT_DATE));
        String min = settings.getString(CFG_MIN, DateStringInputQuickFormNodeModel.FORMAT.format(DEFAULT_MIN));
        String max = settings.getString(CFG_MAX, DateStringInputQuickFormNodeModel.FORMAT.format(DEFAULT_MAX));
        try {
            setDefaultValue(DateStringInputQuickFormNodeModel.FORMAT.parse(value));
            m_min = DateStringInputQuickFormNodeModel.FORMAT.parse(min);
            m_max = DateStringInputQuickFormNodeModel.FORMAT.parse(max);
        } catch (Exception e) {
            m_defaultValue = DEFAULT_DATE;
            m_min = DEFAULT_MIN;
            m_max = DEFAULT_MAX;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addBoolean(CFG_USE_MIN, m_useMin);
        settings.addBoolean(CFG_USE_MAX, m_useMax);
        settings.addString(CFG_DEFAULT, DateStringInputQuickFormNodeModel.FORMAT.format(getDefaultValue()));
        settings.addString(CFG_MIN, DateStringInputQuickFormNodeModel.FORMAT.format(m_min));
        settings.addString(CFG_MAX, DateStringInputQuickFormNodeModel.FORMAT.format(m_max));
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

    /**
     * @return the useMin
     */
    public boolean getUseMin() {
        return m_useMin;
    }

    /**
     * @param useMin the useMin to set
     */
    public void setUseMin(final boolean useMin) {
        m_useMin = useMin;
    }

    /**
     * @return the useMax
     */
    public boolean getUseMax() {
        return m_useMax;
    }

    /**
     * @param useMax the useMax to set
     */
    public void setUseMax(final boolean useMax) {
        m_useMax = useMax;
    }

    /**
     * @return the min
     */
    public Date getMin() {
        return m_min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(final Date min) {
        m_min = min;
    }

    /**
     * @return the max
     */
    public Date getMax() {
        return m_max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(final Date max) {
        m_max = max;
    }

}
