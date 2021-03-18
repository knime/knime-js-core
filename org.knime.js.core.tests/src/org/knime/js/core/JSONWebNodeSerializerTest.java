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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ben.laney
 */
@SuppressWarnings("javadoc")
public class JSONWebNodeSerializerTest {

    private static final String CHECK_STRING = "0riginal 'STRING'!!!";

    @After
    public void tearDown() {
        System.clearProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML);
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

        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");

        try (StringWriter stringWriter = new StringWriter();) {
            JSONViewContent.createObjectMapper().writeValue(stringWriter, getMockWebNode());
            assertFalse(StringUtils.contains(stringWriter.toString(), CHECK_STRING));
        }
    }

    @Test
    public void testModExclusionByName() throws IOException {

        String nodeName = "my_node";

        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");
        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_NODES, nodeName);

        try (StringWriter stringWriter = new StringWriter();) {
            JSONViewContent.createObjectMapper().writeValue(stringWriter, getNamedMockWebNode(nodeName));
            assertTrue(StringUtils.contains(stringWriter.toString(), CHECK_STRING));
        }
    }

    @Test
    public void testIgnoredProperties() throws IOException {

        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");
        JSONWebNode mockWebNode = getMockWebNode();
        ObjectMapper webNodeMapper = JSONViewContent.createObjectMapper();

        try (StringWriter stringWriter = new StringWriter();) {
            webNodeMapper.writeValue(stringWriter, mockWebNode);
            assertFalse(StringUtils.contains(stringWriter.toString(), CHECK_STRING));

            stringWriter.flush();

            mockWebNode.setGetViewValueMethodName(CHECK_STRING);
            webNodeMapper.writeValue(stringWriter, mockWebNode);
            assertTrue(StringUtils.contains(stringWriter.toString(), CHECK_STRING));
        }
    }

    @Test
    public void testSystemProperties() {

        ArrayList<String> mockParam = new ArrayList<>();

        JSONWebNodeSerializer serializer = new JSONWebNodeSerializer(null, null);

        assertTrue(serializer.getAllowNodes().equals(mockParam));
        assertTrue(serializer.getAllowElems().equals(mockParam));
        assertTrue(serializer.getAllowAttrs().equals(mockParam));
        assertTrue(serializer.getAllowCSS());

        String nodeValue = "my_node";
        String elemValue = "div";
        String attrValue = "data";

        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_NODES, nodeValue);
        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ELEMS, elemValue);
        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_ATTRS, attrValue);
        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_ALLOW_CSS, "false");

        serializer = new JSONWebNodeSerializer(null, null);

        assertTrue(serializer.getAllowNodes().contains(nodeValue));
        assertTrue(serializer.getAllowElems().contains(elemValue));
        assertTrue(serializer.getAllowAttrs().contains(attrValue));
        assertFalse(serializer.getAllowCSS());
    }

    private static JSONWebNode getNamedMockWebNode(final String nodeName) {
        JSONWebNodeInfo mockNodeInfo = new JSONWebNodeInfo();
        mockNodeInfo.setNodeName(nodeName);
        return getMockWebNode(mockNodeInfo);
    }

    private static JSONWebNode getMockWebNode() {
        return getMockWebNode(null);
    }

    private static JSONWebNode getMockWebNode(final JSONWebNodeInfo nodeInfo) {
        JSONWebNode mockNode = new JSONWebNode();
        mockNode.setNodeInfo(nodeInfo != null ? nodeInfo : new JSONWebNodeInfo());
        JSONMockContent mockContent = new JSONMockContent();
        mockContent.setPojoValue(CHECK_STRING);
        mockNode.setViewRepresentation(mockContent);
        return mockNode;
    }
}
