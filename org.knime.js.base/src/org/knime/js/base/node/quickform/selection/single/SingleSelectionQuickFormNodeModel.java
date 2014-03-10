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
        QuickFormFlowVariableNodeModel<SingleSelectionQuickFormRepresentation, SingleSelectionQuickFormValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_selection_single";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableString(getDialogRepresentation().getFlowVariableName(), getViewValue().getVariableValue());
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
    @Override
    public SingleSelectionQuickFormRepresentation createEmptyViewRepresentation() {
        return new SingleSelectionQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSelectionQuickFormValue createEmptyViewValue() {
        return new SingleSelectionQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(
            final SingleSelectionQuickFormValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

}
