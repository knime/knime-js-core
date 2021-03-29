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
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.chromium.Browser;
import org.eclipse.swt.chromium.IBrowser;
import org.eclipse.swt.widgets.Display;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.js.core.AbstractImageGenerator;
import org.knime.js.core.JavaScriptViewCreator;

/**
 * Image generator using the chromium embedded framework.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Nico Sebey, EQUO
 *
 * @param <T>
 * @param <REP>
 * @param <VAL>
 */
@SuppressWarnings("java:S119")
public class CEFImageGenerator<T extends NodeModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
    extends AbstractImageGenerator<T, REP, VAL> {

    private static final int MAX_NUMBER_OF_ATTEMPTS_TO_EVALUATE_SCRIPT = 5;

    private static final NodeLogger LOGGER = NodeLogger.getLogger(CEFImageGenerator.class);

    private static final int INTERVAL = 200;

    private static final String FRAME = "document.getElementById('" + SINGLE_NODE_FRAME_ID + "')";

    private static final String FRAME_WIN = FRAME + ".contentWindow.";

    private IBrowser m_browser;

    /**
     * @param nodeModel
     */
    public CEFImageGenerator(final T nodeModel) {
        super(nodeModel);
    }

    @Override
    public void generateView(final Long optionalWait, final ExecutionContext exec) throws Exception {
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
            m_browser = Browser.windowless();
            m_browser.setUrl(new File(viewPath).toURI().toString());
        });

        waitForDocumentReady(exec);

        evaluateInBrowser("window.headless = true;return true;");
        evaluateInBrowser(initCall + "return true");

        exec.setProgress(0.66);

        waitForFrameBodyReady(exec);

        // wait additional specified time to compensate for initial animation, etc.
        if (optionalWait != null && optionalWait > 0L) {
            int waitInS = (int)(optionalWait / 1000);
            String pString = "Waiting additional time.";
            if (waitInS > 0) {
                pString = "Waiting additional " + waitInS + " seconds.";
            }
            exec.setProgress(pString);
            Thread.sleep(optionalWait);
        }
        exec.setProgress(1.0);
    }

    /**
     * Tries to evaluate a script in the browser. Since the script-evaluation doesn't seem to be very reliable, we just
     * try it again (for a limited number of times) if an exception is thrown or no result returned.
     *
     * @param script the script to evaluate
     * @return the return value of the script evaluation
     */
    private Object evaluateInBrowser(final String script) {
        AtomicReference<Object> res = new AtomicReference<>();
        AtomicReference<SWTException> exception = new AtomicReference<>();
        int attempts = 0;
        do {
            Display.getDefault().syncExec(() -> {
                try {
                    res.set(m_browser.evaluate(script));
                } catch (SWTException e) {
                    exception.set(e);
                }
            });
            if (exception.get() != null) {
                attempts++;
                LOGGER.debug(
                    "Executing script failed. Trying again (" + attempts + "). The script is: '" + script + "'",
                    exception.get());
                exception.set(null);
                waitConstantTime();
            }
        } while (res.get() == null && attempts < MAX_NUMBER_OF_ATTEMPTS_TO_EVALUATE_SCRIPT);
        if (res.get() == null) {
            LOGGER.error("Executing script failed after " + attempts + " attempts. The script is: '" + script + "'");
        }
        return res.get();
    }

    private void waitForDocumentReady(final ExecutionContext exec) throws TimeoutException, CanceledExecutionException {
        waitForSuccessTimeoutOrCancellation(DEFAULT_TIMEOUT, exec,
            () -> Boolean.TRUE.equals(evaluateInBrowser("return (document.readyState == 'complete');")));
    }

    private void waitForFrameBodyReady(final ExecutionContext exec)
        throws TimeoutException, CanceledExecutionException {
        // wait until any element has been appended to body, which is not the service header
        final String anyNonKnimeElement = "body > *:not(#knime-service-header)";
        waitForSuccessTimeoutOrCancellation(DEFAULT_TIMEOUT, exec, () -> Boolean.TRUE.equals(evaluateInBrowser("return "
            + FRAME + " && " + FRAME_WIN + "document.querySelectorAll('" + anyNonKnimeElement + "').length > 0;")));
    }

    /**
     * Waits till either<br>
     * - the given condition evaluates to true <br>
     * - we run into the given timeout <br>
     * - the user cancels
     *
     * Cleans up when canceled, but not when timing out!
     *
     * @param timeout the maximum time to wait
     * @param exec for cancellation
     * @param condition the condition to evaluate
     */
    private void waitForSuccessTimeoutOrCancellation(final long timeout, final ExecutionContext exec,
        final BooleanSupplier condition) throws TimeoutException, CanceledExecutionException {
        long time = System.currentTimeMillis();
        long timeoutTime = time + timeout * 1000;
        boolean done = false;
        do {
            try {
                exec.checkCanceled();
            } catch (CanceledExecutionException e) {
                cleanup();
                throw e;
            }

            done = condition.getAsBoolean();
            if (!done) {
                waitConstantTime();
                time = System.currentTimeMillis();
            }
        } while (time < timeoutTime && !done);
        if (!done) {
            throw new TimeoutException("Timeout while generating image");
        }
    }

    private static void waitConstantTime() {
        try {
            Thread.sleep(INTERVAL);
        } catch (InterruptedException e1) { // NOSONAR
            //
        }
    }

    @Override
    public Object retrieveImage(final String methodCall) throws Exception {
        return evaluateInBrowser("return " + FRAME_WIN + methodCall);
    }

    @Override
    public void cleanup() {
        Display.getDefault().syncExec(() -> {
            if (m_browser != null) {
                m_browser.close();
            }
        });
        m_browser = null;
    }

}
