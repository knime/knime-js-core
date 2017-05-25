package org.knime.ext.seleniumdrivers.multios;

import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class MultiOSDriverActivator extends Plugin {

	private static String CHROME_DRIVER_PATH;
	
	/**
     * {@inheritDoc}
     */
    @Override
	public void start(BundleContext bundleContext) throws Exception {
    	super.start(bundleContext);
    	
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
	public void stop(BundleContext bundleContext) throws Exception {
    	CHROME_DRIVER_PATH = null;
	}
    
    
    static Optional<String> getBundledChromeDriverPath() {
    	return Optional.ofNullable(CHROME_DRIVER_PATH);
    }

}
