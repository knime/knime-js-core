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
 *   14.04.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.util.table;

import java.lang.reflect.ParameterizedType;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.JSONDataTable;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 * @param <REP>
 * @param <VAL>
 */
public abstract class WebTableNodeModel<REP extends WebTableViewRepresentation, VAL extends WebTableViewValue>
        extends NodeModel implements WizardNode<REP, VAL> {

    private JSONDataTable m_input;
    private REP m_viewRepresentation;
    private VAL m_viewValue;

    /**
     * Creates a new model with the given number (and types!) of input and
     * output types.
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected WebTableNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        // TODO Auto-generated method stub
        return new PortObjectSpec[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable dataTable = (BufferedDataTable)inObjects[0];
        m_input = new JSONDataTable(dataTable, 1, dataTable.getRowCount(), exec);
        getViewRepresentation().setTable(m_input);
        return new PortObject[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        if (m_viewRepresentation == null) {
            m_viewRepresentation = createEmptyViewRepresentation();
        }
        return m_viewRepresentation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getViewValue() {
        if (m_viewValue == null) {
            m_viewValue = createEmptyViewValue();
        }
        return m_viewValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final WebTableViewValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewContent, final boolean useAsDefault) {
        m_viewValue = viewContent;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public REP createEmptyViewRepresentation() {
        try {
            return ((Class<REP>)((ParameterizedType)this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0]).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public VAL createEmptyViewValue() {
        try {
            return ((Class<VAL>)((ParameterizedType)this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[1]).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_input = null;
        m_viewRepresentation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
    }

}
