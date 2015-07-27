/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 */
package org.knime.js.base.node.quickform.input.molecule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.knime.base.data.xml.SvgCell;
import org.knime.base.data.xml.SvgImageContent;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.ext.phantomjs.PhantomJSImageGenerator;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;
import org.openqa.selenium.TimeoutException;

/**
 * The model for the molecule string input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class MoleculeStringInputQuickFormNodeModel
        extends QuickFormFlowVariableNodeModel
        <MoleculeStringInputQuickFormRepresentation,
        MoleculeStringInputQuickFormValue,
        MoleculeStringInputQuickFormConfig> {

    private static NodeLogger LOGGER = NodeLogger.getLogger(MoleculeStringInputQuickFormNodeModel.class);

    /**
     * The default formats shown in the molecule quickform input.
     */
    static final String[] DEFAULT_FORMATS = {"SDF", "SMILES", "MOL", "SMARTS", "RXN"};

    /** Creates a new node model with no inports and a flow variable and SVG outport. */
    protected MoleculeStringInputQuickFormNodeModel() {
        super(new PortType[0], new PortType[]{FlowVariablePortObject.TYPE, ImagePortObject.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormValue createEmptyViewValue() {
        return new MoleculeStringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.quickform.input.molecule";
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        createAndPushFlowVariable();
        PortObjectSpec imageSpec;
        if (getConfig().getGenerateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE, imageSpec};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        createAndPushFlowVariable();
        PortObject imageObj = createSVGImagePortObjectFromView(exec);
        return new PortObject[]{FlowVariablePortObject.INSTANCE, imageObj};
    }

    /**
     * Renders the view with PhantomJS and retrieves the created SVG image.
     *
     * @return A {@link PortObject} containing the SVG created by the view.
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private final PortObject createSVGImagePortObjectFromView(final ExecutionContext exec) {
        if (!getConfig().getGenerateImage()) {
            return InactiveBranchPortObject.INSTANCE;
        }
        String xmlPrimer = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        String svgPrimer =
            xmlPrimer + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" "
                + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">";
        String svg = null;
        String errorText = "";
        // Only one instance of PhantomJS is running atm, synchronize view generation on static lock.
        // View nodes will get executed sequentially as a result.
        synchronized (PhantomJSImageGenerator.VIEW_GENERATION_LOCK) {
            // Inits PhantomJS AND the view.

            PhantomJSImageGenerator generator = null;
            try {
                generator = new PhantomJSImageGenerator(this, 500L, exec.createSubExecutionContext(0.75));
            } catch (Exception e) {
                if (e instanceof TimeoutException) {
                    errorText = "No elements added to body. Possible JavaScript implementation error.";
                } else {
                    errorText = e.getMessage();
                }
                LOGGER.error("Initializing view failed: " + e.getMessage(), e);
            }

            exec.setProgress(0.75, "Retrieving generated image...");
            String methodCall = "org_knime_js_base_node_quickform_input_molecule.getSVG();";
            // Retrieve the SVG string from the view.
            Object imageData;
            try {
                if (generator != null) {
                    imageData = generator.executeScript(methodCall);
                    if (imageData instanceof String) {
                        svg = (String)imageData;
                    }
                }
                exec.setProgress(0.9, "Creating image output...");
            } catch (IOException e) {
                errorText = e.getMessage();
                LOGGER.error("Retrieving SVG from view failed: " + e.getMessage(), e);
            }

        }
        if (svg == null || svg.isEmpty()) {
            if (errorText.isEmpty()) {
                errorText = "JavaScript returned nothing. Possible implementation error.";
            }
            svg = "<svg width=\"600px\" height=\"40px\">"
                + "<text x=\"0\" y=\"20\" font-family=\"sans-serif;\" font-size=\"10\">"
                + "SVG retrieval failed: " + errorText + "</text></svg>";
         }
        svg = svgPrimer + svg;
        InputStream is = new ByteArrayInputStream(svg.getBytes());
        ImagePortObjectSpec imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        ImagePortObject imagePort = null;
        try {
            imagePort = new ImagePortObject(new SvgImageContent(is), imageSpec);
        } catch (IOException e) {
            LOGGER.error("Creating SVG port object failed: " + e.getMessage(), e);
        }
        exec.setProgress(1);
        return imagePort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        String string = getRelevantValue().getMoleculeString();
        if (string == null) {
            string = "";
        }
        pushFlowVariableString(getConfig().getFlowVariableName(), string);
        pushFlowVariableString("molecule_format", getDialogRepresentation().getFormat());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setMoleculeString(getViewValue().getMoleculeString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeStringInputQuickFormConfig createEmptyConfig() {
        return new MoleculeStringInputQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeStringInputQuickFormRepresentation getRepresentation() {
        return new MoleculeStringInputQuickFormRepresentation(getRelevantValue(), getConfig());
    }

}
