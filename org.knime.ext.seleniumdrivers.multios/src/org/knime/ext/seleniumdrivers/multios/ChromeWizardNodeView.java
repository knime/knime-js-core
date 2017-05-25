package org.knime.ext.seleniumdrivers.multios;

import java.io.File;
import java.util.Optional;

import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.core.node.wizard.WizardNode;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChromeWizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
		extends AbstractWizardNodeView<T, REP, VAL> {
	
	private static final long DEFAULT_TIMEOUT = 30;
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

	@Override
	protected void callOpenView(String title) {
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
		options.addArguments("--app=" + new File(viewPath).toURI().toString());
		m_driver = new ChromeDriver(options);
		waitForDocumentReady();
		//Store the current window handle
		String currentWindowHandle = m_driver.getWindowHandle();

		//run your javascript and alert code
		((JavascriptExecutor)m_driver).executeScript("alert('Close')"); 
		m_driver.switchTo().alert().accept();

		//Switch back to to the window using the handle saved earlier
		m_driver.switchTo().window(currentWindowHandle);
		REP viewRepresentation = model.getViewRepresentation();
		VAL viewValue = model.getViewValue();
		String initCall = model.getViewCreator().createInitJSViewMethodCall(viewRepresentation, viewValue);
		((JavascriptExecutor)m_driver).executeScript(initCall);
		//driver.navigate().to(new File(viewPath).toURI().toString());
	}
	
	private void waitForDocumentReady()
    {
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
    			String readyState = ((JavascriptExecutor)m_driver).executeScript(
    					"if (document.readyState) return document.readyState;").toString();
    			return "complete".equalsIgnoreCase(readyState);
    		}

    		@Override
    		public String toString() {
    			return "document ready state";
    		}
    	};
    }

}
