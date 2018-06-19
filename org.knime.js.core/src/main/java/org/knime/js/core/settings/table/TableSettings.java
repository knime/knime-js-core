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
 */
package org.knime.js.core.settings.table;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;

/**
 * Common table settings.
 *
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 * @since 3.6
 */
public class TableSettings {
    final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    private final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    /**
     * Config name for column filter
     */
    public final static String CFG_COLUMN_FILTER = "columnFilter";
    private DataColumnSpecFilterConfiguration m_columnFilterConfig =
        new DataColumnSpecFilterConfiguration(CFG_COLUMN_FILTER);

    final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    /**
     * Default value for the name of selection column
     */
    public final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (JavaScript Table View)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    private final static String CFG_CUSTOM_CSS = "customCSS";
    private final static String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

    private TableRepresentationSettings m_representationSettings = new TableRepresentationSettings();

    private TableValueSettings m_valueSettings = new TableValueSettings();

    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the custom CSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the custom CSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * @return the columnFilterConfig
     */
    public DataColumnSpecFilterConfiguration getColumnFilterConfig() {
        return m_columnFilterConfig;
    }

    /**
     * @param columnFilterConfig the columnFilterConfig to set
     */
    public void setColumnFilterConfig(final DataColumnSpecFilterConfiguration columnFilterConfig) {
        m_columnFilterConfig = columnFilterConfig;
    }

    /**
     * @return the selectionColumnName
     */
    public String getSelectionColumnName() {
        return m_selectionColumnName;
    }

    /**
     * @param selectionColumnName the selectionColumnName to set
     */
    public void setSelectionColumnName(final String selectionColumnName) {
        m_selectionColumnName = selectionColumnName;
    }

    /**
     * @return the representationSettings
     */
    public TableRepresentationSettings getRepresentationSettings() {
        return m_representationSettings;
    }

    /**
     * @param representationSettings the representationSettings to set
     */
    public void setRepresentationSettings(final TableRepresentationSettings representationSettings) {
        this.m_representationSettings = representationSettings;
    }

    /**
     * @return the valueSettings
     */
    public TableValueSettings getValueSettings() {
        return m_valueSettings;
    }

    /**
     * @param valueSettings the valueSettings to set
     */
    public void setValueSettings(final TableValueSettings valueSettings) {
        this.m_valueSettings = valueSettings;
    }

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        m_columnFilterConfig.saveConfiguration(settings);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);

        // save only those settings which are needed in dialog
        m_representationSettings.saveSettingsFromDialog(settings);
        m_valueSettings.saveSettingsFromDialog(settings);

        //added with 3.6
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_columnFilterConfig.loadConfigurationInModel(settings);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);

        // load only those settings which are needed in dialog
        m_representationSettings.loadSettingsForDialog(settings);
        m_valueSettings.loadSettingsForDialog(settings);

        //added with 3.6
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_columnFilterConfig.loadConfigurationInDialog(settings, spec);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);

        m_representationSettings.loadSettingsForDialog(settings);
        m_valueSettings.loadSettingsForDialog(settings);

        //added with 3.6
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
    }
}
