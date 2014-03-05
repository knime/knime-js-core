/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by
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
 *   Feb 28, 2014 ("Patrick Winter"): created
 */
package org.knime.js.base.node.quickform.input.string;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.util.StringHistoryPanel;

/**
 * 
 * @author "Patrick Winter", University of Konstanz
 */
@SuppressWarnings({"rawtypes", "unchecked" })
public class RegexPanel {

    private StringHistoryPanel m_regex = new StringHistoryPanel("string_input_regex");
    
    private JTextField m_errorMessage = new JTextField();
    
    private JComboBox m_commonRegexes = new JComboBox();
    
    private JPanel m_commonRegexesPanel = new JPanel();
    
    /**
     * 
     */
    public RegexPanel() {
        JButton assign = new JButton("Assign");
        m_regex.setBold(false);
        assign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                CommonRegex commonRegex = (CommonRegex) m_commonRegexes.getSelectedItem();
                m_regex.setSelectedString(commonRegex.getRegex());
                m_errorMessage.setText(commonRegex.getErrorMessage());
                m_commonRegexes.setSelectedIndex(0);
            }
        });
        m_commonRegexesPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        m_commonRegexesPanel.add(m_commonRegexes, gbc);
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.gridx++;
        gbc.weightx = 0;
        m_commonRegexesPanel.add(assign, gbc);
        createCommonRegexes();
    }
    
    /**
     * Commits the current regex to the history.
     */
    public void commitRegexHistory() {
        m_regex.commitSelectedToHistory();
        m_regex.updateHistory();
    }
    
    /**
     * @return The regex
     */
    public String getRegex() {
        return m_regex.getSelectedString();
    }
    
    /**
     * @param regex The regex to set
     */
    public void setRegex(final String regex) {
        m_regex.setSelectedString(regex);
    }
    
    /**
     * @return The error message
     */
    public String getErrorMessage() {
        return m_errorMessage.getText();
    }
    
    /**
     * @param errorMessage The error message to set
     */
    public void setErrorMessage(final String errorMessage) {
        m_errorMessage.setText(errorMessage);
    }
    
    /**
     * @return The regex panel
     */
    public JComponent getRegexPanel() {
        return m_regex;
    }
    
    /**
     * @return The error message panel
     */
    public JComponent getErrorMessagePanel() {
        return m_errorMessage;
    }
    
    /**
     * @return The common regexes panel
     */
    public JComponent getCommonRegexesPanel() {
        return m_commonRegexesPanel;
    }
    
    private void createCommonRegexes() {
        addRegex("", "", "");
        addRegex(
                "Email address",
                "^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$",
                "The given input is not a valid email address");
        addRegex(
                "URL",
                "^(https:[/][/]|http:[/][/]|www.)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*$",
                "The given input is not a valid URL");
        addRegex(
                "IPv4",
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
                "The given input is not a valid IPv4 address");
        addRegex(
                "Windows file path",
                "^((\\\\\\\\[a-zA-Z0-9-]+\\\\[a-zA-Z0-9`~!@#$%^&(){}'._-]+([ ]+[a-zA-Z0-9`~!@#$%^&(){}'._-]+)*)|([a-zA-Z]:))(\\\\[^ \\\\/:*?\"\"<>|]+([ ]+[^ \\\\/:*?\"\"<>|]+)*)*\\\\?$",
                "The given input is not a valid Windows path");
    }
    
    private void addRegex(final String name, final String regex, final String errorMessage) {
        m_commonRegexes.addItem(new CommonRegex(name, regex, errorMessage));
    }
    
    private static class CommonRegex {
        
        private String m_name;
        
        private String m_regex;
        
        private String m_errorMessage;
        
        public CommonRegex(final String name, final String regex, final String errorMessage) {
            m_name = name;
            m_regex = regex;
            m_errorMessage = errorMessage;
        }
        
        /**
         * @return the regex
         */
        public String getRegex() {
            return m_regex;
        }
        
        /**
         * @return the errorMessage
         */
        public String getErrorMessage() {
            return m_errorMessage;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return m_name;
        }
        
    }

}
