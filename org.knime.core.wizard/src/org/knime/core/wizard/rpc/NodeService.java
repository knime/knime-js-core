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
     * Sends a request to a node view's node data service of a certain type.
     *
     * @param projectId
     * @param workflowId
     * @param nodeId
     * @param serviceType specified the type of service to call
     * @param request the request
     * @return the data service response
     */
    String callNodeViewDataService(String projectId, String workflowId, String nodeId, String serviceType,
        String request);

    /**
     * Selects data points, as identified by their row keys. Unselects any other data points, if they were previously
     * selected.
     *
     * @param projectId
     * @param workflowId
     * @param nodeId
     * @param mode the type of selection modification, i.e., ADD, REMOVE, or REPLACE
     * @param rowKeys the keys affected by the data point selection modification
     */
    void selectDataPoints(String projectId, String workflowId, String nodeId, String mode, List<String> rowKeys);

}
