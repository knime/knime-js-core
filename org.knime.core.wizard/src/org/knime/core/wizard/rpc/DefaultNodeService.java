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
 *   Sep 10, 2021 (hornm): created
 */
package org.knime.core.wizard.rpc;

import static org.knime.gateway.impl.service.util.DefaultServiceUtil.getNodeContainer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.knime.core.data.RowKey;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.data.DataServiceDependencies;
import org.knime.core.webui.node.DataServiceManager;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.core.webui.node.dialog.scripting.kai.CodeKaiHandler;
import org.knime.core.webui.node.dialog.scripting.kai.CodeKaiHandler.ProjectId;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.impl.webui.kai.KaiHandlerFactoryRegistry;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 *
 * @since 4.5
 */
public class DefaultNodeService implements NodeService {

    private final Function<String, NodeWrapper> m_getNodeWrapper;

    // NOTE: setting the auth token provider to null will use the default auth token provider
    private final static CodeKaiHandler CODE_KAI_HANDLER =
        KaiHandlerFactoryRegistry.createCodeKaiHandler(null).orElse(null);

    /**
     * Initialize the {@link DefaultNodeService} for a {@link NativeNodeContainer}.
     *
     * @param nnc The {@link NativeNodeContainer}
     */
    DefaultNodeService(final NativeNodeContainer nnc) {
        var validId = new NodeIDEnt(nnc.getID()).toString();
        m_getNodeWrapper = id -> id.equals(validId) ? NodeWrapper.of(nnc) : null;
    }

    /**
     * Initialize the {@link DefaultNodeService} for either the dialog or the composite view of a
     * {@link SubNodeContainer}
     *
     * @param snc The {@link SubNodeContainer} also known as component
     * @param isDialog whether to initialise the node service for a component dialog or its composite view
     */
    DefaultNodeService(final SubNodeContainer snc, final boolean isDialog) {
        if (isDialog) {
            m_getNodeWrapper = id -> NodeWrapper.of(snc);
        } else {
            // initialize 'getNode'-function for a component composite view
            var projectWfm = snc.getParent().getProjectWFM();
            m_getNodeWrapper = id -> {
                try {
                    var nc = projectWfm.findNodeContainer(new NodeIDEnt(id).toNodeID(projectWfm.getID()));
                    if (nc instanceof NativeNodeContainer) {
                        return NodeWrapper.of(nc);
                    }
                } catch (IllegalArgumentException e) {
                    //
                }
                return null;
            };
        }
    }

    /**
     * Initialize the {@link DefaultNodeService} for a port view of a node.
     *
     * @param snc
     * @param portIdx
     * @param viewIdx
     */
    DefaultNodeService(final SingleNodeContainer snc, final int portIdx, final int viewIdx) {
        m_getNodeWrapper = id -> NodePortWrapper.of(snc, portIdx, viewIdx);
    }

    @Override
    public NodeViewEnt getNodeView(final String projectId, final String workflowId, final String versionId,
        final String nodeId) {
        assertIsCurrentState(versionId);
        var nnc = getNC(projectId, new NodeIDEnt(workflowId), VersionId.currentState(), new NodeIDEnt(nodeId),
            NativeNodeContainer.class);

        if (!NodeViewManager.hasNodeView(nnc)) {
            throw new IllegalArgumentException("The node " + nnc.getNameWithID() + " does not have a view");
        }
        if (!nnc.getNodeContainerState().isExecuted()) {
            throw new IllegalStateException(
                "Node view can't be requested. The node " + nnc.getNameWithID() + " is not executed.");
        }
        return NodeViewEnt.create(nnc);
    }

    // opening detached views of workflow-versions isn't supported, yet (see NXT-3670)
    private static void assertIsCurrentState(final String versionId) {
        assert VersionId.parse(versionId).isCurrentState();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String callNodeDataService(final String projectId, final String workflowId, final String versionId,
        final String nodeID, final String extensionType, final String serviceType, final String request) {
        assertIsCurrentState(versionId);
        @SuppressWarnings("rawtypes")
        final DataServiceManager dataServiceManager;
        if ("view".equals(extensionType)) {
            dataServiceManager = NodeViewManager.getInstance().getDataServiceManager();
        } else if ("dialog".equals(extensionType)) {
            dataServiceManager = NodeDialogManager.getInstance().getDataServiceManager();
        } else if ("port".equals(extensionType)) {
            // NOTE!! This is inconsistent with the actual implementation of the DefaultNodeService (see knime-gateway).
            // Because port-view data is actually served via the PortService.callPortDataService-endpoint and
            // this is just a quick and dirty solution - the proper solution is implemented via NXT-1271
            dataServiceManager = PortViewManager.getInstance().getDataServiceManager();
        } else {
            throw new IllegalArgumentException("Unknown target for node data service: " + extensionType);
        }

        var ncWrapper = Optional.ofNullable(m_getNodeWrapper.apply(nodeID))
            .orElseGet(() -> NodeWrapper.of(getNC(projectId, new NodeIDEnt(workflowId), VersionId.currentState(),
                new NodeIDEnt(nodeID), NativeNodeContainer.class)));
        return DataServiceDependencies.runWithDependencies(createDialogDataServiceDependencies(projectId), () -> {
            if ("initial_data".equals(serviceType)) {
                return dataServiceManager.callInitialDataService(ncWrapper);
            } else if ("data".equals(serviceType)) {
                return dataServiceManager.callRpcDataService(ncWrapper, request);
            } else if ("apply_data".equals(serviceType)) {
                return dataServiceManager.callApplyDataService(ncWrapper, request);
            } else {
                throw new IllegalArgumentException("Unknown service type '" + serviceType + "'");
            }
        });
    }

    private static Map<Class<?>, Object> createDialogDataServiceDependencies(final String projectId) {
        return DataServiceDependencies.dependencies( //
            CodeKaiHandler.class, CODE_KAI_HANDLER, //
            ProjectId.class, new ProjectId(projectId) //
        );
    }

    @Override
    public void updateDataPointSelection(final String projectId, final String workflowId, final String versionId,
        final String nodeIdString, final String mode, final List<String> selection) {
        assertIsCurrentState(versionId);
        try {
            var nodeWrapper = m_getNodeWrapper.apply(nodeIdString);
            Set<RowKey> rowKeys;
            HiLiteHandler hiLiteHandler;
            // NOTE!! This is just a quick and dirty solution. There will be an extra
            // PortService.updateDataPointSelection-endpoint for port views specifically.
            // Proper solution is implemented with NXT-1271.
            if (nodeWrapper instanceof NodePortWrapper portWrapper) {
                var tableViewManager = PortViewManager.getInstance().getTableViewManager();
                rowKeys = tableViewManager.callSelectionTranslationService(portWrapper, selection);
                hiLiteHandler =
                    TableViewManager.getOutHiLiteHandler(portWrapper.get(), portWrapper.getPortIdx() - 1).orElseThrow();
            } else {
                var tableViewManager = NodeViewManager.getInstance().getTableViewManager();
                rowKeys = tableViewManager.callSelectionTranslationService(nodeWrapper, selection);
                hiLiteHandler = tableViewManager.getHiLiteHandler(nodeWrapper).orElse(null);
            }
            updateDataPointSelection(hiLiteHandler, nodeWrapper.get().getID(), mode, rowKeys);
        } catch (IOException e) {
            NodeLogger.getLogger(getClass()).error(e);
        }
    }

    private static void updateDataPointSelection(final HiLiteHandler hlh, final NodeID nodeId, final String mode,
        final Set<RowKey> rowKeys) {
        final var selectionEventMode = SelectionEventEnt.ModeEnum.valueOf(mode.toUpperCase(Locale.ROOT));
        SelectionEventBus.processSelectionEvent(hlh, nodeId, selectionEventMode, true, rowKeys);
    }

    @Override
    public void changeNodeStates(final String projectId, final String workflowId, final List<String> nodeIds,
        final String action) {
        assert nodeIds.size() == 1 && "execute".equals(
            action) : "The changeNodeStates-endpoint is only partially implemented - parameter values are out of scope";
        var nc = m_getNodeWrapper.apply(nodeIds.get(0)).get();
        nc.getParent().executeUpToHere(nc.getID());
    }

    private static <T> T getNC(final String projectId, final NodeIDEnt workflowId, final VersionId versionId,
        final NodeIDEnt nodeId, final Class<T> ncClass) {
        var nc = getNodeContainer(projectId, workflowId, versionId, nodeId);
        if (!ncClass.isAssignableFrom(nc.getClass())) {
            throw new IllegalArgumentException(
                "The requested node " + nc.getNameWithID() + " is not a " + ncClass.getName());
        }
        return ncClass.cast(nc);
    }

}
