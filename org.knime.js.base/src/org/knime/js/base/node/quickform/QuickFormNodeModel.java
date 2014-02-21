package org.knime.js.base.node.quickform;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeRepresentation;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @param <DREP> The configuration content of the quickform node.
 * @param <DVAL> The node value implementation of the quickform node.
 * @param <VREP> The configuration content of the quickform node.
 * @param <VVAL> The node value implementation of the quickform node.
 * 
 */
public abstract class QuickFormNodeModel<DREP extends DialogNodeRepresentation<DVAL>, DVAL extends DialogNodeValue, VREP extends WebViewContent, VVAL extends WebViewContent>
        extends NodeModel implements DialogNode<DREP, DVAL>, WizardNode<VREP, VVAL> {

    private DREP m_dialogRepresentation;

    private DVAL m_dialogValue;
    
    private VREP m_viewRepresentation;
    
    private VVAL m_viewValue;
    
    /**
     * Creates a new quickform model with the given number (and types!) of input
     * and output types.
     * 
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected QuickFormNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
        m_dialogRepresentation = createEmptyDialogRepresentation();
        m_dialogValue = createEmptyDialogValue();
        m_viewRepresentation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        getDialogRepresentation().saveToNodeSettings(settings);
        getDialogValue().saveToNodeSettings(settings);
        getViewRepresentation().saveToNodeSettings(settings);
        getViewValue().saveToNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        DREP representation = getDialogRepresentation();
        representation.loadFromNodeSettings(settings);
        DVAL value = getDialogValue();
        value.loadFromNodeSettings(settings);
        VREP viewRepresentation = getViewRepresentation();
        viewRepresentation.loadFromNodeSettings(settings);
        VVAL viewValue = getViewValue();
        viewValue.loadFromNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DREP getDialogRepresentation() {
        return m_dialogRepresentation;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DVAL getDialogValue() {
        return m_dialogValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public VREP getViewRepresentation() {
        return m_viewRepresentation;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public VVAL getViewValue() {
        return m_viewValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VVAL viewContent) {
        m_viewValue = viewContent;
    };
    
    /**
     * @return The node representation
     */
    protected abstract DREP createEmptyDialogRepresentation();

    /**
     * @return The node value
     */
    protected abstract DVAL createEmptyDialogValue();
}
