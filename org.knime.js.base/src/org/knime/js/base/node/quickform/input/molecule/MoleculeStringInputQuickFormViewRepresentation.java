package org.knime.js.base.node.quickform.input.molecule;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class MoleculeStringInputQuickFormViewRepresentation extends JSONViewContent {

    /**
     * The default formats shown in the molecule quickform input.
     */
    static final String[] DEFAULT_FORMATS = {"SDF", "SMILES", "MOL", "SMARTS", "RXN"};

    private static final String CFG_FORMAT = "format";

    private static final String DEFAULT_FORMAT = DEFAULT_FORMATS[0];

    private String m_format = DEFAULT_FORMAT;
    
    private static final String CFG_DEFAULT = "default";
    
    private String m_defaultValue = "";

    /**
     * @return the format
     */
    public String getFormat() {
        return m_format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(final String format) {
        m_format = format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_format = settings.getString(CFG_FORMAT);
        m_defaultValue = settings.getString(CFG_DEFAULT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_FORMAT, m_format);
        settings.addString(CFG_DEFAULT, getDefaultValue());
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

}
