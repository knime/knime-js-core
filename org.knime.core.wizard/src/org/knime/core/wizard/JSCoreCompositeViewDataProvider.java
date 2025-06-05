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
 *   Feb 4, 2025 (hornm): created
 */
package org.knime.core.wizard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.impl.webui.service.CompositeViewDataProvider;

import com.fasterxml.jackson.databind.util.RawValue;

/**
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Tobias Kampmann, TNG
 * @since 5.5
 */
public class JSCoreCompositeViewDataProvider implements CompositeViewDataProvider {

    @Override
    public String getCompositeViewData(final SubNodeContainer snc,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEntity) throws IOException {
        var model = new SubnodeViewableModel(snc, snc.getName());
        model.createPageAndValue(createNodeViewEntity::apply);
        return viewContentToJsonString(model.getViewRepresentation());
    }

    /**
     * Turns a webview content into a json string.
     */
    private static String viewContentToJsonString(final WebViewContent webViewContent) throws IOException {
        // TODO(NXT-3339) :
        // very ugly, but it's done the same way at other places, too
        // WebViewContent should have a 'saveToStream(OutputStream)'-method
        // right now the returning will be json, but what if not anymore?
        return ((ByteArrayOutputStream)webViewContent.saveToStream()).toString("UTF-8");
    }

    @Override
    public PageContainer triggerComponentReexecution(final SubNodeContainer snc, final String resetNodeIdSuffix,
        final Map<String, String> viewValues, final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt)
        throws IOException {
        var model = new SubnodeViewableModel(snc, snc.getName());
        return translatePageContainerType(
            model.createReexecutionService(createNodeViewEnt::apply).reexecutePage(resetNodeIdSuffix, viewValues));
    }

    @Override
    public PageContainer pollComponentReexecutionStatus(final SubNodeContainer snc, final String nodeIdThatTriggered,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt) throws IOException {
        var model = new SubnodeViewableModel(snc, snc.getName());
        return translatePageContainerType(
            model.createReexecutionService(createNodeViewEnt::apply).getPage(nodeIdThatTriggered));
    }

    @Override
    public void setViewValuesAsNewDefault(final SubNodeContainer snc, final Map<String, String> viewValues)
        throws IOException {
        var model = new SubnodeViewableModel(snc, snc.getName());
        var result = model.loadViewValueFromMapAndSetAsDefault(viewValues);
        if (result != null) {
            throw new IOException("Failed to set view values as new default: " + result.getError());
        }
    }

    @Override
    public PageContainer triggerCompleteComponentReexecution(final SubNodeContainer snc,
        final Map<String, String> viewValues, final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt)
        throws IOException {
        var model = new SubnodeViewableModel(snc, snc.getName());
        return translatePageContainerType(
            model.createReexecutionService(createNodeViewEnt::apply).reexecuteCompletePage(snc, viewValues));
    }

    @Override
    public PageContainer pollCompleteComponentReexecutionStatus(final SubNodeContainer snc,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt) throws IOException {
        var model = new SubnodeViewableModel(snc, snc.getName());
        return translatePageContainerType(
            model.createReexecutionService(createNodeViewEnt::apply).getCompletePage(snc));
    }

    @Override
    public void deactivateAllComponentDataServices(final SubNodeContainer snc) throws IOException {

        var model = new SubnodeViewableModel(snc, snc.getName());
        model.discard();

        List<NativeNodeContainer> viewNodes = WizardPageUtil.getWizardPageNodes(snc.getWorkflowManager(), true);

        var nvm = NodeViewManager.getInstance();
        viewNodes.stream().filter(NodeViewManager::hasNodeView)
            .forEach(nnc -> nvm.getDataServiceManager().deactivateDataServices(NodeWrapper.of(nnc)));
    }

    // TODO(NXT-3423): Deduplicate the return type. Currently its duplicated
    private static CompositeViewDataProvider.PageContainer
        translatePageContainerType(final org.knime.core.wizard.rpc.PageContainer pageContainer) {

        return new CompositeViewDataProvider.PageContainer() {
            @Override
            public RawValue getPage() {
                return pageContainer.getPage();
            }

            @Override
            public List<String> getResetNodes() {
                return pageContainer.getResetNodes();
            }

            @Override
            public List<String> getReexecutedNodes() {
                return pageContainer.getReexecutedNodes();
            }
        };
    }
}
