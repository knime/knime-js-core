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
 * Created on 08.09.2017 by Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
package org.knime.js.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.workflow.NodeContainerState;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 *
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONWebNodeInfo {

    /**
     * JSON serializable simple enum for {@link NodeContainerState}
     * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
     */
    public static enum JSONNodeState {
        /** node is idle (unconfigured, not queued, not marked) */
        IDLE,
        /** node is configured (not queued, not marked) */
        CONFIGURED,
        /** node is executing or waiting to be executed (marked, queued, executing) */
        EXECUTING,
        /** node is executed and not marked for re-execution */
        EXECUTED;

        private static Map<String, JSONNodeState> namesMap = new HashMap<String, JSONNodeState>(6);
        static {
            namesMap.put("idle", IDLE);
            namesMap.put("configured", CONFIGURED);
            namesMap.put("executing", EXECUTING);
            namesMap.put("executed", EXECUTED);
        }

        /**
         * @param value a String representing a {@link JSONNodeState}
         * @return a valid {@link JSONNodeState} for the given value
         * @throws JsonMappingException if value cannot be resolved
         */
        @JsonCreator
        public static JSONNodeState forValue(final String value) throws JsonMappingException{
            JSONNodeState method = namesMap.get(value);
            if (method == null) {
                throw new JsonMappingException(null, value + " is not a valid node state.");
            }
            return method;
        }

        /**
         * @return the string value for this {@link JSONNodeState}
         */
        @JsonValue
        public String toValue() {
            for (Entry<String, JSONNodeState> entry : namesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }
            return null;
        }
    }

    private JSONNodeState m_nodeState;
    private boolean m_displayPossible;
    private String m_nodeErrorMessage;
    private String m_nodeWarnMessage;

    /**
     * @return the nodeState
     */
    public JSONNodeState getNodeState() {
        return m_nodeState;
    }

    /**
     * @param nodeState the nodeState to set
     */
    public void setNodeState(final JSONNodeState nodeState) {
        m_nodeState = nodeState;
    }

    /**
     * @return true if node can be displayed, false otherwise
     */
    public boolean isDisplayPossible() {
        return m_displayPossible;
    }

    /**
     * @param displayPossible true if node can be displayed, false otherwise
     */
    public void setDisplayPossible(final boolean displayPossible) {
        m_displayPossible = displayPossible;
    }

    /**
     * @return the error message
     */
    public String getNodeErrorMessage() {
        return m_nodeErrorMessage;
    }

    /**
     * @param nodeErrorMessage the error message to set
     */
    public void setNodeErrorMessage(final String nodeErrorMessage) {
        m_nodeErrorMessage = nodeErrorMessage;
    }

    /**
     * @return the warn message
     */
    public String getNodeWarnMessage() {
        return m_nodeWarnMessage;
    }

    /**
     * @param nodeWarnMessage the warn message to set
     */
    public void setNodeWarnMessage(final String nodeWarnMessage) {
        m_nodeWarnMessage = nodeWarnMessage;
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
        JSONWebNodeInfo other = (JSONWebNodeInfo)obj;
        return new EqualsBuilder()
                .append(m_nodeState, other.m_nodeState)
                .append(m_nodeErrorMessage, other.m_nodeErrorMessage)
                .append(m_nodeWarnMessage, other.m_nodeWarnMessage)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_nodeState)
                .append(m_nodeErrorMessage)
                .append(m_nodeWarnMessage)
                .toHashCode();
    }

}
