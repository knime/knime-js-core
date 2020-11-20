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
 *   Nov 10, 2020 (bogenrieder): created
 */
package org.knime.js.core.layout.bs;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
*
* @author Daniel Bogenrieder, KNIME.com GmbH, Konstanz, Germany
 * @since 4.3
*/
@JsonAutoDetect
public class JSONLayoutConfigurationContent extends JSONLayoutElement implements JSONLayoutContent, Cloneable {

    // general fields
    private String m_nodeID;

    /**
     * Creates a new view content element for a JSON layout, assuming defaults
     */
    public JSONLayoutConfigurationContent() {

    }

    /**
     * @return the nodeID
     */
    public String getNodeID() {
        return m_nodeID;
    }

    /**
     * @param nodeID the nodeID to set
     */
    public void setNodeID(final String nodeID) {
        m_nodeID = nodeID;
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
        JSONLayoutConfigurationContent other = (JSONLayoutConfigurationContent)obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(m_nodeID, other.m_nodeID)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(m_nodeID)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public JSONLayoutConfigurationContent clone() {
        JSONLayoutConfigurationContent clone = new JSONLayoutConfigurationContent();
        clone.m_nodeID = m_nodeID;
        ArrayList<String> additionalClasses = null;
        if (getAdditionalClasses() != null) {
            additionalClasses = new ArrayList<String>();
            additionalClasses.addAll(getAdditionalClasses());
        }
        clone.setAdditionalClasses(additionalClasses);
        ArrayList<String> additionalStyles = null;
        if (getAdditionalStyles() != null) {
            additionalStyles = new ArrayList<String>();
            additionalStyles.addAll(getAdditionalStyles());
        }
        clone.setAdditionalStyles(additionalStyles);
        return clone;
    }

}
