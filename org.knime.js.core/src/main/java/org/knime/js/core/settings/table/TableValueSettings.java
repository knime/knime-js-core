/**
 *
 */
package org.knime.js.core.settings.table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Common table settings which belong to value object.
 *
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 * @since 3.6
 */
public class TableValueSettings {
    private static final String CFG_HIDE_UNSELECTED = "hideUnselected";
    private static final boolean DEFAULT_HIDE_UNSELECTED = false;
    private boolean m_hideUnselected = DEFAULT_HIDE_UNSELECTED;

    final static String CFG_PUBLISH_SELECTION = "publishSelection";
    final static boolean DEFAULT_PUBLISH_SELECTION = true;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;

    final static String CFG_SUBSCRIBE_SELECTION = "subscribeSelection";
    final static boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;

    final static String CFG_PUBLISH_FILTER = "publishFilter";
    final static boolean DEFAULT_PUBLISH_FILTER = true;
    private boolean m_publishFilter = DEFAULT_PUBLISH_FILTER;

    final static String CFG_SUBSCRIBE_FILTER = "subscribeFilter";
    final static boolean DEFAULT_SUBSCRIBE_FILTER = true;
    private boolean m_subscribeFilter = DEFAULT_SUBSCRIBE_FILTER;

    // Settings which come from the view

    private static final String CFG_SELECTION = "selection";
    private String[] m_selection;

    private static final String CFG_SELECT_ALL = "selectAll";
    private boolean m_selectAll;

    private static final String CFG_SELECT_ALL_INDETERMINATE = "selectAllIndeterminate";
    private static final boolean DEFAULT_SELECT_ALL_INDETERMINATE = false;
    private boolean m_selectAllIndeterminate;

    private static final String CFG_PAGE_SIZE = "pageSize";
    private int m_pageSize;

    private static final String CFG_CURRENT_PAGE = "currentPage";
    private int m_currentPage;

    private static final String CFG_FILTER_STRING = "filterString";
    private String m_filterString;

    private static final String CFG_COLUMN_FILTER_STRINGS = "columnFilterStrings";
    private String[] m_columnFilterStrings;

    private static final String CFG_CURRENT_ORDER = "currentOrder";
    private Object[][] m_currentOrder = new Object[0][];

    /**
     * @return the selection
     */
    public String[] getSelection() {
        return m_selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(final String[] selection) {
        m_selection = selection;
    }

    /**
     * @return the selectAll
     */
    public boolean getSelectAll() {
        return m_selectAll;
    }

    /**
     * @param selectAll the selectAll to set
     */
    public void setSelectAll(final boolean selectAll) {
        m_selectAll = selectAll;
    }

    /**
     * @return the selectAllIndeterminate
     */
    public boolean getSelectAllIndeterminate() {
        return m_selectAllIndeterminate;
    }

    /**
     * @param selectAllIndeterminate the selectAllIndeterminate to set
     */
    public void setSelectAllIndeterminate(final boolean selectAllIndeterminate) {
        m_selectAllIndeterminate = selectAllIndeterminate;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return m_pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(final int pageSize) {
        m_pageSize = pageSize;
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return m_currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(final int currentPage) {
        m_currentPage = currentPage;
    }

    /**
     * @return the filterString
     */
    public String getFilterString() {
        return m_filterString;
    }

    /**
     * @param filterString the filterString to set
     */
    public void setFilterString(final String filterString) {
        m_filterString = filterString;
    }

    /**
     * @return the columnFilterStrings
     */
    public String[] getColumnFilterStrings() {
        return m_columnFilterStrings;
    }

    /**
     * @param columnFilterStrings the columnFilterStrings to set
     */
    public void setColumnFilterStrings(final String[] columnFilterStrings) {
        m_columnFilterStrings = columnFilterStrings;
    }

    /**
     * @return the currentOrder
     */
    public Object[][] getCurrentOrder() {
        return m_currentOrder;
    }

    /**
     * @param currentOrder the currentOrder to set
     */
    public void setCurrentOrder(final Object[][] currentOrder) {
        m_currentOrder = currentOrder;
    }

    /**
     * @return if hideUnselected
     */
    public boolean getHideUnselected() {
        return m_hideUnselected;
    }

    /**
     * @param hideUnselected the hideUnselected to set
     */
    public void setHideUnselected(final boolean hideUnselected) {
        m_hideUnselected = hideUnselected;
    }

    /**
     * @return the publishSelection
     */
    public boolean getPublishSelection() {
        return m_publishSelection;
    }

    /**
     * @param publishSelection the publishSelection to set
     */
    public void setPublishSelection(final boolean publishSelection) {
        m_publishSelection = publishSelection;
    }

    /**
     * @return the subscribeSelection
     */
    public boolean getSubscribeSelection() {
        return m_subscribeSelection;
    }

    /**
     * @param subscribeSelection the subscribeSelection to set
     */
    public void setSubscribeSelection(final boolean subscribeSelection) {
        m_subscribeSelection = subscribeSelection;
    }

    /**
     * @return the publishFilter
     */
    public boolean getPublishFilter() {
        return m_publishFilter;
    }

    /**
     * @param publishFilter the publishFilter to set
     */
    public void setPublishFilter(final boolean publishFilter) {
        m_publishFilter = publishFilter;
    }

    /**
     * @return the subscribeFilter
     */
    public boolean getSubscribeFilter() {
        return m_subscribeFilter;
    }

    /**
     * @param subscribeFilter the subscribeFilter to set
     */
    public void setSubscribeFilter(final boolean subscribeFilter) {
        m_subscribeFilter = subscribeFilter;
    }

    /**
     * Saves the current state to the given node settings object.
     * @param settings The settings object to save to.
     */
    @JsonIgnore
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addStringArray(CFG_SELECTION, m_selection);
        settings.addBoolean(CFG_SELECT_ALL, m_selectAll);
        settings.addInt(CFG_PAGE_SIZE, m_pageSize);
        settings.addInt(CFG_CURRENT_PAGE, m_currentPage);
        settings.addString(CFG_FILTER_STRING, m_filterString);
        settings.addStringArray(CFG_COLUMN_FILTER_STRINGS, m_columnFilterStrings);
        NodeSettingsWO orderSettings = settings.addNodeSettings(CFG_CURRENT_ORDER);
        orderSettings.addInt("numSettings", m_currentOrder.length);
        for (int i = 0; i < m_currentOrder.length; i++) {
            NodeSettingsWO sO = orderSettings.addNodeSettings("order_" + i);
            sO.addInt("col", (Integer)m_currentOrder[i][0]);
            sO.addString("dir", (String)m_currentOrder[i][1]);
        }

        //added with 3.3
        settings.addBoolean(CFG_SELECT_ALL_INDETERMINATE, m_selectAllIndeterminate);

        saveSettingsFromDialog(settings);
    }

    /**
     * Populates the object by loading from the NodeSettings object.
     * @param settings The settings to load from
     * @throws InvalidSettingsException on load or validation error
     */
    @JsonIgnore
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_selection = settings.getStringArray(CFG_SELECTION);
        m_selectAll = settings.getBoolean(CFG_SELECT_ALL);
        m_pageSize = settings.getInt(CFG_PAGE_SIZE);
        m_currentPage = settings.getInt(CFG_CURRENT_PAGE);
        m_filterString = settings.getString(CFG_FILTER_STRING);
        m_columnFilterStrings = settings.getStringArray(CFG_COLUMN_FILTER_STRINGS);
        NodeSettingsRO orderSettings = settings.getNodeSettings(CFG_CURRENT_ORDER);
        int numSettings = orderSettings.getInt("numSettings");
        m_currentOrder = new Object[numSettings][];
        for (int i = 0; i < numSettings; i++) {
            NodeSettingsRO sO = orderSettings.getNodeSettings("order_" + i);
            int col = sO.getInt("col");
            String dir = sO.getString("dir");
            m_currentOrder[i] = new Object[]{col, dir};
        }

        //added with 3.3
        m_selectAllIndeterminate = settings.getBoolean(CFG_SELECT_ALL_INDETERMINATE, DEFAULT_SELECT_ALL_INDETERMINATE);
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION);
        m_publishFilter = settings.getBoolean(CFG_PUBLISH_FILTER, DEFAULT_PUBLISH_FILTER);
        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER);

        //added with 3.4
        m_hideUnselected = settings.getBoolean(CFG_HIDE_UNSELECTED, DEFAULT_HIDE_UNSELECTED);
    }

    /**
     * Saves the settings which are used in dialog to the given node settings object.
     * @param settings The settings object to save to.
     */
    @JsonIgnore
    public void saveSettingsFromDialog(final NodeSettingsWO settings) {
        //added with 3.3
        settings.addBoolean(CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(CFG_PUBLISH_FILTER, m_publishFilter);
        settings.addBoolean(CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        //added with 3.4
        settings.addBoolean(CFG_HIDE_UNSELECTED, m_hideUnselected);
    }

    /**
     * Loading settings which are used in dialog from NodeSettings object with defaults fallback.
     * @param settings the settings object to load from.
     */
    @JsonIgnore
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        //added with 3.3
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION);
        m_publishFilter = settings.getBoolean(CFG_PUBLISH_FILTER, DEFAULT_PUBLISH_FILTER);
        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER);

        //added with 3.4
        m_hideUnselected = settings.getBoolean(CFG_HIDE_UNSELECTED, DEFAULT_HIDE_UNSELECTED);
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
        TableValueSettings other = (TableValueSettings)obj;
        return new EqualsBuilder()
                .append(m_selection, other.m_selection)
                .append(m_selectAll, other.m_selectAll)
                .append(m_selectAllIndeterminate, other.m_selectAllIndeterminate)
                .append(m_pageSize, other.m_pageSize)
                .append(m_currentPage, other.m_currentPage)
                .append(m_filterString, other.m_filterString)
                .append(m_columnFilterStrings, other.m_columnFilterStrings)
                .append(m_currentOrder, other.m_currentOrder)
                .append(m_hideUnselected, other.m_hideUnselected)
                .append(m_publishSelection, other.m_publishSelection)
                .append(m_subscribeSelection, other.m_subscribeSelection)
                .append(m_publishFilter, other.m_publishFilter)
                .append(m_subscribeFilter, other.m_subscribeFilter)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_selection)
                .append(m_selectAll)
                .append(m_selectAllIndeterminate)
                .append(m_pageSize)
                .append(m_currentPage)
                .append(m_filterString)
                .append(m_columnFilterStrings)
                .append(m_currentOrder)
                .append(m_hideUnselected)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_publishFilter)
                .append(m_subscribeFilter)
                .toHashCode();
    }

}
