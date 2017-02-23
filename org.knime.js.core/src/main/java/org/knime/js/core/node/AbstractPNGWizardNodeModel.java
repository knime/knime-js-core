/**
 *
 */
package org.knime.js.core.node;

import java.io.IOException;
import java.util.Base64;

import org.knime.core.data.image.ImageContent;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.wizard.WizardNode;
import org.knime.js.core.JSONViewContent;

/**
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @param <REP> The concrete class of the {@link JSONViewContent} acting as representation of the view.
 * @param <VAL> The concrete class of the {@link JSONViewContent} acting as value of the view.
 * @since 3.4
 *
 */
public abstract class AbstractPNGWizardNodeModel<REP extends JSONViewContent, VAL extends JSONViewContent> extends AbstractImageWizardNodeModel<REP, VAL> {

    private static final String pngPrimer = "data:image/png;base64,";

    /**
     * Creates a new {@link WizardNode} model with the given number (and types!) of input and output types.
     *
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     * @param viewName the view name
     */
    protected AbstractPNGWizardNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes, final String viewName) {
        super(inPortTypes, outPortTypes, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImagePortObject createImagePortObjectFromView(final String imageData, final String error) throws IOException {
        String image = imageData;
        String errorText = error;
        if (image == null || image.isEmpty()) {
            if (errorText.isEmpty()) {
                errorText = "JavaScript returned nothing. Possible implementation error.";
            }
            setWarningMessage(errorText);
            return null;
        }
        if (image.startsWith(pngPrimer)) {
            image = image.substring(pngPrimer.length());
        }
        ImagePortObjectSpec imageSpec = new ImagePortObjectSpec(PNGImageContent.TYPE);
        ImageContent imageContent = new PNGImageContent(Base64.getDecoder().decode(image));
        return new ImagePortObject(imageContent, imageSpec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExtractImageMethodName() {
        return "getPNG";
    }

}
