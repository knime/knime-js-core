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
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.filter.column;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;
import org.knime.js.base.node.quickform.QuickFormDialogPanel;

/**
 * Sub node dialog panel for the column filter quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
@SuppressWarnings("serial")
public class ColumnFilterQuickFormDialogPanel extends QuickFormDialogPanel<ColumnFilterQuickFormValue> {

    private DataColumnSpecFilterPanel m_columns;

    private ColumnFilterQuickFormRepresentation m_representation;

    /**
     * @param representation Representation containing the possible values
     */
    public ColumnFilterQuickFormDialogPanel(final ColumnFilterQuickFormRepresentation representation) {
        super(representation.getDefaultValue());
        m_representation = representation;
        m_columns = new DataColumnSpecFilterPanel();
        resetToDefault();
        setComponent(m_columns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterQuickFormValue createNodeValue() throws InvalidSettingsException {
        ColumnFilterQuickFormValue value = new ColumnFilterQuickFormValue();
        DataColumnSpecFilterConfiguration config = new DataColumnSpecFilterConfiguration("columnFilter");
        m_columns.saveConfiguration(config);
        NodeSettings settings = new NodeSettings("columnFilter");
        config.saveConfiguration(settings);
        value.setSettings(settings);
        value.updateFromSpec(m_representation.getSpec());
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final ColumnFilterQuickFormValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            DataColumnSpecFilterConfiguration config = new DataColumnSpecFilterConfiguration("columnFilter");
            config.loadConfigurationInDialog(value.getSettings(), m_representation.getSpec());
            m_columns.loadConfiguration(config, m_representation.getSpec());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        DataColumnSpecFilterConfiguration config = new DataColumnSpecFilterConfiguration("columnFilter");
        config.loadConfigurationInDialog(m_representation.getDefaultValue().getSettings(), m_representation.getSpec());
        m_columns.loadConfiguration(config, m_representation.getSpec());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_columns.setEnabled(enabled);
    }

}
