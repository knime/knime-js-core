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
 *   May 29, 2020 (hornm): created
 */
package org.knime.js.cef.wizardnodeview;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.js.cef.DebugInfo;
import org.knime.js.cef.nodeview.GetDebugInfoBrowserFunction;
import org.knime.js.cef.nodeview.OpenBrowserBrowserFunction;
import org.knime.js.cef.nodeview.ReloadCEFWindowBrowserFunction;
import org.knime.js.swt.wizardnodeview.WizardNodeView;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

/**
 * Wizard node view implementation using the Chromium Embedded Framework (CEF) as browser.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @param <T>
 * @param <REP>
 * @param <VAL>
 */
public class CEFWizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
    extends WizardNodeView<T, REP, VAL> {

    private Browser m_browser;

    /**
     * @param snc
     * @param nodeModel
     */
    public CEFWizardNodeView(final SingleNodeContainer snc, final T nodeModel) {
        super(snc, nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<BrowserFunctionWrapper>
        registerAndGetAdditionalBrowserFunctions(final BrowserWrapper browserWrapper) {
        return Stream.of( //
            new GetDebugInfoBrowserFunction(m_browser, new DebugInfo(true)), //
            new OpenBrowserBrowserFunction(m_browser), //
            new ReloadCEFWindowBrowserFunction(m_browser, this::reloadBrowserContent))
            .map(fct -> browserWrapper.registerBrowserFunction(fct.getName(), fct::function))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BrowserWrapper createBrowserWrapper(final Shell shell) {
        m_browser = new Browser(shell, SWT.NONE);
        m_browser.setMenu(new Menu(m_browser.getShell()));
        return new BrowserWrapper() {

            @Override
            public void execute(final String call) {
                m_browser.execute(call);
            }

            @Override
            public Display getDisplay() {
                return m_browser.getDisplay();
            }

            @Override
            public void addProgressListener(final ProgressListener progressListener) {
                m_browser.addProgressListener(progressListener);
            }

            @Override
            public void removeProgressListener(final ProgressListener progressListener) {
                m_browser.removeProgressListener(progressListener);
            }

            @Override
            public void addLocationListener(final LocationListener locationListener) {
                m_browser.addLocationListener(locationListener);
            }

            @Override
            public void removeLocationListener(final LocationListener locationListener) {
                m_browser.removeLocationListener(locationListener);
            }

            @Override
            public void setUrl(final String absolutePath) {
                m_browser.setUrl(absolutePath);
            }

            @Override
            public void setText(final String html) {
                m_browser.setText(html);
            }

            @Override
            public Shell getShell() {
                return m_browser.getShell();
            }

            @Override
            public String evaluate(final String evalCode) {
                return (String)m_browser.evaluate(evalCode);
            }

            @Override
            public boolean isDisposed() {
                return m_browser.isDisposed();
            }

            @Override
            public void setText(final String html, final boolean trusted) {
                m_browser.setText(html, trusted);
            }

            @Override
            public void setLayoutData(final GridData gridData) {
                m_browser.setLayoutData(gridData);
            }

            @Override
            public BrowserFunctionWrapper registerBrowserFunction(final String name,
                final Function<Object[], Object> func) {
                final BrowserFunction fct = new BrowserFunction(m_browser, name) {
                    @Override
                    public Object function(final Object[] args) {
                        return func.apply(args);
                    }
                };
                return new BrowserFunctionWrapper() {

                    @Override
                    public boolean isDisposed() {
                        return fct.isDisposed();
                    }

                    @Override
                    public void dispose() {
                        fct.dispose();
                    }
                };
            }

        };
    }

}
