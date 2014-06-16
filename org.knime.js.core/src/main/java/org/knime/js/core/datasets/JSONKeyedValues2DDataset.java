/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONKeyedValues2DDataset implements JSONDataset {

    private String[] m_columnKeys;
    private Map<String, String>[] m_symbols;
    private JSONKeyedValuesRow[] m_rows;

    /** Serialization constructor. Don't use. */
    public JSONKeyedValues2DDataset() { }

    /**
     * @param columnKeys
     * @param rows
     */
    public JSONKeyedValues2DDataset(final String[] columnKeys, final JSONKeyedValuesRow[] rows) {
        m_columnKeys = columnKeys;
        m_rows = rows;
        m_symbols = new Map[m_columnKeys.length];
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
     * @return the symbols
     */
    public Map<String, String>[] getSymbols() {
        return m_symbols;
    }

    /**
     * @param symbols the symbols to set
     */
    public void setSymbols(final Map<String, String>[] symbols) {
        m_symbols = symbols;
    }

    /**
     * @param symbols
     * @param index
     */
    @JsonIgnore
    public void setSymbol(final Map<String, String> symbols, final int index) {
        m_symbols[index] = symbols;
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
        settings.addStringArray("columnKeys", getColumnKeys());
        int propSize = 0;
        if (m_symbols != null) {
            for (int i = 0; i < m_symbols.length; i++) {
                Map<String, String> properties = m_symbols[i];
                if (properties != null) {
                    NodeSettingsWO propSettings = settings.addNodeSettings("symbols_" + propSize++);
                    propSettings.addInt("index", i);
                    propSettings.addInt("numSymbols", properties.size());
                    int propertyID = 0;
                    for (Entry<String, String> propertyEntry : properties.entrySet()) {
                        NodeSettingsWO singlePropSettings = propSettings.addNodeSettings("symbol_" + propertyID++);
                        singlePropSettings.addString("key", propertyEntry.getKey());
                        singlePropSettings.addString("value", propertyEntry.getValue());
                    }
                }
            }
        }
        settings.addInt("colPropsSize", propSize);
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
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException{
        m_columnKeys = settings.getStringArray("columnKeys");
        m_symbols = new Map[m_columnKeys.length];
        int numColProperties = settings.getInt("colPropsSize");
        for (int curColProperty = 0; curColProperty < numColProperties; curColProperty++) {
            NodeSettingsRO colPropertySettings = settings.getNodeSettings("symbols_" + curColProperty);
            int index = colPropertySettings.getInt("index");
            int numProperties = colPropertySettings.getInt("numSymbols");
            Map<String, String> curPropertyMap = new HashMap<String, String>();
            for (int curProperty = 0; curProperty < numProperties; curProperty++) {
                NodeSettingsRO propertySettings = colPropertySettings.getNodeSettings("symbol_" + curProperty);
                String key = propertySettings.getString("key");
                String value = propertySettings.getString("value");
                curPropertyMap.put(key, value);
            }
            m_symbols[index] = curPropertyMap;
        }
        int numRows = settings.getInt("numRows");
        m_rows = new JSONKeyedValuesRow[numRows];
        for (int rowID = 0; rowID < m_rows.length; rowID++) {
            NodeSettingsRO rowSettings = settings.getNodeSettings("row_" + rowID);
            m_rows[rowID] = new JSONKeyedValuesRow();
            m_rows[rowID].loadFromNodeSettings(rowSettings);
        }
    }
}