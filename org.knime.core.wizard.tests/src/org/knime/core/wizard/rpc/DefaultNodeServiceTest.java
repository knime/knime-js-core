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
 *   21 Sep 2021 (Marc Bux, KNIME GmbH, Berlin, Germany): created
 */
package org.knime.core.wizard.rpc;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.FIVE_SECONDS;
import static org.awaitility.Duration.ONE_HUNDRED_MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.knime.gateway.impl.service.events.SelectionEventSource.SelectionEventMode.REMOVE;
import static org.knime.gateway.impl.service.events.SelectionEventSource.SelectionEventMode.REPLACE;
import static org.knime.testing.util.WorkflowManagerUtil.createAndAddNode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.data.RowKey;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.impl.service.events.SelectionEvent;
import org.knime.gateway.impl.service.events.SelectionEventSource;
import org.knime.gateway.impl.service.events.SelectionEventSource.SelectionEventMode;
import org.knime.testing.node.view.NodeViewNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
@SuppressWarnings("javadoc")
public class DefaultNodeServiceTest {

    private static final String WORKFLOW_NAME = "workflow";

    private static Set<RowKey> createSet(final RowKey... rowKeys) {
        return Arrays.stream(rowKeys).collect(Collectors.toSet());
    }

    private static final Set<RowKey> ROWKEYS_1 = createSet(new RowKey("Row01"));

    private static final Set<RowKey> ROWKEYS_2 = createSet(new RowKey("Row02"));

    private static final Set<RowKey> ROWKEYS_1_2 = createSet(new RowKey("Row01"), new RowKey("Row02"));

    private WorkflowManager m_wfm;

    private NativeNodeContainer m_nnc;

    private HiLiteHandler m_hlh;

    @Before
    public void setup() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
        m_nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(1,0));
        m_hlh = m_nnc.getNodeModel().getInHiLiteHandler(0);
    }

    @After
    public void tearDown() {
        m_wfm.getParent().removeProject(m_wfm.getID());
    }

    @Test
    public void testSelectDataPoints() {

        final var listenerMock = mock(HiLiteListener.class);
        m_hlh.addHiLiteListener(listenerMock);

        new DefaultNodeService(m_nnc).updateDataPointSelection("ignored", "ignored", "nodeId",
            SelectionEventMode.ADD.toString(), ROWKEYS_1_2.stream().map(RowKey::toString).toList());

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, times(1)).hiLite(argThat(ke -> ke.keys().equals(ROWKEYS_1_2)));
            verify(listenerMock, never()).unHiLite(any());
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, never()).replaceHiLite(any());
        });

        assertEquals(m_hlh.getHiLitKeys(), ROWKEYS_1_2);
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testSelectDataPointsInComponentView() {
        NodeID n0 = createAndAddNode(m_wfm, new NodeViewNodeFactory(0, 1)).getID();
        NodeID n1 = createAndAddNode(m_wfm, new NodeViewNodeFactory(1, 1)).getID();
        NodeID n2 = createAndAddNode(m_wfm, new NodeViewNodeFactory(1, 0)).getID();
        m_wfm.addConnection(n0, 1, n1, 1);
        m_wfm.addConnection(n1, 1, n2, 1);

        NodeID componentId = m_wfm.collapseIntoMetaNode(new NodeID[]{n0, n1, n2}, new WorkflowAnnotationID[0], "component")
            .getCollapsedMetanodeID();
        m_wfm.convertMetaNodeToSubNode(componentId);
        m_wfm.executeAllAndWaitUntilDone();

        @SuppressWarnings("unchecked")
        final BiConsumer<String, SelectionEvent> selectionEventConsumer = mock(BiConsumer.class);
        var component = (SubNodeContainer)m_wfm.getNodeContainer(componentId);
        var nodeService = new DefaultNodeService(component, false);
        setupSelectionEventSource(selectionEventConsumer, component);
        nodeService.updateDataPointSelection("ignored", "ignored", "root:5:0:4", SelectionEventMode.ADD.toString(),
            ROWKEYS_1_2.stream().map(RowKey::toString).toList());

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(selectionEventConsumer, times(1)).accept(eq("SelectionEvent"),
                argThat(se -> verifySelectionEvent(se, "root:5", "root:5:0:5")));
        });
    }

    @Test
    public void testUnSelectDataPoints() throws InterruptedException {
        final var listenerMock = mock(HiLiteListener.class);

        m_hlh.addHiLiteListener(listenerMock);
        m_hlh.fireHiLiteEvent(ROWKEYS_1_2);

        new DefaultNodeService(m_nnc).updateDataPointSelection("ignored", "ignored", "nodeId", REMOVE.toString(),
            ROWKEYS_1.stream().map(RowKey::toString).toList());

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, times(1)).hiLite(any());
            verify(listenerMock, times(1)).unHiLite(argThat(ke -> ke.keys().equals(ROWKEYS_1)));
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, never()).replaceHiLite(any());
        });

        assertEquals(m_hlh.getHiLitKeys(), ROWKEYS_2);
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testReplaceDataPoints() throws InterruptedException {
        final var listenerMock = mock(HiLiteListener.class);

        m_hlh.addHiLiteListener(listenerMock);
        m_hlh.fireHiLiteEvent(ROWKEYS_1);

        new DefaultNodeService(m_nnc).updateDataPointSelection("ignored", "ignored", "nodeId", REPLACE.toString(),
            ROWKEYS_2.stream().map(RowKey::toString).toList());

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, times(1)).hiLite(any());
            verify(listenerMock, never()).unHiLite(any());
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, times(1))
                .replaceHiLite(argThat(ke -> ke.keys().equals(ROWKEYS_2)));
        });

        assertEquals(m_hlh.getHiLitKeys(), ROWKEYS_2);
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testChangeNodeStates() {
        var nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(0, 0));
        assertThat(nnc.getNodeContainerState().isExecuted(), is(false));
        new DefaultNodeService(nnc).changeNodeStates("not used", "not used", List.of("root:2"), "execute");
        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS)
            .untilAsserted(() -> assertThat(nnc.getNodeContainerState().isExecuted(), is(true)));
    }

    private static boolean verifySelectionEvent(final SelectionEvent se, final String workflowId, final String nodeId) {
        return se.getSelection().equals(ROWKEYS_1_2.stream().map(RowKey::toString).collect(Collectors.toList()))
            && se.getMode() == SelectionEventMode.ADD && se.getNodeId().equals(nodeId)
            && se.getWorkflowId().equals(workflowId) && se.getProjectId().startsWith(WORKFLOW_NAME);
    }

    private static void setupSelectionEventSource(final BiConsumer<String, SelectionEvent> selectionEventConsumer,
        final SubNodeContainer node) {
        var selectionEventSource =
            new SelectionEventSource<>((n, o) -> selectionEventConsumer.accept(n, (SelectionEvent)o),
                NodeViewManager.getInstance().getTableViewManager());
        WizardPageUtil.getWizardPageNodes(node.getWorkflowManager())
            .forEach(nnc -> selectionEventSource.addEventListenerFor(NodeWrapper.of(nnc)));
    }

}
