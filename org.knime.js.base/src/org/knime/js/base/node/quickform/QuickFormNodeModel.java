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
 */
package org.knime.js.base.node.quickform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;

/**
 * Model of a quick form node.
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 * @param <REP> The representation implementation of the quick form node.
 * @param <VAL> The value implementation of the quick form node.
 * @param <CONF> The configuration implementation of the quick form node.
 *
 */
public abstract class QuickFormNodeModel
        <REP extends QuickFormRepresentationImpl<VAL>,
        VAL extends DialogNodeValue & WebViewContent,
        CONF extends QuickFormConfig<VAL>>
        extends NodeModel
        implements DialogNode<REP, VAL>,
        WizardNode<REP, VAL> {

    /**
     * Config key for the overwrite mode. Used in {@link #saveCurrentValue(NodeSettingsWO)}.
     */
    public static final String CFG_OVERWRITE_MODE = "overwriteMode";
    /**
     * Config key for the value. Used in {@link #saveCurrentValue(NodeSettingsWO)}.
     */
    public static final String CFG_CURRENT_VALUE = "currentValue";

    private static final NodeLogger LOGGER = NodeLogger.getLogger(QuickFormNodeModel.class);

    private CONF m_config = createEmptyConfig();
    private VAL m_dialogValue = null;
    private VAL m_viewValue = null;

    /**
     * Creates a new quick form model with the given number (and types!) of input
     * and output types.
     *
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected QuickFormNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
    }

    /**
     * @return The config of this node.
     */
    protected CONF getConfig() {
        return m_config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        File valFile = new File(nodeInternDir, "viewvalue.xml");
        NodeSettingsRO valSettings = NodeSettings.loadFromXML(new FileInputStream(valFile));
        m_viewValue = createEmptyViewValue();
        try {
            m_viewValue.loadFromNodeSettings(valSettings);
        } catch (InvalidSettingsException e) {
            // what to do?
            LOGGER.error("Error loading internals: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        NodeSettings valSettings = new NodeSettings("viewvalue");
        if (m_viewValue != null) {
            m_viewValue.saveToNodeSettings(valSettings);
        }
        File valFile = new File(nodeInternDir, "viewvalue.xml");
        valSettings.saveToXML(new FileOutputStream(valFile));
    }

    /**
     * @return Empty instance of the config.
     */
    public abstract CONF createEmptyConfig();

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getDialogRepresentation() {
        return getRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL createEmptyDialogValue() {
        return createEmptyViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        return getRepresentation();
    }

    /**
     * @return Representation based on the current config.
     */
    protected REP getRepresentation() {
        REP representation = createEmptyViewRepresentation();
        representation.setLabel(getConfig().getLabel());
        representation.setDescription(getConfig().getDescription());
        return representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getViewValue() {
        return m_viewValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewContent, final boolean useAsDefault) {
        m_viewValue = viewContent;
        if (useAsDefault) {
            copyValueToConfig();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_viewValue = null;
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
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final VAL viewContent) {
        return null;
    }

    /**
     * Sets the view value as default value of the config.
     */
    protected abstract void copyValueToConfig();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDialogValue(final VAL value) {
        m_dialogValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getDialogValue() {
        return m_dialogValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInDialog() {
        return m_config.getHideInDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * Returns the value that should currently be used.
     *
     * The priority of values is as follows:
     * <ol>
     * <li>View value</li>
     * <li>Dialog value</li>
     * <li>Default value of config</li>
     * </ol>
     *
     * @return The value with the highest priority which is valid.
     */
    protected VAL getRelevantValue() {
        switch (getOverwriteMode()) {
            case WIZARD:
                return m_viewValue;
            case DIALOG:
                return m_dialogValue;
            default:
                return m_config.getDefaultValue();
        }
    }

    /**
     * Sets {@link #getRelevantValue()} as the view value.
     */
    protected void updateViewValue() {
        m_viewValue = getRelevantValue();
    }

    /**
     * @return The mode in which the value is overwritten
     */
    protected ValueOverwriteMode getOverwriteMode() {
        if (m_viewValue != null) {
            return ValueOverwriteMode.WIZARD;
        } else if (m_dialogValue != null) {
            return ValueOverwriteMode.DIALOG;
        } else {
            return ValueOverwriteMode.NONE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        content.addString(CFG_OVERWRITE_MODE, getOverwriteMode().name());
        NodeSettingsWO settings = content.addNodeSettings(CFG_CURRENT_VALUE);
        getRelevantValue().saveToNodeSettings(settings);
    }

}
