/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   16 Jun 2017 (albrecht): created
 */
package org.knime.ext.seleniumdrivers.multios;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Service class for Chrome View implementation.
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class ChromeViewService {

	private static ChromeViewService INSTANCE = new ChromeViewService();

	private Set<ChromeDriver> m_drivers = new HashSet<ChromeDriver>();

	static final String COMET_THREAD_NAME = "Chrome COMET query thread ";
	private ThreadGroup m_cometThreadGroup;

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

	private ChromeViewService() { /* hidden default constructor */ }

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
}
