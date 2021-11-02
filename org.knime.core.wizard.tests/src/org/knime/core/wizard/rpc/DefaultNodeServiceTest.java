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
import static org.junit.Assert.assertEquals;
import static org.knime.core.wizard.rpc.DefaultNodeService.SelectionEventMode.ADD;
import static org.knime.core.wizard.rpc.DefaultNodeService.SelectionEventMode.REMOVE;
import static org.knime.core.wizard.rpc.DefaultNodeService.SelectionEventMode.REPLACE;
import static org.knime.testing.util.WorkflowManagerUtil.createAndAddNode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.data.RowKey;
import org.knime.core.node.port.PortType;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.virtual.subnode.VirtualSubNodeInputNodeFactory;
import org.knime.core.wizard.rpc.DefaultNodeService.SelectionEvent;
import org.knime.core.wizard.rpc.DefaultNodeService.SelectionEventMode;
import org.knime.testing.node.view.NodeViewNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
@SuppressWarnings("javadoc")
public class DefaultNodeServiceTest {

    private static final String WORKFLOW_NAME = "workflow";

    private static final List<String> ROWKEYS_1 = List.of("Row01");

    private static final List<String> ROWKEYS_2 = List.of("Row02");

    private static final List<String> ROWKEYS_1_2 = List.of("Row01", "Row02");

    private WorkflowManager m_wfm;

    private NativeNodeContainer m_nnc;

    private HiLiteHandler m_hlh;

    @Before
    public void setup() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
        m_nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new VirtualSubNodeInputNodeFactory(null, new PortType[0]));
        m_hlh = m_nnc.getNodeModel().getInHiLiteHandler(0);
    }

    @After
    public void tearDown() {
        m_wfm.getParent().removeProject(m_wfm.getID());
    }

    private static Set<RowKey> stringListToRowKeySet(final List<String> keys) {
        return keys.stream().map(RowKey::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Test
    public void testSelectDataPoints() {

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEvent> consumerMock = mock(Consumer.class);
        final var listenerMock = mock(HiLiteListener.class);
        m_hlh.addHiLiteListener(listenerMock);

        new DefaultNodeService(m_nnc, consumerMock).selectDataPoints("projectId", "workflowId", "nodeId", "view",
            SelectionEventMode.ADD.toString(), ROWKEYS_1_2);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, times(1)).hiLite(argThat(ke -> ke.keys().equals(stringListToRowKeySet(ROWKEYS_1_2))));
            verify(listenerMock, never()).unHiLite(any());
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, never()).replaceHiLite(any());
            verify(consumerMock, never()).accept(any());
        });

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_1_2));
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testSelectDataPointsInComponentView() {
        NodeID n1 = createAndAddNode(m_wfm, new NodeViewNodeFactory(0, 1)).getID();
        NodeID n2 = createAndAddNode(m_wfm, new NodeViewNodeFactory(1, 0)).getID();
        m_wfm.addConnection(n1, 1, n2, 1);

        NodeID componentId = m_wfm.collapseIntoMetaNode(new NodeID[]{n1, n2}, new WorkflowAnnotation[0], "component")
            .getCollapsedMetanodeID();
        m_wfm.convertMetaNodeToSubNode(componentId);

        final Consumer<SelectionEvent> selectionEventConsumer = mock(Consumer.class);
        DefaultNodeService nodeService =
            new DefaultNodeService((SingleNodeContainer)m_wfm.getNodeContainer(componentId), selectionEventConsumer);
        nodeService.selectDataPoints("projectId_not_used", "workflowId_not_used", "root:4:0:2", "view",
            SelectionEventMode.ADD.toString(), ROWKEYS_1_2);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(selectionEventConsumer, times(1))
                .accept(argThat(se -> verifySelectionEvent(se, "root:4", "root:4:0:3")));
        });
    }

    @Test
    public void testConsumeHiLiteEvent() {

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEvent> consumerMock = mock(Consumer.class);

        @SuppressWarnings("unused")
        final var dns = new DefaultNodeService(m_nnc, consumerMock);
        m_nnc.getNodeModel().getInHiLiteHandler(0).fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1_2));

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(
            () -> verify(consumerMock, times(1)).accept(argThat(se -> verifySelectionEvent(se, "root", "root:1"))));

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_1_2));
        m_hlh.fireClearHiLiteEvent();
    }

    private static boolean verifySelectionEvent(final SelectionEvent se, final String workflowId, final String nodeId) {
        return se.getKeys().equals(ROWKEYS_1_2) && se.getMode() == ADD && se.getNodeId().equals(nodeId)
            && se.getWorkflowId().equals(workflowId) && se.getProjectId().startsWith(WORKFLOW_NAME);
    }

    @Test
    public void testUnSelectDataPoints() {

        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1_2));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEvent> consumerMock = mock(Consumer.class);
        final var listenerMock = mock(HiLiteListener.class);
        m_hlh.addHiLiteListener(listenerMock);

        new DefaultNodeService(m_nnc, consumerMock).selectDataPoints("projectId", "workflowId", "nodeId", "view",
            SelectionEventMode.REMOVE.toString(), ROWKEYS_1);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, never()).hiLite(any());
            verify(listenerMock, times(1)).unHiLite(argThat(ke -> ke.keys().equals(stringListToRowKeySet(ROWKEYS_1))));
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, never()).replaceHiLite(any());
            verify(consumerMock, never()).accept(any());
        });

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_2));
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testConsumeUnHiLiteEvent() {

        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1_2));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEvent> consumerMock = mock(Consumer.class);

        @SuppressWarnings("unused")
        final var dns = new DefaultNodeService(m_nnc, consumerMock);
        m_nnc.getNodeModel().getInHiLiteHandler(0).fireUnHiLiteEvent(stringListToRowKeySet(ROWKEYS_1));

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS)
            .untilAsserted(() -> verify(consumerMock, times(1))
                .accept(argThat(se -> se.getKeys().equals(ROWKEYS_1) && se.getMode() == REMOVE)));

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_2));
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testReplaceDataPoints() {

        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEvent> consumerMock = mock(Consumer.class);
        final var listenerMock = mock(HiLiteListener.class);
        m_hlh.addHiLiteListener(listenerMock);

        new DefaultNodeService(m_nnc, consumerMock).selectDataPoints("projectId", "workflowId", "nodeId", "view",
            SelectionEventMode.REPLACE.toString(), ROWKEYS_2);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, never()).hiLite(any());
            verify(listenerMock, never()).unHiLite(any());
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, times(1))
                .replaceHiLite(argThat(ke -> ke.keys().equals(stringListToRowKeySet(ROWKEYS_2))));
            verify(consumerMock, never()).accept(any());
        });

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_2));
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testConsumeReplaceHiLiteEvent() {

        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEvent> consumerMock = mock(Consumer.class);

        @SuppressWarnings("unused")
        final var dns = new DefaultNodeService(m_nnc, consumerMock);
        m_nnc.getNodeModel().getInHiLiteHandler(0).fireReplaceHiLiteEvent(stringListToRowKeySet(ROWKEYS_2));

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS)
            .untilAsserted(() -> verify(consumerMock, times(1))
                .accept(argThat(se -> se.getKeys().equals(ROWKEYS_2) && se.getMode() == REPLACE)));

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_2));
        m_hlh.fireClearHiLiteEvent();
    }

}
