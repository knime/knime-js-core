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
import org.knime.core.node.port.PortType;
import org.knime.core.node.wizard.WizardNode;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @param <CNF> The concrete implementation of an {@link QuickFormConfigurationContent}.
 * @param <V> The value type.
 *
 */
public abstract class QuickFormNodeModel<CNF extends DialogNodeRepresentation, V> 
        extends NodeModel implements DialogNode<CNF, V>, WizardNode<CNF> {

	/**
	 * Creates a new quickform model with the given number (and types!) of input and output types.
	 * @param inPortTypes an array of non-null in-port types
	 * @param outPortTypes an array of non-null out-port types
	 */
	protected QuickFormNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
		super(inPortTypes, outPortTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
	    createNodeRepresentation().saveToNodeSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
	    createNodeRepresentation().loadFromNodeSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// do nothing
	}
}
