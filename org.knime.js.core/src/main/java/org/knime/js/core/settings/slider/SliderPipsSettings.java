/*
 * ------------------------------------------------------------------------
 *
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
 *   30 Sep 2016 (albrecht): created
 */
package org.knime.js.core.settings.slider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.settings.numberFormat.NumberFormatSettings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Settings for slider control pips to be used for node config settings, dialog and for inclusion in JSON serialized objects.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @since 3.3
 */
@JsonAutoDetect
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SliderPipsSettings implements Cloneable {

    /**
     * Enumerates all possible modes used in the pip configuration.
     * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
     * @since 3.3
     */
    public static enum PipMode {
        /** The range mode uses the slider range to determine where the pips should be. A pip is generated for every percentage specified. */
        RANGE,
        /** In steps mode, a pip is generated for every step. The filter option can be used to filter the generated pips. */
        STEPS,
        /** In positions mode, pips are generated at percentage-based positions on the slider (set values option). Optionally, the stepped option can be set to true to match the pips to the slider steps.  */
        POSITIONS,
        /** The count mode can be used to generate a fixed number of pips (set values option). Optionally, the stepped option can be set to true to match the pips to the slider steps. */
        COUNT,
        /** In values mode, pips are generated at value-based positions on the slider (set values option). Optionally, the stepped option can be set to true to match the pips to the slider steps. */
        VALUES;

        private static Map<String, PipMode> namesMap = new HashMap<String, PipMode>(5);

        static {
            namesMap.put("range", RANGE);
            namesMap.put("steps", STEPS);
            namesMap.put("positions", POSITIONS);
            namesMap.put("count", COUNT);
            namesMap.put("values", VALUES);
        }

        /**
         * JsonCreator method to create enum value for a given string.
         * @param value the string to convert into a PipMode value.
         * @return the PipMode value for the given string
         */
        @JsonCreator
        public static PipMode forValue(final String value) {
            return namesMap.get(StringUtils.lowerCase(value));
        }

        /**
         * JsonValue method to create the string (JSON) representation of the PipMode value
         * @return the string representation of this PipMode value
         */
        @JsonValue
        public String toValue() {
            for (Entry<String, PipMode> entry : namesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }

            return null; // or fail?
        }
    }

    private static final String CFG_MODE = "mode";
    private PipMode m_mode;

    private static final String CFG_DENSITY = "density";
    private Integer m_density;

    private static final String CFG_VALUES = "values";
    private double[] m_values;

    private static final String CFG_STEPPED = "stepped";
    private Boolean m_stepped;

    private static final String CFG_FILTER = "filter";
    private String m_filter;

    private static final String CFG_FORMAT = "format";
    private static final String CFG_FORMAT_DEFINED = "formatDefined";
    private NumberFormatSettings m_format;

    /**
     * @return the mode
     */
    public PipMode getMode() {
        return m_mode;
    }
    /**
     * @param mode the mode to set
     */
    public void setMode(final PipMode mode) {
        m_mode = mode;
    }
    /**
     * @return the density
     */
    public Integer getDensity() {
        return m_density;
    }
    /**
     * @param density the density to set
     */
    public void setDensity(final Integer density) {
        m_density = density;
    }
    /**
     * @return the values
     */
    public double[] getValues() {
        return m_values;
    }
    /**
     * @param values the values to set
     */
    public void setValues(final double[] values) {
        m_values = values;
    }
    /**
     * @return the stepped
     */
    public Boolean getStepped() {
        return m_stepped;
    }
    /**
     * @param stepped the stepped to set
     */
    public void setStepped(final Boolean stepped) {
        m_stepped = stepped;
    }
    /**
     * @return the filter
     */
    public String getFilter() {
        return m_filter;
    }
    /**
     * @param filter the filter to set
     */
    public void setFilter(final String filter) {
        m_filter = filter;
    }
    /**
     * @return the format
     */
    public NumberFormatSettings getFormat() {
        return m_format;
    }
    /**
     * @param format the format to set
     */
    public void setFormat(final NumberFormatSettings format) {
        m_format = format;
    }

    /**
     * Validates the current settings.
     * @throws InvalidSettingsException If validation fails.
     */
    @JsonIgnore
    public void validateSettings() throws InvalidSettingsException {
        validateSettings(true);
    }

    private void validateSettings(final boolean validateFormat) throws InvalidSettingsException {
        if (m_mode == null) {
            throw new InvalidSettingsException("A mode for pip generation needs to be specified.");
        }
        if (m_density != null && m_density < 0) {
            throw new InvalidSettingsException("The density value needs to be a positive integer.");
        }
        if (m_values != null) {
            if (!(m_mode == PipMode.POSITIONS || m_mode == PipMode.COUNT || m_mode == PipMode.VALUES)) {
                throw new InvalidSettingsException("Values can only be defined for the modes 'positions', 'count' or 'values', but mode is '" + m_mode.toValue() + "'.");
            }
            if (m_mode == PipMode.COUNT && m_values.length > 1) {
                throw new InvalidSettingsException("Only one value can be defined for the mode 'count'.");
            }
            if (m_mode == PipMode.POSITIONS) {
                for (double value : m_values) {
                    if (value < 0 || value > 100) {
                        throw new InvalidSettingsException("All values must be percentage numbers between 0 and 100 in 'positions' mode, but one value is " + value);
                    }
                }
            }
        }
        if (m_mode == PipMode.POSITIONS || m_mode == PipMode.COUNT || m_mode == PipMode.VALUES) {
            if (m_values == null || m_values.length < 1) {
                throw new InvalidSettingsException("At least one value needs to be defined for modes 'positions', 'count' or 'values'.");
            }
        }
        if (m_stepped != null && m_stepped == true) {
            if (!(m_mode == PipMode.POSITIONS || m_mode == PipMode.COUNT || m_mode == PipMode.VALUES)) {
                throw new InvalidSettingsException("Stepped option is only applicable for the modes 'positions', 'count' or 'values', but mode is '" + m_mode.toValue() + "'.");
            }
        }
        if (validateFormat && m_format != null) {
            m_format.validateSettings();
        }
    }

    /**
     * Saves the current state to the given node settings object.
     * @param settings The settings object to save to.
     */
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_MODE, m_mode == null ? null : m_mode.toValue());
        settings.addIntArray(CFG_DENSITY, m_density == null ? null : new int[]{m_density});
        settings.addDoubleArray(CFG_VALUES, m_values);
        settings.addBooleanArray(CFG_STEPPED, m_stepped == null ? null : new boolean[]{m_stepped});
        settings.addString(CFG_FILTER, m_filter);
        NodeSettingsWO formatSettings = settings.addNodeSettings(CFG_FORMAT);
        boolean formatDefined = m_format != null;
        formatSettings.addBoolean(CFG_FORMAT_DEFINED, formatDefined);
        if (formatDefined) {
            m_format.saveToNodeSettings(formatSettings);
        }
    }

    /**
     * Populates the object by loading from the NodeSettings object.
     * The values are validated before being applied. On error this object stays unchanged.
     * @param settings The settings to load from
     * @throws InvalidSettingsException on load or validation error
     */
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        SliderPipsSettings sVal = new SliderPipsSettings();
        sVal.loadValidateSettings(settings);
        sVal.validateSettings(/* already validated during load */ false);
        copyInternals(sVal, this);
    }

    private void loadValidateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_mode = PipMode.forValue(settings.getString(CFG_MODE));
        int[] densityTemp = settings.getIntArray(CFG_DENSITY);
        m_density = densityTemp == null ? null : densityTemp[0];
        m_values = settings.getDoubleArray(CFG_VALUES);
        boolean[] steppedTemp = settings.getBooleanArray(CFG_STEPPED);
        m_stepped = steppedTemp == null ? null : steppedTemp[0];
        m_filter = settings.getString(CFG_FILTER);
        NodeSettingsRO formatSettings = settings.getNodeSettings(CFG_FORMAT);
        boolean formatDefined = formatSettings.getBoolean(CFG_FORMAT_DEFINED);
        if (formatDefined) {
            NumberFormatSettings newFormat = new NumberFormatSettings();
            newFormat.loadFromNodeSettings(formatSettings);
            m_format = newFormat;
        } else {
            m_format = null;
        }
    }

    /**
     * Loading from NodeSettings object with defaults fallback.
     * @param settings the settings object to load from.
     */
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        m_mode = PipMode.forValue(settings.getString(CFG_MODE, null));
        int[] densityTemp = settings.getIntArray(CFG_DENSITY, null);
        m_density = densityTemp == null ? null : densityTemp[0];
        m_values = settings.getDoubleArray(CFG_VALUES, null);
        boolean[] steppedTemp = settings.getBooleanArray(CFG_STEPPED, null);
        m_stepped = steppedTemp == null ? null : steppedTemp[0];
        m_filter = settings.getString(CFG_FILTER, null);
        try {
            NodeSettingsRO formatSettings = settings.getNodeSettings(CFG_FORMAT);
            boolean formatDefined = formatSettings.getBoolean(CFG_FORMAT_DEFINED);
            if (formatDefined) {
                NumberFormatSettings newFormat = new NumberFormatSettings();
                newFormat.loadFromNodeSettingsInDialog(formatSettings);
                m_format = newFormat;
            } else {
                m_format = null;
            }
        } catch (InvalidSettingsException e) {
            m_format = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_mode)
                .append(m_density)
                .append(m_values)
                .append(m_stepped)
                .append(m_filter)
                .append(m_format)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
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
        SliderPipsSettings other = (SliderPipsSettings)obj;
        return new EqualsBuilder()
                .append(m_mode, other.m_mode)
                .append(m_density, other.m_density)
                .append(m_values, other.m_values)
                .append(m_stepped, other.m_stepped)
                .append(m_filter, other.m_filter)
                .append(m_format, other.m_format)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public SliderPipsSettings clone() {
        SliderPipsSettings clonedSettings = new SliderPipsSettings();
        copyInternals(this, clonedSettings);
        return clonedSettings;
    }

    private static synchronized void copyInternals(final SliderPipsSettings settingsFrom, final SliderPipsSettings settingsTo) {
        settingsTo.m_mode = settingsFrom.m_mode;
        settingsTo.m_density = settingsFrom.m_density;
        settingsTo.m_values = settingsFrom.m_values == null ? null : Arrays.copyOf(settingsFrom.m_values, settingsFrom.m_values.length);
        settingsTo.m_stepped = settingsFrom.m_stepped;
        settingsTo.m_filter = settingsFrom.m_filter;
        settingsTo.m_format = settingsFrom.m_format == null ? null : settingsFrom.m_format.clone();
    }

}
