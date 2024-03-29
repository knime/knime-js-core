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
 *   Apr 4, 2021 (hornm): created
 */
package org.knime.core.wizard.rpc;

import java.util.List;

import org.knime.core.node.wizard.page.WizardPage;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.wizard.SubnodeViewableModel;

import com.fasterxml.jackson.databind.util.RawValue;

/**
 * Default implementation of {@link PageContainer}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 *
 * @since 4.5
 */
public final class DefaultPageContainer implements PageContainer {

    private final RawValue m_page;

    private final List<String> m_resetNodes;

    private final List<String> m_reexecutedNodes;

    /**
     * @param page - {@link WizardPage} for the associated {@link SubnodeViewableModel}
     * @param resetNodes - a list of string {@link NodeIDSuffix} for nodes reset during the current re-execution event
     *            or null if re-execution is not in progress.
     * @param reexecutedNodes - a list of string {@link NodeIDSuffix} for nodes that have been reset by a re-execution
     *            event and are effectively re-executed (no longer pending re-execution; e.g. finished, failed,
     *            deactivated, etc.) or an empty list if the nodes reset by the re-execution event are still awaiting
     *            execution.
     */
    public DefaultPageContainer(final RawValue page, final List<String> resetNodes,
        final List<String> reexecutedNodes) {
        m_page = page;
        m_resetNodes = resetNodes;
        m_reexecutedNodes = reexecutedNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getReexecutedNodes() {
        return m_reexecutedNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getResetNodes() {
        return m_resetNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawValue getPage() {
        return m_page;
    }

}
