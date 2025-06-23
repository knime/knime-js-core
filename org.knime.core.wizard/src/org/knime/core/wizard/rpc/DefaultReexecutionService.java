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
import java.util.ArrayList;
import java.util.Collection;
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
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.util.Pair;
import org.knime.core.wizard.CompositeViewPageManager;
import org.knime.core.wizard.WizardPageCreationHelper;
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

    private final CompositeViewPageManager m_compositeViewPageManager;

    private Collection<NodeID> m_resetNodeIds;

    private List<String> m_resetNodes;

    private List<String> m_reexecutedNodes;

    private final Runnable m_onReexecutionStart;

    private final Runnable m_onReexecutionEnd;

    private final WizardPageCreationHelper m_pageCreationHelper;

    /**
     * @param page
     * @param pageCreationHelper helps to control the wizard page creation process
     * @param cvm
     * @param onReexecutionStart will be called whenever a re-execution is triggered, can be <code>null</code>
     * @param onReexecutionEnd will be called whenever a re-execution finishes, can be <code>null</code>
     *
     * @since 5.2
     */
    public DefaultReexecutionService(final SingleNodeContainer page, final WizardPageCreationHelper pageCreationHelper,
        final CompositeViewPageManager cvm, final Runnable onReexecutionStart, final Runnable onReexecutionEnd) {
        m_page = page;
        m_pageCreationHelper = pageCreationHelper;
        m_compositeViewPageManager = cvm;
        m_onReexecutionStart = onReexecutionStart;
        m_onReexecutionEnd = onReexecutionEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageContainer triggerComponentReexecution(final String resetNodeIDSuffix,
        final Map<String, String> viewValues) {
        if (m_onReexecutionStart != null) {
            m_onReexecutionStart.run();
        }
        // validate view values and re-execute
        var pageId = m_page.getID();
        var resetNodeId = NodeIDSuffix.fromString(resetNodeIDSuffix)
            .prependParent(m_compositeViewPageManager.getWorkflowManager().getProjectWFM().getID());

        try (WorkflowLock lock = m_page.getParent().lock()) {
            Map<String, ValidationError> validationErrors =
                m_compositeViewPageManager.applyPartialValuesAndReexecute(viewValues, pageId, List.of(resetNodeId));
            if (validationErrors != null && !validationErrors.isEmpty()) {
                throw new IllegalStateException(
                    "Unable to re-execute component with current page values. Validation errors: " + validationErrors
                        .values().stream().map(ValidationError::getError).collect(Collectors.joining(";")));
            }
        }

        // create response
        m_resetNodes = getSuccessorWizardNodesWithinComponent(resetNodeId, null);
        String page;
        if (m_page.getNodeContainerState().isExecuted()) {
            page = filterAndGetSerializedJSONWebNodePage(m_resetNodes);
            m_reexecutedNodes = m_resetNodes;
            m_resetNodes = null;
            m_resetNodeIds = List.of(resetNodeId);
        } else {
            page = null;
            m_resetNodeIds = List.of(resetNodeId);
            m_reexecutedNodes = Collections.emptyList();
        }
        if (page != null && m_onReexecutionEnd != null) {
            m_onReexecutionEnd.run();
        }
        return new DefaultPageContainer(new RawValue(page), m_resetNodes, m_reexecutedNodes);
    }

    /**
     *
     * Does not set m_resetNodeIds, use extended getPage to poll
     *
     * @param snc the container of the component
     * @param viewValues
     * @return the re-executed or re-executing page
     */
    @Override
    public PageContainer triggerCompleteComponentReexecution(final Map<String, String> viewValues) {
        if (m_onReexecutionStart != null) {
            m_onReexecutionStart.run();
        }
        var pageId = m_page.getID();

        ArrayList<NodeID> resetNodeIds = new ArrayList<>();
        try (WorkflowLock lock = m_page.getParent().lock()) {

            var parent = m_compositeViewPageManager.getWorkflowManager().getProjectWFM().getID();

            for (var dirtyNode : viewValues.keySet()) {
                resetNodeIds.add( //
                    NodeIDSuffix.fromString(dirtyNode).prependParent(parent));
            }

            Map<String, ValidationError> validationErrors =
                m_compositeViewPageManager.applyPartialValuesAndReexecute(viewValues, pageId, resetNodeIds);
            if (validationErrors != null && !validationErrors.isEmpty()) {
                throw new IllegalStateException(
                    "Unable to re-execute component with current page values. Validation errors: " + validationErrors
                        .values().stream().map(ValidationError::getError).collect(Collectors.joining(";")));
            }
        }

        assert (m_page instanceof SubNodeContainer);
        m_resetNodes = getSuccessorWizardNodesWithinComponent(resetNodeIds, nc -> true);
        String page;
        if (m_page.getNodeContainerState().isExecuted()) {
            page = filterAndGetSerializedJSONWebNodePage(m_resetNodes);
            m_reexecutedNodes = m_resetNodes;
            m_resetNodes = null;
        } else {
            page = null;
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
            page = m_compositeViewPageManager.createWizardPage(m_page.getID(), m_pageCreationHelper);
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
    public PageContainer pollComponentReexecutionStatus(final String resetNodeIDSuffix) {
        var resetNodeId = NodeIDSuffix.fromString(resetNodeIDSuffix)
            .prependParent(m_compositeViewPageManager.getWorkflowManager().getProjectWFM().getID());
        var resetNodes =
            getSuccessorWizardNodesWithinComponent(resetNodeId, null).stream().collect(Collectors.toList());
        var pageState = m_page.getNodeContainerState();
        String page;
        List<String> reexecutedNodes;
        if (pageState.isExecutionInProgress() || pageState.isWaitingToBeExecuted()) {
            page = null;
            reexecutedNodes = getSuccessorWizardNodesWithinComponent(resetNodeId,
                nc -> !nc.getNodeContainerState().isWaitingToBeExecuted()
                    && !nc.getNodeContainerState().isExecutionInProgress());
        } else {
            page = filterAndGetSerializedJSONWebNodePage(getSuccessorWizardNodesWithinComponent(resetNodeId, null));
            reexecutedNodes = resetNodes;
            resetNodes = null;
        }
        if (page != null && m_onReexecutionEnd != null) {
            m_onReexecutionEnd.run();
        }
        return new DefaultPageContainer(new RawValue(page), resetNodes, reexecutedNodes);
    }

    @Override
    public PageContainer pollCompleteComponentReexecutionStatus() {
        assert (m_page instanceof SubNodeContainer);

        var resetNodes = getWizardNodesWithinComponent((SubNodeContainer)m_page, nc -> true);
        List<String> reexecutedNodes;

        var pageState = m_page.getNodeContainerState();
        String page;

        Predicate<NodeContainer> isExecuted = nc -> !nc.getNodeContainerState().isWaitingToBeExecuted()
            && !nc.getNodeContainerState().isExecutionInProgress();

        if (pageState.isExecutionInProgress() || pageState.isWaitingToBeExecuted()) {
            page = null;
            reexecutedNodes = getWizardNodesWithinComponent((SubNodeContainer)m_page, isExecuted);
        } else {
            page = filterAndGetSerializedJSONWebNodePage(resetNodes);
            reexecutedNodes = resetNodes;
            resetNodes = null;
        }
        if (page != null && m_onReexecutionEnd != null) {
            m_onReexecutionEnd.run();
        }
        return new DefaultPageContainer(new RawValue(page), resetNodes, reexecutedNodes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageContainer getPage() {
        CheckUtils.checkNotNull(m_resetNodeIds, "Reset node IDs must be defined for updated page response");
        var pageState = m_page.getNodeContainerState();
        String page;
        if (pageState.isExecutionInProgress() || pageState.isWaitingToBeExecuted()) {
            page = null;
            m_reexecutedNodes = getSuccessorWizardNodesWithinComponent(m_resetNodeIds,
                nc -> !nc.getNodeContainerState().isWaitingToBeExecuted()
                    && !nc.getNodeContainerState().isExecutionInProgress());
        } else {
            page = filterAndGetSerializedJSONWebNodePage(getSuccessorWizardNodesWithinComponent(m_resetNodeIds, null));
            m_reexecutedNodes = m_resetNodes;
            m_resetNodes = null;
        }
        if (page != null && m_onReexecutionEnd != null) {
            m_onReexecutionEnd.run();
        }
        return new DefaultPageContainer(new RawValue(page), m_resetNodes, m_reexecutedNodes);
    }

    private static List<String> getWizardNodesWithinComponent(final SubNodeContainer snc,
        final Predicate<NodeContainer> nodeFilter) {

        return WizardPageUtil.getWizardPageNodes(snc.getWorkflowManager(), true).stream().filter(nodeFilter).map(p -> {
            String[] parts = p.getID().toString().split(":", 2);
            return parts[1];
        }).collect(Collectors.toList());
    }

    private List<String> getSuccessorWizardNodesWithinComponent(final Collection<NodeID> resetNodeIds,
        final Predicate<NodeContainer> nodeFilter) {

        NodeID pageId = m_page.getID();

        Stream<Pair<NodeIDSuffix, NodeContainer>> combinedStream =
            WizardPageUtil.getSuccessorWizardPageNodesWithinComponent(m_compositeViewPageManager.getWorkflowManager(),
                pageId, resetNodeIds);

        if (nodeFilter != null) {
            combinedStream = combinedStream.filter(p -> nodeFilter.test(p.getSecond()));
        }

        return combinedStream.map(p -> p.getFirst().toString()).collect(Collectors.toList());
    }

    private List<String> getSuccessorWizardNodesWithinComponent(final NodeID resetNodeId,
        final Predicate<NodeContainer> nodeFilter) {
        return getSuccessorWizardNodesWithinComponent(List.of(resetNodeId), nodeFilter);
    }

}
