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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.web.WebTemplate;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.util.FileUtil;
import org.knime.core.wizard.SubnodeViewableModel;
import org.knime.js.core.JSCorePlugin;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * View implementation utilizing a Chromium browser via Selenium Webdriver. The view
 * has only indirect access to the NodeModel via get and setViewContent methods and therefore simulates the behavior of
 * the same view in the WebPortal.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @param <T> requires a {@link ViewableModel} implementing {@link WizardNode} as well
 * @param <REP> the {@link WebViewContent} view representation implementation used
 * @param <VAL> the {@link WebViewContent} view value implementation used
 * @since 3.4
 */
public class ChromeWizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
		extends AbstractWizardNodeView<T, REP, VAL> {

	private static NodeLogger LOGGER = NodeLogger.getLogger(ChromeWizardNodeView.class);
	private static Boolean CHROME_PRESENT = null;

	private static final int DEFAULT_TIMEOUT = 30;
	private static final int COMET_TIMEOUT = 1;
	private static final int DEFAULT_WIDTH = 1024;
	private static final int DEFAULT_HEIGHT = 768;

	private final Object LOCK = new Object();

	private final ChromeViewService m_service;
	private ChromeDriver m_driver;
	private String m_windowHandle;
	private Thread m_cometThread;
	private AtomicBoolean m_shutdownCometThread = new AtomicBoolean(false);
	private File m_repTempFile;
	private File m_valTempFile;
	private File m_bridgeTempFile;

	private String m_viewTitle = "KNIME view";

	/**
	 * Instantiates a new view instance
	 * @param viewableModel the underlying model
	 */
	public ChromeWizardNodeView(final T viewableModel) {
		super(viewableModel);
		m_service = ChromeViewService.getInstance();
	}

	/**
     * @return true if the view is enabled, false otherwise
	 * @since 3.5
     */
	public static boolean isEnabled() {
	    return MultiOSDriverActivator.getBundledChromeDriverPath().isPresent();
	}

	@Override
	protected void closeView() {
		if (m_driver != null) {
			try {
				m_shutdownCometThread.set(true);
				m_driver.quit();
			} catch (Exception e) { /* continue shutdown */ }
		}
		if (getViewableModel() instanceof SubnodeViewableModel) {
            ((SubnodeViewableModel)getViewableModel()).discard();
        }
		tryDeleteTempFiles(true);
	}

	@Override
	protected void modelChanged() {
	    m_shutdownCometThread.set(true);
	    testAlive();
	    if (m_driver == null) {
	        // view most likely disposed
	        return;
	    }
	    // delete current representation and value files
	    tryDeleteTempFiles(false);
	    // force reload of bridge in view
		m_driver.executeScript("seleniumKnimeBridge.clearView()");
		WizardNode<REP,VAL> model = getModel();
		// write potentially changed representation and value to disk (leave bridge file in place)
		writeTempViewFiles(model.getViewRepresentation(), model.getViewValue(), model.getViewCreator(), null);
		// initialize view without forcing focus of window
		initView(false);
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
		m_viewTitle = title;
        T model = getViewableModel();
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
			String bridgeFile = FileLocator.toFileURL(bridgeURL).getFile();
			if (Platform.getOS().equals(Platform.OS_WIN32) && (bridgeFile.startsWith("/") || bridgeFile.startsWith("\\"))) {
			    bridgeFile = bridgeFile.substring(1);
		    }
			bridgePath = Paths.get(bridgeFile);
		} catch (Exception e) {
			throw new SeleniumViewException("Could not find selenium-knime-bridge.html: " + e.getMessage(), e);
		}
		writeTempViewFiles(viewRepresentation, viewValue, viewCreator, bridgePath);

		m_windowHandle = initDriver(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		if (m_windowHandle != null) {
		    initView(true);
		}
	}

    /**
     * @param left left window coordinate
     * @param top top window coordinate
     * @param width window width
     * @param height window height
     * @return window handle as string to the newly created Chromium instance, or null
     * @since 3.5
     */
    protected String initDriver(final int left, final int top, final int width, final int height) {
        return initDriver(left, top, width, height, false);
    }

    /**
     * @param left left window coordinate
     * @param top top window coordinate
     * @param width window width
     * @param height window height
     * @param resolveChromium true if distributed Chromium should be resolved, false otherwise
     * @return window handle as string to the newly created Chromium instance, or null
     * @since 3.5
     */
    protected String initDriver(final int left, final int top, final int width, final int height, final boolean resolveChromium) {
		Optional<String> chromeDriverPath = MultiOSDriverActivator.getBundledChromeDriverPath();
		if (!chromeDriverPath.isPresent()) {
			throw new SeleniumViewException("Path to Chrome driver could not be retrieved!");
		}
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--app=" + m_bridgeTempFile.toURI().toString());
		options.addArguments("--window-size=" + width + "," + height);
		options.addArguments("--window-position=" + left + "," + top);
		//options.addArguments("--disable-infobars");
		options.addArguments("--allow-file-access", "--allow-file-access-from-files");
		IPreferenceStore prefs = JSCorePlugin.getDefault().getPreferenceStore();
		if (resolveChromium) {
		    Optional<String> cPath = MultiOSDriverActivator.getChromiumPath();
		    if (!cPath.isPresent()) {
		        throw new SeleniumViewException("Path to internal Chromium executables could not be retrieved!");
		    }
		    options.setBinary(cPath.get());
		    Path dataDir = Paths.get(cPath.get()).getParent().resolve("Data");
		    if (!dataDir.toFile().exists()) {
		        dataDir.toFile().mkdir();
		    }
		    options.addArguments("--user-data-dir=" + dataDir);
		} else {
		    String binPath = prefs.getString(JSCorePlugin.P_BROWSER_PATH);
		    if (binPath != null && !binPath.isEmpty()) {
		        options.setBinary(binPath);
		    }
		}

		String cliOptions = prefs.getString(JSCorePlugin.P_BROWSER_CLI_ARGS);
		if (cliOptions != null && !cliOptions.isEmpty()) {
		    options.addArguments(cliOptions);
		}

		try {
		    m_driver = new ChromeDriver(options);
		    m_driver.manage().timeouts().pageLoadTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).setScriptTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

		    waitForDocumentReady();
		    // Store the current window handle
		    return m_driver.getWindowHandle();
		} catch (Exception e) {
		    String errorMessage = "Could not initialize Chrome driver. ";
		    if (e instanceof SessionNotCreatedException) {
		        /*This exception is thrown when view is closed by user while loading,
		        but might also occur in other cases.
		        Message is only displayed as info, as to not confuse users.*/
		        LOGGER.info(errorMessage + e.getMessage(), e);
		    }
		    else {
		        LOGGER.error(errorMessage + e.getMessage(), e);
		        throw new SeleniumViewException(errorMessage + "Check log for more details. \n\nThe browser can be configured in Preferences -> KNIME -> JavaScript Views");
		    }
		    return null;
		}
	}

    /**
     * Tests if a Selenium session with a user installed Chrome instance can be successfully created.
     * The test is only done if no preference setting for the desired browser type is found, otherwise
     * the method always returns true.
     * @return true, if preference setting is found or Chrome is present on the system, false otherwise
     * @since 3.5
     */
    public static boolean isChromePresent() {
        if (CHROME_PRESENT == null) {
            String classString = JSCorePlugin.getDefault().getPreferenceStore().getString(JSCorePlugin.P_VIEW_BROWSER);
            if (StringUtils.isNotEmpty(classString)) {
                CHROME_PRESENT = true;
            } else {
                CHROME_PRESENT = testChromePresent();
            }
        }
        return CHROME_PRESENT;
    }

    private static boolean testChromePresent() {
        LOGGER.debug("Testing for presence of Chrome browser on system...");
        Optional<String> chromeDriverPath = MultiOSDriverActivator.getBundledChromeDriverPath();
        if (!chromeDriverPath.isPresent()) {
            return false;
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        try {
            ChromeDriver driver = new ChromeDriver(options);
            driver.close();
        } catch (Exception e) {
            LOGGER.debug("Chrome browser could not be found or initialized", e);
            return false;
        }
        LOGGER.debug("Chrome browser found successfully.");
        return true;
    }

    private void initView(final boolean forceFocus) {
        WizardViewCreator<REP, VAL> viewCreator = getModel().getViewCreator();
        String viewURL = new File(getModel().getViewHTMLPath()).toURI().toString();
        String repURL = m_repTempFile.toURI().toString();
        String valURL = m_valTempFile.toURI().toString();
        String initCall = viewCreator.wrapInTryCatch(viewCreator.createInitJSViewMethodCall(false, null, null));
        // pass arguments 'nicely' to init method
        m_driver.executeScript("seleniumKnimeBridge.initView(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);", viewURL,repURL, valURL, m_viewTitle, initCall);
        if (forceFocus) {
            m_driver.switchTo().window(m_windowHandle);
        }
        initializeCometQuery();
    }

    /**
     * Writes view representation and value to disk as temporary JSON files. Optionally copies KNIME-Selenium-Bridge to temporary location.
     * @param viewRepresentation the view representation instance to write to disk
     * @param viewValue the view value instance to write to disk
     * @param viewCreator the view creator instance used for the view files and method creation
     * @param bridgePath optional path to the KNIME-Selenium-Bridge file, null if file does not need to be copied (e.g. model changed)
     */
    private void writeTempViewFiles(final REP viewRepresentation, final VAL viewValue, final WizardViewCreator<REP, VAL> viewCreator, final Path bridgePath) {
		// we can't pass data in directly, as chrome seems to have a 2MB size limit for these calls
		// see https://bugs.chromium.org/p/chromedriver/issues/detail?id=1026
		// workaround is writing to disk and passing as urls to be fetched by AJAX call
		String viewRepString = viewCreator.getViewRepresentationJSONString(viewRepresentation);
		String viewValueString = viewCreator.getViewValueJSONString(viewValue);
		try {
		    // force creation of temp directory, copy resources and create HTML stub and debug output
		    getModel().getViewHTMLPath();
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
			m_bridgeTempFile = null;
			if (bridgePath != null) {
			    m_bridgeTempFile = FileUtil.createTempFile("selenium-knime-bridge_ " + System.currentTimeMillis(), ".html", tempPath.toFile(), true);
			}
			try (BufferedWriter writer = Files.newBufferedWriter(m_repTempFile.toPath(), Charset.forName("UTF-8"))) {
				writer.write(viewRepString);
				writer.flush();
			}
			try (BufferedWriter writer = Files.newBufferedWriter(m_valTempFile.toPath(), Charset.forName("UTF-8"))) {
				writer.write(viewValueString);
				writer.flush();
			}
            if (bridgePath != null) {
                try {
                    Files.copy(bridgePath, m_bridgeTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                } catch (Exception e) {
                    throw new SeleniumViewException("Error while copying KNIME-Selenium-Bridge: " + e.getMessage(), e);
                }
            }
		} catch (IOException e) {
			// handle exception further up
			throw new SeleniumViewException(e);
		}
	}

    /**
     * Tries to delete current temporary files. Potential errors are ignored.
     * @param deleteBridgeFile true if the KNIME-Selenium-Bridge file is supposed to be deleted, false otherwise
     */
    private void tryDeleteTempFiles(final boolean deleteBridgeFile) {
        try {
            if (m_repTempFile != null && m_repTempFile.exists()) {
                m_repTempFile.delete();
                m_repTempFile = null;
            }
        } catch (Exception e) { /* continue */ }
        try {
            if (m_valTempFile != null && m_valTempFile.exists()) {
                m_valTempFile.delete();
                m_valTempFile = null;
            }
        } catch (Exception e) { /* continue */ }
        try {
            if (deleteBridgeFile && m_bridgeTempFile != null && m_bridgeTempFile.exists()) {
                m_bridgeTempFile.delete();
                m_bridgeTempFile = null;
            }
        } catch (Exception e) { /* continue */ }
    }

	private void waitForDocumentReady() {
		WebDriverWait wait = new WebDriverWait(m_driver, DEFAULT_TIMEOUT);
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
				try {
				    m_driver.quit();
				} catch (Exception ignore) { /* nothing to do at this point */ }
				m_driver = null;
				return false;
			}
		}
		return false;
	}

	private void initializeCometQuery() {
	    m_shutdownCometThread.set(false);
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
			int alertTimeout = COMET_TIMEOUT;
			m_driver.manage().timeouts().setScriptTimeout(COMET_TIMEOUT * 2, TimeUnit.SECONDS);
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
							String action = (String)m_driver.executeAsyncScript("seleniumKnimeBridge.registerCometRequest(arguments);", COMET_TIMEOUT);
							if (action != null && !ChromeViewService.NO_ACTION.equals(action)) {
							    LOGGER.debug("KNIME-Selenium-COMET returned " + action);
							}
							if (ChromeViewService.RESET_BUTTON_PRESSED.equals(action)) {
							    // Could only call if actual settings have been changed,
				                // however there might be things in views that one can change
				                // which do not get saved, then it's nice to trigger the event anyways.
				                /*if (checkSettingsChanged()) {*/
				                    modelChanged();
				                    break;
				                /*}*/
							} else if (ChromeViewService.APPLY_BUTTON_PRESSED.equals(action)) {
							    applyTriggered(false);
							} else if (ChromeViewService.APPLY_DEFAULT_BUTTON_PRESSED.equals(action)) {
							    applyTriggered(true);
							} else if (ChromeViewService.CLOSE_BUTTON_PRESSED.equals(action)) {
							    if (checkSettingsChanged()) {
							        // close dialog triggers subsequent actions, leave comet thread intact
			                        showCloseDialog();
							    } else {
							        closeView();
							        break;
							    }
                            } else if (ChromeViewService.CLOSE_DISCARD_BUTTON_PRESSED.equals(action)) {
                                closeView();
                                break;
                            } else if (ChromeViewService.CLOSE_APPLY_BUTTON_PRESSED.equals(action)) {
                                if (applyTriggered(false)) {
                                    closeView();
                                    break;
                                }
							} else if (ChromeViewService.CLOSE_APPLY_DEFAULT_BUTTON_PRESSED.equals(action)) {
							    if (applyTriggered(true)) {
                                    closeView();
                                    break;
                                }
							}
						}
					} catch (WebDriverException wde) {
						if (wde instanceof UnhandledAlertException) {
							// open alerts will let the execute call fail, need to explicitly wait
							try {
								Thread.sleep(alertTimeout * 1000);
							} catch (InterruptedException e) { /* do nothing */ }
						}
						/* do nothing, try again */
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
            String validString = (String)m_driver.executeScript("return seleniumKnimeBridge.executeOnFrame(arguments[0]);", evalCode);
            valid = Boolean.parseBoolean(validString);
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
            jsonString = (String)m_driver.executeScript("return seleniumKnimeBridge.executeOnFrame(arguments[0]);", evalCode);
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
        m_driver.executeScript("return seleniumKnimeBridge.executeOnFrame(arguments[0]);", showErrorCall);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean showApplyOptionsDialog(final boolean showDiscardOption, final String title, final String message) {
        List<CloseDialogOption> options = new ArrayList<CloseDialogOption>(3);
        if (showDiscardOption) {
            options.add(new CloseDialogOption(DISCARD_LABEL, DISCARD_DESCRIPTION, ChromeViewService.CLOSE_DISCARD_BUTTON_PRESSED));
        }
        options.add(new CloseDialogOption(APPLY_LABEL, String.format(APPLY_DESCRIPTION_FORMAT, showDiscardOption ? ", closes the view" : ""), ChromeViewService.CLOSE_APPLY_BUTTON_PRESSED));
        options.add(new CloseDialogOption(APPLY_DEFAULT_LABEL, String.format(APPLY_DEFAULT_DESCRIPTION_FORMAT, showDiscardOption ? ", closes the view" : ""), ChromeViewService.CLOSE_APPLY_DEFAULT_BUTTON_PRESSED));
        ObjectMapper mapper = new ObjectMapper();
        try {
            String optionsString = mapper.writeValueAsString(options);
            testAlive();
            if (m_driver != null) {
                m_driver.executeScript("seleniumKnimeBridge.showModal(arguments[0], arguments[1], arguments[2]);", title, message, optionsString);
            }
        } catch (JsonProcessingException ex) {
            LOGGER.error("Could not trigger confirm close dialog: " + ex.getMessage(), ex);
        }
        return false;
    }

    @JsonAutoDetect
    private static class CloseDialogOption {

        private String m_label;
        private String m_description;
        private String m_signal;

        @JsonCreator
        CloseDialogOption(@JsonProperty("label") final String label, @JsonProperty("description") final String description, @JsonProperty("signal") final String signal) {
            m_label = label;
            m_description = description;
            m_signal = signal;
        }

        /**
         * @return the label
         */
        @JsonProperty("label")
        public String getLabel() {
            return m_label;
        }

        /**
         * @return the description
         */
        @JsonProperty("description")
        public String getDescription() {
            return m_description;
        }

        /**
         * @return the signal
         */
        @JsonProperty("signal")
        public String getSignal() {
            return m_signal;
        }
    }
}
