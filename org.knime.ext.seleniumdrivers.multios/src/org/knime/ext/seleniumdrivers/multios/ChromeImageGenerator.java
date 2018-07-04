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
 *   13 Jun 2018 (albrecht): created
 */
package org.knime.ext.seleniumdrivers.multios;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.js.core.AbstractImageGenerator;
import org.knime.js.core.JSCorePlugin;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 3.6
 */
public class ChromeImageGenerator<T extends NodeModel & WizardNode<REP, VAL>, REP extends WebViewContent,
    VAL extends WebViewContent> extends AbstractImageGenerator<T, REP, VAL> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ChromeImageGenerator.class);

    private final ChromeViewService m_service;
    private ChromeDriver m_driver;
    private File m_userDataDir;

    /**
     * @param nodeModel
     */
    public ChromeImageGenerator(final T nodeModel) {
        super(nodeModel);
        m_service = ChromeViewService.getInstance();
    }

    public static boolean isEnabled() {
        return ChromeWizardNodeView.isEnabled();
    }

    protected ChromeDriver initDriver() {
        return initDriver(false);
    }

    /**
     * @param resolveChromium
     * @return
     */
    protected ChromeDriver initDriver(final boolean resolveChromium) {
        Optional<String> chromeDriverPath = MultiOSDriverActivator.getBundledChromeDriverPath();
        if (!chromeDriverPath.isPresent()) {
            throw new SeleniumViewException("Path to Chrome driver could not be retrieved!");
        }
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--allow-file-access", "--allow-file-access-from-files");
            options.setHeadless(true);
            IPreferenceStore prefs = JSCorePlugin.getDefault().getPreferenceStore();
            if (resolveChromium) {
                Optional<String> cPath = MultiOSDriverActivator.getChromiumPath();
                if (!cPath.isPresent()) {
                    throw new SeleniumViewException("Path to internal Chromium executables could not be retrieved!");
                }
                options.setBinary(cPath.get());

                m_userDataDir = m_service.getAndLockUserDataDir(true);
                options.addArguments("--user-data-dir=" + m_userDataDir.getAbsolutePath(), "--profile-directory=Default");
                /*options.addArguments("--no-default-browser-check", "--profiling-flush=1", "--no-session-id");
            options.addArguments("--no-first-run", "--no-experiments", "--noerrdialogs", "--bwsi");
            options.addArguments("--disable-breakpad", "--disable-infobars", "--disable-session-restore");*/
            } else {
                String binPath = prefs.getString(JSCorePlugin.P_HEADLESS_BROWSER_PATH);
                if (binPath != null && !binPath.isEmpty()) {
                    options.setBinary(binPath);
                }
            }
            String cliOptions = prefs.getString(JSCorePlugin.P_HEADLESS_BROWSER_CLI_ARGS);
            if (cliOptions != null && !cliOptions.isEmpty()) {
                options.addArguments(cliOptions);
            }

            m_driver = new ChromeDriver(options);
            m_driver.manage().timeouts()
            .pageLoadTimeout(ChromeWizardNodeView.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .setScriptTimeout(ChromeWizardNodeView.DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            return m_driver;
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
                Optional<String> additionalInfo = Optional.empty();
                if (resolveChromium) {
                    additionalInfo = m_service.tryRetrieveMissingSystemLibraries(MultiOSDriverActivator.getChromiumPath());
                }
                StringBuilder displayString = new StringBuilder(errorMessage);
                if (additionalInfo.isPresent()) {
                    String addString = "Missing system libraries for executing Chromium: " + additionalInfo.get();
                    LOGGER.error(addString);
                    displayString.append(addString + "\n");
                }
                displayString.append("Check log for more details. \n\nThe browser can be configured in Preferences -> KNIME -> JavaScript Views");
                throw new SeleniumViewException(displayString.toString());
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateView(final Long optionalWait, final ExecutionContext exec) throws Exception {
        try {
            m_driver = initDriver();
            if (exec != null) {
                exec.setProgress("Initializing view");
            }
            //m_driver = initDriver();

            T model = getNodeModel();
            String viewPath = model.getViewHTMLPath();
            if (viewPath == null || viewPath.isEmpty()) {
                throw new SeleniumViewException("Node model returned no path to view HTML. Cannot initialize view.");
            }
            m_driver.navigate().to(new File(viewPath).toURI().toString());
            ChromeWizardNodeView.waitForDocumentReady(m_driver);
            REP viewRepresentation = model.getViewRepresentation();
            VAL viewValue = model.getViewValue();
            String initCall = model.getViewCreator().createInitJSViewMethodCall(viewRepresentation, viewValue);
            m_driver.executeScript(initCall);
            if (exec != null) {
                exec.setProgress(0.66);
            }
            WebDriverWait wait = new WebDriverWait(m_driver, ChromeWizardNodeView.DEFAULT_TIMEOUT);
            //wait until any element has been appended to body
            wait.until(driver -> ExpectedConditions.presenceOfElementLocated(By.xpath("//body[./* or ./text()]")));

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
                Wait<WebDriver> timedWait = new FluentWait<WebDriver>(m_driver)
                        .withTimeout(Duration.ofMillis(optionalWait))
                        .pollingEvery(Duration.ofSeconds(1))
                        .ignoring(NoSuchElementException.class);
                try {
                    timedWait.until(driver -> {
                        if (exec != null) {
                            exec.setProgress(exec.getProgressMonitor().getProgress() + interval);
                        }
                        return null;
                    });
                } catch (Exception e) { /* do nothing */ }
            }
            if (exec != null) {
                exec.setProgress(1.0);
            }
        } catch (Exception e) {
            cleanup();
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object retrieveImage(final String methodCall) throws Exception {
        try {
            if (m_driver == null) {
                throw new SeleniumViewException("Chrome driver was not initialized. Could not retrieve image.");
            }
            return m_driver.executeScript("return " + methodCall);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (e instanceof WebDriverException) {
                errorMessage = errorMessage.substring(0, errorMessage.indexOf('\n'));
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(errorMessage);
                    JsonNode errorNode = root.findValue("errorMessage");
                    if (errorNode != null) {
                        errorMessage = errorNode.asText();
                    }
                } catch (Exception e1) { /*do nothing*/ }
            }
            errorMessage = "Error retrieving image: " + errorMessage;
            throw new IOException(errorMessage, e);
        } finally {
            cleanup();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        if (m_userDataDir != null) {
            m_service.unlockUserDataDir(m_userDataDir, true);
            m_userDataDir = null;
        }
        if (m_driver != null) {
            m_driver.quit();
            m_driver = null;
        }
    }

}
