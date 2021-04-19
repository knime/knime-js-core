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
 *   Mar 16, 2021 (ben.laney): created
 */
package org.knime.js.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.StringSanitizationSerializer.JsonSanitizeIgnore;

/**
 * Simple mock sub-class for testing {@link JSONViewContent}.
 *
 * @author ben.laney
 */
public class JSONMockContent extends JSONViewContent {

    private InnerPojo m_value;

    /**
     * Mock nested class
     */
    public JSONMockContent() {
        m_value = new InnerPojo();
    }

    /**
     * @return the value
     */
    public InnerPojo getValue() {
        return m_value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(final InnerPojo value) {
        m_value = value;
    }

    /**
     * @param pojoValue
     */
    public void setPojoValue(final String pojoValue) {
        m_value.setInnerValue(pojoValue);
    }

    /**
     * @param pojoIgnoredValue
     */
    public void setPojoIgnoredValue(final String pojoIgnoredValue) {
        m_value.setIgnoredInnerValue(pojoIgnoredValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        /* mock, unused */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        /* mock, unused */
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
        JSONMockContent other = (JSONMockContent)obj;
        return new EqualsBuilder().append(m_value, other.m_value).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_value).toHashCode();
    }

    static class InnerPojo {
        private String m_innerValue;
        private String m_ignoredInnerValue;

        /**
         * @return the innerValue
         */
        public String getInnerValue() {
            return m_innerValue;
        }

        /**
         * @param innerValue the innerValue to set
         */
        public void setInnerValue(final String innerValue) {
            m_innerValue = innerValue;
        }

        /**
         * @return the ignoredInnerValue
         */
        @JsonSanitizeIgnore
        public String getIgnoredInnerValue() {
            return m_ignoredInnerValue;
        }

        /**
         * @param ignoredInnerValue the ignoredInnerValue to set
         */
        public void setIgnoredInnerValue(final String ignoredInnerValue) {
            m_ignoredInnerValue = ignoredInnerValue;
        }
    }

}
