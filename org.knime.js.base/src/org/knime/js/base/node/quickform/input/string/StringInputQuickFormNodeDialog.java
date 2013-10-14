package org.knime.js.base.node.quickform.input.string;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.js.base.node.quickform.QuickFormNodeDialogPane;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class StringInputQuickFormNodeDialog extends QuickFormNodeDialogPane {

    private final JTextField m_regexField;
	private final JTextField m_valueField;

	/** Constructors, inits fields calls layout routines. */
	StringInputQuickFormNodeDialog() {
	    m_regexField = new JTextField(DEF_TEXTFIELD_WIDTH);
		m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
	    createAndAddTab();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
	    addPairToPanel("Regular Expression: ", m_regexField, panelWithGBLayout, gbc);
		addPairToPanel("String Value: ", m_valueField, panelWithGBLayout, gbc);
	}

}
