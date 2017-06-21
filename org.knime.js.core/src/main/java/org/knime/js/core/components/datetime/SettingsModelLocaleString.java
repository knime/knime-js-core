/**
 *
 */
package org.knime.js.core.components.datetime;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;

/**
 * @author Oleg Yasnev, KNIME.com GmbH, Berlin, Germany
 *
 */
public class SettingsModelLocaleString extends SettingsModelString {

    /**
     * Creates a new object with locale settings.
     *
     * @param configName the identifier the value is stored with in the {@link org.knime.core.node.NodeSettings} object
     * @param localeCode code of the locale, e.g. "en"
     */
    public SettingsModelLocaleString(final String configName, final String localeCode) {
        super(configName, "");
        setLocaleCode(localeCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SettingsModelLocaleString createClone() {
        return new SettingsModelLocaleString(getConfigName(), getLocaleName());
    }

    /**
     * Return code of the locale stored (e.g. "en") in the model
     * @return code of the locale
     */
    public String getLocaleCode() {
        String localeName = getStringValue();
        return SettingsModelDateTimeOptions.PREDEFINED_DATE_TIME_LOCALES.inverse().get(localeName);
    }

    /**
     * Set the code of the locale (e.g. "en") to store in the model
     * @param localeCode code of the locale
     */
    public void setLocaleCode(final String localeCode) {
        String localeName = SettingsModelDateTimeOptions.PREDEFINED_DATE_TIME_LOCALES.get(localeCode);
        setStringValue(localeName);
    }

    /**
     * Return the human name of the locale, e.g. "English (United States)"
     * @return name of the locale
     */
    public String getLocaleName() {
        return getStringValue();
    }

    /**
     * Set the human name of the locale, e.g. "English (United States)"
     * @param localeName name of the locale
     */
    public void setLocaleName(final String localeName) {
        setStringValue(localeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
        try {
            // use the current value, if no value is stored in the settings
            String localeCode = settings.getString(getConfigName(), null);
            if (localeCode != null) {
                setLocaleCode(localeCode);
            }
        } catch (final IllegalArgumentException iae) {
            // if the argument is not accepted: keep the old value.
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        settings.addString(getConfigName(), getLocaleCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        try {
            // no default value, throw an exception instead
            String localeCode = settings.getString(getConfigName());
            setLocaleCode(localeCode);
        } catch (final IllegalArgumentException iae) {
            throw new InvalidSettingsException(iae.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {
        settings.addString(getConfigName(), getLocaleCode());
    }
}
