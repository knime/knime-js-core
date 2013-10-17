package org.knime.js.base.node.quickform.input.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class DateStringInputQuickFormNodeModel extends QuickFormFlowVariableNodeModel
        <DateStringInputQuickFormRepresentation, DateStringInputQuickFormValue, DateStringInputQuickFormViewContent> {

    /**
     * Format for the date to string and string to date operations.
     */
    static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected DateStringInputQuickFormRepresentation createNodeRepresentation() {
        return new DateStringInputQuickFormRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateStringInputQuickFormValue createNodeValue() {
        return new DateStringInputQuickFormValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateStringInputQuickFormViewContent createViewContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateStringInputQuickFormViewContent createEmptyInstance() {
        // TODO Auto-generated method stub
        return null;
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
    public void loadViewContent(final DateStringInputQuickFormViewContent viewContent) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        pushFlowVariableString(getNodeRepresentation().getFlowVariableName(),
                FORMAT.format(getNodeValue().getDate()));
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

}
