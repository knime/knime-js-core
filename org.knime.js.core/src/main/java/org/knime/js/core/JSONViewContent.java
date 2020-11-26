/*
 * ------------------------------------------------------------------------
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
 * Created on 08.05.2013 by Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
package org.knime.js.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.knime.core.node.web.WebViewContent;
import org.knime.core.util.CoreConstants;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * ViewContent that creates and reads from a JSON string.
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 2.9
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class JSONViewContent implements WebViewContent {

    // since 4.3
    private boolean hasArtifactsView = false;

    /**
     * {@inheritDoc}
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    @JsonIgnore
    public final void loadFromStream(final InputStream viewContentStream) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        ObjectReader reader = this.getHasArtifactsView() ?
            mapper.readerWithView(CoreConstants.ArtifactsView.class).withValueToUpdate(this) :
            mapper.readerForUpdating(this);
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            reader.readValue(viewContentStream);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return An {@link OutputStream} containing the JSON string in UTF-8 format.
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    @Override
    @JsonIgnore
    public final OutputStream saveToStream() throws IOException {
        ObjectMapper mapper = createObjectMapper();
        ObjectWriter writer = this.getHasArtifactsView() ?
            mapper.writerWithView(CoreConstants.ArtifactsView.class) :
            mapper.writer();
        String viewContentString = writer.writeValueAsString(this);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(viewContentString.getBytes(Charset.forName("UTF-8")));
        out.flush();
        return out;
    }

    /**
     * @return the object mapper used for de-/serialization of {@link JSONViewContent}-objects.
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Default implementation of if the implementation contains JSON content which should be de-/serialized
     * using the {@link org.knime.core.util.CoreConstants.ArtifactsView} {@code JSONView} annotation.
     *
     * @return if the class has ArtifactsView fields.
     * @since 4.3
     */
    protected boolean getHasArtifactsView() {
        return hasArtifactsView;
    }

    //Force equals and hashCode

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();

}
