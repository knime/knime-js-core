package org.knime.js.base.node.quickform.input.molecule;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormFlowVariableRepresentation;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class MoleculeStringInputQuickFormRepresentation extends
        QuickFormFlowVariableRepresentation<MoleculeStringInputQuickFormValue> {

    /**
     * The default formats shown in the molecule quickform input.
     */
    static final String[] DEFAULT_FORMATS = {"SDF", "SMILES", "MOL", "SMARTS", "RXN"};

    private static final String CFG_FORMAT = "format";

    private static final String DEFAULT_FORMAT = DEFAULT_FORMATS[0];

    private String m_format = DEFAULT_FORMAT;

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
        super.loadFromNodeSettings(settings);
        m_format = settings.getString(CFG_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_format = settings.getString(CFG_FORMAT, DEFAULT_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addString(CFG_FORMAT, m_format);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<MoleculeStringInputQuickFormValue> createDialogPanel() {
        MoleculeStringInputQuickFormDialogPanel panel = new MoleculeStringInputQuickFormDialogPanel();
        fillDialogPanel(panel);
        return panel;
    }

}
