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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import org.knime.core.data.RowKey;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.node.DataServiceManager;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 *
 * @since 4.5
 */
public class DefaultNodeService implements NodeService {

    private final Function<String, NodeWrapper> m_getNodeWrapper;

    /**
     * Initialize the {@link DefaultNodeService} for a {@link NativeNodeContainer}.
     *
     * @param nnc The {@link NativeNodeContainer}
     */
    DefaultNodeService(final NativeNodeContainer nnc) {
        m_getNodeWrapper = id -> NodeWrapper.of(nnc);
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
                var nc = projectWfm.findNodeContainer(new NodeIDEnt(id).toNodeID(projectWfm.getID()));
                if (!(nc instanceof NativeNodeContainer)) {
                    throw new IllegalArgumentException("Not a native node: " + nc.getNameWithID());
                }
                return NodeWrapper.of(nc);
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

    @SuppressWarnings("unchecked")
    @Override
    public String callNodeDataService(final String projectId, final String workflowId, final String nodeID,
        final String extensionType, final String serviceType, final String request) {
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

        var ncWrapper = m_getNodeWrapper.apply(nodeID);
        if ("initial_data".equals(serviceType)) {
            return dataServiceManager.callInitialDataService(ncWrapper);
        } else if ("data".equals(serviceType)) {
            return dataServiceManager.callRpcDataService(ncWrapper, request);
        } else if ("apply_data".equals(serviceType)) {
            try {
                dataServiceManager.callApplyDataService(ncWrapper, request);
            } catch (IOException e) {
                NodeLogger.getLogger(getClass()).error(e);
                return e.getMessage();
            }
            return "";
        } else {
            throw new IllegalArgumentException("Unknown service type '" + serviceType + "'");
        }
    }

    @Override
    public void updateDataPointSelection(final String projectId, final String workflowId, final String nodeIdString,
        final String mode, final List<String> selection) {
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
        var nc = m_getNodeWrapper.apply("").get();
        assert nodeIds.size() == 1 && nodeIds.get(0).equals(new NodeIDEnt(nc.getID()).toString()) && "execute".equals(
            action) : "The changeNodeStates-endpoint is only partially implemented - parameter values are out of scope";
        nc.getParent().executeUpToHere(nc.getID());
    }

}
