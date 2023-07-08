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
 *   Apr 23, 2021 (hornm): created
 */
package org.knime.core.wizard.rpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.data.rpc.RpcSingleClient;
import org.knime.core.webui.data.rpc.RpcSingleServer;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcSingleServer;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcTestUtil;
import org.knime.core.wizard.SubnodeViewableModel;
import org.knime.core.wizard.WorkflowTestCase;
import org.knime.gateway.api.entity.NodeViewEnt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.RawValue;
import com.fasterxml.jackson.module.mrbean.AbstractTypeMaterializer;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * Essentially tests the {@link DefaultReexecutionService} which is triggered on the re-execution of a composite view
 * (i.e. a view of a component).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultReexecutionServiceTest extends WorkflowTestCase {

    private final static ObjectMapper MAPPER = createObjectMapper();

    /**
     * Tests successful re-execution of a component and re-execution with a validation error.
     *
     * @throws Exception
     */
    @Test
    public void testReexecutePage() throws Exception {
        Function<NativeNodeContainer, NodeViewEnt> nodeViewEntCreator =
            nnc -> NodeViewEnt.create(nnc, () -> List.of("selection 1", "selection 2"));
        ReexecutionService service = createReexecutionService(5, nodeViewEntCreator);

        // test normal re-execution
        Map<String, String> viewValues = Map.of("5:0:7",
            "{\"@class\":\"org.knime.js.base.node.base.input.integer.IntegerNodeValue\",\"integer\":834567}}",//
            "5:0:8", "new initial data for node 5:0:8");
        PageContainer res = service.reexecutePage("5:0:2", viewValues);
        assertThat(res.getPage(), is(nullValue()));
        assertThat(res.getResetNodes(),
            containsInAnyOrder("5:0:3", "5:0:2", "5:0:7", "5:0:8", "5:0:13:0:10", "5:0:13:0:11"));
        assertThat(res.getReexecutedNodes(), is(empty()));
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            PageContainer res2 = service.getPage();
            assertThat(res2.getPage().rawValue().toString(), containsString("834567"));
            assertThat(res2.getResetNodes(), is(nullValue()));
            assertThat(res2.getReexecutedNodes(),
                containsInAnyOrder("5:0:3", "5:0:2", "5:0:7", "5:0:8", "5:0:13:0:10", "5:0:13:0:11"));
        });
        res = service.getPage();
        JsonNode page = MAPPER.readTree(res.getPage().rawValue().toString());
        assertThat(page.get("webNodes").get("5:0:7").get("viewValue").get("integer").asInt(), is(834567));
        assertThat(page.get("nodeViews").get("5:0:8").get("initialData").asText(),
            is("new initial data for node 5:0:8"));
        assertThat(page.get("nodeViews").get("5:0:8").get("initialSelection").get(1).asText(), is("selection 2"));
        assertThat(res.getResetNodes(), is(nullValue()));
        assertThat(res.getReexecutedNodes(), is(nullValue()));

        // test re-execution with validation error
        Map<String, String> viewValues2 = Map.of("5:0:7",
            "{\"@class\":\"org.knime.js.base.node.base.input.integer.IntegerNodeValue\",\"integer\":8345673838383838383838383}}", //
            "5:0:8", "ERROR: a validation error");
        IllegalStateException ex =
            assertThrows(IllegalStateException.class, () -> service.reexecutePage("5:0:2", viewValues2));
        String message = ex.getMessage();
        assertThat(message, containsString("Unable to re-execute component with current page values."));
        assertThat(message, containsString("out of range of int"));
        assertThat(message, containsString("ERROR: a validation error"));
    }

    /**
     * Tests the re-execution with nodes failing to re-execute.
     *
     * @throws Exception
     */
    @Test
    public void testFailureOnReExecution() throws Exception {
        ReexecutionService service = createReexecutionService(6, null);

        // test normal re-execution
        Map<String, String> viewValues = Map.of("6:0:9",
            "{\"@class\":\"org.knime.js.base.node.base.input.bool.BooleanNodeValue\",\"boolean\":false}}",//
            "6:0:8", "n�w initial data for node 6:0:8");
        var res = service.reexecutePage("6:0:9", viewValues);
        assertThat(res.getPage(), is(nullValue()));
        assertThat(res.getResetNodes(), containsInAnyOrder("6:0:9", "6:0:3", "6:0:8"));
        assertThat(res.getReexecutedNodes(), is(empty()));
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            PageContainer res2 = service.getPage();
            assertThat(res2.getResetNodes(), is(nullValue()));
            assertThat(res2.getReexecutedNodes(), containsInAnyOrder("6:0:9", "6:0:3", "6:0:8"));
        });
        res = service.getPage();
        var page = MAPPER.readTree(res.getPage().rawValue().toString());
        assertThat(page.get("webNodes").get("6:0:3").get("viewRepresentation").get("text").asText(), is("TEST"));
        assertThat(page.get("nodeViews").get("6:0:8").get("initialData").asText(),
            is("n�w initial data for node 6:0:8"));
        assertThat(res.getResetNodes(), is(nullValue()));
        assertThat(res.getReexecutedNodes(), is(nullValue()));

        // test failing re-execution
        viewValues = Map.of("6:0:9",
            "{\"@class\":\"org.knime.js.base.node.base.input.bool.BooleanNodeValue\",\"boolean\":true}}",//
            "6:0:8", "new initial data for node 6:0:8");
        res = service.reexecutePage("6:0:9", viewValues);
        assertThat(res.getPage(), is(nullValue()));
        assertThat(res.getResetNodes(), containsInAnyOrder("6:0:9", "6:0:3", "6:0:8"));
        assertThat(res.getReexecutedNodes(), is(empty()));
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            PageContainer res2 = service.getPage();
            assertThat(res2.getPage(), is(not(nullValue())));
            assertThat(res2.getResetNodes(), is(nullValue()));
            assertThat(res2.getReexecutedNodes(), containsInAnyOrder("6:0:9", "6:0:3", "6:0:8"));
        });
        res = service.getPage();
        page = MAPPER.readTree(res.getPage().rawValue().toString());
        assertThat(page.get("webNodes").get("6:0:3").get("nodeInfo").get("nodeState").asText(), is("configured"));
        assertThat(page.get("webNodes").get("6:0:3").get("nodeInfo").get("displayPossible").asText(), is("false"));
        assertThat(page.get("nodeViews").get("6:0:8").get("nodeInfo").get("nodeState").asText(), is("configured"));
        assertThat(res.getResetNodes(), is(nullValue()));
        assertThat(res.getReexecutedNodes(), is(nullValue()));
    }

    private ReexecutionService createReexecutionService(final int pageId,
        final Function<NativeNodeContainer, NodeViewEnt> nodeViewEntCreator) throws Exception {
        NodeID wfId = loadAndSetWorkflow();
        executeAllAndWait();

        // create reexecution service 'client'
        SubNodeContainer component = (SubNodeContainer)getManager().getNodeContainer(new NodeID(wfId, pageId));
        SubnodeViewableModel model = new SubnodeViewableModel(component, "view name blub", false, nodeViewEntCreator);
        RpcSingleServer<ReexecutionService> rpcServer =
            new JsonRpcSingleServer<ReexecutionService>(model.createReexecutionService(nodeViewEntCreator));
        RpcSingleClient<ReexecutionService> rpcClient = JsonRpcTestUtil
            .createRpcSingleClientInstanceForTesting(ReexecutionService.class, rpcServer.getHandler(), MAPPER);
        return rpcClient.getService();
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper
            .registerModule(new MrBeanModule(new AbstractTypeMaterializer(ReexecutionService.class.getClassLoader())));
        SimpleModule mod = new SimpleModule();
        mod.addDeserializer(RawValue.class, new StdDeserializer<RawValue>(RawValue.class) {

            @Override
            public RawValue deserialize(final JsonParser p, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
                return new RawValue(p.readValueAsTree().toString());
            }
        });
        mapper.registerModule(mod);
        return mapper;

    }

}
