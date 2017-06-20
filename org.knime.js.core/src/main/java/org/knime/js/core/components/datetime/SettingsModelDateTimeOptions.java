/**
 *
 */
package org.knime.js.core.components.datetime;

import java.util.TimeZone;

import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.StringHistory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Oleg Yasnev, KNIME.com GmbH, Berlin, Germany
 */
public class SettingsModelDateTimeOptions extends SettingsModel {
    static final String GLOBAL_DATE_TIME_LOCALE       = "globalDateTimeLocale";
    static final String GLOBAL_DATE_TIME_FORMAT       = "globalDateFormat";
    static final String GLOBAL_LOCAL_DATE_FORMAT      = "globalLocalDateFormat";
    static final String GLOBAL_LOCAL_DATE_TIME_FORMAT = "globalLocalDateTimeFormat";
    static final String GLOBAL_LOCAL_TIME_FORMAT      = "globalLocalTimeFormat";
    static final String GLOBAL_ZONED_DATE_TIME_FORMAT = "globalZonedDateTimeFormat";
    static final String TIMEZONE = "timezone";

    static final String DEFAULT_GLOBAL_DATE_TIME_LOCALE = "en";
    static final String DEFAULT_GLOBAL_DATE_TIME_FORMAT       = DialogComponentDateTimeOptions.PREDEFINED_DATE_TIME_FORMATS.iterator().next();
    static final String DEFAULT_GLOBAL_LOCAL_DATE_FORMAT      = DialogComponentDateTimeOptions.PREDEFINED_LOCAL_DATE_FORMATS.iterator().next();
    static final String DEFAULT_GLOBAL_LOCAL_DATE_TIME_FORMAT = DialogComponentDateTimeOptions.PREDEFINED_LOCAL_DATE_TIME_FORMATS.iterator().next();
    static final String DEFAULT_GLOBAL_LOCAL_TIME_FORMAT      = DialogComponentDateTimeOptions.PREDEFINED_LOCAL_TIME_FORMATS.iterator().next();
    static final String DEFAULT_GLOBAL_ZONED_DATE_TIME_FORMAT = DialogComponentDateTimeOptions.PREDEFINED_ZONED_DATE_TIME_FORMATS.iterator().next();
    static final String DEFAULT_TIMEZONE = TimeZone.getDefault().getID();

    private SettingsModelLocaleString m_globalDateTimeLocaleModel;
    private SettingsModelString m_globalDateTimeFormatModel;
    private SettingsModelString m_globalLocalDateFormatModel;
    private SettingsModelString m_globalLocalDateTimeFormatModel;
    private SettingsModelString m_globalLocalTimeFormatModel;
    private SettingsModelString m_globalZonedDateTimeFormatModel;
    private SettingsModelString m_timezoneModel;

    private final String m_configName;

    /**
     * Creates a new object with date and time settings. Default settings values are used.
     *
     * @param configName the identifier the value is stored with in the {@link org.knime.core.node.NodeSettings} object
     */
    public SettingsModelDateTimeOptions(final String configName) {
        if ((configName == null) || "".equals(configName)) {
            throw new IllegalArgumentException("The configName must be a " + "non-empty string");
        }
        m_configName = configName;

        m_globalDateTimeLocaleModel = new SettingsModelLocaleString(GLOBAL_DATE_TIME_LOCALE, DEFAULT_GLOBAL_DATE_TIME_LOCALE);
        m_globalDateTimeFormatModel      = new SettingsModelString(GLOBAL_DATE_TIME_FORMAT,       DEFAULT_GLOBAL_DATE_TIME_FORMAT);
        m_globalLocalDateFormatModel     = new SettingsModelString(GLOBAL_LOCAL_DATE_FORMAT,      DEFAULT_GLOBAL_LOCAL_DATE_FORMAT);
        m_globalLocalDateTimeFormatModel = new SettingsModelString(GLOBAL_LOCAL_DATE_TIME_FORMAT, DEFAULT_GLOBAL_LOCAL_DATE_TIME_FORMAT);
        m_globalLocalTimeFormatModel     = new SettingsModelString(GLOBAL_LOCAL_TIME_FORMAT,      DEFAULT_GLOBAL_LOCAL_TIME_FORMAT);
        m_globalZonedDateTimeFormatModel = new SettingsModelString(GLOBAL_ZONED_DATE_TIME_FORMAT, DEFAULT_GLOBAL_ZONED_DATE_TIME_FORMAT);
        m_timezoneModel = new SettingsModelString(TIMEZONE, DEFAULT_TIMEZONE);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected SettingsModelDateTimeOptions createClone() {
        SettingsModelDateTimeOptions copy = new SettingsModelDateTimeOptions(m_configName);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModelTypeID() {
        return "SMID_datetime";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getConfigName() {
        return m_configName;
    }

    /**
     * @return the globalDateTimeLocaleModel
     */
    public SettingsModelLocaleString getGlobalDateTimeLocaleModel() {
        return m_globalDateTimeLocaleModel;
    }

    /**
     * @param globalDateTimeLocaleModel the globalDateTimeLocaleModel to set
     */
    public void setGlobalDateTimeLocaleModel(final SettingsModelLocaleString globalDateTimeLocaleModel) {
        m_globalDateTimeLocaleModel = globalDateTimeLocaleModel;
    }

    /**
     * @return the globalDateTimeFormatModel
     */
    public SettingsModelString getGlobalDateTimeFormatModel() {
        return m_globalDateTimeFormatModel;
    }

    /**
     * @param globalDateTimeFormatModel the globalDateTimeFormatModel to set
     */
    public void setGlobalDateTimeFormatModel(final SettingsModelString globalDateTimeFormatModel) {
        m_globalDateTimeFormatModel = globalDateTimeFormatModel;
    }

    /**
     * @return the globalLocalDateFormatModel
     */
    public SettingsModelString getGlobalLocalDateFormatModel() {
        return m_globalLocalDateFormatModel;
    }

    /**
     * @param globalLocalDateFormatModel the globalLocalDateFormatModel to set
     */
    public void setGlobalLocalDateFormatModel(final SettingsModelString globalLocalDateFormatModel) {
        m_globalLocalDateFormatModel = globalLocalDateFormatModel;
    }

    /**
     * @return the globalLocalDateTimeFormatModel
     */
    public SettingsModelString getGlobalLocalDateTimeFormatModel() {
        return m_globalLocalDateTimeFormatModel;
    }

    /**
     * @param globalLocalDateTimeFormatModel the globalLocalDateTimeFormatModel to set
     */
    public void setGlobalLocalDateTimeFormatModel(final SettingsModelString globalLocalDateTimeFormatModel) {
        m_globalLocalDateTimeFormatModel = globalLocalDateTimeFormatModel;
    }

    /**
     * @return the globalLocalTimeFormatModel
     */
    public SettingsModelString getGlobalLocalTimeFormatModel() {
        return m_globalLocalTimeFormatModel;
    }

    /**
     * @param globalLocalTimeFormatModel the globalLocalTimeFormatModel to set
     */
    public void setGlobalLocalTimeFormatModel(final SettingsModelString globalLocalTimeFormatModel) {
        m_globalLocalTimeFormatModel = globalLocalTimeFormatModel;
    }

    /**
     * @return the globalZonedDateTimeFormatModel
     */
    public SettingsModelString getGlobalZonedDateTimeFormatModel() {
        return m_globalZonedDateTimeFormatModel;
    }

    /**
     * @param globalZonedDateTimeFormatModel the globalZonedDateTimeFormatModel to set
     */
    public void setGlobalZonedDateTimeFormatModel(final SettingsModelString globalZonedDateTimeFormatModel) {
        m_globalZonedDateTimeFormatModel = globalZonedDateTimeFormatModel;
    }

    /**
     * @return the timezoneModel
     */
    public SettingsModelString getTimezoneModel() {
        return m_timezoneModel;
    }

    /**
     * @param timezoneModel the timezoneModel to set
     */
    public void setTimezoneModel(final SettingsModelString timezoneModel) {
        m_timezoneModel = timezoneModel;
    }

    /**
     * @return an object serializable as JSON string
     */
    public JSONDateTimeOptions getJSONSerializableObject() {
        JSONDateTimeOptions options = new JSONDateTimeOptions();
        options.setGlobalDateTimeLocale(getGlobalDateTimeLocaleModel().getLocaleCode());
        options.setGlobalDateTimeFormat(getGlobalDateTimeFormatModel().getStringValue());
        options.setGlobalLocalDateFormat(getGlobalLocalDateFormatModel().getStringValue());
        options.setGlobalLocalDateTimeFormat(getGlobalLocalDateTimeFormatModel().getStringValue());
        options.setGlobalLocalTimeFormat(getGlobalLocalTimeFormatModel().getStringValue());
        options.setGlobalZonedDateTimeFormat(getGlobalZonedDateTimeFormatModel().getStringValue());
        options.setTimezone(getTimezoneModel().getStringValue());
        return options;
    }

    /**
     * Sets the values from a JSON deserialized object.
     * @param options the JSON object
     */
    public void setFromJSON(final JSONDateTimeOptions options) {
        getGlobalDateTimeLocaleModel().setLocaleCode(options.getGlobalDateTimeLocale());
        getGlobalDateTimeFormatModel().setStringValue(options.getGlobalDateTimeFormat());
        getGlobalLocalDateTimeFormatModel().setStringValue(options.getGlobalLocalDateTimeFormat());
        getGlobalLocalDateFormatModel().setStringValue(options.getGlobalLocalDateFormat());
        getGlobalLocalTimeFormatModel().setStringValue(options.getGlobalLocalTimeFormat());
        getGlobalZonedDateTimeFormatModel().setStringValue(options.getGlobalZonedDateTimeFormat());
        getTimezoneModel().setStringValue(options.getTimezone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        try {
            loadSettings(settings);
        } catch (InvalidSettingsException e) {
            // if settings not found: keep the old value.
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO dateTimeSettings = settings.getNodeSettings(m_configName);

        getGlobalDateTimeLocaleModel().validateSettings(dateTimeSettings);
        getGlobalDateTimeFormatModel().validateSettings(dateTimeSettings);
        getGlobalLocalDateFormatModel().validateSettings(dateTimeSettings);
        getGlobalLocalDateTimeFormatModel().validateSettings(dateTimeSettings);
        getGlobalLocalTimeFormatModel().validateSettings(dateTimeSettings);
        getGlobalZonedDateTimeFormatModel().validateSettings(dateTimeSettings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {
        saveSettings(settings);
    }

    private void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO dateTimeSettings = settings.getNodeSettings(m_configName);

        getGlobalDateTimeLocaleModel().loadSettingsFrom(dateTimeSettings);
        getGlobalDateTimeFormatModel().loadSettingsFrom(dateTimeSettings);
        getGlobalLocalDateFormatModel().loadSettingsFrom(dateTimeSettings);
        getGlobalLocalDateTimeFormatModel().loadSettingsFrom(dateTimeSettings);
        getGlobalLocalTimeFormatModel().loadSettingsFrom(dateTimeSettings);
        getGlobalZonedDateTimeFormatModel().loadSettingsFrom(dateTimeSettings);
        getTimezoneModel().loadSettingsFrom(dateTimeSettings);
    }

    private void saveSettings(final NodeSettingsWO settings) {
        NodeSettingsWO dateTimeSettings = settings.addNodeSettings(m_configName);

        getGlobalDateTimeLocaleModel().saveSettingsTo(dateTimeSettings);
        getGlobalDateTimeFormatModel().saveSettingsTo(dateTimeSettings);
        getGlobalLocalDateFormatModel().saveSettingsTo(dateTimeSettings);
        getGlobalLocalDateTimeFormatModel().saveSettingsTo(dateTimeSettings);
        getGlobalLocalTimeFormatModel().saveSettingsTo(dateTimeSettings);
        getGlobalZonedDateTimeFormatModel().saveSettingsTo(dateTimeSettings);
        getTimezoneModel().saveSettingsTo(dateTimeSettings);

        StringHistory.getInstance(DialogComponentDateTimeOptions.DATE_TIME_FORMAT_HISTORY_KEY).add(getGlobalDateTimeFormatModel().getStringValue());
        StringHistory.getInstance(DialogComponentDateTimeOptions.DATE_FORMAT_HISTORY_KEY).add(getGlobalLocalDateFormatModel().getStringValue());
        StringHistory.getInstance(DialogComponentDateTimeOptions.DATE_TIME_FORMAT_HISTORY_KEY).add(getGlobalLocalDateTimeFormatModel().getStringValue());
        StringHistory.getInstance(DialogComponentDateTimeOptions.TIME_FORMAT_HISTORY_KEY).add(getGlobalLocalTimeFormatModel().getStringValue());
        StringHistory.getInstance(DialogComponentDateTimeOptions.ZONED_DATE_TIME_FORMAT_HISTORY_KEY).add(getGlobalZonedDateTimeFormatModel().getStringValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " ('" + m_configName + "')";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prependChangeListener(final ChangeListener l) {
        // make method visible in this package
        super.prependChangeListener(l);
    }

    /**
     * Wrapper for JSON serialization
     * @author Oleg Yasnev, KNIME.com GmbH, Berlin, Germany
     */
    @JsonAutoDetect
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    public static class JSONDateTimeOptions {
        String m_globalDateTimeLocale;
        String m_globalDateTimeFormat;
        String m_globalLocalDateFormat;
        String m_globalLocalDateTimeFormat;
        String m_globalLocalTimeFormat;
        String m_globalZonedDateTimeFormat;
        String m_timezone;

        /**
         * @return the globalDateTimeLocale
         */
        public String getGlobalDateTimeLocale() {
            return m_globalDateTimeLocale;
        }

        /**
         * @param globalDateTimeLocale the globalDateTimeLocale to set
         */
        public void setGlobalDateTimeLocale(final String globalDateTimeLocale) {
            m_globalDateTimeLocale = globalDateTimeLocale;
        }

        /**
         * @return the globalDateTimeFormat
         */
        public String getGlobalDateTimeFormat() {
            return m_globalDateTimeFormat;
        }

        /**
         * @param globalDateTimeFormat the globalDateTimeFormat to set
         */
        public void setGlobalDateTimeFormat(final String globalDateTimeFormat) {
            m_globalDateTimeFormat = globalDateTimeFormat;
        }

        /**
         * @return the globalLocalDateTimeFormat
         */
        public String getGlobalLocalDateTimeFormat() {
            return m_globalLocalDateTimeFormat;
        }

        /**
         * @param globalLocalDateTimeFormat the globalLocalDateTimeFormat to set
         */
        public void setGlobalLocalDateTimeFormat(final String globalLocalDateTimeFormat) {
            m_globalLocalDateTimeFormat = globalLocalDateTimeFormat;
        }

        /**
         * @return the globalLocalDateFormat
         */
        public String getGlobalLocalDateFormat() {
            return m_globalLocalDateFormat;
        }
        /**
         * @param globalLocalDateFormat the globalLocalDateFormat to set
         */
        public void setGlobalLocalDateFormat(final String globalLocalDateFormat) {
            m_globalLocalDateFormat = globalLocalDateFormat;
        }

        /**
         * @return the globalLocalTimeFormat
         */
        public String getGlobalLocalTimeFormat() {
            return m_globalLocalTimeFormat;
        }

        /**
         * @param globalLocalTimeFormat the globalLocalTimeFormat to set
         */
        public void setGlobalLocalTimeFormat(final String globalLocalTimeFormat) {
            m_globalLocalTimeFormat = globalLocalTimeFormat;
        }

        /**
         * @return the globalZonedDateTimeFormat
         */
        public String getGlobalZonedDateTimeFormat() {
            return m_globalZonedDateTimeFormat;
        }
        /**
         * @param globalZonedDateTimeFormat the globalZonedDateTimeFormat to set
         */
        public void setGlobalZonedDateTimeFormat(final String globalZonedDateTimeFormat) {
            m_globalZonedDateTimeFormat = globalZonedDateTimeFormat;
        }

        /**
         * @return the timezone
         */
        public String getTimezone() {
            return m_timezone;
        }

        /**
         * @param timezone the timezone to set
         */
        public void setTimezone(final String timezone) {
            m_timezone = timezone;
        }

        /**
         * @param settings
         */
        public void saveToNodeSettings(final NodeSettingsWO settings) {
            settings.addString(GLOBAL_DATE_TIME_LOCALE, getGlobalDateTimeLocale());
            settings.addString(GLOBAL_DATE_TIME_FORMAT, getGlobalDateTimeFormat());
            settings.addString(GLOBAL_LOCAL_DATE_FORMAT, getGlobalLocalDateFormat());
            settings.addString(GLOBAL_LOCAL_DATE_TIME_FORMAT, getGlobalLocalDateTimeFormat());
            settings.addString(GLOBAL_LOCAL_TIME_FORMAT, getGlobalLocalTimeFormat());
            settings.addString(GLOBAL_ZONED_DATE_TIME_FORMAT, getGlobalZonedDateTimeFormat());
            settings.addString(TIMEZONE, getTimezone());
        }

        /**
         * @param settings
         * @return JSONDateTimeOptions with values copied from settings
         * @throws InvalidSettingsException
         */
        public static JSONDateTimeOptions loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            JSONDateTimeOptions options = new JSONDateTimeOptions();
            options.setGlobalDateTimeLocale(settings.getString(GLOBAL_DATE_TIME_LOCALE));
            options.setGlobalDateTimeFormat(settings.getString(GLOBAL_DATE_TIME_FORMAT));
            options.setGlobalLocalDateFormat(settings.getString(GLOBAL_LOCAL_DATE_FORMAT));
            options.setGlobalLocalDateTimeFormat(settings.getString(GLOBAL_LOCAL_DATE_TIME_FORMAT));
            options.setGlobalLocalTimeFormat(settings.getString(GLOBAL_LOCAL_TIME_FORMAT));
            options.setGlobalZonedDateTimeFormat(settings.getString(GLOBAL_ZONED_DATE_TIME_FORMAT));
            options.setTimezone(settings.getString(TIMEZONE));
            return options;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            JSONDateTimeOptions other = (JSONDateTimeOptions)obj;
            return new EqualsBuilder()
                    .append(m_globalDateTimeLocale, other.m_globalDateTimeLocale)
                    .append(m_globalDateTimeFormat, other.m_globalDateTimeFormat)
                    .append(m_globalLocalDateFormat, other.m_globalLocalDateFormat)
                    .append(m_globalLocalDateTimeFormat, other.m_globalLocalDateTimeFormat)
                    .append(m_globalLocalTimeFormat, other.m_globalLocalTimeFormat)
                    .append(m_globalZonedDateTimeFormat, other.m_globalZonedDateTimeFormat)
                    .append(m_timezone, other.m_timezone)
                    .isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(m_globalDateTimeLocale)
                    .append(m_globalDateTimeFormat)
                    .append(m_globalLocalDateFormat)
                    .append(m_globalLocalDateTimeFormat)
                    .append(m_globalLocalTimeFormat)
                    .append(m_globalZonedDateTimeFormat)
                    .append(m_timezone)
                    .toHashCode();
        }
    }

}
