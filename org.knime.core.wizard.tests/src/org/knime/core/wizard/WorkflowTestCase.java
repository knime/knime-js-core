/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 */
package org.knime.core.wizard;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.junit.After;
import org.junit.Assert;
import org.knime.core.data.container.DataContainerSettings;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerParent;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResultEntry.LoadResultEntryType;
import org.knime.core.node.workflow.WorkflowPersistor.WorkflowLoadResult;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;

/**
 *
 * @author wiswedel, University of Konstanz
 */
public abstract class WorkflowTestCase {

    /** Names of workflow manager 'roots' that are used by some framework code, e.g. the parent object of all
     * metanodes in the metanode repository. Used to filter for 'dangling' workflows after test case completion.
     */
    private static final String[] KNOWN_CHILD_WFM_NAME_SUBSTRINGS = new String[]{"MetaNode Repository",
        "Workflow Template Root", "Streamer-Subnode-Parent", WorkflowManager.EXTRACTED_WORKFLOW_ROOT.getName()};

    private final NodeLogger m_logger = NodeLogger.getLogger(getClass());

    private WorkflowManager m_manager;

    protected NodeID loadAndSetWorkflow() throws Exception {
        File workflowDir = getDefaultWorkflowDirectory();
        return loadAndSetWorkflow(workflowDir);
    }

    protected NodeID loadAndSetWorkflow(final File workflowDir) throws Exception {
        WorkflowManager m = loadWorkflow(workflowDir, new ExecutionMonitor()).getWorkflowManager();
        setManager(m);
        return m.getID();
    }

    protected WorkflowLoadResult loadWorkflow(final File workflowDir, final ExecutionMonitor exec) throws Exception {
        return loadWorkflow(workflowDir, exec, DataContainerSettings.getDefault());
    }

    protected WorkflowLoadResult loadWorkflow(final File workflowDir, final ExecutionMonitor exec,
            final DataContainerSettings settings) throws Exception {
        final WorkflowContextV2 context = WorkflowContextV2.forTemporaryWorkflow(workflowDir.toPath(), null);
        return loadWorkflow(workflowDir, exec, new WorkflowLoadHelper(context, settings));
    }


    protected WorkflowLoadResult loadWorkflow(final File workflowDir, final ExecutionMonitor exec,
            final WorkflowLoadHelper loadHelper) throws Exception {
        WorkflowLoadResult loadResult = WorkflowManager.ROOT.load(workflowDir, exec, loadHelper, false);
        WorkflowManager m = loadResult.getWorkflowManager();
        if (m == null) {
            throw new Exception("Errors reading workflow: "
                    + loadResult.getFilteredError("", LoadResultEntryType.Ok));
        } else {
            switch (loadResult.getType()) {
            case Ok:
                break;
            default:
                m_logger.info("Errors reading workflow (proceeding anyway): ");
                dumpLineBreakStringToLog(loadResult.getFilteredError("",
                    LoadResultEntryType.Warning));
            }
        }
        return loadResult;
    }

    /**
     * @return
     * @throws Exception
     * @throws IOException */
    protected File getDefaultWorkflowDirectory() throws Exception {
        String classSimpleName = getClass().getSimpleName();
        classSimpleName = classSimpleName.substring(0, 1).toLowerCase() + classSimpleName.substring(1);
        return getWorkflowDirectory(classSimpleName);
    }

    protected File getWorkflowDirectory(final String pathRelativeToTestClass) throws Exception {
        ClassLoader l = getClass().getClassLoader();
        String workflowDirString = getClass().getPackage().getName();
        String workflowDirPath = workflowDirString.replace('.', '/');
        workflowDirPath = workflowDirPath.concat("/" + pathRelativeToTestClass);
        URL workflowURL = l.getResource(workflowDirPath);
        if (workflowURL == null) {
            throw new Exception("Can't load workflow that's expected to be in directory " + workflowDirPath);
        }

        if (!"file".equals(workflowURL.getProtocol())) {
            workflowURL = FileLocator.resolve(workflowURL);
        }

        File workflowDir = new File(workflowURL.getFile());
        if (!workflowDir.isDirectory()) {
            throw new Exception("Can't load workflow directory: "
                    + workflowDir);
        }
        return workflowDir;
    }

    /**
     * @param manager the manager to set
     */
    protected void setManager(final WorkflowManager manager) {
        m_manager = manager;
    }

    /**
     * @return the manager
     */
    protected WorkflowManager getManager() {
        return m_manager;
    }

    /** Find the workflow manager that directly contains the node with the argument ID.
     * @param id ID to query
     * @return parent workflow of node with ID (might be meta node or wfm in subnode).
     */
    protected WorkflowManager findParent(final NodeID id) {
        CheckUtils.checkArgumentNotNull(m_manager, "WFM not set");
        final NodeID mgrID = m_manager.getID();
        CheckUtils.checkArgument(id.hasPrefix(mgrID), "NodeID %s has not same prefix as WFM: %s", id, mgrID);

        // this is tricky since we have to deal with subnode containers also. subnode contain a wfm with id suffix "0".
        // this could be a node to query in a subnode
        // 0       - ID of m_manager
        // 0:1     - ID of contained subnode container
        // 0:1:0   - ID of WFM in subnode container
        // 0:1:0:2 - ID of node within subnode

        // here is node contained in a meta node
        // 0       - ID of m_manager
        // 0:2     - ID of contained meta node / wfm
        // 0:1:2   - ID of node within meta node

        Stack<NodeID> prefixStack = new Stack<>();
        NodeID currentID = id;
        while (!currentID.hasSamePrefix(mgrID)) {
            currentID = currentID.getPrefix();
            prefixStack.push(currentID);
        }
        NodeContainerParent currentParent = m_manager;
        while (!prefixStack.isEmpty()) {
            if (currentParent instanceof WorkflowManager) {
                currentParent = ((WorkflowManager)currentParent).getNodeContainer(
                    prefixStack.pop(), NodeContainerParent.class, true);
            } else if (currentParent instanceof SubNodeContainer) {
                SubNodeContainer subnode = (SubNodeContainer)currentParent;
                NodeID expectedWFMID = prefixStack.pop();
                final WorkflowManager innerWFM = subnode.getWorkflowManager();
                Assert.assertEquals(innerWFM.getID(), expectedWFMID);
                currentParent = innerWFM;
            }
        }
        return (WorkflowManager)currentParent;
    }

    protected NodeContainer findNodeContainer(final NodeID id) {
        WorkflowManager parent = findParent(id);
        return parent.getNodeContainer(id);
    }

    protected static int countFilesInDirectory(final File directory) {
        int count = 0;
        for (File child : directory.listFiles()) {
            if (child.isDirectory()) {
                count += countFilesInDirectory(child);
            } else {
                count += 1;
            }
        }
        return count;
    }

    protected static Collection<SingleNodeContainer> iterateSNCs(final WorkflowManager wfm, final boolean recurse) {
        ArrayList<SingleNodeContainer> result = new ArrayList<>();
        for (NodeContainer nc : wfm.getNodeContainers()) {
            if (nc instanceof SingleNodeContainer) {
                result.add((SingleNodeContainer)nc);
            } else if (recurse) {
                WorkflowManager m;
                if (nc instanceof WorkflowManager) {
                    m = (WorkflowManager)nc;
                } else {
                    m = ((SubNodeContainer)nc).getWorkflowManager();
                }
                result.addAll(iterateSNCs(m, true));
            }
        }
        return result;
    }

    protected ConnectionContainer findLeavingWorkflowConnection(final NodeID id,
            final int port) throws Exception {
        NodeContainer nc = findNodeContainer(id);
        if (!(nc instanceof WorkflowManager)) {
            throw new IllegalArgumentException("Node " + id
                    + " is not a workflow manager");
        }
        for (ConnectionContainer cc
                : ((WorkflowManager)nc).getConnectionContainers()) {
            if (cc.getDest().equals(id) && cc.getDestPort() == port) {
                return cc;
            }
        }
        return null;
    }

    protected void executeAllAndWait() throws Exception {
        m_manager.getParent().executeUpToHere(m_manager.getID());
        waitWhileInExecution();
    }

    protected void executeAndWait(final NodeID... ids)
        throws Exception {
        NodeID prefix = null;
        WorkflowManager parent = null;
        for (NodeID id : ids) {
            if (prefix == null) {
                prefix = id.getPrefix();
                parent = findParent(id);
            } else if (!prefix.equals(id.getPrefix())) {
                throw new IllegalArgumentException("Mixing NodeIDs of "
                        + "different levels " + Arrays.toString(ids));
            }
        }
        if (parent != null) {
            parent.executeUpToHere(ids);
        }
        for (NodeID id : ids) {
            waitWhileNodeInExecution(id);
        }
    }

    protected void waitWhileInExecution() throws Exception {
        assert !m_manager.isLockedByCurrentThread();
        waitWhileNodeInExecution(m_manager);
    }

    protected void waitWhileNodeInExecution(final NodeID id) throws Exception {
        waitWhileNodeInExecution(findNodeContainer(id));
    }

    protected void waitWhileNodeInExecution(final NodeContainer node)
    throws Exception {
        waitWhile(node, new Hold() {
            @Override
            protected boolean shouldHold() {
                return node.getNodeContainerState().isExecutionInProgress();
            }
        });
    }

    protected void waitWhile(final NodeContainer nc,
            final Hold hold) throws Exception {
        if (!hold.shouldHold()) {
            return;
        }

        final ReentrantLock lock = nc instanceof WorkflowManager ? ((WorkflowManager)nc).getReentrantLockInstance()
            : nc.getParent().getReentrantLockInstance();
        final Condition condition = lock.newCondition();
        NodeStateChangeListener l = new NodeStateChangeListener() {
            /** {@inheritDoc} */
            @Override
            public void stateChanged(final NodeStateEvent state) {
                lock.lock();
                try {
                    m_logger.info("Received " + state);
                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        };
        lock.lock();
        nc.addNodeStateChangeListener(l);
        try {
            while (hold.shouldHold()) {
                int secToWait = hold.getSecondsToWaitAtMost();
                if (secToWait > 0) {
                    condition.await(secToWait, TimeUnit.SECONDS);
                    break;
                } else {
                    condition.await(5, TimeUnit.SECONDS);
                }
            }
        } finally {
            lock.unlock();
            nc.removeNodeStateChangeListener(l);
        }
    }

    @After
    public void tearDown() throws Exception {
        closeWorkflow();

        // Executed after each test, checks that there are no open workflows dangling around.
        Collection<NodeContainer> openWorkflows = new ArrayList<>(WorkflowManager.ROOT.getNodeContainers());
        openWorkflows.removeIf(nc -> StringUtils.containsAny(nc.getName(), KNOWN_CHILD_WFM_NAME_SUBSTRINGS));
        if (!openWorkflows.isEmpty()) {
            throw new AssertionError(openWorkflows.size() + " dangling workflows detected: " + openWorkflows);
        }
    }

    /**
     * @throws Exception
     */
    protected void closeWorkflow() throws Exception {
        if (m_manager != null) {
            // in most cases we wait for individual nodes to finish. This
            // does not mean that the workflow state is also updated, give
            // it a second to finish its cleanup
            waitWhile(m_manager, new Hold() {
                /** {@inheritDoc} */
                @Override
                protected boolean shouldHold() {
                    return m_manager.getNodeContainerState().isExecutionInProgress();
                }
                /** {@inheritDoc} */
                @Override
                protected int getSecondsToWaitAtMost() {
                    return 2;
                }
            });
            if (!WorkflowManager.ROOT.canRemoveNode(m_manager.getID())) {
                String error = "Cannot remove workflow, dump follows";
                m_logger.error(error);
                dumpWorkflowToLog(m_manager);
                fail(error);
            }
            WorkflowManager.ROOT.removeProject(m_manager.getID());
            setManager(null);
        }
    }

    protected void dumpWorkflowToLog(final WorkflowManager manager) throws IOException {
        String toString = manager.printNodeSummary(manager.getID(), 0);
        dumpLineBreakStringToLog(toString);
    }

    protected void dumpLineBreakStringToLog(final String s) throws IOException {
        BufferedReader r = new BufferedReader(new StringReader(s));
        String line;
        while ((line = r.readLine()) != null) {
            m_logger.info(line);
        }
        r.close();
    }

    protected static abstract class Hold {
        protected abstract boolean shouldHold();
        protected int getSecondsToWaitAtMost() {
            return -1;
        }
    }

}
