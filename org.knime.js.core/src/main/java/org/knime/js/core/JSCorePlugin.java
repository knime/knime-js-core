package org.knime.js.core;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.node.wizard.util.DefaultLayoutCreator;
import org.knime.js.core.layout.DefaultLayoutCreatorImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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
 *   11 Nov 2016 (albrecht): created
 */

/**
 * Activator for the JS Core Plugin.
 * @author Christian Albrecht, KNIME.com GmbH, Kosntanz, Germany
 */
public final class JSCorePlugin extends AbstractUIPlugin {

    /** Preference constant: browser to use for opening views. */
    public static final String P_VIEW_BROWSER = "js.core.viewBrowser";

    /** Preference constant: path to executable for chosen browser. */
    public static final String P_BROWSER_PATH = "js.core.browserPath";

    /** Preference constant: additional cli args for chosen browser. */
    public static final String P_BROWSER_CLI_ARGS = "js.core.browserCliArgs";

    /** Preference constant: if a debug HTML is supposed to be created. */
    public static final String P_DEBUG_HTML = "js.core.createDebugHtml";

    // The shared instance.
    private static JSCorePlugin PLUGIN;
    private String m_pluginRootPath;
    private ServiceRegistration<?> m_defaultLayoutCreatorService;

    /** Plugin constructor */
    public JSCorePlugin() {
        PLUGIN = this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        final URL pluginURL = FileLocator.resolve(FileLocator.find(PLUGIN.getBundle(), new Path(""), null));
        final File tmpFile = new File(pluginURL.getPath());
        m_pluginRootPath = tmpFile.getAbsolutePath();

        m_defaultLayoutCreatorService = context.registerService(DefaultLayoutCreator.class.getName(),
            new DefaultLayoutCreatorImpl(), new Hashtable<String, String>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        PLUGIN = null;
        context.ungetService(m_defaultLayoutCreatorService.getReference());

        super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return The shared instance
     */
    public static JSCorePlugin getDefault() {
        return PLUGIN;
    }

    /**
     * @return the absolute root path of this plugin
     */
    public String getPluginRootPath() {
        return m_pluginRootPath;
    }

}
