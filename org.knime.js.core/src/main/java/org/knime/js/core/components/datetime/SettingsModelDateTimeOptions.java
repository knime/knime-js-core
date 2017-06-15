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
import org.knime.core.node.port.PortObjectSpec;

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

    private String m_globalDateTimeLocale = DEFAULT_GLOBAL_DATE_TIME_LOCALE;
    private String m_globalDateTimeFormat      = DEFAULT_GLOBAL_DATE_TIME_FORMAT;
    private String m_globalLocalDateFormat     = DEFAULT_GLOBAL_LOCAL_DATE_FORMAT;
    private String m_globalLocalDateTimeFormat = DEFAULT_GLOBAL_LOCAL_DATE_TIME_FORMAT;
    private String m_globalLocalTimeFormat     = DEFAULT_GLOBAL_LOCAL_TIME_FORMAT;
    private String m_globalZonedDateTimeFormat = DEFAULT_GLOBAL_ZONED_DATE_TIME_FORMAT;
    private String m_timezone = DEFAULT_TIMEZONE;

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
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected SettingsModelDateTimeOptions createClone() {
        SettingsModelDateTimeOptions copy = new SettingsModelDateTimeOptions(m_configName);
        copy.setGlobalDateTimeLocale(m_globalDateTimeLocale);
        copy.setGlobalDateTimeFormat(m_globalDateTimeFormat);
        copy.setGlobalLocalDateTimeFormat(m_globalLocalDateTimeFormat);
        copy.setGlobalLocalDateFormat(m_globalLocalDateFormat);
        copy.setGlobalLocalTimeFormat(m_globalLocalTimeFormat);
        copy.setGlobalZonedDateTimeFormat(m_globalZonedDateTimeFormat);
        copy.setTimezone(m_timezone);
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
     * @return the globalDateTimeLocale
     */
    public String getGlobalDateTimeLocale() {
        return m_globalDateTimeLocale;
    }

    /**
     * @param globalDateTimeLocale the globalDateTimeLocale to set
     */
    public void setGlobalDateTimeLocale(final String globalDateTimeLocale) {
        String prevLocale = m_globalDateTimeLocale;
        m_globalDateTimeLocale = globalDateTimeLocale;
        if (prevLocale == null && m_globalDateTimeLocale != null || prevLocale != null && !prevLocale.equals(m_globalDateTimeLocale)) {
            notifyChangeListeners();
        }
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
        String prevFormat = m_globalDateTimeFormat;
        m_globalDateTimeFormat = globalDateTimeFormat;
        if (prevFormat == null && m_globalDateTimeFormat != null || prevFormat != null && !prevFormat.equals(m_globalDateTimeFormat)) {
            notifyChangeListeners();
        }
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
        String prevFormat = m_globalLocalDateFormat;
        m_globalLocalDateFormat = globalLocalDateFormat;
        if (prevFormat == null && m_globalLocalDateFormat != null || prevFormat != null && !prevFormat.equals(m_globalLocalDateFormat)) {
            notifyChangeListeners();
        }
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
        String prevFormat = m_globalLocalDateTimeFormat;
        m_globalLocalDateTimeFormat = globalLocalDateTimeFormat;
        if (prevFormat == null && m_globalLocalDateTimeFormat != null || prevFormat != null && !prevFormat.equals(m_globalLocalDateTimeFormat)) {
            notifyChangeListeners();
        }
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
        String prevFormat = m_globalLocalTimeFormat;
        m_globalLocalTimeFormat = globalLocalTimeFormat;
        if (prevFormat == null && m_globalLocalTimeFormat != null || prevFormat != null && !prevFormat.equals(m_globalLocalTimeFormat)) {
            notifyChangeListeners();
        }
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
        String prevFormat = m_globalZonedDateTimeFormat;
        m_globalZonedDateTimeFormat = globalZonedDateTimeFormat;
        if (prevFormat == null && m_globalZonedDateTimeFormat != null || prevFormat != null && !prevFormat.equals(m_globalZonedDateTimeFormat)) {
            notifyChangeListeners();
        }
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
        String prevTimezone = m_timezone;
        m_timezone = timezone;
        if (prevTimezone == null && m_timezone != null || prevTimezone != null && !prevTimezone.equals(m_timezone)) {
            notifyChangeListeners();
        }
    }

    /**
     * @return an object serializable as JSON string
     */
    public JSONDateTimeOptions getJSONSerializableObject() {
        JSONDateTimeOptions options = new JSONDateTimeOptions();
        options.setGlobalDateTimeLocale(getGlobalDateTimeLocale());
        options.setGlobalDateTimeFormat(getGlobalDateTimeFormat());
        options.setGlobalLocalDateFormat(getGlobalLocalDateFormat());
        options.setGlobalLocalDateTimeFormat(getGlobalLocalDateTimeFormat());
        options.setGlobalLocalTimeFormat(getGlobalLocalTimeFormat());
        options.setGlobalZonedDateTimeFormat(getGlobalZonedDateTimeFormat());
        options.setTimezone(getTimezone());
        return options;
    }

    /**
     * Sets the values from a JSON deserialized object.
     * @param options the JSON object
     */
    public void setFromJSON(final JSONDateTimeOptions options) {
        setGlobalDateTimeLocale(options.getGlobalDateTimeLocale());
        setGlobalDateTimeFormat(options.getGlobalDateTimeFormat());
        setGlobalLocalDateTimeFormat(options.getGlobalLocalDateTimeFormat());
        setGlobalLocalDateFormat(options.getGlobalLocalDateFormat());
        setGlobalLocalTimeFormat(options.getGlobalLocalTimeFormat());
        setGlobalZonedDateTimeFormat(options.getGlobalZonedDateTimeFormat());
        setTimezone(options.getTimezone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        NodeSettingsRO dateTimeSettings;
        try {
            dateTimeSettings = settings.getNodeSettings(m_configName);
        } catch (InvalidSettingsException e) {
            // if settings not found: keep the old value.
            return;
        }
        try {
            // use the current value, if no value is stored in the settings
            setGlobalDateTimeLocale(dateTimeSettings.getString(GLOBAL_DATE_TIME_LOCALE, m_globalDateTimeLocale));
            setGlobalDateTimeFormat(dateTimeSettings.getString(GLOBAL_DATE_TIME_FORMAT, m_globalDateTimeFormat));
            setGlobalLocalDateFormat(dateTimeSettings.getString(GLOBAL_LOCAL_DATE_FORMAT, m_globalLocalDateFormat));
            setGlobalLocalDateTimeFormat(dateTimeSettings.getString(GLOBAL_LOCAL_DATE_TIME_FORMAT, m_globalLocalDateTimeFormat));
            setGlobalLocalTimeFormat(dateTimeSettings.getString(GLOBAL_LOCAL_TIME_FORMAT, m_globalLocalTimeFormat));
            setGlobalZonedDateTimeFormat(dateTimeSettings.getString(GLOBAL_ZONED_DATE_TIME_FORMAT, m_globalZonedDateTimeFormat));
            setTimezone(dateTimeSettings.getString(TIMEZONE, m_timezone));
        } catch (final IllegalArgumentException iae) {
            // if the argument is not accepted: keep the old value.
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings) throws InvalidSettingsException {
        NodeSettingsWO dateTimeSettings = settings.addNodeSettings(m_configName);

        dateTimeSettings.addString(GLOBAL_DATE_TIME_LOCALE, getGlobalDateTimeLocale());
        dateTimeSettings.addString(GLOBAL_DATE_TIME_FORMAT, getGlobalDateTimeFormat());
        dateTimeSettings.addString(GLOBAL_LOCAL_DATE_FORMAT, getGlobalLocalDateFormat());
        dateTimeSettings.addString(GLOBAL_LOCAL_DATE_TIME_FORMAT, getGlobalLocalDateTimeFormat());
        dateTimeSettings.addString(GLOBAL_LOCAL_TIME_FORMAT, getGlobalLocalTimeFormat());
        dateTimeSettings.addString(GLOBAL_ZONED_DATE_TIME_FORMAT, getGlobalZonedDateTimeFormat());
        dateTimeSettings.addString(TIMEZONE, getTimezone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO dateTimeSettings = settings.getNodeSettings(m_configName);
        dateTimeSettings.getString(GLOBAL_DATE_TIME_LOCALE);
        dateTimeSettings.getString(GLOBAL_DATE_TIME_FORMAT);
        dateTimeSettings.getString(GLOBAL_LOCAL_DATE_FORMAT);
        dateTimeSettings.getString(GLOBAL_LOCAL_DATE_TIME_FORMAT);
        dateTimeSettings.getString(GLOBAL_LOCAL_TIME_FORMAT);
        dateTimeSettings.getString(GLOBAL_ZONED_DATE_TIME_FORMAT);
        dateTimeSettings.getString(TIMEZONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        try {
            NodeSettingsRO dateTimeSettings = settings.getNodeSettings(m_configName);
            // no default value, throw an exception instead
            setGlobalDateTimeLocale(dateTimeSettings.getString(GLOBAL_DATE_TIME_LOCALE));
            setGlobalDateTimeFormat(dateTimeSettings.getString(GLOBAL_DATE_TIME_FORMAT));
            setGlobalLocalDateFormat(dateTimeSettings.getString(GLOBAL_LOCAL_DATE_FORMAT));
            setGlobalLocalDateTimeFormat(dateTimeSettings.getString(GLOBAL_LOCAL_DATE_TIME_FORMAT));
            setGlobalLocalTimeFormat(dateTimeSettings.getString(GLOBAL_LOCAL_TIME_FORMAT));
            setGlobalZonedDateTimeFormat(dateTimeSettings.getString(GLOBAL_ZONED_DATE_TIME_FORMAT));
            setTimezone(dateTimeSettings.getString(TIMEZONE));
        } catch (final IllegalArgumentException iae) {
            throw new InvalidSettingsException(iae.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {
        NodeSettingsWO dateTimeSettings = settings.addNodeSettings(m_configName);

        dateTimeSettings.addString(GLOBAL_DATE_TIME_LOCALE, getGlobalDateTimeLocale());
        dateTimeSettings.addString(GLOBAL_DATE_TIME_FORMAT, getGlobalDateTimeFormat());
        dateTimeSettings.addString(GLOBAL_LOCAL_DATE_FORMAT, getGlobalLocalDateFormat());
        dateTimeSettings.addString(GLOBAL_LOCAL_DATE_TIME_FORMAT, getGlobalLocalDateTimeFormat());
        dateTimeSettings.addString(GLOBAL_LOCAL_TIME_FORMAT, getGlobalLocalTimeFormat());
        dateTimeSettings.addString(GLOBAL_ZONED_DATE_TIME_FORMAT, getGlobalZonedDateTimeFormat());
        dateTimeSettings.addString(TIMEZONE, getTimezone());
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
