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
package org.knime.js.base.node.viz.plotter.lift;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public final class LiftChartViewConfig {

    /**
     *
     */
    static final String SMOOTHING = "smoothing";
    private static final String DEFAULT_Y_TITLE_LIFT = "Lift";
    private static final String DEFAULT_X_TITLE_LIFT = "% Contacted";
    private static final String DEFAULT_TITLE_LIFT = "Lift Chart";

    private static final String DEFAULT_Y_TITLE_GAIN = "% Positive Responses";
    private static final String DEFAULT_X_TITLE_GAIN = "% Contacted";
    private static final String DEFAULT_TITLE_GAIN = "Cumulative Gain Chart";

    private static final Map<String, String> SMOOTHING_MAP;
    private static final Map<String, String> SMOOTHING_MAP_INV;

    static {
        SMOOTHING_MAP = new HashMap<String, String>();
        SMOOTHING_MAP.put("None", "linear");
        SMOOTHING_MAP.put("Bezier", "basis");
        SMOOTHING_MAP.put("Step before", "step-before");
        SMOOTHING_MAP.put("Cardinal", "cardinal");
        SMOOTHING_MAP.put("Monotone", "monotone");

        SMOOTHING_MAP_INV = new HashMap<String, String>();
        for (Entry<String, String> e : SMOOTHING_MAP.entrySet()) {
            SMOOTHING_MAP_INV.put(e.getValue(), e.getKey());
        }
    }

    public static String[] getSmoothingOptions() {
        return SMOOTHING_MAP.keySet().toArray(new String[0]);
    }

    public static String mapSmoothingInputToValue(final String input) {
        return SMOOTHING_MAP.get(input);
    }

    public static String mapSmoothingValueToInput(final String val) {
        return SMOOTHING_MAP_INV.get(val);
    }

    static final double DEFAULT_INTERVAL_WIDTH = 10;
    static final int DEFAULT_MAX_ROWS = 2500;
    static final int DEFAULT_WIDTH = 800;
    static final int DEFAULT_HEIGHT = 600;
    static final String COLOR_STRING_PREFIX = "rgba(";
    static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255);
    static final Color DEFAULT_DATA_AREA_COLOR = new Color(230, 230, 230);
    static final Color DEFAULT_GRID_COLOR = new Color(255, 255, 255);

    static final String TITLE_LIFT = "titleLift";
    static final String SUBTITLE_LIFT = "subtitleLift";
    static final String X_AXIS_TITLE_LIFT = "xAxisTitleLift";
    static final String Y_AXIS_TITLE_LIFT = "yAxisTitleLift";

    static final String TITLE_GAIN = "titleGain";
    static final String SUBTITLE_GAIN = "subtitleGain";
    static final String X_AXIS_TITLE_GAIN = "xAxisTitleGain";
    static final String Y_AXIS_TITLE_GAIN = "yAxisTitleGain";

    static final String LINE_WIDTH = "lineWidth";
    static final String HIDE_IN_WIZARD = "hideInWizard";
    static final String GENERATE_IMAGE = "generateImage";
    static final String SHOW_GRID = "showGrid";
    static final String RESIZE_TO_WINDOW = "resizeToWindow";
    static final String IMAGE_WIDTH = "imageWidth";
    static final String IMAGE_HEIGHT = "imageHeight";
    static final String BACKGROUND_COLOR = "backgroundColor";
    static final String DATA_AREA_COLOR = "dataAreaColor";
    static final String GRID_COLOR = "gridColor";
    static final String RESPONSE_COLUMN = "responseColumn";
    static final String PROBABILITY_COLUMN = "probabilityColumn";
    static final String DATA_FILE = "LiftChartNodeDataFile";
    static final String RESPONSE_LABEL = "responseLabel";
    static final String INTERVAL_WIDTH = "intervalWidth";
    static final String SHOW_LEGEND = "showLegend";
    static final String SHOW_GAIN_CHART = "showGain";

    static final String ENABLE_CONTROLS = "enableControls";
    static final String ENABLE_VIEW_TOGGLE = "enableViewToggle";
    static final String ENABLE_EDIT_TITLE = "enableEditTitle";
    static final String ENABLE_EDIT_SUBTITLE = "enableEditSubtitle";
    static final String ENABLE_EDIT_X_AXIS_LABEL = "enableEditXAxisLabel";
    static final String ENABLE_EDIT_Y_AXIS_LABEL = "enableEditYAxisLabel";
    static final String ENABLE_EDIT_SMOOTHING = "enableSmoothingEdit";

    private String m_responseColumn;
    private String m_probabilityColumn;
    private String m_responseLabel;
    private double m_intervalWidth = DEFAULT_INTERVAL_WIDTH;
    private boolean m_showLegend = true;
    private boolean m_showGainChart = false;

    private String m_titleLift = DEFAULT_TITLE_LIFT;
    private String m_subtitleLift = "";
    private String m_xAxisTitleLift = DEFAULT_X_TITLE_LIFT;
    private String m_yAxisTitleLift = DEFAULT_Y_TITLE_LIFT;

    private String m_titleGain = DEFAULT_TITLE_GAIN;
    private String m_subtitleGain = "";
    private String m_xAxisTitleGain = DEFAULT_X_TITLE_GAIN;
    private String m_yAxisTitleGain = DEFAULT_Y_TITLE_GAIN;

    private boolean m_hideInWizard = false;
    private boolean m_generateImage = true;
    private boolean m_showGrid = true;
    private int m_lineWidth = 1;
    private boolean m_resizeToWindow = true;
    private int m_imageWidth = DEFAULT_WIDTH;
    private int m_imageHeight = DEFAULT_HEIGHT;
    private Color m_backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Color m_dataAreaColor = DEFAULT_DATA_AREA_COLOR;
    private Color m_gridColor = DEFAULT_GRID_COLOR;
    private String m_smoothing = "linear";

    private boolean m_enableControls = true;
    private boolean m_enableViewToggle = true;
    private boolean m_enableEditTitle = true;
    private boolean m_enableEditSubtitle = true;
    private boolean m_enableEditXAxisLabel = true;
    private boolean m_enableEditYAxisLabel = true;
    private boolean m_enableSmoothing = true;

    /**
     * @return the smoothing
     */
    public String getSmoothing() {
        return m_smoothing;
    }

    /**
     * @param smoothing the smoothing to set
     */
    public void setSmoothing(final String smoothing) {
        m_smoothing = smoothing;
    }

    /**
     * @return the enableSmoothing
     */
    public boolean getEnableSmoothing() {
        return m_enableSmoothing;
    }

    /**
     * @param enableSmoothing the enableSmoothing to set
     */
    public void setEnableSmoothing(final boolean enableSmoothing) {
        m_enableSmoothing = enableSmoothing;
    }

    /**
     * @return the enableControls
     */
    public boolean getEnableControls() {
        return m_enableControls;
    }

    /**
     * @param enableControls the enableControls to set
     */
    public void setEnableControls(final boolean enableControls) {
        m_enableControls = enableControls;
    }

    /**
     * @return the enableViewToggle
     */
    public boolean getEnableViewToggle() {
        return m_enableViewToggle;
    }

    /**
     * @param enableViewToggle the enableViewToggle to set
     */
    public void setEnableViewToggle(final boolean enableViewToggle) {
        m_enableViewToggle = enableViewToggle;
    }

    /**
     * @return the enableEditTitle
     */
    public boolean getEnableEditTitle() {
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
    public boolean getEnableEditSubtitle() {
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
    public boolean getEnableEditXAxisLabel() {
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
    public boolean getEnableEditYAxisLabel() {
        return m_enableEditYAxisLabel;
    }

    /**
     * @param enableEditYAxisLabel the enableEditYAxisLabel to set
     */
    public void setEnableEditYAxisLabel(final boolean enableEditYAxisLabel) {
        m_enableEditYAxisLabel = enableEditYAxisLabel;
    }

    /**
     * @return the showGainChart
     */
    public boolean getShowGainChart() {
        return m_showGainChart;
    }

    /**
     * @param showGainChart the showGainChart to set
     */
    public void setShowGainChart(final boolean showGainChart) {
        m_showGainChart = showGainChart;
    }

    /**
     * @return the showLegend
     */
    public boolean getShowLegend() {
        return m_showLegend;
    }

    /**
     * @param showLegend the showLegend to set
     */
    public void setShowLegend(final boolean showLegend) {
        m_showLegend = showLegend;
    }

    /**
     * @return the titleLift
     */
    public String getTitleLift() {
        return m_titleLift;
    }

    /**
     * @param titleLift the titleLift to set
     */
    public void setTitleLift(final String titleLift) {
        m_titleLift = titleLift;
    }

    /**
     * @return the subtitleLift
     */
    public String getSubtitleLift() {
        return m_subtitleLift;
    }

    /**
     * @param subtitleLift the subtitleLift to set
     */
    public void setSubtitleLift(final String subtitleLift) {
        m_subtitleLift = subtitleLift;
    }

    /**
     * @return the xAxisTitleLift
     */
    public String getxAxisTitleLift() {
        return m_xAxisTitleLift;
    }

    /**
     * @param xAxisTitleLift the xAxisTitleLift to set
     */
    public void setxAxisTitleLift(final String xAxisTitleLift) {
        m_xAxisTitleLift = xAxisTitleLift;
    }

    /**
     * @return the yAxisTitleLift
     */
    public String getyAxisTitleLift() {
        return m_yAxisTitleLift;
    }

    /**
     * @param yAxisTitleLift the yAxisTitleLift to set
     */
    public void setyAxisTitleLift(final String yAxisTitleLift) {
        m_yAxisTitleLift = yAxisTitleLift;
    }

    /**
     * @return the titleGain
     */
    public String getTitleGain() {
        return m_titleGain;
    }

    /**
     * @param titleGain the titleGain to set
     */
    public void setTitleGain(final String titleGain) {
        m_titleGain = titleGain;
    }

    /**
     * @return the subtitleGain
     */
    public String getSubtitleGain() {
        return m_subtitleGain;
    }

    /**
     * @param subtitleGain the subtitleGain to set
     */
    public void setSubtitleGain(final String subtitleGain) {
        m_subtitleGain = subtitleGain;
    }

    /**
     * @return the xAxisTitleGain
     */
    public String getxAxisTitleGain() {
        return m_xAxisTitleGain;
    }

    /**
     * @param xAxisTitleGain the xAxisTitleGain to set
     */
    public void setxAxisTitleGain(final String xAxisTitleGain) {
        m_xAxisTitleGain = xAxisTitleGain;
    }

    /**
     * @return the yAxisTitleGain
     */
    public String getyAxisTitleGain() {
        return m_yAxisTitleGain;
    }

    /**
     * @param yAxisTitleGain the yAxisTitleGain to set
     */
    public void setyAxisTitleGain(final String yAxisTitleGain) {
        m_yAxisTitleGain = yAxisTitleGain;
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
     * @return the responseColumn
     */
    public String getResponseColumn() {
        return m_responseColumn;
    }

    /**
     * @param responseColumn the responseColumn to set
     */
    public void setResponseColumn(final String responseColumn) {
        m_responseColumn = responseColumn;
    }

    /**
     * @return the probabilityColumn
     */
    public String getProbabilityColumn() {
        return m_probabilityColumn;
    }

    /**
     * @param probabilityColumn the probabilityColumn to set
     */
    public void setProbabilityColumn(final String probabilityColumn) {
        m_probabilityColumn = probabilityColumn;
    }

    /**
     * @return the responseLabel
     */
    public String getResponseLabel() {
        return m_responseLabel;
    }

    /**
     * @param responseLabel the responseLabel to set
     */
    public void setResponseLabel(final String responseLabel) {
        m_responseLabel = responseLabel;
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
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return m_backgroundColor;
    }

    /**
     * @return the backgroundColor as rgba string
     */
    public String getBackgroundColorString() {
        return getRGBAStringFromColor(m_backgroundColor);
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(final Color backgroundColor) {
        m_backgroundColor = backgroundColor;
    }

    /**
     * @return the dataAreaColor
     */
    public Color getDataAreaColor() {
        return m_dataAreaColor;
    }

    /**
     * @return the data area color as rgba string
     */
    public String getDataAreaColorString() {
        return getRGBAStringFromColor(m_dataAreaColor);
    }

    /**
     * @param dataAreaColor the dataAreaColor to set
     */
    public void setDataAreaColor(final Color dataAreaColor) {
        m_dataAreaColor = dataAreaColor;
    }

    /**
     * @return the gridColor
     */
    public Color getGridColor() {
        return m_gridColor;
    }

    /**
     * @return the grid color as rgba string
     */
    public String getGridColorString() {
        return getRGBAStringFromColor(m_gridColor);
    }

    /**
     * @param gridColor the gridColor to set
     */
    public void setGridColor(final Color gridColor) {
        m_gridColor = gridColor;
    }

    public static String getRGBAStringFromColor(final Color color) {
        if (color == null) {
            return null;
        }
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        double a = color.getAlpha() / 255.0;
        StringBuilder builder = new StringBuilder(COLOR_STRING_PREFIX);
        builder.append(r);
        builder.append(",");
        builder.append(g);
        builder.append(",");
        builder.append(b);
        builder.append(",");
        builder.append(a);
        builder.append(")");
        return builder.toString();
    }

    public static Color getColorFromString(final String rgbaString) throws InvalidSettingsException {
        if (rgbaString == null) {
            return null;
        }
        String error = "Could not parse color string: " + rgbaString;
        if (!rgbaString.startsWith(COLOR_STRING_PREFIX)) {
            throw new InvalidSettingsException(error);
        }
        String colorSubstring = rgbaString.substring(COLOR_STRING_PREFIX.length(), rgbaString.length() - 1);
        String[] colorComponents = colorSubstring.split(",");
        if (colorComponents.length != 4) {
            throw new InvalidSettingsException(error);
        }
        try {
            int r = Integer.parseInt(colorComponents[0]);
            int g = Integer.parseInt(colorComponents[1]);
            int b = Integer.parseInt(colorComponents[2]);
            int a = (int)Math.round(Double.parseDouble(colorComponents[3]) * 255);
            return new Color(r, g, b, a);
        } catch (NullPointerException | NumberFormatException e) {
            throw new InvalidSettingsException(error);
        }
    }

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(HIDE_IN_WIZARD, getHideInWizard());
        settings.addBoolean(GENERATE_IMAGE, getGenerateImage());
        settings.addBoolean(SHOW_GRID, getShowGrid());
        settings.addBoolean(RESIZE_TO_WINDOW, getResizeToWindow());

        settings.addInt(IMAGE_WIDTH, getImageWidth());
        settings.addInt(IMAGE_HEIGHT, getImageHeight());
        settings.addString(BACKGROUND_COLOR, getBackgroundColorString());
        settings.addString(DATA_AREA_COLOR, getDataAreaColorString());
        settings.addString(GRID_COLOR, getGridColorString());

        settings.addString(RESPONSE_COLUMN, m_responseColumn);
        settings.addString(PROBABILITY_COLUMN, m_probabilityColumn);
        settings.addString(RESPONSE_LABEL, m_responseLabel);
        settings.addDouble(INTERVAL_WIDTH, m_intervalWidth);
        settings.addInt(LINE_WIDTH, m_lineWidth);

        settings.addString(TITLE_LIFT, m_titleLift);
        settings.addString(SUBTITLE_LIFT, m_subtitleLift);
        settings.addString(Y_AXIS_TITLE_LIFT, m_yAxisTitleLift);
        settings.addString(X_AXIS_TITLE_LIFT, m_xAxisTitleLift);

        settings.addString(TITLE_GAIN, m_titleGain);
        settings.addString(SUBTITLE_GAIN, m_subtitleGain);
        settings.addString(Y_AXIS_TITLE_GAIN, m_yAxisTitleGain);
        settings.addString(X_AXIS_TITLE_GAIN, m_xAxisTitleGain);
        settings.addBoolean(SHOW_GAIN_CHART, m_showGainChart);
        settings.addBoolean(SHOW_LEGEND, m_showLegend);

        settings.addBoolean(ENABLE_CONTROLS, m_enableControls);
        settings.addBoolean(ENABLE_VIEW_TOGGLE, m_enableViewToggle);
        settings.addBoolean(ENABLE_EDIT_TITLE, m_enableEditTitle);
        settings.addBoolean(ENABLE_EDIT_SUBTITLE, m_enableEditSubtitle);
        settings.addBoolean(ENABLE_EDIT_X_AXIS_LABEL, m_enableEditXAxisLabel);
        settings.addBoolean(ENABLE_EDIT_Y_AXIS_LABEL, m_enableEditYAxisLabel);
        settings.addBoolean(ENABLE_EDIT_SMOOTHING, m_enableSmoothing);

        settings.addString(SMOOTHING, m_smoothing);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE));
        setShowGrid(settings.getBoolean(SHOW_GRID));
        setResizeToWindow(settings.getBoolean(RESIZE_TO_WINDOW));

        setImageWidth(settings.getInt(IMAGE_WIDTH));
        setImageHeight(settings.getInt(IMAGE_HEIGHT));
        String bgColorString = settings.getString(BACKGROUND_COLOR);
        setBackgroundColor(getColorFromString(bgColorString));
        String dataColorString = settings.getString(DATA_AREA_COLOR);
        setDataAreaColor(getColorFromString(dataColorString));
        String gridColorString = settings.getString(GRID_COLOR);
        setGridColor(getColorFromString(gridColorString));

        setResponseColumn(settings.getString(RESPONSE_COLUMN));
        setResponseLabel(settings.getString(RESPONSE_LABEL));
        setProbabilityColumn(settings.getString(PROBABILITY_COLUMN));
        setIntervalWidth(settings.getDouble(INTERVAL_WIDTH));

        m_titleLift = settings.getString(TITLE_LIFT);
        m_subtitleLift = settings.getString(SUBTITLE_LIFT);
        m_xAxisTitleLift = settings.getString(X_AXIS_TITLE_LIFT);
        m_yAxisTitleLift = settings.getString(Y_AXIS_TITLE_LIFT);

        m_titleGain = settings.getString(TITLE_GAIN);
        m_subtitleGain = settings.getString(SUBTITLE_GAIN);
        m_xAxisTitleGain = settings.getString(X_AXIS_TITLE_GAIN);
        m_yAxisTitleGain = settings.getString(Y_AXIS_TITLE_GAIN);
        m_showGainChart = settings.getBoolean(LiftChartViewConfig.SHOW_GAIN_CHART);
        m_showLegend = settings.getBoolean(SHOW_LEGEND);
        setLineWidth(settings.getInt(LINE_WIDTH));
        m_enableControls = settings.getBoolean(ENABLE_CONTROLS);
        m_enableViewToggle = settings.getBoolean(ENABLE_VIEW_TOGGLE);
        m_enableEditTitle = settings.getBoolean(ENABLE_EDIT_TITLE);
        m_enableEditSubtitle = settings.getBoolean(ENABLE_EDIT_SUBTITLE);
        m_enableEditXAxisLabel = settings.getBoolean(ENABLE_EDIT_X_AXIS_LABEL);
        m_enableEditYAxisLabel = settings.getBoolean(ENABLE_EDIT_Y_AXIS_LABEL);
        m_enableSmoothing = settings.getBoolean(ENABLE_EDIT_SMOOTHING);
        m_smoothing = settings.getString(SMOOTHING);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec the {@link DataTableSpec} to use for loading
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD, false));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE, true));
        setLineWidth(settings.getInt(LINE_WIDTH, 1));

        setShowGrid(settings.getBoolean(SHOW_GRID, true));
        setResizeToWindow(settings.getBoolean(RESIZE_TO_WINDOW, true));

        setImageWidth(settings.getInt(IMAGE_WIDTH, DEFAULT_WIDTH));
        setImageHeight(settings.getInt(IMAGE_HEIGHT, DEFAULT_HEIGHT));

        String bgColorString = settings.getString(BACKGROUND_COLOR, null);
        Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
        try {
            backgroundColor = getColorFromString(bgColorString);
        } catch (InvalidSettingsException e) { /* do nothing */ }
        setBackgroundColor(backgroundColor);
        String dataColorString = settings.getString(DATA_AREA_COLOR, null);
        Color dataAreaColor = DEFAULT_DATA_AREA_COLOR;
        try {
            dataAreaColor = getColorFromString(dataColorString);
        } catch (InvalidSettingsException e) { /* do nothing */ }
        setDataAreaColor(dataAreaColor);
        String gridColorString = settings.getString(GRID_COLOR, null);
        Color gridColor = DEFAULT_GRID_COLOR;
        try {
            gridColor = getColorFromString(gridColorString);
        } catch (InvalidSettingsException e) { /* do nothing */ }
        setGridColor(gridColor);

        try {
        setResponseColumn(settings.getString(RESPONSE_COLUMN));
        setResponseLabel(settings.getString(RESPONSE_LABEL));
        setProbabilityColumn(settings.getString(PROBABILITY_COLUMN));
        } catch (InvalidSettingsException e) { /* do nothing */ }

        setIntervalWidth(settings.getDouble(INTERVAL_WIDTH, DEFAULT_INTERVAL_WIDTH));

        m_titleLift = settings.getString(LiftChartViewConfig.TITLE_LIFT, DEFAULT_TITLE_LIFT);
        m_subtitleLift = settings.getString(LiftChartViewConfig.SUBTITLE_LIFT, "");
        m_xAxisTitleLift = settings.getString(LiftChartViewConfig.X_AXIS_TITLE_LIFT, DEFAULT_X_TITLE_LIFT);
        m_yAxisTitleLift = settings.getString(LiftChartViewConfig.Y_AXIS_TITLE_LIFT, DEFAULT_Y_TITLE_LIFT);

        m_titleGain = settings.getString(LiftChartViewConfig.TITLE_GAIN, DEFAULT_TITLE_GAIN);
        m_subtitleGain = settings.getString(LiftChartViewConfig.SUBTITLE_GAIN, "");
        m_xAxisTitleGain = settings.getString(LiftChartViewConfig.X_AXIS_TITLE_GAIN, DEFAULT_X_TITLE_GAIN);
        m_yAxisTitleGain = settings.getString(LiftChartViewConfig.Y_AXIS_TITLE_GAIN, DEFAULT_Y_TITLE_GAIN);
        m_showGainChart = settings.getBoolean(LiftChartViewConfig.SHOW_GAIN_CHART, false);
        m_showLegend = settings.getBoolean(SHOW_LEGEND, true);
        m_enableControls = settings.getBoolean(ENABLE_CONTROLS, true);
        m_enableViewToggle = settings.getBoolean(ENABLE_VIEW_TOGGLE, true);
        m_enableEditTitle = settings.getBoolean(ENABLE_EDIT_TITLE, true);
        m_enableEditSubtitle = settings.getBoolean(ENABLE_EDIT_SUBTITLE, true);
        m_enableEditXAxisLabel = settings.getBoolean(ENABLE_EDIT_X_AXIS_LABEL, true);
        m_enableEditYAxisLabel = settings.getBoolean(ENABLE_EDIT_Y_AXIS_LABEL, true);
        m_enableSmoothing = settings.getBoolean(ENABLE_EDIT_SMOOTHING, true);
        m_smoothing = settings.getString(SMOOTHING, "none");
    }
}
