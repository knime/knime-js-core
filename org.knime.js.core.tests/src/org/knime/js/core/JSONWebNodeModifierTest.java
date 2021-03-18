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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.js.core.layout.bs.JSONLayoutPage;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author ben.laney
 */
@SuppressWarnings("javadoc")
public class JSONWebNodeModifierTest {
    private SerializationConfig m_configMock;

    private BeanDescription m_descriptionMock;

    private JsonSerializer<?> m_serializerMock;

    @Before
    public void initialize() {
        ObjectMapper mockMapper = new ObjectMapper();
        m_configMock = mockMapper.getSerializationConfig();
        m_descriptionMock = getTypeDescription(Object.class);
        m_serializerMock = new StringSerializer();
    }

    @After
    public void tearDown() {
        System.clearProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML);
    }

    @Test
    public void testProvidingDefaultSerializer() {
        JSONWebNodeModifier nodeModifier = new JSONWebNodeModifier();
        JsonSerializer<?> providedSerializer =
            nodeModifier.modifySerializer(m_configMock, m_descriptionMock, m_serializerMock);
        assertEquals(providedSerializer, m_serializerMock);
    }

    @Test
    public void testProvidingCustomSerializer() {
        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");
        m_descriptionMock = getTypeDescription(JSONWebNode.class);
        JSONWebNodeModifier nodeModifier = new JSONWebNodeModifier();
        JsonSerializer<?> providedSerializer =
            nodeModifier.modifySerializer(m_configMock, m_descriptionMock, m_serializerMock);
        assertNotSame(providedSerializer, m_serializerMock);
        assertTrue(providedSerializer instanceof JSONWebNodeSerializer);
    }

    @Test
    public void testJSONWebNodeModifierIsRegisteredWithObjectMappers() throws JsonMappingException {
        System.setProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML, "true");
        assertObjectMapperUsesJSONWebNodeSerializer(JSONLayoutPage.getConfiguredObjectMapper());
        assertObjectMapperUsesJSONWebNodeSerializer(JSONLayoutPage.getConfiguredVerboseObjectMapper());
        assertObjectMapperUsesJSONWebNodeSerializer(JSONViewContent.createObjectMapper());
    }

    private static void assertObjectMapperUsesJSONWebNodeSerializer(final ObjectMapper mapper)
        throws JsonMappingException {
        TypeWrappedSerializer test = (TypeWrappedSerializer)mapper.getSerializerProviderInstance()
            .findTypedValueSerializer(JSONWebNode.class, false, null);
        assertTrue("object mapper doesn't use the custom JSONWebNodeSerializer",
            JSONWebNodeSerializer.class.isAssignableFrom(test.valueSerializer().getClass()));
    }

    private <T> BeanDescription getTypeDescription(final Class<T> oClass) {
        return m_configMock.introspectClassAnnotations(TypeFactory.rawClass(oClass));
    }
}
