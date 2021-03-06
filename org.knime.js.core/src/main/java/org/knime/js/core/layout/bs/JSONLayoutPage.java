/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   24.09.2013 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.core.layout.bs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.js.core.JSONWebNodeModifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
@JsonAutoDetect
public class JSONLayoutPage {
    private List<JSONLayoutRow> m_rows = new ArrayList<JSONLayoutRow>(0);
    /**
     * Parent layout legacy mode flag for Visual Layout Editor. Can be modified on a
     * node-by-node basis in {@link JSONLayoutViewContent} via the Advanced Layout Editor.
     * Controls legacy rendering mode. Used for re-written views and toggles iFrame
     * rendering. Default value provided for backwards compatibility.
     *
     * @since 4.2
     */
    private boolean m_parentLayoutLegacyMode = false;

    /**
     * @return the content
     */
    @JsonProperty("rows")
    public List<JSONLayoutRow> getRows() {
        return m_rows;
    }

    /**
     * @param rows the content to set
     */
    @JsonProperty("rows")
    public void setRows(final List<JSONLayoutRow> rows) {
        m_rows = rows;
    }


    /**
     * @return parentLayoutLegacyMode
     * @since 4.2
     */
    public boolean getParentLayoutLegacyMode() {
        return m_parentLayoutLegacyMode;
    }

    /**
     * @param parentLayoutLegacyMode the parentLayoutLegacyMode to set
     * @since 4.2
     */
    public void setParentLayoutLegacyMode(final boolean parentLayoutLegacyMode) {
        m_parentLayoutLegacyMode = parentLayoutLegacyMode;
    }

    /**
     * @return a pre-configured {@link ObjectMapper} instance handling polymorphism on layout classes and omitting empty fields
     */
    public static ObjectMapper getConfiguredObjectMapper() {
        ObjectMapper mapper = getBaseObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        return mapper;
    }

    /**
     * @return a pre-configured {@link ObjectMapper} instance handling polymorphism on layout classes including empty fields
     */
    public static ObjectMapper getConfiguredVerboseObjectMapper() {
        ObjectMapper mapper = getBaseObjectMapper();
        mapper.setSerializationInclusion(Include.ALWAYS);
        return mapper;
    }

    private static ObjectMapper registerSubtypes(final ObjectMapper mapper) {
        mapper.registerSubtypes(
            new NamedType(JSONLayoutRow.class, "row"),
            new NamedType(JSONLayoutViewContent.class, "view"),
            new NamedType(JSONLayoutHTMLContent.class, "html"),
            new NamedType(JSONNestedLayout.class, "nestedLayout"),
            // Added 4.3
            new NamedType(JSONLayoutConfigurationContent.class, "configuration")
                );
        return mapper;
    }

    private static ObjectMapper getBaseObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new Jdk8Module());
        // as a POJO-parent of JSONWebNodes, we must register a custom serialization modifier
        mapper.registerModule(
            new SimpleModule().setSerializerModifier(new JSONWebNodeModifier())
        );
        registerSubtypes(mapper);
        return mapper;
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
        JSONLayoutPage other = (JSONLayoutPage)obj;
        return new EqualsBuilder()
                .append(m_rows, other.m_rows)
                .append(m_parentLayoutLegacyMode, other.m_parentLayoutLegacyMode)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_rows)
                .append(m_parentLayoutLegacyMode)
                .toHashCode();
    }
}
