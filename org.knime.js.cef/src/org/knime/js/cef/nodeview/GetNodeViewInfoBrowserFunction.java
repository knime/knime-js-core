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

import java.io.IOException;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.webui.NodeView;
import org.knime.core.node.webui.NodeViewFactory;
import org.knime.core.node.webui.Page;
import org.knime.core.node.webui.internal.NodeViewFactoryInternal;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.webui.NodeViewUtil;

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
        boolean isInternalView = false;
        NodeView<NodeModel> nodeView = null;
        if (NodeViewFactory.hasNodeView(m_nnc)) {
            nodeView = NodeViewFactory.createNodeView(m_nnc);
        } else if (NodeViewFactoryInternal.hasNodeView(m_nnc)) {
            nodeView = NodeViewFactoryInternal.createNodeView(m_nnc);
            isInternalView = true;
        }
        if (nodeView == null) {
            throw new IllegalStateException(String
                .format("Failed to open node view. The node '%s' doesn't provide a node view", m_nnc.getNameWithID()));
        }
        Page page = NodeViewUtil.createPage(nodeView, m_nnc.getNodeModel());

        if (isInternalView && !NodeViewUtil.isCompletelyStatic(page)) {
            throw new IllegalStateException("An 'internal' node view must only provide static resources");
        }
        String viewName = m_nnc.getNodeViewName(0);
        boolean isRemoteDebugEnabled = System.getProperty("chromium.remote_debugging_port") != null;
        NodeViewInfo info =
            NodeViewInfo.create(viewName, getUrl(page, nodeView.getClass()), page.getInitData(), isInternalView, isRemoteDebugEnabled);
        try {
            return MAPPER.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize the node view info into json", e);
        }
    }

    @SuppressWarnings("rawtypes")
    private String getUrl(final Page page, final Class<? extends NodeView> nodeViewClass) {
        NodeFactory<NodeModel> factory = m_nnc.getNode().getFactory();
        String debugUrl = NodeViewUtil.getNodeViewDebugUrl(factory.getClass()).orElse(null);
        if (debugUrl == null) {
            try {
                return NodeViewUtil.writeViewResourcesToDiscAndGetFileUrl(page, nodeViewClass, m_nnc);
            } catch (IOException e) {
                throw new IllegalStateException(
                    "The page '" + page.getRelativePath() + "' could not be written to disc", e);
            }
        } else {
            return debugUrl;
        }
    }

}
