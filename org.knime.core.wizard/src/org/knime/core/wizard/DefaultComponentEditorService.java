/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
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
 *   Jul 1, 2025 (kampmann): created
 */
package org.knime.core.wizard;

import static org.knime.core.node.wizard.page.WizardPageUtil.isWizardPageNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.wizard.ViewHideable;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.service.ComponentEditorService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.js.core.layout.DefaultConfigurationCreatorImpl;
import org.knime.js.core.layout.DefaultLayoutCreatorImpl;
import org.knime.js.core.layout.bs.JSONLayoutConfigurationContent;
import org.knime.js.core.layout.bs.JSONLayoutContent;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.js.core.layout.bs.JSONNestedLayout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for the component editor, providing methods to retrieve view and configuration nodes as JSON strings.
 * @since 5.6
 */
public final class DefaultComponentEditorService implements ComponentEditorService {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultComponentEditorService.class);

    // ensure node layout is written the same as the metanode layout
    private static final ObjectMapper m_configuredObjectMapper = JSONLayoutPage.getConfiguredObjectMapper();

    @Override
    public String getViewNodes(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        LOGGER.debug("getViewNodes called (" + projectId + ", " + workflowId + ", " + nodeId + ").");
        var snc = getSubNodeContainer(projectId, workflowId, nodeId);

        return getViewNodesString(snc);
    }

    @Override
    public String getViewLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        LOGGER.debug("getViewLayout called (" + projectId + ", " + workflowId + ", " + nodeId + ").");

        var snc = getSubNodeContainer(projectId, workflowId, nodeId);
        return snc.getSubnodeLayoutStringProvider().getLayoutString();
    }

    @Override
    public void setViewLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final String componentViewLayout) throws ServiceCallException {

        LOGGER.info("pushLayout called (" + projectId + ", " + workflowId + ", " + nodeId + "): layout: "
            + componentViewLayout);

        if (componentViewLayout == null || componentViewLayout.isBlank()) {
            throw ServiceCallException.builder().withTitle("Applying view layout failed")
                .withDetails("Component view layout is empty.").canCopy(false).build();
        }

        var snc = getSubNodeContainer(projectId, workflowId, nodeId);

        var layoutProvider = snc.getSubnodeLayoutStringProvider();
        layoutProvider.setLayoutString(componentViewLayout);

        snc.setSubnodeLayoutStringProvider(layoutProvider);
    }

    @Override
    public String getConfigurationNodes(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        LOGGER.debug("getConfigurationNodes called (" + projectId + ", " + workflowId + ", " + nodeId + ").");
        var snc = getSubNodeContainer(projectId, workflowId, nodeId);

        return getConfigurationNodesString(snc);
    }

    @Override
    public String getConfigurationLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        LOGGER.debug("getConfigurationLayout called (" + projectId + ", " + workflowId + ", " + nodeId + ").");

        var snc = getSubNodeContainer(projectId, workflowId, nodeId);
        return snc.getSubnodeConfigurationLayoutStringProvider().getConfigurationLayoutString();
    }

    @Override
    public void setConfigurationLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final String componentConfigurationLayout) throws ServiceCallException {

        LOGGER.debug("pushConfigurationLayout called (" + projectId + ", " + workflowId + ", " + nodeId
            + "): layout: " + componentConfigurationLayout);

        if (componentConfigurationLayout == null || componentConfigurationLayout.isBlank()) {
            throw ServiceCallException.builder().withTitle("Applying configuration layout failed")
                .withDetails("Component configuration layout is empty.").canCopy(false).build();
        }

        var snc = getSubNodeContainer(projectId, workflowId, nodeId);
        var layoutProvider = snc.getSubnodeConfigurationLayoutStringProvider();

        layoutProvider.setConfigurationLayoutString(componentConfigurationLayout);
        snc.setSubnodeConfigurationStringProvider(layoutProvider);

    }

    private static String getViewNodesString(final SubNodeContainer subNodeContainer) {

        final List<VisualLayoutEditorJSONNode> nodes =
            createJSONViewNodeList(getViewValues(subNodeContainer.getWorkflowManager()));

        String jsonNodes = "";
        try {
            jsonNodes = m_configuredObjectMapper.writeValueAsString(nodes);
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot write JSON: " + e.getMessage(), e);
        }
        return jsonNodes;
    }

    private static String getConfigurationNodesString(final SubNodeContainer subNodeContainer) {

        Map<NodeIDSuffix, DialogNode> dialogNodes = getConfigurationValues(subNodeContainer.getWorkflowManager());

        final List<ConfigurationLayoutEditorJSONNode> nodes =
            createJSONConfigurationNodeList(subNodeContainer, dialogNodes);

        String jsonNodes = "";
        try {
            jsonNodes = m_configuredObjectMapper.writeValueAsString(nodes);
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot write JSON: " + e.getMessage(), e);
        }

        return jsonNodes;
    }

    private static LinkedHashMap<NodeIDSuffix, SingleNodeContainer> getViewValues(final WorkflowManager wfManager) {

        LinkedHashMap<NodeIDSuffix, SingleNodeContainer> resultMap = new LinkedHashMap<>();

        List<NativeNodeContainer> viewNodes = WizardPageUtil.getAllWizardPageNodes(wfManager, false);
        for (NativeNodeContainer nc : viewNodes) {
            NodeID.NodeIDSuffix idSuffix = NodeID.NodeIDSuffix.create(wfManager.getID(), nc.getID());
            resultMap.put(idSuffix, nc);
        }

        Map<NodeID, SubNodeContainer> nestedSubnodes = WizardPageUtil.getSubPageNodes(wfManager);
        for (Map.Entry<NodeID, SubNodeContainer> entry : nestedSubnodes.entrySet()) {
            NodeID.NodeIDSuffix idSuffix = NodeID.NodeIDSuffix.create(wfManager.getID(), entry.getKey());
            resultMap.put(idSuffix, entry.getValue());
        }
        return resultMap;
    }

    private static LinkedHashMap<NodeIDSuffix, DialogNode> getConfigurationValues(final WorkflowManager wfManager) {

        Map<NodeID, DialogNode> viewConfigurationNodes = wfManager.findNodes(DialogNode.class, false);
        LinkedHashMap<NodeIDSuffix, DialogNode> resultMapConfiguration = new LinkedHashMap<>();
        for (Map.Entry<NodeID, DialogNode> entry : viewConfigurationNodes.entrySet()) {
            NodeID.NodeIDSuffix idSuffix = NodeID.NodeIDSuffix.create(wfManager.getID(), entry.getKey());
            resultMapConfiguration.put(idSuffix, entry.getValue());
        }
        return resultMapConfiguration;
    }

    private static List<VisualLayoutEditorJSONNode>
        createJSONViewNodeList(final Map<NodeIDSuffix, SingleNodeContainer> viewNodes) {

        final List<VisualLayoutEditorJSONNode> nodes = new ArrayList<>();
        for (final Entry<NodeIDSuffix, SingleNodeContainer> viewNode : viewNodes.entrySet()) {

            final SingleNodeContainer node = viewNode.getValue();
            final VisualLayoutEditorJSONNode jsonNode = new VisualLayoutEditorJSONNode( //
                node.getID().getIndex(), //
                getTemplateId(node), //
                node.getName(), //
                node.getNodeAnnotation().getText(), //
                getLayout(viewNode.getValue(), viewNode.getKey()), //
                !isHideInWizard(node), //
                getType(node));

            if (node instanceof SubNodeContainer) {
                // set to provide additional info in the Visual Layout Editor
                boolean isSubNodeContainerUsingLegacyMode = !((SubNodeContainer)node).getSubnodeLayoutStringProvider()
                    .getLayoutString().contains("\"parentLayoutLegacyMode\":false");
                jsonNode.setContainerLegacyModeEnabled(isSubNodeContainerUsingLegacyMode);
            }
            nodes.add(jsonNode);
        }
        return nodes;
    }

    private static String getTemplateId(final SingleNodeContainer nc) {
        if (nc instanceof NativeNodeContainer nnc) {
            return nnc.getNode().getFactory().getFactoryId();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private static List<ConfigurationLayoutEditorJSONNode> createJSONConfigurationNodeList(
        final SubNodeContainer subNodeContainer, final Map<NodeIDSuffix, DialogNode> dialogNodes) {

        final List<ConfigurationLayoutEditorJSONNode> nodes = new ArrayList<>();
        for (final Entry<NodeIDSuffix, DialogNode> dialogNode : dialogNodes.entrySet()) {

            final DialogNode node = dialogNode.getValue();
            final NodeID nodeID = dialogNode.getKey().prependParent(subNodeContainer.getWorkflowManager().getID());
            final NodeContainer nodeContainer = subNodeContainer.getWorkflowManager().getNodeContainer(nodeID);

            final ConfigurationLayoutEditorJSONNode jsonNode = new ConfigurationLayoutEditorJSONNode( //
                nodeContainer.getID().getIndex(), //
                getTemplateId(nodeContainer), //
                nodeContainer.getName(), //
                nodeContainer.getNodeAnnotation().getText(), //
                getConfigurationLayout(dialogNode.getValue(), dialogNode.getKey()), //
                !node.isHideInDialog(), //
                "configuration");
            nodes.add(jsonNode);
        }
        return nodes;
    }

    private static String getTemplateId(final NodeContainer nc) {
        if (nc instanceof NativeNodeContainer nnc) {
            return nnc.getNode().getFactory().getFactoryId();
        }
        return null;
    }

    private static String getType(final SingleNodeContainer node) {
        if (node instanceof SubNodeContainer) {
            return "nestedLayout";
        } else if (node instanceof NativeNodeContainer) {
            NativeNodeContainer nnc = (NativeNodeContainer)node;
            NodeModel model = nnc.getNodeModel();
            if (isWizardPageNode(nnc)) {
                if (model instanceof DialogNode) {
                    return "quickform";
                }
                return "view";
            }
            if (model instanceof DialogNode) {
                return "configuration";
            }
        }

        throw new IllegalArgumentException(
            "Node is not view, subnode, configuration or quickform: " + node.getNameWithID());
    }

    private static JSONLayoutContent getLayout(final SingleNodeContainer node, final NodeIDSuffix id) {
        if (node instanceof SubNodeContainer) {
            final JSONNestedLayout layout = new JSONNestedLayout();
            layout.setNodeID(id.toString());
            return layout;
        }
        return DefaultLayoutCreatorImpl.getDefaultViewContentForNode(id, (NativeNodeContainer)node);
    }

    private static JSONLayoutConfigurationContent
        getConfigurationLayout(@SuppressWarnings("rawtypes") final DialogNode node, final NodeIDSuffix id) {
        return DefaultConfigurationCreatorImpl.getDefaultConfigurationContentForNode(id, node);
    }

    private static boolean isHideInWizard(final SingleNodeContainer nc) {

        if (nc instanceof NativeNodeContainer nnc && //
            nnc.getNodeModel() instanceof ViewHideable hideableModel) {

            return hideableModel.isHideInWizard();
        }
        return false;
    }

    private static SubNodeContainer getSubNodeContainer(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException {
        try {

            var container = workflowId == nodeId ? //
                DefaultServiceUtil.getNodeContainer(projectId, workflowId) : //
                DefaultServiceUtil.getNodeContainer(projectId, workflowId, VersionId.currentState(), nodeId); //

            if (container instanceof WorkflowManager wfm && wfm.isComponentProjectWFM()) {
                container = (NodeContainer)wfm.getDirectNCParent();
            }
            if (container instanceof SubNodeContainer snc) {
                return snc;
            }
            throw ServiceCallException.builder().withTitle("Internal error")
                .withDetails("Node with id '" + nodeId + "' is not a component.").canCopy(false).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw ServiceCallException.builder().withTitle("Internal error")
            .withDetails(e.getMessage()).canCopy(true).withCause(e).build();
        }
    }
}
