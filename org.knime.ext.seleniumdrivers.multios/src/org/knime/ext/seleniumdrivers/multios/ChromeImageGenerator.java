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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.util.FileUtil;
import org.knime.js.core.AbstractImageGenerator;
import org.knime.js.core.JSCorePlugin;
import org.knime.js.core.JavaScriptViewCreator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
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

    //the timeout to wait for the view to append an element to the body tag
    static final int VIEW_INIT_TIMEOUT = 60;

    private final ChromeViewService m_service;
    private ChromeDriver m_driver;
    private File m_repTempFile;
    private File m_valTempFile;
    private File m_userDataDir;

    /**
     * @param nodeModel
     */
    public ChromeImageGenerator(final T nodeModel) {
        super(nodeModel);
        m_service = ChromeViewService.getInstance();
        final REP viewRepresentation = nodeModel.getViewRepresentation();
        final VAL viewValue = nodeModel.getViewValue();
        final WizardViewCreator<REP, VAL> viewCreator = nodeModel.getViewCreator();
        writeTempViewFiles(viewRepresentation, viewValue, viewCreator);
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
        String os = Platform.getOS();
        Optional<String> chromeDriverPath = MultiOSDriverActivator.getBundledChromeDriverPath();
        if (!chromeDriverPath.isPresent()) {
            throw new SeleniumViewException("Path to Chrome driver could not be retrieved!");
        }
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--allow-file-access", "--allow-file-access-from-files");
            if (Platform.OS_MACOSX.equals(os) || Platform.OS_LINUX.equals(os)) {
                options.addArguments("--headless");
            } else {
                /*this will also disable GPU acceleration, but this is only needed on Windows as per
                Chromium bugs https://bugs.chromium.org/p/chromium/issues/detail?id=737678
                and https://bugs.chromium.org/p/chromium/issues/detail?id=729961*/
                options.setHeadless(true);
            }
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
                options.addArguments(cliOptions.split("\\s+"));
            }
            options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);

            m_driver = new ChromeDriver(options);
            m_driver.manage().timeouts().implicitlyWait(VIEW_INIT_TIMEOUT, TimeUnit.SECONDS)
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
        if (m_repTempFile == null || m_valTempFile == null) {
            throw new SeleniumViewException("One or more mandatory temporary view files not present. "
                + "View generation not possible.");
        }
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
            embedUtilFileInLoadedPage();

            WizardViewCreator<REP, VAL> viewCreator = model.getViewCreator();
            String repURL = m_repTempFile.toURI().toString();
            String valURL = m_valTempFile.toURI().toString();
            String initCall = viewCreator.wrapInTryCatch(viewCreator.createInitJSViewMethodCall(false, null, null));
            m_driver.executeScript("knimeImageUtil.loadView(arguments[0], arguments[1], arguments[2]);",
                repURL, valURL, initCall);
            if (exec != null) {
                exec.setProgress(0.66);
            }
            WebDriverWait wait = new WebDriverWait(m_driver, ChromeWizardNodeView.DEFAULT_TIMEOUT);
            //wait until any element has been appended to body, which is not the service header
            By anyNonKnimeElement = By.cssSelector("body > *:not(#knime-service-header)");
            wait.until(driver -> ExpectedConditions.presenceOfElementLocated(anyNonKnimeElement));
            //the wait seems to work unreliably, enforcing element present in implicit wait time
            m_driver.findElements(anyNonKnimeElement);

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
            m_driver.switchTo().frame(m_driver.findElementById("node-SINGLE"));
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
     * Writes view representation and value to disk as temporary JSON files. Also copies a JS utility file to
     * the temporary location.
     *
     * @param viewRepresentation the view representation instance to write to disk
     * @param viewValue the view value instance to write to disk
     * @param viewCreator the view creator instance used for the view files and method creation
     */
    private void writeTempViewFiles(final REP viewRepresentation, final VAL viewValue,
        final WizardViewCreator<REP, VAL> viewCreator) {
        // we can't pass data in directly, as Chromium seems to have a 2MB size limit for these calls
        // see https://bugs.chromium.org/p/chromedriver/issues/detail?id=1026
        // workaround is writing to disk and passing as URLs to be fetched by AJAX call
        String viewRepString = ((JavaScriptViewCreator<REP, VAL>)viewCreator)
                .getPageContentJSONString(viewRepresentation, viewValue);
        String viewValueString = viewCreator.getViewValueJSONString(viewValue);
        try {
            // force creation of temp directory, copy resources and create HTML stub and debug output
            getNodeModel().getViewHTMLPath();
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
            m_repTempFile = FileUtil.createTempFile("imageRep_" + System.currentTimeMillis() + "_", ".json",
                tempPath.toFile(), true);
            m_valTempFile = FileUtil.createTempFile("imageVal_" + System.currentTimeMillis() + "_", ".json",
                tempPath.toFile(), true);
            try (BufferedWriter writer = Files.newBufferedWriter(m_repTempFile.toPath(), Charset.forName("UTF-8"))) {
                writer.write(viewRepString);
                writer.flush();
            }
            try (BufferedWriter writer = Files.newBufferedWriter(m_valTempFile.toPath(), Charset.forName("UTF-8"))) {
                writer.write(viewValueString);
                writer.flush();
            }
        } catch (IOException e) {
            // handle exception further up
            throw new SeleniumViewException(e);
        }
    }

    private void embedUtilFileInLoadedPage() {
        if (m_driver == null) {
            return;
        }
        Path utilPath = null;
        try {
            URL utilURL = Platform.getBundle(MultiOSDriverActivator.getBundleName())
                    .getEntry("src-js/selenium-knime-image-util.js");
            String utilFile = FileLocator.toFileURL(utilURL).getFile();
            if (Platform.getOS().equals(Platform.OS_WIN32)
                    && (utilFile.startsWith("/") || utilFile.startsWith("\\"))) {
                utilFile = utilFile.substring(1);
            }
            utilPath = Paths.get(utilFile);
        } catch (Exception e) {
            throw new SeleniumViewException("Image generation failed. "
                + "Could not find selenium-knime-image-util.js: " + e.getMessage());
        }
        String embedScript = "";
        try {
            embedScript = new String(Files.readAllBytes(utilPath), "UTF-8");
        } catch (IOException ex) {
            throw new SeleniumViewException("Reading utility file for image generation failed: "
                    + ex.getMessage(), ex);
        }
        m_driver.executeScript(embedScript);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        tryDeleteTempFiles();
        try {
            if (m_driver != null) {
                m_driver.quit();
            }
            if (m_userDataDir != null) {
                m_service.unlockUserDataDir(m_userDataDir, true);
            }
        } catch (Throwable t) {
            /* continue, the browser might be unavailable or unresponsive */
            LOGGER.error("Could not shutdown headless Chromium browser. The process might still be "
                    + "existing in the system and require manual shutdown.", t);
            m_service.tryDeleteUserDataDir(m_userDataDir, true);
        } finally {
            m_driver = null;
            m_userDataDir = null;
        }
    }

    /**
     * Tries to delete current temporary files. Potential errors are ignored.
     */
    private void tryDeleteTempFiles() {
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
    }

}
