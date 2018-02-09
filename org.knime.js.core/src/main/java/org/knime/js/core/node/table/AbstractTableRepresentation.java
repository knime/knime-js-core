/**
 *
 */
package org.knime.js.core.node.table;

import org.knime.js.core.JSONViewContent;
import org.knime.js.core.settings.table.TableRepresentationSettings;

/**
 * Abstract table representation.
 *
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 * @since 3.6
 */
public abstract class AbstractTableRepresentation extends JSONViewContent {

    /**
     * @return the settings
     */
    public abstract TableRepresentationSettings getSettings();

    /**
     * @param settings the settings to set
     */
    public abstract void setSettings(final TableRepresentationSettings settings);

    /**
     * Copy settings from dialog keeping the existing table data
     * @param settings the settings to set
     */
    public abstract void setSettingsFromDialog(final TableRepresentationSettings settings);
}
