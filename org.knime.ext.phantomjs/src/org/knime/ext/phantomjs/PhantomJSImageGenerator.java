/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   22.09.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.ext.phantomjs;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 * @param <T> 
 * @param <REP> 
 * @param <VAL> 
 */
public class PhantomJSImageGenerator<T extends NodeModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent> {
    
	/** 
	 * Global lock object to synchronize view generation and subsequent operations. 
	 */
    public static final Object VIEW_GENERATION_LOCK = new Object();
    
	private static final NodeLogger LOGGER = NodeLogger.getLogger(PhantomJSImageGenerator.class); 
    private static final long DEFAULT_TIMEOUT = 30;
    
    private final WebDriver m_driver;
    private final T m_nodeModel;
    
    /**
     * Creates a new image generator object.<br>
     * The PhantomJS process is started if not present and the view is loaded and initialized.
     * @param nodeModel The node model.
     * @param waitForView If view executes animations after 
     * initialization it might be sensible to wait for a specific time.
     * @param exec An execution context used for progress reporting.
     * 
     */
    public PhantomJSImageGenerator(final T nodeModel, final Long waitForView, final ExecutionContext exec) throws Exception {
        m_nodeModel = nodeModel;
        if (exec != null) {
        	exec.setProgress("Starting PhantomJS");
        }
        m_driver = PhantomJSActivator.getConfiguredPhantomJSDriver();
        if (exec != null) {
        	exec.setProgress(0.25);
        }
        generateView(waitForView, exec);
    }
    
    /**
     * Creates a new image generator object.<br>
     * The PhantomJS process is started if not present and the view is loaded and initialized.
     * @param nodeModel The node model.
     * 
     */
    public PhantomJSImageGenerator(final T nodeModel) throws Exception {
    	this(nodeModel, null, null);
    }
    
    /**
     * Executes a given JavaScript and returns the result, if available.<br>
     * See {@link JavascriptExecutor#executeScript(String, Object...) executeScript}.
     * @param script The JavaScript to execute
     * @param args The arguments to the script. May be empty
     * @return One of Boolean, Long, String, List or WebElement. Or null if script has no return value.
     * @throws IOException on script execution exception
     */
    public Object executeScript(final String script, final Object... args) throws IOException {
        if (m_driver instanceof JavascriptExecutor) {
            try {
                return ((JavascriptExecutor)m_driver).executeScript("return " + script, args);
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
                errorMessage = "Error executing JavaScript: " + errorMessage;
                throw new IOException(errorMessage, e);
            }
        }
        return null;
    }

    private void generateView(final Long optionalWait, final ExecutionContext exec) {
        //TODO make size editable
        /*Window window = m_driver.manage().window();
        window.setPosition(new Point(20, 20));
        window.setSize(new Dimension(800, 600));*/
        if (exec != null) {
        	exec.setProgress("Initializing view");
        }
        String viewPath = m_nodeModel.getViewHTMLPath();
        if (viewPath == null || viewPath.isEmpty()) {
        	LOGGER.error("Node model returned no path to view HTML. Cannot initialize view.");
        	return;
        }
        m_driver.navigate().to(new File(viewPath).toURI().toString());
        waitForDocumentReady();
        REP viewRepresentation = m_nodeModel.getViewRepresentation();
        VAL viewValue = m_nodeModel.getViewValue();
        String initCall = m_nodeModel.getViewCreator().createInitJSViewMethodCall(viewRepresentation, viewValue);
        ((JavascriptExecutor)m_driver).executeScript(initCall);
        if (exec != null) {
        	exec.setProgress(0.66);
        }
        WebDriverWait wait = new WebDriverWait(m_driver, DEFAULT_TIMEOUT);
        //TODO wait until what?
        wait.until(driver -> ExpectedConditions.presenceOfElementLocated(By.xpath("//body[./* or ./text()]")));
        //wait.until(ExpectedConditions.presenceOfElementLocated(By.id("layoutContainer")));
        
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
        			.withTimeout(optionalWait, TimeUnit.MILLISECONDS)
        			.pollingEvery(1, TimeUnit.SECONDS)
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
