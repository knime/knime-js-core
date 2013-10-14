package org.knime.js.base.node.quickform.input.string;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 *
 */
public class StringInputQuickFormNodeFactory extends
		NodeFactory<StringInputQuickFormNodeModel> implements
		WizardNodeFactoryExtension<StringInputQuickFormNodeModel, StringInputQuickFormViewContent> {

	@Override
	public StringInputQuickFormNodeModel createNodeModel() {
		return new StringInputQuickFormNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<StringInputQuickFormNodeModel> createNodeView(
			int viewIndex, StringInputQuickFormNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new StringInputQuickFormNodeDialog();
	}
}
