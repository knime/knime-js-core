package org.knime.ext.seleniumdrivers.multios;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.NodeLogger;
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

public class ChromeWizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
		extends AbstractWizardNodeView<T, REP, VAL> {
	
	private static NodeLogger LOGGER = NodeLogger.getLogger(ChromeWizardNodeView.class);
	
	private static final long DEFAULT_TIMEOUT = 30;
	private static final int DEFAULT_WIDTH = 1024;
	private static final int DEFAULT_HEIGHT = 768;
	
	private final Object LOCK = new Object();
	
	private final ChromeViewService m_service;
	private ChromeDriver m_driver;
	private Thread m_cometThread;
	private AtomicBoolean m_shutdownCometThread = new AtomicBoolean(false);
	private File m_repTempFile;
	private File m_valTempFile;

	public ChromeWizardNodeView(T viewableModel) {
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
		// TODO Auto-generated method stub

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
		
		writeTempViewFiles(viewRepresentation, viewValue, viewCreator);
		try {
			URL bridgeURL = Platform.getBundle(MultiOSDriverActivator.getBundleName()).getEntry("src-js/selenium-knime-bridge.js");
			Path bridgePath = Paths.get(FileLocator.toFileURL(bridgeURL).getFile());
			try (BufferedReader reader = Files.newBufferedReader(bridgePath, Charset.forName("UTF-8"))) {

			}
		} catch (Exception e) {
			//TODO
		}

		String handle = initDriver(new File(viewPath).toURI().toString(), title, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		m_driver.executeScript(signalBrowserWindowCloseCode());
		String initCall = viewCreator.wrapInTryCatch(viewCreator.createInitJSViewMethodCall(false, null, null));
		m_driver.executeScript(loadAndParseArgumentsCode(), m_repTempFile.toURI().toString(), m_valTempFile.toURI().toString(), initCall);
		m_driver.switchTo().window(handle);
		initializeCometQuery();
	}
    
    private String initDriver(final String url, final String viewTitle, final int left, final int top, final int width, final int height) {
		Optional<String> chromeDriverPath = MultiOSDriverActivator.getBundledChromeDriverPath();
		if (!chromeDriverPath.isPresent()) {
			//TODO throw error
			return null;
		}
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--app=" + url);
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
	
    private void writeTempViewFiles(REP viewRepresentation, VAL viewValue, WizardViewCreator<REP, VAL> viewCreator) {
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
			throw new RuntimeException(e);
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
	
	private void testAlive() {
		if (m_driver != null) {
			try {
				//this leads to exception if the browser was closed by user
				m_driver.getWindowHandles();
			} catch (WebDriverException e) {
				m_shutdownCometThread.set(true);
				m_driver.quit();
				m_driver = null;
			}
		}
	}
	
	private void initializeCometQuery() {
		String handle = m_driver.getWindowHandle();
		m_cometThread = new Thread(m_service.getCometThreadGroup(), new CometRunnable(handle), ChromeViewService.COMET_THREAD_NAME + handle);
		m_cometThread.start();
	}
	
	private class CometRunnable implements Runnable {
		
		private String m_handle;
		
		CometRunnable(String windowHandle) {
			m_handle = windowHandle;
		}

		@Override
		public void run() {
			int cometTimeout = 3;
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
						String testScript = "window.signal = arguments[arguments.length - 1]; window.setTimeout(function() {signal('" + ChromeViewService.NO_ACTION + "');}, arguments[0] * 1000);";
						if (m_driver.getWindowHandle().equals(m_handle)) {
							String action = (String) m_driver.executeAsyncScript(testScript, cometTimeout);
							LOGGER.debug("COMET returned " + action);
							if (ChromeViewService.CLOSE_BUTTON_PRESSED.equals(action)) {
								m_driver.quit();
								break;
							}
						}
					} catch (WebDriverException wde) {
						if (wde instanceof UnhandledAlertException) {
							// alerts will let the execute call fail, need to explicitly wait
							try {
								Thread.sleep(cometTimeout * 1000);
							} catch (InterruptedException e) { /* do nothing */ }
						}
						/* do nothing */
						LOGGER.warn(wde.getMessage());
					}
				}
			}
		}
	}
}
