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

import java.util.List;

import org.knime.core.webui.node.view.NodeView;
import org.knime.core.webui.node.view.table.selection.SelectionTranslationService;

/**
 * {@link NodeView}s for the new web-ui assume a certain backend to be available when opened from the desktop
 * application.
 *
 * The required backend is actually the so called 'gateway API' (called through json-rpc in case of the desktop app)
 * which is not available to the 'this' container of a node view (i.e. the node view 'container' which opens the node
 * views in the predominantly java-based (classic) UI.
 *
 * This interface mirrors the few gateway API methods that are necessary to make the node views work here, too, without
 * requiring the node view framework to call out to yet another backend (apart from the gateway API). I.e. all methods
 * defined here are defined in the gateway API, too, and their implementations ideally re-use the very same logic.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 * @since 4.5
 */
public interface NodeService {

    /**
     * Returns the node-view representation for the frontend to render it. Enables the frontend to render nested
     * node-view ui-extensions.
     *
     * @param projectId -
     * @param workflowId -
     * @param versionId -
     * @param nodeId -
     * @return representation of the node-view
     * @since 5.6
     */
    Object getNodeView(String projectId, String workflowId, String versionId, String nodeId);

    /**
     * Sends a request to a node view's or dialog's data service of a certain type.
     *
     * @param projectId -
     * @param workflowId -
     * @param versionId -
     * @param nodeId -
     * @param extensionType the node 'extension', i.e. view or dialog, to direct the data service call to
     * @param serviceType specified the type of service to call
     * @param request the request
     * @return the data service response
     */
    String callNodeDataService(String projectId, String workflowId, String versionId, String nodeId,
        String extensionType, String serviceType, String request);

    /**
     * Updates the selected data points as specified by the 'mode' and identified by their row keys. Unselects any other
     * data points, if they were previously selected.
     *
     * @param projectId -
     * @param workflowId -
     * @param versionId -
     * @param nodeId -
     * @param mode the type of selection modification, i.e., ADD, REMOVE, or REPLACE
     * @param selection a list of strings that can be {@link SelectionTranslationService translated} to the row keys
     *            affected by the data point selection modification
     *
     * @since 4.6
     */
    void updateDataPointSelection(String projectId, String workflowId, String versionId, String nodeId, String mode,
        final List<String> selection);

    /**
     * Changes the node state of multiple nodes represented by a list of node-ids.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeIds The list of node ids of the nodes to be changed. All ids must reference nodes on the same workflow
     *            level. If no node ids are given the state of the parent workflow (i.e. the one referenced by
     *            workflow-id) is changed which is equivalent to change the states of all contained nodes.
     * @param action The action (reset, cancel, execute) to be performed in order to change the node&#39;s state.
     *
     * @since 4.6
     */
    void changeNodeStates(String projectId, String workflowId, List<String> nodeIds, String action);

}
