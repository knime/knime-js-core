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
 *   11 Nov 2016 (albrecht): created
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

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.wizard.ViewHideable;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.util.DefaultLayoutCreator;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SinglePageWebResourceController;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WebResourceController;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.js.core.layout.bs.JSONLayoutColumn;
import org.knime.js.core.layout.bs.JSONLayoutContent;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.js.core.layout.bs.JSONLayoutRow;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONNestedLayout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public final class DefaultLayoutCreatorImpl implements DefaultLayoutCreator {

    private final static NodeLogger LOGGER = NodeLogger.getLogger(DefaultLayoutCreatorImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String createDefaultLayout(final Map<NodeIDSuffix, ViewHideable> viewNodes) throws IOException {
        JSONLayoutPage page = createDefaultLayoutStructure(viewNodes);
        ObjectMapper mapper = JSONLayoutPage.getConfiguredObjectMapper();
        try {
            String defaultJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(page);
            return defaultJson;
        } catch (JsonProcessingException e) {
            throw new IOException(e);
        }
    }

    /**
     * Creates a new default layout structure from a given set of {@link WizardNode}s.
     * @param viewNodes a map of view nodes to create the layout for
     * @return the new default layout structure
     */
    public static JSONLayoutPage createDefaultLayoutStructure(final Map<NodeIDSuffix, ViewHideable> viewNodes) {
        JSONLayoutPage page = new JSONLayoutPage();
        List<JSONLayoutRow> rows = new ArrayList<JSONLayoutRow>();
        page.setRows(rows);
        for (NodeIDSuffix suffix : viewNodes.keySet()) {
            ViewHideable viewNode = viewNodes.get(suffix);
            JSONLayoutRow row = createDefaultRowForViewContent(suffix, viewNode);
            rows.add(row);
        }
        return page;
    }

    private static JSONLayoutRow createDefaultRowForViewContent(final NodeIDSuffix suffix, final ViewHideable viewNode) {
        JSONLayoutRow row = new JSONLayoutRow();
        JSONLayoutColumn col = new JSONLayoutColumn();
        JSONLayoutContent colContent;
        if (viewNode instanceof SubNodeContainer) {
            JSONNestedLayout nestedLayout = new JSONNestedLayout();
            NodeID id = NodeID.fromString(suffix.toString());
            nestedLayout.setNodeID(Integer.toString(id.getIndex()));
            colContent = nestedLayout;
        } else {
            colContent = getDefaultViewContentForNode(suffix, viewNode);
        }
        col.setContent(Arrays.asList(new JSONLayoutContent[]{colContent}));
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
     * @param viewNode the node to create the view content for
     * @return a new view content element for a JSON layout
     * @since 3.7
     */
    public static JSONLayoutViewContent getDefaultViewContentForNode(final NodeIDSuffix suffix,
        final ViewHideable viewNode) {
        NodeID id = NodeID.fromString(suffix.toString());
        JSONLayoutViewContent view = new JSONLayoutViewContent();
        if (viewNode instanceof LayoutTemplateProvider) {
            JSONLayoutViewContent layoutViewTemplate = ((LayoutTemplateProvider)viewNode).getLayoutTemplate();
            if (layoutViewTemplate != null) {
                view = layoutViewTemplate;
            }
        }
        view.setNodeID(Integer.toString(id.getIndex()));
        return view;
    }

    /**
     * {@inheritDoc}
     * @since 3.7
     */
    @Override
    public String expandNestedLayout(final String originalLayout, final WorkflowManager wfm) {
        if (originalLayout == null || originalLayout.isEmpty()) {
            return originalLayout;
        }
        try {
            JSONLayoutPage parentPage = deserializeLayout(originalLayout);
            parentPage = expandNestedLayout(parentPage, wfm);
            return serializeLayout(parentPage);
        } catch (IOException ex) {
            LOGGER.error("Could not expand a potentially nested layout: " + ex.getMessage(), ex);
            return originalLayout;
        }
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

    private JSONLayoutPage expandNestedLayout(final JSONLayoutPage originalLayout, final WorkflowManager wfm) throws JsonProcessingException, IOException {
        List<JSONLayoutRow> rows = originalLayout.getRows();
        if (rows != null) {
            for (JSONLayoutRow row : originalLayout.getRows()) {
                expandNestedRow(row, wfm);
            }
        }
        return originalLayout;
    }

    private JSONLayoutRow expandNestedRow(final JSONLayoutRow originalRow, final WorkflowManager wfm) throws JsonProcessingException, IOException {
        for (JSONLayoutColumn col : originalRow.getColumns()) {
            List<JSONLayoutContent> replacedContent = new ArrayList<JSONLayoutContent>(col.getContent().size());
            for (JSONLayoutContent content : col.getContent()) {
                if (content instanceof JSONNestedLayout) {
                    JSONNestedLayout nestedLayout = (JSONNestedLayout)content;
                    NodeIDSuffix suffix = NodeIDSuffix.fromString(nestedLayout.getNodeID());
                    NodeID nodeID = suffix.prependParent(wfm.getID());
                    NodeContainer nodeContainer = null;
                    try {
                        nodeContainer = wfm.getNodeContainer(nodeID);
                    } catch (IllegalArgumentException e) {
                        //node probably deleted from workflow
                        continue;
                    }
                    if (nodeContainer != null && nodeContainer instanceof SubNodeContainer) {
                        expandSubnode(nestedLayout, (SubNodeContainer)nodeContainer);
                    }
                    replacedContent.add(nestedLayout);
                } else if (content instanceof JSONLayoutViewContent) {
                    JSONLayoutViewContent viewContent = (JSONLayoutViewContent)content;
                    NodeIDSuffix suffix = NodeIDSuffix.fromString(viewContent.getNodeID());
                    NodeID nodeID = suffix.prependParent(wfm.getID());
                    NodeContainer nodeContainer = null;
                    try {
                        nodeContainer = wfm.getNodeContainer(nodeID);
                    } catch (IllegalArgumentException e) {
                        //node probably deleted from workflow
                        continue;
                    }
                    if (nodeContainer != null && nodeContainer instanceof SubNodeContainer) {
                        LOGGER.info("Node " + nodeID + " was defined as a view but will be treated as nested layout. "
                            + "Consider updating your layout for node " + wfm.getID());
                        JSONNestedLayout nestedLayout = new JSONNestedLayout();
                        nestedLayout.setNodeID(viewContent.getNodeID());
                        expandSubnode(nestedLayout, (SubNodeContainer)nodeContainer);
                        replacedContent.add(nestedLayout);
                    } else {
                        replacedContent.add(viewContent);
                    }
                } else if (content instanceof JSONLayoutRow) {
                    JSONLayoutRow rowContent = (JSONLayoutRow)content;
                    rowContent = expandNestedRow(rowContent, wfm);
                    replacedContent.add(rowContent);
                } else {
                    // html content
                    replacedContent.add(content);
                }
            }
            col.setContent(replacedContent);
        }
        return originalRow;
    }

    private void expandSubnode(final JSONNestedLayout nestedLayout, final SubNodeContainer sub) throws JsonProcessingException, IOException {
        WorkflowManager wfm = sub.getWorkflowManager();
        String nestedLayoutString = sub.getLayoutJSONString();
        if (StringUtils.isNotEmpty(nestedLayoutString)) {
            JSONLayoutPage nestedPage = deserializeLayout(nestedLayoutString);
            expandNestedLayout(nestedPage, wfm);
            nestedLayout.setLayout(nestedPage);
        } else {
            // create default layout also for nested subnodes, if there is no layout defined
            @SuppressWarnings("rawtypes")
            Map<NodeID, WizardNode> nestedNodes =
                wfm.findNodes(WizardNode.class, WebResourceController.NOT_HIDDEN_FILTER, false);
            if (nestedNodes.size() > 0) {
                Map<NodeIDSuffix, ViewHideable> nestedViews = nestedNodes.entrySet().stream().collect(Collectors
                    .toMap(e -> NodeIDSuffix.create(wfm.getID(), e.getKey()), v -> (ViewHideable)v.getValue()));
                Map<NodeID, SubNodeContainer> nestedSubnodes = WebResourceController.getSubnodeContainers(wfm);
                for (Entry<NodeID, SubNodeContainer> entry : nestedSubnodes.entrySet()) {
                    SinglePageWebResourceController controller =
                        new SinglePageWebResourceController(wfm, entry.getKey());
                    if (controller.isSubnodeViewAvailable()) {
                        nestedViews.put(NodeIDSuffix.create(wfm.getID(), entry.getKey()), entry.getValue());
                    }
                }
                JSONLayoutPage nestedPage = createDefaultLayoutStructure(nestedViews);
                expandNestedLayout(nestedPage, wfm);
                nestedLayout.setLayout(nestedPage);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @since 3.7
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String addUnreferencedViews(final String originalLayout, final Map<NodeIDSuffix, WizardNode> allNodes,
        final Map<NodeIDSuffix, SubNodeContainer> allNestedViews, final NodeID containerID) {
        JSONLayoutPage finalLayout;
        if (originalLayout == null || originalLayout.isEmpty()) {
            finalLayout = new JSONLayoutPage();
            finalLayout.setRows(new ArrayList<JSONLayoutRow>(0));
        } else {
            try {
                finalLayout = deserializeLayout(originalLayout);
            } catch (IOException ex) {
                LOGGER.error("Could not add unreferenced views to a layout: " + ex.getMessage(), ex);
                return originalLayout;
            }
        }
        try {
            finalLayout = addUnreferencedViews(containerID, finalLayout, allNodes, allNestedViews);
            return serializeLayout(finalLayout);
        } catch (Exception ex) {
            LOGGER.error("Could not deserialize amended layout, returning original: " + ex.getMessage(), ex);
        }
        return originalLayout;
    }

    @SuppressWarnings("rawtypes")
    private JSONLayoutPage addUnreferencedViews(final NodeID containerID, final JSONLayoutPage layout,
        final Map<NodeIDSuffix, WizardNode> allNodes, final Map<NodeIDSuffix, SubNodeContainer> allNestedViews) {
        List<NodeIDSuffix> containedNodes = new ArrayList<NodeIDSuffix>();
        layout.getRows().stream().forEach(row -> {
            addNodesFromRow(row, containedNodes);
        });
        Map<NodeIDSuffix, ViewHideable> allViews =
            getAllCurrentSNCViews(containerID, allNodes, allNestedViews);
        Map<NodeIDSuffix, ViewHideable> missingViews = allViews.entrySet().stream()
            .filter(e -> {
                final NodeID nodeID = NodeID.fromString(e.getKey().toString());
                return !containedNodes.contains(NodeIDSuffix.create(containerID, nodeID));
            }).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        missingViews.entrySet().stream().forEach(e -> {
            JSONLayoutRow newRow = createDefaultRowForViewContent(e.getKey(), e.getValue());
            layout.getRows().add(newRow);
            if (e.getValue() instanceof SubNodeContainer) {
                try {
                    expandSubnode((JSONNestedLayout)newRow.getColumns().get(0).getContent().get(0),
                        (SubNodeContainer)e.getValue());
                } catch (IOException ex) {
                    LOGGER.error("Could not expand nested layout: " + ex.getMessage(), ex);
                }
            }
        });
        Map<NodeIDSuffix, JSONNestedLayout> nestedLayouts = new LinkedHashMap<NodeIDSuffix, JSONNestedLayout>();
        layout.getRows().stream().forEach(row -> {
           getNestedLayoutsFromRow(row, nestedLayouts);
        });
        nestedLayouts.entrySet().stream().forEach(e -> {
            JSONLayoutPage nLP = e.getValue().getLayout();
            NodeID sncID = e.getKey().prependParent(containerID);
            SubNodeContainer nestedSNC = allNestedViews.get(NodeIDSuffix.fromString(sncID.toString()));
            NodeID sncContainerID = sncID.createChild(nestedSNC.getWorkflowManager().getID().getIndex());
            addUnreferencedViews(sncContainerID, nLP, allNodes, allNestedViews);
        });
        return layout;
    }

    private void addNodesFromRow(final JSONLayoutRow row, final List<NodeIDSuffix> nodes) {
        row.getColumns().stream().forEach(col -> {
            col.getContent().stream().forEach(content -> {
                if (content instanceof JSONLayoutViewContent) {
                    nodes.add(NodeIDSuffix.fromString(((JSONLayoutViewContent)content).getNodeID()));
                } else if (content instanceof JSONLayoutRow) {
                    addNodesFromRow((JSONLayoutRow)content, nodes);
                } else if (content instanceof JSONNestedLayout) {
                    nodes.add(NodeIDSuffix.fromString(((JSONNestedLayout)content).getNodeID()));
                }
            });
        });
    }

    private void getNestedLayoutsFromRow(final JSONLayoutRow row, final Map<NodeIDSuffix, JSONNestedLayout> layoutMap) {
        row.getColumns().stream().forEach(col -> {
            col.getContent().stream().forEach(content -> {
                if (content instanceof JSONLayoutRow) {
                    getNestedLayoutsFromRow((JSONLayoutRow)content, layoutMap);
                } else if (content instanceof JSONNestedLayout) {
                    JSONNestedLayout nL = (JSONNestedLayout)content;
                    layoutMap.put(NodeIDSuffix.fromString(nL.getNodeID()), nL);
                }
            });
        });
    }

    @SuppressWarnings("rawtypes")
    private static Map<NodeIDSuffix, ViewHideable> getAllCurrentSNCViews(final NodeID containerID,
        final Map<NodeIDSuffix, WizardNode> allNodes, final Map<NodeIDSuffix, SubNodeContainer> allNestedViews) {
        Map<NodeIDSuffix, ViewHideable> allViews = new LinkedHashMap<NodeIDSuffix, ViewHideable>();
        allNodes.entrySet().stream().filter(e -> isInCurrentSNC(containerID, e.getKey()))
            .forEach(e -> allViews.put(e.getKey(), e.getValue()));
        allNestedViews.entrySet().stream().filter(e -> isInCurrentSNC(containerID, e.getKey()))
            .forEach(e -> allViews.put(e.getKey(), e.getValue()));
        return allViews;
    }

    private static boolean isInCurrentSNC(final NodeID containerID, final NodeIDSuffix viewSuffix) {
        NodeID viewID = NodeID.fromString(viewSuffix.toString());
        if (!viewID.hasPrefix(containerID)) {
            return false;
        }
        NodeIDSuffix shortenedViewSuffix = NodeIDSuffix.create(containerID, viewID);
        return shortenedViewSuffix.getSuffixArray().length == 1;
    }

}
