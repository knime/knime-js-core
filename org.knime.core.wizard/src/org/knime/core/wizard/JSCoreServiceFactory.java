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
 *   Jun 11, 2025 (kampmann): created
 */
package org.knime.core.wizard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.util.NodeCleanUpCallback;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.service.ComponentEditorService;
import org.knime.gateway.api.webui.service.CompositeViewService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.impl.webui.service.GatewayServiceFactory;

/**
 * Factory for creating instances of {@link CompositeViewService} that can be used to interact with components.
 *
 * @since 5.6
 */
public class JSCoreServiceFactory implements GatewayServiceFactory {

    @Override
    public CompositeViewService createCompositeViewService(
        final Function<String, Function<NativeNodeContainer, NodeViewEnt>> createNodeViewEntityFactory) {
        return new JSCoreCompositeViewService(createNodeViewEntityFactory);
    }

    @Override
    public ComponentEditorService createComponentEditorService() {
        return new DefaultComponentEditorService();
    }

    /**
     * Implementation of the {@link CompositeViewService} interface
     */
    static class JSCoreCompositeViewService implements CompositeViewService {

        private final Map<NodeID, SubnodeViewableModel> m_modelCache = Collections.synchronizedMap(new HashMap<>());

        private final Function<String, Function<NativeNodeContainer, NodeViewEnt>> m_createNodeViewEntityFactory;

        /**
         * @param createNodeViewEntityFactory a factory function that creates a function that creates a NodeViewEnt
         */
        public JSCoreCompositeViewService(
            final Function<String, Function<NativeNodeContainer, NodeViewEnt>> createNodeViewEntityFactory) {

            m_createNodeViewEntityFactory = createNodeViewEntityFactory;

        }

        @Override
        public Object getCompositeViewPage(final String projectId, final NodeIDEnt workflowId, final String versionId,
            final NodeIDEnt nodeId)
            throws ServiceExceptions.ServiceCallException, NodeNotFoundException {

            SubnodeViewableModel model;
            try {
                model = getOrCreateModel(getSubNodeContainer(projectId, workflowId, VersionId.parse(versionId), nodeId),
                    projectId);
                return viewContentToJsonString(model.getViewRepresentation());
            } catch (IOException e) {
                throw ServiceExceptions.ServiceCallException.builder().withTitle("Failed to get composite view page")
                    .withDetails(e.getMessage()).canCopy(false).build();
            }
        }

        @Override
        public Object pollCompleteComponentReexecutionStatus(final String projectId, final NodeIDEnt workflowId,
            final NodeIDEnt nodeId) throws ServiceExceptions.ServiceCallException, NodeNotFoundException {

            try {
                return getOrCreateModel(getSubNodeContainer(projectId, workflowId, nodeId), projectId)
                    .createReexecutionService(m_createNodeViewEntityFactory.apply(projectId)::apply)
                    .pollCompleteComponentReexecutionStatus();
            } catch (IOException e) {
                throw ServiceExceptions.ServiceCallException.builder()
                    .withTitle("Failed to poll component reexecution status").withDetails(e.getMessage()).canCopy(false)
                    .build();
            }
        }

        @Override
        public Object pollComponentReexecutionStatus(final String projectId, final NodeIDEnt workflowId,
            final NodeIDEnt nodeId, final String resetNodeIdSuffix)
            throws ServiceExceptions.ServiceCallException, NodeNotFoundException {

            try {
                return getOrCreateModel(getSubNodeContainer(projectId, workflowId, nodeId), projectId)
                    .createReexecutionService(m_createNodeViewEntityFactory.apply(projectId)::apply)
                    .pollComponentReexecutionStatus(resetNodeIdSuffix);
            } catch (IOException e) {
                throw ServiceExceptions.ServiceCallException.builder()
                    .withTitle("Failed to poll component reexecution status").withDetails(e.getMessage()).canCopy(false)
                    .build();
            }
        }

        @Override
        public void setViewValuesAsNewDefault(final String projectId, final NodeIDEnt workflowId,
            final NodeIDEnt nodeId, final Map<String, String> viewValues)
            throws ServiceExceptions.ServiceCallException, NodeNotFoundException {

            try {
                var model = getOrCreateModel(getSubNodeContainer(projectId, workflowId, nodeId), projectId);
                var result = model.loadViewValueFromMapAndSetAsDefault(viewValues);
                if (result != null) {
                    throw ServiceExceptions.ServiceCallException.builder()
                        .withTitle("Failed to set view values as new default").withDetails(result.getError())
                        .canCopy(false).build();
                }
            } catch (IOException e) {
                throw ServiceExceptions.ServiceCallException.builder()
                    .withTitle("Failed to set view values as new default").withDetails(e.getMessage()).canCopy(false)
                    .build();
            }
        }

        @Override
        public Object triggerCompleteComponentReexecution(final String projectId, final NodeIDEnt workflowId,
            final NodeIDEnt nodeId, final Map<String, String> viewValues)
            throws ServiceExceptions.ServiceCallException, NodeNotFoundException {

            try {
                return getOrCreateModel(getSubNodeContainer(projectId, workflowId, nodeId), projectId)
                    .createReexecutionService(m_createNodeViewEntityFactory.apply(projectId)::apply)
                    .triggerCompleteComponentReexecution(viewValues);
            } catch (IOException e) {
                throw ServiceExceptions.ServiceCallException.builder().withTitle("Failed to trigger re-execution")
                    .withDetails(e.getMessage()).canCopy(false).build();
            }
        }

        @Override
        public Object triggerComponentReexecution(final String projectId, final NodeIDEnt workflowId,
            final NodeIDEnt nodeId, final String resetNodeIdSuffix, final Map<String, String> viewValues)
            throws ServiceExceptions.ServiceCallException, NodeNotFoundException {

            try {
                return getOrCreateModel(getSubNodeContainer(projectId, workflowId, nodeId), projectId)
                    .createReexecutionService(m_createNodeViewEntityFactory.apply(projectId)::apply)
                    .triggerComponentReexecution(resetNodeIdSuffix, viewValues);
            } catch (IOException e) {
                throw ServiceExceptions.ServiceCallException.builder().withTitle("Failed to trigger re-execution")
                    .withDetails(e.getMessage()).canCopy(false).build();
            }
        }

        @Override
        public void deactivateAllCompositeViewDataServices(final String projectId, final NodeIDEnt workflowId,
            final NodeIDEnt nodeId) throws ServiceExceptions.ServiceCallException, NodeNotFoundException {
            var subnodeContainer = getSubNodeContainer(projectId, workflowId, nodeId);
            deactivateAllCompositeViewDataServices(subnodeContainer);
        }

        private static SubNodeContainer getSubNodeContainer(final String projectId, final NodeIDEnt workflowId,
            final VersionId versionId, final NodeIDEnt nodeId) throws NodeNotFoundException {
            NodeContainer container = null;
            try {
                container = org.knime.gateway.impl.service.util.DefaultServiceUtil.getNodeContainer(projectId,
                    workflowId, versionId, nodeId);
            } catch (IllegalStateException | IllegalArgumentException | NoSuchElementException e) {
                throw NodeNotFoundException.builder().withTitle("Node with id '" + nodeId + "' not found.")
                    .withDetails(e.getMessage()).canCopy(false).build();
            }
            if (container instanceof SubNodeContainer snc) {
                return snc;
            }
            throw NodeNotFoundException.builder().withTitle("Node with id '" + nodeId + "' is not a component.")
                .withDetails().canCopy(false).build();
        }

        private static SubNodeContainer getSubNodeContainer(final String projectId, final NodeIDEnt workflowId,
            final NodeIDEnt nodeId) throws NodeNotFoundException {
            return getSubNodeContainer(projectId, workflowId, VersionId.currentState(), nodeId);
        }

        /**
         * Turns a webview content into a json string.
         */
        private static String viewContentToJsonString(final WebViewContent webViewContent) throws IOException {
            // TODO(NXT-3339) :
            // very ugly, but it's done the same way at other places, too
            // WebViewContent should have a 'saveToStream(OutputStream)'-method
            // right now the returning will be json, but what if not anymore?
            return ((ByteArrayOutputStream)webViewContent.saveToStream()).toString("UTF-8");
        }

        private SubnodeViewableModel getOrCreateModel(final SubNodeContainer snc, final String projectId)
            throws IOException {

            var model = m_modelCache.get(snc.getID());
            if (model == null) {
                model = new SubnodeViewableModel(snc, snc.getName(), false,
                    m_createNodeViewEntityFactory.apply(projectId)::apply);

                m_modelCache.put(snc.getID(), model);

                NodeCleanUpCallback.builder(snc, () -> {
                    var modelToDiscard = m_modelCache.remove(snc.getID());
                    if (modelToDiscard != null) {
                        modelToDiscard.discard();
                    }
                }) //
                    .cleanUpOnNodeStateChange(true) //
                    .deactivateOnNodeStateChange(true) //
                    .build();
            }

            return model;
        }

        private static void deactivateAllCompositeViewDataServices(final SubNodeContainer subnodeContainer) {
            List<NativeNodeContainer> viewNodes =
                WizardPageUtil.getWizardPageNodes(subnodeContainer.getWorkflowManager(), true);

            var nvm = NodeViewManager.getInstance();
            viewNodes.stream().filter(NodeViewManager::hasNodeView)
                .forEach(nnc -> nvm.getDataServiceManager().deactivateDataServices(NodeWrapper.of(nnc)));
        }

    }
}
