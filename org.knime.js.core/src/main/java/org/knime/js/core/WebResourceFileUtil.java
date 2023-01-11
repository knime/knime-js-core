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
 *   Dec 20, 2022 (hornm): created
 */
package org.knime.js.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.FileUtil;
import org.osgi.framework.Bundle;

/**
 * Utility class to be able to access the web resources files of the js views and the page builder which are located in
 * respective bundles bundles (made available via the web-resources extension point).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 5.0
 */
public final class WebResourceFileUtil {

    /**
     * Name of the page-builders 'index'-html (i.e. the html-document that bootstraps the page-builder).
     */
    public static final String PAGEBUILDER_AP_WRAPPER_HTML_DOC = "/apWrapper.html";

    private static final Bundle PAGEBUILDER_BUNDLE = Platform.getBundle("org.knime.js.pagebuilder");

    private static final NodeLogger LOGGER = NodeLogger.getLogger(WebResourceFileUtil.class);

    private static Map<String, URL> webResourcesUrls;

    private static final String ID_WEB_RES = "org.knime.js.core.webResources";
    private static final String ELEM_BUNDLE = "webResourceBundle";
    private static final String ELEM_RES = "webResource";
    private static final String ATTR_SOURCE = "relativePathSource";
    private static final String ATTR_TARGET = "relativePathTarget";

    /**
     * @param path the relative path of the page builder resource
     * @return a url pointing to the actual file of the resource; {@code null} if there is no file
     * @throws IOException
     */
    public static URL getPageBuilderResourceFileURL(final String path) throws IOException {
        // must not use url.toURI() -- FileLocator leaves spaces in the URL (see eclipse bug 145096)
        // -- taken from TableauHyperActivator.java line 158
        var url = PAGEBUILDER_BUNDLE.getEntry("dist/app" + path);
        return url == null ? null : FileLocator.toFileURL(url);
    }

    /**
     * @return list of resource-ids of all available web resources; the id can be used, e.g., to get access to the
     *         actual resource-file via* {@link #getWebResourceFileURL(String)}
     */
    public static List<String> getWebResourceIds() {
        ensureThatWebResourceUrlsAreAvailable();
        return new ArrayList<>(webResourcesUrls.keySet());
    }

    /**
     * @param resourceId the id of the resource to get the file-url for (usually a relative path)
     * @return a url pointing to the actual file of the resource; {@code null} if none found
     */
    public static URL getWebResourceFileURL(final String resourceId) {
        ensureThatWebResourceUrlsAreAvailable();
        return webResourcesUrls.get(resourceId);
    }

    private static void ensureThatWebResourceUrlsAreAvailable() {
        if (webResourcesUrls == null) {
            webResourcesUrls = collectWebResourceUrls();
        }
    }

    private static Map<String, URL> collectWebResourceUrls() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(ID_WEB_RES);
        if (point == null) {
            throw new IllegalStateException("Invalid extension point : " + ID_WEB_RES);
        }

        Map<String, URL> urls = new HashMap<>();
        for (IExtension ext : point.getExtensions()) {
            collectWebResourceUrlsFromExtension(urls, ext);
        }
        return urls;
    }

    private static void collectWebResourceUrlsFromExtension(final Map<String, URL> urls, final IExtension ext) {
        // get plugin path
        String pluginName = ext.getContributor().getName();
        var bundle = Platform.getBundle(pluginName);

        // get relative paths and collect in map
        IConfigurationElement[] bundleElements = ext.getConfigurationElements();
        for (IConfigurationElement bundleElem : bundleElements) {
            assert bundleElem.getName().equals(ELEM_BUNDLE);
            for (IConfigurationElement resElement : bundleElem.getChildren(ELEM_RES)) {
                try {
                    collectWebResourceUrlsFromConfigElement(urls, pluginName, bundle, resElement);
                } catch (Throwable t) { // NOSONAR
                    LOGGER.error("Problem collecting web resources for plugin '" + pluginName + "'", t);
                }
            }
        }
    }

    private static void collectWebResourceUrlsFromConfigElement(final Map<String, URL> urls, final String pluginName,
        final Bundle bundle, final IConfigurationElement resElement)
        throws IOException, URISyntaxException {
        String relSource = resElement.getAttribute(ATTR_SOURCE);
        var resourceUrl = bundle.getEntry(relSource);
        if (resourceUrl == null) {
            throw new NoSuchElementException(
                "Resource '" + relSource + "' does not exist in plug-in '" + pluginName + "'");
        }

        String relTarget = resElement.getAttribute(ATTR_TARGET);
        if (StringUtils.isEmpty(relTarget)) {
            relTarget = relSource;
        }
        var fileUrl = FileLocator.toFileURL(resourceUrl);
        var resourceFile = FileUtil.resolveToPath(fileUrl);
        if (Files.isDirectory(resourceFile)) {
            collectWebResourceUrlsFromDirectory(fileUrl, relTarget, urls);
        } else {
            urls.put(relTarget, resourceFile.toUri().toURL());
        }
    }

    private static void collectWebResourceUrlsFromDirectory(final URL url, String relTarget,
        final Map<String, URL> urls) throws IOException, URISyntaxException {
        Deque<Path> queue = new ArrayDeque<>(32);
        var dir = FileUtil.resolveToPath(url);
        queue.push(dir);

        if (!relTarget.isEmpty() && !relTarget.endsWith("/")) {
            relTarget += "/";
        }
        if (relTarget.startsWith("/")) {
            relTarget = relTarget.substring(1);
        }

        while (!queue.isEmpty()) {
            Path p = queue.poll();
            if (Files.isDirectory(p)) {
                if (!relTarget.isEmpty() || !p.equals(dir)) {
                    // don't add an (empty) entry for the root directory itself
                    String s = relTarget + dir.relativize(p);
                    s = addTrailingSlash(s);
                    urls.put(s, p.toUri().toURL());
                }
                try (DirectoryStream<Path> contents = Files.newDirectoryStream(p)) {
                    contents.forEach(queue::add);
                }
            } else {
                urls.put(relTarget + dir.relativize(p).toString().replace("\\", "/"), p.toUri().toURL());
            }
        }
    }

    private static String addTrailingSlash(String s) {
        if (!s.endsWith("/")) {
            s += "/";
        }
        return s;
    }

    private WebResourceFileUtil() {
        // utility
    }

}
