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
package org.knime.js.cef.nodeview.jsonrpc;

import static org.knime.js.cef.nodeview.GetNodeViewInfoBrowserFunction.MAPPER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.swt.widgets.Display;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcServer;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * A browser function for 'remote procedure calls' using the json-rpc standard. It's exactly the same browser function
 * that is injected by the web-ui used for jsonrpc calls to the 'gateway API'. By that, the frontend (e.g. the node view
 * framework) doesn't require extra logic.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JsonRpcBrowserFunction extends BrowserFunction {

    private static final String FUNCTION_NAME = "jsonrpc";

    private final JsonRpcServer m_jsonRpcServer;

    /**
     * @param browser
     * @param nnc
     */
    public JsonRpcBrowserFunction(final Browser browser, final NativeNodeContainer nnc) {
        super(browser, FUNCTION_NAME);
        m_jsonRpcServer = new JsonRpcServer();

        m_jsonRpcServer.addService(NodeService.class, new DefaultNodeService(nnc, selectionEvent -> { // NOSONAR
            // code copied from org.knime.ui.java.browser.KnimeBrowserView
            final var jsonrpcObjectNode = MAPPER.createObjectNode();
            final var paramsArrayNode = jsonrpcObjectNode.arrayNode();
            paramsArrayNode.addPOJO(selectionEvent);
            jsonrpcObjectNode.put(FUNCTION_NAME, "2.0").put("method", "SelectionEvent").set("params", paramsArrayNode);
            try {
                final String jsCode = "jsonrpcNotification(\""
                    + StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(jsonrpcObjectNode)) + "\");";
                Display.getDefault().syncExec(() -> browser.execute(jsCode));
            } catch (JsonProcessingException ex) {
                NodeLogger.getLogger(DefaultNodeService.class)
                    .error("Problem creating a json-rpc notification in order to send an event", ex);
            }
        }));
    }

    @Override
    public Object function(final Object[] args) {
        try (ByteArrayInputStream request =
            new ByteArrayInputStream(((String)args[0]).getBytes(StandardCharsets.UTF_8));
                ByteArrayOutputStream response = new ByteArrayOutputStream()) {
            m_jsonRpcServer.handleRequest(request, response);
            return new String(response.toByteArray(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            // should never happen
            throw new IllegalStateException(e);
        }
    }
}
