package org.knime.js.base.node.quickform.input.date;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
    
    private final JCheckBox m_useMin;
    
    private final JCheckBox m_useMax;
    
    private final JFormattedTextField m_min;
    
    private final JFormattedTextField m_max;

    private final JFormattedTextField m_defaultField;
    
    private final JFormattedTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    DateStringInputQuickFormNodeDialog() {
        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();
        m_min = new JFormattedTextField(DateStringInputQuickFormNodeModel.FORMAT);
        m_min.setColumns(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        m_min.setValue(new Date());
        m_max = new JFormattedTextField(DateStringInputQuickFormNodeModel.FORMAT);
        m_max.setColumns(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        m_max.setValue(new Date());
        m_defaultField = new JFormattedTextField(DateStringInputQuickFormNodeModel.FORMAT);
        m_defaultField.setColumns(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        m_defaultField.setValue(new Date());
        m_valueField = new JFormattedTextField(DateStringInputQuickFormNodeModel.FORMAT);
        m_valueField.setColumns(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        m_valueField.setValue(new Date());
        m_useMin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_min.setEnabled(m_useMin.isSelected());
            }
        });
        m_useMax.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_max.setEnabled(m_useMax.isSelected());
            }
        });
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        JPanel minPanel = new JPanel();
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(0, 0, 0, 0);
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 0;
        gbc2.weighty = 0;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        minPanel.add(m_useMin, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        minPanel.add(m_min, gbc2);
        JPanel maxPanel = new JPanel();
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_useMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_max, gbc2);
        GridBagConstraints gbc3 = (GridBagConstraints) gbc.clone();
        gbc3.insets = new Insets(0, 0, 0, 0);
        addPairToPanel("Earliest: ", minPanel, panelWithGBLayout, gbc3);
        addPairToPanel("Latest: ", maxPanel, panelWithGBLayout, gbc3);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Date Value: ", m_valueField, panelWithGBLayout, gbc);
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
        m_useMin.setSelected(representation.getUseMin());
        m_useMax.setSelected(representation.getUseMax());
        m_min.setValue(representation.getMin());
        m_max.setValue(representation.getMax());
        DateStringInputQuickFormValue value = new DateStringInputQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.setValue(value.getDate());
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        DateStringInputQuickFormRepresentation representation = new DateStringInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultValue((Date)m_defaultField.getValue());
        representation.setUseMin(m_useMin.isSelected());
        representation.setUseMax(m_useMax.isSelected());
        representation.setMin((Date) m_min.getValue());
        representation.setMax((Date) m_max.getValue());
        representation.saveToNodeSettings(settings);
        DateStringInputQuickFormValue value = new DateStringInputQuickFormValue();
        value.setDate((Date)m_valueField.getValue());
        value.saveToNodeSettings(settings);
    }

}
