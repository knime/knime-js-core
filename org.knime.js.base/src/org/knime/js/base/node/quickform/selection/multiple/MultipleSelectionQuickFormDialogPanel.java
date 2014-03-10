/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.selection.multiple;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang.ArrayUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.util.filter.StringFilterPanel;
import org.knime.js.base.node.quickform.QuickFormDialogPanel;

/**
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked", "deprecation" })
public class MultipleSelectionQuickFormDialogPanel extends QuickFormDialogPanel<MultipleSelectionQuickFormValue> {

    private MultipleSelectionComponent m_selectionComponent;

    /**
     * @param representation The representation containing layout information
     */
    public MultipleSelectionQuickFormDialogPanel(final MultipleSelectionQuickFormRepresentation representation) {
        String[] choices = representation.getPossibleChoices();
        if (representation.getType().equals(MultipleSelectionType.CHECKBOXES_VERTICAL.getName())) {
            m_selectionComponent = new CheckBoxesComponent(choices, true);
        } else if (representation.getType().equals(MultipleSelectionType.CHECKBOXES_HORIZONTAL.getName())) {
            m_selectionComponent = new CheckBoxesComponent(choices, false);
        } else if (representation.getType().equals(MultipleSelectionType.LIST.getName())) {
            m_selectionComponent = new ListComponent(choices);
        } else if (representation.getType().equals(MultipleSelectionType.TWINLIST.getName())) {
            m_selectionComponent = new TwinlistComponent(choices);
        }
        addComponent(m_selectionComponent.getComponent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveNodeValue(final MultipleSelectionQuickFormValue value) throws InvalidSettingsException {
        value.setVariableValue(m_selectionComponent.getSelections());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final MultipleSelectionQuickFormValue value) {
        m_selectionComponent.setSelections(value.getVariableValue());
    }

    private interface MultipleSelectionComponent {

        public JComponent getComponent();

        public String[] getSelections();

        public void setSelections(final String[] selections);

    }

    private class CheckBoxesComponent implements MultipleSelectionComponent {

        private JPanel m_panel = new JPanel();

        private List<JCheckBox> m_boxes = new ArrayList<JCheckBox>();

        CheckBoxesComponent(final String[] choices, final boolean vertical) {
            int rows = vertical ? choices.length : 1;
            int cols = vertical ? 1 : choices.length;
            GridLayout layout = new GridLayout(rows, cols);
            m_panel.setLayout(layout);
            for (String choice : choices) {
                JCheckBox box = new JCheckBox(choice);
                m_boxes.add(box);
                m_panel.add(box);
            }
        }

        @Override
        public JComponent getComponent() {
            return m_panel;
        }

        @Override
        public String[] getSelections() {
            List<String> selections = new ArrayList<String>();
            for (JCheckBox box : m_boxes) {
                if (box.isSelected()) {
                    selections.add(box.getText());
                }
            }
            return selections.toArray(new String[selections.size()]);
        }

        @Override
        public void setSelections(final String[] selections) {
            List<String> selectionList = Arrays.asList(selections);
            for (JCheckBox box : m_boxes) {
                box.setSelected(selectionList.contains(box.getText()));
            }
        }

    }

    private class ListComponent implements MultipleSelectionComponent {

        private static final int MIN_WIDTH = 200;

        private JList m_list;

        ListComponent(final String[] choices) {
            m_list = new JList(choices);
            m_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            m_list.setBorder(new EtchedBorder());
            if (m_list.getPreferredSize().width < MIN_WIDTH) {
                m_list.setPreferredSize(new Dimension(MIN_WIDTH, m_list.getPreferredSize().height));
            }
        }

        @Override
        public JComponent getComponent() {
            return m_list;
        }

        @Override
        public String[] getSelections() {
            return (Arrays.asList(m_list.getSelectedValues())).toArray(new String[0]);
        }

        @Override
        public void setSelections(final String[] selections) {
            List<Integer> indices = new ArrayList<Integer>(selections.length);
            List<String> selectionsList = Arrays.asList(selections);
            ListModel model = m_list.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                if (selectionsList.contains(model.getElementAt(i))) {
                    indices.add(i);
                }
            }
            m_list.setSelectedIndices(ArrayUtils.toPrimitive(indices.toArray(new Integer[indices.size()])));
        }

    }
    
    private class TwinlistComponent implements MultipleSelectionComponent {
        
        private StringFilterPanel m_filter;
        
        private String[] m_choices;
        
        TwinlistComponent(final String[] choices) {
            m_filter = new StringFilterPanel(true);
            m_choices = choices;
            m_filter.update(new ArrayList<String>(0), Arrays.asList(m_choices), m_choices);
        }

        @Override
        public JComponent getComponent() {
            return m_filter;
        }

        @Override
        public String[] getSelections() {
            Set<String> includes = m_filter.getIncludeList();
            return includes.toArray(new String[includes.size()]);
        }

        @Override
        public void setSelections(final String[] selections) {
            List<String> includes = Arrays.asList(selections);
            List<String> excludes = new ArrayList<String>(Math.max(0, m_choices.length - includes.size()));
            for (String string : m_choices) {
                if (!includes.contains(string)) {
                    excludes.add(string);
                }
            }
            m_filter.update(includes, excludes, m_choices);
        }
        
    }

}
