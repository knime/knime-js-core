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
 *   Jun 26, 2020 (benlaney): created
 */
package org.knime.js.core.layout;

import java.util.Optional;

import org.knime.core.util.LoadVersion;

/**
 * A Version representing layout format for SubNodeContainer Layouts. This enum derives versions from the
 * {@link LoadVersion} enum with a narrower range to reflect the it's creation date. It can map LoadVersion strings to
 * the corresponding LayoutVersion. Additional versions need only be added for breaking changes.
 *
 * @since 4.2
 */
public enum LayoutVersion {
        // Don't modify order, ordinal number are important.
        /**
         * Unversioned SubNodeContainer layouts older than 4.2.0.
         *
         * @since 5.14
         */
        V4020Pre("4.2.0Pre"),
        /**
         * Layout versions and layout "Legacy Mode" added. WebPortal/PageBuilder rewrite.
         *
         * @since 5.14
         */
        V4020(LoadVersion.V4020.getVersionString()),
        /**
         * Try to be forward compatible.
         *
         */
        FUTURE(LoadVersion.FUTURE.getVersionString());

    private final String m_versionString;

    private LayoutVersion(final String str) {
        m_versionString = str;
    }

    /**
     * @return the string representation of the LayoutVersion.
     */
    public String getVersionString() {
        return m_versionString;
    }

    /**
     * Check the age of the LayoutVersion against the version provided.
     *
     * @param version the version to compare.
     * @return if the calling version is older than the provided version.
     */
    public boolean isOlderThan(final LayoutVersion version) {
        return ordinal() < version.ordinal();
    }

    /**
     * Get the layout version for the version string. This method compares the provided string with LayoutVersions as
     * well as maps any versions found in {@link LoadVersion} to the correct LayoutVersion. If no version is found, an
     * empty Optional is returned.
     *
     * @param string the {@link LayoutVersion} or {@link LoadVersion} version string.
     * @return The LayoutVersion as an Optional.
     */
    public static Optional<LayoutVersion> get(final String string) {
        for (LayoutVersion e : values()) {
            if (e.getVersionString().equals(string)) {
                return Optional.of(e);
            }
        }
        // check LoadVersion mappings
        for (LoadVersion e : LoadVersion.values()) {
            if (e.getVersionString().equals(string)) {
                if (e.isOlderThan(LoadVersion.V4020)) {
                    return Optional.of(V4020Pre);
                } else if (string.equals(V4020.getVersionString())) {
                    return Optional.of(V4020);
                }
                return Optional.of(FUTURE);
            }
        }
        return Optional.empty();
    }
}
