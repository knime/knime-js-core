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
 * ---------------------------------------------------------------------
 *
 * Created on 16.09.2013 by Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
package org.knime.js.core;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONWebNodePage extends JSONViewContent {

    private final String m_version;
    private JSONWebNodePageConfiguration m_configuration;
    private Map<String, JSONWebNode> m_webNodes;
    // since 4.3
    private boolean hasArtifactsView = true;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean getHasArtifactsView() {
        return hasArtifactsView;
    }

    /**
     * @param configuration
     * @param webNodes
     *
     */
    public JSONWebNodePage(final JSONWebNodePageConfiguration configuration,
        final Map<String, JSONWebNode> webNodes) {
        this(configuration, webNodes, KNIMEConstants.VERSION);
    }

    @JsonCreator
    private JSONWebNodePage(@JsonProperty("webNodePageConfiguration") final JSONWebNodePageConfiguration configuration,
        @JsonProperty("webNodes") final Map<String, JSONWebNode> webNodes,
        @JsonProperty("version") final String version) {
        m_version = version;
        m_configuration = configuration;
        m_webNodes = webNodes;
    }

    /**
     * @return the version
     */
    @JsonProperty("version")
    public String getVersion() {
        return m_version;
    }

    /**
     * @return the configuration
     */
    @JsonProperty("webNodePageConfiguration")
    public JSONWebNodePageConfiguration getWebNodePageConfiguration() {
        return m_configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    @JsonProperty("webNodePageConfiguration")
    public void setWebNodePageConfiguration(final JSONWebNodePageConfiguration configuration) {
        m_configuration = configuration;
    }

    /**
     * @return the webNodes
     */
    @JsonProperty("webNodes")
    public Map<String, JSONWebNode> getWebNodes() {
        return m_webNodes;
    }

    /**
     * @param webNodes the webNodes to set
     */
    @JsonProperty("webNodes")
    public void setWebNodes(final Map<String, JSONWebNode> webNodes) {
        m_webNodes = webNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) { /* not needed so far */ }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException { /* not needed so far */ }

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
        JSONWebNodePage other = (JSONWebNodePage)obj;
        return new EqualsBuilder()
                .append(m_version, other.m_version)
                .append(m_configuration, other.m_configuration)
                .append(m_webNodes, other.m_webNodes)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_version)
                .append(m_configuration)
                .append(m_webNodes)
                .toHashCode();
    }
}
