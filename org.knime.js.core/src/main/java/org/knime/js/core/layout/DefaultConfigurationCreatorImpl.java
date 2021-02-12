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
 *   9 Nov 2020 (bogenrieder): created
 */
package org.knime.js.core.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.util.DefaultConfigurationLayoutCreator;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SubnodeContainerConfigurationStringProvider;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.js.core.layout.bs.JSONLayoutColumn;
import org.knime.js.core.layout.bs.JSONLayoutConfigurationContent;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.js.core.layout.bs.JSONLayoutRow;
import org.knime.js.core.layout.bs.JSONNestedLayout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 *
 * @author Daniel Bogenrieder, KNIME.com GmbH, Konstanz, Germany
 * @since 4.3
 */
public final class DefaultConfigurationCreatorImpl implements DefaultConfigurationLayoutCreator {

    private final static NodeLogger LOGGER = NodeLogger.getLogger(DefaultConfigurationCreatorImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String createDefaultConfigurationLayout(
        @SuppressWarnings("rawtypes") final Map<NodeIDSuffix, DialogNode> configurationNodes) throws IOException {
        JSONLayoutPage page = createDefaultConfigurationLayoutStructure(configurationNodes);
        ObjectMapper mapper = JSONLayoutPage.getConfiguredObjectMapper();
        try {
            String defaultJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(page);
            return defaultJson;
        } catch (JsonProcessingException e) {
            throw new IOException(e);
        }
    }

    /**
     * Creates a new default configuration layout structure from a given set of {@link DialogNode}s.
     * @param dialogNodes a map of view nodes to create the layout for
     * @return the new default configuration layout structure
     */
    public static JSONLayoutPage createDefaultConfigurationLayoutStructure(
        @SuppressWarnings("rawtypes") final Map<NodeIDSuffix, DialogNode> dialogNodes) {
        JSONLayoutPage page = new JSONLayoutPage();
        List<JSONLayoutRow> rows = new ArrayList<JSONLayoutRow>();
        page.setRows(rows);
        for (NodeIDSuffix suffix : dialogNodes.keySet()) {
            @SuppressWarnings("rawtypes")
            DialogNode viewNode = dialogNodes.get(suffix);
            JSONLayoutRow row = createDefaultRowForDialogContent(suffix, viewNode);
            rows.add(row);
        }
        return page;
    }

    private static JSONLayoutRow createDefaultRowForDialogContent(final NodeIDSuffix suffix,
        @SuppressWarnings("rawtypes") final DialogNode dialogNode) {
        JSONLayoutRow row = new JSONLayoutRow();
        JSONLayoutColumn col = new JSONLayoutColumn();
        JSONLayoutConfigurationContent colContent;

        colContent = getDefaultConfigurationContentForNode(suffix, dialogNode);
        col.setContent(Arrays.asList(new JSONLayoutConfigurationContent[]{colContent}));
        try {
            col.setWidthXS(12);
        } catch (JsonMappingException e) {
            /* do nothing */ }
        row.addColumn(col);
        return row;
    }

    /**
     * Creates the view content element for a given node. If the node implements {@link LayoutTemplateProvider}, the
     * template is used, otherwise the generic defaults.
     *
     * @param suffix the node id suffix as used in the layout definition
     * @param dialogNode the node to create the view content for
     * @return a new view content element for a JSON layout
     */
    public static JSONLayoutConfigurationContent getDefaultConfigurationContentForNode(final NodeIDSuffix suffix,
        @SuppressWarnings("rawtypes") final DialogNode dialogNode) {
        NodeID id = NodeID.fromString(suffix.toString());
        JSONLayoutConfigurationContent view = new JSONLayoutConfigurationContent();
        if (dialogNode instanceof ConfigurationLayoutTemplateProvider) {
            JSONLayoutConfigurationContent layoutViewTemplate =
                ((ConfigurationLayoutTemplateProvider)dialogNode).getLayoutTemplate();
            if (layoutViewTemplate != null) {
                view = layoutViewTemplate;
            }
        }
        view.setNodeID(Integer.toString(id.getIndex()));
        return view;
    }

    private static JSONLayoutPage deserializeLayout(final String layout) throws JsonProcessingException, IOException {
        JSONLayoutPage page = new JSONLayoutPage();
        ObjectMapper mapper = JSONLayoutPage.getConfiguredObjectMapper();
        ObjectReader reader = mapper.readerForUpdating(page);
        reader.readValue(layout);
        return page;
    }

    private static String serializeLayout(final JSONLayoutPage layout) throws JsonProcessingException {
        ObjectMapper mapper = JSONLayoutPage.getConfiguredVerboseObjectMapper();
        return mapper.writeValueAsString(layout);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void addUnreferencedDialogNodes(
        final SubnodeContainerConfigurationStringProvider configurationStringProvider,
        final Map<NodeIDSuffix, DialogNode> allNodes) {
        JSONLayoutPage finalLayout;
        if (configurationStringProvider.isEmptyLayout()) {
            finalLayout = new JSONLayoutPage();
            finalLayout.setRows(new ArrayList<JSONLayoutRow>(0));
        } else {
            try {
                finalLayout = deserializeLayout(configurationStringProvider.getConfigurationLayoutString());
            } catch (IOException ex) {
                LOGGER.error("Could not add unreferenced views to a layout: " + ex.getMessage(), ex);
                return;
            }
        }
        try {
            finalLayout = addUnreferencedDialogNodes(finalLayout, allNodes);
            configurationStringProvider.setConfigurationLayoutString(serializeLayout(finalLayout));
        } catch (Exception ex) {
            LOGGER.error("Could not deserialize amended layout, returning original: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings({"java:S3740", "rawtypes"}) // DialogNode generics
    @Override
    public List<Integer> getConfigurationOrder(
        final SubnodeContainerConfigurationStringProvider configurationStringProvider,
        final Map<NodeID, DialogNode> nodes, final WorkflowManager wfm) {
        LinkedHashMap<NodeIDSuffix, DialogNode> resultMap = new LinkedHashMap<>();
        for (Map.Entry<NodeID, DialogNode> entry : nodes.entrySet()) {
            NodeID.NodeIDSuffix idSuffix = NodeID.NodeIDSuffix.create(wfm.getID(), entry.getKey());
            resultMap.put(idSuffix, entry.getValue());
        }
        List<Integer> order = new ArrayList<>();
        if (configurationStringProvider != null) {
            String configurationLayoutString = configurationStringProvider.getConfigurationLayoutString();
            final ObjectMapper mapper = JSONLayoutPage.getConfiguredObjectMapper();
            final SimpleModule module = new SimpleModule();
            mapper.registerModule(module);
            try {
                JSONLayoutPage page = mapper.readValue(configurationLayoutString, JSONLayoutPage.class);
                List<JSONLayoutRow> rows = page.getRows();
                for (JSONLayoutRow row : rows) {
                    Integer id = Integer.valueOf(
                        ((JSONLayoutConfigurationContent)row.getColumns().get(0).getContent().get(0)).getNodeID());
                    order.add(id);
                }
            } catch (JsonProcessingException ex) {
                LOGGER.warn("Unable to parse JSON describing layout of configuration nodes, using default", ex);
            }
        }
        return order;
    }

    @SuppressWarnings({"java:S3740", "rawtypes"}) // DialogNode generics
    private static JSONLayoutPage addUnreferencedDialogNodes(final JSONLayoutPage layout,
        final Map<NodeIDSuffix, DialogNode> allNodes) {
        List<NodeIDSuffix> containedNodes = new ArrayList<>();
        layout.getRows().stream().forEach(row -> addNodesFromRow(row, containedNodes));
        Map<NodeIDSuffix, DialogNode> allDialogs = getAllCurrentSNCDialogs(allNodes);
        Map<NodeIDSuffix, DialogNode> missingViews = allDialogs.entrySet().stream()
                .filter(e -> {
                    final NodeID nodeID = NodeID.fromString(e.getKey().toString());
                    return !containedNodes.contains(NodeIDSuffix.fromString(String.valueOf(nodeID.getIndex())));
                }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            missingViews.entrySet().stream().forEach(e -> {
                JSONLayoutRow newRow = createDefaultRowForDialogContent(e.getKey(), e.getValue());
                layout.getRows().add(newRow);
            });
        return layout;
    }

    private static void addNodesFromRow(final JSONLayoutRow row, final List<NodeIDSuffix> nodes) {
        row.getColumns().stream().forEach(col -> col.getContent().stream().forEach(content -> {
            if (content instanceof JSONLayoutConfigurationContent) {
                nodes.add(NodeIDSuffix.fromString(((JSONLayoutConfigurationContent)content).getNodeID()));
            } else if (content instanceof JSONNestedLayout) {
                nodes.add(NodeIDSuffix.fromString(((JSONNestedLayout)content).getNodeID()));
            }
        }));
    }

    @SuppressWarnings("rawtypes")
    private static Map<NodeIDSuffix, DialogNode> getAllCurrentSNCDialogs(final Map<NodeIDSuffix, DialogNode> allNodes) {
        Map<NodeIDSuffix, DialogNode> allDialogs = new LinkedHashMap<NodeIDSuffix, DialogNode>();
        allNodes.entrySet().stream().forEach(e -> allDialogs.put(e.getKey(), e.getValue()));
        return allDialogs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void
        updateConfigurationLayout(final SubnodeContainerConfigurationStringProvider configurationStringProvider) {
        JSONLayoutPage finalLayout;
        try {
            finalLayout = deserializeLayout(configurationStringProvider.getConfigurationLayoutString());
        } catch (IOException ex) {
            LOGGER.error("Could not update layout: " + ex.getMessage(), ex);
            return;
        }
        try {
            configurationStringProvider.setConfigurationLayoutString(serializeLayout(finalLayout));
        } catch (Exception ex) {
            LOGGER.error("Could not deserialize updated layout, returning original: " + ex.getMessage(), ex);
        }
    }
}
