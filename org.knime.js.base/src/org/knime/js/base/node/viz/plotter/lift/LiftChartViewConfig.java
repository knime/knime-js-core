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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public final class LiftChartViewConfig {

    static final double DEFAULT_INTERVAL_WIDTH = 10;
    static final int DEFAULT_MAX_ROWS = 2500;
    static final int DEFAULT_WIDTH = 800;
    static final int DEFAULT_HEIGHT = 600;
    static final String COLOR_STRING_PREFIX = "rgba(";
    static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255);
    static final Color DEFAULT_DATA_AREA_COLOR = new Color(230, 230, 230);
    static final Color DEFAULT_GRID_COLOR = new Color(255, 255, 255);

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

    private String m_responseColumn;
    private String m_probabilityColumn;
    private String m_responseLabel;
    private double m_intervalWidth = DEFAULT_INTERVAL_WIDTH;

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
        setLineWidth(settings.getInt(LINE_WIDTH));
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
    }
}
