/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   18 Aug 2016 (albrecht): created
 */
package org.knime.js.core.selections.json;

import java.util.Collection;
import java.util.HashSet;

import javax.naming.OperationNotSupportedException;

import org.knime.core.data.DataCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.property.filter.FilterModel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
public class RangeSelection extends SelectionElement {

    private AbstractColumnRangeSelection[] m_columns;

    /**
     * @return the columns
     */
    public AbstractColumnRangeSelection[] getColumns() {
        return m_columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(final AbstractColumnRangeSelection[] columns) {
        m_columns = columns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterModel createFilterModel() throws OperationNotSupportedException {
        if (m_columns == null || m_columns.length == 0 || m_columns[0] == null) {
            throw new OperationNotSupportedException("Element does not contain any filter definitions.");
        }
        if (m_columns.length > 1) {
            throw new OperationNotSupportedException("Element contains more than one filter definition.");
        }
        FilterModel model = null;
        if (m_columns[0] instanceof NumericColumnRangeSelection) {
            NumericColumnRangeSelection fDef = (NumericColumnRangeSelection)m_columns[0];
            model = FilterModel.newRangeModel(fDef.getMinimum(), fDef.getMaximum(), fDef.getMinimumInclusive(), fDef.getMaximumInclusive());
        } else  if (m_columns[0] instanceof NominalColumnRangeSelection) {
            NominalColumnRangeSelection fDef = (NominalColumnRangeSelection)m_columns[0];
            Collection<DataCell> filterValues = new HashSet<DataCell>();
            String[] values = fDef.getValues();
            if (values != null) {
                for (String value : values) {
                    filterValues.add(new StringCell(value));
                }
            }
            FilterModel.newNominalModel(filterValues);
        } else {
            throw new OperationNotSupportedException(m_columns[0].getClass().getSimpleName() + " is not supported.");
        }
        return model;
    }

}
