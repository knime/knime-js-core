/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.selection.single;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.dialog.selection.single.DropdownComponent;
import org.knime.js.base.dialog.selection.single.ListComponent;
import org.knime.js.base.dialog.selection.single.RadioButtonComponent;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponent;
import org.knime.js.base.node.quickform.QuickFormDialogPanel;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
@SuppressWarnings("serial")
public class SingleSelectionQuickFormDialogPanel extends QuickFormDialogPanel<SingleSelectionQuickFormValue> {

    private SingleSelectionComponent m_selectionComponent;

    /**
     * @param representation The representation containing layout information
     */
    public SingleSelectionQuickFormDialogPanel(final SingleSelectionQuickFormRepresentation representation) {
        String[] choices = representation.getPossibleChoices();
        if (representation.getType().equals(SingleSelectionType.RADIOBUTTONS_VERTICAL.getName())) {
            m_selectionComponent = new RadioButtonComponent(choices, true);
        } else if (representation.getType().equals(SingleSelectionType.RADIOBUTTONS_HORIZONTAL.getName())) {
            m_selectionComponent = new RadioButtonComponent(choices, false);
        } else if (representation.getType().equals(SingleSelectionType.LIST.getName())) {
            m_selectionComponent = new ListComponent(choices);
        } else if (representation.getType().equals(SingleSelectionType.DROPDOWN.getName())) {
            m_selectionComponent = new DropdownComponent(choices);
        } 
        addComponent(m_selectionComponent.getComponent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveNodeValue(final SingleSelectionQuickFormValue value) throws InvalidSettingsException {
        value.setVariableValue(m_selectionComponent.getSelection());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final SingleSelectionQuickFormValue value) {
        m_selectionComponent.setSelection(value.getVariableValue());
    }

}
