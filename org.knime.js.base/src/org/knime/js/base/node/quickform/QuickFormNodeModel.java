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
 * @param <REP> The configuration content of the quickform node.
 * @param <VAL> The node value implementation of the quickform node.
 * 
 */
public abstract class QuickFormNodeModel<REP extends DialogNodeRepresentation<VAL> & WebViewContent,
        VAL extends DialogNodeValue & WebViewContent>
        extends NodeModel implements DialogNode<REP, VAL>, WizardNode<REP, VAL> {

    private REP m_representation;

    private VAL m_value;
    
    /**
     * Creates a new quickform model with the given number (and types!) of input
     * and output types.
     * 
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected QuickFormNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
        m_representation = createEmptyViewRepresentation();
        m_value = createEmptyViewValue();
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
        REP representation = getDialogRepresentation();
        representation.loadFromNodeSettings(settings);
        VAL value = getDialogValue();
        value.loadFromNodeSettings(settings);
        REP viewRepresentation = getViewRepresentation();
        viewRepresentation.loadFromNodeSettings(settings);
        VAL viewValue = getViewValue();
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
    public REP getDialogRepresentation() {
        return m_representation;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getDialogValue() {
        return m_value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        return m_representation;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getViewValue() {
        return m_value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewContent) {
        m_value = viewContent;
    };
}
