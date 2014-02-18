package org.knime.js.base.node.quickform.selection.single;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class SingleSelectionQuickFormNodeModel
        extends
        QuickFormFlowVariableNodeModel<SingleSelectionQuickFormRepresentation, SingleSelectionQuickFormValue,
        SingleSelectionQuickFormViewRepresentation, SingleSelectionQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected SingleSelectionQuickFormRepresentation createEmptyDialogRepresentation() {
        return new SingleSelectionQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SingleSelectionQuickFormValue createEmptyDialogValue() {
        return new SingleSelectionQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getDialogValue().getVariableValue());
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

    /**
     * {@inheritDoc}
     */
    public SingleSelectionQuickFormViewRepresentation createEmptyViewRepresentation() {
        return new SingleSelectionQuickFormViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    public SingleSelectionQuickFormValue createEmptyViewValue() {
        return new SingleSelectionQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    public ValidationError validateViewValue(
            SingleSelectionQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void loadViewValue(SingleSelectionQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        
    }

}
