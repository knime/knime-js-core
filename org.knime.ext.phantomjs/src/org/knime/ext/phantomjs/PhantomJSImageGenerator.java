/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   22.09.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.ext.phantomjs;

import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.core.node.wizard.WizardNode;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 * @param <T> 
 * @param <REP> 
 * @param <VAL> 
 */
public class PhantomJSImageGenerator<T extends NodeModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent> 
    extends AbstractWizardNodeView<T, REP, VAL>{
    
    private static final long DEFAULT_TIMEOUT = 10;
    
    private final WebDriver m_driver;
    
    /**
     * Creates a new image generator object.<br>
     * The PhantomJS process is started if not present and the view is loaded and initialized.
     * @param nodeModel 
     * 
     */
    public PhantomJSImageGenerator(final T nodeModel) {
        super(nodeModel);
        m_driver = PhantomJSActivator.getConfiguredPhantomJSDriver();
        callOpenView(null);
    }
    
    /**
     * Executes a given JavaScript and returns the result, if available.<br>
     * See {@link JavascriptExecutor#executeScript(String, Object...) executeScript}.
     * @param script The JavaScript to execute
     * @param args The arguments to the script. May be empty
     * @return One of Boolean, Long, String, List or WebElement. Or null if script has no return value.
     */
    public Object executeScript(final String script, final Object... args) {
        if (m_driver instanceof JavascriptExecutor) {
            return ((JavascriptExecutor)m_driver).executeScript("return " + script, args);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void closeView() {
        // do nothing        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        // do nothing        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void callOpenView(final String title) {
        //TODO make size editable
        m_driver.manage().window().setPosition(new Point(20, 20));
        m_driver.manage().window().setSize(new Dimension(800, 600));
        m_driver.navigate().to(getViewSource().toURI().toString());
        waitForDocumentReady();
        ((JavascriptExecutor)m_driver).executeScript(createInitJSViewMethodCall());
        WebDriverWait wait = new WebDriverWait(m_driver, DEFAULT_TIMEOUT);
        //TODO wait until what?
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("layoutContainer")));
    }
    
    private void waitForDocumentReady()
    {
        WebDriverWait wait = new WebDriverWait(m_driver, DEFAULT_TIMEOUT);
        if (!(m_driver instanceof JavascriptExecutor)) {
            throw new IllegalArgumentException("Driver must support javascript execution");
        }
        wait.until(documentReady());
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
