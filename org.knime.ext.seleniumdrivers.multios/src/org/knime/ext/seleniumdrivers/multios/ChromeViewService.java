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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.FileUtil;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Service class for Chrome View implementation.
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class ChromeViewService {

    static final int IMAGE_GENERATION_POOL_SIZE = 10;
    static final String COMET_THREAD_NAME = "Chrome COMET query thread ";

	// signals for COMET-type request queries (callback emulation)
	static final String NO_ACTION = "NO_ACTION";
	static final String CLOSE_BUTTON_PRESSED = "CLOSE_BUTTON_PRESSED";
	static final String CLOSE_DISCARD_BUTTON_PRESSED = "CLOSE_DISCARD_BUTTON_PRESSED";
	static final String CLOSE_APPLY_BUTTON_PRESSED = "CLOSE_APPLY_BUTTON_PRESSED";
	static final String CLOSE_APPLY_DEFAULT_BUTTON_PRESSED = "CLOSE_APPLY_DEFAULT_BUTTON_PRESSED";
	static final String RESET_BUTTON_PRESSED = "RESET_BUTTON_PRESSED";
	static final String APPLY_BUTTON_PRESSED = "APPLY_BUTTON_PRESSED";
	static final String APPLY_DEFAULT_BUTTON_PRESSED = "APPLY_DEFAULT_BUTTON_PRESSED";
	static final String CLOSE_WINDOW = "CLOSE_WINDOW";

	private static final ChromeViewService INSTANCE = new ChromeViewService();
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ChromeViewService.class);

    private final Set<ChromeDriver> m_drivers = new HashSet<ChromeDriver>();
    private ThreadGroup m_cometThreadGroup;
	private final Map<File, AtomicBoolean> m_userDirMap;
	private final Semaphore m_imageGenerationCounter;

	/* hidden default constructor */
	private ChromeViewService() {
	    m_userDirMap = new ConcurrentHashMap<File, AtomicBoolean>();
	    m_imageGenerationCounter = new Semaphore(IMAGE_GENERATION_POOL_SIZE, true);
	}

	static ChromeViewService getInstance() {
		return INSTANCE;
	}

	boolean registerDriver(final ChromeDriver driver) {
		return m_drivers.add(driver);
	}

	boolean unregisterDriver(final ChromeDriver driver) {
		return m_drivers.remove(driver);
	}

	void shutdown() {
		for (Iterator<ChromeDriver> iterator = m_drivers.iterator(); iterator.hasNext();) {
			ChromeDriver driver = iterator.next();
			try {
				driver.quit();
			} catch (Exception e) {}
			iterator.remove();
		}
	}

	/**
	 * Creates a JavaScript call to open a new browser window.
	 * @param url the url to open
	 * @param title the title of the new window
	 * @param left the left position of the new window
	 * @param top the top position of the new window
	 * @param width the width of the new window
	 * @param height the height of the new window
	 * @return a string containing the function call to open a new window
	 */
	public String openNewWindow(final String url, final String title, final int left, final int top, final int width, final int height) {
		//for options see https://developer.mozilla.org/en-US/docs/Web/API/Window/open
		StringBuilder builder = new StringBuilder();
		builder.append("window.open('");
		builder.append(url);
		builder.append("', '");
		builder.append(title);
		builder.append("', '");
		builder.append("left=" + left);
		builder.append(",top=" + top);
		builder.append(",width=" + width);
		builder.append(",height=" + height);
		builder.append(",menubar=no");
		builder.append(",toolbar=no");
		builder.append(",location=no");
		builder.append(",personalbar=no");
		builder.append(",status=no");
		builder.append(",resizable=yes");
		builder.append(",scrollbars=yes");
		builder.append(",chrome=yes");
		builder.append("');");
		return builder.toString();
	}

	/**
	 * Retrieves the thread group for Selenium comet-style threads. Lazily creates if not existent.
	 * @return the thread group
	 */
	public ThreadGroup getCometThreadGroup() {
		if (m_cometThreadGroup == null) {
			m_cometThreadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), COMET_THREAD_NAME);
		}
		return m_cometThreadGroup;
	}

    Optional<String> tryRetrieveMissingSystemLibraries(final Optional<String> cPath) {
        String os = Platform.getOS();
        String command = null;
        if (Platform.OS_MACOSX.equals(os)) {
            command = "otool -L ";
        } else if (Platform.OS_LINUX.equals(os)) {
            command = "ldd -r ";
        }
        if (!cPath.isPresent() || command == null) {
            return Optional.empty();
        }
        StringBuilder errorBuilder = new StringBuilder();
        try {
            Process ldd = Runtime.getRuntime().exec(command + cPath.get());
            InputStream in = ldd.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("not found")) {
                        errorBuilder.append(line);
                    }
                }
            }
            if (errorBuilder.length() > 0) {
                return Optional.ofNullable(errorBuilder.toString());
            }
        } catch (Exception ex) { /* do nothing */ }
        return Optional.empty();
    }

    File getAndLockUserDataDir() throws IOException, InterruptedException {
	    return getAndLockUserDataDir(false);
	}

	File getAndLockUserDataDir(final boolean imageGeneration) throws IOException, InterruptedException {
	    if (imageGeneration) {
	        // only allow a maximum number of folders for image generation
	        m_imageGenerationCounter.acquire();
            LOGGER.debug("Acquiring Chromium image generation instance (" + m_imageGenerationCounter.availablePermits()
                + " left available of " + IMAGE_GENERATION_POOL_SIZE + ").");
	    }
	    // try recycling an available existing directory
	    for (Entry<File, AtomicBoolean> entry : m_userDirMap.entrySet()) {
	        if (entry.getValue().compareAndSet(false, true)) {
	            return entry.getKey();
	        }
	    }

	    // otherwise create new directory
	    File dir = createUserDataDir();
	    m_userDirMap.put(dir, new AtomicBoolean(true));
	    return dir;
	}

	void unlockUserDataDir(final File dir) {
	    unlockUserDataDir(dir, false);
	}

	void unlockUserDataDir(final File dir, final boolean imageGeneration) {
	    if (m_userDirMap.containsKey(dir)) {
	        m_userDirMap.get(dir).set(false);
	    }
	    if (imageGeneration) {
	        m_imageGenerationCounter.release();
	        LOGGER.debug("Releasing Chromium image generation instance (" + m_imageGenerationCounter.availablePermits()
	            + " left available of " + IMAGE_GENERATION_POOL_SIZE + ").");
	    }
	}

	private static File createUserDataDir() throws IOException {
        /* Make sure that bundled Chromium instances us a different user directory and profile, than
        other potentially installed Chrome/Chromium applications. */
	    //TODO: limit maximum? intermediate cleanup? don't use regular temp dir?
	    return FileUtil.createTempDir("knime_chromium_data");
    }
}
