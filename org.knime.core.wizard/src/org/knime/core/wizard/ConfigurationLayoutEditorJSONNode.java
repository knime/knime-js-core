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
 *   Nov 10, 2020 (bogenrieder): created
 */
package org.knime.core.wizard;

import org.apache.commons.lang3.StringEscapeUtils;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutConfigurationContent;
import org.knime.js.core.layout.bs.JSONLayoutContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A JSON representation of a Javascript view node for use in the configuration sorter editor.
 *
 * @author Daniel Bogenrieder, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
public class ConfigurationLayoutEditorJSONNode {

    private String m_nodeID;

    private String m_name;

    private String m_description;

    private JSONLayoutContent m_layout;

    private boolean m_availableInDialog;

    private String m_preview;

    private String m_type;

    /**
     * @param nodeID node id
     * @param name name of the node
     * @param description custom description of the node
     * @param layout the node's layout, if it is not a {@link LayoutTemplateProvider} then a default layout
     * @param icon the url to the node's icon
     * @param availableInDialog if the node is displayed
     * @param type the node type (view, configuration, quickform, or nestedLayout)
     */
    public ConfigurationLayoutEditorJSONNode(final int nodeID, final String name, final String description,
        final JSONLayoutContent layout, final boolean availableInDialog, final String type) {
        m_nodeID = Integer.toString(nodeID);
        // it doesn't seem to be possible to escape the apostrophe for the resulting script to be executed correctly
        // replacing it with a 'right single quotation mark' keeps the visual appearance
        m_name = StringEscapeUtils.escapeJava(name.replace("'", "’"));
        m_description = StringEscapeUtils.escapeJava(description.replace("'", "’"));
        m_availableInDialog = availableInDialog;
        m_layout = layout;
        m_type = type;
    }

    /**
     * @return the node ID
     */
    public String getNodeID() {
        return m_nodeID;
    }

    /**
     * @param nodeID the node ID to set
     */
    public void setNodeID(final String nodeID) {
        m_nodeID = nodeID;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     *
     * @param name the name to set
     */
    public void setName(final String name) {
        m_name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    /**
     *
     * @param description the description to set
     */
    public void setDescription(final String description) {
        m_description = description;
    }

    /**
     * @return the layout
     */
    public JSONLayoutContent getLayout() {
        return m_layout;
    }

    /**
     *
     * @param layout the layout to set
     */
    public void setLayout(final JSONLayoutConfigurationContent layout) {
        m_layout = layout;
    }

    /**
     * @return the availableInDialog
     */
    public boolean getAvailableInDialog() {
        return m_availableInDialog;
    }

    /**
     *
     * @param availableInDialog the availableInDialog to set
     */
    public void setAvailableInDialog(final boolean availableInDialog) {
        m_availableInDialog = availableInDialog;
    }

    /**
     * @return the preview
     */
    public String getPreview() {
        return m_preview;
    }

    /**
     *
     * @param preview the preview to set
     */
    public void setPreview(final String preview) {
        m_preview = preview;
    }

    /**
     * @return the node type (view, quickform, configuration or nestedLayout)
     */
    public String getType() {
        return m_type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        m_type = type;
    }

}
