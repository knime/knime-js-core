/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 * ---------------------------------------------------------------------
 *
 * History
 *   24.09.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core.preferences;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.js.core.JSCorePlugin;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class JavaScriptPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private static final String INTERNAL_BROWSER = "org.knime.workbench.editor2.WizardNodeView";

    private RadioGroupFieldEditor m_browserSelector;
    private FileFieldEditor m_browserExePath;
    private StringFieldEditor m_browserCLIArgs;

    private BooleanFieldEditor m_createDebugHtml;
    private BooleanFieldEditor m_enableLegacyQuickformExecution;

    /** Creates a new preference page */
    public JavaScriptPreferencePage() {
        super("KNIME JavaScript View Settings", null, GRID);
        try {
            URL jsIcon = FileLocator.resolve(FileLocator.find(JSCorePlugin.getDefault().getBundle(), new Path("icons/js.png"), null));
            setImageDescriptor(ImageDescriptor.createFromURL(jsIcon));
        } catch (IOException e) { /* do nothing, it's just a picture */ }
        setDescription("Setup display options for nodes utilizing JavaScript views.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbench workbench) {
        setPreferenceStore(JSCorePlugin.getDefault().getPreferenceStore());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();

        m_browserSelector = new RadioGroupFieldEditor(JSCorePlugin.P_VIEW_BROWSER,
            "Please choose the browser to use for displaying JavaScript views:", 1,
            AbstractWizardNodeView.getAllWizardNodeViews().stream()
                .sorted((e1, e2) -> {
                    String s1 = e1.getViewClass().getCanonicalName();
                    String s2 = e2.getViewClass().getCanonicalName();
                    return INTERNAL_BROWSER.equals(s1) ? -1 : INTERNAL_BROWSER.equals(s2) ? 1 : s1.compareTo(s2);
                }).map(e -> new String[]{e.getViewName(), e.getViewClass().getCanonicalName()})
                .toArray(String[][]::new),
            parent);

        addField(m_browserSelector);

        for (Control radioButton : m_browserSelector.getRadioBoxControl(parent).getChildren()) {
            ((Button)radioButton).addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    enableBrowserField((String)e.widget.getData(), parent);
                }

                @Override
                public void widgetDefaultSelected(final SelectionEvent e) {
                    enableBrowserField((String)e.widget.getData(), parent);
                }
            });
        }

        m_browserExePath = new FileFieldEditor(JSCorePlugin.P_BROWSER_PATH, "Path to browser executable\n(leave empty for default):", true, parent);
        addField(m_browserExePath);
        m_browserCLIArgs = new StringFieldEditor(JSCorePlugin.P_BROWSER_CLI_ARGS, "Additional command\nline arguments for chosen browser:", parent);
        addField(m_browserCLIArgs);

        m_createDebugHtml = new BooleanFieldEditor(JSCorePlugin.P_DEBUG_HTML, "Create debug HTML for JavaScript views", BooleanFieldEditor.DEFAULT, parent);
        addField(m_createDebugHtml);

        m_enableLegacyQuickformExecution = new BooleanFieldEditor(JSCorePlugin.P_SHOW_LEGACY_QUICKFORM_EXECUTION, "Enable legacy Quickform execution", BooleanFieldEditor.DEFAULT, parent);
        addField(m_enableLegacyQuickformExecution);

        enableBrowserField(getPreferenceStore().getString(JSCorePlugin.P_VIEW_BROWSER), parent);
    }

    private void enableBrowserField(final String view, final Composite parent) {
        boolean enabled = !INTERNAL_BROWSER.equals(view) && view != null;
        m_browserExePath.setEnabled(enabled, parent);
        m_browserCLIArgs.setEnabled(enabled, parent);
    }

}
