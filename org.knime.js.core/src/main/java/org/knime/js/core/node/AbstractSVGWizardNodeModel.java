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
 *   07.11.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core.node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.knime.base.data.xml.SvgCell;
import org.knime.base.data.xml.SvgImageContent;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.web.WebTemplate;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.WizardExecutionController;
import org.knime.ext.phantomjs.PhantomJSImageGenerator;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @param <REP> The concrete class of the {@link WebViewContent} acting as representation of the view.
 * @param <VAL> The concrete class of the {@link WebViewContent} acting as value of the view.
 * @since 2.11
 */
public abstract class AbstractSVGWizardNodeModel<REP extends WebViewContent, VAL extends WebViewContent> extends
    AbstractWizardNodeModel<REP, VAL> {

    /**
     * Creates a new {@link WizardNode} model with the given number (and types!) of input and
     * output types.
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected AbstractSVGWizardNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec)
            throws Exception {
        exec.setProgress(0.0, "Creating view model...");
        performExecuteCreateView(inObjects, createThirdsExecutionContext(exec));
        exec.setProgress(1.0 / 3.0, "Rendering SVG image...");
        ImagePortObject svgPortObject = createSVGImagePortObjectFromView(createThirdsExecutionContext(exec));
        exec.setProgress(2.0 / 3.0, "Creating output...");
        PortObject[] output = performExecuteCreatePortObjects(svgPortObject, createThirdsExecutionContext(exec));
        exec.setProgress(1.0);
        return output;
    }

    private ExecutionContext createThirdsExecutionContext(final ExecutionContext originalExec) {
        return originalExec.createSubExecutionContext(1.0 / 3.0);
    }


    /**
     * Called during {@link NodeModel#execute(PortObject[], ExecutionContext) execute}.
     * View representation and value are populated in this method.
     * <br><br>
     * Called BEFORE image creation.
     * @param inObjects The input objects.
     * @param exec For {@link BufferedDataTable} creation and progress.
     * @throws Exception If the node execution fails for any reason.
     */
    protected abstract void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec)
        throws Exception;

    /**
     * Called during {@link NodeModel#execute(PortObject[], ExecutionContext) execute}.
     * Populates the resulting {@link PortObject} array.
     * The {@link ImagePortObject} containing the required SVG image is passed in as a parameter.
     * <br><br>
     * Called AFTER image creation.
     * @param svgImageFromView The port object, containing the SVG created by the view.
     * @param exec For {@link BufferedDataTable} creation and progress.
     * @return The output objects.
     * @throws Exception If the node execution fails for any reason.
     */
    protected abstract PortObject[] performExecuteCreatePortObjects(ImagePortObject svgImageFromView,
        final ExecutionContext exec) throws Exception;

    /**
     * @return true if the SVG image is supposed to be rendered and retrieved, false otherwise
     */
    protected abstract boolean generateImage();

    /**
     * Renders the view with PhantomJS and retrieves the created SVG image.
     * @return A {@link PortObject} containing the SVG created by the view.
     * @throws IOException if an I/O error occurs
     */
    private final ImagePortObject createSVGImagePortObjectFromView(final ExecutionContext exec) throws IOException {
        String xmlPrimer = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        String svgPrimer = xmlPrimer + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" "
                + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">";
        String svg = null;
        if (generateImage()) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            PhantomJSImageGenerator generator = new PhantomJSImageGenerator(this);

            exec.setProgress(0.75, "Retrieving generated image...");
            String namespace = getViewNamespace();
            String methodCall = "";
            if (namespace != null && !namespace.isEmpty()) {
                methodCall += namespace + ".";
            }
            methodCall += getExtractSVGMethodName() + "();";
            Object imageData = generator.executeScript(methodCall);
            if (imageData instanceof String) {
                svg = (String)imageData;
            }
        }
        exec.setProgress(0.9, "Creating image output...");
        if (svg == null || svg.isEmpty()) {
            svg = "<svg width=\"1px\" height=\"1px\"></svg>";
        }
        svg = svgPrimer + svg;
        InputStream is = new ByteArrayInputStream(svg.getBytes());
        ImagePortObjectSpec imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        ImagePortObject imagePort = new ImagePortObject(new SvgImageContent(is), imageSpec);
        exec.setProgress(1);
        return imagePort;
    }

    /**
     * @return
     */
    private String getViewNamespace() {
        WebTemplate template = WizardExecutionController.getWebTemplateFromJSObjectID(getJavascriptObjectID());
        return template.getNamespace();
    }

    /**
     * Override this method, if JavaScript implementation uses a different
     * method name then getSVG() for returning the rendered SVG.
     * @return The method name, used in the JavaScript view implementation, which returns the rendered SVG.
     */
    protected String getExtractSVGMethodName() {
        return "getSVG";
    }
}
