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
 *   Jun 13, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.selection.multiple;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

/**
 *
 * @author winter
 */
public class MultipleSelectionQuickFormConfig extends QuickFormFlowVariableConfig {

    private static final String CFG_DEFAULT_VALUE = "default_value";

    private static final String[] DEFAULT_DEFAULT_VALUE = new String[0];

    private String[] m_defaultValue = DEFAULT_DEFAULT_VALUE;

    private static final String CFG_POSSIBLE_CHOICES = "possible_choices";

    private static final String[] DEFAULT_POSSIBLE_CHOICES = new String[0];

    private String[] m_possibleChoices = DEFAULT_POSSIBLE_CHOICES;

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = MultipleSelectionsComponentFactory.TWINLIST;

    private String m_type = DEFAULT_TYPE;

    private static final String CFG_VARIABLE_VALUE = "variable_value";

    private static final String[] DEFAULT_VARIABLE_VALUE = new String[0];

    private String[] m_variableValue = DEFAULT_VARIABLE_VALUE;

    String[] getDefaultValue() {
        return m_defaultValue;
    }

    void setDefaultValue(final String[] defaultValue) {
        m_defaultValue = defaultValue;
    }

    String[] getPossibleChoices() {
        return m_possibleChoices;
    }

    void setPossibleChoices(final String[] possibleChoices) {
        m_possibleChoices = possibleChoices;
    }

    String getType() {
        return m_type;
    }

    void setType(final String type) {
        m_type = type;
    }

    String[] getVariableValue() {
        return m_variableValue;
    }

    void setVariableValue(final String[] variableValue) {
        m_variableValue = variableValue;
    }

    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addStringArray(CFG_DEFAULT_VALUE, m_defaultValue);
        settings.addStringArray(CFG_POSSIBLE_CHOICES, m_possibleChoices);
        settings.addString(CFG_TYPE, m_type);
        settings.addStringArray(CFG_VARIABLE_VALUE, m_variableValue);
    }

    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_defaultValue = settings.getStringArray(CFG_DEFAULT_VALUE);
        m_possibleChoices = settings.getStringArray(CFG_POSSIBLE_CHOICES);
        m_type = settings.getString(CFG_TYPE);
        m_variableValue = settings.getStringArray(CFG_VARIABLE_VALUE);
    }

    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_defaultValue = settings.getStringArray(CFG_DEFAULT_VALUE, DEFAULT_DEFAULT_VALUE);
        m_possibleChoices = settings.getStringArray(CFG_POSSIBLE_CHOICES, DEFAULT_POSSIBLE_CHOICES);
        m_type = settings.getString(CFG_TYPE, DEFAULT_TYPE);
        m_variableValue = settings.getStringArray(CFG_VARIABLE_VALUE, DEFAULT_VARIABLE_VALUE);
    }

}
