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
 * @author Alexander Fillbrunn
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ROCCurveViewValue extends JSONViewContent {
    private String m_title = "";
    private String m_subtitle = "";
    private String m_xAxisTitle = "X";
    private String m_yAxisTitle = "Y";

    /**
     * @return the xAxisTitle
     */
    public String getxAxisTitle() {
        return m_xAxisTitle;
    }

    /**
     * @param xAxisTitle the xAxisTitle to set
     */
    public void setxAxisTitle(final String xAxisTitle) {
        m_xAxisTitle = xAxisTitle;
    }

    /**
     * @return the yAxisTitle
     */
    public String getyAxisTitle() {
        return m_yAxisTitle;
    }

    /**
     * @param yAxisTitle the yAxisTitle to set
     */
    public void setyAxisTitle(final String yAxisTitle) {
        m_yAxisTitle = yAxisTitle;
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
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(ROCCurveViewConfig.TITLE, m_title);
        settings.addString(ROCCurveViewConfig.SUBTITLE, m_subtitle);
        settings.addString(ROCCurveViewConfig.Y_AXIS_TITLE, m_yAxisTitle);
        settings.addString(ROCCurveViewConfig.X_AXIS_TITLE, m_xAxisTitle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_title = settings.getString(ROCCurveViewConfig.TITLE);
        m_subtitle = settings.getString(ROCCurveViewConfig.SUBTITLE);
        m_xAxisTitle = settings.getString(ROCCurveViewConfig.X_AXIS_TITLE);
        m_yAxisTitle = settings.getString(ROCCurveViewConfig.Y_AXIS_TITLE);
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
        ROCCurveViewValue other = (ROCCurveViewValue)obj;
        return new EqualsBuilder()
                .append(m_title, other.m_title)
                .append(m_subtitle, other.m_subtitle)
                .append(m_xAxisTitle, other.m_xAxisTitle)
                .append(m_yAxisTitle, other.m_yAxisTitle)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_title)
                .append(m_subtitle)
                .append(m_xAxisTitle)
                .append(m_yAxisTitle)
                .toHashCode();
    }

}
