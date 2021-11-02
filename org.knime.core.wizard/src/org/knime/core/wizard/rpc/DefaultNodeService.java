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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.knime.core.data.RowKey;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.property.hilite.KeyEvent;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.util.Pair;
import org.knime.core.webui.data.DataServiceProvider;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.NodeIDEnt;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 *
 * @since 4.5
 */
public class DefaultNodeService implements NodeService {

    static interface SelectionEvent {

        String getProjectId();

        String getWorkflowId();

        String getNodeId();

        SelectionEventMode getMode();

        List<String> getKeys();
    }

    enum SelectionEventMode {
            ADD, REMOVE, REPLACE
    }

    private final Function<String, NativeNodeContainer> m_getNode;

    private final List<WeakReference<Pair<HiLiteHandler, HiLiteListener>>> m_registeredHiLiteListeners;

    DefaultNodeService(final SingleNodeContainer snc, final Consumer<SelectionEvent> selectionEventConsumer) {
        m_registeredHiLiteListeners = new ArrayList<>();
        if (snc instanceof NativeNodeContainer) {
            addHiLiteListener((NativeNodeContainer)snc,
                new PerNodeHiliteListener(selectionEventConsumer, (NativeNodeContainer)snc));
            m_getNode = id -> (NativeNodeContainer)snc;
        } else {
            SubNodeContainer component = (SubNodeContainer)snc;
            WizardPageUtil.getWizardPageNodes(component.getWorkflowManager(), true)
                .forEach(nnc -> addHiLiteListener(nnc, new PerNodeHiliteListener(selectionEventConsumer, nnc)));
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

    private void addHiLiteListener(final NativeNodeContainer nnc, final HiLiteListener listener) {
        var hiLiteHandler = getHiLiteHandler(nnc);
        hiLiteHandler.addHiLiteListener(listener);
        m_registeredHiLiteListeners.add(new WeakReference<>(Pair.create(hiLiteHandler, listener)));
    }

    private static HiLiteHandler getHiLiteHandler(final NativeNodeContainer nnc) {
        return nnc.getNodeModel().getInHiLiteHandler(0);
    }

    @Override
    public String callNodeDataService(final String projectId, final String workflowId, final String nodeID,
        final String extensionType, final String serviceType, final String request) {
        var nc = m_getNode.apply(nodeID);
        final DataServiceProvider dataServiceProvider;
        if ("view".equals(extensionType)) {
            dataServiceProvider = NodeViewManager.getInstance().getNodeView(nc);
        } else if ("dialog".equals(extensionType)) {
            dataServiceProvider = NodeDialogManager.getInstance().getNodeDialog(nc);
        } else {
            throw new IllegalArgumentException("Unknown target for node data service: " + extensionType);
        }

        if ("initial_data".equals(serviceType)) {
            return dataServiceProvider.callTextInitialDataService();
        } else if ("data".equals(serviceType)) {
            return dataServiceProvider.callTextDataService(request);
        } else if ("apply_data".equals(serviceType)) {
            try {
                dataServiceProvider.callTextAppyDataService(request);
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
    public void selectDataPoints(final String projectId, final String workflowId, final String nodeIdString,
        final String extensionType, final String mode, final List<String> rowKeys) {
        if (!"view".equals(extensionType)) {
            throw new IllegalArgumentException("Unknown target for selection service: " + extensionType);
        }
        final var selectionEventMode = SelectionEventMode.valueOf(mode);
        var nc = m_getNode.apply(nodeIdString);
        final var keyEvent = new KeyEvent(nc.getID(), rowKeys.stream().map(RowKey::new).toArray(RowKey[]::new));
        var hiLiteHandler = getHiLiteHandler(nc);
        switch (selectionEventMode) {
            case ADD:
                hiLiteHandler.fireHiLiteEvent(keyEvent);
                break;
            case REMOVE:
                hiLiteHandler.fireUnHiLiteEvent(keyEvent);
                break;
            case REPLACE:
                hiLiteHandler.fireReplaceHiLiteEvent(keyEvent);
                break;
            default:
        }
    }

    /**
     * Cleans up the default node service (e.g. removing hilite listeners).
     */
    public void dispose() {
        m_registeredHiLiteListeners.forEach(r -> {
            Pair<HiLiteHandler, HiLiteListener> p = r.get();
            if (p != null) {
                p.getFirst().removeHiLiteListener(p.getSecond());
            }
        });
    }

    private static class PerNodeHiliteListener implements HiLiteListener {

        private final Consumer<SelectionEvent> m_eventConsumer;

        private final NodeID m_nodeId;

        private final String m_projectId;

        private final String m_workflowId;

        private final String m_nodeIdString;

        PerNodeHiliteListener(final Consumer<SelectionEvent> eventConsumer, final NativeNodeContainer nnc) {
            m_eventConsumer = eventConsumer;
            var parent = nnc.getParent();
            var projectWfm = parent.getProjectWFM();
            m_projectId = projectWfm.getNameWithID();
            NodeID ncParentId = parent.getDirectNCParent() instanceof SubNodeContainer
                ? ((SubNodeContainer)parent.getDirectNCParent()).getID() : parent.getID();
            m_workflowId = new NodeIDEnt(ncParentId).toString();
            m_nodeId = nnc.getID();
            m_nodeIdString = new NodeIDEnt(m_nodeId).toString();
        }

        @Override
        public void hiLite(final KeyEvent event) {
            consumeSelectionEvent(event, SelectionEventMode.ADD);
        }

        @Override
        public void unHiLite(final KeyEvent event) {
            consumeSelectionEvent(event, SelectionEventMode.REMOVE);
        }

        @Override
        public void unHiLiteAll(final KeyEvent event) {
            consumeSelectionEvent(new KeyEvent(event.getSource()), SelectionEventMode.REPLACE);
        }

        @Override
        public void replaceHiLite(final KeyEvent event) {
            consumeSelectionEvent(event, SelectionEventMode.REPLACE);
        }

        private void consumeSelectionEvent(final KeyEvent event, final SelectionEventMode type) {
            // do not consume selection events that have been fired by the node this listener is registered on
            if (!m_nodeId.equals(event.getSource())) {
                final var keys = event.keys().stream().map(RowKey::getString).collect(Collectors.toUnmodifiableList());
                m_eventConsumer.accept(new SelectionEvent() { // NOSONAR

                    @Override
                    public SelectionEventMode getMode() {
                        return type;
                    }

                    @Override
                    public List<String> getKeys() {
                        return keys;
                    }

                    @Override
                    public String getProjectId() {
                        return m_projectId;
                    }

                    @Override
                    public String getWorkflowId() {
                        return m_workflowId;
                    }

                    @Override
                    public String getNodeId() {
                        return m_nodeIdString;
                    }
                });
            }
        }
    }

}
