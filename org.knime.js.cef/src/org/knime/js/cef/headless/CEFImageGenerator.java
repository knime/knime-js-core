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
 *   Mar 1, 2021 (Nicolas Sebey): created
 */
package org.knime.js.cef.headless;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.chromium.Browser;
import org.eclipse.swt.chromium.IBrowser;
import org.eclipse.swt.widgets.Display;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.js.core.AbstractImageGenerator;
import org.knime.js.core.JavaScriptViewCreator;

public class CEFImageGenerator<T extends NodeModel & WizardNode<REP, VAL>, REP extends WebViewContent,
    VAL extends WebViewContent> extends AbstractImageGenerator<T, REP, VAL> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(CEFImageGenerator.class);
    private static final int INTERVAL = 500;
    private static final String FRAME = "document.getElementById('"+SINGLE_NODE_FRAME_ID+"')";
    private static final String FRAME_WIN = FRAME+".contentWindow.";
    private IBrowser browser;

    public CEFImageGenerator(final T nodeModel) {
        super(nodeModel);
    }

    @Override
    public void generateView(final Long optionalWait, final ExecutionContext exec) throws Exception {
        if (exec != null) {
            exec.setProgress("Initializing view");
        }
        T model = getNodeModel();
        String viewPath = model.getViewHTMLPath();
        if (viewPath == null || viewPath.isEmpty()) {
            LOGGER.error("Node model returned no path to view HTML. Cannot initialize view.");
            return;
        }

        WizardViewCreator<REP, VAL> viewCreator = model.getViewCreator();
        if (viewCreator instanceof JavaScriptViewCreator<?, ?> && model instanceof CSSModifiable) {
            String customCSS = ((CSSModifiable)model).getCssStyles();
            ((JavaScriptViewCreator<?, ?>)viewCreator).setCustomCSS(customCSS);
        }
        REP viewRepresentation = model.getViewRepresentation();
        VAL viewValue = model.getViewValue();
        String initCall = viewCreator.createInitJSViewMethodCall(viewRepresentation, viewValue);

        Display.getDefault().syncExec(() -> {
            browser = Browser.windowless();
        });
        Display.getDefault().syncExec(() -> {
            browser.setUrl(new File(viewPath).toURI().toString());
        });
        waitForDocumentReady(INTERVAL);
        Display.getDefault().syncExec(() -> {
            browser.evaluate("window.headless = true;");
            browser.evaluate(initCall);
        });
        if (exec != null) {
            exec.setProgress(0.66);
        }

        //wait until any element has been appended to body, which is not the service header
        waitFor(DEFAULT_TIMEOUT, INTERVAL, true, () -> {
            String anyNonKnimeElement = "body > *:not(#knime-service-header)";
            return Boolean.TRUE.equals(browser.evaluate("return "+ FRAME +" && "+ FRAME_WIN +"$('"+anyNonKnimeElement+"').length > 0;"));
        });

        //wait additional specified time to compensate for initial animation, etc.
        if (optionalWait != null && optionalWait > 0L) {
            int waitInS = (int) (optionalWait/1000);
            final double interval = 0.33 / Math.max(1, waitInS);
            if (exec != null) {
                String pString = "Waiting additional time.";
                if (waitInS > 0) {
                    pString = "Waiting additional " + waitInS + " seconds.";
                }
                exec.setProgress(pString);
            }
            try {
                waitFor(optionalWait, INTERVAL, false, () -> {
                    if (exec != null) {
                        exec.setProgress(exec.getProgressMonitor().getProgress() + interval);
                    }
                    return false;
                });
            } catch (Exception e) { /* do nothing */ }
        }
        if (exec != null) {
            exec.setProgress(1.0);
        }
    }

    private boolean waitForDocumentReady(final long interval) throws Exception {
        return waitFor(DEFAULT_TIMEOUT, interval, true, () -> {
            return (Boolean.TRUE.equals(browser.evaluate("return (document.readyState == 'complete');")));
        });
    }

    private static boolean waitFor(final long timeout, final long interval, final boolean ui, final Callable<Boolean> condition) throws Exception {
        AtomicBoolean ready = new AtomicBoolean(false);
        long time = System.currentTimeMillis();
        long time_timeout = time + timeout*1000;

        while (time < time_timeout && !ready.get()) {
            Runnable runnable = () -> {
                try {
                    if (Boolean.TRUE.equals(condition.call())) {
                        ready.set(true);
                    }
                } catch (Exception e) {
                    // ignoring
                }
            };
            if (ui) {
                Display.getDefault().syncExec(runnable);
            } else {
                runnable.run();
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                // ignoring
            }
            time = System.currentTimeMillis();
        }
        if (ready.get()) {
            return true;
        }
        throw new RuntimeException("Timeout waiting");
    }

    @Override
    public Object retrieveImage(final String methodCall) throws Exception {
        Object[] image = new Object[1];
        Display.getDefault().syncExec(() -> {
            image[0] = browser.evaluate("return " + FRAME_WIN + methodCall);
        });
        return image[0];
    }

    @Override
    public void cleanup() {
        Display.getDefault().syncExec(() -> {
            browser.close();
        });
        browser = null;
    }

}
