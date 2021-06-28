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
 *   12.06.2018 (albrecht): created
 */
package org.knime.js.core;

import static org.knime.js.core.JSCorePlugin.CEF_BROWSER;
import static org.knime.js.core.JSCorePlugin.HEADLESS_CEF;
import static org.knime.js.core.JSCorePlugin.HEADLESS_CHROMIUM;
import static org.knime.js.core.JSCorePlugin.HEADLESS_PHANTOMJS;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @param <T> the {@link NodeModel} implementation
 * @param <REP> the view content serving as view representation
 * @param <VAL> the view content serving as view value
*/
public abstract class AbstractImageGenerator<T extends NodeModel & WizardNode<REP, VAL>,
        REP extends WebViewContent, VAL extends WebViewContent> {

    /**
     * ID of iframe used when a single node view is displayed.
     * @since 4.2
     */
    protected static final String SINGLE_NODE_FRAME_ID = "node-SINGLE";

    /**
     * @since 4.2
     */
    protected static final int DEFAULT_TIMEOUT = 60;

    /**
     * @since 4.2
     */
    protected static final int VIEW_INIT_TIMEOUT = 90;

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AbstractImageGenerator.class);
    private static final String EXT_POINT_ID = "org.knime.js.core.headlessBrowsers";
    private static final String CHROME = "org.knime.ext.seleniumdrivers.multios.ChromeImageGenerator";
    private static final String IMAGE_GENERATOR_CLASS = "imageGeneratorClass";


    private final T m_nodeModel;

    /**
     * @param nodeModel
     */
    public AbstractImageGenerator(final T nodeModel) {
        m_nodeModel = nodeModel;
    }

    protected T getNodeModel() {
        return m_nodeModel;
    }

    public abstract void generateView(final Long optionalWait, final ExecutionContext exec) throws Exception;

    public abstract Object retrieveImage(final String methodCall) throws Exception;

    public abstract void cleanup();

    /**
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<HeadlessBrowserExtension> getAllHeadlessBrowsers() {
        List<HeadlessBrowserExtension> viewExtensionList = new ArrayList<HeadlessBrowserExtension>(3);
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(EXT_POINT_ID);
        assert point != null : "Invalid extension point id: " + EXT_POINT_ID;

        for (IExtension ext : point.getExtensions()) {
            IConfigurationElement[] elements = ext.getConfigurationElements();
            for (IConfigurationElement viewElement : elements) {
                String className = viewElement.getAttribute(IMAGE_GENERATOR_CLASS);
                String browserName = viewElement.getAttribute("name");
                String browserDesc = viewElement.getAttribute("description");
                Class<AbstractImageGenerator> viewClass;
                try {
                    viewClass = (Class<AbstractImageGenerator>)Platform
                        .getBundle(viewElement.getDeclaringExtension().getContributor().getName())
                        .loadClass(className);
                    viewExtensionList.add(new HeadlessBrowserExtension(viewClass, browserName, browserDesc));
                } catch (ClassNotFoundException ex) {
                    LOGGER.error("Could not find implementation for " + className
                        + " Browser won't be available", ex);
                }
            }
        }
        return viewExtensionList;
    }

    /**
     * Returns an instance of an {@link AbstractImageGenerator} according to the user preferences and plugins
     * installed
     * @param model the {@link ViewableModel} to pass to the constructor of the generator
     * @return a configured image generator instance
     * @throws InstantiationException if the constructor of the image generator can not be called correctly
     * @throws OperationNotSupportedException if no image generator is available
     */
    @SuppressWarnings("rawtypes")
    public static AbstractImageGenerator getConfiguredHeadlessBrowser(final ViewableModel model)
            throws InstantiationException, OperationNotSupportedException {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] configurationElements =
                registry.getConfigurationElementsFor(EXT_POINT_ID);

        Class<?> viewClass = null;
        String classString = JSCorePlugin.getDefault().getPreferenceStore()
                .getString(JSCorePlugin.P_HEADLESS_BROWSER);
        // disable Chrome image generation in general or Chromium image generation if the binaries are not installed
        if (classString.equals(CHROME)
            || (classString.equals(HEADLESS_CHROMIUM) && !JSCorePlugin.isChromiumInstalled())) {
            classString = null;
        }

        // TEMPORARY - WILL BE REMOVED AGAIN SOON - see AP-17033
        if (JSCorePlugin.isRunningOnMacCatalina() && CEF_BROWSER.equals(classString)) {
            // make sure to never use the CEF browser on Catalina
            classString = null;
        }

        if (StringUtils.isNotEmpty(classString)) {
         // try loading selected view
            viewClass = getViewClassByReflection(classString, configurationElements);
            if (viewClass == null) {
                LOGGER.error("Headless browser set in preferences (" + classString
                    + ") can't be loaded. Switching to default.");
            }
        }
        if (viewClass == null) {
            // try loading defaults
            if (JSCorePlugin.isChromiumInstalled()) {
                viewClass = getViewClassByReflection(HEADLESS_CHROMIUM, configurationElements);
            }
            if (viewClass == null && //
            // TEMPORARY - WILL BE REMOVED AGAIN SOON - see AP-17033
                !JSCorePlugin.isRunningOnMacCatalina()) {
                viewClass = getViewClassByReflection(HEADLESS_CEF, configurationElements);
            }
            if (viewClass == null) {
                LOGGER.error("Headless Chromium could not be initialized as default browser for image "
                    + "generation. Trying PhantomJS...");
                viewClass = getViewClassByReflection(HEADLESS_PHANTOMJS, configurationElements);
            }
        }
        if (viewClass != null) {
            try {
                Constructor<?> constructor = viewClass.getConstructor(NodeModel.class);
                return (AbstractImageGenerator)constructor.newInstance(model);
            } catch (Exception e) {
                throw new InstantiationException("Headless browser can not be initialized. "
                    + "Image generation not possible.");
            }
        }
        throw new OperationNotSupportedException("No headless browser available. Image generation not possible.");
    }

    private static Class<?> getViewClassByReflection(final String className,
            final IConfigurationElement[] confElements) {
        Class<?> viewClass = null;
        try {
            for (IConfigurationElement element : confElements) {
                if (className.equals(element.getAttribute(IMAGE_GENERATOR_CLASS))) {
                    viewClass = Platform.getBundle(element.getDeclaringExtension().getContributor().getName())
                        .loadClass(element.getAttribute(IMAGE_GENERATOR_CLASS));
                }
            }
            return viewClass;
        } catch (Exception e) { /* do nothing */}
        return null;
    }

    /**
     * Implementation of a HeadlessBrowser from extension point.
     *
     * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
     */
    public static class HeadlessBrowserExtension {

        @SuppressWarnings("rawtypes")
        private Class<AbstractImageGenerator> m_imageGeneratorClass;
        private String m_browserName;
        private String m_browserDescription;

        /**
         * Creates a new HeadlessBrowserExtension.
         *
         * @param imageGeneratorClass the class holding the view implementation
         * @param browserName the name of the browser
         * @param browserDescription the optional description of the browser
         */
        @SuppressWarnings("rawtypes")
        public HeadlessBrowserExtension(final Class<AbstractImageGenerator> imageGeneratorClass, final String browserName,
            final String browserDescription) {
            m_imageGeneratorClass = imageGeneratorClass;
            m_browserName = browserName;
            m_browserDescription = browserDescription;
        }

        /**
         * @return the viewClass
         */
        @SuppressWarnings("rawtypes")
        public Class<AbstractImageGenerator> getImageGeneratorClass() {
            return m_imageGeneratorClass;
        }

        /**
         * @return the viewName
         */
        public String getBrowserName() {
            return m_browserName;
        }

        /**
         * @return the viewDescription
         */
        public String getBrowserDescription() {
            return m_browserDescription;
        }
    }

}
