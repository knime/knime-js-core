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
 *   22.09.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.core;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.web.WebResourceLocator;
import org.knime.core.node.web.WebTemplate;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.node.workflow.WebResourceController;
import org.knime.core.util.FileUtil;
import org.knime.js.core.layout.bs.JSONLayoutColumn;
import org.knime.js.core.layout.bs.JSONLayoutContent;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.js.core.layout.bs.JSONLayoutRow;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @param <REP> the {@link WebViewContent} implementation used as view representation
 * @param <VAL> the {@link WebViewContent} implementation used as view value
 */
public class JavaScriptViewCreator<REP extends WebViewContent, VAL extends WebViewContent> implements
        WizardViewCreator<REP, VAL> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(JavaScriptViewCreator.class);

    private static final String SINGLE_NODE_ID = "SINGLE";

    static final String SINGLE_PAGE_NODE_NAME = "Single Node Page";

    private static File tempFolder;

    private File m_tempIndexFile;

    private WebTemplate m_template;

    private String m_title;

    private static final Object LOCK = new Object();

    private String m_customCSS;

    /**
     * @return true if is running in debug mode, false otherwise
     */
    protected static boolean isDebug() {
        IPreferenceStore prefs = JSCorePlugin.getDefault().getPreferenceStore();
        if (prefs.getBoolean(JSCorePlugin.P_DEBUG_HTML)) {
            return true;
        }
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        if (runtimeBean != null && runtimeBean.getInputArguments() != null) {
            String inputArguments = runtimeBean.getInputArguments().toString();
            if (inputArguments != null && !inputArguments.isEmpty()) {
                return inputArguments.indexOf("jdwp") >= 0;
            }
        }
        return false;
    }

    private static boolean viewTempDirExists() {
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
            m_template = WebResourceController.getWebTemplateFromJSObjectID(javascriptObjectID);
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
    public String createWebResources(final String viewTitle, final REP viewRepresentation,
            final VAL viewValue, final String customCSS) throws IOException {
        m_customCSS = customCSS;
        m_title = viewTitle == null ? "KNIME view" : viewTitle;
        synchronized(LOCK) {
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
        }
        m_tempIndexFile = FileUtil.createTempFile("index_" + System.currentTimeMillis(), ".html", tempFolder, true);
        try (BufferedWriter writer = Files.newBufferedWriter(m_tempIndexFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write(buildHTMLResource(viewRepresentation, viewValue, customCSS));
            writer.flush();
        }
        return m_tempIndexFile.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewRepresentationJSONString(final REP rep) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewValueJSONString(final VAL val) {
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

    /**
     * With the PageBuilder v.2, all JavaScript views now get a single node page
     * representation created for them (as if they were a single node component
     * view).
     *
     * @param rep the WebViewContent representation for the view representation
     * @param val the WebViewContent value for the view value
     * @return a String of the correct JSON-formatted page content
     * @since 4.2
     */
    public String getPageContentJSONString(final REP rep, final VAL val) {
        JSONSingleNodePage page = new JSONSingleNodePage(rep, val);
        return getViewRepresentationJSONString((REP)page);
    }

    /**
     * Explicitly set the customCSS to be used with the view.
     *
     * @param customCSS the customCSS string to set
     * @since 4.3
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
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
     * @param customCSS optional custom css
     * @return an HTML string representing the view page
     * @throws IOException if the debug file cannot be written
     */
    protected String buildHTMLResource(final REP viewRepresentation, final VAL viewValue, final String customCSS)
            throws IOException {

        m_customCSS = customCSS;

        String setIEVersion = "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">";
        String inlineScript = "<script type=\"text/javascript\" charset=\"UTF-8\">%s</script>";
        String scriptString = "<script type=\"text/javascript\" src=\"%s\" charset=\"UTF-8\"></script>";
        String inlineCSS = "<style type=\"text/css\">%s</style>";
        String cssString = "<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\">";

        StringBuilder pageBuilder = new StringBuilder();
        pageBuilder.append("<!doctype html><html lang=\"en-US\"><head>");
        pageBuilder.append("<meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">");
        pageBuilder.append("<meta charset=\"UTF-8\">");
        pageBuilder.append("<title>" + m_title + "</title>");
        pageBuilder.append(setIEVersion);
        //pageBuilder.append(String.format(inlineScript, "BASE_DIR = " + m_tempFolder));
        //pageBuilder.append(debugScript);

        //  uncomment if testing SWT
        //  pageBuilder.append(String.format(scriptString, "js-lib/firebug-lite/firebug-lite.js"));

        String bodyText = "";
        if (m_template == null || m_template.getWebResources() == null || m_template.getWebResources().length < 1) {
            bodyText = "<p>ERROR: No view implementation available!</p>";
            LOGGER.error("No JavaScript view implementation available for view: " + m_title);
        }

        if (isVisualLayoutEditor()) {
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
        } else {
            // PageBuilder always included in pages (except layout editor) @since 4.2
            pageBuilder.append(String.format(scriptString, "org/knime/core/knime-pagebuilder2-ap.js"));
        }

        if (isDebug()) {
            String loadScript = "function loadWizardNodeView(){window.debugHTML = true;%s};";
            loadScript =
                String.format(loadScript, wrapInTryCatch(createInitJSViewMethodCall(viewRepresentation, viewValue)));
            StringBuilder debugBuilder = new StringBuilder(pageBuilder.toString());
            debugBuilder.append(String.format(inlineScript, loadScript));
            debugBuilder.append("</head><body" + " onload=\"loadWizardNodeView();\"" + ">");
            debugBuilder.append(bodyText);
            debugBuilder.append("</body></html>");
            File debugFile = FileUtil.createTempFile("debug_" + System.currentTimeMillis(), ".html", tempFolder, true);
            try (BufferedWriter writer = Files.newBufferedWriter(debugFile.toPath(), StandardCharsets.UTF_8)) {
                writer.write(debugBuilder.toString());
                writer.flush();
            }
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

    private boolean isVisualLayoutEditor() {
        final String LAYOUT_EDITOR_CREATOR_CLASS = "VisualLayoutViewCreator";
        String className = this.getClass().getName();
        String innerClassCreatorName = className.substring(className.indexOf('$') + 1);
        return LAYOUT_EDITOR_CREATOR_CLASS.equals(innerClassCreatorName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createInitJSViewMethodCall(final boolean parseArguments, final REP viewRepresentation, final VAL viewValue) {
        StringBuilder builder = new StringBuilder();
        String initCall = "";
        if (isVisualLayoutEditor()) {
            return initCall;
        }
        if (parseArguments) {
            // create page container for single node
            String jsonViewRepresentation = getPageContentJSONString(viewRepresentation, viewValue);
            String escapedRepresentation = jsonViewRepresentation.replace("\\", "\\\\").replace("'", "\\'");
            String repParseCall = "var parsedRepresentation = JSON.parse('" + escapedRepresentation + "');";
            builder.append(repParseCall);
        }
        initCall = "window.KnimePageLoader.init(parsedRepresentation, null, null, " + isDebug() + ");";
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
        pageBuilder.append("</head><body><p>");
        // content
        pageBuilder.append(message);
        // content end
        pageBuilder.append("</p></body></html>");

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
                String exports = bundleElem.getAttribute("exports");
                String isDebugRes = bundleElem.getAttribute("debug");
                // only copy source *.js.map files if in debug mode
                if (exports != null && exports.equals("KnimePageLoader") &&
                        isDebugRes != null && isDebugRes.equals("true") && !isDebug()) {
                    continue;
                }
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

    @Override
    public java.nio.file.Path getCurrentLocation() {
        if (tempFolder == null) {
            return null;
        }
        return tempFolder.toPath();
    }

    /**
    * Serializable JSON container object wrapper which configures a single node page to use with the
    * PageBuilder v.2.
    *
    * @author benlaney
    * @since 4.2
    */
    @JsonAutoDetect
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private final class JSONSingleNodePage extends JSONWebNodePage {

        private static final boolean m_isSingleView = true;

        @JsonCreator
        private JSONSingleNodePage(final REP viewRep, final VAL viewVal) {
            super(new JSONWebNodePageConfiguration(), new HashMap<String, JSONWebNode>());
            setWebNodePageConfiguration(createJSONPageConfig());
            setWebNodes(createNodesMap(createJsonWebNode(viewRep, viewVal)));
        }

        private JSONWebNode createJsonWebNode(final WebViewContent viewRep, final WebViewContent viewVal) {
            JSONWebNode node = new JSONWebNode();
            setMethodNames(node);
            setResources(node);
            setInternals(node, viewRep, viewVal);
            setNodeInfo(node);
            setCustomCSS(node);
            return node;
        }

        private Map<String, JSONWebNode> createNodesMap(final JSONWebNode node) {
            Map<String, JSONWebNode> nodesMap = new HashMap<String, JSONWebNode>();
            nodesMap.put(SINGLE_NODE_ID, node);
            return nodesMap;
        }

        /**
        * @return the isSingleView
        */
        @JsonProperty("isSingleView")
        public boolean getIsSingleView() {
            return m_isSingleView;
        }

        private void setMethodNames(final JSONWebNode node) {
            node.setNamespace(m_template.getNamespace());
            node.setInitMethodName(m_template.getInitMethodName());
            node.setValidateMethodName(m_template.getValidateMethodName());
            node.setGetViewValueMethodName(m_template.getPullViewContentMethodName());
            node.setSetValidationErrorMethodName(m_template.getSetValidationErrorMethodName());
        }

        private void setResources( final JSONWebNode node) {
            WebResourceLocator[] resources = m_template.getWebResources();
            List<String> jsLibraries = new ArrayList<>();
            List<String> styleSheets = new ArrayList<>();

            for (WebResourceLocator resFile : resources) {
                String path = resFile.getRelativePathTarget();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                switch (resFile.getType()) {
                    case CSS:
                        styleSheets.add(path);
                        break;
                    case JAVASCRIPT:
                        jsLibraries.add(path);
                        break;
                    case FILE:
                        break;
                    default:
                        LOGGER.error("Unrecognized resource type " + resFile.getType());
                }
            }

            node.setJavascriptLibraries(jsLibraries);
            node.setStylesheets(styleSheets);
        }

        private void setInternals( final JSONWebNode node, final WebViewContent rep, final WebViewContent val) {
            node.setViewRepresentation((JSONViewContent)rep);
            node.setViewValue((JSONViewContent)val);
        }

        private void setNodeInfo( final JSONWebNode node) {
            JSONWebNodeInfo info = new JSONWebNodeInfo();
            info.setNodeName(SINGLE_PAGE_NODE_NAME);
            info.setDisplayPossible(true);
            node.setNodeInfo(info);
        }

        private void setCustomCSS( final JSONWebNode node) {
            node.setCustomCSS(m_customCSS);
        }

        private JSONWebNodePageConfiguration createJSONPageConfig() {
            return new JSONWebNodePageConfiguration(createJSONLayoutPage(), null, null);
        }

        private JSONLayoutPage createJSONLayoutPage() {
            JSONLayoutPage page = new JSONLayoutPage();
            page.setParentLayoutLegacyMode(false);
            page.setRows(createJSONRow());
            return page;
        }

        private List<JSONLayoutRow> createJSONRow() {
            JSONLayoutRow row = new JSONLayoutRow();
            row.addColumn(createJSONColumn());
            List<JSONLayoutRow> list = new ArrayList<>();
            list.add(row);
            return list;
        }

        private JSONLayoutColumn createJSONColumn() {
            JSONLayoutColumn column = new JSONLayoutColumn();
            column.setContent(createJSONContent());
            return column;
        }

        private List<JSONLayoutContent> createJSONContent() {
            JSONLayoutViewContent content = new JSONLayoutViewContent();
            content.setUseLegacyMode(false);
            content.setNodeID(SINGLE_NODE_ID);
            List<JSONLayoutContent> list = new ArrayList<>();
            list.add(content);
            return list;
        }
    }
}
