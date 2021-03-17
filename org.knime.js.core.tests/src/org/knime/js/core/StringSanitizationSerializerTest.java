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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author ben.laney
 */
@SuppressWarnings("javadoc")
public class StringSanitizationSerializerTest {

    private static final ArrayList<String> DEFAULT_PARAM = new ArrayList<>();

    private static ObjectMapper m_mapper = JSONViewContent.createObjectMapper();

    @Test
    public void testEmptySerialization() throws IOException {

        String testString = "";
        String expectedString = "\"" + testString + "\"";

        StringSanitizationSerializer serializer = new StringSanitizationSerializer(DEFAULT_PARAM, DEFAULT_PARAM, true);
        assertEquals(wrapTestGetResult(serializer, testString), expectedString);
    }

    @Test
    public void testDefaultSerialization() throws IOException {

        String testString = "'$p3<1la [h4r`$'";
        String expectedString = "\"&#39;$p3&lt;1la [h4r&#96;$&#39;\"";

        StringSanitizationSerializer serializer = new StringSanitizationSerializer(DEFAULT_PARAM, DEFAULT_PARAM, true);
        assertEquals(wrapTestGetResult(serializer, testString), expectedString);
    }

    @Test
    public void testBasicTextSerialization() throws IOException {

        String testString = "Jack and Jill";
        String expectedString = "\"" + testString + "\"";

        StringSanitizationSerializer serializer = new StringSanitizationSerializer(DEFAULT_PARAM, DEFAULT_PARAM, true);
        assertEquals(wrapTestGetResult(serializer, testString), expectedString);
    }

    @Test
    public void testElemSerialization() throws IOException {

        ArrayList<String> allowedElem = new ArrayList<>();

        allowedElem.add("div");

        String testString = "<div>Went up</div><p>the hill</p>";
        String expectedString = "\"<div>Went up</div>the hill\"";

        StringSanitizationSerializer serializer = new StringSanitizationSerializer(allowedElem, DEFAULT_PARAM, true);
        assertEquals(wrapTestGetResult(serializer, testString), expectedString);
    }

    @Test
    public void testAttrSerialization() throws IOException {

        ArrayList<String> allowedAttr = new ArrayList<>();

        allowedAttr.add("id");

        String testString = "<div id=\"fetched\" class=\"filled\">Pail of water</div>";
        String expectedString = "\"<div id=\\\"fetched\\\">Pail of water</div>\"";

        StringSanitizationSerializer serializer = new StringSanitizationSerializer(DEFAULT_PARAM, allowedAttr, true);
        assertEquals(wrapTestGetResult(serializer, testString), expectedString);
    }

    @Test
    public void testCssSerialization() throws IOException {

        String testString = "<div style=\"color:\"gold\"\"\">Broken crown</div>";
        String expectedString = "\"<div>Broken crown</div>\"";

        StringSanitizationSerializer serializer = new StringSanitizationSerializer(DEFAULT_PARAM, DEFAULT_PARAM, false);
        assertEquals(wrapTestGetResult(serializer, testString), expectedString);
    }

    private static String wrapTestGetResult(final StringSanitizationSerializer serializer, final String input)
        throws IOException {
        try (StringWriter stringWriter = new StringWriter(); JsonGenerator generator = getGenerator(stringWriter)) {
            serializer.serialize(input, generator, getProvider());
            generator.close();
            stringWriter.close();
            return stringWriter.toString();
        }
    }

    private static JsonGenerator getGenerator(final Writer writer) throws IOException {
        return m_mapper.createGenerator(writer);
    }

    private static SerializerProvider getProvider() {
        return m_mapper.getSerializerProvider();
    }
}
