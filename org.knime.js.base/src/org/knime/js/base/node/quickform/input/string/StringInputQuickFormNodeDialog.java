/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.js.base.node.quickform.input.string;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
public class StringInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final RegexPanel m_regexField;
    
    private final JTextField m_defaultField;

    private final JTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    StringInputQuickFormNodeDialog() {
        m_regexField = new RegexPanel();
        m_defaultField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Regular Expression: ", m_regexField.getRegexPanel(), panelWithGBLayout, gbc);
        addPairToPanel("Validation error message: ", m_regexField.getErrorMessagePanel(), panelWithGBLayout, gbc);
        addPairToPanel("Common Regular Expressions: ",
                m_regexField.getCommonRegexesPanel(), panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("String Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        StringInputQuickFormRepresentation representation = new StringInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_regexField.setRegex(representation.getRegex());
        m_regexField.setErrorMessage(representation.getErrorMessage());
        m_defaultField.setText(representation.getDefaultValue());
        StringInputQuickFormValue value = new StringInputQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.setText(value.getString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_regexField.commitRegexHistory();
        StringInputQuickFormRepresentation representation = new StringInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setRegex(m_regexField.getRegex());
        representation.setErrorMessage(m_regexField.getErrorMessage());
        representation.setDefaultValue(m_defaultField.getText());
        representation.saveToNodeSettings(settings);
        StringInputQuickFormValue value = new StringInputQuickFormValue();
        value.setString(m_valueField.getText());
        value.saveToNodeSettings(settings);
    }

}
