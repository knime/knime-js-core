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
package org.knime.js.core.selections;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    defaultImpl = RowSelection.class
    )
@JsonSubTypes({
    @Type(value = RowSelection.class, name = "row"),
    @Type(value = RangeSelection.class, name = "range")
    })
public abstract class SelectionElement {

    private String m_id;
    private String[] m_rows;

    //private boolean m_inverse = false;
    //private SetOperation m_operation = SetOperation.ADD;

    /*public static enum SetOperation {
        ADD,
        SUBTRACT;

        private static Map<String, SetOperation> namesMap = new HashMap<String, SetOperation>(2);

        static {
            namesMap.put("add", SetOperation.ADD);
            namesMap.put("subtract", SetOperation.SUBTRACT);
        }

        @JsonCreator
        public static SetOperation forValue(final String value) throws JsonMappingException {
            SetOperation method = namesMap.get(value.toLowerCase());
            if (method == null) {
                throw new JsonMappingException(null, value + " is not a valid set operation.");
            }
            return method;
        }

        @JsonValue
        public String toValue() {
            for (Entry<String, SetOperation> entry : namesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }
            return null;
        }
    } */

    /**
     * @return the id
     */
    public String getId() {
        return m_id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final String id) {
        m_id = id;
    }

    /**
     * @return the rows
     */
    public String[] getRows() {
        return m_rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(final String[] rows) {
        m_rows = rows;
    }

    /**
     * @return the inverse
     */
    /*public boolean getInverse() {
        return m_inverse;
    }*/

    /**
     * @param inverse the inverse to set
     */
    /*public void setInverse(final boolean inverse) {
        m_inverse = inverse;
    }*/

    /**
     * @return the operation
     */
    /*public SetOperation getOperation() {
        return m_operation;
    }*/

    /**
     * @param operation the operation to set
     */
    /*public void setOperation(final SetOperation operation) {
        m_operation = operation;
    }*/
}
