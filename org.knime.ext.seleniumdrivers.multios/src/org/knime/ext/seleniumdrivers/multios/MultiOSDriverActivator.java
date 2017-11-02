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
import java.util.Enumeration;
import java.util.Optional;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class MultiOSDriverActivator extends Plugin {

	private static String BUNDLE_NAME;
	private static String CHROME_DRIVER_PATH;

	/**
     * {@inheritDoc}
     */
    @Override
	public void start(final BundleContext bundleContext) throws Exception {
    	super.start(bundleContext);

    	BUNDLE_NAME = getBundle().getSymbolicName();

    	String os = Platform.getOS();
        String arch = Platform.getOSArch();


        Enumeration<URL> e;
        if (Platform.OS_WIN32.equals(os)) {
            // 32 and 64bit Windows use the same 32bit executables
            e = getBundle().findEntries("win32/x86", "chromedriver.exe", false);
        } else {
            e = getBundle().findEntries(os + "/" + arch, "chromedriver", false);
        }

        URL url = null;
        if ((e != null) && e.hasMoreElements()) {
            url = e.nextElement();
        }

        if (url != null) {
            url = FileLocator.toFileURL(url);
            System.setProperty("webdriver.chrome.driver", url.getFile());
            CHROME_DRIVER_PATH = url.getFile();
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

	}


    static Optional<String> getBundledChromeDriverPath() {
    	return Optional.ofNullable(CHROME_DRIVER_PATH);
    }

    static String getBundleName() {
    	return BUNDLE_NAME;
    }

}
