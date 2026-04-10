/*
 * ------------------------------------------------------------------------
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
 * Created on 16.09.2013 by Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
package org.knime.js.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.gateway.api.entity.UIExtensionEnt;
import org.knime.js.core.layout.bs.JSONLayoutColumn;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.js.core.layout.bs.JSONLayoutRow;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONWebNodePage extends JSONViewContent {

    /**
     * Node ID used when a page contains a single node view (as opposed to a composite/wizard page with multiple nodes).
     *
     * @since 5.9
     */
    public static final String SINGLE_NODE_ID = "SINGLE";

    private final String m_version;
    private JSONWebNodePageConfiguration m_configuration;
    private Map<String, JSONWebNode> m_webNodes;
    private Map<String, UIExtensionEnt> m_nodeViews;

    /**
     * Factory method creating a {@link JSONWebNodePage} from a map of {@link UIExtensionEnt} node view entities with a
     * generated layout. If both {@code viewId} and {@code dialogNodeId} are non-null, the layout uses a split view
     * (dialog occupying 3 columns, view occupying 9 columns). Pass {@code null} for either ID to omit that pane.
     *
     * @param viewId the node ID to place in the view pane, or {@code null} if there is no view
     * @param dialogNodeId the node ID to place in the dialog pane, or {@code null} if there is no dialog
     * @param ents map from node ID to {@link UIExtensionEnt}
     * @return a new {@link JSONWebNodePage}
     *
     * @since 5.9
     */
    public static JSONWebNodePage create(final String viewId, final String dialogNodeId,
        final Map<String, UIExtensionEnt> ents) {
        var layoutPage = createJSONLayoutPage(viewId, dialogNodeId);
        var webNodePageConfig = new JSONWebNodePageConfiguration(layoutPage, null, null, null);
        return new JSONWebNodePage(webNodePageConfig, Collections.emptyMap(), ents);
    }

    /**
     * @return the resulting {@link JSONLayoutPage}
     */
    private static JSONLayoutPage createJSONLayoutPage(final String viewId, final String dialogNodeId) {
        JSONLayoutColumn viewColumn = null;
        JSONLayoutColumn dialogColumn = null;
        String id;
        if ((id = viewId) != null) {
            viewColumn = new JSONLayoutColumn();
            var content = new JSONLayoutViewContent();
            content.setUseLegacyMode(false);
            content.setNodeID(id);
            viewColumn.setContent(List.of(content));
        }
        if ((id = dialogNodeId) != null) {
            dialogColumn = new JSONLayoutColumn();
            var content = new JSONLayoutViewContent();
            content.setUseLegacyMode(false);
            content.setNodeID(id);
            dialogColumn.setContent(List.of(content));
            if (viewColumn != null) {
                try { // update layout for split view
                    dialogColumn.setWidthXS(3);
                    viewColumn.setWidthXS(9);
                } catch (IOException e) {
                    // Do nothing as JSONMappingException is never thrown.
                }
            }
        }

        var row = new JSONLayoutRow();
        if (viewColumn != null) {
            row.addColumn(viewColumn);
        }
        if (dialogColumn != null) {
            row.addColumn(dialogColumn);
        }
        List<JSONLayoutRow> layoutRowList = new ArrayList<>();
        layoutRowList.add(row);

        var layoutPage = new JSONLayoutPage();
        layoutPage.setParentLayoutLegacyMode(false);
        layoutPage.setRows(layoutRowList);
        return layoutPage;
    }

    /**
     * @param configuration
     * @param webNodes
     * @param nodeViews
     *
     * @since 4.5
     */
    public JSONWebNodePage(final JSONWebNodePageConfiguration configuration, final Map<String, JSONWebNode> webNodes,
        final Map<String, UIExtensionEnt> nodeViews) {
        this(configuration, webNodes, nodeViews, KNIMEConstants.VERSION);
    }

    @JsonCreator
    private JSONWebNodePage(@JsonProperty("webNodePageConfiguration") final JSONWebNodePageConfiguration configuration,
        @JsonProperty("webNodes") final Map<String, JSONWebNode> webNodes, final Map<String, UIExtensionEnt> nodeViews,
        @JsonProperty("version") final String version) {
        m_version = version;
        m_configuration = configuration;
        m_webNodes = webNodes;
        m_nodeViews = nodeViews;
    }

    /**
     * @return the version
     */
    @JsonProperty("version")
    public String getVersion() {
        return m_version;
    }

    /**
     * @return the configuration
     */
    @JsonProperty("webNodePageConfiguration")
    public JSONWebNodePageConfiguration getWebNodePageConfiguration() {
        return m_configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    @JsonProperty("webNodePageConfiguration")
    public void setWebNodePageConfiguration(final JSONWebNodePageConfiguration configuration) {
        m_configuration = configuration;
    }

    /**
     * @return the webNodes
     */
    @JsonProperty("webNodes")
    public Map<String, JSONWebNode> getWebNodes() {
        return m_webNodes;
    }

    /**
     * @param webNodes the webNodes to set
     */
    @JsonProperty("webNodes")
    public void setWebNodes(final Map<String, JSONWebNode> webNodes) {
        m_webNodes = webNodes;
    }

    /**
     * @return the nodeViews
     *
     * @since 4.5
     */
    @JsonProperty("nodeViews")
    public Map<String, UIExtensionEnt> getNodeViews() {
        return m_nodeViews;
    }

    /**
     * @param nodeViews the nodeViews to set
     *
     * @since 4.5
     */
    @JsonProperty("nodeViews")
    public void setNodeViews(final Map<String, UIExtensionEnt> nodeViews) {
        m_nodeViews = nodeViews;
    }

    /**
     * Filter the included web nodes in the JSON page to only include specified node IDs. Useful if
     * only part of the page has been updated and reducing the size of the serialized page string is
     * desired.
     *
     * @param includedNodeIds
     * @since 4.4
     */
    public void filterWebNodesById(final List<String> includedNodeIds) {
        if (m_webNodes != null) {
            Iterator<Entry<String, JSONWebNode>> webNodeIterator = m_webNodes.entrySet().iterator();
            while (webNodeIterator.hasNext()) {
                Entry<String, JSONWebNode> nodeEntry = webNodeIterator.next();
                if (!includedNodeIds.contains(nodeEntry.getKey())) {
                    webNodeIterator.remove();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) { /* not needed so far */ }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException { /* not needed so far */ }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        JSONWebNodePage other = (JSONWebNodePage)obj;
        return new EqualsBuilder()
                .append(m_version, other.m_version)
                .append(m_configuration, other.m_configuration)
                .append(m_webNodes, other.m_webNodes)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_version)
                .append(m_configuration)
                .append(m_webNodes)
                .toHashCode();
    }

    /**
     * Serializes this page to a JSON string with escaped backslashes and single quotes, suitable for embedding in a
     * JavaScript string literal.
     *
     * @return the JSON representation of this page, or {@code null} if serialization fails
     * @since 5.9
     */
    public String toJsonString() {
        try (@SuppressWarnings("resource")
        var stream = (ByteArrayOutputStream)saveToStream()) {
            return stream.toString(StandardCharsets.UTF_8).replace("\\", "\\\\").replace("'", "\\'");
        } catch (IOException e) {
            NodeLogger.getLogger(getClass()).error("Failed to serialize JSONWebNodePage to JSON string", e);
            return null;
        }
    }
}
