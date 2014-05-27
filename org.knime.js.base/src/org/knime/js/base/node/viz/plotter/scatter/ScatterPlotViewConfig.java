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
package org.knime.js.base.node.viz.plotter.scatter;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class ScatterPlotViewConfig {

    static final String ALLOW_CONFIG = "allowViewConfiguration";
    static final String ENABLE_X_COL_CHANGE = "enableXColumnChange";
    static final String ENABLE_Y_COL_CHANGE = "enableYColumnChange";
    static final String ENABLE_X_LABEL_EDIT = "enableXAxisLabelEdit";
    static final String ENABLE_Y_LABEL_EDIT = "enableYAxisLabelEdit";
    static final String ALLOW_DOT_SIZE_CHANGE = "allowDotSizeChange";
    static final String ALLOW_ZOOMING = "allowZooming";
    static final String ALLOW_PANNING = "allowPanning";
    static final String X_COL = "xCol";
    static final String Y_COL = "yCol";
    static final String MAX_ROWS = "maxRows";
    static final String X_AXIS_LABEL = "xAxisLabel";
    static final String Y_AXIS_LABEL = "yAxisLabel";
    static final String X_AXIS_MIN = "xAxisMin";
    static final String X_AXIS_MAX = "xAxisMax";
    static final String Y_AXIS_MIN = "yAxisMin";
    static final String Y_AXIS_MAX = "yAxisMax";
    static final String DOT_SIZE = "dot_size";


    private boolean m_allowViewConfiguration = false;
    private boolean m_enableXColumnChange = false;
    private boolean m_enableYColumnChange = false;
    private boolean m_enableXAxisLabelEdit = false;
    private boolean m_enableYAxisLabelEdit = false;
    private boolean m_allowDotSizeChange = false;
    private boolean m_allowZooming = true;
    private boolean m_allowPanning = true;
    private int m_maxRows = 2500;
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
    public boolean getAllowViewConfiguration() {
        return m_allowViewConfiguration;
    }

    /**
     * @param allowViewConfiguration the allowViewConfiguration to set
     */
    public void setAllowViewConfiguration(final boolean allowViewConfiguration) {
        m_allowViewConfiguration = allowViewConfiguration;
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
    public boolean getAllowDotSizeChange() {
        return m_allowDotSizeChange;
    }

    /**
     * @param allowDotSizeChange the allowDotSizeChange to set
     */
    public void setAllowDotSizeChange(final boolean allowDotSizeChange) {
        m_allowDotSizeChange = allowDotSizeChange;
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
    public boolean getAllowZooming() {
        return m_allowZooming;
    }

    /**
     * @param allowZooming the allowZooming to set
     */
    public void setAllowZooming(final boolean allowZooming) {
        m_allowZooming = allowZooming;
    }

    /**
     * @return the allowPanning
     */
    public boolean getAllowPanning() {
        return m_allowPanning;
    }

    /**
     * @param allowPanning the allowPanning to set
     */
    public void setAllowPanning(final boolean allowPanning) {
        m_allowPanning = allowPanning;
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
        settings.addBoolean(ALLOW_CONFIG, getAllowViewConfiguration());
        settings.addBoolean(ENABLE_X_COL_CHANGE, getEnableXColumnChange());
        settings.addBoolean(ENABLE_Y_COL_CHANGE, getEnableYColumnChange());
        settings.addBoolean(ENABLE_X_LABEL_EDIT, getEnableXAxisLabelEdit());
        settings.addBoolean(ENABLE_Y_LABEL_EDIT, getEnableYAxisLabelEdit());
        settings.addBoolean(ALLOW_DOT_SIZE_CHANGE, getAllowDotSizeChange());
        settings.addBoolean(ALLOW_ZOOMING, getAllowZooming());
        settings.addBoolean(ALLOW_PANNING, getAllowPanning());
        settings.addString(X_COL, getxColumn());
        settings.addString(Y_COL, getyColumn());
        settings.addInt(MAX_ROWS, getMaxRows());
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
        setAllowViewConfiguration(settings.getBoolean(ALLOW_CONFIG));
        setEnableXColumnChange(settings.getBoolean(ENABLE_X_COL_CHANGE));
        setEnableYColumnChange(settings.getBoolean(ENABLE_Y_COL_CHANGE));
        setEnableXAxisLabelEdit(settings.getBoolean(ENABLE_X_LABEL_EDIT));
        setEnableYAxisLabelEdit(settings.getBoolean(ENABLE_Y_LABEL_EDIT));
        setAllowDotSizeChange(settings.getBoolean(ALLOW_DOT_SIZE_CHANGE));
        setAllowZooming(settings.getBoolean(ALLOW_ZOOMING));
        setAllowPanning(settings.getBoolean(ALLOW_PANNING));
        setxColumn(settings.getString(X_COL));
        setyColumn(settings.getString(Y_COL));
        setMaxRows(settings.getInt(MAX_ROWS));
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
        setAllowViewConfiguration(settings.getBoolean(ALLOW_CONFIG, false));
        setEnableXColumnChange(settings.getBoolean(ENABLE_X_COL_CHANGE, false));
        setEnableYColumnChange(settings.getBoolean(ENABLE_Y_COL_CHANGE, false));
        setEnableXAxisLabelEdit(settings.getBoolean(ENABLE_X_LABEL_EDIT, false));
        setEnableYAxisLabelEdit(settings.getBoolean(ENABLE_Y_LABEL_EDIT, false));
        setAllowDotSizeChange(settings.getBoolean(ALLOW_DOT_SIZE_CHANGE, false));
        setAllowZooming(settings.getBoolean(ALLOW_ZOOMING, true));
        setAllowPanning(settings.getBoolean(ALLOW_PANNING, true));
        setxColumn(settings.getString(X_COL, null));
        setyColumn(settings.getString(Y_COL, null));
        setMaxRows(settings.getInt(MAX_ROWS, 2500));
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
