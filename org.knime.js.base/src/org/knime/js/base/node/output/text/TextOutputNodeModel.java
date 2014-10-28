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
package org.knime.js.base.node.output.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.knime.base.util.flowvariable.FlowVariableProvider;
import org.knime.base.util.flowvariable.FlowVariableResolver;
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
public class TextOutputNodeModel extends NodeModel implements
    WizardNode<TextOutputRepresentation, TextOutputValue>, FlowVariableProvider {

    private TextOutputConfig m_config = new TextOutputConfig();
    private TextOutputRepresentation m_representation;
    private String m_viewPath;

    /**
     * Creates a new file download node model.
     */
    public TextOutputNodeModel() {
        super(new PortType[]{FlowVariablePortObject.TYPE}, new PortType[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
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
        m_representation.setTextFormat(m_config.getTextFormat().toString());
        String flowVarCorrectedText;
        try {
            flowVarCorrectedText = FlowVariableResolver.parse(m_config.getText(), this);
        } catch (NoSuchElementException nse) {
            throw new InvalidSettingsException(nse.getMessage(), nse);
        }
        m_representation.setText(flowVarCorrectedText);
        return new PortObject[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final TextOutputValue viewContent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final TextOutputValue viewContent, final boolean useAsDefault) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextOutputRepresentation getViewRepresentation() {
        return m_representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextOutputValue getViewValue() {
        return new TextOutputValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextOutputRepresentation createEmptyViewRepresentation() {
        return new TextOutputRepresentation(m_config.getLabel(), m_config.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextOutputValue createEmptyViewValue() {
        return new TextOutputValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.output.text";
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
        m_representation = createEmptyViewRepresentation();
        m_representation.setTextFormat(m_config.getTextFormat().toString());
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
        // do nothing
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
        m_viewPath = null;
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
            return viewCreator.createWebResources("Image Output", getViewRepresentation(), getViewValue());
        } catch (IOException e) {
            return null;
        }
    }

}
