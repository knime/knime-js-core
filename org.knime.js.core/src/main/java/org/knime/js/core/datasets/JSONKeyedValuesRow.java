/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *   29.04.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core.datasets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
public class JSONKeyedValuesRow implements JSONDataset {

    private String m_rowKey;

    private Double[] m_values;
    private Map<String, String> m_properties;

    /** Serialization constructor. Don't use. */
    public JSONKeyedValuesRow() { }

    /**
     * Creates a new keyed values row.
     *
     * @param rowkey the row key
     * @param values array of row values, missing values should be passed as null.
     */
    public JSONKeyedValuesRow(final String rowkey, final Double[] values) {
        m_rowKey = rowkey;
        m_values = values;
        m_properties = new HashMap<String, String>();
    }

    /**
     * Creates a new keyed values row.
     *
     * @param rowkey the row key
     * @param values array of row values
     * @deprecated Use {@link #JSONKeyedValuesRow(String, Double[])} instead.
     */
    @Deprecated
    public JSONKeyedValuesRow(final String rowkey, final double[] values) {
        this(rowkey, ArrayUtils.toObject(values));
    }

    /**
     * @return the rowKey
     */
    public String getRowKey() {
        return m_rowKey;
    }

    /**
     * @param rowKey the rowKey to set
     */
    public void setRowKey(final String rowKey) {
        m_rowKey = rowKey;
    }

    /**
     * @return the values
     */
    public Double[] getValues() {
        return m_values;
    }

    /**
     * @param values the values to set, missing values should be passed as null.
     */
    public void setValues(final Double[] values) {
        m_values = values;
    }

    /**
     * @param values the values to set
     * @deprecated Use {@link #setValues(Double[])} instead.
     */
    @Deprecated
    public void setValues(final double[] values) {
        m_values = ArrayUtils.toObject(values);
    }

    /**
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return m_properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(final Map<String, String> properties) {
        m_properties = properties;
    }

    /**
     * @param colorValue the value to set
     * @throws IllegalArgumentException if parameter length is not equal to values array length
     */
    @JsonIgnore
    public void setColor(final String colorValue) {
        m_properties.put("color", colorValue);
    }

    /**
     * @param shapeValue the value to set
     * @throws IllegalArgumentException if parameter length is not equal to values array length
     */
    @JsonIgnore
    public void setShapes(final String shapeValue) {
        m_properties.put("shape", shapeValue);
    }

    /**
     * @param sizeValue the value to set
     * @throws IllegalArgumentException if parameter length is not equal to values array length
     */
    @JsonIgnore
    public void setSizes(final String sizeValue) {
        m_properties.put("size", sizeValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString("rowKey", m_rowKey);
        // Since the values type has changed to Double and node settings provide support only for double - we do a conversion to String
        settings.addStringArray("values", Stream.of(m_values).map(value -> value == null ? null : value.toString()).toArray(String[]::new));
        settings.addInt("numProperties", m_properties.size());
        int propertyID = 0;
        for (Entry<String, String> propertyEntry : m_properties.entrySet()) {
            NodeSettingsWO propSettings = settings.addNodeSettings("property_" + propertyID++);
            propSettings.addString("key", propertyEntry.getKey());
            propSettings.addString("value", propertyEntry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException{
        m_rowKey = settings.getString("rowKey");
        // Since the values type has changed to Double and node settings provide support only for double - we do a conversion to String
        // For backwards compatibility we need to address the double array though
        try {
            m_values = Stream.of(settings.getStringArray("values")).map(value -> value == null ? null : Double.parseDouble(value)).toArray(Double[]::new);
        } catch (InvalidSettingsException e) {
            m_values = DoubleStream.of(settings.getDoubleArray("values")).boxed().toArray(Double[]::new);
        }
        int numProperties = settings.getInt("numProperties");
        m_properties = new HashMap<String, String>();
        for (int propertyID = 0; propertyID < numProperties; propertyID++) {
            NodeSettingsRO propSettings = settings.getNodeSettings("property_" + propertyID);
            String propKey = propSettings.getString("key");
            String propValue = propSettings.getString("value");
            m_properties.put(propKey, propValue);
        }
    }
}