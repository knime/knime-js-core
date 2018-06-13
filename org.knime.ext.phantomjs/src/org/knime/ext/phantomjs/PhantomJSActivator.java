/*
 * ------------------------------------------------------------------------
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
 *   03.02.2012 (meinl): created
 */
package org.knime.ext.phantomjs;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.osgi.framework.BundleContext;

/**
 * Activator for the PhantomJS Selenium Driver plugin that looks up a bundled PhantomJS executable.
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
public class PhantomJSActivator extends Plugin {
    private static String m_phantomJSPath;
    private static PhantomJSDriver m_driver;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        String os = Platform.getOS();
        String arch = Platform.getOSArch();


        Enumeration<URL> e;
        if (Platform.OS_WIN32.equals(os)) {
            // 32 and 64bit Windows use the same 32bit executables
            e = getBundle().findEntries("win32/x86", "phantomjs.exe", false);
        } else {
            e = getBundle().findEntries(os + "/" + arch, "phantomjs", false);
        }

        URL url = null;
        if ((e != null) && e.hasMoreElements()) {
            url = e.nextElement();
        }

        if (url != null) {
            url = FileLocator.toFileURL(url);
            m_phantomJSPath = url.getFile();
        }
        
        //createPhantomJSDriver();
    }

    private static void createPhantomJSDriver() throws IOException {
        if (m_phantomJSPath != null) {
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, m_phantomJSPath);
            capabilities.setJavascriptEnabled(true);
            String [] phantomJsArgs = {"--ignore-ssl-errors=yes"};
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomJsArgs);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "localToRemoteUrlAccessEnabled", true);
            m_driver = new PhantomJSDriver(capabilities);
        } else {
        	throw new IOException("Could not find PhantomJS installation, please check your installation.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        
        if (m_driver != null) {
            m_driver.quit();
        }
    }

    /**
     * Returns the path to the bundled PhantomJS executable.
     *
     * @return the path to the PhantomJS executable, or <code>null</code> if it
     *         could not be found
     */
    public static String getBundledPhantomJSPath() {
        return m_phantomJSPath;
    }
    
    /**
     * Returns a {@link PhantomJSDriver} with the correct executable set.
     * @return a correctly configured PhantomJSDriver, or <code>null</code> if the 
     * executable could not be found
     * @throws IOException if an error occurs while creating the PhantomJS instance
     */
    public static PhantomJSDriver getConfiguredPhantomJSDriver() throws IOException {
        if (m_driver == null) {
            createPhantomJSDriver();
        }
        return m_driver;
    }
}
