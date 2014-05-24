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
package org.knime.js.base.node.quickform.selection.column;

import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
@SuppressWarnings("unchecked")
public class ColumnSelectionQuickFormNodeDialog extends QuickFormNodeDialog {

    private final ColumnSelectionPanel m_defaultField;

    private final ColumnSelectionPanel m_columnField;
    
    private final JComboBox<String> m_type;
    
    private String[] m_possibleColumns;

    /** Constructors, inits fields calls layout routines. */
    ColumnSelectionQuickFormNodeDialog() {
        m_type = new JComboBox<String>(SingleSelectionComponentFactory.listSingleSelectionComponents());
        m_defaultField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_columnField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Selection type: ", m_type, panelWithGBLayout, gbc);
        addPairToPanel("Default column: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Column selection: ", m_columnField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        DataTableSpec spec = (DataTableSpec) specs[0];
        m_defaultField.update(spec, null);
        m_columnField.update(spec, null);
        m_possibleColumns = spec.getColumnNames();
        ColumnSelectionQuickFormRepresentation representation = new ColumnSelectionQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        String selectedDefault = representation.getDefaultColumn();
        if (selectedDefault.isEmpty()) {
            List<DataColumnSpec> cspecs = m_defaultField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedDefault = cspecs.get(0).getName();
            }
        }
        m_defaultField.setSelectedColumn(selectedDefault);
        ColumnSelectionQuickFormValue value = new ColumnSelectionQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        String selectedColumn = value.getColumn();
        if (selectedColumn.isEmpty()) {
            List<DataColumnSpec> cspecs = m_columnField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedColumn = cspecs.get(0).getName();
            }
        }
        m_columnField.setSelectedColumn(selectedColumn);
        m_type.setSelectedItem(representation.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ColumnSelectionQuickFormRepresentation representation = new ColumnSelectionQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultColumn(m_defaultField.getSelectedColumn());
        representation.setType((String)m_type.getSelectedItem());
        representation.setPossibleColumns(m_possibleColumns);
        representation.saveToNodeSettings(settings);
        ColumnSelectionQuickFormValue value = new ColumnSelectionQuickFormValue();
        value.setColumn(m_columnField.getSelectedColumn());
        value.saveToNodeSettings(settings);
    }

}
