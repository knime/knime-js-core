package org.knime.js.base.node.quickform.input.dbl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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
public class DoubleInputQuickFormNodeDialog extends QuickFormNodeDialog {
    
    private final JCheckBox m_useMin;
    
    private final JCheckBox m_useMax;
    
    private final JSpinner m_min;
    
    private final JSpinner m_max;

    private final JSpinner m_defaultSpinner;
    
    private final JSpinner m_valueSpinner;

    /** Constructors, inits fields calls layout routines. */
    DoubleInputQuickFormNodeDialog() {
        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();
        m_min = new JSpinner(getSpinnerModel());
        m_max = new JSpinner(getSpinnerModel());
        m_defaultSpinner = new JSpinner(getSpinnerModel());
        m_valueSpinner = new JSpinner(getSpinnerModel());
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
        m_min.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                double min = (Double)m_min.getValue();
                if (((Double)m_max.getValue()) < min) {
                    m_max.setValue(min);
                }
            }
        });
        m_max.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                double max = (Double)m_max.getValue();
                if (((Double)m_min.getValue()) > max) {
                    m_min.setValue(max);
                }
            }
        });
        createAndAddTab();
    }

    private SpinnerNumberModel getSpinnerModel() {
        return new SpinnerNumberModel(0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.01);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        JPanel minPanel = new JPanel(new GridBagLayout());
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
        JPanel maxPanel = new JPanel(new GridBagLayout());
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_useMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_max, gbc2);
        addPairToPanel("Minimum: ", minPanel, panelWithGBLayout, gbc);
        addPairToPanel("Maximum: ", maxPanel, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultSpinner, panelWithGBLayout, gbc);
        addPairToPanel("Double Value: ", m_valueSpinner, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        DoubleInputQuickFormRepresentation representation = new DoubleInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_defaultSpinner.setValue(representation.getDefaultValue());
        m_useMin.setSelected(representation.getUseMin());
        m_useMax.setSelected(representation.getUseMax());
        m_min.setValue(representation.getMin());
        m_max.setValue(representation.getMax());
        DoubleInputQuickFormValue value = new DoubleInputQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueSpinner.setValue(value.getDouble());
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        DoubleInputQuickFormRepresentation representation = new DoubleInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultValue((Double)m_defaultSpinner.getValue());
        representation.setUseMin(m_useMin.isSelected());
        representation.setUseMax(m_useMax.isSelected());
        representation.setMin((Double)m_min.getValue());
        representation.setMax((Double)m_max.getValue());
        representation.saveToNodeSettings(settings);
        DoubleInputQuickFormValue value = new DoubleInputQuickFormValue();
        value.setDouble((Double)m_valueSpinner.getValue());
        value.saveToNodeSettings(settings);
    }

}
