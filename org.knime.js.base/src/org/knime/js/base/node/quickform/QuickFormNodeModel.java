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
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeRepresentation;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @param <REP> The configuration content of the quickform node.
 * @param <VAL> The node value implementation of the quickform node.
 *
 */
public abstract class QuickFormNodeModel<REP extends DialogNodeRepresentation<VAL> & WebViewContent,
        VAL extends DialogNodeValue & WebViewContent>
        extends NodeModel implements DialogNode<REP, VAL>, WizardNode<REP, VAL> {

    private final REP m_representation;
    private VAL m_value;

    /**
     * Creates a new quickform model with the given number (and types!) of input
     * and output types.
     *
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected QuickFormNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
        m_representation = createEmptyViewRepresentation();
        m_value = createEmptyViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        getDialogRepresentation().saveToNodeSettings(settings);
        getDialogValue().saveToNodeSettings(settings);
        getViewRepresentation().saveToNodeSettings(settings);
        getViewValue().saveToNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        REP representation = getDialogRepresentation();
        representation.loadFromNodeSettings(settings);
        VAL value = getDialogValue();
        value.loadFromNodeSettings(settings);
        REP viewRepresentation = getViewRepresentation();
        viewRepresentation.loadFromNodeSettings(settings);
        VAL viewValue = getViewValue();
        viewValue.loadFromNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getDialogRepresentation() {
        return m_representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getDialogValue() {
        return m_value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        return m_representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getViewValue() {
        return m_value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewContent, final boolean useAsDefault) {
        m_value = viewContent;
        if (useAsDefault) {
            // TODO: overwrite node settings
        }
    };
}
