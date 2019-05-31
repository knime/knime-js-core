/*
 * ------------------------------------------------------------------------
 *
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
 *   29 Sep 2016 (albrecht): created
 */
package org.knime.js.core.settings.slider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
 * Settings for a slider control to be used for node config settings, dialog and for inclusion in JSON serialized
 * objects.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @since 3.3
 */
@JsonAutoDetect
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SliderSettings implements Cloneable {

    /**
     * Enumerates all possible orientations of a slider.
     *
     * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
     * @since 3.3
     */
    public static enum Orientation {
        /** Horizontal orientation */
        HORIZONTAL,
        /** Vertical Orientation */
        VERTICAL;

        private static Map<String, Orientation> namesMap = new HashMap<String, Orientation>(2);

        static {
            namesMap.put("horizontal", HORIZONTAL);
            namesMap.put("vertical", VERTICAL);
        }

        /**
         * JsonCreator method to create enum value for a given string.
         *
         * @param value the string to convert into an Orientation value.
         * @return the Orientation value for the given string
         */
        @JsonCreator
        public static Orientation forValue(final String value) {
            return namesMap.get(StringUtils.lowerCase(value));
        }

        /**
         * JsonValue method to create the string (JSON) representation of the Orientation value
         *
         * @return the string representation of this Orientation value
         */
        @JsonValue
        public String toValue() {
            for (Entry<String, Orientation> entry : namesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }

            return null; // or fail?
        }
    }

    /**
     * Enumerates all possible directions of a slider.
     *
     * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
     * @since 3.3
     */
    public static enum Direction {
        /** Left to right direction */
        LTR,
        /** Right to left direction */
        RTL;

        private static Map<String, Direction> namesMap = new HashMap<String, Direction>(2);

        static {
            namesMap.put("ltr", LTR);
            namesMap.put("rtl", RTL);
        }

        /**
         * JsonCreator method to create enum value for a given string.
         *
         * @param value the string to convert into an Direction value.
         * @return the Direction value for the given string
         */
        @JsonCreator
        public static Direction forValue(final String value) {
            return namesMap.get(StringUtils.lowerCase(value));
        }

        /**
         * JsonValue method to create the string (JSON) representation of the Direction value
         *
         * @return the string representation of this Direction value
         */
        @JsonValue
        public String toValue() {
            for (Entry<String, Direction> entry : namesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }

            return null; // or fail?
        }
    }

    private static final String RANGE_MIN = "min";

    private static final String RANGE_MAX = "max";

    private static final String CFG_RANGE = "range";

    private static final String NUM_SETTINGS = "numSettings";

    private LinkedHashMap<String, double[]> m_range = new LinkedHashMap<String, double[]>();

    private static final String CFG_START = "start";

    private double[] m_start;

    private static final String CFG_CONNECT = "connect";

    private boolean[] m_connect;

    private static final String CFG_FIX = "fix";

    private boolean[] m_fix;

    private static final String CFG_CONNECT_COLOR = "connectColor";

    private static final String DEFAULT_CONNECT_COLOR = "#3FB8AF";

    private String m_connectColor = DEFAULT_CONNECT_COLOR;

    private static final String CFG_STEP = "step";

    private Double m_step;

    private static final String CFG_SNAP = "snap";

    private Boolean m_snap;

    private static final String CFG_MARGIN = "margin";

    private Double m_margin;

    private static final String CFG_LIMIT = "limit";

    private Double m_limit;

    private static final String CFG_ORIENTATION = "orientation";

    private Orientation m_orientation;

    private static final String CFG_DIRECTION = "direction";

    private Direction m_direction;

    private static final String CFG_TOOLTIPS = "tooltips";

    private Object[] m_tooltips;

    private static final String CFG_ANIMATE = "animate";

    private Boolean m_animate;

    private static final String CFG_ANIMATION_DURATION = "animationDuration";

    private Integer m_animationDuration;

    private static final String CFG_BEHAVIOUR = "behaviour";

    private String m_behaviour;

    private static final String CFG_PIPS = "pips";

    private static final String CFG_PIPS_DEFINED = "pipsDefined";

    private SliderPipsSettings m_pips;

    private static final boolean[] DEFAULT_FIX_ARRAY = {false, true, false};

    /**
     * @return the range
     */
    public LinkedHashMap<String, double[]> getRange() {
        return m_range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(final LinkedHashMap<String, double[]> range) {
        m_range = range;
    }

    /**
     * @return the start
     */
    public double[] getStart() {
        return m_start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(final double[] start) {
        m_start = start;
    }

    /**
     * @return the connect
     */
    public boolean[] getConnect() {
        return m_connect;
    }

    /**
     * @param connect the connect to set
     */
    public void setConnect(final boolean[] connect) {
        m_connect = connect;
    }

    /**
     * @return the fix
     * @since 4.0
     */
    public boolean[] getFix() {
        return m_fix;
    }

    /**
     * @param fix the fix to set
     * @since 4.0
     */
    public void setFix(final boolean[] fix) {
        m_fix = fix;
    }

    /**
     * @return the step
     */
    public Double getStep() {
        return m_step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(final Double step) {
        m_step = step;
    }

    /**
     * @return the snap
     */
    public Boolean getSnap() {
        return m_snap;
    }

    /**
     * @param snap the snap to set
     */
    public void setSnap(final Boolean snap) {
        m_snap = snap;
    }

    /**
     * @return the margin
     */
    public Double getMargin() {
        return m_margin;
    }

    /**
     * @param margin the margin to set
     */
    public void setMargin(final Double margin) {
        m_margin = margin;
    }

    /**
     * @return the limit
     */
    public Double getLimit() {
        return m_limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(final Double limit) {
        m_limit = limit;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return m_orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(final Orientation orientation) {
        m_orientation = orientation;
    }

    /**
     * @return the diretion
     */
    public Direction getDirection() {
        return m_direction;
    }

    /**
     * @param diretion the diretion to set
     */
    public void setDirection(final Direction diretion) {
        m_direction = diretion;
    }

    /**
     * @return the tooltips
     */
    public Object[] getTooltips() {
        return m_tooltips;
    }

    /**
     * @param tooltips the tooltips to set
     */
    public void setTooltips(final Object[] tooltips) {
        m_tooltips = tooltips;
    }

    /**
     * @return the animate
     */
    public Boolean getAnimate() {
        return m_animate;
    }

    /**
     * @param animate the animate to set
     */
    public void setAnimate(final Boolean animate) {
        m_animate = animate;
    }

    /**
     * @return the animationDuration
     */
    public Integer getAnimationDuration() {
        return m_animationDuration;
    }

    /**
     * @param animationDuration the animationDuration to set
     */
    public void setAnimationDuration(final Integer animationDuration) {
        m_animationDuration = animationDuration;
    }

    /**
     * @return the behaviour
     */
    public String getBehaviour() {
        return m_behaviour;
    }

    /**
     * @param behaviour the behaviour to set
     */
    public void setBehaviour(final String behaviour) {
        m_behaviour = behaviour;
    }

    /**
     * @return the pips
     */
    public SliderPipsSettings getPips() {
        return m_pips;
    }

    /**
     * @param pips the pips to set
     */
    public void setPips(final SliderPipsSettings pips) {
        m_pips = pips;
    }

    /**
     * Returns the minimum value for the configured slider range.
     *
     * @return The minimum value for the range, or null if no minimum is set.
     */
    @JsonIgnore
    public Double getRangeMinValue() {
        if (m_range != null) {
            double[] min = m_range.get(RANGE_MIN);
            if (min != null && min.length > 0) {
                return new Double(min[0]);
            }
        }
        return null;
    }

    /**
     * Sets the minimum value for the configured slider range.
     *
     * @param min the new minimum value
     * @return the previous minimum value, or null if there was no minimum value set.
     */
    @JsonIgnore
    public double[] setRangeMinValue(final double min) {
        if (m_range == null) {
            m_range = new LinkedHashMap<String, double[]>();
        }
        return m_range.put(RANGE_MIN, new double[]{min});
    }

    /**
     * Returns the maximum value for the configured slider range.
     *
     * @return The maximum value for the range, or null if no maximum is set.
     */
    @JsonIgnore
    public Double getRangeMaxValue() {
        if (m_range != null) {
            double[] max = m_range.get(RANGE_MAX);
            if (max != null && max.length > 0) {
                return new Double(max[0]);
            }
        }
        return null;
    }

    /**
     * Sets the maximum value for the configured slider range.
     *
     * @param max the new maximum value
     * @return the previous maximum value, or null if there was no maximum value set.
     */
    @JsonIgnore
    public double[] setRangeMaxValue(final double max) {
        if (m_range == null) {
            m_range = new LinkedHashMap<String, double[]>();
        }
        return m_range.put(RANGE_MAX, new double[]{max});
    }

    /**
     * Checks if the configured slider will only produce integer values. Specifically the step and start values are
     * checked. If any of the values is not set the method will also return false;
     *
     * @return true, if the slider settings only produce integer values, false otherwise
     */
    @JsonIgnore
    public boolean outputsIntegerOnly() {
        if (m_start != null) {
            boolean startValid = true;
            for (double startValue : m_start) {
                startValid &= doubleIsInt(startValue);
            }
            return startValid && doubleIsInt(m_step);
        }
        return false;
    }

    private boolean doubleIsInt(final Double value) {
        return value != null && value == Math.floor(value) && !Double.isInfinite(value);
    }

    /**
     * Validates the current settings.
     *
     * @throws InvalidSettingsException If validation fails.
     */
    @JsonIgnore
    public void validateSettings() throws InvalidSettingsException {
        validateSettings(true);
    }

    private void validateSettings(final boolean deepValidate) throws InvalidSettingsException {
        if (getRangeMinValue() == null) {
            throw new InvalidSettingsException("A range minimum needs to be specified.");
        }
        if (getRangeMaxValue() == null) {
            throw new InvalidSettingsException("A range maximum needs to be specified.");
        }
        if (getRangeMinValue() >= getRangeMaxValue()) {
            throw new InvalidSettingsException("The range maximum needs to be larger than the range minimum.");
        }
        if (m_start == null || m_start.length < 1) {
            throw new InvalidSettingsException("At least one start value needs to be specified.");
        }
        for (double start : m_start) {
            if(getFix() != null) {
            	// Round range values to 7 decimals, as the framework is only able to handle inputs with 7 decimals.
            	Double roundedMax = (double)Math.round(getRangeMaxValue()*10000000)/10000000;
            	Double roundedMin = Math.floor(getRangeMinValue()*10000000)/10000000;
            	if (getFix()[0] && start > roundedMax) {
                    throw new InvalidSettingsException("Slider value needs to be inside range bounds.");
            	} else if (getFix()[2] && start < roundedMin) {
                    throw new InvalidSettingsException("Slider value needs to be inside range bounds.");
            	} else if (getFix()[1] && (start < roundedMin || start > roundedMax)) {
                    throw new InvalidSettingsException("Slider value needs to be inside range bounds.");
                }
            }
        }
        if (m_connect != null && m_connect.length != (m_start.length + 1)) {
            throw new InvalidSettingsException("The connect array length needs to be start array length + 1");
        }
        if (m_tooltips != null) {
            if (m_tooltips.length != m_start.length) {
                throw new InvalidSettingsException("Tooltips array length needs to be equal to start array length");
            }
            for (Object tip : m_tooltips) {
                if (tip instanceof NumberFormatSettings) {
                    if (deepValidate) {
                        ((NumberFormatSettings)tip).validateSettings();
                    }
                } else if (!(tip instanceof Boolean)) {
                    throw new InvalidSettingsException(
                        "Tooltip needs to be either Boolean or NumberFormatSettings object.");
                }
            }
        }
        if (deepValidate && m_pips != null) {
            m_pips.validateSettings();
        }
    }

    /**
     * Saves the current state to the given node settings object.
     *
     * @param settings The settings object to save to.
     */
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        NodeSettingsWO rangeSettings = settings.addNodeSettings(CFG_RANGE);
        int rangeSize = m_range == null ? 0 : m_range.size();
        rangeSettings.addInt(NUM_SETTINGS, rangeSize);
        if (rangeSize > 0) {
            Iterator<Entry<String, double[]>> rI = m_range.entrySet().iterator();
            for (int r = 0; r < rangeSize; r++) {
                Entry<String, double[]> entry = rI.next();
                rangeSettings.addString("key_" + r, entry.getKey());
                rangeSettings.addDoubleArray("value_" + r, entry.getValue());
            }
        }
        settings.addDoubleArray(CFG_START, m_start);
        settings.addBooleanArray(CFG_CONNECT, m_connect);
        settings.addBooleanArray(CFG_FIX, m_fix);
        settings.addDoubleArray(CFG_STEP, m_step == null ? null : new double[]{m_step});
        settings.addBooleanArray(CFG_SNAP, m_snap == null ? null : new boolean[]{m_snap});
        settings.addDoubleArray(CFG_MARGIN, m_margin == null ? null : new double[]{m_margin});
        settings.addDoubleArray(CFG_LIMIT, m_limit == null ? null : new double[]{m_limit});
        settings.addString(CFG_ORIENTATION, m_orientation == null ? null : m_orientation.toValue());
        settings.addString(CFG_DIRECTION, m_direction == null ? null : m_direction.toValue());
        NodeSettingsWO tipsSettings = settings.addNodeSettings(CFG_TOOLTIPS);
        int numTips = m_tooltips == null ? 0 : m_tooltips.length;
        tipsSettings.addInt(NUM_SETTINGS, numTips);
        for (int t = 0; t < numTips; t++) {
            Object o = m_tooltips[t];
            String type = "undefined";
            if (o instanceof Boolean) {
                type = "boolean";
                tipsSettings.addBoolean("value_" + t, (Boolean)o);
            } else if (o instanceof NumberFormatSettings) {
                type = "format";
                NodeSettingsWO formatSettings = tipsSettings.addNodeSettings("value_" + t);
                ((NumberFormatSettings)o).saveToNodeSettings(formatSettings);
            }
            tipsSettings.addString("type_" + t, type);
        }
        settings.addBooleanArray(CFG_ANIMATE, m_animate == null ? null : new boolean[]{m_animate});
        settings.addIntArray(CFG_ANIMATION_DURATION,
            m_animationDuration == null ? null : new int[]{m_animationDuration});
        settings.addString(CFG_BEHAVIOUR, m_behaviour);
        NodeSettingsWO pipSettings = settings.addNodeSettings(CFG_PIPS);
        boolean pipsDefined = m_pips != null;
        pipSettings.addBoolean(CFG_PIPS_DEFINED, pipsDefined);
        if (pipsDefined) {
            m_pips.saveToNodeSettings(pipSettings);
        }
    }

    /**
     * Populates the object by loading from the NodeSettings object. The values are validated before being applied. On
     * error this object stays unchanged.
     *
     * @param settings The settings to load from
     * @throws InvalidSettingsException on load or validation error
     */
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        SliderSettings sVal = new SliderSettings();
        sVal.loadValidateSettings(settings);
        sVal.validateSettings(/* already validated during load */ false);
        copyInternals(sVal, this);
    }

    private void loadValidateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO rangeSettings = settings.getNodeSettings(CFG_RANGE);
        int numSettings = rangeSettings.getInt(NUM_SETTINGS);
        m_range = new LinkedHashMap<String, double[]>();
        if (numSettings > 0) {
            for (int r = 0; r < numSettings; r++) {
                String rangeKey = rangeSettings.getString("key_" + r);
                double[] rangeValue = rangeSettings.getDoubleArray("value_" + r);
                m_range.put(rangeKey, rangeValue);
            }
        }
        m_start = settings.getDoubleArray(CFG_START);
        m_connect = settings.getBooleanArray(CFG_CONNECT);
        m_fix = settings.getBooleanArray(CFG_FIX, DEFAULT_FIX_ARRAY);
        double[] stepTemp = settings.getDoubleArray(CFG_STEP);
        m_step = stepTemp == null ? null : stepTemp[0];
        boolean[] snapTemp = settings.getBooleanArray(CFG_SNAP);
        m_snap = snapTemp == null ? null : snapTemp[0];
        double[] marginTemp = settings.getDoubleArray(CFG_MARGIN);
        m_margin = marginTemp == null ? null : marginTemp[0];
        double[] limitTemp = settings.getDoubleArray(CFG_LIMIT);
        m_limit = limitTemp == null ? null : limitTemp[0];
        String orientationTemp = settings.getString(CFG_ORIENTATION);
        m_orientation = orientationTemp == null ? null : Orientation.forValue(orientationTemp);
        String directionTemp = settings.getString(CFG_DIRECTION);
        m_direction = directionTemp == null ? null : Direction.forValue(directionTemp);
        NodeSettingsRO tipsSettings = settings.getNodeSettings(CFG_TOOLTIPS);
        int numTips = tipsSettings.getInt(NUM_SETTINGS);
        if (numTips > 0) {
            m_tooltips = new Object[numTips];
            for (int t = 0; t < numTips; t++) {
                String type = tipsSettings.getString("type_" + t);
                Object value = null;
                if ("boolean".equals(type)) {
                    value = tipsSettings.getBoolean("value_" + t);
                } else if ("format".equals(type)) {
                    NodeSettingsRO formatSettings = tipsSettings.getNodeSettings("value_" + t);
                    value = new NumberFormatSettings();
                    ((NumberFormatSettings)value).loadFromNodeSettings(formatSettings);
                }
                m_tooltips[t] = value;
            }
        } else {
            m_tooltips = null;
        }

        boolean[] animateTemp = settings.getBooleanArray(CFG_ANIMATE);
        m_animate = animateTemp == null ? null : animateTemp[0];
        int[] durationTemp = settings.getIntArray(CFG_ANIMATION_DURATION);
        m_animationDuration = durationTemp == null ? null : durationTemp[0];
        m_behaviour = settings.getString(CFG_BEHAVIOUR);
        NodeSettingsRO pipSettings = settings.getNodeSettings(CFG_PIPS);
        boolean pipsDefined = pipSettings.getBoolean(CFG_PIPS_DEFINED);
        if (pipsDefined) {
            SliderPipsSettings newPips = new SliderPipsSettings();
            newPips.loadFromNodeSettings(pipSettings);
            m_pips = newPips;
        } else {
            m_pips = null;
        }
    }

    /**
     * Loading from NodeSettings object with defaults fallback.
     *
     * @param settings the settings object to load from.
     */
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        try {
            NodeSettingsRO rangeSettings = settings.getNodeSettings(CFG_RANGE);
            int numSettings = rangeSettings.getInt(NUM_SETTINGS);
            m_range = new LinkedHashMap<String, double[]>();
            if (numSettings > 0) {
                for (int r = 0; r < numSettings; r++) {
                    String rangeKey = rangeSettings.getString("key_" + r);
                    double[] rangeValue = rangeSettings.getDoubleArray("value_" + r);
                    m_range.put(rangeKey, rangeValue);
                }
            }
        } catch (InvalidSettingsException e) {
            m_range = null;
        }
        m_start = settings.getDoubleArray(CFG_START, null);
        m_connect = settings.getBooleanArray(CFG_CONNECT, null);
        m_fix = settings.getBooleanArray(CFG_FIX, DEFAULT_FIX_ARRAY);
        double[] stepTemp = settings.getDoubleArray(CFG_STEP, null);
        m_step = stepTemp == null ? null : stepTemp[0];
        boolean[] snapTemp = settings.getBooleanArray(CFG_SNAP, null);
        m_snap = snapTemp == null ? null : snapTemp[0];
        double[] marginTemp = settings.getDoubleArray(CFG_MARGIN, null);
        m_margin = marginTemp == null ? null : marginTemp[0];
        double[] limitTemp = settings.getDoubleArray(CFG_LIMIT, null);
        m_limit = limitTemp == null ? null : limitTemp[0];
        String orientationTemp = settings.getString(CFG_ORIENTATION, null);
        m_orientation = orientationTemp == null ? null : Orientation.forValue(orientationTemp);
        String directionTemp = settings.getString(CFG_DIRECTION, null);
        m_direction = directionTemp == null ? null : Direction.forValue(directionTemp);
        try {
            NodeSettingsRO tipsSettings = settings.getNodeSettings(CFG_TOOLTIPS);
            int numTips = tipsSettings.getInt(NUM_SETTINGS);
            if (numTips > 0) {
                m_tooltips = new Object[numTips];
                for (int t = 0; t < numTips; t++) {
                    String type = tipsSettings.getString("type_" + t);
                    Object value = null;
                    if ("boolean".equals(type)) {
                        value = tipsSettings.getBoolean("value_" + t);
                    } else if ("format".equals(type)) {
                        NodeSettingsRO formatSettings = tipsSettings.getNodeSettings("value_" + t);
                        value = new NumberFormatSettings();
                        ((NumberFormatSettings)value).loadFromNodeSettingsInDialog(formatSettings);
                    }
                    m_tooltips[t] = value;
                }
            } else {
                m_tooltips = null;
            }
        } catch (InvalidSettingsException e) {
            m_tooltips = null;
        }
        boolean[] animateTemp = settings.getBooleanArray(CFG_ANIMATE, null);
        m_animate = animateTemp == null ? null : animateTemp[0];
        int[] durationTemp = settings.getIntArray(CFG_ANIMATION_DURATION, null);
        m_animationDuration = durationTemp == null ? null : durationTemp[0];
        m_behaviour = settings.getString(CFG_BEHAVIOUR, null);
        try {
            NodeSettingsRO pipSettings = settings.getNodeSettings(CFG_PIPS);
            boolean pipsDefined = pipSettings.getBoolean(CFG_PIPS_DEFINED);
            if (pipsDefined) {
                SliderPipsSettings newPips = new SliderPipsSettings();
                newPips.loadFromNodeSettingsInDialog(pipSettings);
                m_pips = newPips;
            } else {
                m_pips = null;
            }
        } catch (InvalidSettingsException e) {
            m_pips = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (m_range != null) {
            builder.append("Range=(");
            double[] min = m_range.get(RANGE_MIN);
            builder.append((min == null || min.length < 1) ? Double.NEGATIVE_INFINITY : min[0]);
            builder.append(" - ");
            double[] max = m_range.get(RANGE_MAX);
            builder.append((max == null || max.length < 1) ? Double.POSITIVE_INFINITY : max[0]);
            builder.append(") ");
        }
        if (m_start != null && m_start.length > 0) {
            builder.append("Start Values=");
            builder.append(Arrays.toString(m_start));
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_range)
                .append(m_start)
                .append(m_connect)
                .append(m_fix)
                .append(m_step)
                .append(m_snap)
                .append(m_margin)
                .append(m_limit)
                .append(m_orientation)
                .append(m_direction)
                .append(m_tooltips)
                .append(m_animate)
                .append(m_animationDuration)
                .append(m_behaviour)
                .append(m_pips)
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
        SliderSettings other = (SliderSettings)obj;
        return new EqualsBuilder()
                .append(m_range, other.m_range)
                .append(m_start, other.m_start)
                .append(m_connect, other.m_connect)
                .append(m_fix, other.m_fix)
                .append(m_step, other.m_step)
                .append(m_snap, other.m_snap)
                .append(m_margin, other.m_margin)
                .append(m_limit, other.m_limit)
                .append(m_orientation, other.m_orientation)
                .append(m_direction, other.m_direction)
                .append(m_tooltips, other.m_tooltips)
                .append(m_animate, other.m_animate)
                .append(m_animationDuration, other.m_animationDuration)
                .append(m_behaviour, other.m_behaviour)
                .append(m_pips, other.m_pips)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public SliderSettings clone() {
        SliderSettings clonedSettings = new SliderSettings();
        copyInternals(this, clonedSettings);
        return clonedSettings;
    }

    @SuppressWarnings("unchecked")
    private static synchronized void copyInternals(final SliderSettings settingsFrom, final SliderSettings settingsTo) {
        settingsTo.m_range = settingsFrom.m_range == null ? null : new LinkedHashMap<String, double[]>();
        if (settingsFrom.m_range != null) {
            Iterator<Entry<String, double[]>> i = settingsFrom.m_range.entrySet().iterator();
            while (i.hasNext()) {
                Entry<String, double[]> entry = i.next();
                double[] valueFrom = entry.getValue();
                double[] value = valueFrom == null ? null : Arrays.copyOf(valueFrom, valueFrom.length);
                settingsTo.m_range.put(entry.getKey(), value);
            }
        }
        settingsTo.m_range = (LinkedHashMap<String, double[]>)settingsFrom.m_range.clone();
        settingsTo.m_start =
            settingsFrom.m_start == null ? null : Arrays.copyOf(settingsFrom.m_start, settingsFrom.m_start.length);
        settingsTo.m_connect = settingsFrom.m_connect == null ? null
            : Arrays.copyOf(settingsFrom.m_connect, settingsFrom.m_connect.length);
        settingsTo.m_fix = settingsFrom.m_fix == null ? null
            : Arrays.copyOf(settingsFrom.m_fix, settingsFrom.m_fix.length);
        settingsTo.m_step = settingsFrom.m_step;
        settingsTo.m_snap = settingsFrom.m_snap;
        settingsTo.m_margin = settingsFrom.m_margin;
        settingsTo.m_limit = settingsFrom.m_limit;
        settingsTo.m_orientation = settingsFrom.m_orientation;
        settingsTo.m_direction = settingsFrom.m_direction;
        settingsTo.m_tooltips = settingsFrom.m_tooltips == null ? null
            : Arrays.copyOf(settingsFrom.m_tooltips, settingsFrom.m_tooltips.length);
        if (settingsTo.m_tooltips != null) {
            for (int i = 0; i < settingsTo.m_tooltips.length; i++) {
                if (settingsTo.m_tooltips[i] instanceof NumberFormatSettings) {
                    settingsTo.m_tooltips[i] = ((NumberFormatSettings)settingsTo.m_tooltips[i]).clone();
                }
            }
        }
        settingsTo.m_animate = settingsFrom.m_animate;
        settingsTo.m_animationDuration = settingsFrom.m_animationDuration;
        settingsTo.m_behaviour = settingsFrom.m_behaviour;
        settingsTo.m_pips = settingsFrom.m_pips == null ? null : settingsFrom.m_pips.clone();
    }

}
