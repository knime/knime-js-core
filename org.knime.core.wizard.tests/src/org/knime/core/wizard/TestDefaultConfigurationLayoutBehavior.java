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
 *   Nov 11, 2020 (bogenrieder): created
 */
package org.knime.core.wizard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.util.ConfigurationLayoutUtil;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.SubnodeContainerConfigurationStringProvider;
import org.knime.js.core.layout.bs.JSONLayoutPage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * Check SubNodeContainer configuration layout v4.3.0
 *
 * @author Daniel Bogenrieder
 */
public class TestDefaultConfigurationLayoutBehavior extends WorkflowTestCase {

    private NodeID m_subnodeID;
    private NodeID m_oldSubNode;
    private NodeID m_mixedNodes;
    private NodeID m_disabledNodes;
    private NodeID m_containsUnreferencedNodes;
    private NodeID m_onlyViewNodes;

    /**
     * Creates and copies the workflow into a temporary directory.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        NodeID baseID = loadAndSetWorkflow();
        m_subnodeID = new NodeID(baseID, 6);
        m_oldSubNode = new NodeID(baseID, 5);
        m_mixedNodes = new NodeID(baseID, 10);
        m_disabledNodes = new NodeID(baseID, 11);
        m_containsUnreferencedNodes = new NodeID(baseID, 15);
        m_onlyViewNodes = new NodeID(baseID, 18);
    }

    /**
     * Simple test if a combined view can successfully be created and contains the configuration layout and expected
     * node order
     *
     * @throws Exception
     */
    @Test
    @SuppressWarnings({"java:S3740", "rawtypes"}) // DialogNode generics
    public void testExecuteAndCreateSubnodeConfiguration() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_subnodeID);
        assertNotNull(container);
        SubnodeContainerConfigurationStringProvider configurationLayoutProvider =
            container.getSubnodeConfigurationLayoutStringProvider();

        Map<NodeID, DialogNode> configurationNodes =
            container.getWorkflowManager().findNodes(DialogNode.class, false);
        List<Integer> order = ConfigurationLayoutUtil.getConfigurationOrder(configurationLayoutProvider,
            configurationNodes, getManager());
        assertEquals("Configuration node order should be: 2, 4, 3, 5, 1", order, Arrays.asList(2, 4, 3, 5, 1));
    }

    /**
     * Simple test if an older component (previous to 4.3) can be successfully executed, although it does not have a
     * configuration layout defined.
     *
     * @throws Exception
     */
    @Test
    @SuppressWarnings({"java:S3740", "rawtypes"}) // DialogNode generics
    public void testExecuteAndCreateOldSubnodeConfiguration() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_oldSubNode);
        assertNotNull(container);
        SubnodeContainerConfigurationStringProvider configurationLayoutProvider =
            container.getSubnodeConfigurationLayoutStringProvider();

        Map<NodeID, DialogNode> configurationNodes =
            container.getWorkflowManager().findNodes(DialogNode.class, false);
        List<Integer> order = ConfigurationLayoutUtil.getConfigurationOrder(configurationLayoutProvider,
            configurationNodes, getManager());
        assertArrayEquals("There should be no order", order.toArray(), new String[]{});
    }

    /**
     * Check if a component with a mix of configuration, view and quickform nodes can be executed and only the two
     * dialog nodes are shown in the configuration layout
     *
     * @throws Exception
     */
    @Test
    @SuppressWarnings({"java:S3740", "rawtypes"}) // DialogNode generics
    public void testMixedConfigurationAndQuickFormNodes() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_mixedNodes);
        assertNotNull(container);
        SubnodeContainerConfigurationStringProvider configurationLayoutProvider =
            container.getSubnodeConfigurationLayoutStringProvider();
        JSONLayoutPage page = new JSONLayoutPage();
        ObjectMapper mapper = JSONLayoutPage.getConfiguredObjectMapper();
        ObjectReader reader = mapper.readerForUpdating(page);
        reader.readValue(configurationLayoutProvider.getConfigurationLayoutString());
        assertTrue("Only two dialog nodes should be present", page.getRows().size() == 2);

        Map<NodeID, DialogNode> configurationNodes =
            container.getWorkflowManager().findNodes(DialogNode.class, false);
        List<Integer> order = ConfigurationLayoutUtil.getConfigurationOrder(configurationLayoutProvider,
            configurationNodes, getManager());
        assertTrue("Only two dialog nodes should be present in the order", order.toArray().length == 2);
    }

    /**
     * Check if a component with a only view nodes contains no rows in the dialog layout editor
     *
     * @throws Exception
     */
    @Test
    public void testComponentWithOnlyViewNodes() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_onlyViewNodes);
        assertNotNull(container);
        SubnodeContainerConfigurationStringProvider configurationLayoutProvider =
            container.getSubnodeConfigurationLayoutStringProvider();
        JSONLayoutPage page = new JSONLayoutPage();
        ObjectMapper mapper = JSONLayoutPage.getConfiguredObjectMapper();
        ObjectReader reader = mapper.readerForUpdating(page);
        reader.readValue(configurationLayoutProvider.getConfigurationLayoutString());
        assertTrue("No dialog node should be present", page.getRows().size() == 0);
    }

    /**
     * Check if disabling of is saved to the layout
     *
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void testDisabledNodesInLayout() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_disabledNodes);
        assertNotNull(container);
        Map<NodeID, DialogNode> configurationNodes = container.getWorkflowManager().findNodes(DialogNode.class, false);
        LinkedHashMap<NodeIDSuffix, DialogNode> resultMapConfiguration = new LinkedHashMap<>();
        for (Map.Entry<NodeID, DialogNode> entry : configurationNodes.entrySet()) {
            NodeID.NodeIDSuffix idSuffix = NodeID.NodeIDSuffix.create(container.getID(), entry.getKey());
            resultMapConfiguration.put(idSuffix, entry.getValue());
        }
        DialogNode disabledNode = resultMapConfiguration.get(NodeID.NodeIDSuffix.fromString("0:2"));
        assertTrue("Disabled property should be in the layout", disabledNode.isHideInDialog());
        DialogNode enabledNode = resultMapConfiguration.get(NodeID.NodeIDSuffix.fromString("0:1"));
        assertFalse("Node 3 should not be disabled", enabledNode.isHideInDialog());
    }

    /**
     * Check if unreferenced views are correctly added to the layout
     *
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void testComponentContainingUnreferencedNodes() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_containsUnreferencedNodes);
        assertNotNull(container);
        SubnodeContainerConfigurationStringProvider configurationStringProvider =
            container.getSubnodeConfigurationLayoutStringProvider();

        Map<NodeID, DialogNode> dialogNodes = container.getWorkflowManager().findNodes(DialogNode.class, false);
        LinkedHashMap<NodeIDSuffix, DialogNode> resultMapConfiguration = new LinkedHashMap<>();
        for (Map.Entry<NodeID, DialogNode> entry : dialogNodes.entrySet()) {
            NodeID.NodeIDSuffix idSuffix = NodeID.NodeIDSuffix.create(container.getID(), entry.getKey());
            resultMapConfiguration.put(idSuffix, entry.getValue());
        }
        List<Integer> orderBefore = ConfigurationLayoutUtil.getConfigurationOrder(configurationStringProvider,
            dialogNodes, getManager());
        assertTrue("Only three dialog nodes should be present", orderBefore.toArray().length == 2);
        ConfigurationLayoutUtil.addUnreferencedDialogNodes(configurationStringProvider, resultMapConfiguration);
        List<Integer> orderAfter = ConfigurationLayoutUtil.getConfigurationOrder(configurationStringProvider,
            dialogNodes, getManager());
        assertTrue("Five dialog nodes should be present", orderAfter.toArray().length == 4);
        assertEquals("Configuration node order should be: 12, 14, 17, 18", orderAfter, Arrays.asList(12, 14, 17, 18));
    }
}
