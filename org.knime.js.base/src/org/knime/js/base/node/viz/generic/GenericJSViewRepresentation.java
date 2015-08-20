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
 *   30.04.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.generic;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class GenericJSViewRepresentation extends JSONViewContent {

    private static final String JS_CODE = "jsCode";
    private static final String CSS_CODE = "cssCode";
    private static final String JS_DEPENDENCIES = "jsDependencies";
    private static final String CSS_DEPENDENCIES = "cssDependencies";

    private String m_jsCode;
    private String m_cssCode;
    private String[] m_jsDependencies;
    private String[] m_cssDependencies;
    private JSONDataTable m_table;

    /** Serialization constructor. Don't use. */
    public GenericJSViewRepresentation() { }

    /**
     * @param dependencies
     * @param jsCode
     * @param table
     */
    public GenericJSViewRepresentation(final String jsCode, final String cssCode, final String[] jsDependencies, final String[] cssDependencies, final JSONDataTable table) {
        m_jsDependencies = jsDependencies;
        m_cssDependencies = cssDependencies;
        m_jsCode = jsCode;
        m_cssCode = cssCode;
        m_table = table;
    }

    /**
     * @return the jsCode
     */
    public String getJsCode() {
        return m_jsCode;
    }

    /**
     * @param jsCode the jsCode to set
     */
    public void setJsCode(final String jsCode) {
        m_jsCode = jsCode;
    }

    /**
     * @return the cssCode
     */
    public String getCssCode() {
        return m_cssCode;
    }

    /**
     * @param cssCode the cssCode to set
     */
    public void setCssCode(final String cssCode) {
        m_cssCode = cssCode;
    }

    /**
     * @return the jsDependencies
     */
    public String[] getJsDependencies() {
        return m_jsDependencies;
    }

    /**
     * @param jsDependencies the jsDependencies to set
     */
    public void setJsDependencies(final String[] jsDependencies) {
        m_jsDependencies = jsDependencies;
    }

    /**
     * @return the cssDependencies
     */
    public String[] getCssDependencies() {
        return m_cssDependencies;
    }

    /**
     * @param cssDependencies the cssDependencies to set
     */
    public void setCssDependencies(final String[] cssDependencies) {
        m_cssDependencies = cssDependencies;
    }

    /**
     * @return the table
     */
    public JSONDataTable getTable() {
        return m_table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(final JSONDataTable table) {
        m_table = table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(JS_CODE, m_jsCode);
        settings.addString(CSS_CODE, m_cssCode);
        settings.addStringArray(JS_DEPENDENCIES, m_jsDependencies);
        settings.addStringArray(CSS_DEPENDENCIES, m_cssDependencies);
        if (m_table != null) {
            m_table.saveJSONToNodeSettings(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_jsCode = settings.getString(JS_CODE, "");
        m_cssCode = settings.getString(CSS_CODE, "");
        m_jsDependencies = settings.getStringArray(JS_DEPENDENCIES, new String[0]);
        m_cssDependencies = settings.getStringArray(CSS_DEPENDENCIES, new String[0]);
        m_table = JSONDataTable.loadFromNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        GenericJSViewRepresentation other = (GenericJSViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_jsCode, other.m_jsCode)
                .append(m_cssCode, other.m_cssCode)
                .append(m_jsDependencies, other.m_jsDependencies)
                .append(m_cssDependencies, other.m_cssDependencies)
                .append(m_table, other.m_table)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_jsCode)
                .append(m_cssCode)
                .append(m_jsDependencies)
                .append(m_cssDependencies)
                .append(m_table)
                .toHashCode();
    }
}
