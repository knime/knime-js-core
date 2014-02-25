package org.knime.js.base.node.quickform.selection.multiple;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MultipleSelectionQuickFormViewRepresentation extends JSONViewContent {

    private static final String CFG_DEFAULT_VALUE = "default_value";
    
    private static final String DEFAULT_DEFAULT_VALUE = "";

    private String m_defaultValue = DEFAULT_DEFAULT_VALUE;

    private static final String CFG_POSSIBLE_CHOICES = "possible_choices";

    private static final String DEFAULT_POSSIBLE_CHOICES = "";

    private String m_possibleChoices = DEFAULT_POSSIBLE_CHOICES;

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = MultipleSelectionType.TWINLIST.getName();

    private String m_type = DEFAULT_TYPE;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setPossibleChoices(settings.getString(CFG_POSSIBLE_CHOICES));
        m_defaultValue = settings.getString(CFG_DEFAULT_VALUE);
        setType(settings.getString(CFG_TYPE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_POSSIBLE_CHOICES, m_possibleChoices);
        settings.addString(CFG_DEFAULT_VALUE, m_defaultValue);
        settings.addString(CFG_TYPE, m_type);
    }

    /**
     * @return the defaultValue
     */
    @JsonProperty("default")
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    @JsonProperty("default")
    public void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * @return the possibleChoices
     */
    @JsonIgnore
    public String getPossibleChoices() {
        return m_possibleChoices;
    }

    /**
     * @param possibleChoices the possibleChoices to set
     */
    @JsonIgnore
    public void setPossibleChoices(final String possibleChoices) {
        m_possibleChoices = possibleChoices;
    }

    /**
     * @return the possibleChoices
     */
    @JsonProperty("possibleChoices")
    public String[] getPossibleChoicesArray() {
        return m_possibleChoices.split(",");
    }

    /**
     * @param possibleChoices the possibleChoices to set
     */
    @JsonProperty("possibleChoices")
    public void setPossibleChoicesArray(final String[] possibleChoices) {
        m_possibleChoices = StringUtils.join(possibleChoices, ",");
    }

    /**
     * @return the type
     */
    @JsonProperty("type")
    public String getType() {
        return m_type;
    }

    /**
     * @param type the type to set
     */
    @JsonProperty("type")
    public void setType(final String type) {
        m_type = type;
    }

}
