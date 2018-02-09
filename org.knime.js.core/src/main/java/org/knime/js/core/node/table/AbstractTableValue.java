/**
 *
 */
package org.knime.js.core.node.table;

import org.knime.js.core.JSONViewContent;
import org.knime.js.core.settings.table.TableValueSettings;

/**
 * Abstract table value.
 *
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 * @since 3.6
 */
public abstract class AbstractTableValue extends JSONViewContent {

    /**
     * @return the settings
     */
    public abstract TableValueSettings getSettings();

    /**
     * @param settings the settings to set
     */
    public abstract void setSettings(final TableValueSettings settings);
}
