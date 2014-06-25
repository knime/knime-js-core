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
 * ---------------------------------------------------------------------
 *
 * Created on 28.01.2014 by Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
package org.knime.js.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONWebNode {

    private List<String> m_javascriptLibraries;
    private List<String> m_stylesheets;
    private String m_namespace;
    private String m_initMethodName;
    private String m_validateMethodName;
    private String m_setValidationErrorMethodName;
    private String m_getViewValueMethodName;

    private JSONViewContent m_viewRepresentation;
    private JSONViewContent m_viewValue;

    /**
     * @return the javascript libraries
     */
    @JsonProperty("javascriptLibraries")
    public List<String> getJavascriptLibraries() {
        return m_javascriptLibraries;
    }

    /**
     * @param javascriptLibraries
     */
    @JsonProperty("javascriptLibraries")
    public void setJavascriptLibraries(final List<String> javascriptLibraries) {
        m_javascriptLibraries = javascriptLibraries;
    }

    /**
     * @return the stylesheets
     */
    @JsonProperty("stylesheets")
    public List<String> getStylesheets() {
        return m_stylesheets;
    }

    /**
     * @param stylesheets
     */
    @JsonProperty("stylesheets")
    public void setStylesheets(final List<String> stylesheets) {
        m_stylesheets = stylesheets;
    }

    /**
     * @return the namespace
     */
    @JsonProperty("namespace")
    public String getNamespace() {
        return m_namespace;
    }

    /**
     * @param namespace
     */
    @JsonProperty("namespace")
    public void setNamespace(final String namespace) {
        m_namespace = namespace;
    }

    /**
     * @return the init method name
     */
    @JsonProperty("initMethodName")
    public String getInitMethodName() {
        return m_initMethodName;
    }

    /**
     * @param initMethodName
     */
    @JsonProperty("initMethodName")
    public void setInitMethodName(final String initMethodName) {
        m_initMethodName = initMethodName;
    }

    /**
     * @return the validate method name
     */
    @JsonProperty("validateMethodName")
    public String getValidateMethodName() {
        return m_validateMethodName;
    }

    /**
     * @param validateMethodName
     */
    @JsonProperty("validateMethodName")
    public void setValidateMethodName(final String validateMethodName) {
        m_validateMethodName = validateMethodName;
    }

    /**
     * @return the setValidationErrorMethodName
     */
    @JsonProperty("setValidationErrorMethodName")
    public String getSetValidationErrorMethodName() {
        return m_setValidationErrorMethodName;
    }

    /**
     * @param setValidationErrorMethodName the setValidationErrorMethodName to set
     */
    @JsonProperty("setValidationErrorMethodName")
    public void setSetValidationErrorMethodName(final String setValidationErrorMethodName) {
        m_setValidationErrorMethodName = setValidationErrorMethodName;
    }

    /**
     * @return the get view value method name
     */
    @JsonProperty("getViewValueMethodName")
    public String getGetViewValueMethodName() {
        return m_getViewValueMethodName;
    }

    /**
     * @param getViewValueMethodName
     */
    @JsonProperty("getViewValueMethodName")
    public void setGetViewValueMethodName(final String getViewValueMethodName) {
        m_getViewValueMethodName = getViewValueMethodName;
    }

    /**
     * @return the view representation
     */
    @JsonProperty("viewRepresentation")
    public JSONViewContent getViewRepresentation() {
        return m_viewRepresentation;
    }

    /**
     * @param viewRepresentation
     */
    @JsonProperty("viewRepresentation")
    public void setViewRepresentation(final JSONViewContent viewRepresentation) {
        m_viewRepresentation = viewRepresentation;
    }

    /**
     * @return the view value
     */
    @JsonProperty("viewValue")
    public JSONViewContent getViewValue() {
        return m_viewValue;
    }

    /**
     * @param viewValue
     */
    @JsonProperty("viewValue")
    public void setViewValue(final JSONViewContent viewValue) {
        m_viewValue = viewValue;
    }

}
