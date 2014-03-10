package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class StringInputQuickFormNodeModel extends QuickFormFlowVariableNodeModel<StringInputQuickFormRepresentation, 
        StringInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_string";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        String string = getViewValue().getString();
        if (string == null) {
            string = "";
        }
        String regex = getDialogRepresentation().getRegex();
        if (regex != null && !regex.isEmpty() && !string.matches(regex)) {
            throw new InvalidSettingsException(getDialogRepresentation()
                    .getErrorMessage());
        }
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        createEmptyViewRepresentation().loadFromNodeSettings(settings);
        createEmptyViewValue().loadFromNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInputQuickFormRepresentation createEmptyViewRepresentation() {
        return new StringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInputQuickFormValue createEmptyViewValue() {
        return new StringInputQuickFormValue();
    }
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final StringInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
