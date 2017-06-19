/**
 *
 */
package org.knime.js.core.components.datetime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.StringHistory;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

/**
 * @author Oleg Yasnev, KNIME.com GmbH, Berlin, Germany
 *
 */
public class DialogComponentDateTimeOptions extends DialogComponent {

    /** BiMap of locale keys and locale values as supported by moment.js */
    public static final BiMap<String, String> PREDEFINED_DATE_TIME_LOCALES = loadDateTimeLocales();

    // Keys for the string history to re-use user entered date formats.

    /**
     * New and old date and time format history key
     */
    public static final String DATE_TIME_FORMAT_HISTORY_KEY = "momentjs-date-formats";
    /**
     * Date only format history key
     */
    public static final String DATE_FORMAT_HISTORY_KEY = "momentjs-date-new-formats";
    /**
     * Time only format history key
     */
    public static final String TIME_FORMAT_HISTORY_KEY = "momentjs-time-formats";
    /**
     * Zoned date and time format history key
     */
    public static final String ZONED_DATE_TIME_FORMAT_HISTORY_KEY = "momentjs-zoned-date-time-formats";

    // Sets of predefined date and time formats for JavaScript processing with moment.js.

    /**
     * Old date and time formats
     */
    public static final LinkedHashSet<String> PREDEFINED_DATE_TIME_FORMATS = createPredefinedDateTimeFormats();
    /**
     * Date only formats
     */
    public static final LinkedHashSet<String> PREDEFINED_LOCAL_DATE_FORMATS = createPredefinedLocalDateFormats();
    /**
     * New date and time formats
     */
    public static final LinkedHashSet<String> PREDEFINED_LOCAL_DATE_TIME_FORMATS = createPredefinedLocalDateTimeFormats();
    /**
     * Time only formats
     */
    public static final LinkedHashSet<String> PREDEFINED_LOCAL_TIME_FORMATS = createPredefinedLocalTimeFormats();
    /**
     * Zoned date and time formats
     */
    public static final LinkedHashSet<String> PREDEFINED_ZONED_DATE_TIME_FORMATS = createPredefinedZonedDateTimeFormats();

    private DialogComponentStringSelection m_globalDateTimeLocaleChooser;
    private DialogComponentStringSelection m_globalDateTimeFormatChooser;
    private DialogComponentStringSelection m_globalLocalDateFormatChooser;
    private DialogComponentStringSelection m_globalLocalDateTimeFormatChooser;
    private DialogComponentStringSelection m_globalLocalTimeFormatChooser;
    private DialogComponentStringSelection m_globalZonedDateTimeFormatChooser;
    private DialogComponentStringSelection m_timezoneChooser;

    private final DialogComponentDateTimeOptions.Config m_config;

    private JPanel m_panel;

    private static final int FORMAT_CHOOSER_WIDTH = 235;
    private static final int FORMAT_CHOOSER_HEIGHT = 17;

    /**
     * Creates a date and time component with all controls
     * @param model
     * @param label
     */
    public DialogComponentDateTimeOptions(final SettingsModelDateTimeOptions model, final String label) {
        this(model, label, new DialogComponentDateTimeOptions.Config());
    }

    /**
     * Creates a date and time component with controls specified in config
     * @param model
     * @param label
     * @param config
     */
    public DialogComponentDateTimeOptions(final SettingsModelDateTimeOptions model, final String label, final DialogComponentDateTimeOptions.Config config) {
        super(model);

        m_config = config;

        if (m_config.getShowLocaleChooser()) {
            m_globalDateTimeLocaleChooser = new DialogComponentStringSelection(
                model.getGlobalDateTimeLocaleModel(), "", PREDEFINED_DATE_TIME_LOCALES.values(), true);
        }

        if (m_config.getShowLegacyDateTimeFormatChooser()) {
            m_globalDateTimeFormatChooser =
                    new DialogComponentStringSelection(model.getGlobalDateTimeFormatModel(), "", PREDEFINED_DATE_TIME_FORMATS, true);
            m_globalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        }

        if (m_config.getShowDateFormatChooser()) {
            m_globalLocalDateFormatChooser =
                    new DialogComponentStringSelection(model.getGlobalLocalDateFormatModel(), "", PREDEFINED_LOCAL_DATE_FORMATS, true);
            m_globalLocalDateFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        }

        if (m_config.getShowDateTimeFormatChooser()) {
            m_globalLocalDateTimeFormatChooser =
                    new DialogComponentStringSelection(model.getGlobalLocalDateTimeFormatModel(), "", PREDEFINED_LOCAL_DATE_TIME_FORMATS, true);
            m_globalLocalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        }

        if (m_config.getShowTimeFormatChooser()) {
            m_globalLocalTimeFormatChooser =
                    new DialogComponentStringSelection(model.getGlobalLocalTimeFormatModel(), "", PREDEFINED_LOCAL_TIME_FORMATS, true);
            m_globalLocalTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        }

        if (m_config.getShowZonedDateTimeFormatChooser()) {
            m_globalZonedDateTimeFormatChooser =
                new DialogComponentStringSelection(model.getGlobalZonedDateTimeFormatModel(), "", PREDEFINED_ZONED_DATE_TIME_FORMATS, true);
            m_globalZonedDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

            if (m_config.getShowTimezoneChooser()) {
                m_timezoneChooser =
                        new DialogComponentStringSelection(model.getTimezoneModel(), "", new LinkedHashSet<String>(Arrays.asList(TimeZone.getAvailableIDs())), false);
                m_timezoneChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
            }
        }

        initPanel(label);

        // update the inputs, whenever the model changes
        model.prependChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateComponent();
            }
        });

        updateComponent();
    }



    /**
     * Copies settings from the provided model
     * @param model
     */
    public void loadSettingsFromModel(final SettingsModelDateTimeOptions model) {
        SettingsModelDateTimeOptions thisModel = (SettingsModelDateTimeOptions) getModel();
        thisModel.getGlobalDateTimeLocaleModel().setStringValue(model.getGlobalDateTimeLocaleModel().getStringValue());
        thisModel.getGlobalDateTimeFormatModel().setStringValue(model.getGlobalDateTimeFormatModel().getStringValue());
        thisModel.getGlobalLocalDateTimeFormatModel().setStringValue(model.getGlobalLocalDateTimeFormatModel().getStringValue());
        thisModel.getGlobalLocalDateFormatModel().setStringValue(model.getGlobalLocalDateFormatModel().getStringValue());
        thisModel.getGlobalLocalTimeFormatModel().setStringValue(model.getGlobalLocalTimeFormatModel().getStringValue());
        thisModel.getGlobalZonedDateTimeFormatModel().setStringValue(model.getGlobalZonedDateTimeFormatModel().getStringValue());
        thisModel.getTimezoneModel().setStringValue(model.getTimezoneModel().getStringValue());
        updateComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateComponent() {
        setEnabledComponents(getModel().isEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsBeforeSave() throws InvalidSettingsException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] specs) throws NotConfigurableException {
        // always ok
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEnabledComponents(final boolean enabled) {
        if (m_config.getShowLocaleChooser()) {
            m_globalDateTimeLocaleChooser.getModel().setEnabled(enabled);
        }
        if (m_config.getShowLegacyDateTimeFormatChooser()) {
            m_globalDateTimeFormatChooser.getModel().setEnabled(enabled);
        }
        if (m_config.getShowDateFormatChooser()) {
            m_globalLocalDateFormatChooser.getModel().setEnabled(enabled);
        }
        if (m_config.getShowDateTimeFormatChooser()) {
            m_globalLocalDateTimeFormatChooser.getModel().setEnabled(enabled);
        }
        if (m_config.getShowTimeFormatChooser()) {
            m_globalLocalTimeFormatChooser.getModel().setEnabled(enabled);
        }
        if (m_config.getShowZonedDateTimeFormatChooser()) {
            m_globalZonedDateTimeFormatChooser.getModel().setEnabled(enabled);
            if (m_config.getShowTimezoneChooser()) {
                m_timezoneChooser.getModel().setEnabled(enabled);
                setTimezoneChooserState();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTipText(final String text) {
        getComponentPanel().setToolTipText(text);
    }

    /**
     * @return the panel
     */
    public JPanel getPanel() {
        return m_panel;
    }

    private void initPanel(final String label) {
        m_panel = getComponentPanel();
        m_panel.setLayout(new GridBagLayout());
        m_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory
            .createEtchedBorder(), label));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;

        c.gridx = 0;
        c.gridy = 0;
        if (m_config.getShowLocaleChooser()) {
            m_panel.add(new JLabel("Locale: "), c);
            c.gridx++;
            m_panel.add(m_globalDateTimeLocaleChooser.getComponentPanel(), c);
            c.gridx = 0;
            c.gridy++;
        }
        if (m_config.getShowDateFormatChooser()) {
            m_panel.add(new JLabel("Local Date format: "), c);
            c.gridx++;
            m_panel.add(m_globalLocalDateFormatChooser.getComponentPanel(), c);
            c.gridx = 0;
            c.gridy++;
        }
        if (m_config.getShowDateTimeFormatChooser()) {
            m_panel.add(new JLabel("Local Date&Time format: "), c);
            c.gridx++;
            m_panel.add(m_globalLocalDateTimeFormatChooser.getComponentPanel(), c);
            c.gridx = 0;
            c.gridy++;
        }
        if (m_config.getShowTimeFormatChooser()) {
            m_panel.add(new JLabel("Local Time format: "), c);
            c.gridx++;
            m_panel.add(m_globalLocalTimeFormatChooser.getComponentPanel(), c);
            c.gridx = 0;
            c.gridy++;
        }
        if (m_config.getShowZonedDateTimeFormatChooser()) {
            m_panel.add(new JLabel("Zoned Date&Time format: "), c);
            c.gridx++;
            m_panel.add(m_globalZonedDateTimeFormatChooser.getComponentPanel(), c);
            c.gridx = 0;
            c.gridy++;
            if (m_config.getShowTimezoneChooser()) {
                m_panel.add(new JLabel("Time zone (for zoned format): "), c);
                c.gridx++;
                m_panel.add(m_timezoneChooser.getComponentPanel(), c);
                c.gridx = 0;
                c.gridy++;
            }
        }
        if (m_config.getShowLegacyDateTimeFormatChooser()) {
            m_panel.add(new JLabel("Date&Time (legacy) format: "), c);
            c.gridx++;
            m_panel.add(m_globalDateTimeFormatChooser.getComponentPanel(), c);
            c.gridx = 0;
            c.gridy++;
        }
    }

    /**
     *
     * @throws InvalidSettingsException
     */
    public void validateSettings() throws InvalidSettingsException {
        if (m_config.getShowTimeFormatChooser()) {
            String localTimeFormatString = ((SettingsModelString)m_globalLocalTimeFormatChooser.getModel()).getStringValue();
            String pattern = "(\\[.*\\])*((A|a|H|h|k|m|S|s|[^a-zA-Z]|\\[.*\\])+|(LT|LTS))(\\[.*\\])*";
            if (!Pattern.matches(pattern, localTimeFormatString)) {
                throw new InvalidSettingsException("Local Time format is not valid.");
            }
        }
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedDateTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check the StringHistory first
        String[] userFormats = StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }

        formats.add("YYYY-MM-DD");
        formats.add("ddd MMM DD YYYY HH:mm:ss");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS");

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedZonedDateTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check the StringHistory first
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(ZONED_DATE_TIME_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).getHistory()
        ));

        formats.add("YYYY-MM-DD z");
        formats.add("ddd MMM DD YYYY HH:mm:ss z");
        formats.add("M/D/YY z");
        formats.add("MMM D, YYYY z");
        formats.add("MMMM D, YYYY z");
        formats.add("dddd, MMM D, YYYY z");
        formats.add("h:mm A z");
        formats.add("h:mm:ss A z");
        formats.add("HH:mm:ss z");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS z");

        formats.add("YYYY-MM-DD");
        formats.add("ddd MMM DD YYYY HH:mm:ss");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS");

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check also the StringHistory....
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).getHistory()
        ));

        formats.add("HH:mm:ss");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss.SSS");

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalDateTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();
        formats.add("YYYY-MM-DD");
        formats.add("ddd MMM DD YYYY HH:mm:ss");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS");

        // check the StringHistory first
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).getHistory()
        ));

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalDateFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check the StringHistory first
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).getHistory()
        ));

        formats.add("YYYY-MM-DD");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");

        return formats;
    }

    /**
     * @return a BiMap of locale keys and locale values as supported by moment.js
     * @throws IOException
     */
    private static BiMap<String, String> loadDateTimeLocales() {
        Builder<String, String> biMapBuilder = ImmutableBiMap.builder();

        Properties props = new Properties();
        InputStream input = DialogComponentDateTimeOptions.class.getResourceAsStream("locales.properties");

        try {
            props.load(input);
            props.entrySet().stream()
                .sorted(
                    (e1, e2) -> ((String)e1.getValue()).toLowerCase().compareTo(((String)e2.getValue()).toLowerCase())
                )
                .forEach(
                    (entry) -> biMapBuilder.put((String)entry.getKey(), (String)entry.getValue())
                );

        } catch (IOException e) {
            biMapBuilder.put("en", "English (United States)");
        }

        return biMapBuilder.build();
    }

    private void setTimezoneChooserState() {
        String zonedValue = ((SettingsModelString)m_globalZonedDateTimeFormatChooser.getModel()).getStringValue();
        boolean enabled = zonedValue.indexOf('z') != -1 || zonedValue.indexOf('Z') != -1;
        m_timezoneChooser.getModel().setEnabled(enabled);
        String tooltip = enabled ? "" : "Zoned Date&Time format must contain a zone mask symbol ('z' or 'Z') to enable the time zone selector";
        m_timezoneChooser.setToolTipText(tooltip);
    }

    /**
     * Specifies which controls will be shown in the component
     * @author Oleg Yasnev, KNIME.com GmbH, Berlin, Germany
     *
     */
    public static class Config {
        private boolean m_showLocaleChooser = true;
        private boolean m_showDateTimeFormatChooser = true;
        private boolean m_showDateFormatChooser = true;
        private boolean m_showTimeFormatChooser = true;
        private boolean m_showZonedDateTimeFormatChooser = true;
        private boolean m_showLegacyDateTimeFormatChooser = true;
        private boolean m_showTimezoneChooser = true;

        /**
         * @return the showLocaleChooser
         */
        public boolean getShowLocaleChooser() {
            return m_showLocaleChooser;
        }

        /**
         * @param showLocaleChooser the showLocaleChooser to set
         */
        public void setShowLocaleChooser(final boolean showLocaleChooser) {
            m_showLocaleChooser = showLocaleChooser;
        }

        /**
         * @return the showDateTimeFormatChooser
         */
        public boolean getShowDateTimeFormatChooser() {
            return m_showDateTimeFormatChooser;
        }

        /**
         * @param showDateTimeFormatChooser the showDateTimeFormatChooser to set
         */
        public void setShowDateTimeFormatChooser(final boolean showDateTimeFormatChooser) {
            m_showDateTimeFormatChooser = showDateTimeFormatChooser;
        }

        /**
         * @return the showDateFormatChooser
         */
        public boolean getShowDateFormatChooser() {
            return m_showDateFormatChooser;
        }

        /**
         * @param showDateFormatChooser the showDateFormatChooser to set
         */
        public void setShowDateFormatChooser(final boolean showDateFormatChooser) {
            m_showDateFormatChooser = showDateFormatChooser;
        }

        /**
         * @return the showTimeFormatChooser
         */
        public boolean getShowTimeFormatChooser() {
            return m_showTimeFormatChooser;
        }

        /**
         * @param showTimeFormatChooser the showTimeFormatChooser to set
         */
        public void setShowTimeFormatChooser(final boolean showTimeFormatChooser) {
            m_showTimeFormatChooser = showTimeFormatChooser;
        }

        /**
         * @return the showZonedDateTimeFormatChooser
         */
        public boolean getShowZonedDateTimeFormatChooser() {
            return m_showZonedDateTimeFormatChooser;
        }

        /**
         * Hiding the zoned date&time format chooser also leads to hiding the timezone chooser
         * @param showZonedDateTimeFormatChooser the showZonedDateTimeFormatChooser to set
         */
        public void setShowZonedDateTimeFormatChooser(final boolean showZonedDateTimeFormatChooser) {
            m_showZonedDateTimeFormatChooser = showZonedDateTimeFormatChooser;
            if (!showZonedDateTimeFormatChooser) {
                setShowTimezoneChooser(false);
            }
        }

        /**
         * @return the showLegacyDateTimeFormatChooser
         */
        public boolean getShowLegacyDateTimeFormatChooser() {
            return m_showLegacyDateTimeFormatChooser;
        }

        /**
         * @param showLegacyDateTimeFormatChooser the showLegacyDateTimeFormatChooser to set
         */
        public void setShowLegacyDateTimeFormatChooser(final boolean showLegacyDateTimeFormatChooser) {
            m_showLegacyDateTimeFormatChooser = showLegacyDateTimeFormatChooser;
        }

        /**
         * @return the showTimezoneChooser
         */
        public boolean getShowTimezoneChooser() {
            return m_showTimezoneChooser;
        }

        /**
         * Timezone chooser will be available only if zoned date&time format chooser is available as well
         * @param showTimezoneChooser the showTimezoneChooser to set
         */
        public void setShowTimezoneChooser(final boolean showTimezoneChooser) {
            m_showTimezoneChooser = showTimezoneChooser;
        }
    }
}
