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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * A serializer-provider modifier to be registered with the {@link ObjectMapper} module for a serialization parent of
 * the {@link JSONWebNode} class. For different application serialization mechanisms, this modifier should be registered
 * on each instance of {@link ObjectMapper} which will serialize an instance of {@link JSONWebNode}, even if it is a
 * nested POJO field.
 *
 * The modifier intercepts the default serializer for the class and creates a custom serializer which can perform both
 * default and field-specific serializations. Importantly, by intercepting via modifier, we can access the default
 * serializer at runtime and choose based on the current KNIME Node identity if we want to use the default or custom
 * serializer.
 *
 *
 * @author ben.laney
 * @since 4.4
 */
public class JSONWebNodeModifier extends BeanSerializerModifier {

    @SuppressWarnings("unchecked")
    @Override
    public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc,
        final JsonSerializer<?> serializer) {
        if (beanDesc.getBeanClass().equals(JSONWebNode.class)) {
            return new JSONWebNodeSerializer((JsonSerializer<Object>)serializer, beanDesc);
        }
        return serializer;
    }
}