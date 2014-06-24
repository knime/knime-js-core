/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   05.05.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.generic;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FlowVariableListCellRenderer;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.js.base.node.ui.CSSSnippetTextArea;
import org.knime.js.base.node.ui.JSSnippetTextArea;
import org.knime.js.core.JSONWebNode;
import org.osgi.framework.FrameworkUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class GenericJSViewNodeDialogPane extends NodeDialogPane {

    private static final String ID_WEB_RES = "org.knime.js.core.webResources";
    private static final String ATTR_RES_BUNDLE_ID = "webResourceBundleID";
    private static final String ATTR_RES_BUNDLE_NAME = "name";
    private static final String ATTR_RES_BUNDLE_VERSION = "version";
    private static final String ATTR_RES_BUNDLE_DEBUG = "debug";
    private static final String ATTR_RES_BUNDLE_DESCRIPTION = "description";

    private final GenericJSViewConfig m_config;
    private BiMap<String, String> m_availableLibraries;

    //private final JTextField m_viewName;
    private final JList m_flowVarList;
    private final JTable m_dependenciesTable;
    private final JSSnippetTextArea m_jsTextArea;
    private final CSSSnippetTextArea m_cssTextArea;

    /**
     * Initializes new dialog pane.
     */
    public GenericJSViewNodeDialogPane(final GenericJSViewConfig config) {
        m_config = config;
        //m_viewName = new JTextField(20);
        m_flowVarList = new JList(new DefaultListModel());
        m_flowVarList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_flowVarList.setCellRenderer(new FlowVariableListCellRenderer());
        m_flowVarList.addMouseListener(new MouseAdapter() {
            /** {@inheritDoc} */
            @Override
            public final void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    FlowVariable o = (FlowVariable)m_flowVarList.getSelectedValue();
                    if (o != null) {
                        m_jsTextArea.replaceSelection(FlowVariableResolver.getPlaceHolderForVariable(o));
                        m_flowVarList.clearSelection();
                        m_jsTextArea.requestFocus();
                    }
                }
            }
        });
        m_jsTextArea = new JSSnippetTextArea();
        m_cssTextArea = new CSSSnippetTextArea();
        @SuppressWarnings("serial")
        TableModel tableModel = new DefaultTableModel(0, 2) {
            /**
             * {@inheritDoc}
             */
            @Override
            public Class<?> getColumnClass(final int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isCellEditable(final int row, final int column) {
                if (column == 0) {
                    return true;
                }
                return false;
            }
        };
        m_dependenciesTable = new JTable(tableModel);
        m_dependenciesTable.getColumnModel().getColumn(0).setMaxWidth(30);
        /*m_dependenciesTable.setDefaultRenderer(String.class, new TableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
                final int row, final int column) {
                // TODO Auto-generated method stub
                return null;
            }
        })*/
        addTab("JavaScript View", initLayout());
    }

    /**
     * @return
     */
    private JPanel initLayout() {
        JPanel p = new JPanel(new BorderLayout());
        //p.add(m_viewName, BorderLayout.NORTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(m_flowVarList, BorderLayout.NORTH);
        JScrollPane cssScroller = new RTextScrollPane(m_cssTextArea);
        leftPanel.add(cssScroller, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);
        JScrollPane jsScroller = new RTextScrollPane(m_jsTextArea);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(m_dependenciesTable, BorderLayout.NORTH);
        rightPanel.add(jsScroller, BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        p.add(splitPane, BorderLayout.CENTER);
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        DefaultListModel listModel = (DefaultListModel)m_flowVarList.getModel();
        listModel.removeAllElements();
        for (FlowVariable e : getAvailableFlowVariables().values()) {
            listModel.addElement(e);
        }
        DefaultTableModel tableModel = (DefaultTableModel)m_dependenciesTable.getModel();
        tableModel.setRowCount(0);
        m_availableLibraries = getAvailableLibraries();
        System.out.println(m_availableLibraries);
        List<String> libNameList = new ArrayList<String>(m_availableLibraries.values());
        Collections.sort(libNameList);
        for(String lib : libNameList) {
            tableModel.addRow(new Object[]{false, lib});
        }
        m_config.loadSettingsForDialog(settings);
        String[] activeLibs = m_config.getDependencies();
        for (String lib: activeLibs) {
            String displayLib = m_availableLibraries.get(lib);
            for(int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 1).equals(displayLib)) {
                    tableModel.setValueAt(true, i, 0);
                    break;
                }
            }
        }
        //m_viewName.setText(m_config.getViewName());
        m_jsTextArea.setText(m_config.getJsCode());
        m_cssTextArea.setText(m_config.getCssCode());
    }

    private BiMap<String, String> getAvailableLibraries() {
        BiMap<String, String> availableLibraries = HashBiMap.create();
        availableLibraries.put("jsFreeChart_0.5", "JSFreeChart - Version 0.5.0");
        availableLibraries.put("D3_3.2.8", "D3 - Version 3.2.8");
        availableLibraries.put("jQuery_1.11.0", "jQuery - Version 1.11.0");
        availableLibraries.put("jQueryUi_1.10.4", "jQuery UI - Version 1.10.4");
        return availableLibraries;
    }

    private BiMap<String, String> getAllAvailableLibraries() {
        BiMap<String, String> availableLibraries = HashBiMap.create();
        String libBundleName = FrameworkUtil.getBundle(JSONWebNode.class).getSymbolicName();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(ID_WEB_RES);
        if (point == null) {
            throw new IllegalStateException("Invalid extension point id: " + ID_WEB_RES);
        }

        for (IExtension ext : point.getExtensions()) {
            IConfigurationElement[] elements = ext.getConfigurationElements();
            for (IConfigurationElement e : elements) {
                String bundleId = e.getDeclaringExtension().getNamespaceIdentifier();
                // Only load elements from library plugin
                if (!bundleId.equalsIgnoreCase(libBundleName)) {
                    continue;
                }
                String resBundleID = e.getAttribute(ATTR_RES_BUNDLE_ID);
                String resBundleName = e.getAttribute(ATTR_RES_BUNDLE_NAME);
                String resBundleVersion = e.getAttribute(ATTR_RES_BUNDLE_VERSION);
                boolean resBundleDebug = Boolean.parseBoolean(e.getAttribute(ATTR_RES_BUNDLE_DEBUG));
                String resBundleDisplay = resBundleName + " - Version " + resBundleVersion;
                if (resBundleDebug) {
                    resBundleDisplay += " - Debug";
                }
                availableLibraries.forcePut(resBundleID, resBundleDisplay);
            }
        }
        return availableLibraries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        List<String> dependencies = new ArrayList<String>();
        for (int row = 0; row < m_dependenciesTable.getRowCount(); row++) {
            if ((boolean)m_dependenciesTable.getValueAt(row, 0)) {
                String libDisplay = (String)m_dependenciesTable.getValueAt(row, 1);
                dependencies.add(m_availableLibraries.inverse().get(libDisplay));
            }
        }
        //m_config.setViewName(m_viewName.getText());
        m_config.setJsCode(m_jsTextArea.getText());
        m_config.setCssCode(m_cssTextArea.getText());
        m_config.setDependencies(dependencies.toArray(new String[0]));
        m_config.saveSettings(settings);
    }

    /*public static class JSLibrary {

        private String id;
        private String display;

        public JSLibrary(id, display) {
            // TODO Auto-generated constructor stub
        }

    }*/

}
