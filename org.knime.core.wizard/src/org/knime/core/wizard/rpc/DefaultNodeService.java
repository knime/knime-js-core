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
import java.util.function.Function;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.webui.node.DataServiceManager;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.impl.service.events.SelectionEventSource;
import org.knime.gateway.impl.service.events.SelectionEventSource.SelectionEventMode;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 *
 * @since 4.5
 */
public class DefaultNodeService implements NodeService {

    private final Function<String, NativeNodeContainer> m_getNode;

    DefaultNodeService(final SingleNodeContainer snc) {
        if (snc instanceof NativeNodeContainer) {
            m_getNode = id -> (NativeNodeContainer)snc;
        } else {
            var projectWfm = snc.getParent().getProjectWFM();
            m_getNode = id -> {
                var nc = projectWfm.findNodeContainer(new NodeIDEnt(id).toNodeID(projectWfm.getID()));
                if (!(nc instanceof NativeNodeContainer)) {
                    throw new IllegalArgumentException("Not a native node: " + nc.getNameWithID());
                }
                return (NativeNodeContainer)nc;
            };
        }
    }

    @Override
    public String callNodeDataService(final String projectId, final String workflowId, final String nodeID,
        final String extensionType, final String serviceType, final String request) {
        final DataServiceManager dataServiceManager;
        if ("view".equals(extensionType)) {
            dataServiceManager = NodeViewManager.getInstance();
        } else if ("dialog".equals(extensionType)) {
            dataServiceManager = NodeDialogManager.getInstance();
        } else {
            throw new IllegalArgumentException("Unknown target for node data service: " + extensionType);
        }

        var nc = m_getNode.apply(nodeID);
        if ("initial_data".equals(serviceType)) {
            return dataServiceManager.callTextInitialDataService(nc);
        } else if ("data".equals(serviceType)) {
            return dataServiceManager.callTextDataService(nc, request);
        } else if ("apply_data".equals(serviceType)) {
            try {
                dataServiceManager.callTextApplyDataService(nc, request);
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
        final String mode, final List<String> rowKeys) {
        final var selectionEventMode = SelectionEventMode.valueOf(mode);
        var nc = m_getNode.apply(nodeIdString);
        SelectionEventSource.processSelectionEvent(nc, selectionEventMode, true, rowKeys);
    }

    @Override
    @SuppressWarnings("java:S4274")
    public void changeNodeStates(final String projectId, final String workflowId, final List<String> nodeIds,
        final String action) {
        var nc = m_getNode.apply("");
        assert nodeIds.size() == 1 && nodeIds.get(0).equals(new NodeIDEnt(nc.getID()).toString()) && "execute".equals(
            action) : "The changeNodeStates-endpoint is only partially implemented - parameter values are out of scope";
        nc.getParent().executeUpToHere(nc.getID());
    }

}
