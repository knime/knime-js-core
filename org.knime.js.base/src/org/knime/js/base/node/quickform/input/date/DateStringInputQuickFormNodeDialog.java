package org.knime.js.base.node.quickform.input.date;

import java.awt.GridBagConstraints;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
public class DateStringInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JFormattedTextField m_defaultField;
    
    private final JFormattedTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    DateStringInputQuickFormNodeDialog() {
        m_defaultField = new JFormattedTextField(DateStringInputQuickFormNodeModel.FORMAT);
        m_defaultField.setColumns(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        m_defaultField.setValue(new Date());
        m_valueField = new JFormattedTextField(DateStringInputQuickFormNodeModel.FORMAT);
        m_valueField.setColumns(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        m_valueField.setValue(new Date());
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("String Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        DateStringInputQuickFormRepresentation representation = new DateStringInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_defaultField.setValue(representation.getDefaultValue());
        DateStringInputQuickFormValue value = new DateStringInputQuickFormValue(getFlowVariableName());
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.setValue(value.getDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        DateStringInputQuickFormRepresentation representation = new DateStringInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultValue((Date)m_defaultField.getValue());
        representation.saveToNodeSettings(settings);
        DateStringInputQuickFormValue value = new DateStringInputQuickFormValue(getFlowVariableName());
        value.setDate((Date)m_valueField.getValue());
        value.saveToNodeSettings(settings);
    }

}
