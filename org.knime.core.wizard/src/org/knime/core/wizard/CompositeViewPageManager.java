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
 *   3 Apr 2017 (albrecht): created
 */
package org.knime.core.wizard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.interactive.ViewRequestHandlingException;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardViewResponse;
import org.knime.core.node.workflow.CompositeViewController;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.WebResourceController.WizardPageContent;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.js.core.JSONWebNodePage;

/**
 * Utility class which handles serialization/deserialization of component's composite views,
 * as well as forwarding and bundling requests for those.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @since 3.4
 */
public class CompositeViewPageManager extends AbstractPageManager {

    /**
     * Returns a {@link CompositeViewPageManager} instance for the given {@link CompositeViewPageManager}
     * @param workflowManager the {@link WorkflowManager} to get the {@link CompositeViewPageManager} instance for
     * @return a {@link CompositeViewPageManager} of the given {@link WorkflowManager}
     */
    public static CompositeViewPageManager of(final WorkflowManager workflowManager) {
        // return new instance, could also be used to invoke a caching/pooling service in the future
        return new CompositeViewPageManager(workflowManager);
    }

    /**
     * Creates a new SinglePageManager instance.
     *
     * @param workflowManager a {@link WorkflowManager} corresponding to the current workflow
     */
    private CompositeViewPageManager(final WorkflowManager workflowManager) {
        super(workflowManager);
    }

    private CompositeViewController getController(final NodeID containerNodeID) {
        return new CompositeViewController(getWorkflowManager(), containerNodeID);
    }

    /**
     * Checks different criteria to determine if a combined page view is available for a given metanode.
     * @param containerNodeID the {@link NodeID} of the metanode to check
     * @return true, if a view on the metanode is available, false otherwise
     */
    public boolean hasWizardPage(final NodeID containerNodeID) {
        return getController(containerNodeID).isSubnodeViewAvailable();
    }

    /**
     * Creates a wizard page object from a given node id
     *
     * @param containerNodeID the node id to create the wizard page for
     * @return a {@link JSONWebNodePage} object which can be used for serialization
     * @throws IOException if the layout of the wizard page can not be generated
     */
    public JSONWebNodePage createWizardPage(final NodeID containerNodeID) throws IOException {
        CompositeViewController sec = getController(containerNodeID);
        WizardPageContent page = sec.getWizardPage();
        return createWizardPageInternal(page);
    }

    /**
     * Creates a map of node id string to JSON view value string for all appropriate wizard nodes from a given node id.
     *
     * @param containerNodeID the node id to create the view value map for
     * @return a map containing all appropriate view values
     * @throws IOException on serialization error
     */
    public Map<String, String> createWizardPageViewValueMap(final NodeID containerNodeID) throws IOException {
        CompositeViewController sec = getController(containerNodeID);
        Map<NodeIDSuffix, WebViewContent> viewMap = sec.getWizardPageViewValueMap();
        Map<String, String> resultMap = new HashMap<String, String>();
        for (Entry<NodeIDSuffix, WebViewContent> entry : viewMap.entrySet()) {
            WebViewContent c = entry.getValue();
            resultMap.put(entry.getKey().toString(), new String(((ByteArrayOutputStream)c.saveToStream()).toByteArray()));
        }
        return resultMap;
    }

    /**
     * Validates a given map of view values contained in a given subnode.
     * @param viewValues a map with {@link NodeIDSuffix} string as key and parsed view value as value
     * @param containerNodeId the {@link NodeID} of the subnode
     * @return Null or empty map if validation succeeds, map of errors otherwise
     * @throws IOException on serialization error
     */
    public Map<String, ValidationError> validateViewValues(final Map<String, String> viewValues, final NodeID containerNodeId) throws IOException {
        try (WorkflowLock lock = getWorkflowManager().lock()) {
            /*ObjectMapper mapper = new ObjectMapper();
            for (String key : viewValues.keySet()) {
                String content = mapper.writeValueAsString(viewValues.get(key));
                viewValues.put(key, content);
            }*/
            if (!viewValues.isEmpty()) {
                CompositeViewController sec = getController(containerNodeId);
                return sec.validateViewValuesInPage(viewValues);
            } else {
                return Collections.emptyMap();
            }
        }
    }

    /**
     * Applies a given map of view values to a given subnode which have already been validated.
     * @param viewValues an already validated map with {@link NodeIDSuffix} string as key and parsed view value as value
     * @param containerNodeId the {@link NodeID} of the subnode
     * @param useAsDefault true, if values are supposed to be applied as new defaults, false if applied temporarily
     * @throws IOException on serialization error
     */
    public void applyValidatedViewValues(final Map<String, String> viewValues, final NodeID containerNodeId, final boolean useAsDefault) throws IOException {
        try (WorkflowLock lock = getWorkflowManager().lock()) {
            /*ObjectMapper mapper = new ObjectMapper();
            for (String key : viewValues.keySet()) {
                String content = mapper.writeValueAsString(viewValues.get(key));
                viewValues.put(key, content);
            }*/
            if (!viewValues.isEmpty()) {
                CompositeViewController sec = getController(containerNodeId);
                sec.loadValuesIntoPage(viewValues, false, useAsDefault, null);
            }
        }
    }

    /**
     * Applies a given map of view values to a given subnode which have already been validated and triggers reexecution
     * subsequently.
     *
     * @param valueMap an already validated map with {@link NodeIDSuffix} string as key and parsed view value as value
     * @param containerNodeId the {@link NodeID} of the subnode
     * @param useAsDefault true, if values are supposed to be applied as new defaults, false if applied temporarily
     * @throws IOException on serialization error
     */
    public void applyValidatedValuesAndExecute(final Map<String, String> valueMap, final NodeID containerNodeId,
        final boolean useAsDefault) throws IOException {
        try (WorkflowLock lock = getWorkflowManager().assertLock()) {
            applyValidatedViewValues(valueMap, containerNodeId, useAsDefault);
            getController(containerNodeId).executeSinglePage();
        }
    }

    /**
     * Applies a pre-filtered subset of serialized view values to nodes within a subnode container and partially
     * re-executes the component, starting with the reset {@link NodeID} provided.
     *
     * @param valueMap a map with {@link NodeIDSuffix} string as key and parsed view value as value.
     * @param containerNodeId the {@link NodeID} of the subnode.
     * @param resetNodeId the absolute {@link NodeID} which should initiate partial re-execution.
     * @return a map of validation errors which occurred when applying the updated values or else null.
     *
     * @since 4.4
     */
    public Map<String, ValidationError> applyPartialValuesAndReexecute(final Map<String, String> valueMap,
        final NodeID containerNodeId, final NodeID resetNodeId) {
        try (WorkflowLock lock = getWorkflowManager().assertLock()) {
            return getController(containerNodeId).reexecuteSinglePage(resetNodeId, valueMap);
        }
    }

    /**
     * Processes a JSON serialized request issued by a view and returns the corresponding JSON serialized
     * response.
     *
     * @param nodeID The node id of the node that the request belongs to.
     * @param jsonRequest The JSON serialized view request
     * @param containerNodeId the {@link NodeID} of the containing subnode
     * @param exec The execution monitor to set progress and check possible cancellation.
     * @return A JSON serialized {@link WizardViewResponse} object.
     * @throws ViewRequestHandlingException If the request handling or response generation fails for any
     * reason.
     * @throws InterruptedException If the thread handling the request is interrupted.
     * @throws CanceledExecutionException If the handling of the request was canceled e.g. by user
     * intervention.
     * @since 3.7
     */
    String processViewRequest(final String nodeID, final String jsonRequest, final NodeID containerNodeId,
        final ExecutionMonitor exec)
        throws ViewRequestHandlingException, InterruptedException, CanceledExecutionException {
        try (WorkflowLock lock = getWorkflowManager().lock()) {
            CompositeViewController sec = getController(containerNodeId);
            WizardViewResponse response = sec.processViewRequest(nodeID, jsonRequest, exec);
            return serializeViewResponse(response);
        }
    }

}
