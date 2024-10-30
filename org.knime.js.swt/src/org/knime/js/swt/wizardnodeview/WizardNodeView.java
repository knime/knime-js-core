/*
 * ------------------------------------------------------------------------
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
 * Created on Apr 22, 2013 by Berthold
 */
package org.knime.js.swt.wizardnodeview;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.AbstractWizardNodeView;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.wizard.SubnodeViewableModel;
import org.knime.core.wizard.WizardPageCreationHelper;
import org.knime.core.wizard.rpc.JsonRpcFunction;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.JavaScriptViewCreator;
import org.knime.js.swt.wizardnodeview.ElementRadioSelectionDialog.RadioItem;

/**
 * Standard implementation for interactive views which are launched on the client side via an integrated browser. They
 * only have indirect access to the NodeModel via get and setViewContent methods and therefore simulate the behavior of
 * the same view in the WebPortal.
 *
 * @author B. Wiswedel, M. Berthold, Th. Gabriel, C. Albrecht
 * @param <T> requires a {@link NodeModel} implementing {@link WizardNode} as well
 * @param <REP> the {@link WebViewContent} implementation used
 * @param <VAL>
 * @since 4.2
 */
public class WizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>,
        REP extends WebViewContent, VAL extends WebViewContent>
        extends AbstractWizardNodeView<T, REP, VAL> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(WizardNodeView.class);
    private static final int ASYNC_TIMEOUT = 10 * 1000;
    private static final String VIEW_VALID = "viewValid";
    private static final String VIEW_VALUE = "viewValue";
    private static final String EMPTY_OBJECT_STRING = "{}";

    private static final int COMPOSITE_VIEW_WIDTH = 1024;
    private static final int COMPOSITE_VIEW_HEIGHT = 768;

    private Shell m_shell;

    private BrowserWrapper m_browserWrapper;
    private BrowserFunctionInternal m_viewRequestCallback;
    private BrowserFunctionInternal m_updateRequestStatusCallback;
    private BrowserFunctionInternal m_cancelRequestCallback;
    private BrowserFunctionInternal m_isPushSupportedCallback;
    private BrowserFunctionInternal m_validateCurrentValueInViewCallback;
    private BrowserFunctionInternal m_retrieveCurrentValueFromViewCallback;
    private BrowserFunctionInternal m_rpcCallback;
    private List<BrowserFunctionWrapper> m_additionalCallbacks;
    private final AtomicBoolean m_viewSet = new AtomicBoolean(false);
    private final Map<String, AtomicReference<Object>> m_asyncEvalReferenceMap;
    private String m_title;

    private NodeStateChangeListener m_nodeStateChangeListener;

    /**
     * @param snc
     * @param nodeModel the underlying model
     * @since 4.5
     */
    public WizardNodeView(final SingleNodeContainer snc, final T nodeModel) {
        super(snc, nodeModel);

        if (snc instanceof NativeNodeContainer) {
            // special handling for native nodes because it's not possible to
            // properly determine the node state within the #modelChanged-callback
            // because it's, e.g., called from within the 'execute'-method
            m_nodeStateChangeListener = e -> nodeStateChanged(snc);
            snc.addNodeStateChangeListener(m_nodeStateChangeListener);
        }

        m_asyncEvalReferenceMap = new HashMap<String, AtomicReference<Object>>(2);
        m_asyncEvalReferenceMap.put(VIEW_VALID, new AtomicReference<Object>(null));
        m_asyncEvalReferenceMap.put(VIEW_VALUE, new AtomicReference<Object>(null));
    }

    private void nodeStateChanged(final SingleNodeContainer snc) {
        var display = getDisplay();
        if (display == null) {
            // view most likely disposed
            return;
        }

        var isExecuted = snc.getNodeContainerState().isExecuted();

        if (m_viewSet.getAndSet(isExecuted) != isExecuted) {
            display.asyncExec(() -> setBrowserContent(isExecuted));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modelChanged() {
        cancelOutstandingViewRequests();
        var nc = getNodeContainer();
        if (nc instanceof SubNodeContainer) {
            // native nodes are handled differently
            // see #WizardNodeView-constructor
            nodeStateChanged(nc);
        }
    }

    private Display getDisplay() {
        //Display display = new Display();
        Display display = Display.getCurrent();
        if (display == null && m_browserWrapper != null && !m_browserWrapper.isDisposed()) {
            display = m_browserWrapper.getDisplay();
        }
        return display;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void callOpenView(final String title) {
        callOpenView(title, null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    protected void callOpenView(final String title, final Rectangle knimeWindowBounds) {
        m_title = (title == null ? "View" : title);

        Display display = getDisplay();
        m_shell = new Shell(display.getActiveShell(), SWT.SHELL_TRIM);
        m_shell.setText(m_title);

        //m_shell.setImage(ImageRepository.getIconImage(SharedImages.KNIME));
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        m_shell.setLayout(layout);

        m_browserWrapper = createBrowserWrapper(m_shell);
        initBrowserFunctions();
        var snc = getNodeContainer();
        m_browserWrapper.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite buttonComposite = new Composite(m_shell, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(GridData.END, GridData.END, false, false));
        RowLayout buttonLayout = new RowLayout();
        buttonLayout.marginWidth = 4;
        buttonLayout.marginTop = 1;
        buttonLayout.marginBottom = 6;
        buttonComposite.setLayout(buttonLayout);

        ToolBar toolBar = new ToolBar(buttonComposite, SWT.BORDER | SWT.FLAT | SWT.HORIZONTAL);
        toolBar.setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
        ToolItem resetButton = new ToolItem(toolBar, SWT.PUSH);
        resetButton.setText("Reset");
        resetButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // Could only call if actual settings have been changed,
                // however there might be things in views that one can change
                // which do not get saved, then it's nice to trigger the event anyways.
                /*if (checkSettingsChanged()) {*/
                    modelChanged();
                    if (snc != null) {
                        nodeStateChanged(snc);
                    }
                /*}*/
            }
        });
        new ToolItem(toolBar, SWT.SEPARATOR);
        ToolItem applyButton = new ToolItem(toolBar, SWT.DROP_DOWN);
        applyButton.setText("Apply");
        applyButton.setToolTipText("Applies the current settings and triggers a re-execute of the node.");
        DropdownSelectionListener applyListener = new DropdownSelectionListener(applyButton);
        String aTTooltip = "Applies the current settings and triggers a re-execute of the node.";
        applyListener.add("Apply temporarily", aTTooltip, new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                applyTriggered(false);
            }
        });
        String nDTooltip = "Applies the current settings as the node default settings and triggers a re-execute of the node.";
        applyListener.add("Apply as new default", nDTooltip, new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                applyTriggered(true);
            }
        });
        applyButton.addSelectionListener(applyListener);

        applyButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (e.detail != SWT.ARROW) {
                    if (checkSettingsChanged()) {
                        showApplyDialog();
                    }
                }
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        ToolItem closeButton = new ToolItem(toolBar, SWT.DROP_DOWN);
        closeButton.setText("Close");
        closeButton.setToolTipText("Closes the view.");
        DropdownSelectionListener closeListener = new DropdownSelectionListener(closeButton);
        String cDTooltip = "Closes the view and discards any changes made.";
        closeListener.add("Close && Discard", cDTooltip, new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                m_shell.dispose();
            }
        });
        String cATooltip = "Closes the view, applies the current settings and triggers a re-execute of the node.";
        closeListener.add("Close && Apply temporarily", cATooltip, new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (applyTriggered(false)) {
                    m_shell.dispose();
                }
            }
        });
        String cTTooltip = "Closes the view, applies the current settings as node defaults and triggers a re-execute of the node.";
        closeListener.add("Close && Apply as new default", cTTooltip, new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (applyTriggered(true)) {
                    m_shell.dispose();
                }
            }
        });
        closeButton.addSelectionListener(closeListener);

        closeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (e.detail != SWT.ARROW) {
                    if (checkSettingsChanged()) {
                        /*MessageDialogWithToggle dialog =
                            MessageDialogWithToggle.openOkCancelConfirm(m_browser.getShell(), "Discard Settings",
                                "View settings have changed and will be lost. Do you want to continue?",
                                "Do not ask again", false, null, null);*/
                        if  (!showCloseDialog()) {
                            return;
                        }
                    }
                    m_shell.dispose();
                }
            }
        });

        m_shell.addListener(SWT.Close, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (checkSettingsChanged()) {
                    event.doit = showCloseDialog();
                }
            }
        });

        // Adjust to the DPI of the monitor where the shell is currently displayed.
        // Note: SWT uses the DPI of the monitor where the shell was initially created,
        // which can cause issues in DPI-aware applications on Windows.
        // This scalingFactor ensures dialogs adjust to the current monitor's DPI.
        var scalingFactor = 1.0f;
        if (SystemUtils.IS_OS_WINDOWS) {
            var monitorDPI = m_shell.getMonitor().getZoom();
            scalingFactor = (float)monitorDPI / DPIUtil.getDeviceZoom();
        }
        m_shell.setSize(Math.round(COMPOSITE_VIEW_WIDTH * scalingFactor),
            Math.round(COMPOSITE_VIEW_HEIGHT * scalingFactor));

        Point middle = new Point(knimeWindowBounds.width / 2, knimeWindowBounds.height / 2);
        // Left upper point for window
        Point newLocation = new Point(middle.x - (m_shell.getSize().x / 2) + knimeWindowBounds.x,
                                      middle.y - (m_shell.getSize().y / 2) + knimeWindowBounds.y);
        m_shell.setLocation(newLocation.x, newLocation.y);
        m_shell.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
            @Override
            public void widgetDisposed(final DisposeEvent e) {
                callCloseView();
            }
        });
        m_shell.open();

        setBrowserContent(true);
        m_shell.setFocus(); // moves focus off the buttons when window opened
        m_viewSet.set(true);
    }

    private void setBrowserContent(final boolean hasData) {
        // m_browserWrapper can be null if this method is called while the
        // view is being closed
        if (!isBrowserWrapperDisposed()) {
            synchronized (m_browserWrapper) {
                if (isBrowserWrapperDisposed()) {
                    return;
                }
                try {
                    String url;
                    if (hasData && (url = getViewURL().orElse(null)) != null) {
                        onPageLoaded(() -> {
                            var pageCreationHelper = createWizardPageCreationHelper();
                            initializeJsonRpcJavaBrowserCommunication(
                                createJsonRpcFunction(getNodeContainer(), getViewableModel(), pageCreationHelper));
                            return createInitScript(pageCreationHelper);
                        });
                        m_browserWrapper.setUrl(url);
                        return;
                    }

                    m_browserWrapper.setText(getViewCreator().createMessageHTML("No data to display"));
                } catch (Exception e) {
                    m_browserWrapper.setText(getViewCreator().createMessageHTML(e.getMessage()));
                    m_viewSet.set(false);
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @return a new {@link WizardPageCreationHelper}-instance
     * @since 5.3
     */
    protected WizardPageCreationHelper createWizardPageCreationHelper() {
        return nnc -> NodeViewEnt.create(nnc, List::of);
    }

    /**
     * Reloads the view's content.
     *
     * @since 4.6
     */
    protected final void reloadBrowserContent() {
        setBrowserContent(true);
    }

    private boolean isBrowserWrapperDisposed() {
        return m_browserWrapper == null || m_browserWrapper.isDisposed();
    }

    private String createInitScript(final WizardPageCreationHelper pageCreationHelper) {
        WizardNode<REP, VAL> model = getModel();
        WizardViewCreator<REP, VAL> creator = model.getViewCreator();
        if (creator instanceof JavaScriptViewCreator<?, ?> && model instanceof CSSModifiable) {
            String customCSS = ((CSSModifiable)model).getCssStyles();
            ((JavaScriptViewCreator<?, ?>)creator).setCustomCSS(customCSS);
        }
        if (model instanceof SubnodeViewableModel) {
            try {
                ((SubnodeViewableModel)model).createPageAndValue(pageCreationHelper);
            } catch (IOException e) {
                // should never happen
                throw new IllegalStateException("Wizard page couldn't be created", e);
            }
        } else {
            var rep = model.getViewRepresentation();
            if (rep instanceof JSONViewContent jsonViewContent) {
                pageCreationHelper.updateViewRepresentation(jsonViewContent);
            }
        }
        var initCall = creator.createInitJSViewMethodCall(model.getViewRepresentation(), model.getViewValue());
        return creator.wrapInTryCatch(initCall);
    }

    private void onPageLoaded(final Supplier<String> scriptToRun) {
        m_browserWrapper.addProgressListener(new ProgressListener() {

            @Override
            public void changed(final ProgressEvent event) {
                //
            }

            @Override
            public void completed(final ProgressEvent event) {
                try {
                    String script = scriptToRun.get();
                    if (script != null) {
                        m_browserWrapper.execute(script);
                    }
                } finally {
                    m_browserWrapper.removeProgressListener(this);
                }
            }
        });
    }

    /**
     * Initializes the js-java communication (i.e. between the browser and java) through json-rpc messages.
     *
     * To be overwritten by subclasses to implement alternative communication mechanisms.
     *
     * @param jsonRpcFunction processes the jsonrpc messages and returns the respective response
     * @return an event consumer that is used to dispatch events to the browser/js
     * @since 4.7
     */
    protected BiConsumer<String, Object>
        initializeJsonRpcJavaBrowserCommunication(final JsonRpcFunction jsonRpcFunction) {
        var snc = getNodeContainer();
        if (snc != null) {
            m_rpcCallback = new RpcFunction(m_browserWrapper, jsonRpcFunction);
        }
        return (s, e) -> {
            var jsCall = JsonRpcFunction.createJsonRpcNotificationCall(s, e);
            Display.getDefault().syncExec(() -> m_browserWrapper.execute(jsCall));
        };
    }

    private static JsonRpcFunction createJsonRpcFunction(final SingleNodeContainer snc,
        final ViewableModel viewableModel, final WizardPageCreationHelper pageCreationHelper) {
        if (snc instanceof SubNodeContainer) {
            return new JsonRpcFunction((SubNodeContainer)snc,
                ((SubnodeViewableModel)viewableModel).createReexecutionService(pageCreationHelper), false);
        } else {
            return new JsonRpcFunction((NativeNodeContainer)snc);
        }
    }

    private void initBrowserFunctions() {
        m_viewRequestCallback = new ViewRequestFunction(m_browserWrapper, "knimeViewRequest");
        m_updateRequestStatusCallback = new UpdateRequestStatusFunction(m_browserWrapper, "knimeUpdateRequestStatus");
        m_cancelRequestCallback = new CancelRequestFunction(m_browserWrapper, "knimeCancelRequest");
        m_isPushSupportedCallback = new PushSupportedFunction(m_browserWrapper, "knimePushSupported");
        m_validateCurrentValueInViewCallback = new AsyncEvalCallbackFunction<Boolean>(m_browserWrapper,
            "validateCurrentValueInView", VIEW_VALID, Boolean.FALSE);
        m_retrieveCurrentValueFromViewCallback = new AsyncEvalCallbackFunction<String>(m_browserWrapper,
            "retrieveCurrentValueFromView", VIEW_VALUE, EMPTY_OBJECT_STRING);
        m_additionalCallbacks = registerAndGetAdditionalBrowserFunctions(m_browserWrapper);
    }

    /**
     * @param browser the browser to add the browser function to
     * @return list of additional browser function registered with passed browser
     * @since 4.5
     */
    protected List<BrowserFunctionWrapper> registerAndGetAdditionalBrowserFunctions(final BrowserWrapper browser) {
        return Collections.emptyList();
    }

    class DropdownSelectionListener extends SelectionAdapter {

        private Menu menu;

        public DropdownSelectionListener(final ToolItem drop) {
          menu = new Menu(drop.getParent().getShell(), SWT.POP_UP);
        }

        public void add(final String text, final String tooltip, final SelectionAdapter selectionListener) {
          //MenuItem menuItem = new MenuItem(menu, SWT.NONE);
          MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
          menuItem.setText(text);
          menuItem.setToolTipText(tooltip);
          menuItem.addSelectionListener(selectionListener);
        }

        @Override
        public void widgetSelected(final SelectionEvent event) {
          if (event.detail == SWT.ARROW) {
            ToolItem item = (ToolItem) event.widget;
            org.eclipse.swt.graphics.Rectangle rect = item.getBounds();
            org.eclipse.swt.graphics.Point pt = item.getParent().toDisplay(new org.eclipse.swt.graphics.Point(rect.x, rect.y));
            menu.setLocation(pt.x, pt.y + rect.height);
            menu.setVisible(true);
          }
        }
      }

    /**
     * Creates a browser wrapper instance. The browser wrapper is necessary because there is nor
     * {@link Browser}-interface.
     *
     * To be overwritten by subclasses to use another browser implementation.
     *
     * @param shell the shell to add the browser widget to
     * @return the new browser wrapper instance
     */
    protected BrowserWrapper createBrowserWrapper(final Shell shell) {
        final Browser browser = new Browser(shell, SWT.NONE);
        return new BrowserWrapper() {

            @Override
            public void execute(final String call) {
                browser.execute(call);
            }

            @Override
            public Display getDisplay() {
                return browser.getDisplay();
            }

            @Override
            public void addProgressListener(final ProgressListener progressListener) {
                browser.addProgressListener(progressListener);
            }

            @Override
            public void removeProgressListener(final ProgressListener progressListener) {
                browser.removeProgressListener(progressListener);
            }

            @Override
            public void addLocationListener(final LocationListener locationListener) {
                browser.addLocationListener(locationListener);
            }

            @Override
            public void removeLocationListener(final LocationListener locationListener) {
                browser.removeLocationListener(locationListener);
            }

            @Override
            public void setUrl(final String absolutePath) {
                browser.setUrl(absolutePath);
            }

            @Override
            public void setText(final String html) {
                browser.setText(html);
            }

            @Override
            public Shell getShell() {
                return browser.getShell();
            }

            @Override
            public boolean isDisposed() {
                return browser.isDisposed();
            }

            @Override
            public void setText(final String html, final boolean trusted) {
                browser.setText(html, trusted);
            }

            @Override
            public void setLayoutData(final GridData gridData) {
                browser.setLayoutData(gridData);
            }

            @Override
            public BrowserFunctionWrapper registerBrowserFunction(final String name,
                final Function<Object[], Object> func) {
                final BrowserFunction fct = new BrowserFunction(browser, name) {
                    @Override
                    public Object function(final Object[] args) {
                        return func.apply(args);
                    }
                };
                return new BrowserFunctionWrapper() {

                    @Override
                    public boolean isDisposed() {
                        return fct.isDisposed();
                    }

                    @Override
                    public void dispose() {
                        fct.dispose();
                    }
                };
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeView() {
        if (m_viewRequestCallback != null && !m_viewRequestCallback.isDisposed()) {
            m_viewRequestCallback.dispose();
        }
        if (m_updateRequestStatusCallback != null && !m_updateRequestStatusCallback.isDisposed()) {
            m_updateRequestStatusCallback.dispose();
        }
        if (m_cancelRequestCallback != null && !m_cancelRequestCallback.isDisposed()) {
            m_cancelRequestCallback.dispose();
        }
        if (m_isPushSupportedCallback != null && !m_isPushSupportedCallback.isDisposed()) {
            m_isPushSupportedCallback.dispose();
        }
        if (m_validateCurrentValueInViewCallback != null && !m_validateCurrentValueInViewCallback.isDisposed()) {
            m_validateCurrentValueInViewCallback.dispose();
        }
        if (m_retrieveCurrentValueFromViewCallback != null && !m_retrieveCurrentValueFromViewCallback.isDisposed()) {
            m_retrieveCurrentValueFromViewCallback.dispose();
        }
        if (m_rpcCallback != null && !m_rpcCallback.isDisposed()) {
            m_rpcCallback.dispose();
        }
        if (m_additionalCallbacks != null) {
            m_additionalCallbacks.forEach(c -> {
                if (!c.isDisposed()) {
                    c.dispose();
                }
            });
            m_additionalCallbacks = null;
        }
        if (m_shell != null && !m_shell.isDisposed()) {
            m_shell.dispose();
        }
        m_shell = null;
        if (m_browserWrapper != null) {
            synchronized (m_browserWrapper) { // NOSONAR
                m_browserWrapper = null;
            }
        }
        m_viewRequestCallback = null;
        m_updateRequestStatusCallback = null;
        m_cancelRequestCallback = null;
        m_isPushSupportedCallback = null;
        m_validateCurrentValueInViewCallback = null;
        m_retrieveCurrentValueFromViewCallback = null;
        m_rpcCallback = null;
        m_viewSet.set(false);
        // do instanceof check here to avoid a public discard method in the ViewableModel interface
        if (getViewableModel() instanceof SubnodeViewableModel) {
            ((SubnodeViewableModel)getViewableModel()).discard();
        }

        var nc = getNodeContainer();
        if (m_nodeStateChangeListener != null) {
            nc.removeNodeStateChangeListener(m_nodeStateChangeListener);
            m_nodeStateChangeListener = null;
        }

        List<NativeNodeContainer> viewNodes;
        if (nc instanceof NativeNodeContainer) {
            viewNodes = Collections.singletonList((NativeNodeContainer)nc);
        } else {
            viewNodes = WizardPageUtil.getWizardPageNodes(((SubNodeContainer)nc).getWorkflowManager(), true);
        }
        var nvm = NodeViewManager.getInstance();
        viewNodes.stream().filter(NodeViewManager::hasNodeView)
            .forEach(nnc -> nvm.getDataServiceManager().deactivateDataServices(NodeWrapper.of(nnc)));

    }

     /**
     * {@inheritDoc}
     */
    @Override
    protected boolean showApplyOptionsDialog(final boolean showDiscardOption, final String title, final String message) {
        ElementRadioSelectionDialog dialog = new ElementRadioSelectionDialog(m_browserWrapper.getShell());
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setSize(60, showDiscardOption ? 14 : 11);
        RadioItem discardOption =
            new RadioItem(DISCARD_LABEL, null, DISCARD_DESCRIPTION);
        RadioItem applyOption = new RadioItem(APPLY_LABEL, null,
            String.format(APPLY_DESCRIPTION_FORMAT, showDiscardOption ? ", closes the view" : ""));
        RadioItem newDefaultOption = new RadioItem(APPLY_DEFAULT_LABEL, null,
            String.format(APPLY_DEFAULT_DESCRIPTION_FORMAT, showDiscardOption ? ", closes the view" : ""));
        if (showDiscardOption) {
            dialog.setElements(new RadioItem[]{discardOption, applyOption, newDefaultOption});
            dialog.setInitialSelectedElement(discardOption);
        } else {
            dialog.setElements(new RadioItem[]{applyOption, newDefaultOption});
            dialog.setInitialSelectedElement(applyOption);
        }
        dialog.open();
        if (dialog.getReturnCode() != IDialogConstants.OK_ID) {
            return false;
        }
        RadioItem selectedItem = dialog.getSelectedElement();
        if (applyOption.equals(selectedItem)) {
            return applyTriggered(false);
        }
        if (newDefaultOption.equals(selectedItem)) {
            return applyTriggered(true);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean viewInteractionPossible() {
        return m_viewSet.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validateCurrentValueInView() {
        String evalCode = "window.KnimePageLoader.validate();";
        String warnMessage = "Current view value is invalid";
        return evaluateAsync(evalCode, VIEW_VALID, Boolean.FALSE, warnMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String retrieveCurrentValueFromView() {
        String evalCode = "window.KnimePageLoader.getPageValues();";
        String warnMessage = "Unable to retrieve value from view";
        return evaluateAsync(evalCode, VIEW_VALUE, EMPTY_OBJECT_STRING, warnMessage);
    }

    private static boolean isExecuted(final NodeContext context) {
        NodeContainerUI nc = context.getContextObjectForClass(NodeContainerUI.class).orElse(null);
        return nc != null && nc.getNodeContainerState().isExecuted();
    }

    /**
     * Calls evaluate on the browser and waits until a given object was assigned by a {@link BrowserFunction}
     *
     * @param <O> The class of the referenceObject
     * @param evalCode the code the browser should evaluate
     * @param referenceObject the object that should be assigned by a {@link BrowserFunction}, this call will wait until
     *            the object is not null
     * @param defaultValue the value by which the a warning message should be issued
     * @param warnMessage a warning message in case the async call is not successful or only the default value is
     *            retrieved
     * @return the retrieved value from the async call, may be null in case of an error; returns the default value if
     *         the underlying node is not executed
     */
    private <O> O evaluateAsync(final String evalCode, final String referenceObject, final O defaultValue,
        final String warnMessage) {
        if (m_nodeContext != null && !isExecuted(m_nodeContext)) {
            return defaultValue;
        }
        Display display = getDisplay();
        @SuppressWarnings("unchecked")
        AtomicReference<O> reference = (AtomicReference<O>)m_asyncEvalReferenceMap.get(referenceObject);
        WizardViewCreator<REP, VAL> creator = getViewCreator();
        String wrappedCode = creator.wrapInTryCatch(evalCode);
        m_browserWrapper.execute(wrappedCode);
        long startTime = System.currentTimeMillis();
        if (display != null) {
            while (reference.get() == null) {
                if (System.currentTimeMillis() - startTime > ASYNC_TIMEOUT) {
                    reference.set(defaultValue);
                    break;
                }
                if (display.readAndDispatch()) {
                    display.sleep();
                }
            }
            display.wake();
        }
        O localValue = reference.get();
        reference.set(null);
        if (localValue != null && localValue.equals(defaultValue)) {
            LOGGER.warn(warnMessage);
        }
        return localValue == null ? defaultValue : localValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showValidationErrorInView(final String error) {
        WizardViewCreator<REP, VAL> creator = getViewCreator();
        String escapedError = error.replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ");
        // If single node (non-component), update to valid JS string.
        if (getModel() instanceof NodeModel) {
            escapedError = "'" + escapedError + "'";
        }
        String showErrorCall = creator.wrapInTryCatch("window.KnimePageLoader.setValidationError(" + escapedError + ")");
        m_browserWrapper.execute(showErrorCall);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void respondToViewRequest(final String response) {
        Display display = getDisplay();
        if (display == null) {
            // view most likely disposed
            return;
        }
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                LOGGER.debug("Sending response: " + response);
                String call = "window.KnimeInteractivity.respondToViewRequest(JSON.parse('" + response + "'));";
                WizardViewCreator<REP, VAL> creator = getViewCreator();
                call = creator.wrapInTryCatch(call);
                if (m_browserWrapper != null && !m_browserWrapper.isDisposed()) {
                    m_browserWrapper.execute(call);
                }
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushRequestUpdate(final String monitor) {
        Display display = getDisplay();
        if (display == null) {
            // view most likely disposed
            return;
        }
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                String call = "window.KnimeInteractivity.updateResponseMonitor(JSON.parse('" + monitor + "'));";
                WizardViewCreator<REP, VAL> creator = getViewCreator();
                call = creator.wrapInTryCatch(call);
                if (m_browserWrapper != null && !m_browserWrapper.isDisposed()) {
                    m_browserWrapper.execute(call);
                }
            }
        });
    }

    private class ViewRequestFunction extends BrowserFunctionInternal {

        /**
         * @param browser
         * @param name
         */
        public ViewRequestFunction(final BrowserWrapper browser, final String name) {
            super(browser, name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object function(final Object[] arguments) {
            if (arguments == null || arguments.length < 1) {
                return false;
            }
            return handleViewRequest((String)arguments[0]);
        }

    }

    private class UpdateRequestStatusFunction extends BrowserFunctionInternal {

        /**
         * @param browser
         * @param name
         */
        public UpdateRequestStatusFunction(final BrowserWrapper browser, final String name) {
            super(browser, name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object function(final Object[] arguments) {
            if (arguments == null || arguments.length < 1) {
                return false;
            }
            return updateRequestStatus((String)arguments[0]);
        }
    }

    private class CancelRequestFunction extends BrowserFunctionInternal {

        /**
         * @param browser
         * @param name
         */
        public CancelRequestFunction(final BrowserWrapper browser, final String name) {
            super(browser, name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object function(final Object[] arguments) {
            if (arguments == null || arguments.length < 1) {
                return false;
            }
            cancelRequest((String)arguments[0]);
            return null;
        }
    }

    private class PushSupportedFunction extends BrowserFunctionInternal {

        /**
         * @param browser
         * @param name
         */
        public PushSupportedFunction(final BrowserWrapper browser, final String name) {
            super(browser, name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object function(final Object[] arguments) {
            return isPushEnabled();
        }

    }

    private class RpcFunction extends BrowserFunctionInternal {

        private final JsonRpcFunction m_function;

        RpcFunction(final BrowserWrapper browser, final JsonRpcFunction function) {
            super(browser, JsonRpcFunction.FUNCTION_NAME);
            m_function = function;
        }

        @Override
        public Object function(final Object[] args) {
            return m_function.call((String)args[0]);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
            super.dispose();
            m_function.dispose();
        }

    }

    @SuppressWarnings("unchecked")
    private class AsyncEvalCallbackFunction<O> extends BrowserFunctionInternal {

        private static final String SUCCESS_TRUE = "{ success: true }";
        private static final String SUCCESS_FALSE = "{ success: false }";

        private final AtomicReference<O> m_referenceObject;
        private final O m_defaultValue;

        public AsyncEvalCallbackFunction(final BrowserWrapper browser, final String name, final String referenceKey,
            final O defaultValue) {
            super(browser, name);
            m_referenceObject = (AtomicReference<O>)m_asyncEvalReferenceMap.get(referenceKey);
            m_defaultValue = defaultValue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object function(final Object[] arguments) {
            if (arguments == null || arguments.length < 1) {
                m_referenceObject.set(m_defaultValue);
                return SUCCESS_FALSE;
            }
            m_referenceObject.set((O)arguments[0]);
            return SUCCESS_TRUE;
        }

    }

    /**
     * Wrapper to abstract from specific browser implementations.
     */
    @SuppressWarnings("javadoc")
    protected interface BrowserWrapper {

        void execute(String call);

        void setText(String html, boolean b);

        void setLayoutData(GridData gridData);

        Display getDisplay();

        void addProgressListener(ProgressListener progressListener);

        void removeProgressListener(ProgressListener progressListener);

        void addLocationListener(LocationListener locationListener);

        void removeLocationListener(LocationListener locationListener);

        void setUrl(String absolutePath);

        void setText(String html);

        Shell getShell();

        boolean isDisposed();

        BrowserFunctionWrapper registerBrowserFunction(String name, Function<Object[], Object> func);

    }

    @SuppressWarnings("javadoc")
    protected interface BrowserFunctionWrapper {

        boolean isDisposed();

        void dispose();

    }

    private abstract class BrowserFunctionInternal {

        private BrowserFunctionWrapper m_browserFunctionWrapper;

        public BrowserFunctionInternal(final BrowserWrapper browserWrapper, final String name) {
            m_browserFunctionWrapper = browserWrapper.registerBrowserFunction(name, this::function);
        }

        public boolean isDisposed() {
            return m_browserFunctionWrapper.isDisposed();
        }

        public void dispose() {
            m_browserFunctionWrapper.dispose();
        }

        public abstract Object function(final Object[] arguments);

    }

}
