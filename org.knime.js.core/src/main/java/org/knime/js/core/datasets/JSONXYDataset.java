/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by
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
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   28.04.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core.datasets;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONXYDataset {

    private JSONDatasetSeries<XYDatasetSeriesItem>[] m_series;

    private JSONDatasetSelection[] m_selections;

    /** Serialization constructor. Don't use. */
    public JSONXYDataset() { }

    /**
     * @param series
     * @param selections
     */
    public JSONXYDataset(final JSONDatasetSeries<XYDatasetSeriesItem>[] series, final JSONDatasetSelection[] selections) {
        m_series = series;
        m_selections = selections;
    }

    /**
     * @return the series
     */
    public JSONDatasetSeries<XYDatasetSeriesItem>[] getSeries() {
        return m_series;
    }

    /**
     * @param series the series to set
     */
    public void setSeries(final JSONDatasetSeries<XYDatasetSeriesItem>[] series) {
        m_series = series;
    }

    /**
     * @return the selections
     */
    public JSONDatasetSelection[] getSelections() {
        return m_selections;
    }

    /**
     * @param selections the selections to set
     */
    public void setSelections(final JSONDatasetSelection[] selections) {
        m_selections = selections;
    }

    /**
     *
     * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
     */
    @JsonAutoDetect
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    static class XYDatasetSeriesItem extends JSONDatasetSeriesItem {

        private double m_x;

        private double m_y;

        /** Serialization constructor. Don't use. */
        public XYDatasetSeriesItem() { }

        /**
         * @param x
         * @param y
         */
        public XYDatasetSeriesItem(final double x, final double y) {
            m_x = x;
            m_y = y;
        }

        /**
         * @return the x
         */
        public double getX() {
            return m_x;
        }

        /**
         * @param x the x to set
         */
        public void setX(final double x) {
            m_x = x;
        }

        /**
         * @return the y
         */
        public double getY() {
            return m_y;
        }

        /**
         * @param y the y to set
         */
        public void setY(final double y) {
            m_y = y;
        }
    }

}
