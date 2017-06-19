package org.knime.ext.seleniumdrivers.multios;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.knime.core.node.NodeLogger;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeViewService {

	private static ChromeViewService INSTANCE = new ChromeViewService();
	private static NodeLogger LOGGER = NodeLogger.getLogger(ChromeViewService.class);

	private Set<ChromeDriver> m_drivers = new HashSet<ChromeDriver>();

	public static final String COMET_THREAD_NAME = "Chrome COMET query thread ";
	private ThreadGroup m_cometThreadGroup;

	// signals for COMET-type request queries (callback emulation)
	public static final String NO_ACTION = "NO_ACTION";
	public static final String CLOSE_BUTTON_PRESSED = "CLOSE_BUTTON_PRESSED";
	public static final String CLOSE_DISCARD_BUTTON_PRESSED = "CLOSE_DISCARD_BUTTON_PRESSED";
	public static final String CLOSE_APPLY_BUTTON_PRESSED = "CLOSE_APPLY_BUTTON_PRESSED";
	public static final String CLOSE_APPLY_DEFAULT_BUTTON_PRESSED = "CLOSE_APPLY_DEFAULT_BUTTON_PRESSED";
	public static final String RESET_BUTTON_PRESSED = "RESET_BUTTON_PRESSED";
	public static final String APPLY_BUTTON_PRESSED = "APPLY_BUTTON_PRESSED";
	public static final String APPLY_DEFAULT_BUTTON_PRESSED = "APPLY_DEFAULT_BUTTON_PRESSED";
	public static final String CLOSE_WINDOW = "CLOSE_WINDOW";

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

	private String openNewWindow(final String url, final String title, final int left, final int top, final int width, final int height) {
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

	public ThreadGroup getCometThreadGroup() {
		if (m_cometThreadGroup == null) {
			m_cometThreadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), COMET_THREAD_NAME);
		}
		return m_cometThreadGroup;
	}
}
