/*
 * ------------------------------------------------------------------------
 *
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
 *   Jun 13, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.input.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

/**
 * The config for the date input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class DateInputQuickFormConfig extends QuickFormFlowVariableConfig<DateInputQuickFormValue> {

    private static final String CFG_USE_MIN = "use_min";
    private static final boolean DEFAULT_USE_MIN = false;
    private boolean m_useMin = DEFAULT_USE_MIN;
    private static final String CFG_USE_MAX = "use_max";
    private static final boolean DEFAULT_USE_MAX = false;
    private boolean m_useMax = DEFAULT_USE_MAX;
    private static final String CFG_MIN = "min";
    private static final Date DEFAULT_MIN = DateInputQuickFormValue.DEFAULT_DATE;
    private Date m_min = DEFAULT_MIN;
    private static final String CFG_MAX = "max";
    private static final Date DEFAULT_MAX = DateInputQuickFormValue.DEFAULT_DATE;
    private Date m_max = DEFAULT_MAX;
    private static final String CFG_WITH_TIME = "with_time";
    private static final boolean DEFAULT_WITH_TIME = true;
    private boolean m_withTime = DEFAULT_WITH_TIME;

    /**
     * @return the useMin
     */
    boolean getUseMin() {
        return m_useMin;
    }

    /**
     * @param useMin the useMin to set
     */
    void setUseMin(final boolean useMin) {
        m_useMin = useMin;
    }

    /**
     * @return the useMax
     */
    boolean getUseMax() {
        return m_useMax;
    }

    /**
     * @param useMax the useMax to set
     */
    void setUseMax(final boolean useMax) {
        m_useMax = useMax;
    }

    /**
     * @return the min
     */
    Date getMin() {
        return m_min;
    }

    /**
     * @param min the min to set
     */
    void setMin(final Date min) {
        m_min = min;
    }

    /**
     * @return the max
     */
    Date getMax() {
        return m_max;
    }

    /**
     * @param max the max to set
     */
    void setMax(final Date max) {
        m_max = max;
    }

    /**
     * @return the withTime
     */
    boolean getWithTime() {
        return m_withTime;
    }

    /**
     * @param withTime the withTime to set
     */
    void setWithTime(final boolean withTime) {
        m_withTime = withTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        SimpleDateFormat sdf = new SimpleDateFormat(DateInputQuickFormNodeModel.DATE_TIME_FORMAT);
        settings.addBoolean(CFG_USE_MIN, m_useMin);
        settings.addBoolean(CFG_USE_MAX, m_useMax);
        settings.addString(CFG_MIN, sdf.format(m_min));
        settings.addString(CFG_MAX, sdf.format(m_max));
        settings.addBoolean(CFG_WITH_TIME, m_withTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        SimpleDateFormat sdf = new SimpleDateFormat(DateInputQuickFormNodeModel.DATE_TIME_FORMAT);
        m_useMin = settings.getBoolean(CFG_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX);
        m_withTime = settings.getBoolean(CFG_WITH_TIME);
        try {
            m_min = sdf.parse(settings.getString(CFG_MIN));
            m_max = sdf.parse(settings.getString(CFG_MAX));
        } catch (ParseException e) {
            throw new InvalidSettingsException("Could not parse date format", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        SimpleDateFormat sdf = new SimpleDateFormat(DateInputQuickFormNodeModel.DATE_TIME_FORMAT);
        m_useMin = settings.getBoolean(CFG_USE_MIN, DEFAULT_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX, DEFAULT_USE_MAX);
        m_withTime = settings.getBoolean(CFG_WITH_TIME, DEFAULT_WITH_TIME);
        try {
            m_min = sdf.parse(settings.getString(CFG_MIN, sdf.format(DEFAULT_MIN)));
            m_max = sdf.parse(settings.getString(CFG_MAX, sdf.format(DEFAULT_MAX)));
        } catch (ParseException e) {
            m_min = DEFAULT_MIN;
            m_max = DEFAULT_MAX;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateInputQuickFormValue createEmptyValue() {
        return new DateInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("useMin=");
        sb.append(m_useMin);
        sb.append(", ");
        sb.append("useMax=");
        sb.append(m_useMax);
        sb.append(", ");
        sb.append("min=");
        sb.append("{");
        sb.append(m_min);
        sb.append("}");
        sb.append(", ");
        sb.append("max=");
        sb.append("{");
        sb.append(m_max);
        sb.append("}");
        sb.append(", ");
        sb.append("withTime=");
        sb.append(m_withTime);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_useMin)
                .append(m_useMax)
                .append(m_min)
                .append(m_max)
                .append(m_withTime)
                .toHashCode();
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
        DateInputQuickFormConfig other = (DateInputQuickFormConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_useMin, other.m_useMin)
                .append(m_useMax, other.m_useMax)
                .append(m_min, other.m_min)
                .append(m_max, other.m_max)
                .append(m_withTime, other.m_withTime)
                .isEquals();
    }

}
