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
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.knime.core.node.AbstractNodeView;
import org.knime.core.node.NodeModel;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.webui.node.view.NodeView;

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
        Display display = Display.getDefault();
        m_shell = new Shell(display, SWT.SHELL_TRIM);
        m_shell.setText(title);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        m_shell.setLayout(layout);

        m_browser = new Browser(m_shell, SWT.NONE);
        new JsonRpcBrowserFunction(m_browser, m_nnc);
        new GetNodeViewInfoBrowserFunction(m_browser, m_nnc);

        m_browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        m_shell.setSize(1024, 768);

        Point middle = new Point(knimeWindowBounds.width / 2, knimeWindowBounds.height / 2);
        // Left upper point for window
        Point newLocation = new Point(middle.x - (m_shell.getSize().x / 2) + knimeWindowBounds.x,
            middle.y - (m_shell.getSize().y / 2) + knimeWindowBounds.y);
        m_shell.setLocation(newLocation.x, newLocation.y);
        m_shell.addDisposeListener(e -> callCloseView());
        m_shell.open();

        setUrl();
    }

    private void setUrl() {
        if (m_nnc.getNodeContainerState().isExecuted()) {
            URL url = Platform.getBundle("org.knime.js.cef").getEntry("js-src/ap-wrapper/dist/index.html");
            try {
                String path = FileLocator.toFileURL(url).getPath();
                m_browser.setUrl("file://" + path);
            } catch (IOException e) {
                // should never happen
                throw new IllegalStateException(e);
            }
        } else {
            m_browser.setText(NO_DATA_HTML);
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
