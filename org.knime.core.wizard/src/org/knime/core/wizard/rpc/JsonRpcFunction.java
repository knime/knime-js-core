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
package org.knime.core.wizard.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.ui.node.workflow.NativeNodeContainerUI;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcServer;
import org.knime.gateway.json.util.JsonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A (browser) function for 'remote procedure calls' using the json-rpc standard. It's exactly the same browser function
 * that is injected by the web-ui used for jsonrpc calls to the 'gateway API'. By that, the frontend (e.g. the node view
 * framework) doesn't require extra logic.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 *
 * @since 4.5
 */
public class JsonRpcFunction {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        JsonUtil.addIDEntityDeSerializer(MAPPER);
    }

    /**
     * Name to use when, e.g., this function is injected into the browser.
     */
    public static final String FUNCTION_NAME = "jsonrpc";

    private final JsonRpcServer m_jsonRpcServer = new JsonRpcServer();

    /**
     * Initializes the json-rpc function for composite views.
     *
     * @param snc the component with the composite view
     * @param reexecutionService TODO
     * @param isDialog Show the dialog or the composite view of the component
     * @since 4.7
     */
    public JsonRpcFunction(final SubNodeContainer snc, final ReexecutionService reexecutionService,
        final boolean isDialog) {
        m_jsonRpcServer.addService(NodeService.class, new DefaultNodeService(snc, isDialog));
        if (reexecutionService != null) {
            m_jsonRpcServer.addService(ReexecutionService.class, reexecutionService);
        }
    }

    /**
     * Intializes the json-rpc function for single node view.
     *
     * @param nnc
     */
    public JsonRpcFunction(final NativeNodeContainer nnc) {
        m_jsonRpcServer.addService(NodeService.class, new DefaultNodeService(nnc));
    }

    /**
     * Initializes the json-rpc function for a port view.
     *
     * @param nc
     * @param portIdx
     * @param viewIdx
     * @since 5.2
     */
    public JsonRpcFunction(final SingleNodeContainer nc, final int portIdx, final int viewIdx) {
        m_jsonRpcServer.addService(NodeService.class, new DefaultNodeService(nc, portIdx, viewIdx));
    }

    /**
     * Json-rpc functions required for node dialogs used in the context of the remote workflow editor.
     *
     * @param nncUI
     * @since 5.4
     */
    public JsonRpcFunction(final NativeNodeContainerUI nncUI) {
        m_jsonRpcServer.addService(NodeService.class, new RWENodeService(nncUI));
    }

    /**
     * Helper to create a jsonrpc-notification call from a (json-serializable) event to be run as a JS script in the
     * browser.
     *
     * NOTE: usually only used in conjunction with the SWT browser!
     *
     * @param eventName
     * @param event
     * @return the js-call or {@code null} if a problem occurred
     *
     * @since 4.6
     */
    public static String createJsonRpcNotificationCall(final String eventName, final Object event) {
        final var jsonrpcObjectNode = MAPPER.createObjectNode();
        final var paramsArrayNode = jsonrpcObjectNode.arrayNode();
        paramsArrayNode.addPOJO(event);
        jsonrpcObjectNode.put(FUNCTION_NAME, "2.0").put("method", eventName).set("params", paramsArrayNode);
        try {
            return "jsonrpcNotification(\"" + StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(jsonrpcObjectNode))
                + "\");";
        } catch (JsonProcessingException ex) {
            NodeLogger.getLogger(DefaultNodeService.class)
                .error("Problem creating a json-rpc notification in order to send an event", ex);
            return null;
        }
    }

    /**
     * Helper to create a event-message to be sent to the browser.
     *
     * @param name
     * @param payload the event's payload
     * @return the message
     * @throws IllegalStateException if the message couldn't be created
     */
    public static String createEventMessage(final String name, final Object payload) {
        var event = MAPPER.createObjectNode();
        try {
            return MAPPER.writeValueAsString(event.put("eventType", name).set("payload", MAPPER.valueToTree(payload)));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Problem creating the event-message in order to send an event", ex);
        }
    }

    /**
     * Carries out a json-rpc function call.
     *
     * @param jsonRpcRequest
     * @return the json-rpc response
     */
    public String call(final String jsonRpcRequest) {
        try (var request = new ByteArrayInputStream(jsonRpcRequest.getBytes(StandardCharsets.UTF_8));
                var response = new ByteArrayOutputStream()) {
            m_jsonRpcServer.handleRequest(request, response);
            return new String(response.toByteArray(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            // should never happen
            throw new IllegalStateException(e);
        }
    }

    /**
     * Cleans up the rpc function.
     */
    public void dispose() {
        // nothing to dispose
    }

    /**
     * {@link NodeService} implementation as backend for node dialogs in the context of remote workflow editor.
     *
     * @since 5.4
     */
    public static class RWENodeService implements NodeService {

        private final NativeNodeContainerUI m_nncUI;

        RWENodeService(final NativeNodeContainerUI nncUI) {
            m_nncUI = nncUI;
        }

        @Override
        public String callNodeDataService(final String projectId, final String workflowId, final String nodeId,
            final String extensionType, final String serviceType, final String request) {
            return m_nncUI.callNodeDataService(UUID.fromString(projectId), workflowId, nodeId, extensionType,
                serviceType, request);
        }

        @Override
        public void updateDataPointSelection(final String projectId, final String workflowId, final String nodeId,
            final String mode, final List<String> selection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void changeNodeStates(final String projectId, final String workflowId, final List<String> nodeIds,
            final String action) {
            throw new UnsupportedOperationException();
        }

    }
}
