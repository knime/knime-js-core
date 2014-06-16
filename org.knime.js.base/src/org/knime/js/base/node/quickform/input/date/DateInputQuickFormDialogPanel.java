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
 *
 * History
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.input.date;

import java.util.Date;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.quickform.QuickFormDialogPanel;

/**
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
@SuppressWarnings("serial")
public class DateInputQuickFormDialogPanel extends QuickFormDialogPanel<DateInputQuickFormValue> {

    private JSpinner m_component;

    private DateInputQuickFormRepresentation m_representation;

    /**
     * @param representation Representation to get the date format
     */
    public DateInputQuickFormDialogPanel(final DateInputQuickFormRepresentation representation) {
        m_representation = representation;
        String format =
                representation.getWithTime() ? DateInputQuickFormNodeModel.DATE_TIME_FORMAT
                        : DateInputQuickFormNodeModel.DATE_FORMAT;
        m_component = new JSpinner(new SpinnerDateModel());
        m_component.setEditor(new JSpinner.DateEditor(m_component, format));
        addComponent(m_component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveNodeValue(final DateInputQuickFormValue value) throws InvalidSettingsException {
        value.setDate((Date)m_component.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final DateInputQuickFormValue value) {
        Date date = value.getDate() != null ? value.getDate() : m_representation.getDefaultValue();
        m_component.setValue(date);
    }

}