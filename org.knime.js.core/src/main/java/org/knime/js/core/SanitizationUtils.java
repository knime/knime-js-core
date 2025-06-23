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
 *   3 Mar 2024 (albrecht): created
 */
package org.knime.js.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 5.2
 */
public final class SanitizationUtils {

    private SanitizationUtils() { /* no default constructor */ }

    private static final NodeLogger LOGGER = NodeLogger.getLogger(SanitizationUtils.class);

    static final List<String> SAFE_WIDGET_NODES = Arrays.asList(
        "String Widget",
        "Integer Widget",
        "Double Widget",
        "Boolean Widget",
        "Slider Widget",
        "List Box Widget",
        "Date&Time Widget",
        "Credentials Widget",
        "File Chooser Widget",
        "File Upload Widget",
        "Refresh Button Widget",
        "Column Filter Widget",
        "Nominal Row Filter Widget",
        "Single Selection Widget",
        "Multiple Selection Widget",
        "Value Selection Widget",
        "Column Selection Widget",
        "Text Output Widget",
        "Image Output Widget",
        "Interactive Value Filter Widget",
        "Interactive Range Slider Filter Widget"
    );

    static final List<String> UNSAFE_LEGACY_WIDGET_NODES = Arrays.asList(
        "String Widget",
        "Integer Widget",
        "Double Widget",
        "Boolean Widget",
        "Slider Widget",
        "List Box Widget",
        "Date&Time Widget",
        "Credentials Widget",
        "File Chooser Widget",
        "File Upload Widget",
        "Molecule Widget",
        "Column Filter Widget",
        "Nominal Row Filter Widget",
        "Single Selection Widget",
        "Multiple Selection Widget",
        "Value Selection Widget",
        "Column Selection Widget",
        "Image Output Widget",
        "Interactive Value Filter Widget",
        "Interactive Range Slider Filter Widget"
    );

    static final List<String> TREATED_EXEMPT_NODES = Arrays.asList(
        JavaScriptViewCreator.SINGLE_PAGE_NODE_NAME,
        "Generic JavaScript View",
        "Generic JavaScript View (JavaScript) (legacy)",
        "Generic JavaScript View (legacy)"
    );

    private static final List<String> ALWAYS_ALLOWED_NODES = new ArrayList<>(TREATED_EXEMPT_NODES);
    private static final List<String> UNSAFE_LEGACY_NODES = new ArrayList<>();
    static {
        SAFE_WIDGET_NODES.stream().forEach(widget -> {
            ALWAYS_ALLOWED_NODES.add(widget);
            ALWAYS_ALLOWED_NODES.add(widget + " (legacy)");
        });
        UNSAFE_LEGACY_WIDGET_NODES.stream().forEach(widget -> {
            UNSAFE_LEGACY_NODES.add(widget);
            UNSAFE_LEGACY_NODES.add(widget + " (legacy)");
        });
    }

    // default empty
    static final List<String> USER_ALLOWED_NODES = getUserAllowedNodes();

    static List<String> getUserAllowedNodes() {
        String allowedNodesLocation = System.getProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH);
        if (StringUtils.isNotBlank(allowedNodesLocation)) {
            try {
                Path allowedNodesPath = Paths.get(allowedNodesLocation);
                CheckUtils.checkArgument(allowedNodesPath.isAbsolute(), "System property "
                    + JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH + " must be an absolute path.");
                CheckUtils.checkArgument(Files.exists(allowedNodesPath),
                    "System property " + JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH + " file not found.");
                return Files.readAllLines(allowedNodesPath);
            } catch (IOException ex) {
                LOGGER.error("Could not read from file configured for "
                    + JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH + ".", ex);
            }
        }
        return new ArrayList<>();
    }

    // default empty
    static final List<String> ALLOW_ELEMENTS = getSysPropertyOrDefault(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ELEMS);

    // default empty
    static final List<String> ALLOW_ATTRIBUTES =
        getSysPropertyOrDefault(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ATTRS);

    // default true
    static boolean ALLOW_CSS = BooleanUtils.isNotTrue(
        StringUtils.equalsIgnoreCase(System.getProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_CSS), "false"));

    /**
     * Helper to retrieve the value of a comma-separated {@literal String} system property (if it has been defined) and
     * return an array of strings; else an empty array.
     *
     * @param propertyName - system property name to retrieve and parse
     * @return array of parsed strings from the defined property value else an empty string array
     */
    static List<String> getSysPropertyOrDefault(final String propertyName) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(propertyValue.split(",")).stream().map(String::trim).collect(ArrayList::new,
            ArrayList::add, ArrayList::addAll);
    }

    static boolean shouldSanitizeNode(final JSONWebNodeInfo nodeInfo) {
        String nodeName = nodeInfo.getNodeName();
        if (nodeInfo.isLegacyMode() && UNSAFE_LEGACY_WIDGET_NODES.contains(nodeName)
            && !USER_ALLOWED_NODES.contains(nodeName)) {
            // force sanitization of widget nodes if displayed in legacy mode, except if explicitly allowed by user
            return true;
        } else {
            return Stream.concat(ALWAYS_ALLOWED_NODES.stream(), USER_ALLOWED_NODES.stream())
                    .noneMatch(allowedNodeName -> StringUtils.equals(allowedNodeName, nodeName));
        }
    }

}
