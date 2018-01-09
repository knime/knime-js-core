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
 *   2 Feb 2017 (albrecht): created
 */
package org.knime.js.core.selections.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.data.RowKey;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteManager;
import org.knime.core.node.property.hilite.HiLiteMapper;
import org.knime.core.node.property.hilite.HiLiteTranslator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
public class JSONSelectionTranslator {

    private String m_sourceID;
    private List<String> m_targetIDs;
    private boolean m_forward;
    private Map<String, List<String>> m_mapping;

    /**
     * Creates a new {@link JSONSelectionTranslator} instance from a given {@link HiLiteManager}.
     * Only source and target IDs are retrieved and no mapping is provided.
     * @param hiliteManager the {@link HiLiteManager} to create the selection translator from
     */
    public JSONSelectionTranslator(final HiLiteManager hiliteManager) {
        if (hiliteManager == null) {
            return;
        }
        setHiliteHandlers(hiliteManager.getFromHiLiteHandler(), hiliteManager.getToHiLiteHandlers());
        m_forward = true;
    }

    /**
     * Creates a new {@link JSONSelectionTranslator} instance from a given {@link HiLiteTranslator}.
     * Source and target IDs are retrieved and, if present, a mapping is provided.
     * @param hiliteTranslator the {@link HiLiteTranslator} to create the selection translator from
     */
    public JSONSelectionTranslator(final HiLiteTranslator hiliteTranslator) {
        if (hiliteTranslator == null) {
            return;
        }
        setHiliteHandlers(hiliteTranslator.getFromHiLiteHandler(), hiliteTranslator.getToHiLiteHandlers());
        HiLiteMapper mapper = hiliteTranslator.getMapper();
        if (mapper == null) {
            return;
        }
        m_forward = false;
        m_mapping = new HashMap<String, List<String>>();
        for (RowKey key : mapper.keySet()) {
            Set<RowKey> mappedSet = mapper.getKeys(key);
            if (mappedSet != null) {
                List<String> values = Arrays.asList(RowKey.toStrings(mappedSet.toArray(new RowKey[0])));
                m_mapping.put(key.toString(), values);
            }
        }
    }

    private void setHiliteHandlers(final HiLiteHandler fromHiLiteHandler, final Set<HiLiteHandler> toHiLiteHandlers) {
        if (fromHiLiteHandler == null || toHiLiteHandlers == null || toHiLiteHandlers.size() < 1) {
            return;
        }
        m_sourceID = fromHiLiteHandler.getHiliteHandlerID().toString();
        m_targetIDs = new ArrayList<String>(toHiLiteHandlers.size());
        for (HiLiteHandler toHiliteHandler : toHiLiteHandlers) {
            m_targetIDs.add(toHiliteHandler.getHiliteHandlerID().toString());
        }
    }

    /**
     * @return the sourceID
     */
    public String getSourceID() {
        return m_sourceID;
    }

    /**
     * @return the targetIDs
     */
    public List<String> getTargetIDs() {
        return m_targetIDs;
    }

    /**
     * @return the forward
     */
    public boolean isForward() {
        return m_forward;
    }

    /**
     * @return the mapping
     */
    public Map<String, List<String>> getMapping() {
        return m_mapping;
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
        JSONSelectionTranslator other = (JSONSelectionTranslator)obj;
        return new EqualsBuilder()
                .append(m_sourceID, other.m_sourceID)
                .append(m_targetIDs, other.m_targetIDs)
                .append(m_forward, other.m_forward)
                .append(m_mapping, other.m_mapping)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_sourceID)
                .append(m_targetIDs)
                .append(m_forward)
                .append(m_mapping)
                .toHashCode();
    }
}
