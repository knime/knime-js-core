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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.data.RowKey;
import org.knime.core.node.port.PortType;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.virtual.subnode.VirtualSubNodeInputNodeFactory;
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

    @Test
    public void testSelectDataPoints() {

        final var listenerMock = mock(HiLiteListener.class);
        m_hlh.addHiLiteListener(listenerMock);

        new DefaultNodeService(m_nnc).updateDataPointSelection("projectId", "workflowId", "nodeId",
            SelectionEventMode.ADD.toString(), ROWKEYS_1_2);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, times(1)).hiLite(argThat(ke -> ke.keys().equals(stringListToRowKeySet(ROWKEYS_1_2))));
            verify(listenerMock, never()).unHiLite(any());
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, never()).replaceHiLite(any());
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

        @SuppressWarnings("unchecked")
        final BiConsumer<String, SelectionEvent> selectionEventConsumer = mock(BiConsumer.class);
        var component = (SubNodeContainer)m_wfm.getNodeContainer(componentId);
        var nodeService = new DefaultNodeService(component);
        setupSelectionEventSource(selectionEventConsumer, component);
        nodeService.updateDataPointSelection("projectId_not_used", "workflowId_not_used", "root:4:0:2",
            SelectionEventMode.ADD.toString(), ROWKEYS_1_2);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(selectionEventConsumer, times(1)).accept(eq("SelectionEvent"),
                argThat(se -> verifySelectionEvent(se, "root:4", "root:4:0:3")));
        });
    }

    @Test
    public void testUnSelectDataPoints() throws InterruptedException {
        final var listenerMock = mock(HiLiteListener.class);

        m_hlh.addHiLiteListener(listenerMock);
        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1_2));

        new DefaultNodeService(m_nnc).updateDataPointSelection("projectId", "workflowId", "nodeId", REMOVE.toString(), ROWKEYS_1);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, times(1)).hiLite(any());
            verify(listenerMock, times(1)).unHiLite(argThat(ke -> ke.keys().equals(stringListToRowKeySet(ROWKEYS_1))));
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, never()).replaceHiLite(any());
        });

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_2));
        m_hlh.fireClearHiLiteEvent();
    }

    @Test
    public void testReplaceDataPoints() throws InterruptedException {
        final var listenerMock = mock(HiLiteListener.class);

        m_hlh.addHiLiteListener(listenerMock);
        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1));

        new DefaultNodeService(m_nnc).updateDataPointSelection("projectId", "workflowId", "nodeId", REPLACE.toString(),
            ROWKEYS_2);

        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(() -> {
            verify(listenerMock, times(1)).hiLite(any());
            verify(listenerMock, never()).unHiLite(any());
            verify(listenerMock, never()).unHiLiteAll(any());
            verify(listenerMock, times(1))
                .replaceHiLite(argThat(ke -> ke.keys().equals(stringListToRowKeySet(ROWKEYS_2))));
        });

        assertEquals(m_hlh.getHiLitKeys(), stringListToRowKeySet(ROWKEYS_2));
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

    private static Set<RowKey> stringListToRowKeySet(final List<String> keys) {
        return keys.stream().map(RowKey::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static boolean verifySelectionEvent(final SelectionEvent se, final String workflowId, final String nodeId) {
        return se.getKeys().equals(ROWKEYS_1_2) && se.getMode() == SelectionEventMode.ADD
            && se.getNodeId().equals(nodeId) && se.getWorkflowId().equals(workflowId)
            && se.getProjectId().startsWith(WORKFLOW_NAME);
    }

    private static void setupSelectionEventSource(final BiConsumer<String, SelectionEvent> selectionEventConsumer,
        final SubNodeContainer node) {
        var selectionEventSource =
            new SelectionEventSource((n, o) -> selectionEventConsumer.accept(n, (SelectionEvent)o));
        WizardPageUtil.getWizardPageNodes(node.getWorkflowManager())
            .forEach(nnc -> selectionEventSource.addEventListenerFor(nnc));
    }

}
