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
 *   21.10.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.output.filedownload;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class FileDownloadRepresentation extends JSONViewContent {

    private String m_label;
    private String m_description;
    private String m_linkTitle;

    private static final String SETTINGS_PATH = "path";
    private static final String DEFAULT_PATH = "";
    private String m_path = DEFAULT_PATH;

    /**
     * @return the label
     */
    @JsonProperty("label")
    public String getLabel() {
        return m_label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(final String label) {
        m_label = label;
    }

    /**
     * @return the description
     */
    @JsonProperty("description")
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        m_description = description;
    }

    /**
     * @return the linkTitle
     */
    @JsonProperty("linkTitle")
    public String getLinkTitle() {
        return m_linkTitle;
    }

    /**
     * @param linkTitle the linkTitle to set
     */
    public void setLinkTitle(final String linkTitle) {
        m_linkTitle = linkTitle;
    }

    /**
     * @param path the path to set
     */
    @JsonProperty("path")
    public void setPath(final String path) {
        m_path = path;
    }

    /**
     * @return the path
     */
    @JsonProperty("path")
    public String getPath() {
        return m_path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(SETTINGS_PATH, m_path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_path = settings.getString(SETTINGS_PATH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileDownloadRepresentation.class);
        sb.append(", label=");
        sb.append(m_label);
        sb.append(", description=");
        sb.append(m_description);
        sb.append(", linkTitle=");
        sb.append(m_linkTitle);
        sb.append(", path=");
        sb.append(m_path);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(m_label)
            .append(m_description)
            .append(m_linkTitle)
            .append(m_path).toHashCode();
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
        FileDownloadRepresentation other = (FileDownloadRepresentation)obj;
        return new EqualsBuilder()
            .append(m_label, other.m_label)
            .append(m_description, other.m_description)
            .append(m_linkTitle, other.m_linkTitle)
            .append(m_path, other.m_path).isEquals();
    }

}
