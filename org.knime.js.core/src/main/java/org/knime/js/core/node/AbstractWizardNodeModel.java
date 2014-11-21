/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   07.11.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.JavaScriptViewCreator;

/**
 * Abstract implementation of {@link WizardNode}, which manages HTML view creation.
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @param <REP> The concrete class of the {@link JSONViewContent} acting as representation of the view.
 * @param <VAL> The concrete class of the {@link JSONViewContent} acting as value of the view.
 * @since 2.11
 */
public abstract class AbstractWizardNodeModel<REP extends JSONViewContent, VAL extends JSONViewContent> extends NodeModel
    implements WizardNode<REP, VAL> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AbstractWizardNodeModel.class);

    private final Object m_lock = new Object();

    private String m_viewPath;
    private REP m_representation;
    private VAL m_value;
    private final JavaScriptViewCreator<REP, VAL> m_viewCreator;

    /**
     * Creates a new {@link WizardNode} model with the given number (and types!) of input and
     * output types.
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected AbstractWizardNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
        m_representation = createEmptyViewRepresentation();
        m_value = createEmptyViewValue();
        m_viewCreator = new JavaScriptViewCreator<>(getJavascriptObjectID());
    }

    /**
     * A lock object, which should be used when modifying view representation or value.
     * @return the lock
     */
    public Object getLock() {
        return m_lock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        PortObject[] portObjects = performExecute(inObjects, exec);
        return portObjects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
            throws Exception {
        return super.execute(inData, exec);
    }

    /**
     * Invoked on execute. See {@link NodeModel#execute(PortObject[], ExecutionContext) NodeModel.execute}.
     * @param inObjects The input objects.
     * @param exec For {@link BufferedDataTable} creation and progress.
     * @return The output objects.
     * @throws Exception If the node execution fails for any reason.
     */
    protected abstract PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec)
        throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void reset() {
        synchronized (m_lock) {
            m_representation = createEmptyViewRepresentation();
            m_value = createEmptyViewValue();
            resetViewHTML();
        }
        performReset();
    }

    /**
     * Invoked on reset.
     */
    protected abstract void performReset();

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getViewHTMLPath() {
        if (m_viewPath == null || m_viewPath.isEmpty()) {
            // view is not created
            m_viewPath = createViewPath();
        } else {
            // check if file still exists, create otherwise
            File viewFile = new File(m_viewPath);
            if (!viewFile.exists()) {
                m_viewPath = createViewPath();
            }
        }
        return m_viewPath;
    }

    private String createViewPath() {
        JavaScriptViewCreator<REP, VAL> viewCreator = new JavaScriptViewCreator<REP, VAL>(getJavascriptObjectID());
        try {
            return viewCreator.createWebResources(getInteractiveViewName(), getViewRepresentation(), getViewValue());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets the name of the interactive view.
     * This can usually be achieved by returning the concrete {@link NodeFactory#getInteractiveViewName()}.
     * @return the view name
     */
    protected abstract String getInteractiveViewName();

    /**
     * Resets the view HTML, attempts to delete file.
     */
    void resetViewHTML() {
        if (m_viewPath != null) {
            try {
                File viewFile = new File(m_viewPath);
                if (viewFile.exists()) {
                    viewFile.delete();
                }
            } catch (Exception e) { /* do nothing */ }
        }
        m_viewPath = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardViewCreator<REP, VAL> getViewCreator() {
        return m_viewCreator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        synchronized (m_lock) {
            return m_representation;
        }
    }

    /**
     * Sets the view representation.
     * @param representation the representation to set.
     */
    protected void setViewRepresentation(final REP representation) {
        synchronized (m_lock) {
            m_representation = representation;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getViewValue() {
        synchronized (m_lock) {
            return m_value;
        }
    }

    /**
     * Sets the view value.
     * @param value the value to set.
     */
    protected void setViewValue(final VAL value) {
        synchronized (m_lock) {
            m_value = value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewValue, final boolean useAsDefault) {
        synchronized (m_lock) {
            m_value = viewValue;
            if (useAsDefault) {
                useCurrentValueAsDefault();
            }
        }
    }

    /**
     * Uses current view value as new default.
     * Usually achieved by overwriting node config with view values.
     */
    protected abstract void useCurrentValueAsDefault();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        File repFile = new File(nodeInternDir, "representation.xml");
        File valFile = new File(nodeInternDir, "value.xml");
        NodeSettingsRO repSettings = NodeSettings.loadFromXML(new FileInputStream(repFile));
        NodeSettingsRO valSettings = NodeSettings.loadFromXML(new FileInputStream(valFile));
        m_representation = createEmptyViewRepresentation();
        m_value = createEmptyViewValue();
        try {
            m_representation.loadFromNodeSettings(repSettings);
            m_value.loadFromNodeSettings(valSettings);
        } catch (InvalidSettingsException e) {
            // what to do?
            LOGGER.error("Error loading internals: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        NodeSettings repSettings = new NodeSettings("viewRepresentation");
        NodeSettings valSettings = new NodeSettings("viewValue");
        if (m_representation != null) {
            m_representation.saveToNodeSettings(repSettings);
        }
        if (m_value != null) {
            m_value.saveToNodeSettings(valSettings);
        }
        File repFile = new File(nodeInternDir, "representation.xml");
        File valFile = new File(nodeInternDir, "value.xml");
        repSettings.saveToXML(new FileOutputStream(repFile));
        valSettings.saveToXML(new FileOutputStream(valFile));
    }
}
