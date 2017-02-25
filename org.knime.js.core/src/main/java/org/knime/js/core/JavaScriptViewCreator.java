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
 *   22.09.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.web.WebResourceLocator;
import org.knime.core.node.web.WebTemplate;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.node.workflow.WizardExecutionController;
import org.knime.core.util.FileUtil;
import org.osgi.framework.Bundle;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @param <REP> the {@link WebViewContent} implementation used as view representation
 * @param <VAL> the {@link WebViewContent} implementation used as view value
 */
public class JavaScriptViewCreator<REP extends WebViewContent, VAL extends WebViewContent> implements
        WizardViewCreator<REP, VAL> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(JavaScriptViewCreator.class);

    private static File tempFolder;

    private File m_tempIndexFile;

    private WebTemplate m_template;

    private String m_title;

    /**
     * @return true if is running in debug mode, false otherwise
     */
    protected static boolean isDebug() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        if (runtimeBean != null && runtimeBean.getInputArguments() != null) {
            String inputArguments = runtimeBean.getInputArguments().toString();
            if (inputArguments != null && !inputArguments.isEmpty()) {
                return inputArguments.indexOf("jdwp") >= 0;
            }
        }
        return false;
    }

    private boolean viewTempDirExists() {
        return !isDebug() && tempFolder != null && tempFolder.exists() && tempFolder.isDirectory();
    }

    /**
     * Creates a new creator instance without deriving the view from a JavaScriptComponent definition.
     * Implementations using this constructor must set their own {@link WebTemplate}.
     */
    protected JavaScriptViewCreator() {
        this(null);
    }

    /**
     * Creates a new creator instance from a given JavaScriptComponent id.
     * @param javascriptObjectID The id of the JavaScriptComponent defined in extension point.
     */
    public JavaScriptViewCreator(final String javascriptObjectID) {
        if (javascriptObjectID != null) {
            m_template = WizardExecutionController.getWebTemplateFromJSObjectID(javascriptObjectID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTemplate getWebTemplate() {
        return m_template;
    }

    /**
     * Sets a custom web template, if the view is not derived from a JavaScript object ID
     *
     * @param template the template to set
     */
    protected void setWebTemplate(final WebTemplate template) {
        m_template = template;
    }

    /**
     * Creates all web resources, returns path to the created HTML file which contains the JS view.
     *
     * @param viewRepresentation the view representation
     * @param viewValue the view value
     * @param viewTitle the view title
     * @return the path to the view HTML
     * @throws IOException on IO error
     */
    @Override
    public String createWebResources(final String viewTitle,
            final REP viewRepresentation, final VAL viewValue) throws IOException {
        m_title = viewTitle == null ? "KNIME view" : viewTitle;
        if (!viewTempDirExists()) {
            File tempDir = null;
            String tempPath = System.getProperty("java.io.tmpdir");
            if (tempPath != null) {
                tempDir = new File(tempPath);
            }
            tempFolder = FileUtil.createTempDir("knimeViewContainer", tempDir, true);
            try {
                copyWebResources();
            } catch (IOException e) {
                deleteTempFile(tempFolder);
                tempFolder = null;
                throw e;
            }
        }
        m_tempIndexFile = FileUtil.createTempFile("index_" + System.currentTimeMillis(), ".html", tempFolder, true);
        BufferedWriter writer = new BufferedWriter(new FileWriter(m_tempIndexFile));
        writer.write(buildHTMLResource(viewRepresentation, viewValue));
        writer.flush();
        writer.close();
        return m_tempIndexFile.getAbsolutePath();
    }

    private String getViewRepresentationJSONString(final REP rep) {
        try {
            if (rep != null) {
                return ((ByteArrayOutputStream)rep.saveToStream()).toString("UTF-8");
            } else {
                return "null";
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("No view representation available!", e);
        }

    }

    private String getViewValueJSONString(final VAL val) {
        try {
            if (val != null) {
                return ((ByteArrayOutputStream)val.saveToStream()).toString("UTF-8");
            } else {
                return "null";
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("No view value available!", e);
        }
    }

    private void copyWebResources() throws IOException {
        for (Entry<File, String> copyEntry : getAllWebResources().entrySet()) {
            File src = copyEntry.getKey();
            File dest = new File(tempFolder, FilenameUtils.separatorsToSystem(copyEntry.getValue()));
            if (src.isDirectory()) {
                FileUtils.copyDirectory(src, dest);
            } else {
                FileUtils.copyFile(src, dest);
            }
        }
    }

    /**
     * Creates the HTML string representing the view page
     *
     * @param viewRepresentation the view representation
     * @param viewValue the view value
     * @return an HTML string representing the view page
     * @throws IOException if the debug file cannot be written
     */
    protected String buildHTMLResource(final REP viewRepresentation, final VAL viewValue)
            throws IOException {

        String setIEVersion = "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">";
        String inlineScript = "<script type=\"text/javascript\" charset=\"UTF-8\">%s</script>";
        /* String debugScript = "<script type=\"text/javascript\" "
                + "src=\"https://getfirebug.com/firebug-lite.js#startOpened=true\"></script>"; */
        String scriptString = "<script type=\"text/javascript\" src=\"%s\" charset=\"UTF-8\"></script>";
        String cssString = "<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\">";

        StringBuilder pageBuilder = new StringBuilder();
        pageBuilder.append("<!doctype html><html lang=\"en-US\"><head>");
        pageBuilder.append("<meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">");
        pageBuilder.append("<meta charset=\"UTF-8\">");
        pageBuilder.append("<title>" + m_title + "</title>");
        pageBuilder.append(setIEVersion);
        //pageBuilder.append(String.format(inlineScript, "BASE_DIR = " + m_tempFolder));
        //pageBuilder.append(debugScript);

        String bodyText = "";
        if (m_template == null || m_template.getWebResources() == null || m_template.getWebResources().length < 1) {
            bodyText = "ERROR: No view implementation available!";
            LOGGER.error("No JavaScript view implementation available for view: " + m_title);
        }

        for (WebResourceLocator resFile : getResourceFileList()) {
            String path = resFile.getRelativePathTarget();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            switch (resFile.getType()) {
                case CSS:
                    pageBuilder.append(String.format(cssString, path));
                    break;
                case JAVASCRIPT:
                    pageBuilder.append(String.format(scriptString, path));
                    break;
                case FILE:
                    break;
                default:
                    LOGGER.error("Unrecognized resource type " + resFile.getType());
            }
        }
        if (isDebug()) {
            String loadScript = "function loadWizardNodeView(){%s};";
            loadScript =
                String.format(loadScript, wrapInTryCatch(createInitJSViewMethodCall(viewRepresentation, viewValue)));
            StringBuilder debugBuilder = new StringBuilder(pageBuilder.toString());
            debugBuilder.append(String.format(inlineScript, loadScript));
            debugBuilder.append("</head><body" + " onload=\"loadWizardNodeView();\"" + ">");
            debugBuilder.append(bodyText);
            debugBuilder.append("</body></html>");
            File debugFile = FileUtil.createTempFile("debug_" + System.currentTimeMillis(), ".html", tempFolder, true);
            BufferedWriter writer = new BufferedWriter(new FileWriter(debugFile));
            writer.write(debugBuilder.toString());
            writer.flush();
            writer.close();
            LOGGER.info("JavaScript view - " + m_title + " - created. Debug output at: " + debugFile.getAbsolutePath());
        }
        pageBuilder.append("</head><body>");
        pageBuilder.append(bodyText);
        pageBuilder.append("</body></html>");
        return pageBuilder.toString();
    }

    /**
     * Wraps a JavaScript code block in a try/catch block.
     * In the catch block an alert with the error message and stack trace is shown.
     * @param jsCode The code block to wrap.
     * @return The resulting JavaScript as string.
     */
    @Override
    public String wrapInTryCatch(final String jsCode) {
        StringBuilder builder = new StringBuilder();
        builder.append("try {");
        builder.append(jsCode);
        builder.append("} catch(err) {if (err.stack) {alert(err.stack);} else {alert (err);}}");
        return builder.toString();
    }

    /**
     * Creates the JavaScript code to initialize the view implementation with the respective
     * view representation and value objects.
     *
     * @param viewRepresentation The view representation.
     * @param viewValue The view value.
     * @return The JavaScript code to initialize the view.
     */
    @Override
    public String createInitJSViewMethodCall(final REP viewRepresentation, final VAL viewValue) {
        String jsonViewRepresentation = getViewRepresentationJSONString(viewRepresentation);
        String jsonViewValue = getViewValueJSONString(viewValue);
        StringBuilder builder = new StringBuilder();
        String escapedRepresentation = jsonViewRepresentation.replace("\\", "\\\\").replace("'", "\\'");
        String escapedValue = jsonViewValue.replace("\\", "\\\\").replace("'", "\\'");
        String repParseCall = "var parsedRepresentation = JSON.parse('" + escapedRepresentation + "');";
        builder.append(repParseCall);
        String valParseCall = "var parsedValue = JSON.parse('" + escapedValue + "');";
        builder.append(valParseCall);
        String initMethod = m_template.getInitMethodName();
        String initCall = getNamespacePrefix() + initMethod + "(parsedRepresentation, parsedValue);";
        builder.append(initCall);
        return builder.toString();
    }

    /**
     * @return The namespace prefix for all method calls of the respective view implementation.
     */
    @Override
    public String getNamespacePrefix() {
        String namespace = m_template.getNamespace();
        if (namespace != null && !namespace.isEmpty()) {
            namespace += ".";
        } else {
            namespace = "";
        }
        return namespace;
    }

    /**
     * Creates a minimal HTML string to display a message.
     * @param message The message to display.
     * @return The created HTML string
     */
    @Override
    public String createMessageHTML(final String message) {
        String setIEVersion = "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">";

        StringBuilder pageBuilder = new StringBuilder();
        pageBuilder.append("<!doctype html><html><head>");
        pageBuilder.append(setIEVersion);
        pageBuilder.append("</head><body>");
        // content
        pageBuilder.append(message);
        // content end
        pageBuilder.append("</body></html>");

        return pageBuilder.toString();
    }

    private ArrayList<WebResourceLocator> getResourceFileList() {
        ArrayList<WebResourceLocator> resourceFiles = new ArrayList<WebResourceLocator>();

        if (m_template.getWebResources() != null) {
            resourceFiles.addAll(Arrays.asList(m_template.getWebResources()));
        }

        return resourceFiles;
    }

    private void deleteTempFile(final File tempFile) {
        if (tempFile.isDirectory()) {
            for (File file : tempFile.listFiles()) {
                if (file.isDirectory()) {
                    deleteTempFile(file);
                } else {
                    file.delete();
                }
            }
        }
        tempFile.delete();
    }

    private static final String ID_WEB_RES = "org.knime.js.core.webResources";

    private static final String ELEM_BUNDLE = "webResourceBundle";

    private static final String ELEM_RES = "webResource";

    private static final String ATTR_BUNDLE_ID = "webResourceBundleID";

    private static final String ATTR_SOURCE = "relativePathSource";

    private static final String ATTR_TARGET = "relativePathTarget";

    private Map<File, String> getAllWebResources() throws IOException {
        Map<File, String> copyLocations = new HashMap<File, String>();
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(ID_WEB_RES);
        if (point == null) {
            throw new IllegalStateException("Invalid extension point : " + ID_WEB_RES);
        }
        IExtension[] webResExtensions = point.getExtensions();
        for (IExtension ext : webResExtensions) {
            // get plugin path
            String pluginName = ext.getContributor().getName();
            Bundle bundle = Platform.getBundle(pluginName);

            // get relative paths and collect in map
            IConfigurationElement[] bundleElements = ext.getConfigurationElements();
            for (IConfigurationElement bundleElem : bundleElements) {
                assert bundleElem.getName().equals(ELEM_BUNDLE);
                for (IConfigurationElement resElement : bundleElem.getChildren(ELEM_RES)) {
                    String relSource = resElement.getAttribute(ATTR_SOURCE);

                    try {
                        resolveResource(copyLocations, bundle, resElement, relSource);
                    } catch (IOException | URISyntaxException ex) {
                        LOGGER.errorWithFormat(
                            "Web resource source file '%s' from plug-in %s could not be resolved: %s", relSource,
                            pluginName, ex.getMessage(), ex);
                    }
                }
            }
        }
        return copyLocations;
    }

    private void resolveResource(final Map<File, String> copyLocations, final Bundle bundle,
        final IConfigurationElement resElement, final String relSource) throws IOException, URISyntaxException {
        URL sourceURL = FileLocator.find(bundle, new Path(relSource), null);
        if (sourceURL == null) {
            throw new IOException("Cannot find location of '" + relSource + "' in bundle");
        }

        URL sourceFileURL = FileLocator.toFileURL(sourceURL);
        java.nio.file.Path sourcePath = FileUtil.resolveToPath(sourceFileURL);
        if (sourcePath == null) {
            throw new IOException("Cannot resolve '" + sourceFileURL + "' to local file");
        }

        String relTarget = resElement.getAttribute(ATTR_TARGET);
        if (StringUtils.isEmpty(relTarget)) {
            relTarget = relSource;
        }
        copyLocations.put(sourcePath.toFile(), relTarget);
    }

}
