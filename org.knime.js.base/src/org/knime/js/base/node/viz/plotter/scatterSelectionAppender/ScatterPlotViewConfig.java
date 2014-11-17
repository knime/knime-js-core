/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   14.05.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.scatterSelectionAppender;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
final class ScatterPlotViewConfig {

    static final int DEFAULT_MAX_ROWS = 2500;
    static final String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Scatter Plot)";

    static final String HIDE_IN_WIZARD = "hideInWizard";
    static final String GENERATE_IMAGE = "generateImage";
    static final String ENABLE_CONFIG = "enableViewConfiguration";
    static final String ENABLE_TTILE_CHANGE = "enableTitleChange";
    static final String ENABLE_SUBTTILE_CHANGE = "enableSubtitleChange";
    static final String ENABLE_X_COL_CHANGE = "enableXColumnChange";
    static final String ENABLE_Y_COL_CHANGE = "enableYColumnChange";
    static final String ENABLE_X_LABEL_EDIT = "enableXAxisLabelEdit";
    static final String ENABLE_Y_LABEL_EDIT = "enableYAxisLabelEdit";
    static final String ENABLE_DOT_SIZE_CHANGE = "enableDotSizeChange";
    static final String ENABLE_ZOOMING = "enableZooming";
    static final String ENABLE_DRAG_ZOOMING = "enableDragZooming";
    static final String ENABLE_PANNING = "enablePanning";
    static final String SHOW_ZOOM_RESET_BUTTON = "showZoomResetButton";
    static final String CHART_TITLE = "chartTitle";
    static final String CHART_SUBTITLE = "chartSubtitle";
    static final String X_COL = "xCol";
    static final String Y_COL = "yCol";
    static final String MAX_ROWS = "maxRows";
    static final String SELECTION_COLUMN_NAME = "selectionColumnName";
    static final String X_AXIS_LABEL = "xAxisLabel";
    static final String Y_AXIS_LABEL = "yAxisLabel";
    static final String X_AXIS_MIN = "xAxisMin";
    static final String X_AXIS_MAX = "xAxisMax";
    static final String Y_AXIS_MIN = "yAxisMin";
    static final String Y_AXIS_MAX = "yAxisMax";
    static final String DOT_SIZE = "dot_size";

    private boolean m_hideInWizard = false;
    private boolean m_generateImage = true;
    private boolean m_enableViewConfiguration = false;
    private boolean m_enableTitleChange = false;
    private boolean m_enableSubtitleChange = false;
    private boolean m_enableXColumnChange = false;
    private boolean m_enableYColumnChange = false;
    private boolean m_enableXAxisLabelEdit = false;
    private boolean m_enableYAxisLabelEdit = false;
    private boolean m_enableDotSizeChange = false;
    private boolean m_enableZooming = true;
    private boolean m_enablePanning = true;
    private boolean m_enableDragZooming = false;
    private boolean m_showZoomResetButton = false;
    private int m_maxRows = DEFAULT_MAX_ROWS;
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;
    private String m_chartTitle;
    private String m_chartSubtitle;
    private String m_xColumn;
    private String m_yColumn;
    private String m_xAxisLabel;
    private String m_yAxisLabel;
    private Double m_xAxisMin;
    private Double m_xAxisMax;
    private Double m_yAxisMin;
    private Double m_yAxisMax;
    private Integer m_dotSize = 3;


    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the generateImage
     */
    public boolean getGenerateImage() {
        return m_generateImage;
    }

    /**
     * @param generateImage the generateImage to set
     */
    public void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
    }

    /**
     * @return the chartTitle
     */
    public String getChartTitle() {
        return m_chartTitle;
    }

    /**
     * @param chartTitle the chartTitle to set
     */
    public void setChartTitle(final String chartTitle) {
        m_chartTitle = chartTitle;
    }

    /**
     * @return the chartSubtitle
     */
    public String getChartSubtitle() {
        return m_chartSubtitle;
    }

    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    public void setChartSubtitle(final String chartSubtitle) {
        m_chartSubtitle = chartSubtitle;
    }

    /**
     * @return the xColumn
     */
    public String getxColumn() {
        return m_xColumn;
    }

    /**
     * @param xColumn the xColumn to set
     */
    public void setxColumn(final String xColumn) {
        m_xColumn = xColumn;
    }

    /**
     * @return the yColumn
     */
    public String getyColumn() {
        return m_yColumn;
    }

    /**
     * @param yColumn the yColumn to set
     */
    public void setyColumn(final String yColumn) {
        m_yColumn = yColumn;
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
     * @return the selectionColumnName
     */
    public String getSelectionColumnName() {
        return m_selectionColumnName;
    }

    /**
     * @param selectionColumnName the selectionColumnName to set
     */
    public void setSelectionColumnName(final String selectionColumnName) {
        m_selectionColumnName = selectionColumnName;
    }

    /**
     * @return the xAxisLabel
     */
    public String getxAxisLabel() {
        return m_xAxisLabel;
    }

    /**
     * @param xAxisLabel the xAxisLabel to set
     */
    public void setxAxisLabel(final String xAxisLabel) {
        m_xAxisLabel = xAxisLabel;
    }

    /**
     * @return the yAxisLabel
     */
    public String getyAxisLabel() {
        return m_yAxisLabel;
    }

    /**
     * @param yAxisLabel the yAxisLabel to set
     */
    public void setyAxisLabel(final String yAxisLabel) {
        m_yAxisLabel = yAxisLabel;
    }

    /**
     * @return the allowViewConfiguration
     */
    public boolean getEnableViewConfiguration() {
        return m_enableViewConfiguration;
    }

    /**
     * @param enableViewConfiguration the allowViewConfiguration to set
     */
    public void setEnableViewConfiguration(final boolean enableViewConfiguration) {
        m_enableViewConfiguration = enableViewConfiguration;
    }

    /**
     * @return the allowTitleChange
     */
    public boolean getEnableTitleChange() {
        return m_enableTitleChange;
    }

    /**
     * @param enableTitleChange the allowTitleChange to set
     */
    public void setEnableTitleChange(final boolean enableTitleChange) {
        m_enableTitleChange = enableTitleChange;
    }

    /**
     * @return the allowSubtitleChange
     */
    public boolean getEnableSubtitleChange() {
        return m_enableSubtitleChange;
    }

    /**
     * @param enableSubtitleChange the allowSubtitleChange to set
     */
    public void setEnableSubtitleChange(final boolean enableSubtitleChange) {
        m_enableSubtitleChange = enableSubtitleChange;
    }

    /**
     * @return the enableXColumnChange
     */
    public boolean getEnableXColumnChange() {
        return m_enableXColumnChange;
    }

    /**
     * @param enableXColumnChange the enableXColumnChange to set
     */
    public void setEnableXColumnChange(final boolean enableXColumnChange) {
        m_enableXColumnChange = enableXColumnChange;
    }

    /**
     * @return the enableYColumnChange
     */
    public boolean getEnableYColumnChange() {
        return m_enableYColumnChange;
    }

    /**
     * @param enableYColumnChange the enableYColumnChange to set
     */
    public void setEnableYColumnChange(final boolean enableYColumnChange) {
        m_enableYColumnChange = enableYColumnChange;
    }

    /**
     * @return the enableXAxisLabelEdit
     */
    public boolean getEnableXAxisLabelEdit() {
        return m_enableXAxisLabelEdit;
    }

    /**
     * @param enableXAxisLabelEdit the enableXAxisLabelEdit to set
     */
    public void setEnableXAxisLabelEdit(final boolean enableXAxisLabelEdit) {
        m_enableXAxisLabelEdit = enableXAxisLabelEdit;
    }

    /**
     * @return the enableYAxisLabelEdit
     */
    public boolean getEnableYAxisLabelEdit() {
        return m_enableYAxisLabelEdit;
    }

    /**
     * @param enableYAxisLabelEdit the enableYAxisLabelEdit to set
     */
    public void setEnableYAxisLabelEdit(final boolean enableYAxisLabelEdit) {
        m_enableYAxisLabelEdit = enableYAxisLabelEdit;
    }

    /**
     * @return the allowDotSizeChange
     */
    public boolean getEnableDotSizeChange() {
        return m_enableDotSizeChange;
    }

    /**
     * @param enableDotSizeChange the allowDotSizeChange to set
     */
    public void setEnableDotSizeChange(final boolean enableDotSizeChange) {
        m_enableDotSizeChange = enableDotSizeChange;
    }

    /**
     * @return the xAxisMin
     */
    public Double getxAxisMin() {
        return m_xAxisMin;
    }

    /**
     * @param xAxisMin the xAxisMin to set
     */
    public void setxAxisMin(final Double xAxisMin) {
        m_xAxisMin = xAxisMin;
    }

    /**
     * @return the xAxisMax
     */
    public Double getxAxisMax() {
        return m_xAxisMax;
    }

    /**
     * @param xAxisMax the xAxisMax to set
     */
    public void setxAxisMax(final Double xAxisMax) {
        m_xAxisMax = xAxisMax;
    }

    /**
     * @return the yAxisMin
     */
    public Double getyAxisMin() {
        return m_yAxisMin;
    }

    /**
     * @param yAxisMin the yAxisMin to set
     */
    public void setyAxisMin(final Double yAxisMin) {
        m_yAxisMin = yAxisMin;
    }

    /**
     * @return the yAxisMax
     */
    public Double getyAxisMax() {
        return m_yAxisMax;
    }

    /**
     * @param yAxisMax the yAxisMax to set
     */
    public void setyAxisMax(final Double yAxisMax) {
        m_yAxisMax = yAxisMax;
    }

    /**
     * @return the allowZooming
     */
    public boolean getEnableZooming() {
        return m_enableZooming;
    }

    /**
     * @param enableZooming the allowZooming to set
     */
    public void setEnableZooming(final boolean enableZooming) {
        m_enableZooming = enableZooming;
    }

    /**
     * @return the allowPanning
     */
    public boolean getEnablePanning() {
        return m_enablePanning;
    }

    /**
     * @param enablePanning the allowPanning to set
     */
    public void setEnablePanning(final boolean enablePanning) {
        m_enablePanning = enablePanning;
    }

    /**
     * @return the enableDragZooming
     */
    public boolean getEnableDragZooming() {
        return m_enableDragZooming;
    }

    /**
     * @param enableDragZooming the enableDragZooming to set
     */
    public void setEnableDragZooming(final boolean enableDragZooming) {
        m_enableDragZooming = enableDragZooming;
    }

    /**
     * @return the showZoomResetButton
     */
    public boolean getShowZoomResetButton() {
        return m_showZoomResetButton;
    }

    /**
     * @param showZoomResetButton the showZoomResetButton to set
     */
    public void setShowZoomResetButton(final boolean showZoomResetButton) {
        m_showZoomResetButton = showZoomResetButton;
    }

    /**
     * @return the dotSize
     */
    public Integer getDotSize() {
        return m_dotSize;
    }

    /**
     * @param dotSize the dotSize to set
     */
    public void setDotSize(final Integer dotSize) {
        m_dotSize = dotSize;
    }

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(HIDE_IN_WIZARD, getHideInWizard());
        settings.addBoolean(GENERATE_IMAGE, getGenerateImage());
        settings.addBoolean(ENABLE_CONFIG, getEnableViewConfiguration());
        settings.addBoolean(ENABLE_TTILE_CHANGE, getEnableTitleChange());
        settings.addBoolean(ENABLE_SUBTTILE_CHANGE, getEnableSubtitleChange());
        settings.addBoolean(ENABLE_X_COL_CHANGE, getEnableXColumnChange());
        settings.addBoolean(ENABLE_Y_COL_CHANGE, getEnableYColumnChange());
        settings.addBoolean(ENABLE_X_LABEL_EDIT, getEnableXAxisLabelEdit());
        settings.addBoolean(ENABLE_Y_LABEL_EDIT, getEnableYAxisLabelEdit());
        settings.addBoolean(ENABLE_DOT_SIZE_CHANGE, getEnableDotSizeChange());
        settings.addBoolean(ENABLE_ZOOMING, getEnableZooming());
        settings.addBoolean(ENABLE_DRAG_ZOOMING, getEnableDragZooming());
        settings.addBoolean(ENABLE_PANNING, getEnablePanning());
        settings.addBoolean(SHOW_ZOOM_RESET_BUTTON, getShowZoomResetButton());
        settings.addString(CHART_TITLE, getChartTitle());
        settings.addString(CHART_SUBTITLE, getChartSubtitle());
        settings.addString(X_COL, getxColumn());
        settings.addString(Y_COL, getyColumn());
        settings.addInt(MAX_ROWS, getMaxRows());
        settings.addString(SELECTION_COLUMN_NAME, getSelectionColumnName());
        settings.addString(X_AXIS_LABEL, getxAxisLabel());
        settings.addString(Y_AXIS_LABEL, getyAxisLabel());
        settings.addString(X_AXIS_MIN, getxAxisMin() == null ? null : getxAxisMin().toString());
        settings.addString(X_AXIS_MAX, getxAxisMax() == null ? null : getxAxisMax().toString());
        settings.addString(Y_AXIS_MIN, getyAxisMin() == null ? null : getyAxisMin().toString());
        settings.addString(Y_AXIS_MAX, getyAxisMax() == null ? null : getyAxisMax().toString());
        settings.addString(DOT_SIZE, getDotSize() == null ? null : getDotSize().toString());
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE));
        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE));
        setEnableSubtitleChange(settings.getBoolean(ENABLE_SUBTTILE_CHANGE));
        setEnableXColumnChange(settings.getBoolean(ENABLE_X_COL_CHANGE));
        setEnableYColumnChange(settings.getBoolean(ENABLE_Y_COL_CHANGE));
        setEnableXAxisLabelEdit(settings.getBoolean(ENABLE_X_LABEL_EDIT));
        setEnableYAxisLabelEdit(settings.getBoolean(ENABLE_Y_LABEL_EDIT));
        setEnableDotSizeChange(settings.getBoolean(ENABLE_DOT_SIZE_CHANGE));
        setEnableZooming(settings.getBoolean(ENABLE_ZOOMING));
        setEnableDragZooming(settings.getBoolean(ENABLE_DRAG_ZOOMING));
        setEnablePanning(settings.getBoolean(ENABLE_PANNING));
        setShowZoomResetButton(settings.getBoolean(SHOW_ZOOM_RESET_BUTTON));
        setChartTitle(settings.getString(CHART_TITLE));
        setChartSubtitle(settings.getString(CHART_SUBTITLE));
        setxColumn(settings.getString(X_COL));
        setyColumn(settings.getString(Y_COL));
        setMaxRows(settings.getInt(MAX_ROWS));
        setSelectionColumnName(settings.getString(SELECTION_COLUMN_NAME));
        setxAxisLabel(settings.getString(X_AXIS_LABEL));
        setyAxisLabel(settings.getString(Y_AXIS_LABEL));
        String xMin = settings.getString(X_AXIS_MIN);
        String xMax = settings.getString(X_AXIS_MAX);
        String yMin = settings.getString(Y_AXIS_MIN);
        String yMax = settings.getString(Y_AXIS_MAX);
        String dotSize = settings.getString(DOT_SIZE);
        setxAxisMin(xMin == null ? null : Double.parseDouble(xMin));
        setxAxisMax(xMax == null ? null : Double.parseDouble(xMax));
        setyAxisMin(yMin == null ? null : Double.parseDouble(yMin));
        setyAxisMax(yMax == null ? null : Double.parseDouble(yMax));
        setDotSize(dotSize == null ? null : Integer.parseInt(dotSize));
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD, false));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE, true));
        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG, false));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE, false));
        setEnableSubtitleChange(settings.getBoolean(ENABLE_SUBTTILE_CHANGE, false));
        setEnableXColumnChange(settings.getBoolean(ENABLE_X_COL_CHANGE, false));
        setEnableYColumnChange(settings.getBoolean(ENABLE_Y_COL_CHANGE, false));
        setEnableXAxisLabelEdit(settings.getBoolean(ENABLE_X_LABEL_EDIT, false));
        setEnableYAxisLabelEdit(settings.getBoolean(ENABLE_Y_LABEL_EDIT, false));
        setEnableDotSizeChange(settings.getBoolean(ENABLE_DOT_SIZE_CHANGE, false));
        setEnableZooming(settings.getBoolean(ENABLE_ZOOMING, true));
        setEnableDragZooming(settings.getBoolean(ENABLE_DRAG_ZOOMING, false));
        setEnablePanning(settings.getBoolean(ENABLE_PANNING, true));
        setShowZoomResetButton(settings.getBoolean(SHOW_ZOOM_RESET_BUTTON, false));
        setChartTitle(settings.getString(CHART_TITLE, null));
        setChartSubtitle(settings.getString(CHART_SUBTITLE, null));
        setxColumn(settings.getString(X_COL, null));
        setyColumn(settings.getString(Y_COL, null));
        setMaxRows(settings.getInt(MAX_ROWS, DEFAULT_MAX_ROWS));
        setSelectionColumnName(settings.getString(SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME));
        setxAxisLabel(settings.getString(X_AXIS_LABEL, null));
        setyAxisLabel(settings.getString(Y_AXIS_LABEL, null));
        String xMin = settings.getString(X_AXIS_MIN, null);
        String xMax = settings.getString(X_AXIS_MAX, null);
        String yMin = settings.getString(Y_AXIS_MIN, null);
        String yMax = settings.getString(Y_AXIS_MAX, null);
        String dotSize = settings.getString(DOT_SIZE, "3");
        setxAxisMin(xMin == null ? null : Double.parseDouble(xMin));
        setxAxisMax(xMax == null ? null : Double.parseDouble(xMax));
        setyAxisMin(yMin == null ? null : Double.parseDouble(yMin));
        setyAxisMax(yMax == null ? null : Double.parseDouble(yMax));
        setDotSize(dotSize == null ? null : Integer.parseInt(dotSize));
    }
}
