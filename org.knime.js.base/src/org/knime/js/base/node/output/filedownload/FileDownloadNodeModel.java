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
 *   21.10.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.output.filedownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;
import org.knime.js.core.JavaScriptViewCreator;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class FileDownloadNodeModel extends NodeModel implements
    WizardNode<FileDownloadRepresentation, FileDownloadValue> {

    private FileDownloadConfig m_config = new FileDownloadConfig();

    private FileDownloadRepresentation m_representation;

    private String m_viewPath;

    /**
     * Creates a new file download node model.
     */
    public FileDownloadNodeModel() {
        super(new PortType[]{FlowVariablePortObject.TYPE}, new PortType[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        getPathFromVariable();
        return new PortObjectSpec[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        if (m_representation == null) {
            m_representation = createEmptyViewRepresentation();
        }
        m_representation.setPath(getPathFromVariable());
        return new PortObject[0];
    }

    private String getPathFromVariable() throws InvalidSettingsException {
        String varName = m_config.getFlowVariable();
        if (varName == null || varName.length() == 0) {
            throw new InvalidSettingsException("Invalid (empty) variable name");
        }

        String value;
        try {
            value = peekFlowVariableString(varName);
        } catch (NoSuchElementException e) {
            throw new InvalidSettingsException(e.getMessage(), e);
        }
        File f = new File(value);
        if (!f.exists()) {
            throw new InvalidSettingsException("Variable \"" + varName + "\" does not denote an existing file: "
                + value);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final FileDownloadValue viewContent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final FileDownloadValue viewContent, final boolean useAsDefault) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileDownloadRepresentation getViewRepresentation() {
        return m_representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileDownloadValue getViewValue() {
        return new FileDownloadValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileDownloadRepresentation createEmptyViewRepresentation() {
        return new FileDownloadRepresentation(m_config.getLabel(), m_config.getDescription(), m_config.getLinkTitle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileDownloadValue createEmptyViewValue() {
        return new FileDownloadValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.output.filedownload";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        File valFile = new File(nodeInternDir, "representation.xml");
        NodeSettingsRO valSettings = NodeSettings.loadFromXML(new FileInputStream(valFile));
        m_representation =
            new FileDownloadRepresentation(m_config.getLabel(), m_config.getDescription(), m_config.getLinkTitle());
        try {
            m_representation.loadFromNodeSettings(valSettings);
        } catch (InvalidSettingsException e) {
            m_representation = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        NodeSettings valSettings = new NodeSettings("representation");
        if (m_representation != null) {
            m_representation.saveToNodeSettings(valSettings);
        }
        File valFile = new File(nodeInternDir, "representation.xml");
        valSettings.saveToXML(new FileOutputStream(valFile));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        //new FileDownloadConfig().loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_representation = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewHTMLPath() {
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String createViewPath() {
        JavaScriptViewCreator viewCreator = new JavaScriptViewCreator(getJavascriptObjectID());
        try {
            return viewCreator.createWebResources("Quickform View", getViewRepresentation(), getViewValue());
        } catch (IOException e) {
            return null;
        }
    }

}
