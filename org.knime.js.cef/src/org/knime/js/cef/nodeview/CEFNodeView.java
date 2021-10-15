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
 *   Aug 24, 2021 (hornm): created
 */
package org.knime.js.cef.nodeview;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.knime.core.node.AbstractNodeView;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.util.FileUtil;
import org.knime.core.webui.node.view.NodeView;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.wizard.debug.DebugInfo;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.js.core.JSONWebNodePage;
import org.knime.js.core.JSONWebNodePageConfiguration;
import org.knime.js.core.layout.bs.JSONLayoutColumn;
import org.knime.js.core.layout.bs.JSONLayoutContent;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.js.core.layout.bs.JSONLayoutRow;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;

import com.equo.chromium.swt.Browser;

/**
 * A node view implementation using the Chromium Embedded Framework-Browser.
 *
 * Only displays web-ui node views, i.e. {@link NodeView}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class CEFNodeView extends AbstractNodeView<NodeModel> {

    private static final String NO_DATA_HTML = "<html><head></head><body><p>No data to display</p></body></html>";

    private Browser m_browser;

    private Shell m_shell;

    private NativeNodeContainer m_nnc;

    private static String htmlDocumentWithPageBuilderURL;

    private String m_title;

    /**
     * @param nnc
     */
    public CEFNodeView(final NativeNodeContainer nnc) {
        super(nnc.getNodeModel());
        m_nnc = nnc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        if (m_browser != null) {
            Display.getDefault().asyncExec(this::setUrl);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void callOpenView(final String title) {
        callOpenView(title, null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    protected void callOpenView(final String title, final Rectangle knimeWindowBounds) {
        m_title = title;
        var display = Display.getDefault();
        m_shell = new Shell(display, SWT.SHELL_TRIM);
        m_shell.setText(title);

        var layout = new GridLayout();
        layout.numColumns = 1;
        m_shell.setLayout(layout);

        m_browser = new Browser(m_shell, SWT.NONE);
        new JsonRpcBrowserFunction(m_browser, m_nnc);
        new GetDebugInfoBrowserFunction(m_browser, NodeViewManager.getInstance().getNodeView(m_nnc).getPage());

        m_browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        m_shell.setSize(1024, 768);

        var middle = new Point(knimeWindowBounds.width / 2, knimeWindowBounds.height / 2);
        // Left upper point for window
        var newLocation = new Point(middle.x - (m_shell.getSize().x / 2) + knimeWindowBounds.x,
            middle.y - (m_shell.getSize().y / 2) + knimeWindowBounds.y);
        m_shell.setLocation(newLocation.x, newLocation.y);
        m_shell.addDisposeListener(e -> callCloseView());
        m_shell.open();

        m_browser.addProgressListener(new ProgressListener() {

            @Override
            public void completed(final ProgressEvent event) {
                try {
                    initializePageBuilder(m_browser, m_nnc);
                } catch (Exception e) { // NOSONAR
                    var message = "Initialization of the node view failed";
                    NodeLogger.getLogger(CEFNodeView.class).error(message, e);
                    m_browser.execute("window.alert('" + message + ": " + e.getMessage() + "')");
                }
            }

            @Override
            public void changed(final ProgressEvent event) {
                // do nothing
            }
        });

        setUrl();
    }

    private static void initializePageBuilder(final Browser browser, final NativeNodeContainer nnc) throws IOException {
        var nodeViewEnt = new NodeViewEnt(nnc);
        var page = createJSONWebNodePage(nodeViewEnt);
        String pageString;
        try (@SuppressWarnings("resource")
        var stream = (ByteArrayOutputStream)page.saveToStream()) {
            pageString = stream.toString(StandardCharsets.UTF_8).replace("\\", "\\\\").replace("'", "\\'");
        }
        var initCall = "var parsedRepresentation = JSON.parse('" + pageString + "');"
            + "window.KnimePageLoader.init(parsedRepresentation, null, null, false);";
        browser.execute(initCall);
    }

    private static JSONWebNodePage createJSONWebNodePage(final NodeViewEnt nodeViewEnt) {
        var singleNodeId = "SINGLE";
        var layoutViewContent = new JSONLayoutViewContent();
        layoutViewContent.setUseLegacyMode(false);
        layoutViewContent.setNodeID(singleNodeId);
        List<JSONLayoutContent> layoutContentList = new ArrayList<>();
        layoutContentList.add(layoutViewContent);

        var layoutColumn = new JSONLayoutColumn();
        layoutColumn.setContent(layoutContentList);

        var row = new JSONLayoutRow();
        row.addColumn(layoutColumn);
        List<JSONLayoutRow> layoutRowList = new ArrayList<>();
        layoutRowList.add(row);

        var layoutPage = new JSONLayoutPage();
        layoutPage.setParentLayoutLegacyMode(false);
        layoutPage.setRows(layoutRowList);

        var webNodePageConfig = new JSONWebNodePageConfiguration(layoutPage, null, null, null);

        Map<String, NodeViewEnt> nodeViewMap = Map.of(singleNodeId, nodeViewEnt);
        return new JSONWebNodePage(webNodePageConfig, Collections.emptyMap(), nodeViewMap);
    }

    private void setUrl() {
        if (m_nnc.getNodeContainerState().isExecuted()) {
            if (htmlDocumentWithPageBuilderURL == null) {
                writeHtmlDocumentWithPageBuilder(m_title);
            }
            m_browser.setUrl(htmlDocumentWithPageBuilderURL);
        } else {
            m_browser.setText(NO_DATA_HTML);
        }
    }

    private static void writeHtmlDocumentWithPageBuilder(final String title) {
        var sb = new StringBuilder();
        sb.append("<!doctype html><html lang=\"en-US\"><head>");
        sb.append("<meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">");
        sb.append("<meta charset=\"UTF-8\">");
        if (title != null) {
            sb.append("<title>" + title + "</title>");
        }
        sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
        sb.append(
            "<script type=\"text/javascript\" src=\"org/knime/core/knime-pagebuilder2-ap.js\" charset=\"UTF-8\"></script>");
        sb.append("</head><body>");
        sb.append("</body></html>");

        try {
            var dir = FileUtil.createTempDir("ui_extensions_container_").toPath();
            var indexHtml = dir.resolve("index.html");
            Files.writeString(indexHtml, sb.toString());
            copyPageBuilderResources(dir);
            htmlDocumentWithPageBuilderURL = "file://" + indexHtml.toString();
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Problem writing the pagebuilder resources", e);
        }
    }

    private static void copyPageBuilderResources(final Path destDir) throws IOException, URISyntaxException {
        var from = new String[]{ //
            "dist/knime-pagebuilder2-ap.js", //
            "dist/knime-pagebuilder.umd.min.js", //
            "dist/knime-pagebuilder2-ap.js.map", //
            "dist/knime-pagebuilder.umd.min.js.map" //
        };
        var to = new String[]{ //
            "org/knime/core/knime-pagebuilder2-ap.js", //
            "org/knime/core/knime-pagebuilder2.js", //
            "org/knime/core/knime-pagebuilder2-ap.js.map", //
            "org/knime/core/knime-pagebuilder.umd.min.js.map" //
        };
        var bundle = Platform.getBundle("org.knime.js.pagebuilder");
        for (var i = 0; i < to.length; i++) {
            if (DebugInfo.REMOTE_DEBUGGING_PORT == null && to[i].endsWith(".map")) {
                // skip source maps if remote debugging is disabled
                continue;
            }
            var url = bundle.getEntry(from[i]);
            var destFile = destDir.resolve(to[i]);
            Files.createDirectories(destFile.getParent());
            Files.copy(Paths.get(FileLocator.toFileURL(url).toURI()), destFile);
        }
    }

    @Override
    protected void callCloseView() {
        if (m_browser != null) {
            // this also disposes the registered browser functions
            m_browser.dispose();
            m_browser = null;
        }
        if (m_shell != null) {
            m_shell.dispose();
            m_shell = null;
        }
    }

}
