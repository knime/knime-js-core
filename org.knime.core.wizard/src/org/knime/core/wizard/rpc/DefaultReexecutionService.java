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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.util.Pair;
import org.knime.core.wizard.CompositeViewPageManager;
import org.knime.js.core.JSONWebNodePage;

import com.fasterxml.jackson.databind.util.RawValue;

/**
 * Default implementation of {@link ReexecutionService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 4.5
 *
 */
public final class DefaultReexecutionService implements ReexecutionService {

    private final SingleNodeContainer m_page;

    private final CompositeViewPageManager m_cvm;

    private NodeID m_resetNodeId;

    private List<String> m_resetNodes;

    private List<String> m_reexecutedNodes;

    private final Runnable m_onReexecutionStart;

    private final Runnable m_onReexecutionEnd;

    /**
     * @param page
     * @param cvm
     * @param onReexecutionStart will be called whenever a re-execution is triggered, can be <code>null</code>
     * @param onReexecutionEnd will be called whenever a re-execution finishes, can be <code>null</code>
     */
    public DefaultReexecutionService(final SingleNodeContainer page, final CompositeViewPageManager cvm,
        final Runnable onReexecutionStart, final Runnable onReexecutionEnd) {
        m_page = page;
        m_cvm = cvm;
        m_onReexecutionStart = onReexecutionStart;
        m_onReexecutionEnd = onReexecutionEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageContainer reexecutePage(final String nodeIDSuffix, final Map<String, String> viewValues) {
        if (m_onReexecutionStart != null) {
            m_onReexecutionStart.run();
        }
        // validate view values and re-execute
        var pageId = m_page.getID();
        var resetNodeId =
            NodeIDSuffix.fromString(nodeIDSuffix).prependParent(m_cvm.getWorkflowManager().getProjectWFM().getID());
        try (WorkflowLock lock = m_page.getParent().lock()) {
            Map<String, ValidationError> validationErrors =
                m_cvm.applyPartialValuesAndReexecute(viewValues, pageId, resetNodeId);
            if (validationErrors != null && !validationErrors.isEmpty()) {
                throw new IllegalStateException(
                    "Unable to re-execute component with current page values. Validation errors: " + validationErrors
                        .values().stream().map(ValidationError::getError).collect(Collectors.joining(";")));
            }
        }

        // create response
        m_resetNodes = getSuccessorWizardNodesWithinComponent(resetNodeId, null).stream().collect(Collectors.toList());
        String page;
        if (m_page.getNodeContainerState().isExecuted()) {
            page = filterAndGetSerializedJSONWebNodePage(m_resetNodes);
            m_reexecutedNodes = m_resetNodes;
            m_resetNodes = null;
            m_resetNodeId = resetNodeId;
        } else {
            page = null;
            m_resetNodeId = resetNodeId;
            m_reexecutedNodes = Collections.emptyList();
        }
        if (page != null && m_onReexecutionEnd != null) {
            m_onReexecutionEnd.run();
        }
        return new DefaultPageContainer(new RawValue(page), m_resetNodes, m_reexecutedNodes);
    }

    private String filterAndGetSerializedJSONWebNodePage(final List<String> resetNodeIDs) {
        JSONWebNodePage page;
        try {
            page = m_cvm.createWizardPage(m_page.getID());
            page.filterWebNodesById(resetNodeIDs);
            try (OutputStream pageStream = page.saveToStream()) {
                return ((ByteArrayOutputStream)pageStream).toString(StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Problem occurred while serializing page", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageContainer getPage() {
        CheckUtils.checkNotNull(m_resetNodeId, "Reset node ID must be defined for updated page response");
        var pageState = m_page.getNodeContainerState();
        String page;
        if(pageState.isExecutionInProgress() || pageState.isWaitingToBeExecuted()) {
            page = null;
            m_reexecutedNodes = getSuccessorWizardNodesWithinComponent(m_resetNodeId,
                nc -> !nc.getNodeContainerState().isWaitingToBeExecuted()
                    && !nc.getNodeContainerState().isExecutionInProgress());
        } else {
            page = filterAndGetSerializedJSONWebNodePage(getSuccessorWizardNodesWithinComponent(m_resetNodeId, null));
            m_reexecutedNodes = m_resetNodes;
            m_resetNodes = null;
        }
        if (page != null && m_onReexecutionEnd != null) {
            m_onReexecutionEnd.run();
        }
        return new DefaultPageContainer(new RawValue(page), m_resetNodes, m_reexecutedNodes);
    }

    private List<String> getSuccessorWizardNodesWithinComponent(final NodeID resetNodeId,
        final Predicate<NodeContainer> nodeFilter) {
        NodeID pageId = m_page.getID();
        Stream<Pair<NodeIDSuffix, NodeContainer>> res =
            WizardPageUtil.getSuccessorWizardPageNodesWithinComponent(m_cvm.getWorkflowManager(), pageId,
                resetNodeId);
        if (nodeFilter != null) {
            res = res.filter(p -> nodeFilter.test(p.getSecond()));
        }
        return res.map(p -> p.getFirst().toString()).collect(Collectors.toList());
    }

}
