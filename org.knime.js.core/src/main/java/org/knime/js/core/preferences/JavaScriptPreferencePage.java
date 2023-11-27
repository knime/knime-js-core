/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   24.09.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.core.preferences;

import static org.knime.js.core.JSCorePlugin.CEF_BROWSER;
import static org.knime.js.core.JSCorePlugin.CHROMIUM_BROWSER;
import static org.knime.js.core.JSCorePlugin.HEADLESS_CEF;
import static org.knime.js.core.JSCorePlugin.HEADLESS_CHROMIUM;
import static org.knime.js.core.JSCorePlugin.HEADLESS_PHANTOMJS;
import static org.knime.js.core.JSCorePlugin.INTERNAL_BROWSER;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.core.node.wizard.AbstractWizardNodeView.WizardNodeViewExtension;
import org.knime.js.core.AbstractImageGenerator;
import org.knime.js.core.AbstractImageGenerator.HeadlessBrowserExtension;
import org.knime.js.core.JSCorePlugin;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class JavaScriptPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private FileFieldEditor m_browserExePath;
    private StringFieldEditor m_browserCLIArgs;

    private FileFieldEditor m_headlessBrowserExePath;
    private StringFieldEditor m_headlesBrowserCLIArgs;

    /** Creates a new preference page */
    public JavaScriptPreferencePage() {
        super("KNIME JavaScript View Settings", null, GRID);
        try {
            URL jsIcon = FileLocator.resolve(FileLocator.find(JSCorePlugin.getDefault().getBundle(),
                new Path("icons/js.png"), null));
            setImageDescriptor(ImageDescriptor.createFromURL(jsIcon));
        } catch (IOException e) { /* do nothing, it's just a picture */ }
        setDescription("Setup display options for nodes utilizing legacy JavaScript views. "
            + "Note that the preferences on this page do not affect the modern JavaScript view nodes. "
            + "These nodes always use the Chromium Embedded Framework (CEF) browser.");
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

        if (!JSCorePlugin.isCEFInstalled()) {
            addField(new HorizontalLineField(parent));
            addInstallCEFButton(parent);
        }

        var allBrowsers = retrieveAllBrowsers();
        var browserSelector = new RadioGroupFieldEditor(JSCorePlugin.P_VIEW_BROWSER,
            "Please choose the browser to use for displaying JavaScript views:", 1, allBrowsers, parent);
        addField(browserSelector);
        for (Control radioButton : browserSelector.getRadioBoxControl(parent).getChildren()) {
            ((Button)radioButton).addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    enableBrowserFields((String)e.widget.getData(), parent);
                }

                @Override
                public void widgetDefaultSelected(final SelectionEvent e) {
                    enableBrowserFields((String)e.widget.getData(), parent);
                }
            });
        }
        m_browserExePath = new FileFieldEditor(JSCorePlugin.P_BROWSER_PATH,
            "Path to browser executable\n(leave empty for default):", true, parent);
        addField(m_browserExePath);
        m_browserCLIArgs = new StringFieldEditor(JSCorePlugin.P_BROWSER_CLI_ARGS,
            "Additional command\nline arguments for chosen browser:", parent);
        addField(m_browserCLIArgs);
        addField(new HorizontalLineField(parent));
        enableBrowserFields(getSelectedBrowser(allBrowsers, JSCorePlugin.P_VIEW_BROWSER), parent);

        String[][] headlessBrowsers = retrieveHeadlessBrowsers();
        var headlessBrowserSelector = new RadioGroupFieldEditor(JSCorePlugin.P_HEADLESS_BROWSER,
            "Please choose the headless browser to use for generating images from JavaScript views:"
            , 1, headlessBrowsers, parent);
        for (Control radioButton : headlessBrowserSelector.getRadioBoxControl(parent).getChildren()) {
            ((Button)radioButton).addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    enableHeadlessFields((String)e.widget.getData(), parent);
                }

                @Override
                public void widgetDefaultSelected(final SelectionEvent e) {
                    enableHeadlessFields((String)e.widget.getData(), parent);
                }
            });
        }
        addField(headlessBrowserSelector);
        if (headlessBrowsers.length > 0) {
            m_headlessBrowserExePath = new FileFieldEditor(JSCorePlugin.P_HEADLESS_BROWSER_PATH,
                "Path to headless browser executable\n(leave empty for default):", true, parent);
            addField(m_headlessBrowserExePath);
            m_headlesBrowserCLIArgs = new StringFieldEditor(JSCorePlugin.P_HEADLESS_BROWSER_CLI_ARGS,
                "Additional command\nline arguments for chosen headless browser:", parent);
            addField(m_headlesBrowserCLIArgs);
            enableHeadlessFields(getSelectedBrowser(headlessBrowsers, JSCorePlugin.P_HEADLESS_BROWSER), parent);
        } else {
            final Label label = new Label(parent, SWT.NONE);
            label.setText("No headless browser installed. Image generation will be unavailable!");
            final FontData data = label.getFont().getFontData()[0];
            final Font font = new Font(label.getDisplay(), new FontData(data.getName(), data.getHeight(), SWT.BOLD));
            label.setFont(font);
            label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_RED));
        }
        addField(new HorizontalLineField(parent));

        var createDebugHtml = new BooleanFieldEditor(JSCorePlugin.P_DEBUG_HTML, "Create debug HTML for JavaScript views", BooleanFieldEditor.DEFAULT, parent);
        addField(createDebugHtml);

        var enableLegacyQuickformExecution = new BooleanFieldEditor(JSCorePlugin.P_SHOW_LEGACY_QUICKFORM_EXECUTION, "Enable legacy Quickform execution", BooleanFieldEditor.DEFAULT, parent);
        addField(enableLegacyQuickformExecution);
    }

    private static String[][] retrieveAllBrowsers() {
        return AbstractWizardNodeView.getAllWizardNodeViews().stream()
            .filter(v -> {
                try {
                    Method isEnabled = v.getViewClass().getMethod("isEnabled");
                    return (boolean)isEnabled.invoke(null);
                } catch (Exception e) {
                    /* if method is not present, assume view is enabled */
                    return true;
                }
            })
            .sorted((e1, e2) -> {
                String s1 = e1.getViewClass().getCanonicalName();
                String s2 = e2.getViewClass().getCanonicalName();
                return CEF_BROWSER.equals(s1) ? -1 : CEF_BROWSER.equals(s2) ? 1 : s1.compareTo(s2);
            }).map(e -> new String[]{getViewName(e), e.getViewClass().getCanonicalName()})
            .toArray(String[][]::new);
    }

    private static String getSelectedBrowser(final String[][] allBrowsers, final String preferenceKey) {
        var selectedBrowser = JSCorePlugin.getDefault().getPreferenceStore().getString(preferenceKey);
        for (String[] b : allBrowsers) {
            if (b[1].equals(selectedBrowser)) {
                return selectedBrowser;
            }
        }
        // the selected browser is not available -> fallback to the first in the list of all browsers
        if (allBrowsers.length > 0) {
            return allBrowsers[0][1];
        } else {
            return null;
        }
    }

    private static String[][] retrieveHeadlessBrowsers() {
        return AbstractImageGenerator.getAllHeadlessBrowsers().stream()
                .filter(v -> {
                    try {
                        Method isEnabled = v.getImageGeneratorClass().getMethod("isEnabled");
                        return (boolean)isEnabled.invoke(null);
                    } catch (Exception e) {
                        /* if method is not present, assume view is enabled */
                        return true;
                    }
                })
                .sorted((e1, e2) -> {
                    String s1 = e1.getImageGeneratorClass().getCanonicalName();
                    String s2 = e2.getImageGeneratorClass().getCanonicalName();
                    return HEADLESS_CEF.equals(s1) ? -1 : HEADLESS_CEF.equals(s2) ? 1 : e1.getBrowserName().compareTo(e2.getBrowserName());
                }).map(e -> new String[] {getHeadlessName(e), e.getImageGeneratorClass().getCanonicalName()})
                .toArray(String[][]::new);
    }

    private static String getViewName(final WizardNodeViewExtension view) {
        String name = view.getViewName();
        boolean isInternal = INTERNAL_BROWSER.equals(view.getViewClass().getCanonicalName());
        if (isInternal) {
            String os = Platform.getOS();
            if (Platform.OS_WIN32.equals(os)) {
                return name + " (IE - not recommended)";
            }
            if (Platform.OS_MACOSX.equals(os)) {
                return name + " (Safari)";
            }
            if (Platform.OS_LINUX.equals(os)) {
                return name + " (Webkit)";
            }
        }
        return name;
    }

    private static String getHeadlessName(final HeadlessBrowserExtension browser) {
        String name = browser.getBrowserName();
        boolean isPhantom = HEADLESS_PHANTOMJS.equals(browser.getImageGeneratorClass().getCanonicalName());
        if (isPhantom) {
            return name + " (support discontinued)";
        }
        return name;
    }

    private void enableBrowserFields(final String view, final Composite parent) {
        boolean isSWT = INTERNAL_BROWSER.equals(view);
        boolean isChromium = CHROMIUM_BROWSER.equals(view);
        boolean isCEF = CEF_BROWSER.equals(view);
        m_browserExePath.setEnabled(!isSWT && !isChromium && !isCEF && view != null, parent);
        m_browserCLIArgs.setEnabled(!isSWT && !isCEF && view != null, parent);
    }

    private void enableHeadlessFields(final String view, final Composite parent) {
        if (m_headlessBrowserExePath != null && m_headlesBrowserCLIArgs != null) {
            boolean isPhantom = HEADLESS_PHANTOMJS.equals(view);
            boolean isChromium = HEADLESS_CHROMIUM.equals(view);
            boolean isCEF = HEADLESS_CEF.equals(view);
            m_headlessBrowserExePath.setEnabled(!isPhantom && !isChromium && !isCEF && view != null, parent);
            m_headlesBrowserCLIArgs.setEnabled(!isCEF && view != null, parent);
        }
    }

    /**
     * A simple field editor which just creates a horizontal line
     */
    private class HorizontalLineField extends FieldEditor {

        private Label m_line;

        /**
         * @param parent the parent of the field editor's control
         */
        public HorizontalLineField(final Composite parent) {
            super("HOR_LINE", "", parent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void createControl(final Composite parent) {
            m_line = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
            super.createControl(parent); // calls doFillIntoGrid!
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void adjustForNumColumns(final int numColumns) {
            Object o = m_line.getLayoutData();
            if (o instanceof GridData) {
                ((GridData)o).horizontalSpan = numColumns;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doFillIntoGrid(final Composite parent, final int numColumns) {
            m_line.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, numColumns, 1));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doLoad() {
            // nothing to load
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doLoadDefault() {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doStore() {
            // nothing to store
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumberOfControls() {
            return 1;
        }

    }

    private static void addInstallCEFButton(final Composite parent) {
        final Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));
        comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        final Label label = new Label(comp, SWT.NONE);
        label.setText("The Chromium Embedded Framework (CEF) browser is not installed. It is recommended to install it.");
        final FontData data = label.getFont().getFontData()[0];
        final Font font = new Font(label.getDisplay(), new FontData(data.getName(), data.getHeight(), SWT.BOLD));
        label.setFont(font);
        label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_RED));

        final Button button = new Button(comp, SWT.NONE);
        button.setText("Install Chromium Embedded Framework (CEF) browser");

        button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                JSCorePlugin.installCEFExtension();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                JSCorePlugin.installCEFExtension();
            }
        });
    }

}
