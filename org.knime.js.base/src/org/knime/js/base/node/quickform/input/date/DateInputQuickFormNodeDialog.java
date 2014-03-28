package org.knime.js.base.node.quickform.input.date;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
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
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class DateInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JCheckBox m_useMin;

    private final JCheckBox m_useMax;

    private final JSpinner m_min;

    private final JSpinner m_max;

    private final JSpinner m_defaultField;

    private final JSpinner m_valueField;

    private final ButtonGroup m_withTime;

    private final JRadioButton m_date;

    private final JRadioButton m_dateAndTime;

    /** Constructors, inits fields calls layout routines. */
    DateInputQuickFormNodeDialog() {
        m_date = new JRadioButton("Date");
        m_date.setActionCommand("date");
        m_dateAndTime = new JRadioButton("Date and Time");
        m_dateAndTime.setActionCommand("dateandtime");
        m_date.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateFormat();
            }
        });
        m_dateAndTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateFormat();
            }
        });
        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();
        m_withTime = new ButtonGroup();
        m_min = new JSpinner(new SpinnerDateModel());
        m_max = new JSpinner(new SpinnerDateModel());
        m_defaultField = new JSpinner(new SpinnerDateModel());
        m_valueField = new JSpinner(new SpinnerDateModel());
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
        m_withTime.add(m_date);
        m_withTime.add(m_dateAndTime);
        JPanel dateAndTimePanel = new JPanel();
        dateAndTimePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new Insets(0, 0, 0, 0);
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.weightx = 0;
        gbc4.weighty = 0;
        gbc4.gridx = 0;
        gbc4.gridy = 0;
        dateAndTimePanel.add(m_date, gbc4);
        gbc4.gridx++;
        gbc4.weightx = 1;
        dateAndTimePanel.add(m_dateAndTime, gbc4);
        JPanel minPanel = new JPanel();
        minPanel.setLayout(new GridBagLayout());
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
        maxPanel.setLayout(new GridBagLayout());
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_useMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_max, gbc2);
        GridBagConstraints gbc3 = (GridBagConstraints)gbc.clone();
        gbc3.insets = new Insets(0, 0, 0, 0);
        addPairToPanel("Selection: ", dateAndTimePanel, panelWithGBLayout, gbc3);
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
        DateInputQuickFormRepresentation representation = new DateInputQuickFormRepresentation();
        representation.loadFromNodeSettingsInDialog(settings);
        loadSettingsFrom(representation);
        m_defaultField.setValue(representation.getDefaultValue());
        m_useMin.setSelected(representation.getUseMin());
        m_useMax.setSelected(representation.getUseMax());
        m_min.setValue(representation.getMin());
        m_max.setValue(representation.getMax());
        DateInputQuickFormValue value = new DateInputQuickFormValue();
        value.loadFromNodeSettingsInDialog(settings);
        m_valueField.setValue(value.getDate());
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
        if (representation.getWithTime()) {
            m_dateAndTime.setSelected(true);
        } else {
            m_date.setSelected(true);
        }
        updateFormat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        DateInputQuickFormRepresentation representation = new DateInputQuickFormRepresentation();
        saveSettingsTo(representation);
        representation.setDefaultValue((Date)m_defaultField.getValue());
        representation.setUseMin(m_useMin.isSelected());
        representation.setUseMax(m_useMax.isSelected());
        representation.setMin((Date)m_min.getValue());
        representation.setMax((Date)m_max.getValue());
        representation.setWithTime(m_dateAndTime.isSelected());
        representation.saveToNodeSettings(settings);
        DateInputQuickFormValue value = new DateInputQuickFormValue();
        value.setDate((Date)m_valueField.getValue());
        value.saveToNodeSettings(settings);
    }

    private void updateFormat() {
        String format =
                m_dateAndTime.isSelected() ? DateInputQuickFormNodeModel.DATE_TIME_FORMAT
                        : DateInputQuickFormNodeModel.DATE_FORMAT;
        m_min.setEditor(new JSpinner.DateEditor(m_min, format));
        m_max.setEditor(new JSpinner.DateEditor(m_max, format));
        m_defaultField.setEditor(new JSpinner.DateEditor(m_defaultField, format));
        m_valueField.setEditor(new JSpinner.DateEditor(m_valueField, format));
    }

}
