package org.knime.js.base.node.quickform.selection.multiple;

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
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MultipleSelectionQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<MultipleSelectionQuickFormValue> {

    private static final String CFG_DEFAULT_VALUE = "default_value";
    
    private static final String[] DEFAULT_DEFAULT_VALUE = new String[0];

    private String[] m_defaultValue = DEFAULT_DEFAULT_VALUE;

    private static final String CFG_POSSIBLE_CHOICES = "possible_choices";

    private static final String[] DEFAULT_POSSIBLE_CHOICES = new String[0];

    private String[] m_possibleChoices = DEFAULT_POSSIBLE_CHOICES;

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = MultipleSelectionType.TWINLIST.getName();

    private String m_type = DEFAULT_TYPE;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        setPossibleChoices(settings.getStringArray(CFG_POSSIBLE_CHOICES));
        m_defaultValue = settings.getStringArray(CFG_DEFAULT_VALUE);
        setType(settings.getString(CFG_TYPE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        setPossibleChoices(settings.getStringArray(CFG_POSSIBLE_CHOICES, DEFAULT_POSSIBLE_CHOICES));
        m_defaultValue = settings.getStringArray(CFG_DEFAULT_VALUE, DEFAULT_DEFAULT_VALUE);
        setType(settings.getString(CFG_TYPE, DEFAULT_TYPE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_POSSIBLE_CHOICES, m_possibleChoices);
        settings.addStringArray(CFG_DEFAULT_VALUE, m_defaultValue);
        settings.addString(CFG_TYPE, m_type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<MultipleSelectionQuickFormValue> createDialogPanel() {
        MultipleSelectionQuickFormDialogPanel panel = new MultipleSelectionQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the defaultValue
     */
    @JsonProperty("default")
    public String[] getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    @JsonProperty("default")
    public void setDefaultValue(final String[] defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void resetNodeValueToDefault(final MultipleSelectionQuickFormValue value) {
        value.setVariableValue(m_defaultValue);
    }

    /**
     * @return the possibleChoices
     */
    @JsonProperty("possibleChoices")
    public String[] getPossibleChoices() {
        return m_possibleChoices;
    }

    /**
     * @param possibleChoices the possibleChoices to set
     */
    @JsonProperty("possibleChoices")
    public void setPossibleChoices(final String[] possibleChoices) {
        m_possibleChoices = possibleChoices;
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
