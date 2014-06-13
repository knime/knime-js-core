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
 *   Jun 12, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.input.molecule;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

/**
 *
 * @author winter
 */
public class MoleculeStringInputQuickFormConfig extends QuickFormFlowVariableConfig {

    private static final String CFG_FORMAT = "format";
    private static final String DEFAULT_FORMAT = MoleculeStringInputQuickFormRepresentation.DEFAULT_FORMATS[0];
    private String m_format = DEFAULT_FORMAT;
    private static final String CFG_DEFAULT = "default";
    private String m_defaultValue = "";
    private static final String CFG_STRING = "moleculeString";
    private static final String DEFAULT_STRING = "";
    private String m_moleculeString = DEFAULT_STRING;

    String getFormat() {
        return m_format;
    }

    void setFormat(final String format) {
        m_format = format;
    }

    String getDefaultValue() {
        return m_defaultValue;
    }

    void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

    String getMoleculeString() {
        return m_moleculeString;
    }

    void setMoleculeString(final String moleculeString) {
        m_moleculeString = moleculeString;
    }

    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_FORMAT, m_format);
        settings.addString(CFG_DEFAULT, m_defaultValue);
        settings.addString(CFG_STRING, m_moleculeString);
    }

    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_format = settings.getString(CFG_FORMAT);
        m_defaultValue = settings.getString(CFG_DEFAULT);
        m_moleculeString = settings.getString(CFG_STRING);
    }

    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_format = settings.getString(CFG_FORMAT, DEFAULT_FORMAT);
        m_defaultValue = settings.getString(CFG_DEFAULT, DEFAULT_STRING);
        m_moleculeString = settings.getString(CFG_STRING, DEFAULT_STRING);
    }

}
