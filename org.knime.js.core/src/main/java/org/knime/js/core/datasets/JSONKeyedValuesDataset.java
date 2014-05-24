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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONKeyedValuesDataset {

    private KeyedValuesSection[] m_sections;

    /** Serialization constructor. Don't use. */
    public JSONKeyedValuesDataset() { }

    /**
     * @param sections
     */
    public JSONKeyedValuesDataset(final KeyedValuesSection[] sections) {
        m_sections = sections;
    }

    /**
     * @return the sections
     */
    public KeyedValuesSection[] getSections() {
        return m_sections;
    }

    /**
     * @param sections the sections to set
     */
    public void setSections(final KeyedValuesSection[] sections) {
        m_sections = sections;
    }

    /**
     *
     * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
     */
    @JsonAutoDetect
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    static class KeyedValuesSection {

        private String m_sectionKey;

        private double m_value;

        /** Serialization constructor. Don't use. */
        public KeyedValuesSection() { }

        /**
         * @param sectionKey
         * @param value
         */
        public KeyedValuesSection(final String sectionKey, final double value) {
            m_sectionKey = sectionKey;
            m_value = value;
        }

        /**
         * @return the sectionKey
         */
        public String getSectionKey() {
            return m_sectionKey;
        }

        /**
         * @param sectionKey the sectionKey to set
         */
        public void setSectionKey(final String sectionKey) {
            m_sectionKey = sectionKey;
        }

        /**
         * @return the value
         */
        public double getValue() {
            return m_value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(final double value) {
            m_value = value;
        }

    }

}
