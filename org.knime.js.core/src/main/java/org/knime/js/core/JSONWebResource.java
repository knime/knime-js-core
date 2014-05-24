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
 *   24.09.2013 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core;

import java.io.IOException;

import org.knime.js.core.JSONWebResource.JSONStringToWebResourceDeserializer;
import org.knime.js.core.JSONWebResource.WebResourceToJSONStringSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Helper class to allow serialization and deserialization of KNIME web resources.
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSerialize(using = WebResourceToJSONStringSerializer.class, as = String.class)
@JsonDeserialize(using = JSONStringToWebResourceDeserializer.class, as = String.class)
public class JSONWebResource {

    private String m_absolutePathSource;
    private String m_relativePathTarget;

    /** Serialization constructor, don't use. */
    public JSONWebResource() { }

    /**
     * Creates a new JSONWebResource.
     * @param absolutePathSource the absolute path to the source file
     * @param relativePathTarget the relative path of the target file
     */
    public JSONWebResource(final String absolutePathSource, final String relativePathTarget) {
        m_absolutePathSource = absolutePathSource;
        m_relativePathTarget = relativePathTarget;
    }

    /**
     * @return the absolutePathSource
     */
    public final String getAbsolutePathSource() {
        return m_absolutePathSource;
    }

    /**
     * @param absolutePathSource the absolutePathSource to set
     */
    public final void setAbsolutePathSource(final String absolutePathSource) {
        m_absolutePathSource = absolutePathSource;
    }

    /**
     * @return the relativePathTarget
     */
    public final String getRelativePathTarget() {
        return m_relativePathTarget;
    }

    /**
     * @param relativePathTarget the relativePathTarget to set
     */
    public final void setRelativePathTarget(final String relativePathTarget) {
        m_relativePathTarget = relativePathTarget;
    }

    /** JSON serializer for JSONWebResource.  */
    public static class WebResourceToJSONStringSerializer extends JSONTypedSerializer<JSONWebResource> {

        /** Create new serializer. */
        protected WebResourceToJSONStringSerializer() {
            super(JSONWebResource.class);
        }

        /** {@inheritDoc} */
        @Override
        public final void serialize(final JSONWebResource value, final JsonGenerator jgen,
                final SerializerProvider provider) throws IOException {
            jgen.writeStringField("absolutePathSource", value.getAbsolutePathSource());
            jgen.writeStringField("relativePathTarget", value.getRelativePathTarget());
            jgen.writeEndObject();
        }
    }

    /** JSON deserializer for JSONWebResource. */
    @SuppressWarnings("serial")
    public static class JSONStringToWebResourceDeserializer extends JSONTypedDeserializer<JSONWebResource> {

        /** Create new deserializer. */
        protected JSONStringToWebResourceDeserializer() {
            super(JSONWebResource.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final JSONWebResource deserialize(final JsonParser jp, final DeserializationContext ctxt,
                final JSONWebResource intoValue) throws IOException {
            JsonToken token;
            while ((token = jp.nextToken()) != null) {
                if (JsonToken.VALUE_STRING == token) {
                    if (jp.getCurrentName().equals("absolutePathSource")) {
                        intoValue.setAbsolutePathSource(jp.getValueAsString());
                    }
                    if (jp.getCurrentName().equals("relativePathTarget")) {
                        intoValue.setRelativePathTarget(jp.getValueAsString());
                    }
                } else if (JsonToken.END_OBJECT == token) {
                    break;
                }
            }
            return intoValue;
        }
    }
}
