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
 *   Apr 4, 2021 (hornm): created
 */
package org.knime.core.wizard.rpc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.wizard.CompositeViewPageManager;
import org.knime.js.core.JSONWebNodePage;

/**
 * Default implementation of {@link ReexecutionService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultReexecutionService implements ReexecutionService {

    private SingleNodeContainer m_container;

    private CompositeViewPageManager m_cvm;

    private NodeID m_resetNodeId;

    /**
     * @param container
     * @param cvm
     *
     */
    public DefaultReexecutionService(final SingleNodeContainer container, final CompositeViewPageManager cvm) {
        m_container = container;
        m_cvm = cvm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageContainer reexecutePage(final String nodeID, final Map<String, String> viewValues) {
        // validate view values and re-execute
        NodeID resetNodeId = NodeID.fromString(nodeID);
        NodeID containerId = m_container.getID();
        try (WorkflowLock lock = m_container.getParent().lock()) {
            // TODO properly respect and set the 'isReexecuteInProgress' flag in SubnodeViewableModel??
            Map<String, ValidationError> validationErrors =
                m_cvm.applyPartialValuesAndReexecute(viewValues, containerId, resetNodeId);
            if (validationErrors != null && !validationErrors.isEmpty()) {
                // TODO
                throw new IllegalStateException(
                    "Unable to re-execute component with current page values. Please check the workflow for errors.");
            }
        }

        // create response
        List<String> resetNodes = m_cvm.getDownstreamNodes(containerId, resetNodeId);
        String page;
        if (m_container.getNodeContainerState().isExecuted()) {
            try {
                page = filterAndGetSerializedJSONWebNodePage(resetNodes);
            } catch (IOException ex) {
                // TODO
                throw new IllegalStateException(ex);
            }
            resetNodes = null;
            m_resetNodeId = resetNodeId;
        } else {
            page = null;
            m_resetNodeId = resetNodeId;
        }
        return new DefaultPageContainer(page, resetNodes);
    }

    private String filterAndGetSerializedJSONWebNodePage(final List<String> resetNodeIDs) throws IOException {
        JSONWebNodePage page = m_cvm.createWizardPage(m_container.getID());
        page.filterWebNodesById(resetNodeIDs);
        try (OutputStream pageStream = page.saveToStream()) {
            return pageStream.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageContainer getPage() {
        try {
            CheckUtils.checkNotNull(m_resetNodeId, "Reset node ID must be defined for updated page response");
            String page =
                filterAndGetSerializedJSONWebNodePage(m_cvm.getDownstreamNodes(m_container.getID(), m_resetNodeId));
            return new DefaultPageContainer(page, null);
        } catch (IOException ex) {
            // TODO
            throw new IllegalStateException(ex);
        }
    }

}
