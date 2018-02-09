/**
 *
 */
package org.knime.js.core.node.table;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.settings.table.TableSettings;

/**
 * Base table config interface
 *
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 * @since 3.6
 */
public interface TableConfig {
    /**
     * @return the settings
     */
    public TableSettings getSettings();

    /**
     * @param settings the settings to set
     */
    public void setSettings(final TableSettings settings);

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings);

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException;

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec);
}
