/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   16 Jun 2017 (albrecht): created
 */
package org.knime.ext.seleniumdrivers.multios;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.web.WebTemplate;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.util.FileUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class ChromeWizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
		extends AbstractWizardNodeView<T, REP, VAL> {

	private static NodeLogger LOGGER = NodeLogger.getLogger(ChromeWizardNodeView.class);

	private static final int DEFAULT_TIMEOUT = 30;
	private static final int DEFAULT_WIDTH = 1024;
	private static final int DEFAULT_HEIGHT = 768;

	private final Object LOCK = new Object();

	private final ChromeViewService m_service;
	private ChromeDriver m_driver;
	private Thread m_cometThread;
	private AtomicBoolean m_shutdownCometThread = new AtomicBoolean(false);
	private File m_repTempFile;
	private File m_valTempFile;
	private File m_bridgeTempFile;

	public ChromeWizardNodeView(final T viewableModel) {
		super(viewableModel);
		m_service = ChromeViewService.getInstance();
	}

	@Override
	protected void closeView() {
		if (m_driver != null) {
			try {
				m_shutdownCometThread.set(true);
				m_driver.quit();
			} catch (Exception e) {}
		}
	}

	@Override
	protected void modelChanged() {
		m_driver.executeScript("seleniumKnimeBridge.clearView()");
		//TODO fill again
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
    @Override
    public final void callOpenView(final String title, final Rectangle knimeWindowBounds) {
		T model = getViewableModel();
		String viewPath = model.getViewHTMLPath();
		int x = 0;
		int y = 0;
		if (knimeWindowBounds != null) {
			x = (knimeWindowBounds.width / 2) - (DEFAULT_WIDTH / 2) + knimeWindowBounds.x;
			y = (knimeWindowBounds.height / 2) - (DEFAULT_HEIGHT / 2) + knimeWindowBounds.y;
		}

		REP viewRepresentation = model.getViewRepresentation();
		VAL viewValue = model.getViewValue();
		WizardViewCreator<REP, VAL> viewCreator = model.getViewCreator();

		Path bridgePath;
		try {
			URL bridgeURL = Platform.getBundle(MultiOSDriverActivator.getBundleName()).getEntry("src-js/selenium-knime-bridge.html");
			bridgePath = Paths.get(FileLocator.toFileURL(bridgeURL).getFile());
			/*try (BufferedReader reader = Files.newBufferedReader(bridgePath, Charset.forName("UTF-8"))) {
				bridgeCode = reader.lines().collect(Collectors.joining("\n"));
			}*/
		} catch (Exception e) {
			throw new SeleniumViewException("Could not find selenium-knime-bridge.html: " + e.getMessage(), e);
		}
		writeTempViewFiles(viewRepresentation, viewValue, viewCreator, bridgePath);

		String handle = initDriver(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		//m_driver.executeScript(bridgeCode);
		//m_driver.executeScript("seleniumKnimeBridge.initButtons();");
		String initCall = viewCreator.wrapInTryCatch(viewCreator.createInitJSViewMethodCall(false, null, null));
		m_driver.executeScript("seleniumKnimeBridge.initView(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);", new File(viewPath).toURI().toString(), m_repTempFile.toURI().toString(), m_valTempFile.toURI().toString(), title, initCall);
		m_driver.switchTo().window(handle);
		initializeCometQuery();
	}

    private String initDriver(final int left, final int top, final int width, final int height) {
		Optional<String> chromeDriverPath = MultiOSDriverActivator.getBundledChromeDriverPath();
		if (!chromeDriverPath.isPresent()) {
			//TODO throw error
			return null;
		}
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--app=" + m_bridgeTempFile.toURI().toString());
		options.addArguments("--window-size=" + width + "," + height);
		options.addArguments("--window-position=" + left + "," + top);
		options.addArguments("--allow-file-access", "--allow-file-access-from-files");
		//TODO set chrome executable and additional options from preferences

		m_driver = new ChromeDriver(options);
		m_driver.manage().timeouts().pageLoadTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).setScriptTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

		waitForDocumentReady();
		//Store the current window handle
		String currentWindowHandle = m_driver.getWindowHandle();

		//run your javascript and alert code
		//m_driver.executeScript("alert('View created')");
		//m_driver.switchTo().alert().accept();

		//Switch back to to the window using the handle saved earlier
		//m_driver.switchTo().window(currentWindowHandle);
		return currentWindowHandle;
	}

    private void writeTempViewFiles(final REP viewRepresentation, final VAL viewValue, final WizardViewCreator<REP, VAL> viewCreator, final Path bridgePath) {
		// we can't pass data in directly, as chrome seems to have a 2MB size limit for these calls
		// see https://bugs.chromium.org/p/chromedriver/issues/detail?id=1026
		// workaround is writing to disk and passing as urls to be fetched by AJAX call
		String viewRepString = viewCreator.getViewRepresentationJSONString(viewRepresentation);
		String viewValueString = viewCreator.getViewValueJSONString(viewValue);
		try {
			Path tempPath = viewCreator.getCurrentLocation();
			if (tempPath == null) {
				throw new IllegalArgumentException("Temporary directory for view creation does not exist.");
			}
			try {
				if (m_repTempFile != null) {
					Files.deleteIfExists(m_repTempFile.toPath());
				}
				if (m_valTempFile != null) {
					Files.deleteIfExists(m_valTempFile.toPath());
				}
			} catch (IOException | SecurityException e) {
				// log error but continue
				LOGGER.error("Temporary view file could not be deleted: " + e.getMessage(), e);
			}
			m_repTempFile = FileUtil.createTempFile("rep_" + System.currentTimeMillis(), ".json", tempPath.toFile(), true);
			m_valTempFile = FileUtil.createTempFile("val_" + System.currentTimeMillis(), ".json", tempPath.toFile(), true);
			m_bridgeTempFile = FileUtil.createTempFile("selenium-knime-bridge_ " + System.currentTimeMillis(), ".html", tempPath.toFile(), true);
			try (BufferedWriter writer = Files.newBufferedWriter(m_repTempFile.toPath(), Charset.forName("UTF-8"))) {
				writer.write(viewRepString);
				writer.flush();
			}
			try (BufferedWriter writer = Files.newBufferedWriter(m_valTempFile.toPath(), Charset.forName("UTF-8"))) {
				writer.write(viewValueString);
				writer.flush();
			}
			try {
			    Files.copy(bridgePath, m_bridgeTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (Exception e) {
			    throw new SeleniumViewException("Error while copying KNIME-Selenium-Bridge: " + e.getMessage(), e);
			}
		} catch (IOException e) {
			// handle exception further up
			throw new SeleniumViewException(e);
		}
	}

	private String loadAndParseArgumentsCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(loadJSONCode());
		builder.append("\n");
		builder.append("var initCall = arguments[2];\n");
		builder.append("var parsedRepresentation, parsedValue;\n");
		builder.append("laodJSONFile(arguments[0], function(rep){\n");
		builder.append("\tparsedRepresentation = rep;\n");
		builder.append("\tif (parsedValue) { eval(initCall) };\n");
		builder.append("});\n");
		builder.append("laodJSONFile(arguments[1], function(val){\n");
		builder.append("\tparsedValue = val;\n");
		builder.append("\tif (parsedRepresentation) { eval(initCall) };\n");
		builder.append("});\n");
		return builder.toString();
	}

	//TODO put this in a JS file to be loaded at the beginning
	private String loadJSONCode() {
		StringBuilder builder = new StringBuilder();
		builder.append("function laodJSONFile(url, callback) {\n");
		builder.append("\tvar httpRequest = new XMLHttpRequest();\n");
		builder.append("\thttpRequest.onreadystatechange = function() {\n");
		builder.append("\t\tif (httpRequest.readyState === 4) {\n");
		builder.append("\t\t\tif (httpRequest.status === 200 || httpRequest.status === 0) {\n");
		builder.append("\t\t\t\tvar data = JSON.parse(httpRequest.responseText);\n");
		builder.append("\t\t\t\tif (callback) callback(data);\n");
		builder.append("\t}}};\n");
		builder.append("\thttpRequest.open('GET', url);\n");
		builder.append("\thttpRequest.send();\n");
		builder.append("}\n");
		return builder.toString();
	}

	private String signalBrowserWindowCloseCode() {
		//FIXME this doesn't work yet, request returns with null, which also works, better solution?
		StringBuilder builder = new StringBuilder();
		builder.append("window.addEventListener('beforeunload', function (event) {\n");
		builder.append("\tif (window.signal) { window.signal('");
		builder.append(ChromeViewService.CLOSE_WINDOW);
		builder.append("'); }\n");
		builder.append("});");
		return builder.toString();
	}

	private void waitForDocumentReady() {
		WebDriverWait wait = new WebDriverWait(m_driver, DEFAULT_TIMEOUT);
		if (!(m_driver instanceof JavascriptExecutor)) {
			throw new IllegalArgumentException("Driver must support javascript execution");
		}
		wait.until(driver -> documentReady());
	}

	private ExpectedCondition<Boolean> documentReady() {
		return new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(final WebDriver driver) {
				String readyState = ((JavascriptExecutor) m_driver)
						.executeScript("if (document.readyState) return document.readyState;").toString();
				return "complete".equalsIgnoreCase(readyState);
			}

			@Override
			public String toString() {
				return "document ready state";
			}
		};
	}

	private boolean testAlive() {
		if (m_driver != null) {
			try {
				//this leads to exception if the browser was closed by user
				m_driver.getWindowHandles();
				return true;
			} catch (WebDriverException e) {
				m_shutdownCometThread.set(true);
				m_driver.quit();
				m_driver = null;
				return false;
			}
		}
		return false;
	}

	private void initializeCometQuery() {
		String handle = m_driver.getWindowHandle();
		m_cometThread = new Thread(m_service.getCometThreadGroup(), new CometRunnable(handle), ChromeViewService.COMET_THREAD_NAME + handle);
		m_cometThread.start();
	}

	private class CometRunnable implements Runnable {

		private String m_handle;

		CometRunnable(final String windowHandle) {
			m_handle = windowHandle;
		}

		@Override
		public void run() {
			int cometTimeout = DEFAULT_TIMEOUT;
			int alertTimeout = 2;
			m_driver.manage().timeouts().setScriptTimeout(cometTimeout * 2, TimeUnit.SECONDS);
			while (true) {
				if (m_shutdownCometThread.get()) {
					break;
				}
				synchronized(LOCK) {
					testAlive();
					if (m_driver == null) {
						break;
					}
					try {
						if (!m_driver.getWindowHandles().contains(m_handle)) {
							m_driver.quit();
							break;
						}
						if (m_driver.getWindowHandle().equals(m_handle)) {
							String action = (String)m_driver.executeAsyncScript("seleniumKnimeBridge.registerCometRequest(arguments);", cometTimeout);
							if (action != null && !ChromeViewService.NO_ACTION.equals(action)) {
							    LOGGER.debug("KNIME-Selenium-COMET returned " + action);
							}
							if (ChromeViewService.RESET_BUTTON_PRESSED.equals(action)) {
							    // Could only call if actual settings have been changed,
				                // however there might be things in views that one can change
				                // which do not get saved, then it's nice to trigger the event anyways.
				                /*if (checkSettingsChanged()) {*/
				                    modelChanged();
				                /*}*/
							} else if (ChromeViewService.APPLY_BUTTON_PRESSED.equals(action)) {
							    //TODO apply values
							} else if (ChromeViewService.APPLY_DEFAULT_BUTTON_PRESSED.equals(action)) {
							    //TODO apply values
							} else if (ChromeViewService.CLOSE_BUTTON_PRESSED.equals(action)) {
                                //TODO close dialog
                            } else if (ChromeViewService.CLOSE_DISCARD_BUTTON_PRESSED.equals(action)) {
                                closeView();
                                break;
                            } else if (ChromeViewService.CLOSE_APPLY_BUTTON_PRESSED.equals(action)) {
                                //TODO
							} else if (ChromeViewService.CLOSE_APPLY_DEFAULT_BUTTON_PRESSED.equals(action)) {
							    //TODO
							}
						}
					} catch (WebDriverException wde) {
						if (wde instanceof UnhandledAlertException) {
							// alerts will let the execute call fail, need to explicitly wait
							try {
								Thread.sleep(alertTimeout * 1000);
							} catch (InterruptedException e) { /* do nothing */ }
						}
						/* do nothing */
						//LOGGER.warn(wde.getMessage());
					}
				}
			}
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean viewInteractionPossible() {
        return testAlive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validateCurrentValueInView() {
        boolean valid = true;
        WizardViewCreator<REP, VAL> creator = getViewCreator();
        WebTemplate template = creator.getWebTemplate();
        String validateMethod = template.getValidateMethodName();
        if (validateMethod != null && !validateMethod.isEmpty()) {
            String evalCode = creator
                .wrapInTryCatch("return JSON.stringify(" + creator.getNamespacePrefix() + validateMethod + "());");
            valid = (Boolean)m_driver.executeScript(evalCode);
        }
        return valid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String retrieveCurrentValueFromView() {
        WizardViewCreator<REP, VAL> creator = getViewCreator();
        WebTemplate template = creator.getWebTemplate();
        String pullMethod = template.getPullViewContentMethodName();
        String ns = creator.getNamespacePrefix();
        String jsonString = null;
        if (ns != null && !ns.isEmpty() && pullMethod != null && !pullMethod.isEmpty()) {
            String evalCode = creator.wrapInTryCatch("if (typeof " + ns.substring(0, ns.length() - 1)
                + " != 'undefined') { return JSON.stringify(" + ns + pullMethod + "());}");
            jsonString = (String)m_driver.executeScript(evalCode);
        }
        return jsonString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showValidationErrorInView(final String error) {
        WizardViewCreator<REP, VAL> creator = getViewCreator();
        WebTemplate template = creator.getWebTemplate();
        String showErrorMethod = template.getSetValidationErrorMethodName();
        String escapedError = error.replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ");
        String showErrorCall = creator.wrapInTryCatch(creator.getNamespacePrefix() + showErrorMethod + "('" + escapedError + "');");
        m_driver.executeScript(showErrorCall);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean showApplyOptionsDialog(final boolean showDiscardOption, final String title, final String message) {
        // TODO Auto-generated method stub
        return false;
    }
}
