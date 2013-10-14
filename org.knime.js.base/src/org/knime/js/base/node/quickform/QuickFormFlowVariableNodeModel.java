package org.knime.js.base.node.quickform;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.dialog.DialogNodeRepresentation;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.quickform.QuickFormConfigurationContent;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @param <CNF> The concrete implementation of an {@link QuickFormConfigurationContent}.
 * @param <V> The value type.
 *
 */
public abstract class QuickFormFlowVariableNodeModel<CNF extends DialogNodeRepresentation<V>, V> 
		extends QuickFormNodeModel<CNF, V> {

	/** Creates a new node model with no inports and one flow variable outport. */
	protected QuickFormFlowVariableNodeModel() {
		super(new PortType[0], new PortType[]{FlowVariablePortObject.TYPE});
	}
	
	/** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        createAndPushFlowVariable();
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }
    
    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        createAndPushFlowVariable();
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
    }
    
    /** Subclasses will publish their flow variables here. Called from
     * configure and execute.
     * @throws InvalidSettingsException If settings are invalid.
     */
    protected abstract void createAndPushFlowVariable()
            throws InvalidSettingsException;

}
