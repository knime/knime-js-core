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

import java.io.File;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SinglePageWebResourceController;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WebResourceController.WizardPageContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.js.core.layout.DefaultLayoutCreatorImpl;
import org.knime.js.core.layout.LayoutVersion;
import org.knime.js.core.layout.bs.JSONLayoutPage;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Check SubNode component behavior with both versioned and unversioned saved components.
 *
 * @author benlaney
 */
public class TestVersionedLayout extends WorkflowTestCase {

    private File m_workflowDir;

    private NodeID m_subNode1; // unversioned pre4.2.0, no layout

    private NodeID m_subNode2; // unversioned pre4.2.0, layout

    private NodeID m_subNode3; // versioned 4.2.0, no layout

    private NodeID m_subNode4; // versioned 4.2.0, layout

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
        m_subNode1 = new NodeID(baseID, 3);
        m_subNode2 = new NodeID(baseID, 4);
        m_subNode3 = new NodeID(baseID, 5);
        m_subNode4 = new NodeID(baseID, 6);
    }

    /**
     * Checks components saved with and without layout versions to simulate older workflows/components being loaded into
     * the new framework. Also tests consistent behavior saving these components with their default versions and check
     * consistent behavior upon reloading the saved workflow.
     *
     * @throws Exception
     */
    @Test
    public void testLayoutVersions() throws Exception {
        // check components saved without layout version
        checkSubNode(m_subNode1, true);
        checkSubNode(m_subNode2, true);
        // check components saved with current layout version
        checkSubNode(m_subNode3, false);
        checkSubNode(m_subNode4, false);
        // loading old workflows & components should not dirty the workflow
        assertFalse("Loaded workflow should not be dirty", getManager().isDirty());
        // expect clean execution
        executeAllAndWait();
        // check components post execution
        checkSubNode(m_subNode1, true);
        checkSubNode(m_subNode1, true);
        checkSubNode(m_subNode3, false);
        checkSubNode(m_subNode4, false);
        // check clean save
        testSavingLayoutVersions();
        initWorkflowFromTemp();
        // check consistent behavior after reload
        checkSubNode(m_subNode1, true);
        checkSubNode(m_subNode2, true);
        checkSubNode(m_subNode3, false);
        checkSubNode(m_subNode4, false);
        // loading saved workflows & components should not dirty the workflow
        assertFalse("Loaded workflow should not be dirty", getManager().isDirty());

    }

    /**
     * Test default saving and closing of the workflow.
     *
     * @throws Exception
     */
    private void testSavingLayoutVersions() throws Exception {
        getManager().save(getDefaultWorkflowDirectory(), new ExecutionMonitor(), true);
        closeWorkflow();
    }

    /**
     * Check the component characteristics based on default layout version behavior.
     *
     * @param node the component nodeId.
     * @param isPreVersionLayout if the component was created before versions were introduced.
     * @throws Exception
     */
    private void checkSubNode(final NodeID node, final boolean isPreVersionLayout) throws Exception {
        final WorkflowManager wfm = getManager();
        SubNodeContainer container = (SubNodeContainer)findNodeContainer(node);
        assertNotNull(container);

        Optional<LayoutVersion> layoutVersion = LayoutVersion.get(container.getLayoutVersion());
        assertTrue(layoutVersion.isPresent());
        if (isPreVersionLayout) {
            assertTrue("Default layout version should be older than current",
                layoutVersion.get().isOlderThan(LayoutVersion.V4020));
        } else {
            assertTrue("Current layout versions should be equal or newer than version introducing layout versions",
                0 <= layoutVersion.get().compareTo(LayoutVersion.V4020));
        }

        WizardPageContent page = getAndCheckPage(wfm, node);
        checkV4020Markers(page, isPreVersionLayout);
    }

    private static WizardPageContent getAndCheckPage(final WorkflowManager wfm, final NodeID node) {
        SinglePageWebResourceController spc = new SinglePageWebResourceController(wfm, node);
        assertTrue("Should have subnode view", spc.isSubnodeViewAvailable());
        WizardPageContent page = spc.getWizardPage();
        assertNotNull("Page content should be available", page);
        return page;
    }

    /**
     * Checks for V4020 specific markers in layouts.
     *
     * @param page the page to check
     * @param isPreVersionLayout if the page belongs to a component created before the introduction of Layout Versions.
     * @throws Exception
     */
    private static void checkV4020Markers(final WizardPageContent page, final boolean isPreVersionLayout)
        throws Exception {
        String layoutString = page.getLayoutInfo();
        assertNotNull("Page layout should be available", layoutString);
        assertFalse(DefaultLayoutCreatorImpl.isMissingLegacyFlag(layoutString));
        ObjectMapper mapper = JSONLayoutPage.getConfiguredVerboseObjectMapper();
        JSONLayoutPage layout = mapper.readerFor(JSONLayoutPage.class).readValue(layoutString);
        assertNotNull("Layout should be deserializable", layout);
        if (isPreVersionLayout) {
            assertTrue("Pre-versioned layout not reflected in page settings", layout.getParentLayoutLegacyMode());
        } else {
            assertFalse("Versioned layout default settings not applied", layout.getParentLayoutLegacyMode());
        }
    }
}
