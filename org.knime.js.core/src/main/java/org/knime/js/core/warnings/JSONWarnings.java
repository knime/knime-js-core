/**
 *
 */
package org.knime.js.core.warnings;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Oleg Yasnev, KNIME.com GmbH, Berlin, Germany
 *
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown=true)
public class JSONWarnings {
    private Map<String, String> m_warningMap = new HashMap<String, String>();

    private static final String CFG_HAS_WARNINGS = "hasWarnings";
    private static final String CFG_WARNING_MAP = "warningMap";
    private static final String CFG_NUMBER_OF_WARNINGS = "numberOfWarnings";
    private static final String CFG_WARNING_ID = "id";
    private static final String CFG_WARNING_MSG = "msg";


    /**
     * @return the warningMap
     */
    public Map<String, String> getWarningMap() {
        return m_warningMap;
    }

    /**
     * Add (or overwrite, if exists) the warning message with the corresponding id
     * @param message
     * @param id
     */
    @JsonIgnore
    public void setWarningMessage(final String message, final String id) {
        m_warningMap.put(id, message);
    }

    /**
     * Remove the warning message by its id
     * @param id
     */
    @JsonIgnore
    public void clearWarningMessage(final String id) {
        m_warningMap.remove(id);
    }

    /**
     * Remove all warning messages
     */
    @JsonIgnore
    public void clearAllWarningMessages() {
        m_warningMap.clear();
    }

    /**
     * Saves the current state to the given settings object.
     * @param settings the settings to save to
     */
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        if (m_warningMap.size() > 0) {
            settings.addBoolean(CFG_HAS_WARNINGS, true);
            NodeSettingsWO warningSettings = settings.addNodeSettings(CFG_WARNING_MAP);
            warningSettings.addInt(CFG_NUMBER_OF_WARNINGS, m_warningMap.size());
            int i = 0;
            for (Map.Entry<String, String> entry : m_warningMap.entrySet()) {
                warningSettings.addString(CFG_WARNING_ID + i, entry.getKey());
                warningSettings.addString(CFG_WARNING_MSG + i, entry.getValue());
                i++;
            }
        } else {
            settings.addBoolean(CFG_HAS_WARNINGS, false);
        }
    }

    /**
     * Loads the configuration from the given settings object.
     * @param settings the settings to load from
     * @throws InvalidSettingsException on load error
     */
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        clearAllWarningMessages();
        boolean hasWarnings = settings.getBoolean(CFG_HAS_WARNINGS, false);
        if (hasWarnings) {
            NodeSettingsRO warningSettings = settings.getNodeSettings(CFG_WARNING_MAP);
            int numWarnings = warningSettings.getInt(CFG_NUMBER_OF_WARNINGS);
            for (int i = 0; i < numWarnings; i++) {
                setWarningMessage(warningSettings.getString(CFG_WARNING_MSG + i), warningSettings.getString(CFG_WARNING_ID + i));
            }
        }
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
        JSONWarnings other = (JSONWarnings)obj;
        return new EqualsBuilder()
                .append(m_warningMap, other.m_warningMap)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_warningMap)
                .toHashCode();
    }

}
