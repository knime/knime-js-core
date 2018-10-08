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

import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Optional;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.knime.core.node.NodeLogger;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MultiOSDriverActivator extends Plugin {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(MultiOSDriverActivator.class);

    private static String BUNDLE_NAME;
	private static String CHROME_DRIVER_PATH;

	// Optional path to distributed chromium binaries
    private static String CHROMIUM_PATH;

	/**
     * {@inheritDoc}
     */
    @Override
	public void start(final BundleContext bundleContext) throws Exception {
    	super.start(bundleContext);

    	BUNDLE_NAME = getBundle().getSymbolicName();

    	String os = Platform.getOS();
        String arch = Platform.getOSArch();

        Enumeration<URL> eDriver = null;
        Enumeration<URL> eChromium = null;
        if (Platform.OS_WIN32.equals(os)) {
            // 32 and 64bit Windows use the same 32bit executables
            eDriver = getBundle().findEntries("win32/x86", "chromedriver.exe", false);
            eChromium = getBundle().findEntries("win32/x86", "chrome.exe", false);
        } else if (Platform.OS_MACOSX.equals(os)) {
            eDriver = getBundle().findEntries(os + "/" + arch, "chromedriver", false);
            eChromium = getBundle().findEntries("macosx/x86_64/Chromium.app/Contents/MacOS", "Chromium", false);
        } else if (Platform.OS_LINUX.equals(os)) {
            eDriver = getBundle().findEntries(os + "/" + arch, "chromedriver", false);
            eChromium = getBundle().findEntries(os + "/" + arch, "chrome", false);
        }

        URL urlDriver = null;
        URL urlChromium = null;
        if ((eDriver != null) && eDriver.hasMoreElements()) {
            urlDriver = eDriver.nextElement();
        }
        if ((eChromium != null) && eChromium.hasMoreElements()) {
            urlChromium = eChromium.nextElement();
        }

        if (urlDriver != null) {
            urlDriver = FileLocator.toFileURL(urlDriver);
            System.setProperty("webdriver.chrome.driver", urlDriver.getFile());
            CHROME_DRIVER_PATH = urlDriver.getFile();
        }
        if (urlChromium != null) {
            urlChromium = FileLocator.toFileURL(urlChromium);
            CHROMIUM_PATH = urlChromium.getFile();
            if (Platform.OS_WIN32.equals(os) && CHROMIUM_PATH.startsWith("/")) {
                CHROMIUM_PATH = CHROMIUM_PATH.substring(1);
            }
            CHROMIUM_PATH = Paths.get(CHROMIUM_PATH).normalize().toString();
        }
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public void stop(final BundleContext bundleContext) throws Exception {
    	// close all possible started chrome windows
    	ChromeViewService.getInstance().shutdown();

    	CHROME_DRIVER_PATH = null;
    	CHROMIUM_PATH = null;

	}

    static Optional<String> getBundledChromeDriverPath() {
    	return Optional.ofNullable(CHROME_DRIVER_PATH);
    }

    static Optional<String> getChromiumPath() {
        return Optional.ofNullable(CHROMIUM_PATH);
    }

    static String getBundleName() {
    	return BUNDLE_NAME;
    }

}
