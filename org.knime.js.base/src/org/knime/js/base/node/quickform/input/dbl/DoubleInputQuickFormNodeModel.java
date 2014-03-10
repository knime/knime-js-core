package org.knime.js.base.node.quickform.input.dbl;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class DoubleInputQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<DoubleInputQuickFormRepresentation, DoubleInputQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleInputQuickFormRepresentation createEmptyViewRepresentation() {
        return new DoubleInputQuickFormRepresentation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleInputQuickFormValue createEmptyViewValue() {
        return new DoubleInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_dbl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        double value = getViewValue().getDouble();
        double min = getDialogRepresentation().getMin();
        double max = getDialogRepresentation().getMax();
        if (getDialogRepresentation().getUseMin() && value < min) {
            throw new InvalidSettingsException("The set double " + value
                    + " is smaller than the allowed minimum of " + min);
        }
        if (getDialogRepresentation().getUseMax() && value > max) {
            throw new InvalidSettingsException("The set double " + value
                    + " is bigger than the allowed maximum of " + max);
        }
        pushFlowVariableDouble(getDialogRepresentation().getFlowVariableName(), getViewValue().getDouble());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Auto-generated method stub

    }
    
    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final DoubleInputQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
