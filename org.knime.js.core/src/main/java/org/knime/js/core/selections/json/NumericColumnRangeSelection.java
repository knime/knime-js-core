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
 *   18 Aug 2016 (albrecht): created
 */
package org.knime.js.core.selections.json;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
public class NumericColumnRangeSelection extends AbstractColumnRangeSelection {

    private static final String CFG_MINIMUM = "minimum";
    private static final double DEFAULT_MINIMUM = Double.NEGATIVE_INFINITY;
    private double m_minimum = DEFAULT_MINIMUM;

    private static final String CFG_MAXIMUM = "maximum";
    private static final double DEFAULT_MAXIMUM = Double.POSITIVE_INFINITY;
    private double m_maximum = DEFAULT_MAXIMUM;

    private static final String CFG_MINIMUM_INCLUSIVE = "minimumInclusive";
    private static final boolean DEFAULT_MINIMUM_INCLUSIVE = true;
    private boolean m_minimumInclusive = DEFAULT_MINIMUM_INCLUSIVE;

    private static final String CFG_MAXIMUM_INCLUSIVE = "maximumInclusive";
    private static final boolean DEFAULT_MAXIMUM_INCLUSIVE = true;
    private boolean m_maximumInclusive = DEFAULT_MAXIMUM_INCLUSIVE;

    /**
     * @return the minimum
     */
    public double getMinimum() {
        return m_minimum;
    }

    /**
     * @param minimum the minimum to set
     */
    public void setMinimum(final double minimum) {
        m_minimum = minimum;
    }

    /**
     * @return the maximum
     */
    public double getMaximum() {
        return m_maximum;
    }

    /**
     * @param maximum the maximum to set
     */
    public void setMaximum(final double maximum) {
        m_maximum = maximum;
    }

    /**
     * @return the minimumInclusive
     */
    public boolean getMinimumInclusive() {
        return m_minimumInclusive;
    }

    /**
     * @param minimumInclusive the minimumInclusive to set
     */
    public void setMinimumInclusive(final boolean minimumInclusive) {
        m_minimumInclusive = minimumInclusive;
    }

    /**
     * @return the maximumInclusive
     */
    public boolean getMaximumInclusive() {
        return m_maximumInclusive;
    }

    /**
     * @param maximumInclusive the maximumInclusive to set
     */
    public void setMaximumInclusive(final boolean maximumInclusive) {
        m_maximumInclusive = maximumInclusive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addDouble(CFG_MINIMUM, m_minimum);
        settings.addDouble(CFG_MAXIMUM, m_maximum);
        settings.addBoolean(CFG_MINIMUM_INCLUSIVE, m_minimumInclusive);
        settings.addBoolean(CFG_MAXIMUM_INCLUSIVE, m_maximumInclusive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_minimum = settings.getDouble(CFG_MINIMUM);
        m_maximum = settings.getDouble(CFG_MAXIMUM);
        m_minimumInclusive = settings.getBoolean(CFG_MINIMUM_INCLUSIVE);
        m_maximumInclusive = settings.getBoolean(CFG_MAXIMUM_INCLUSIVE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_minimum = settings.getDouble(CFG_MINIMUM, DEFAULT_MINIMUM);
        m_maximum = settings.getDouble(CFG_MAXIMUM, DEFAULT_MAXIMUM);
        m_minimumInclusive = settings.getBoolean(CFG_MINIMUM_INCLUSIVE, DEFAULT_MINIMUM_INCLUSIVE);
        m_maximumInclusive = settings.getBoolean(CFG_MAXIMUM_INCLUSIVE, DEFAULT_MAXIMUM_INCLUSIVE);
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
        NumericColumnRangeSelection other = (NumericColumnRangeSelection)obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(m_minimum, other.m_minimum)
                .append(m_maximum, other.m_maximum)
                .append(m_minimumInclusive, other.m_minimumInclusive)
                .append(m_maximumInclusive, other.m_maximumInclusive)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(m_minimum)
                .append(m_maximum)
                .append(m_minimumInclusive)
                .append(m_maximumInclusive)
                .toHashCode();
    }

}
