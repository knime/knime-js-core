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
 *   Jun 29, 2020 (benlaney): created
 */
package org.knime.core.wizard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.SubnodeContainerLayoutStringProvider;

/**
 * Check SubNodeContainer layout v4.2.0 vs. pre4.2.0 detection and default layout creation methods.
 *
 * @author benlaney
 */
public class TestDefaultLayoutBehavior extends WorkflowTestCase {

    private static final String POST_42_SEARCH_TERM = "parentLayoutLegacyMode";

    private NodeID m_subNode1; // saved pre-changes layout
    private NodeID m_subNode2; // saved pre-changes no layout
    private NodeID m_subNode3; // saved post-changes layout
    private NodeID m_subNode4; // saved post-changes no layout

    /**
     * Creates and copies the workflow into a temporary directory.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        initWorkflowFromTemp();
    }

    private void initWorkflowFromTemp() throws Exception {
        NodeID baseID = loadAndSetWorkflow();
        assertFalse("Loaded workflow should not be dirty", getManager().isDirty());
        m_subNode1 = new NodeID(baseID, 3);
        m_subNode2 = new NodeID(baseID, 4);
        m_subNode3 = new NodeID(baseID, 7);
        m_subNode4 = new NodeID(baseID, 8);
    }

    /**
     * Test the an old component saved with an empty layout. It should require a new layout and be missing the required
     * legacy flag.
     *
     * @throws Exception
     */
    @Test
    public void testOldEmptyLayout() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_subNode1);
        assertNotNull(container);
        SubnodeContainerLayoutStringProvider layoutProvider = container.getSubnodeLayoutStringProvider();
        assertFalse(layoutProvider.checkOriginalContains(POST_42_SEARCH_TERM));
        assertTrue(layoutProvider.isPlaceholderLayout());
    }

    /**
     * Test the an old component saved with a custom layout. It should not require a new layout but be missing the
     * required legacy flag.
     *
     * @throws Exception
     */
    @Test
    public void testOldSavedLayout() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_subNode2);
        assertNotNull(container);
        SubnodeContainerLayoutStringProvider layoutProvider = container.getSubnodeLayoutStringProvider();
        assertFalse(layoutProvider.checkOriginalContains(POST_42_SEARCH_TERM));
        assertFalse(layoutProvider.isPlaceholderLayout());
    }

    /**
     * Test the new default layout for a saved component. It should not require a new layout or be missing the legacy
     * flag.
     *
     * @throws Exception
     */
    @Test
    public void testNewDefaultLayout() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_subNode3);
        assertNotNull(container);
        SubnodeContainerLayoutStringProvider layoutProvider = container.getSubnodeLayoutStringProvider();
        assertTrue(layoutProvider.checkOriginalContains(POST_42_SEARCH_TERM));
        assertFalse(layoutProvider.isPlaceholderLayout());
    }

    /**
     * Test a current custom component saved layout. It should not require a new layout or be missing the legacy flag.
     *
     * @throws Exception
     */
    @Test
    public void testNewSavedLayout() throws Exception {
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(m_subNode4);
        assertNotNull(container);
        SubnodeContainerLayoutStringProvider layoutProvider = container.getSubnodeLayoutStringProvider();
        assertTrue(layoutProvider.checkOriginalContains(POST_42_SEARCH_TERM));
        assertFalse(layoutProvider.isPlaceholderLayout());
    }
}
