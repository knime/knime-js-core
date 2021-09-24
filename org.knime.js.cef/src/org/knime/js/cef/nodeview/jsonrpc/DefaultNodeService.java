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
package org.knime.js.cef.nodeview.jsonrpc;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.knime.core.data.RowKey;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.property.hilite.KeyEvent;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.webui.node.view.NodeViewManager;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
public class DefaultNodeService implements NodeService {

    static interface SelectionEvent {
        SelectionEventMode getMode();

        List<String> getKeys();
    }

    enum SelectionEventMode {
            ADD, REMOVE, REPLACE
    }

    private final NativeNodeContainer m_nnc;

    private final Consumer<SelectionEvent> m_selectionEventConsumer;

    private final HiLiteHandler m_hiliteHandler;

    DefaultNodeService(final NativeNodeContainer nnc, final Consumer<SelectionEvent> selectionEventConsumer) {
        m_nnc = nnc;
        m_selectionEventConsumer = selectionEventConsumer;
        m_hiliteHandler = m_nnc.getNodeModel().getInHiLiteHandler(0);
        m_hiliteHandler.addHiLiteListener(new HiLiteListener() {
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
        });
    }

    private void consumeSelectionEvent(final KeyEvent event, final SelectionEventMode type) {
        final var src = event.getSource();
        // do not consume selection events that have been fired by this very node / default node service
        if (src != this) {
            final var keys = event.keys().stream().map(RowKey::getString).collect(Collectors.toUnmodifiableList());
            m_selectionEventConsumer.accept(new SelectionEvent() {
                @Override
                public SelectionEventMode getMode() {
                    return type;
                }

                @Override
                public List<String> getKeys() {
                    return keys;
                }
            });
        }
    }

    @Override
    public String callNodeViewDataService(final String projectId, final String workflowId, final String nodeID,
        final String serviceType, final String request) {
        final var nvm = NodeViewManager.getInstance();
        if ("initial_data".equals(serviceType)) {
            return nvm.callTextInitialDataService(m_nnc);
        } else if ("data".equals(serviceType)) {
            return nvm.callTextDataService(m_nnc, request);
        } else if ("apply_data".equals(serviceType)) {
            try {
                nvm.callTextReExecuteDataService(m_nnc, request);
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
    public void selectDataPoints(final String projectId, final String workflowId, final String nodeId,
        final String mode, final List<String> rowKeys) {
        final var selectionEventMode = SelectionEventMode.valueOf(mode);
        final var keyEvent = new KeyEvent(this, rowKeys.stream().map(RowKey::new).toArray(RowKey[]::new));
        switch (selectionEventMode) {
            case ADD:
                m_hiliteHandler.fireHiLiteEvent(keyEvent);
                break;
            case REMOVE:
                m_hiliteHandler.fireUnHiLiteEvent(keyEvent);
                break;
            case REPLACE:
                m_hiliteHandler.fireReplaceHiLiteEvent(keyEvent);
                break;
            default:
        }
    }

}
