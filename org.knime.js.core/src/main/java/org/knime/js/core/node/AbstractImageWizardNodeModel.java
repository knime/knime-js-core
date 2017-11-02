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
 *   23 Feb 2017 (amartin): created
 */
package org.knime.js.core.node;

import java.io.IOException;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.web.WebTemplate;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.WebResourceController;
import org.knime.ext.phantomjs.PhantomJSImageGenerator;
import org.knime.js.core.JSONViewContent;
import org.openqa.selenium.TimeoutException;

/**
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @param <REP> The concrete class of the {@link JSONViewContent} acting as representation of the view.
 * @param <VAL> The concrete class of the {@link JSONViewContent} acting as value of the view.
 * @since 3.4
 */
public abstract class AbstractImageWizardNodeModel<REP extends JSONViewContent, VAL extends JSONViewContent> extends AbstractWizardNodeModel<REP, VAL> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AbstractImageWizardNodeModel.class);

    private Long m_optionalViewWaitTime = null;

    /**
     * Creates a new {@link WizardNode} model with the given number (and types!) of input and output types.
     *
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     * @param viewName the view name
     */
    protected AbstractImageWizardNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes, final String viewName) {
        super(inPortTypes, outPortTypes, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec)
        throws Exception {
        exec.setProgress(0.0, "Creating view model...");
        performExecuteCreateView(inObjects, exec.createSubExecutionContext(0.25));
        exec.setProgress(1.0 / 3.0, "Rendering image...");
        PortObject imagePortObject = renderViewAndCreateImage(exec.createSubExecutionContext(0.5));
        exec.setProgress(2.0 / 3.0, "Creating output...");
        PortObject[] output =
            performExecuteCreatePortObjects(imagePortObject, inObjects, exec.createSubExecutionContext(0.25));
        exec.setProgress(1.0);
        return output;
    }

    /**
     * Called during {@link NodeModel#execute(PortObject[], ExecutionContext) execute}. View representation and value
     * are populated in this method. <br>
     * <br>
     * Called BEFORE image creation.
     *
     * @param inObjects The input objects.
     * @param exec For {@link BufferedDataTable} creation and progress.
     * @throws Exception If the node execution fails for any reason.
     */
    protected abstract void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec)
        throws Exception;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private PortObject renderViewAndCreateImage(final ExecutionContext exec) throws IOException {
        if (!generateImage()) {
            return InactiveBranchPortObject.INSTANCE;
        }

        String image = null;
        String errorText = null;

        // Only one instance of PhantomJS is running atm, synchronize view generation on static lock.
        // View nodes will get executed sequentially as a result.
        synchronized (PhantomJSImageGenerator.VIEW_GENERATION_LOCK) {
            // Inits PhantomJS AND the view.

            PhantomJSImageGenerator generator = null;
            try {
                generator = new PhantomJSImageGenerator(this, getOptionalViewWaitTime(), exec.createSubExecutionContext(0.75));
            } catch (IOException ex) {
                throw ex;
            } catch (Exception e) {
                if (e instanceof TimeoutException) {
                    errorText = "No elements added to body. Possible JavaScript implementation error.";
                } else {
                    errorText = e.getMessage();
                }
                LOGGER.error("Initializing view failed: " + e.getMessage(), e);
            }

            exec.setProgress(0.75, "Retrieving generated image...");
            String namespace = getViewNamespace();
            String methodCall = "";
            if (namespace != null && !namespace.isEmpty()) {
                methodCall += namespace + ".";
            }
            methodCall += getExtractImageMethodName() + "();";
            // Retrieve the SVG string from the view.
            Object imageData;
            try {
                if (generator != null) {
                    imageData = generator.executeScript(methodCall);
                    if (imageData instanceof String) {
                        image = (String)imageData;
                    }
                }
                exec.setProgress(0.9, "Creating image output...");
            } catch (IOException e) {
                errorText = e.getMessage();
                LOGGER.error("Retrieving image from view failed: " + e.getMessage(), e);
            }
            ImagePortObject imagePort = null;
            try {
                imagePort = createImagePortObjectFromView(image, errorText);
                exec.setProgress(1);
            } catch (IOException e) {
                LOGGER.error("Creating image port object failed: " + e.getMessage(), e);
            }
            return imagePort;
        }
    }

    /**
     * Creates the port object from the retrieved image content string.
     *
     * @param imageContent the string retrieved from the view, representing the image, may be null
     * @param errorText an error string in case view retrieval failed, may be null
     * @return A {@link PortObject} containing the image created by the view.
     * @throws IOException if an I/O error occurs
     */
    protected abstract ImagePortObject createImagePortObjectFromView(final String imageContent, final String errorText) throws IOException;

    /**
     * Called during {@link NodeModel#execute(PortObject[], ExecutionContext) execute}. Populates the resulting
     * {@link PortObject} array. The {@link ImagePortObject} containing the required SVG image is passed in as a
     * parameter. <br>
     * <br>
     * Called AFTER image creation.
     *
     * @param svgImageFromView The port object, containing the SVG created by the view, or inactive port object.
     * @param inObjects The input objects.
     * @param exec For {@link BufferedDataTable} creation and progress.
     * @return The output objects.
     * @throws Exception If the node execution fails for any reason.
     */
    protected abstract PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView,
        final PortObject[] inObjects, final ExecutionContext exec) throws Exception;

    /**
     * @return true if an image is supposed to be rendered and retrieved, false otherwise
     */
    protected abstract boolean generateImage();

    /**
     * Returns the JavaScript method name used for image extraction
     * Overwrite this method to use a different method name in JavaScript.
     *
     * @return the method name
     */
    protected abstract String getExtractImageMethodName();

    /**
     * @return the optionalViewTimeout
     */
    protected final Long getOptionalViewWaitTime() {
        return m_optionalViewWaitTime;
    }

    /**
     * @param optionalViewTimeout the optionalViewTimeout to set
     */
    protected final void setOptionalViewWaitTime(final Long optionalViewTimeout) {
        m_optionalViewWaitTime = optionalViewTimeout;
    }

    /**
     * @return the view implementation namespace
     */
    protected final String getViewNamespace() {
        WebTemplate template = WebResourceController.getWebTemplateFromJSObjectID(getJavascriptObjectID());
        return template.getNamespace();
    }
}
