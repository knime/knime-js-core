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
 *   Mar 16, 2021 (ben.laney): created
 */
package org.knime.js.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.knime.js.core.JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ATTRS;
import static org.knime.js.core.JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_CSS;
import static org.knime.js.core.JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ELEMS;
import static org.knime.js.core.JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH;
import static org.knime.js.core.JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML;
import static org.knime.js.core.SanitizationUtils.getSysPropertyOrDefault;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;
import org.knime.core.util.FileUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ben.laney
 */
@SuppressWarnings("javadoc")
public class JSONWebNodeSerializerTest {

    private static final String CHECK_STRING = "0riginal 'STRING'!!!";

    @After
    public void tearDown() {
        System.clearProperty(SYS_PROPERTY_SANITIZE_CLIENT_HTML);
        System.clearProperty(SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH);
        System.clearProperty(SYS_PROPERTY_SANITIZE_ALLOW_ELEMS);
        System.clearProperty(SYS_PROPERTY_SANITIZE_ALLOW_ATTRS);
        System.clearProperty(SYS_PROPERTY_SANITIZE_ALLOW_CSS);
    }

    @Test
    public void testDefaultSerializer() throws IOException {
        try (StringWriter stringWriter = new StringWriter();) {
            JSONViewContent.createObjectMapper().writeValue(stringWriter, getMockWebNode());
            assertTrue(StringUtils.contains(stringWriter.toString(), CHECK_STRING));
        }
    }

    @Test
    public void testModifiedSerializer() throws IOException {

        System.setProperty(SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");

        try (StringWriter stringWriter = new StringWriter();) {
            JSONViewContent.createObjectMapper().writeValue(stringWriter, getMockWebNode());
            assertFalse(StringUtils.contains(stringWriter.toString(), CHECK_STRING));
        }
    }

    @Test
    public void testNodeExclusionLists() throws IOException {
        // simple sanity checks
        // the molecule widget only exists as 'legacy mode' and is therefore not exempt
        assertFalse(SanitizationUtils.SAFE_WIDGET_NODES.contains("Molecule Widget"));
        // the text output widget is treated special with input data being sanitized by default
        assertTrue(SanitizationUtils.SAFE_WIDGET_NODES.contains("Text Output Widget"));

        // the text output widget is treated special with input data being sanitized by default
        assertFalse(SanitizationUtils.UNSAFE_LEGACY_WIDGET_NODES.contains("Text Output Widget"));
        // the refresh button only exists as Vue component and thus has no 'legacy mode'
        assertFalse(SanitizationUtils.UNSAFE_LEGACY_WIDGET_NODES.contains("Refresh Button Widget"));
    }

    @Test
    public void testModExclusionByName() throws IOException {

        File allowedNodesConfig = null;

        try {

            List<String> allowedNodes = new ArrayList<String>();
            String nodeName = "my_node";
            allowedNodes.add(nodeName);
            allowedNodes.add("Scatter Plot");
            allowedNodesConfig = getMockAllowedNodesFile(String.join("\n", allowedNodes));

            System.setProperty(SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");
            System.setProperty(SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH,
                allowedNodesConfig.getCanonicalPath());

            // needed for testing only to manually read and update the static configuration field
            updateStaticValues();

            try (StringWriter stringWriter = new StringWriter();) {
                JSONViewContent.createObjectMapper().writeValue(stringWriter, getNamedMockWebNode(nodeName));
                assertTrue(StringUtils.contains(stringWriter.toString(), CHECK_STRING));
            }
        } catch (IOException e) {
            assertTrue("Mocking allowed nodes configuration file failed: " + e, false);
        } finally {
            if (allowedNodesConfig != null) {
                allowedNodesConfig.delete();
            }
        }
    }

    @Test
    public void testIgnoredProperties() throws IOException {

        ObjectMapper webNodeMapper = JSONViewContent.createObjectMapper();

        // set a nested ignored property (explicit exclusion)
        JSONWebNode mockWebNode = getMockWebNode(null, true);
        // set a top-level sibling property (implicit exclusion)
        mockWebNode.setGetViewValueMethodName(CHECK_STRING);

        try (StringWriter stringWriter = new StringWriter();) {
            // default, non-sanitized should match nested ignored and non-annotated property and sibling property
            webNodeMapper.writeValue(stringWriter, mockWebNode);
            int strMatches = StringUtils.countMatches(stringWriter.toString(), CHECK_STRING);
            assertEquals(3, strMatches);
        }

        System.setProperty(SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");
        webNodeMapper = JSONViewContent.createObjectMapper();

        try (StringWriter stringWriter = new StringWriter();) {
            webNodeMapper.writeValue(stringWriter, mockWebNode);
            // should match nested ignored property and sibling property, but not nested sanitized property
            int strMatches = StringUtils.countMatches(stringWriter.toString(), CHECK_STRING);
            assertEquals(2, strMatches);
        }
    }

    @Test
    public void testSystemProperties() {

        ArrayList<String> mockParam = new ArrayList<>();

        assertTrue(SanitizationUtils.USER_ALLOWED_NODES.equals(mockParam));
        assertTrue(SanitizationUtils.ALLOW_ELEMENTS.equals(mockParam));
        assertTrue(SanitizationUtils.ALLOW_ATTRIBUTES.equals(mockParam));
        assertTrue(SanitizationUtils.ALLOW_CSS);

        File allowedNodesConfig = null;

        try {

            List<String> allowedNodes = new ArrayList<String>();

            allowedNodes.add("Table View");
            allowedNodes.add("Scatter Plot");
            allowedNodesConfig = getMockAllowedNodesFile(String.join("\n", allowedNodes));

            String elemValue = "div";
            String attrValue = "data";

            System.setProperty(SYS_PROPERTY_SANITIZE_ALLOW_NODES_PATH,
                allowedNodesConfig.getCanonicalPath());
            System.setProperty(SYS_PROPERTY_SANITIZE_ALLOW_ELEMS, elemValue);
            System.setProperty(SYS_PROPERTY_SANITIZE_ALLOW_ATTRS, attrValue);
            System.setProperty(SYS_PROPERTY_SANITIZE_ALLOW_CSS, "false");

            // needed for testing only to manually read and update the static configuration field
            updateStaticValues();

            List<String> sysAllowedNodes = SanitizationUtils.USER_ALLOWED_NODES;

            assertFalse(sysAllowedNodes.isEmpty());
            sysAllowedNodes.forEach(nodeName -> assertTrue(allowedNodes.contains(nodeName)));
            assertTrue(SanitizationUtils.ALLOW_ELEMENTS.contains(elemValue));
            assertTrue(SanitizationUtils.ALLOW_ATTRIBUTES.contains(attrValue));
            assertFalse(SanitizationUtils.ALLOW_CSS);
        } catch (IOException e) {
            assertTrue("Mocking allowed configuration file failed: " + e, false);
        } finally {
            if (allowedNodesConfig != null) {
                allowedNodesConfig.delete();
            }
        }
    }

    private static void updateStaticValues() {
        SanitizationUtils.USER_ALLOWED_NODES.clear();
        SanitizationUtils.ALLOW_ELEMENTS.clear();
        SanitizationUtils.ALLOW_ATTRIBUTES.clear();
        SanitizationUtils.USER_ALLOWED_NODES.addAll(SanitizationUtils.getUserAllowedNodes());
        SanitizationUtils.ALLOW_ELEMENTS.addAll(getSysPropertyOrDefault(SYS_PROPERTY_SANITIZE_ALLOW_ELEMS));
        SanitizationUtils.ALLOW_ATTRIBUTES.addAll(getSysPropertyOrDefault(SYS_PROPERTY_SANITIZE_ALLOW_ATTRS));
        SanitizationUtils.ALLOW_CSS = BooleanUtils
            .isNotTrue(StringUtils.equalsIgnoreCase(System.getProperty(SYS_PROPERTY_SANITIZE_ALLOW_CSS), "false"));
    }

    private static JSONWebNode getNamedMockWebNode(final String nodeName) {
        JSONWebNodeInfo mockNodeInfo = new JSONWebNodeInfo();
        mockNodeInfo.setNodeName(nodeName);
        return getMockWebNode(mockNodeInfo, false);
    }

    private static JSONWebNode getMockWebNode() {
        return getMockWebNode(null, false);
    }

    private static JSONWebNode getMockWebNode(final JSONWebNodeInfo nodeInfo, final boolean setIgnored) {
        JSONWebNode mockNode = new JSONWebNode();
        mockNode.setNodeInfo(nodeInfo != null ? nodeInfo : new JSONWebNodeInfo());
        JSONMockContent mockContent = new JSONMockContent();
        mockContent.setPojoValue(CHECK_STRING);
        if (setIgnored) {
            mockContent.setPojoIgnoredValue(CHECK_STRING);
        }
        mockNode.setViewRepresentation(mockContent);
        return mockNode;
    }

    private static File getMockAllowedNodesFile(final String allowedNodes) throws IOException {
        File dir = FileUtil.createTempDir("sanitization_junit_test");
        File configFile = new File(dir, "test_allowed_nodes.txt");
        if (configFile.createNewFile()) {
            try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
                outputStream.write(allowedNodes.getBytes());
            }
        } else {
            throw new IllegalStateException("Creating mock allowed nodes config file failed.");
        }
        return configFile;
    }
}
