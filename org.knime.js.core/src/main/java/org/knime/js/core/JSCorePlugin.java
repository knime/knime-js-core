package org.knime.js.core;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.LoadMetadataRepositoryJob;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.dialog.util.DefaultConfigurationLayoutCreator;
import org.knime.core.node.wizard.util.DefaultLayoutCreator;
import org.knime.js.core.layout.DefaultConfigurationCreatorImpl;
import org.knime.js.core.layout.DefaultLayoutCreatorImpl;
import org.knime.js.core.preferences.JavaScriptPreferenceInitializer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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
 *   11 Nov 2016 (albrecht): created
 */

/**
 * Activator for the JS Core Plugin.
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public final class JSCorePlugin extends AbstractUIPlugin {

    /** Preference constant: browser to use for opening views. */
    public static final String P_VIEW_BROWSER = "js.core.viewBrowser";

    /** Preference constant: path to executable for chosen browser. */
    public static final String P_BROWSER_PATH = "js.core.browserPath";

    /** Preference constant: additional cli args for chosen browser. */
    public static final String P_BROWSER_CLI_ARGS = "js.core.browserCliArgs";

    /** Preference constant: headless browser to use for generating images. */
    public static final String P_HEADLESS_BROWSER = "js.core.headlessBrowser";

    /** Preference constant: path to executable for chosen headless browser. */
    public static final String P_HEADLESS_BROWSER_PATH = "js.core.headlessBrowserPath";

    /** Preference constant: additional cli args for chosen headless browser. */
    public static final String P_HEADLESS_BROWSER_CLI_ARGS = "js.core.headlessBrowserCliArgs";

    /** Preference constant: if a debug HTML is supposed to be created. */
    public static final String P_DEBUG_HTML = "js.core.createDebugHtml";

    /** Preference constant: show context menu entry for legacy Quickform execution. */
    public static final String P_SHOW_LEGACY_QUICKFORM_EXECUTION = "js.core.enableLegacyQuickformExecution";

    /**
     * If <code>true</code> (default <code>false</code>), user input/data in view/wizard page will be sanitized for
     * potential vulnerabilities before rendering in the browser. The default sanitization behavior can be overridden
     * with the additional sanitize properties. These additional properties have no effect if this property is
     * <code>false</code> or empty (default).
     *
     * @since 4.4 (AP-16130)
     */
    public static final String SYS_PROPERTY_SANITIZE_CLIENT_HTML = "js.core.sanitize.clientHTML";

    /**
     * The optional absolute path to a file containing new-line delimited node nodes (as seen in the node description;
     * e.g. "Table View")(one per line) to exclude from sanitization before being transferred to the client. Default is
     * empty; thereby sanitizing all node data.
     *
     * @since 4.4 (AP-16130)
     */
    public static final String SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH = "js.core.sanitize.allowNodesPath";

    /**
     * A comma separated list of valid HTML element tags which should be allowed in the sanitized data. Any non-empty
     * value overrides the default element policy defined by OWASP.
     *
     * @since 4.4 (AP-16130)
     */
    public static final String SYS_PROPERTY_SANITIZE_ALLOW_ELEMS = "js.core.sanitize.allowElements";

    /**
     * A comma separated list of valid HTML attribute tags which should be allowed in the sanitized data. Any non-empty
     * value overrides the default attribute policy defined by OWASP.
     *
     * @since 4.4 (AP-16130)
     */
    public static final String SYS_PROPERTY_SANITIZE_ALLOW_ATTRS = "js.core.sanitize.allowAttributes";

    /**
     * If <code>false</code> (default <code>true</code>), all CSS styles will be removed from the final HTML sent to the
     * client. Otherwise, CSS styles are allowed as defined by the default OWASP policy.
     *
     * @since 4.4 (AP-16130)
     */
    public static final String SYS_PROPERTY_SANITIZE_ALLOW_CSS = "js.core.sanitize.allowCSS";

    /**
     * If <code>true</code> (default <code>false</code>) the input data (input table and flow variables) for
     * the Generic JavaScript View will be sanitized according to the other set OWASP policy values.
     *
     * @since 5.2 (UIEXT-1518)
     */
    public static final String SYS_PROPERTY_SANITIZE_GENERIC_JS_VIEW = "js.core.sanitize.sanitizeGenericJSView";

    /**
     * Class name of a node view implementation using a specific browser.
     *
     * @since 4.4
     */
    public static final String INTERNAL_BROWSER = "org.knime.js.swt.wizardnodeview.WizardNodeView";

    /**
     * Class name of a node view implementation using a specific browser.
     *
     * @since 4.4
     */
    public static final String CHROMIUM_BROWSER = "org.knime.ext.seleniumdrivers.multios.ChromiumWizardNodeView";

    /**
     * Class name of a node view implementation using a specific browser.
     *
     * @since 4.4
     */
    public static final String CHROME_BROWSER = "org.knime.ext.seleniumdrivers.multios.ChromeWizardNodeView";

    /**
     * Class name of a node view implementation using a specific browser.
     *
     * @since 4.4
     */
    public static final String CEF_BROWSER = "org.knime.js.cef.wizardnodeview.CEFWizardNodeView";

    /**
     * Class name for a JS image generation implementation using a specific headless/windowless browser.
     *
     * @since 4.4
     */
    public static final String HEADLESS_CHROMIUM = "org.knime.ext.seleniumdrivers.multios.ChromiumImageGenerator";

    /**
     * Class name for a JS image generation implementation using a specific headless/windowless browser.
     *
     * @since 4.4
     */
    public static final String HEADLESS_PHANTOMJS = "org.knime.ext.phantomjs.PhantomJSImageGenerator";

    /**
     * Class name for a JS image generation implementation using a specific headless/windowless browser.
     *
     * @since 4.4
     */
    public static final String HEADLESS_CEF = "org.knime.js.cef.headless.CEFImageGenerator";


    // The shared instance.
    private static JSCorePlugin PLUGIN;

    private String m_pluginRootPath;
    private ServiceRegistration<?> m_defaultLayoutCreatorService;
    private ServiceRegistration<?> m_defaultConfigurationLayoutCreatorService;

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

        m_defaultConfigurationLayoutCreatorService = context.registerService(DefaultConfigurationLayoutCreator.class.getName(),
            new DefaultConfigurationCreatorImpl(), new Hashtable<String, String>());

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

    /**
     * Tests if the operating system which is currently running the application supports Chromium browsers,
     * esp. CentOS v < 7 and RHEL v < 7 are not supported anymore and might need special treatment
     * @return true if the os supports Chromium browsers, false otherwise
     */
    public static boolean osSupportsChromium() {
        if (Platform.OS_LINUX.equals(Platform.getOS())) {
            Pattern pattern = Pattern.compile("^\\s*[CentOS|Red Hat Enterprise Linux].*\\s[0-6]\\..*$",
                Pattern.CASE_INSENSITIVE);
            return !pattern.matcher(KNIMEConstants.getOSVariant()).matches();
        }
        return true;
    }

    /**
     * Checks whether the Chromium feature is already installed or not.
     *
     * @return {@code true} if the feature is already installed, {@code false} otherwise
     * @since 4.2
     */
    public static boolean isChromiumInstalled() {
        Bundle b = Platform.getBundle("org.knime.ext.seleniumdrivers.multios");
        if (b != null) {
            return Arrays.stream(Platform.getFragments(b)).map(Bundle::getSymbolicName)
                    .anyMatch(n -> n.startsWith("org.knime.ext.chromium.bin"));
        }
        return false;
    }

    /**
     * Checks whether the CEF plugin is installed
     *
     * @return {@code true} if installed
     * @since 4.5
     */
    public static boolean isCEFInstalled() {
        return Platform.getBundle("org.knime.js.cef") != null;
    }

    private static final String FEATURE_GROUP_SUFFIX = ".feature.group";

    private static final String CEF_FEATURE_NAME = "org.knime.features.browser.cef";

    /**
     * Installs the CEF extension.
     *
     * @since 4.5
     */
    public static void installCEFExtension() {
        final ProvisioningSession session = ProvisioningUI.getDefaultUI().getSession();

        try {
            final IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager)session
                .getProvisioningAgent().getService(IMetadataRepositoryManager.SERVICE_NAME);
            final Set<IInstallableUnit> featuresToInstall = new HashSet<>();

            for (URI uri : metadataManager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL)) {
                IMetadataRepository repo = metadataManager.loadRepository(uri, null);
                searchInRepository(repo, featuresToInstall);
            }

            if (featuresToInstall.isEmpty()) {
                Display.getDefault().syncExec(() -> MessageDialog.openWarning(Display.getDefault().getActiveShell(),
                    "No extension found",
                    "No extension with name '" + CEF_FEATURE_NAME + FEATURE_GROUP_SUFFIX + "' found."));
            } else {
                startInstallCEFExtension(featuresToInstall);
            }

        } catch (ProvisionException ex) {
            Display.getDefault()
                .syncExec(() -> MessageDialog.openWarning(Display.getDefault().getActiveShell(),
                    "Error while installing extension", "Error while installign extension '"
                        + CEF_FEATURE_NAME + FEATURE_GROUP_SUFFIX + "': " + ex.getMessage()));

            NodeLogger.getLogger(JavaScriptPreferenceInitializer.class.getName())
                .error("Error while installing extension '" + CEF_FEATURE_NAME + FEATURE_GROUP_SUFFIX
                    + "': " + ex.getMessage(), ex);
        }
    }

    /**
     * Searches the hub feature in the provided repository.
     *
     * @param repository the repository to search
     * @param featuresToInstall a set that will be filled with the features to be installed
     * @throws ProvisionException if an error occurs
     */
    private static void searchInRepository(final IMetadataRepository repository,
        final Set<IInstallableUnit> featuresToInstall) throws ProvisionException {
        final IQuery<IInstallableUnit> query = QueryUtil
            .createLatestQuery(QueryUtil.createIUQuery(CEF_FEATURE_NAME + FEATURE_GROUP_SUFFIX));
        final IQueryResult<IInstallableUnit> result = repository.query(query, null);

        result.forEach(i -> featuresToInstall.add(i));
    }

    /**
     * Starts installing the CEF feature.
     *
     * @param featuresToInstall the features that have to be installed.
     */
    private static void startInstallCEFExtension(final Set<IInstallableUnit> featuresToInstall) {
        final ProvisioningUI provUI = ProvisioningUI.getDefaultUI();
        Job.getJobManager().cancel(LoadMetadataRepositoryJob.LOAD_FAMILY);
        final LoadMetadataRepositoryJob loadJob = new LoadMetadataRepositoryJob(provUI);
        loadJob.setProperty(LoadMetadataRepositoryJob.ACCUMULATE_LOAD_ERRORS, Boolean.toString(true));

        loadJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(final IJobChangeEvent event) {
                if (PlatformUI.isWorkbenchRunning() && event.getResult().isOK()) {
                    Display.getDefault().asyncExec(() -> {
                        if (Display.getDefault().isDisposed()) {
                            NodeLogger.getLogger(JavaScriptPreferenceInitializer.class.getName())
                                .debug("Display disposed, aborting install action");
                            return;
                        }

                        provUI.getPolicy().setRepositoriesVisible(false);
                        provUI.openInstallWizard(featuresToInstall,
                            new InstallOperation(provUI.getSession(), featuresToInstall), loadJob);
                        provUI.getPolicy().setRepositoriesVisible(true);
                    });
                }
            }
        });

        loadJob.setUser(true);
        loadJob.schedule();
    }

}
