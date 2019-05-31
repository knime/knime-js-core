/**
 *
 */
package org.knime.js.core.settings.table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions.JSONDateTimeOptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Common table settings which belong to representation object.
 *
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 * @since 3.6
 */
public class TableRepresentationSettings {
    private JSONDataTable m_table;

    final static String CFG_ENABLE_LAZY_LOADING = "enableLazyLoading";
    private final static boolean DEFAULT_ENABLE_LAZY_LOADING = false;
    private boolean m_enableLazyLoading = DEFAULT_ENABLE_LAZY_LOADING;

    final static String CFG_ENABLE_PAGING = "enablePaging";
    private final static boolean DEFAULT_ENABLE_PAGING = true;
    private boolean m_enablePaging = DEFAULT_ENABLE_PAGING;

    final static String CFG_INITIAL_PAGE_SIZE = "initialPageSize";
    private final static int DEFAULT_INITIAL_PAGE_SIZE = 10;
    private int m_initialPageSize = DEFAULT_INITIAL_PAGE_SIZE;

    final static String CFG_ENABLE_PAGE_SIZE_CHANGE = "enablePageSizeChange";
    private final static boolean DEFAULT_ENABLE_PAGE_SIZE_CHANGE = true;
    private boolean m_enablePageSizeChange = DEFAULT_ENABLE_PAGE_SIZE_CHANGE;

    final static String CFG_PAGE_SIZES = "allowedPageSizes";
    private final static int[] DEFAULT_PAGE_SIZES = new int[]{10, 25, 50, 100};
    private int[] m_allowedPageSizes = DEFAULT_PAGE_SIZES;

    final static String CFG_PAGE_SIZE_SHOW_ALL = "enableShowAll";
    private final static boolean DEFAULT_PAGE_SIZE_SHOW_ALL = false;
    private boolean m_pageSizeShowAll = DEFAULT_PAGE_SIZE_SHOW_ALL;

    final static String CFG_ENABLE_JUMP_TO_PAGE = "enableJumpToPage";
    private final static boolean DEFAULT_ENABLE_JUMP_TO_PAGE = false;
    private boolean m_enableJumpToPage = DEFAULT_ENABLE_JUMP_TO_PAGE;

    final static String CFG_DISPLAY_ROW_COLORS = "displayRowColors";
    private final static boolean DEFAULT_DISPLAY_ROW_COLORS = true;
    private boolean m_displayRowColors = DEFAULT_DISPLAY_ROW_COLORS;

    final static String CFG_DISPLAY_ROW_IDS = "displayRowIDs";
    private final static boolean DEFAULT_DISPLAY_ROW_IDS = true;
    private boolean m_displayRowIds = DEFAULT_DISPLAY_ROW_IDS;

    final static String CFG_DISPLAY_COLUMN_HEADERS = "displayColumnHeaders";
    private final static boolean DEFAULT_DISPLAY_COLUMN_HEADERS = true;
    private boolean m_displayColumnHeaders = DEFAULT_DISPLAY_COLUMN_HEADERS;

    final static String CFG_DISPLAY_ROW_INDEX = "displayRowIndex";
    private final static boolean DEFAULT_DISPLAY_ROW_INDEX = false;
    private boolean m_displayRowIndex = DEFAULT_DISPLAY_ROW_INDEX;

    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    final static String CFG_FIXED_HEADERS = "fixedHeaders";
    private final static boolean DEFAULT_FIXED_HEADERS = false;
    private boolean m_fixedHeaders = DEFAULT_FIXED_HEADERS;

    final static String CFG_TITLE = "title";
    private final static String DEFAULT_TITLE = "";
    private String m_title = DEFAULT_TITLE;

    final static String CFG_SUBTITLE = "subtitle";
    private final static String DEFAULT_SUBTITLE = "";
    private String m_subtitle = DEFAULT_SUBTITLE;

    final static String CFG_ENABLE_SELECTION = "enableSelection";
    final static boolean DEFAULT_ENABLE_SELECTION = true;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    final static String CFG_SINGLE_SELECTION = "singleSelection";
    final static boolean DEFAULT_SINGLE_SELECTION = false;
    private boolean m_singleSelection = DEFAULT_SINGLE_SELECTION;

    final static String CFG_ENABLE_CLEAR_SELECTION_BUTTON = "enableClearSelectionButton";
    final static boolean DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON = true;
    private boolean m_enableClearSelectionButton = DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON;

    final static String CFG_ENABLE_SEARCHING = "enableSearching";
    private final static boolean DEFAULT_ENABLE_SEARCHING = true;
    private boolean m_enableSearching = DEFAULT_ENABLE_SEARCHING;

    final static String CFG_ENABLE_COLUMN_SEARCHING = "enableColumnSearching";
    private final static boolean DEFAULT_ENABLE_COLUMN_SEARCHING = false;
    private boolean m_enableColumnSearching = DEFAULT_ENABLE_COLUMN_SEARCHING;

    final static String CFG_ENABLE_HIDE_UNSELECTED = "enableHideUnselected";
    final static boolean DEFAULT_ENABLE_HIDE_UNSELECTED = true;
    private boolean m_enableHideUnselected = DEFAULT_ENABLE_HIDE_UNSELECTED;

    final static String CFG_ENABLE_SORTING = "enableSorting";
    private final static boolean DEFAULT_ENABLE_SORTING = true;
    private boolean m_enableSorting = DEFAULT_ENABLE_SORTING;

    final static String CFG_ENABLE_CLEAR_SORT_BUTTON = "enableClearSortButton";
    private final static boolean DEFAULT_ENABLE_CLEAR_SORT_BUTTON = false;
    private boolean m_enableClearSortButton = DEFAULT_ENABLE_CLEAR_SORT_BUTTON;

    /**
     * Config name for date and time formats
     */
    public final static String CFG_DATE_TIME_FORMATS = "dateTimeFormats";
    private SettingsModelDateTimeOptions m_dateTimeFormats = new SettingsModelDateTimeOptions(CFG_DATE_TIME_FORMATS);

    final static String CFG_GLOBAL_DATE_TIME_FORMAT = "globalDateFormat";

    final static String CFG_ENABLE_GLOBAL_NUMBER_FORMAT = "enableGlobalNumberFormat";
    private final static boolean DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT = false;
    private boolean m_enableGlobalNumberFormat = DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT;

    final static String CFG_GLOBAL_NUMBER_FORMAT_DECIMALS = "globalNumberFormatDecimals";
    private final static int DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS = 2;
    private int m_globalNumberFormatDecimals = DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS;

    final static String CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK = "displayMissingValueAsQuestionMark";
    final static boolean DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK = true;
    private boolean m_displayMissingValueAsQuestionMark = DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK;

    private static final String CFG_PUBLISH_FILTER_ID = "publishFilterId";
    private String m_publishFilterId;

    private static final String CFG_SUBSCRIPTION_FILTER_IDS = "subscriptionFilterIds";
    private String[] m_subscriptionFilterIds;

    final static String CFG_MAX_ROWS = "maxRows";
    private final static int DEFAULT_MAX_ROWS = 100000;
    private int m_maxRows = DEFAULT_MAX_ROWS;

    /**
     * @return The JSON data table.
     */
    public JSONDataTable getTable() {
        return m_table;
    }

    /**
     * @param table The table to set.
     */
    public void setTable(final JSONDataTable table) {
        m_table = table;
    }

    /**
     * @return the enableLazyLoading
     * @since 4.0
     */
    public boolean getEnableLazyLoading() {
        return m_enableLazyLoading;
    }

    /**
     * @param enableLazyLoading the enableLazyLoading to set
     * @since 4.0
     */
    public void setEnableLazyLoading(final boolean enableLazyLoading) {
        m_enableLazyLoading = enableLazyLoading;
    }

    /**
     * @return the enablePaging
     */
    public boolean getEnablePaging() {
        return m_enablePaging;
    }

    /**
     * @param enablePaging the enablePaging to set
     */
    public void setEnablePaging(final boolean enablePaging) {
        m_enablePaging = enablePaging;
    }

    /**
     * @return the initialPageSize
     */
    public int getInitialPageSize() {
        return m_initialPageSize;
    }

    /**
     * @param initialPageSize the initialPageSize to set
     */
    public void setInitialPageSize(final int initialPageSize) {
        m_initialPageSize = initialPageSize;
    }

    /**
     * @return the enablePageSizeChange
     */
    public boolean getEnablePageSizeChange() {
        return m_enablePageSizeChange;
    }

    /**
     * @param enablePageSizeChange the enablePageSizeChange to set
     */
    public void setEnablePageSizeChange(final boolean enablePageSizeChange) {
        m_enablePageSizeChange = enablePageSizeChange;
    }

    /**
     * @return the allowedPageSizes
     */
    public int[] getAllowedPageSizes() {
        return m_allowedPageSizes;
    }

    /**
     * @param allowedPageSizes the allowedPageSizes to set
     */
    public void setAllowedPageSizes(final int[] allowedPageSizes) {
        m_allowedPageSizes = allowedPageSizes;
    }

    /**
     * @return the pageSizeShowAll
     */
    public boolean getPageSizeShowAll() {
        return m_pageSizeShowAll;
    }

    /**
     * @param pageSizeShowAll the pageSizeShowAll to set
     */
    public void setPageSizeShowAll(final boolean pageSizeShowAll) {
        m_pageSizeShowAll = pageSizeShowAll;
    }

    /**
     * @return the enableJumpToPage
     */
    public boolean getEnableJumpToPage() {
        return m_enableJumpToPage;
    }

    /**
     * @param enableJumpToPage the enableJumpToPage to set
     */
    public void setEnableJumpToPage(final boolean enableJumpToPage) {
        m_enableJumpToPage = enableJumpToPage;
    }

    /**
     * @return the displayRowColors
     */
    public boolean getDisplayRowColors() {
        return m_displayRowColors;
    }

    /**
     * @param displayRowColors the displayRowColors to set
     */
    public void setDisplayRowColors(final boolean displayRowColors) {
        m_displayRowColors = displayRowColors;
    }

    /**
     * @return the displayRowIds
     */
    public boolean getDisplayRowIds() {
        return m_displayRowIds;
    }

    /**
     * @param displayRowIds the displayRowIds to set
     */
    public void setDisplayRowIds(final boolean displayRowIds) {
        m_displayRowIds = displayRowIds;
    }

    /**
     * @return the displayColumnHeaders
     */
    public boolean getDisplayColumnHeaders() {
        return m_displayColumnHeaders;
    }

    /**
     * @param displayColumnHeaders the displayColumnHeaders to set
     */
    public void setDisplayColumnHeaders(final boolean displayColumnHeaders) {
        m_displayColumnHeaders = displayColumnHeaders;
    }

    /**
     * @return the displayRowIndex
     */
    public boolean getDisplayRowIndex() {
        return m_displayRowIndex;
    }

    /**
     * @param displayRowIndex the displayRowIndex to set
     */
    public void setDisplayRowIndex(final boolean displayRowIndex) {
        m_displayRowIndex = displayRowIndex;
    }

    /**
     * @return the displayFullscreenButton
     */
    public boolean getDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    public void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
    }

    /**
     * @return the fixedHeaders
     */
    public boolean getFixedHeaders() {
        return m_fixedHeaders;
    }

    /**
     * @param fixedHeaders the fixedHeaders to set
     */
    public void setFixedHeaders(final boolean fixedHeaders) {
        m_fixedHeaders = fixedHeaders;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        m_title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return m_subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(final String subtitle) {
        m_subtitle = subtitle;
    }

    /**
     * @return the enableSelection
     */
    public boolean getEnableSelection() {
        return m_enableSelection;
    }

    /**
     * @param enableSelection the enableSelection to set
     */
    public void setEnableSelection(final boolean enableSelection) {
        m_enableSelection = enableSelection;
    }

    /**
     * @return the singleSelection
     */
    public boolean getSingleSelection() {
        return m_singleSelection;
    }

    /**
     * @param singleSelection the singleSelection to set
     */
    public void setSingleSelection(final boolean singleSelection) {
        m_singleSelection = singleSelection;
    }

    /**
     * @return the enableHideUnselected
     */
    public boolean getEnableHideUnselected() {
        return m_enableHideUnselected;
    }

    /**
     * @param enableHideUnselected the enableHideUnselected to set
     */
    public void setEnableHideUnselected(final boolean enableHideUnselected) {
        m_enableHideUnselected = enableHideUnselected;
    }

    /**
     * @return the enableClearSelectionButton
     */
    public boolean getEnableClearSelectionButton() {
        return m_enableClearSelectionButton;
    }

    /**
     * @param enableClearSelectionButton the enableClearSelectionButton to set
     */
    public void setEnableClearSelectionButton(final boolean enableClearSelectionButton) {
        m_enableClearSelectionButton = enableClearSelectionButton;
    }

    /**
     * @return the enableSearching
     */
    public boolean getEnableSearching() {
        return m_enableSearching;
    }

    /**
     * @param enableSearching the enableSearching to set
     */
    public void setEnableSearching(final boolean enableSearching) {
        m_enableSearching = enableSearching;
    }

    /**
     * @return the enableColumnSearching
     */
    public boolean getEnableColumnSearching() {
        return m_enableColumnSearching;
    }

    /**
     * @param enableColumnSearching the enableColumnSearching to set
     */
    public void setEnableColumnSearching(final boolean enableColumnSearching) {
        m_enableColumnSearching = enableColumnSearching;
    }

    /**
     * @return the enableSorting
     */
    public boolean getEnableSorting() {
        return m_enableSorting;
    }

    /**
     * @param enableSorting the enableSorting to set
     */
    public void setEnableSorting(final boolean enableSorting) {
        m_enableSorting = enableSorting;
    }

    /**
     * @return the enableClearSortButton
     */
    public boolean getEnableClearSortButton() {
        return m_enableClearSortButton;
    }

    /**
     * @param enableClearSortButton the enableClearSortButton to set
     */
    public void setEnableClearSortButton(final boolean enableClearSortButton) {
        m_enableClearSortButton = enableClearSortButton;
    }

    /**
     * @return the dateTimeFormats
     */
    @JsonIgnore
    public SettingsModelDateTimeOptions getDateTimeFormats() {
        return m_dateTimeFormats;
    }

    /**
     * @param dateTimeFormats the dateTimeFormats to set
     */
    @JsonIgnore
    public void setDateTimeFormats(final SettingsModelDateTimeOptions dateTimeFormats) {
        m_dateTimeFormats = dateTimeFormats;
    }

    /**
     * @return dateTimeFormats converted to JSON
     */
    @JsonProperty("dateTimeFormats")
    public JSONDateTimeOptions getJsonDateTimeFormats() {
        return m_dateTimeFormats.getJSONSerializableObject();
    }

    /**
     * @param options - JSON dateTimeFormats
     */
    @JsonProperty("dateTimeFormats")
    public void setJsonDateTimeFormats(final JSONDateTimeOptions options) {
        m_dateTimeFormats.setFromJSON(options);
    }

    /**
     * @return the enableGlobalNumberFormat
     */
    public boolean getEnableGlobalNumberFormat() {
        return m_enableGlobalNumberFormat;
    }

    /**
     * @param enableGlobalNumberFormat the enableGlobalNumberFormat to set
     */
    public void setEnableGlobalNumberFormat(final boolean enableGlobalNumberFormat) {
        m_enableGlobalNumberFormat = enableGlobalNumberFormat;
    }

    /**
     * @return the globalNumberFormatDecimals
     */
    public int getGlobalNumberFormatDecimals() {
        return m_globalNumberFormatDecimals;
    }

    /**
     * @param globalNumberFormatDecimals the globalNumberFormatDecimals to set
     */
    public void setGlobalNumberFormatDecimals(final int globalNumberFormatDecimals) {
        m_globalNumberFormatDecimals = globalNumberFormatDecimals;
    }

    /**
     * @return the displayMissingValueAsQuestionMark
     */
    public boolean getDisplayMissingValueAsQuestionMark() {
        return m_displayMissingValueAsQuestionMark;
    }

    /**
     * @param displayMissingValueAsQuestionMark the displayMissingValueAsQuestionMark to set
     */
    public void setDisplayMissingValueAsQuestionMark(final boolean displayMissingValueAsQuestionMark) {
        m_displayMissingValueAsQuestionMark = displayMissingValueAsQuestionMark;
    }

    /**
     * @return the publishFilterId
     */
    public String getPublishFilterId() {
        return m_publishFilterId;
    }

    /**
     * @param publishFilterId the publishFilterId to set
     */
    public void setPublishFilterId(final String publishFilterId) {
        m_publishFilterId = publishFilterId;
    }

    /**
     * @return the subscriptionFilterIds
     */
    public String[] getSubscriptionFilterIds() {
        return m_subscriptionFilterIds;
    }

    /**
     * @param subscriptionFilterIds the subscriptionFilterIds to set
     */
    public void setSubscriptionFilterIds(final String[] subscriptionFilterIds) {
        m_subscriptionFilterIds = subscriptionFilterIds;
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return m_maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(final int maxRows) {
        m_maxRows = maxRows;
    }


    /**
     * Saves the current state to the given node settings object.
     * @param settings The settings object to save to.
     */
    @JsonIgnore
    public void saveSettings(final NodeSettingsWO settings) {
        //added with 3.3
        settings.addString(CFG_PUBLISH_FILTER_ID, m_publishFilterId);
        settings.addStringArray(CFG_SUBSCRIPTION_FILTER_IDS, m_subscriptionFilterIds);

        saveSettingsFromDialog(settings);
    }

    /**
     * Populates the object by loading from the NodeSettings object.
     * @param settings The settings to load from
     * @throws InvalidSettingsException on load or validation error
     */
    @JsonIgnore
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // load everything but table
        m_enablePaging = settings.getBoolean(CFG_ENABLE_PAGING);
        m_initialPageSize = settings.getInt(CFG_INITIAL_PAGE_SIZE);
        m_enablePageSizeChange = settings.getBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL);
        m_enableJumpToPage = settings.getBoolean(CFG_ENABLE_JUMP_TO_PAGE);
        m_displayRowColors = settings.getBoolean(CFG_DISPLAY_ROW_COLORS);
        m_displayRowIds = settings.getBoolean(CFG_DISPLAY_ROW_IDS);
        m_displayColumnHeaders = settings.getBoolean(CFG_DISPLAY_COLUMN_HEADERS);
        m_displayRowIndex = settings.getBoolean(CFG_DISPLAY_ROW_INDEX);
        m_fixedHeaders = settings.getBoolean(CFG_FIXED_HEADERS);
        m_title = settings.getString(CFG_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_enableSearching = settings.getBoolean(CFG_ENABLE_SEARCHING);
        m_enableColumnSearching = settings.getBoolean(CFG_ENABLE_COLUMN_SEARCHING);
        m_enableSorting = settings.getBoolean(CFG_ENABLE_SORTING);
        m_enableClearSortButton = settings.getBoolean(CFG_ENABLE_CLEAR_SORT_BUTTON);
        m_enableGlobalNumberFormat = settings.getBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT);
        m_globalNumberFormatDecimals = settings.getInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS);

        //added with 3.3
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_enableHideUnselected = settings.getBoolean(CFG_ENABLE_HIDE_UNSELECTED, DEFAULT_ENABLE_HIDE_UNSELECTED);
        m_publishFilterId = settings.getString(CFG_PUBLISH_FILTER_ID, null);
        m_subscriptionFilterIds = settings.getStringArray(CFG_SUBSCRIPTION_FILTER_IDS, (String[])null);

        //added with 3.4
        m_displayMissingValueAsQuestionMark = settings.getBoolean(CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK);

        if (settings.containsKey(CFG_DATE_TIME_FORMATS)) {
            m_dateTimeFormats.loadSettingsFrom(settings);
        } else {
            String legacyDateTimeFormat = settings.getString(CFG_GLOBAL_DATE_TIME_FORMAT);
            m_dateTimeFormats.getGlobalDateTimeFormatModel().setStringValue(legacyDateTimeFormat);
        }

        //added with 3.5
        m_singleSelection = settings.getBoolean(CFG_SINGLE_SELECTION, DEFAULT_SINGLE_SELECTION);
        m_enableClearSelectionButton = settings.getBoolean(CFG_ENABLE_CLEAR_SELECTION_BUTTON, DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON);

        //added with 3.6
        m_maxRows = settings.getInt(CFG_MAX_ROWS, DEFAULT_MAX_ROWS);

        //added with 3.8
        m_enableLazyLoading = settings.getBoolean(CFG_ENABLE_LAZY_LOADING, DEFAULT_ENABLE_LAZY_LOADING);
    }

    /**
     * Saves the settings which are used in dialog to the given node settings object.
     * @param settings The settings object to save to.
     */
    @JsonIgnore
    public void saveSettingsFromDialog(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_ENABLE_PAGING, m_enablePaging);
        settings.addInt(CFG_INITIAL_PAGE_SIZE, m_initialPageSize);
        settings.addBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE, m_enablePageSizeChange);
        settings.addIntArray(CFG_PAGE_SIZES, m_allowedPageSizes);
        settings.addBoolean(CFG_PAGE_SIZE_SHOW_ALL, m_pageSizeShowAll);
        settings.addBoolean(CFG_ENABLE_JUMP_TO_PAGE, m_enableJumpToPage);
        settings.addBoolean(CFG_DISPLAY_ROW_COLORS, m_displayRowColors);
        settings.addBoolean(CFG_DISPLAY_ROW_IDS, m_displayRowIds);
        settings.addBoolean(CFG_DISPLAY_COLUMN_HEADERS, m_displayColumnHeaders);
        settings.addBoolean(CFG_DISPLAY_ROW_INDEX, m_displayRowIndex);
        settings.addBoolean(CFG_FIXED_HEADERS, m_fixedHeaders);
        settings.addString(CFG_TITLE, m_title);
        settings.addString(CFG_SUBTITLE, m_subtitle);
        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addBoolean(CFG_ENABLE_SEARCHING, m_enableSearching);
        settings.addBoolean(CFG_ENABLE_COLUMN_SEARCHING, m_enableColumnSearching);
        settings.addBoolean(CFG_ENABLE_SORTING, m_enableSorting);
        settings.addBoolean(CFG_ENABLE_CLEAR_SORT_BUTTON, m_enableClearSortButton);
        settings.addBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT, m_enableGlobalNumberFormat);
        settings.addInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS, m_globalNumberFormatDecimals);

        //added with 3.3
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addBoolean(CFG_ENABLE_HIDE_UNSELECTED, m_enableHideUnselected);

        //added with 3.4
        settings.addBoolean(CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, m_displayMissingValueAsQuestionMark);
        m_dateTimeFormats.saveSettingsTo(settings);

        //added with 3.5
        settings.addBoolean(CFG_SINGLE_SELECTION, m_singleSelection);
        settings.addBoolean(CFG_ENABLE_CLEAR_SELECTION_BUTTON, m_enableClearSelectionButton);

        //added with 3.6
        settings.addInt(CFG_MAX_ROWS, m_maxRows);

        //added with 3.8
        settings.addBoolean(CFG_ENABLE_LAZY_LOADING, m_enableLazyLoading);
    }

    /**
     * Loading settings which are used in dialog from NodeSettings object with defaults fallback.
     * @param settings the settings object to load from.
     */
    @JsonIgnore
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        // load everything but table
        m_enablePaging = settings.getBoolean(CFG_ENABLE_PAGING, DEFAULT_ENABLE_PAGING);
        m_initialPageSize = settings.getInt(CFG_INITIAL_PAGE_SIZE, DEFAULT_INITIAL_PAGE_SIZE);
        m_enablePageSizeChange = settings.getBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE, DEFAULT_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES, DEFAULT_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL, DEFAULT_PAGE_SIZE_SHOW_ALL);
        m_enableJumpToPage = settings.getBoolean(CFG_ENABLE_JUMP_TO_PAGE, DEFAULT_ENABLE_JUMP_TO_PAGE);
        m_displayRowColors = settings.getBoolean(CFG_DISPLAY_ROW_COLORS, DEFAULT_DISPLAY_ROW_COLORS);
        m_displayRowIds = settings.getBoolean(CFG_DISPLAY_ROW_IDS, DEFAULT_DISPLAY_ROW_IDS);
        m_displayColumnHeaders = settings.getBoolean(CFG_DISPLAY_COLUMN_HEADERS, DEFAULT_DISPLAY_COLUMN_HEADERS);
        m_displayRowIndex = settings.getBoolean(CFG_DISPLAY_ROW_INDEX, DEFAULT_DISPLAY_ROW_INDEX);
        m_fixedHeaders = settings.getBoolean(CFG_FIXED_HEADERS, DEFAULT_FIXED_HEADERS);
        m_title = settings.getString(CFG_TITLE, DEFAULT_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE, DEFAULT_SUBTITLE);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_enableSearching = settings.getBoolean(CFG_ENABLE_SEARCHING, DEFAULT_ENABLE_SEARCHING);
        m_enableColumnSearching = settings.getBoolean(CFG_ENABLE_COLUMN_SEARCHING, DEFAULT_ENABLE_COLUMN_SEARCHING);
        m_enableSorting = settings.getBoolean(CFG_ENABLE_SORTING, DEFAULT_ENABLE_SORTING);
        m_enableClearSortButton = settings.getBoolean(CFG_ENABLE_CLEAR_SORT_BUTTON, DEFAULT_ENABLE_CLEAR_SORT_BUTTON);
        m_enableGlobalNumberFormat = settings.getBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT, DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT);
        m_globalNumberFormatDecimals = settings.getInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS, DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS);

        //added with 3.3
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_enableHideUnselected = settings.getBoolean(CFG_ENABLE_HIDE_UNSELECTED, DEFAULT_ENABLE_HIDE_UNSELECTED);

        //added with 3.4
        m_displayMissingValueAsQuestionMark = settings.getBoolean(CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK);
        if (settings.containsKey(CFG_DATE_TIME_FORMATS)) {
            try {
                m_dateTimeFormats.loadSettingsFrom(settings);
            } catch (InvalidSettingsException e) {
                // return default
            }
        } else {
            String legacyDateTimeFormat = settings.getString(CFG_GLOBAL_DATE_TIME_FORMAT, null);
            if (legacyDateTimeFormat != null) {
                m_dateTimeFormats.getGlobalDateTimeFormatModel().setStringValue(legacyDateTimeFormat);
            }
        }

        //added with 3.5
        m_singleSelection = settings.getBoolean(CFG_SINGLE_SELECTION, DEFAULT_SINGLE_SELECTION);
        m_enableClearSelectionButton = settings.getBoolean(CFG_ENABLE_CLEAR_SELECTION_BUTTON, DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON);

        //added with 3.6
        m_maxRows = settings.getInt(CFG_MAX_ROWS, DEFAULT_MAX_ROWS);

        //added with 3.8
        m_enableLazyLoading = settings.getBoolean(CFG_ENABLE_LAZY_LOADING, DEFAULT_ENABLE_LAZY_LOADING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_table)
                .append(m_enablePaging)
                .append(m_initialPageSize)
                .append(m_enablePageSizeChange)
                .append(m_allowedPageSizes)
                .append(m_pageSizeShowAll)
                .append(m_enableJumpToPage)
                .append(m_displayRowColors)
                .append(m_displayRowIds)
                .append(m_displayColumnHeaders)
                .append(m_displayRowIndex)
                .append(m_displayFullscreenButton)
                .append(m_fixedHeaders)
                .append(m_title)
                .append(m_subtitle)
                .append(m_enableSelection)
                .append(m_singleSelection)
                .append(m_enableClearSelectionButton)
                .append(m_enableSearching)
                .append(m_enableColumnSearching)
                .append(m_enableHideUnselected)
                .append(m_enableSorting)
                .append(m_enableClearSortButton)
                .append(m_dateTimeFormats)
                .append(m_enableGlobalNumberFormat)
                .append(m_globalNumberFormatDecimals)
                .append(m_displayMissingValueAsQuestionMark)
                .append(m_publishFilterId)
                .append(m_subscriptionFilterIds)
                .append(m_maxRows)
                .append(m_enableLazyLoading)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
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
        TableRepresentationSettings other = (TableRepresentationSettings)obj;
        return new EqualsBuilder()
                .append(m_table, other.m_table)
                .append(m_enablePaging, other.m_enablePaging)
                .append(m_initialPageSize, other.m_initialPageSize)
                .append(m_enablePageSizeChange, other.m_enablePageSizeChange)
                .append(m_allowedPageSizes, other.m_allowedPageSizes)
                .append(m_pageSizeShowAll, other.m_pageSizeShowAll)
                .append(m_enableJumpToPage, other.m_enableJumpToPage)
                .append(m_displayRowColors, other.m_displayRowColors)
                .append(m_displayRowIds, other.m_displayRowIds)
                .append(m_displayColumnHeaders, other.m_displayColumnHeaders)
                .append(m_displayRowIndex, other.m_displayRowIndex)
                .append(m_displayFullscreenButton, other.m_displayFullscreenButton)
                .append(m_fixedHeaders, other.m_fixedHeaders)
                .append(m_title, other.m_title)
                .append(m_subtitle, other.m_subtitle)
                .append(m_enableSelection, other.m_enableSelection)
                .append(m_singleSelection, other.m_singleSelection)
                .append(m_enableClearSelectionButton, other.m_enableClearSelectionButton)
                .append(m_enableSearching, other.m_enableSearching)
                .append(m_enableColumnSearching, other.m_enableColumnSearching)
                .append(m_enableHideUnselected, other.m_enableHideUnselected)
                .append(m_enableSorting, other.m_enableSorting)
                .append(m_enableClearSortButton, other.m_enableClearSortButton)
                .append(m_dateTimeFormats, other.m_dateTimeFormats)
                .append(m_enableGlobalNumberFormat, other.m_enableGlobalNumberFormat)
                .append(m_globalNumberFormatDecimals, other.m_globalNumberFormatDecimals)
                .append(m_displayMissingValueAsQuestionMark, other.m_displayMissingValueAsQuestionMark)
                .append(m_publishFilterId, other.m_publishFilterId)
                .append(m_subscriptionFilterIds, other.m_subscriptionFilterIds)
                .append(m_maxRows, other.m_maxRows)
                .append(m_enableLazyLoading, other.m_enableLazyLoading)
                .isEquals();
    }
}
