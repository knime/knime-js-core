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
 *   Oct 14, 2021 (hornm): created
 */
package org.knime.js.cef;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Debugging info for wizard (node) views.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 4.5
 */
@JsonAutoDetect
public class DebugInfo {

    @SuppressWarnings("javadoc")
    public static final String FUNCTION_NAME = "getDebugInfo";

    /**
     * The CEF's remote debugging port or <code>null</code> if not set.
     */
    public static final String REMOTE_DEBUGGING_PORT = System.getProperty("chromium.remote_debugging_port");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final boolean m_refreshRequired;

    /**
     * @param refreshRequired
     */
    public DebugInfo(final boolean refreshRequired) {
        m_refreshRequired = refreshRequired;
    }

    /**
     * @return the remoteDebuggingPort
     */
    public String getRemoteDebuggingPort() {
        return REMOTE_DEBUGGING_PORT;
    }

    /**
     * @return the refreshRequired
     */
    public boolean isRefreshRequired() {
        return m_refreshRequired;
    }

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            // should never happen
            throw new RuntimeException(ex); // NOSONAR
        }
    }

}
