/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   28.04.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.core.datasets;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONKeyedValues3DDataset implements JSONDataset {

    private String m_id;
    private String[] m_columnKeys;
    private String[] m_rowKeys;
    private KeyedValues3DSeries[] m_series;

    /** Serialization constructor. Don't use. */
    public JSONKeyedValues3DDataset() { }

    /**
     * @param id
     * @param columnKeys
     * @param rowKeys
     * @param series
     */
    public JSONKeyedValues3DDataset(final String id, final String[] columnKeys, final String[] rowKeys, final KeyedValues3DSeries[] series) {
        m_id = id;
        m_columnKeys = columnKeys;
        m_rowKeys = rowKeys;
        m_series = series;
    }

    /**
     * @return the id
     */
    public String getId() {
        return m_id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final String id) {
        m_id = id;
    }

    /**
     * @return the columnKeys
     */
    public String[] getColumnKeys() {
        return m_columnKeys;
    }

    /**
     * @param columnKeys the columnKeys to set
     */
    public void setColumnKeys(final String[] columnKeys) {
        m_columnKeys = columnKeys;
    }

    /**
     * @return the rowKeys
     */
    public String[] getRowKeys() {
        return m_rowKeys;
    }

    /**
     * @param rowKeys the rowKeys to set
     */
    public void setRowKeys(final String[] rowKeys) {
        m_rowKeys = rowKeys;
    }

    /**
     * @return the series
     */
    public KeyedValues3DSeries[] getSeries() {
        return m_series;
    }

    /**
     * @param series the series to set
     */
    public void setSeries(final KeyedValues3DSeries[] series) {
        m_series = series;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(JSONDataTable.TABLE_ID, m_id);
        settings.addStringArray("colKeys", m_columnKeys);
        settings.addStringArray("rowKeys", m_rowKeys);
        settings.addInt("numSeries", m_series.length);
        for (int seriesID = 0; seriesID < m_series.length; seriesID++) {
            NodeSettingsWO seriesSettings = settings.addNodeSettings("series_" + seriesID);
            m_series[seriesID].saveToNodeSettings(seriesSettings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // id added with 3.3
        m_id = settings.getString(JSONDataTable.TABLE_ID, null);

        m_columnKeys = settings.getStringArray("colKeys");
        m_rowKeys = settings.getStringArray("rowKeys");
        int numSeries = settings.getInt("numSeries");
        m_series = new KeyedValues3DSeries[numSeries];
        for (int seriesID = 0; seriesID < numSeries; seriesID++) {
            NodeSettingsRO seriesSettings = settings.getNodeSettings("series_" + seriesID);
            m_series[seriesID] = new KeyedValues3DSeries();
            m_series[seriesID].loadFromNodeSettings(seriesSettings);
        }
    }

    /**
     *
     * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
     */
    @JsonAutoDetect
    public static class KeyedValues3DSeries implements JSONDataset {

        private String m_seriesKey;

        private JSONKeyedValuesRow[] m_rows;

        /** Serialization constructor. Don't use. */
        public KeyedValues3DSeries() { }

        /**
         * @param seriesKey
         * @param rows
         */
        public KeyedValues3DSeries(final String seriesKey, final JSONKeyedValuesRow[] rows) {
            m_seriesKey = seriesKey;
            m_rows = rows;
        }

        /**
         * @return the seriesKey
         */
        public String getSeriesKey() {
            return m_seriesKey;
        }

        /**
         * @param seriesKey the seriesKey to set
         */
        public void setSeriesKey(final String seriesKey) {
            m_seriesKey = seriesKey;
        }

        /**
         * @return the rows
         */
        public JSONKeyedValuesRow[] getRows() {
            return m_rows;
        }

        /**
         * @param rows the rows to set
         */
        public void setRows(final JSONKeyedValuesRow[] rows) {
            m_rows = rows;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveToNodeSettings(final NodeSettingsWO settings) {
            settings.addString("seriesKey", m_seriesKey);
            settings.addInt("numRows", m_rows.length);
            for (int rowID = 0; rowID < m_rows.length; rowID++) {
                NodeSettingsWO rowSettings = settings.addNodeSettings("row_" + rowID);
                m_rows[rowID].saveToNodeSettings(rowSettings);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            m_seriesKey = settings.getString("seriesKey");
            int numRows = settings.getInt("numRows");
            m_rows = new JSONKeyedValuesRow[numRows];
            for (int rowID = 0; rowID < m_rows.length; rowID++) {
                NodeSettingsRO rowSettings = settings.getNodeSettings("row_" + rowID);
                m_rows[rowID] = new JSONKeyedValuesRow();
                m_rows[rowID].loadFromNodeSettings(rowSettings);
            }
        }
    }
}
