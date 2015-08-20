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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
public class LiftChartPlotViewValue extends JSONViewContent {
    private String m_titleLift = "";
    private String m_subtitleLift = "";
    private String m_xAxisTitleLift = "";
    private String m_yAxisTitleLift = "Y";

    private String m_titleGain = "";
    private String m_subtitleGain = "";
    private String m_xAxisTitleGain = "";
    private String m_yAxisTitleGain = "Y";

    private String m_smoothing = "none";

    private boolean m_showGainChart = false;

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
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(LiftChartViewConfig.TITLE_LIFT, m_titleLift);
        settings.addString(LiftChartViewConfig.SUBTITLE_LIFT, m_subtitleLift);
        settings.addString(LiftChartViewConfig.Y_AXIS_TITLE_LIFT, m_yAxisTitleLift);
        settings.addString(LiftChartViewConfig.X_AXIS_TITLE_LIFT, m_xAxisTitleLift);


        settings.addString(LiftChartViewConfig.TITLE_GAIN, m_titleGain);
        settings.addString(LiftChartViewConfig.SUBTITLE_GAIN, m_subtitleGain);
        settings.addString(LiftChartViewConfig.Y_AXIS_TITLE_GAIN, m_yAxisTitleGain);
        settings.addString(LiftChartViewConfig.X_AXIS_TITLE_GAIN, m_xAxisTitleGain);
        settings.addBoolean(LiftChartViewConfig.SHOW_GAIN_CHART, m_showGainChart);
        settings.addString(LiftChartViewConfig.SMOOTHING, m_smoothing);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_titleLift = settings.getString(LiftChartViewConfig.TITLE_LIFT);
        m_subtitleLift = settings.getString(LiftChartViewConfig.SUBTITLE_LIFT);
        m_xAxisTitleLift = settings.getString(LiftChartViewConfig.X_AXIS_TITLE_LIFT);
        m_yAxisTitleLift = settings.getString(LiftChartViewConfig.Y_AXIS_TITLE_LIFT);

        m_titleGain = settings.getString(LiftChartViewConfig.TITLE_GAIN);
        m_subtitleGain = settings.getString(LiftChartViewConfig.SUBTITLE_GAIN);
        m_xAxisTitleGain = settings.getString(LiftChartViewConfig.X_AXIS_TITLE_GAIN);
        m_yAxisTitleGain = settings.getString(LiftChartViewConfig.Y_AXIS_TITLE_GAIN);
        m_showGainChart = settings.getBoolean(LiftChartViewConfig.SHOW_GAIN_CHART);
        m_smoothing = settings.getString(LiftChartViewConfig.SMOOTHING);
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
        LiftChartPlotViewValue other = (LiftChartPlotViewValue)obj;
        return new EqualsBuilder()
                .append(m_titleLift, other.m_titleLift)
                .append(m_subtitleLift, other.m_subtitleLift)
                .append(m_xAxisTitleLift, other.m_xAxisTitleLift)
                .append(m_yAxisTitleLift, other.m_yAxisTitleLift)
                .append(m_titleGain, other.m_titleGain)
                .append(m_subtitleGain, other.m_subtitleGain)
                .append(m_xAxisTitleGain, other.m_xAxisTitleGain)
                .append(m_yAxisTitleGain, other.m_yAxisTitleGain)
                .append(m_smoothing, other.m_smoothing)
                .append(m_showGainChart, other.m_showGainChart)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_titleLift)
                .append(m_subtitleLift)
                .append(m_xAxisTitleLift)
                .append(m_yAxisTitleLift)
                .append(m_titleGain)
                .append(m_subtitleGain)
                .append(m_xAxisTitleGain)
                .append(m_yAxisTitleGain)
                .append(m_smoothing)
                .append(m_showGainChart)
                .toHashCode();
    }
}
