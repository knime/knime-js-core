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

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.ExternalNodeOutput;
import org.knime.core.node.dialog.OutputNode;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class FileDownloadNodeModel extends AbstractWizardNodeModel<FileDownloadRepresentation, FileDownloadValue>
    implements OutputNode {

    private FileDownloadConfig m_config = new FileDownloadConfig();

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
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            FileDownloadRepresentation representation = getViewRepresentation();
            if (representation == null) {
                representation = createEmptyViewRepresentation();
            }
            representation.setLabel(m_config.getLabel());
            representation.setDescription(m_config.getDescription());
            representation.setLinkTitle(m_config.getLinkTitle());
            representation.setPath(getPathFromVariable().toString());
        }
        return new PortObject[0];
    }

    private Path getPathFromVariable() throws InvalidSettingsException {
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
        Path path = Paths.get(value);
        if (!Files.exists(path)) {
            throw new InvalidSettingsException("Variable \"" + varName + "\" does not denote an existing file: "
                + value);
        }
        return path;
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
    public FileDownloadRepresentation createEmptyViewRepresentation() {
        return new FileDownloadRepresentation();
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
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new FileDownloadConfig().loadSettings(settings);
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
    protected void performReset() {
        // do nothing
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
    protected String getInteractiveViewName() {
        return (new FileDownloadNodeFactory()).getInteractiveViewName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalNodeOutput getExternalOutput() {
        try {
            URL url = getPathFromVariable().toUri().toURL();
            return ExternalNodeOutput.builder(m_config.getResourceName()).resource(url).build();
        } catch (MalformedURLException | InvalidSettingsException ex) {
            getLogger().error("Could not get output resource URL: " + ex.getMessage(), ex);
            return ExternalNodeOutput.builder(m_config.getResourceName()).build();
        }
    }

}
