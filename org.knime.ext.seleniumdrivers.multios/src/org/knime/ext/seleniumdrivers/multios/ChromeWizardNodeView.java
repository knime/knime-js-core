package org.knime.ext.seleniumdrivers.multios;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.util.FileUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChromeWizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
		extends AbstractWizardNodeView<T, REP, VAL> {
	
	private static NodeLogger LOGGER = NodeLogger.getLogger(ChromeWizardNodeView.class);
	
	private static final long DEFAULT_TIMEOUT = 30;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private ChromeDriver m_driver;

	public ChromeWizardNodeView(T viewableModel) {
		super(viewableModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void closeView() {
		// TODO Auto-generated method stub

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
		Optional<String> chromeDriverPath = MultiOSDriverActivator.getBundledChromeDriverPath();
		if (!chromeDriverPath.isPresent()) {
			return;
		}
		T model = getViewableModel();
		String viewPath = model.getViewHTMLPath();
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		//capabilities.setCapability(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, chromeDriverPath.get());
		//capabilities.setCapability(ChromeDriverService., chromeDriverPath.get());
		capabilities.setJavascriptEnabled(true);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--app=" + new File(viewPath).toURI().toString() /*"about:blank"*/);
		options.addArguments("--window-size=" + WIDTH + "," + HEIGHT);
		options.addArguments("--allow-file-access", "--allow-file-access-from-files");
		if (knimeWindowBounds != null) {
			int x = (knimeWindowBounds.width / 2) - (WIDTH / 2) + knimeWindowBounds.x;
			int y = (knimeWindowBounds.height / 2) - (HEIGHT / 2) + knimeWindowBounds.y;
			options.addArguments("--window-position=" + x + "," + y);
		}
		m_driver = new ChromeDriver(options);
		m_driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS).setScriptTimeout(10, TimeUnit.SECONDS);
		//m_driver.navigate().to(new File(viewPath).toURI().toString());
		//m_driver.executeScript("window.location.assign(\"" + new File(viewPath).toURI().toString() + "\");");
		waitForDocumentReady();
		//Store the current window handle
		String currentWindowHandle = m_driver.getWindowHandle();

		//run your javascript and alert code
		((JavascriptExecutor)m_driver).executeScript("alert('View created')"); 
		m_driver.switchTo().alert().accept();

		//Switch back to to the window using the handle saved earlier
		m_driver.switchTo().window(currentWindowHandle);
		REP viewRepresentation = model.getViewRepresentation();
		VAL viewValue = model.getViewValue();
		WizardViewCreator<REP, VAL> viewCreator = model.getViewCreator();
		
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
			File repTempFile = FileUtil.createTempFile("rep_" + System.currentTimeMillis(), ".json", viewCreator.getCurrentLocation().toFile(), true);
			File valTempFile = FileUtil.createTempFile("val_" + System.currentTimeMillis(), ".json", viewCreator.getCurrentLocation().toFile(), true);
			try (BufferedWriter writer = Files.newBufferedWriter(repTempFile.toPath(), Charset.forName("UTF-8"))) {
				writer.write(viewRepString);
			}
			try (BufferedWriter writer = Files.newBufferedWriter(valTempFile.toPath(), Charset.forName("UTF-8"))) {
				writer.write(viewValueString);
			}
			String initCall = viewCreator.wrapInTryCatch(viewCreator.createInitJSViewMethodCall(false, null, null));
			m_driver.executeScript(loadAndParseArgumentsCode(), repTempFile.toURI().toString(), valTempFile.toURI().toString(), initCall);
		} catch (IOException e) {
			// handle exception further up
			throw new RuntimeException(e);
		}
		//driver.navigate().to(new File(viewPath).toURI().toString());
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

}
