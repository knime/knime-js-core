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
 *   Aug 25, 2021 (hornm): created
 */
package org.knime.js.cef.nodeview;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.text.TextInitialDataService;
import org.knime.core.webui.node.view.NodeView;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.page.Page;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A browser function which returns a {@link NodeViewInfo} object.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GetNodeViewInfoBrowserFunction extends BrowserFunction {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String FUNCTION_NAME = "getNodeViewInfo";

    private NativeNodeContainer m_nnc;

    /**
     * @param browser
     * @param nnc
     */
    public GetNodeViewInfoBrowserFunction(final Browser browser, final NativeNodeContainer nnc) {
        super(browser, FUNCTION_NAME);
        m_nnc = nnc;
    }

    @Override
    public Object function(final Object[] args) {
        NodeView nodeView = NodeViewManager.getInstance().getNodeView(m_nnc);

        Page page = nodeView.getPage();
        if (page.isWebComponent() && !page.isCompletelyStatic()) {
            throw new IllegalStateException("An 'internal' node view must only provide static resources");
        }
        String viewName = m_nnc.getNodeViewName(0);
        InitialDataService initDataService = nodeView.getInitialDataService().orElse(null);
        String initData;
        if (initDataService instanceof TextInitialDataService) {
            initData = ((TextInitialDataService)initDataService).getInitialData();
        } else {
            initData = null;
        }
        String remoteDebugPort = System.getProperty("chromium.remote_debugging_port");
        NodeViewInfo info = NodeViewInfo.create(viewName, getUrl(), initData, page.isWebComponent(), remoteDebugPort);
        try {
            return MAPPER.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize the node view info into json", e);
        }
    }

    private String getUrl() {
        NodeFactory<NodeModel> factory = m_nnc.getNode().getFactory();
        String debugUrl = NodeViewManager.getNodeViewDebugUrl(factory.getClass()).orElse(null);
        if (debugUrl == null) {
            return NodeViewManager.getInstance().getNodeViewPageUrl(m_nnc).orElseThrow(
                () -> new IllegalStateException("No node view page url available for node " + m_nnc.getNameWithID()));
        } else {
            return debugUrl;
        }
    }

}
