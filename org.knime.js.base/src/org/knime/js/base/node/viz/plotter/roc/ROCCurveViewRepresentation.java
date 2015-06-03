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
package org.knime.js.base.node.viz.plotter.roc;

import java.awt.Color;

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
public class ROCCurveViewRepresentation extends JSONViewContent {

    private static final String COLORS = "colors";
    private static final String SHOW_GRID = "showGrid";
    private static final String IMAGE_HEIGHT = "imageHeight";
    private static final String IMAGE_WIDTH = "imageWidth";
    private static final String LINE_WIDTH = "lineWidth";
    private static final String GRID_COLOR = "gridColor";
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String DATA_AREA_COLOR = "dataAreaColor";
    private static final String SHOW_AREA = "showArea";

    private JSONROCCurve[] m_curves;
    private String[] m_colors;

    private boolean m_showGrid;
    private boolean m_showArea;
    private boolean m_resizeToWindow;
    private boolean m_enableStaggeredRendering = true;
    private int m_imageWidth;
    private int m_imageHeight;
    private int m_lineWidth;
    private String m_gridColor;
    private String m_backgroundColor;
    private String m_dataAreaColor;
    private boolean m_showLegend;

    private boolean m_enableControls = true;
    private boolean m_enableEditTitle = true;
    private boolean m_enableEditSubtitle = true;
    private boolean m_enableEditXAxisLabel = true;
    private boolean m_enableEditYAxisLabel = true;

    /**
     * @return the enableControls
     */
    public boolean isEnableControls() {
        return m_enableControls;
    }

    /**
     * @param enableControls the enableControls to set
     */
    public void setEnableControls(final boolean enableControls) {
        m_enableControls = enableControls;
    }

    /**
     * @return the enableEditTitle
     */
    public boolean isEnableEditTitle() {
        return m_enableEditTitle;
    }

    /**
     * @param enableEditTitle the enableEditTitle to set
     */
    public void setEnableEditTitle(final boolean enableEditTitle) {
        m_enableEditTitle = enableEditTitle;
    }

    /**
     * @return the enableEditSubtitle
     */
    public boolean isEnableEditSubtitle() {
        return m_enableEditSubtitle;
    }

    /**
     * @param enableEditSubtitle the enableEditSubtitle to set
     */
    public void setEnableEditSubtitle(final boolean enableEditSubtitle) {
        m_enableEditSubtitle = enableEditSubtitle;
    }

    /**
     * @return the enableEditXAxisLabel
     */
    public boolean isEnableEditXAxisLabel() {
        return m_enableEditXAxisLabel;
    }

    /**
     * @param enableEditXAxisLabel the enableEditXAxisLabel to set
     */
    public void setEnableEditXAxisLabel(final boolean enableEditXAxisLabel) {
        m_enableEditXAxisLabel = enableEditXAxisLabel;
    }

    /**
     * @return the enableEditYAxisLabel
     */
    public boolean isEnableEditYAxisLabel() {
        return m_enableEditYAxisLabel;
    }

    /**
     * @param enableEditYAxisLabel the enableEditYAxisLabel to set
     */
    public void setEnableEditYAxisLabel(final boolean enableEditYAxisLabel) {
        m_enableEditYAxisLabel = enableEditYAxisLabel;
    }

    /**
     * @return the showLegend
     */
    public boolean isShowLegend() {
        return m_showLegend;
    }

    /**
     * @param showLegend the showLegend to set
     */
    public void setShowLegend(final boolean showLegend) {
        m_showLegend = showLegend;
    }

    /**
     * @return the showArea
     */
    public boolean getShowArea() {
        return m_showArea;
    }

    /**
     * @param showArea the showArea to set
     */
    public void setShowArea(final boolean showArea) {
        m_showArea = showArea;
    }

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
     * @return the dataAreaColor
     */
    public String getDataAreaColor() {
        return m_dataAreaColor;
    }

    /**
     * @return the gridColor
     */
    public String getGridColor() {
        return m_gridColor;
    }

    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return m_backgroundColor;
    }

    /**
     * @param c the color to set
     */
    public void setGridColor(final Color c) {
        m_gridColor = ROCCurveViewConfig.getRGBAStringFromColor(c);
    }

    /**
     * @param c the color to set
     */
    public void setBackgroundColor(final Color c) {
        m_backgroundColor = ROCCurveViewConfig.getRGBAStringFromColor(c);
    }

    /**
     * @param c the color to set
     */
    public void setDataAreaColor(final Color c) {
        m_dataAreaColor = ROCCurveViewConfig.getRGBAStringFromColor(c);//colorToRGBString(c);
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
     * @return the imageHeigth
     */
    public int getImageHeight() {
        return m_imageHeight;
    }

    /**
     * @param imageHeigth the imageHeigth to set
     */
    public void setImageHeight(final int imageHeigth) {
        m_imageHeight = imageHeigth;
    }

    /**
     * @return the showGrid
     */
    public boolean isShowGrid() {
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
     * @return the colors
     */
    public String[] getColors() {
        return m_colors;
    }

    /**
     * @param colors the colors to set
     */
    public void setColors(final String[] colors) {
        m_colors = colors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        NodeSettingsWO curveSettings = settings.addNodeSettings("curveSettings");
        for (JSONROCCurve c : m_curves) {
            NodeSettingsWO s = curveSettings.addNodeSettings(c.getName());
            c.saveToNodeSettings(s);
        }
        settings.addBoolean("enableStaggeredRendering", m_enableStaggeredRendering);
        settings.addBoolean("resizeToWindow", m_resizeToWindow);

        settings.addString(DATA_AREA_COLOR, m_dataAreaColor);
        settings.addString(BACKGROUND_COLOR, m_backgroundColor);
        settings.addString(GRID_COLOR, m_gridColor);
        settings.addInt(LINE_WIDTH, m_lineWidth);
        settings.addInt(IMAGE_WIDTH, m_imageWidth);
        settings.addInt(IMAGE_HEIGHT, m_imageHeight);
        settings.addBoolean(SHOW_GRID, m_showGrid);
        settings.addBoolean(SHOW_AREA, m_showArea);
        settings.addStringArray(COLORS, m_colors);
        settings.addBoolean(ROCCurveViewConfig.SHOW_LEGEND, m_showLegend);
        settings.addBoolean(ROCCurveViewConfig.ENABLE_CONTROLS, m_enableControls);
        settings.addBoolean(ROCCurveViewConfig.ENABLE_EDIT_TITLE, m_enableEditTitle);
        settings.addBoolean(ROCCurveViewConfig.ENABLE_EDIT_SUBTITLE, m_enableEditSubtitle);
        settings.addBoolean(ROCCurveViewConfig.ENABLE_EDIT_X_AXIS_LABEL, m_enableEditXAxisLabel);
        settings.addBoolean(ROCCurveViewConfig.ENABLE_EDIT_Y_AXIS_LABEL, m_enableEditYAxisLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO curveSettings = settings.getNodeSettings("curveSettings");
        m_curves = new JSONROCCurve[curveSettings.keySet().size()];
        int count = 0;
        for (String key : curveSettings.keySet()) {
            m_curves[count] = new JSONROCCurve();
            m_curves[count].loadFromNodeSettings(curveSettings.getNodeSettings(key));
            count++;
        }
        m_enableStaggeredRendering = settings.getBoolean("enableStaggeredRendering");
        m_resizeToWindow = settings.getBoolean("resizeToWindow");

        m_dataAreaColor = settings.getString(DATA_AREA_COLOR);
        m_backgroundColor = settings.getString(BACKGROUND_COLOR);
        m_gridColor = settings.getString(GRID_COLOR);
        m_lineWidth = settings.getInt(LINE_WIDTH);
        m_imageWidth = settings.getInt(IMAGE_WIDTH);
        m_imageHeight = settings.getInt(IMAGE_HEIGHT);
        m_showGrid = settings.getBoolean(SHOW_GRID);
        m_showArea = settings.getBoolean(SHOW_AREA);
        m_colors = settings.getStringArray(COLORS);
        m_showLegend = settings.getBoolean(ROCCurveViewConfig.SHOW_LEGEND);
        m_enableControls = settings.getBoolean(ROCCurveViewConfig.ENABLE_CONTROLS);
        m_enableEditTitle = settings.getBoolean(ROCCurveViewConfig.ENABLE_EDIT_TITLE);
        m_enableEditSubtitle = settings.getBoolean(ROCCurveViewConfig.ENABLE_EDIT_SUBTITLE);
        m_enableEditXAxisLabel = settings.getBoolean(ROCCurveViewConfig.ENABLE_EDIT_X_AXIS_LABEL);
        m_enableEditYAxisLabel = settings.getBoolean(ROCCurveViewConfig.ENABLE_EDIT_Y_AXIS_LABEL);
    }

    /**
     * @return the roc curves as calculated from the data.
     */
    public JSONROCCurve[] getCurves() {
        return m_curves;
    }

    /**
     * Sets the roc curves.
     * @param curves the curves
     */
    public void setCurves(final JSONROCCurve[] curves) {
        m_curves = curves;
    }
}
