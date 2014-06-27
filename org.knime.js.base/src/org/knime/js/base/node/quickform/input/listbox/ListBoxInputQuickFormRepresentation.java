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
package org.knime.js.base.node.quickform.input.listbox;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ListBoxInputQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<ListBoxInputQuickFormValue> {

    private static final String CFG_REGEX = "regex";

    private static final String DEFAULT_REGEX = "";

    private String m_regex = DEFAULT_REGEX;

    private static final String CFG_ERROR_MESSAGE = "error_message";

    private static final String DEFAULT_ERROR_MESSAGE = "";

    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;

    private static final String CFG_SEPARATOR = "separator";

    private static final String DEFAULT_SEPARATOR = "\\n";

    private String m_separator = DEFAULT_SEPARATOR;

    private static final String CFG_DEFAULT = "default";

    private String m_defaultValue = "";

    private static final String CFG_OMIT_EMPTY = "omit_empty";

    private static final boolean DEFAULT_OMIT_EMPTY = true;

    private boolean m_omitEmpty = DEFAULT_OMIT_EMPTY;

    /**
     * @return the regex
     */
    @JsonProperty("regex")
    public String getRegex() {
        return m_regex;
    }

    /**
     * @param regex the regex to set
     */
    @JsonProperty("regex")
    public void setRegex(final String regex) {
        m_regex = regex;
    }

    /**
     * @return the errorMessage
     */
    @JsonProperty("errormessage")
    public String getErrorMessage() {
        return m_errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    @JsonProperty("errormessage")
    public void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }

    /**
     * @return the separator
     */
    @JsonProperty("separator")
    public String getSeparator() {
        return m_separator;
    }

    /**
     * @param separator the separator to set
     */
    @JsonProperty("separator")
    public void setSeparator(final String separator) {
        m_separator = separator;
    }

    /**
     * @return separatorRegex
     */
    @JsonProperty("separatorregex")
    public String getSeparatorRegex() {
        if (m_separator == null || m_separator.isEmpty()) {
            return m_separator;
        }
        StringBuilder sepString = new StringBuilder();
        for (int i = 0; i < m_separator.length(); i++) {
            if (i > 0) {
                sepString.append('|');
            }
            char c = m_separator.charAt(i);
            if (c == '|') {
                sepString.append("\\|");
            } else if (c == '\\') {
                if (i + 1 < m_separator.length()) {
                    if (m_separator.charAt(i + 1) == 'n') {
                        sepString.append("\\n");
                        i++;
                    } else if (m_separator.charAt(i + 1) == 't') {
                        sepString.append("\\t");
                        i++;
                    }
                }
            } else {
                // a real, non-specific char
                sepString.append("[" + c + "]");
            }
        }
        return sepString.toString();
    }

    /**
     * @return the omitEmpty
     */
    @JsonProperty("omitempty")
    public boolean getOmitEmpty() {
        return m_omitEmpty;
    }

    /**
     * @param omitEmpty the omitEmpty to set
     */
    @JsonProperty("omitempty")
    public void setOmitEmpty(final boolean omitEmpty) {
        m_omitEmpty = omitEmpty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        m_regex = settings.getString(CFG_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE);
        m_separator = settings.getString(CFG_SEPARATOR);
        m_omitEmpty = settings.getBoolean(CFG_OMIT_EMPTY);
        setDefaultValue(settings.getString(CFG_DEFAULT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_regex = settings.getString(CFG_REGEX, DEFAULT_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
        m_separator = settings.getString(CFG_SEPARATOR, DEFAULT_SEPARATOR);
        m_omitEmpty = settings.getBoolean(CFG_OMIT_EMPTY, DEFAULT_OMIT_EMPTY);
        setDefaultValue(settings.getString(CFG_DEFAULT, ""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_REGEX, m_regex);
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
        settings.addString(CFG_SEPARATOR, m_separator);
        settings.addBoolean(CFG_OMIT_EMPTY, m_omitEmpty);
        settings.addString(CFG_DEFAULT, getDefaultValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<ListBoxInputQuickFormValue> createDialogPanel() {
        ListBoxInputQuickFormDialogPanel panel = new ListBoxInputQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultValue
     */
    @JsonProperty("defaultvalue")
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    @JsonProperty("defaultvalue")
    public void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final ListBoxInputQuickFormValue value) {
        value.setString(getDefaultValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("regex=");
        sb.append(m_regex);
        sb.append(", ");
        sb.append("errorMessage=");
        sb.append(m_errorMessage);
        sb.append(", ");
        sb.append("separator=");
        sb.append(m_separator);
        sb.append(", ");
        sb.append("defaultValue=");
        sb.append(m_defaultValue);
        sb.append(", ");
        sb.append("omitEmpty=");
        sb.append(m_omitEmpty);
        return sb.toString();
    }

}
