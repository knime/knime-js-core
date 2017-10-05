/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.com; Email: contact@knime.com
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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class SampleSelectionPrinter {

    /**
     * @param args
     * @throws JsonProcessingException
     */
    public static void main(final String[] args) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        //mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.setSerializationInclusion(Include.ALWAYS);

        System.out.println("EMPTY SELECTION");
        JSONTableSelection selection = JSONTableSelection.getEmptySelection();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println();

        /*System.out.println("SELECT ALL");
        selection = JSONTableSelection.getSelectAll();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println(); */

        System.out.println("SELECT SINGLE ROW");
        selection = JSONTableSelection.getEmptySelection();
        RowSelection rowS = new RowSelection();
        //rowS.setOperation(SetOperation.ADD);
        rowS.setRows(new String[]{"Row0"});
        selection.setElements(new SelectionElement[]{rowS});
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println();

        /*System.out.println("SELECT ALL EXCEPT ONE ROW");
        selection = JSONTableSelection.getSelectAll();
        selection.setElements(new SelectionElement[]{rowS});
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println();*/

        System.out.println("SELECT SINGLE NUMERIC COLUMN RANGE");
        selection = JSONTableSelection.getEmptySelection();
        NumericColumnRangeSelection nRange = new NumericColumnRangeSelection();
        nRange.setColumnName("Column0");
        nRange.setMinimum(2);
        nRange.setMaximum(5.5);
        RangeSelection rangeS = new RangeSelection();
        rangeS.setColumns(new AbstractColumnRangeSelection[]{nRange});
        rangeS.setRows(new String[]{"Row3", "Row25", "Row27", "Row34"});
        selection.setElements(new SelectionElement[]{rangeS});
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println();

        System.out.println("SELECT RECTANGLE (2 COLUMN RANGE)");
        selection = JSONTableSelection.getEmptySelection();
        NumericColumnRangeSelection nRange2 = new NumericColumnRangeSelection();
        nRange2.setColumnName("Column1");
        nRange2.setMinimum(-0.46);
        //nRange2.setMaximum(3.0002);
        rangeS.setColumns(new AbstractColumnRangeSelection[]{nRange, nRange2});
        rangeS.setRows(new String[]{"Row3", "Row25", "Row34"});
        selection.setElements(new SelectionElement[]{rangeS});
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println();

        /*System.out.println("SELECT NUMERIC COLUMN RANGE, EXCEPT FOR TWO ROWS");
        selection = JSONTableSelection.getEmptySelection();
        nRange = new NumericColumnRangeSelection();
        nRange.setColumnName("Column0");
        nRange.setMinimum(2);
        nRange.setMaximum(5.5);
        nRange.setMaximumInclusive(false);
        rangeS.setColumns(new AbstractColumnRangeSelection[]{nRange});
        rowS.setOperation(SetOperation.SUBTRACT);
        rowS.setRows(new String[]{"Row264", "Row93"});
        selection.setElements(new SelectionElement[]{rangeS, rowS});
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println();*/

        System.out.println("SELECT NOMINAL COLUMN RANGE");
        selection = JSONTableSelection.getEmptySelection();
        NominalColumnRangeSelection noRange = new NominalColumnRangeSelection();
        noRange.setColumnName("Column1");
        noRange.setValues(new String[]{"Iris Setosa", "Iris Versicolor"});
        rangeS.setColumns(new AbstractColumnRangeSelection[]{noRange});
        rangeS.setId("ValueFilter:21");
        rangeS.setRows(new String[]{"Row3", "Row25", "Row27", "Row34"});
        selection.setElements(new SelectionElement[]{rangeS});
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(selection));
        System.out.println();

    }

}
