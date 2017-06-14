/**
 *
 */
package org.knime.js.core.components.datatime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.TimeZone;

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

    private final DialogComponentStringSelection m_globalDateTimeLocaleChooser;
    private final DialogComponentStringSelection m_globalDateTimeFormatChooser;
    private final DialogComponentStringSelection m_globalLocalDateFormatChooser;
    private final DialogComponentStringSelection m_globalLocalDateTimeFormatChooser;
    private final DialogComponentStringSelection m_globalLocalTimeFormatChooser;
    private final DialogComponentStringSelection m_globalZonedDateTimeFormatChooser;
    private final DialogComponentStringSelection m_timezoneChooser;

    private static final int FORMAT_CHOOSER_WIDTH = 235;
    private static final int FORMAT_CHOOSER_HEIGHT = 17;

    /**
     * @param model
     * @param label
     */
    public DialogComponentDateTimeOptions(final SettingsModelDateTimeOptions model, final String label) {
        super(model);

        m_globalDateTimeLocaleChooser =
                new DialogComponentStringSelection(
                    new SettingsModelString(
                        SettingsModelDateTimeOptions.GLOBAL_DATE_TIME_LOCALE,
                        PREDEFINED_DATE_TIME_LOCALES.get(SettingsModelDateTimeOptions.DEFAULT_GLOBAL_DATE_TIME_LOCALE)
                    ),
                    "", PREDEFINED_DATE_TIME_LOCALES.values(), true);
        m_globalDateTimeLocaleChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });

        m_globalDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(SettingsModelDateTimeOptions.GLOBAL_DATE_TIME_FORMAT,
                SettingsModelDateTimeOptions.DEFAULT_GLOBAL_DATE_TIME_FORMAT), "", PREDEFINED_DATE_TIME_FORMATS, true);
        m_globalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        m_globalDateTimeFormatChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });

        m_globalLocalDateFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(SettingsModelDateTimeOptions.GLOBAL_LOCAL_DATE_FORMAT,
                SettingsModelDateTimeOptions.DEFAULT_GLOBAL_LOCAL_DATE_FORMAT), "", PREDEFINED_LOCAL_DATE_FORMATS, true);
        m_globalLocalDateFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        m_globalLocalDateFormatChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });

        m_globalLocalDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(SettingsModelDateTimeOptions.GLOBAL_LOCAL_DATE_TIME_FORMAT,
                SettingsModelDateTimeOptions.DEFAULT_GLOBAL_LOCAL_DATE_TIME_FORMAT), "", PREDEFINED_LOCAL_DATE_TIME_FORMATS, true);
        m_globalLocalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        m_globalLocalDateTimeFormatChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });

        m_globalLocalTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(SettingsModelDateTimeOptions.GLOBAL_LOCAL_TIME_FORMAT,
                SettingsModelDateTimeOptions.DEFAULT_GLOBAL_LOCAL_TIME_FORMAT), "", PREDEFINED_LOCAL_TIME_FORMATS, true);
        m_globalLocalTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        m_globalLocalTimeFormatChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });

        m_globalZonedDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(SettingsModelDateTimeOptions.GLOBAL_ZONED_DATE_TIME_FORMAT,
                SettingsModelDateTimeOptions.DEFAULT_GLOBAL_ZONED_DATE_TIME_FORMAT), "", PREDEFINED_ZONED_DATE_TIME_FORMATS, true);
        m_globalZonedDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        m_globalZonedDateTimeFormatChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setTimezoneChooserState();
                updateModel();
            }
        });

        m_timezoneChooser = new DialogComponentStringSelection(new SettingsModelString(SettingsModelDateTimeOptions.TIMEZONE, SettingsModelDateTimeOptions.DEFAULT_TIMEZONE), "",
           new LinkedHashSet<String>(Arrays.asList(TimeZone.getAvailableIDs())), false);
        m_timezoneChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        m_timezoneChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });

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
     * {@inheritDoc}
     */
    @Override
    protected void updateComponent() {
        SettingsModelDateTimeOptions model = (SettingsModelDateTimeOptions) getModel();

        m_globalDateTimeLocaleChooser.replaceListItems(loadDateTimeLocales().values(),
            PREDEFINED_DATE_TIME_LOCALES.get(model.getGlobalDateTimeLocale()));
        m_globalDateTimeFormatChooser.replaceListItems(createPredefinedDateTimeFormats(), model.getGlobalDateTimeFormat());
        m_globalLocalDateFormatChooser.replaceListItems(createPredefinedLocalDateFormats(), model.getGlobalLocalDateFormat());
        m_globalLocalDateTimeFormatChooser.replaceListItems(createPredefinedLocalDateTimeFormats(), model.getGlobalLocalDateTimeFormat());
        m_globalLocalTimeFormatChooser.replaceListItems(createPredefinedLocalTimeFormats(), model.getGlobalLocalTimeFormat());
        m_globalZonedDateTimeFormatChooser.replaceListItems(createPredefinedZonedDateTimeFormats(), model.getGlobalZonedDateTimeFormat());
        ((SettingsModelString)m_timezoneChooser.getModel()).setStringValue(model.getTimezone());

        setEnabledComponents(model.isEnabled());
    }

    private void updateModel() {
        SettingsModelDateTimeOptions model = (SettingsModelDateTimeOptions) getModel();

        model.setGlobalDateTimeLocale(PREDEFINED_DATE_TIME_LOCALES.inverse().get(
            ((SettingsModelString)m_globalDateTimeLocaleChooser.getModel()).getStringValue())
        );
        String globalDateTimeFormat = ((SettingsModelString)m_globalDateTimeFormatChooser.getModel()).getStringValue();
        model.setGlobalDateTimeFormat(globalDateTimeFormat);
        String globalLocalDateFormat = ((SettingsModelString)m_globalLocalDateFormatChooser.getModel()).getStringValue();
        model.setGlobalLocalDateFormat(globalLocalDateFormat);
        String globalLocalDateTimeFormat = ((SettingsModelString)m_globalLocalDateTimeFormatChooser.getModel()).getStringValue();
        model.setGlobalLocalDateTimeFormat(globalLocalDateTimeFormat);
        String globalLocalTimeFormat = ((SettingsModelString)m_globalLocalTimeFormatChooser.getModel()).getStringValue();
        model.setGlobalLocalTimeFormat(globalLocalTimeFormat);
        String globalZonedDateTimeFormat = ((SettingsModelString)m_globalZonedDateTimeFormatChooser.getModel()).getStringValue();
        model.setGlobalZonedDateTimeFormat(globalZonedDateTimeFormat);
        StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).add(globalDateTimeFormat);
        StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).add(globalLocalDateFormat);
        StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).add(globalLocalDateTimeFormat);
        StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).add(globalLocalTimeFormat);
        StringHistory.getInstance(ZONED_DATE_TIME_FORMAT_HISTORY_KEY).add(globalZonedDateTimeFormat);
        String timezone = ((SettingsModelString)m_timezoneChooser.getModel()).getStringValue();
        model.setTimezone(timezone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsBeforeSave() throws InvalidSettingsException {
        updateModel();
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
        m_globalDateTimeLocaleChooser.getModel().setEnabled(enabled);
        m_globalDateTimeFormatChooser.getModel().setEnabled(enabled);
        m_globalLocalDateFormatChooser.getModel().setEnabled(enabled);
        m_globalLocalDateTimeFormatChooser.getModel().setEnabled(enabled);
        m_globalLocalTimeFormatChooser.getModel().setEnabled(enabled);
        m_globalZonedDateTimeFormatChooser.getModel().setEnabled(enabled);
        m_timezoneChooser.getModel().setEnabled(enabled);

        setTimezoneChooserState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTipText(final String text) {
        getComponentPanel().setToolTipText(text);
    }

    private void initPanel(final String label) {
        JPanel panel = getComponentPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory
            .createEtchedBorder(), label));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Locale: "), c);
        c.gridx++;
        panel.add(m_globalDateTimeLocaleChooser.getComponentPanel(), c);
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Local Date format: "), c);
        c.gridx++;
        panel.add(m_globalLocalDateFormatChooser.getComponentPanel(), c);
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Local Date&Time format: "), c);
        c.gridx++;
        panel.add(m_globalLocalDateTimeFormatChooser.getComponentPanel(), c);
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Local Time format: "), c);
        c.gridx++;
        panel.add(m_globalLocalTimeFormatChooser.getComponentPanel(), c);
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Zoned Date&Time format: "), c);
        c.gridx++;
        panel.add(m_globalZonedDateTimeFormatChooser.getComponentPanel(), c);
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Time zone (for zoned format): "), c);
        c.gridx++;
        panel.add(m_timezoneChooser.getComponentPanel(), c);
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Date&Time (legacy) format: "), c);
        c.gridx++;
        panel.add(m_globalDateTimeFormatChooser.getComponentPanel(), c);
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
}
