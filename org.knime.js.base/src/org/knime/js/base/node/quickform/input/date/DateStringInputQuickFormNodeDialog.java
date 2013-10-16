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
import org.knime.core.util.node.quickform.in.DateStringInputQuickFormInElement;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
public class DateStringInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JFormattedTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    DateStringInputQuickFormNodeDialog() {
        m_valueField = new JFormattedTextField(DateStringInputQuickFormInElement.FORMAT);
        m_valueField.setColumns(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        m_valueField.setValue(new Date());
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
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
        DateStringInputQuickFormValue value = new DateStringInputQuickFormValue();
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
        representation.saveToNodeSettings(settings);
        DateStringInputQuickFormValue value = new DateStringInputQuickFormValue();
        value.setDate((Date)m_valueField.getValue());
        value.saveToNodeSettings(settings);
    }

}
