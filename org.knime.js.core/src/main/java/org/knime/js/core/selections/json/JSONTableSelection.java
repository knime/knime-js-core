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
 *   17 Aug 2016 (albrecht): created
 */
package org.knime.js.core.selections.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
public class JSONTableSelection {

    private SelectionMethod m_selectionMethod = SelectionMethod.SELECTION;
    private SelectionElement[] m_elements = null;

    //private boolean m_inverse = false;

    public static enum SelectionMethod {
        SELECTION,
        FILTER;

        private static Map<String, SelectionMethod> namesMap = new HashMap<String, SelectionMethod>(2);

        static {
            namesMap.put("selection", SelectionMethod.SELECTION);
            namesMap.put("filter", SelectionMethod.FILTER);
        }

        @JsonCreator
        public static SelectionMethod forValue(final String value) throws JsonMappingException {
            SelectionMethod method = namesMap.get(value.toLowerCase());
            if (method == null) {
                throw new JsonMappingException(null, value + " is not a valid selection method.");
            }
            return method;
        }

        @JsonValue
        public String toValue() {
            for (Entry<String, SelectionMethod> entry : namesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }
            return null;
        }
    }

    /**
     * @return the selectionMethod
     */
    public SelectionMethod getSelectionMethod() {
        return m_selectionMethod;
    }

    /**
     * @param selectionMethod the selectionMethod to set
     */
    public void setSelectionMethod(final SelectionMethod selectionMethod) {
        m_selectionMethod = selectionMethod;
    }

    /**
     * @return the inverseSelection
     */
    /*public boolean getInverse() {
        return m_inverse;
    }*/

    /**
     * @param inverse the inverseSelection to set
     */
    /*public void setInverse(final boolean inverse) {
        m_inverse = inverse;
    }*/

    /**
     * @return the elements
     */
    public SelectionElement[] getElements() {
        return m_elements;
    }

    /**
     * @param elements the elements to set
     */
    public void setElements(final SelectionElement[] elements) {
        m_elements = elements;
    }

    @JsonIgnore
    public static JSONTableSelection getEmptySelection() {
        return new JSONTableSelection();
    }
}
