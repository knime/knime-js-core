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
 *   13.05.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.lift;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class LiftChartViewRepresentation extends JSONViewContent {

    private static final String LIFT_VALUES = "liftValues";
    private static final String BASELINE = "baseline";
    private static final String CUMULATIVE_LIFT = "cumLift";

    private boolean m_showGrid;
    private boolean m_resizeToWindow;
    private int m_lineWidth;
    private int m_imageWidth;
    private int m_imageHeight;
    private String m_backgroundColor;
    private String m_dataAreaColor;
    private String m_gridColor;

    private double m_intervalWidth;
    private double[] m_liftValues;
    private double m_baseline;
    private double[] m_cumulativeLift;

    private boolean m_enableStaggeredRendering = true;



    /**
     * @return the lineWidth
     */
    public int getLineWidth() {
        return m_lineWidth;
    }

    /**
     * @param lineWidth the lineWidth to set
     */
    public void setLineWidth(final int lineWidth) {
        m_lineWidth = lineWidth;
    }

    /**
     * @return the intervalWidth
     */
    public double getIntervalWidth() {
        return m_intervalWidth;
    }

    /**
     * @param intervalWidth the intervalWidth to set
     */
    public void setIntervalWidth(final double intervalWidth) {
        m_intervalWidth = intervalWidth;
    }

    /**
     * @return the liftvalues
     */
    public double[] getLiftValues() {
        return m_liftValues;
    }

    /**
     * @param liftvalues the liftvalues to set
     */
    public void setLiftValues(final double[] liftvalues) {
        m_liftValues = liftvalues;
    }

    /**
     * @return the baseline
     */
    public double getBaseline() {
        return m_baseline;
    }

    /**
     * @param baseline the baseline to set
     */
    public void setBaseline(final double baseline) {
        m_baseline = baseline;
    }

    /**
     * @return the cumulativeLift
     */
    public double[] getCumulativeLift() {
        return m_cumulativeLift;
    }

    /**
     * @param cumulativeLift the cumulativeLift to set
     */
    public void setCumulativeLift(final double[] cumulativeLift) {
        m_cumulativeLift = cumulativeLift;
    }

    /**
     * @return the showGrid
     */
    public boolean getShowGrid() {
        return m_showGrid;
    }

    /**
     * @param showGrid the showGrid to set
     */
    public void setShowGrid(final boolean showGrid) {
        m_showGrid = showGrid;
    }

    /**
     * @return the resizeToWindow
     */
    public boolean getResizeToWindow() {
        return m_resizeToWindow;
    }

    /**
     * @param resizeToWindow the resizeToWindow to set
     */
    public void setResizeToWindow(final boolean resizeToWindow) {
        m_resizeToWindow = resizeToWindow;
    }

    /**
     * @return the imageWidth
     */
    public int getImageWidth() {
        return m_imageWidth;
    }

    /**
     * @param imageWidth the imageWidth to set
     */
    public void setImageWidth(final int imageWidth) {
        m_imageWidth = imageWidth;
    }

    /**
     * @return the imageHeight
     */
    public int getImageHeight() {
        return m_imageHeight;
    }

    /**
     * @param imageHeight the imageHeight to set
     */
    public void setImageHeight(final int imageHeight) {
        m_imageHeight = imageHeight;
    }

    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return m_backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(final String backgroundColor) {
        m_backgroundColor = backgroundColor;
    }

    /**
     * @return the dataAreaColor
     */
    public String getDataAreaColor() {
        return m_dataAreaColor;
    }

    /**
     * @param dataAreaColor the dataAreaColor to set
     */
    public void setDataAreaColor(final String dataAreaColor) {
        m_dataAreaColor = dataAreaColor;
    }

    /**
     * @return the gridColor
     */
    public String getGridColor() {
        return m_gridColor;
    }

    /**
     * @param gridColor the gridColor to set
     */
    public void setGridColor(final String gridColor) {
        m_gridColor = gridColor;
    }

    /**
     * @return the enableStaggeredRendering
     */
    public boolean getEnableStaggeredRendering() {
        return m_enableStaggeredRendering;
    }

    /**
     * @param enableStaggeredRendering the enableStaggeredRendering to set
     */
    public void setEnableStaggeredRendering(final boolean enableStaggeredRendering) {
        m_enableStaggeredRendering = enableStaggeredRendering;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(LiftChartViewConfig.SHOW_GRID, getShowGrid());
        settings.addBoolean(LiftChartViewConfig.RESIZE_TO_WINDOW, getResizeToWindow());

        settings.addInt(LiftChartViewConfig.IMAGE_WIDTH, getImageWidth());
        settings.addInt(LiftChartViewConfig.IMAGE_HEIGHT, getImageHeight());
        settings.addString(LiftChartViewConfig.BACKGROUND_COLOR, getBackgroundColor());
        settings.addString(LiftChartViewConfig.DATA_AREA_COLOR, getDataAreaColor());
        settings.addString(LiftChartViewConfig.GRID_COLOR, getGridColor());

        settings.addDoubleArray(LIFT_VALUES, m_liftValues);
        settings.addDoubleArray(CUMULATIVE_LIFT, m_cumulativeLift);
        settings.addDouble(BASELINE, m_baseline);
        settings.addDouble(LiftChartViewConfig.INTERVAL_WIDTH, m_intervalWidth);
        settings.addInt(LiftChartViewConfig.LINE_WIDTH, m_lineWidth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setShowGrid(settings.getBoolean(LiftChartViewConfig.SHOW_GRID));
        setResizeToWindow(settings.getBoolean(LiftChartViewConfig.RESIZE_TO_WINDOW));

        setImageWidth(settings.getInt(LiftChartViewConfig.IMAGE_WIDTH));
        setImageHeight(settings.getInt(LiftChartViewConfig.IMAGE_HEIGHT));
        setBackgroundColor(settings.getString(LiftChartViewConfig.BACKGROUND_COLOR));
        setDataAreaColor(settings.getString(LiftChartViewConfig.DATA_AREA_COLOR));
        setGridColor(settings.getString(LiftChartViewConfig.GRID_COLOR));
        setLineWidth(settings.getInt(LiftChartViewConfig.LINE_WIDTH));
        m_liftValues = settings.getDoubleArray(LIFT_VALUES);
        m_cumulativeLift = settings.getDoubleArray(LIFT_VALUES);
        m_baseline = settings.getDouble(BASELINE);
        m_intervalWidth = settings.getDouble(LiftChartViewConfig.INTERVAL_WIDTH);
    }
}
