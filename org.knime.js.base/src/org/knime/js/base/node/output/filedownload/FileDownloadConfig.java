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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.util.CheckUtils;
import org.knime.js.base.util.LabeledViewConfig;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class FileDownloadConfig extends LabeledViewConfig {

    private static final String CFG_LINK_TITLE = "linktitle";
    private static final String DEFAULT_LINK_TITLE = "";
    private String m_linkTitle = DEFAULT_LINK_TITLE;

    private static final String CFG_FLOW_VARIABLE = "flowvariable";
    private static final String DEFAULT_FLOW_VARIABLE = "";
    private String m_flowVariable = DEFAULT_FLOW_VARIABLE;

    private static final String CFG_RESOURCE_NAME = "resourceName";
    private String m_resourceName = "";

    /**
     * @return the linkTitle
     */
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
     * @return the flowVariable
     */
    public String getFlowVariable() {
        return m_flowVariable;
    }

    /**
     * @param flowVariable the flowVariable to set
     */
    public void setFlowVariable(final String flowVariable) {
        m_flowVariable = flowVariable;
    }

    /**
     * Sets the name for the resource created by the node.
     *
     * @param name the name
     * @throws InvalidSettingsException if the name in invalid
     */
    public void setResourceName(final String name) throws InvalidSettingsException {
        CheckUtils.checkSetting(StringUtils.isNotEmpty(name), "parameter name must not be null or empty");
        CheckUtils.checkSetting(DialogNode.PARAMETER_NAME_PATTERN.matcher(name).matches(),
            "Parameter doesn't match pattern - must start with character, followed by other characters, digits, "
            + "or single dashes or underscores:\n  Input: %s\n  Pattern: %s",
            name, DialogNode.PARAMETER_NAME_PATTERN.pattern());
        m_resourceName = name;
    }

    /**
     * Returns the name of the resource created by the node.
     *
     * @return a resource name
     */
    public String getResourceName() {
        return m_resourceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_LINK_TITLE, m_linkTitle);
        settings.addString(CFG_FLOW_VARIABLE, m_flowVariable);
        settings.addString(CFG_RESOURCE_NAME, m_resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_linkTitle = settings.getString(CFG_LINK_TITLE);
        m_flowVariable = settings.getString(CFG_FLOW_VARIABLE);
        // new in 2.12
        m_resourceName = settings.getString(CFG_RESOURCE_NAME, "file-download");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_linkTitle = settings.getString(CFG_LINK_TITLE, DEFAULT_LINK_TITLE);
        m_flowVariable = settings.getString(CFG_FLOW_VARIABLE, DEFAULT_FLOW_VARIABLE);
        m_resourceName = settings.getString(CFG_RESOURCE_NAME, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", linkTitle=");
        sb.append(m_linkTitle);
        sb.append(", flowvariable=");
        sb.append(m_flowVariable);
        sb.append(", parameterName=");
        sb.append(m_resourceName);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(m_linkTitle)
                .append(m_flowVariable)
                .append(m_resourceName)
                .toHashCode();
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
        FileDownloadConfig other = (FileDownloadConfig)obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(m_linkTitle, other.m_linkTitle)
                .append(m_flowVariable, other.m_flowVariable)
                .append(m_resourceName, other.m_resourceName)
                .isEquals();
    }
}
