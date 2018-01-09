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
import java.util.List;
import java.util.Map;

import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.util.DefaultLayoutCreator;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.js.core.layout.bs.JSONLayoutColumn;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.js.core.layout.bs.JSONLayoutRow;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public final class DefaultLayoutCreatorImpl implements DefaultLayoutCreator {

    /**
     * {@inheritDoc}
     */
    @Override
    public String createDefaultLayout(@SuppressWarnings("rawtypes") final Map<NodeIDSuffix, WizardNode> viewNodes) throws IOException {
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
    public JSONLayoutPage createDefaultLayoutStructure(@SuppressWarnings("rawtypes") final Map<NodeIDSuffix, WizardNode> viewNodes) {
        JSONLayoutPage page = new JSONLayoutPage();
        List<JSONLayoutRow> rows = new ArrayList<JSONLayoutRow>();
        page.setRows(rows);
        for (NodeIDSuffix suffix : viewNodes.keySet()) {
            JSONLayoutRow row = new JSONLayoutRow();
            JSONLayoutColumn col = new JSONLayoutColumn();
            JSONLayoutViewContent view = getDefaultViewContentForNode(suffix, viewNodes.get(suffix));
            col.setContent(Arrays.asList(new JSONLayoutViewContent[]{view}));
            try {
                col.setWidthMD(12);
            } catch (JsonMappingException e) { /* do nothing */ }
            row.addColumn(col);
            rows.add(row);
        }
        return page;
    }

    /**
     * Creates the view content element for a given node. If the node implements {@link LayoutTemplateProvider}, the
     * template is used, otherwise the generic defaults.
     *
     * @param suffix the node id suffix as used in the layout definition
     * @param viewNode the node to create the view content for
     * @return a new view content element for a JSON layout
     */
    public JSONLayoutViewContent getDefaultViewContentForNode(final NodeIDSuffix suffix, @SuppressWarnings("rawtypes") final WizardNode viewNode) {
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

}
