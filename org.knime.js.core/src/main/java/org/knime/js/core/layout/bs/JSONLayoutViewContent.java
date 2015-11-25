/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   Nov 12, 2015 (albrecht): created
 */
package org.knime.js.core.layout.bs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
*
* @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
*/
@JsonAutoDetect
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class JSONLayoutViewContent extends JSONLayoutElement implements JSONLayoutContent {

    // general fields
    private String m_nodeID;
    private Integer m_maxWidth;
    private Integer m_maxHeight;
    private Integer m_minWidth;
    private Integer m_minHeight;
    private ResizeMethod m_resizeMethod;

    // iframe resizer fields
    private boolean m_autoResize;
    private Integer m_resizeInterval;
    private boolean m_scrolling;
    private boolean m_sizeHeight;
    private boolean m_sizeWidth;
    private Integer m_resizeTolerance;

    public static enum ResizeMethod {
        /* Iframe Resizer methods */
        /** calculates offset height of body element */
        VIEW_BODY_OFFSET,
        /** uses document.body.scrollHeight */
        VIEW_BODY_SCROLL,
        /** uses document.documentElement.offsetHeight */
        VIEW_DOCUMENT_ELEMENT_OFFSET,
        /** uses document.documentElement.scrollHeight */
        VIEW_DOCUMENT_ELEMENT_SCROLL,
        /** largest value of the four main methods */
        VIEW_MAX,
        /** smallest value of the four main methods */
        VIEW_MIN,
        /** like VIEW_MAX but never shrinks */
        VIEW_GROW,
        /** lowest point of every DOM element present */
        VIEW_LOWEST_ELEMENT,
        /** lowest point of every element tagged with data-iframe-height attribute */
        VIEW_TAGGED_ELEMENT,
        /** like VIEW_LOWEST_ELEMENT with fall back to MAX on IE 10 or below */
        VIEW_LOWEST_ELEMENT_IE_MAX,

        /* Bootstrap responsive embed methods */
        /** Resize according to available space in layout and keeping a 16:9 aspect ratio. */
        ASPECT_RATIO_16by9,
        /** Resize according to available space in layout and keeping a 4:3 aspect ratio. */
        ASPECT_RATIO_4by3,

        /* Manual resize method */
        /** No automatic resizing. Resize calls need to be made manually from the view. */
        MANUAL;

        private static Map<String, ResizeMethod> namesMap = new HashMap<String, ResizeMethod>(13);

        static {
            namesMap.put("viewBodyOffset", VIEW_BODY_OFFSET);
            namesMap.put("viewBodyScroll", VIEW_BODY_SCROLL);
            namesMap.put("viewDocumentElementOffset", VIEW_DOCUMENT_ELEMENT_OFFSET);
            namesMap.put("viewDocumentElementScroll", VIEW_DOCUMENT_ELEMENT_SCROLL);
            namesMap.put("viewMax", VIEW_MAX);
            namesMap.put("viewMin", VIEW_MIN);
            namesMap.put("viewGrow", VIEW_GROW);
            namesMap.put("viewLowestElement", VIEW_LOWEST_ELEMENT);
            namesMap.put("viewTaggedElement", VIEW_TAGGED_ELEMENT);
            namesMap.put("viewLowestElementIEMax", ResizeMethod.VIEW_LOWEST_ELEMENT_IE_MAX);
            namesMap.put("aspectRatio16by9", ASPECT_RATIO_16by9);
            namesMap.put("aspectRatio4by3", ASPECT_RATIO_4by3);
            namesMap.put("manual", MANUAL);
        }

        @JsonCreator
        public static ResizeMethod forValue(final String value) {
            return namesMap.get(value);
        }

        @JsonValue
        public String toValue() {
            for (Entry<String, ResizeMethod> entry : namesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }

            return null;
        }
    }

    /**
     *
     */
    public JSONLayoutViewContent() {
        // set default resize method to lowest element and fall back to max on IE 10 and below
        m_resizeMethod = ResizeMethod.VIEW_LOWEST_ELEMENT_IE_MAX;

        // default boolean resize properties
        m_autoResize = true;
        m_scrolling = false;
        m_sizeHeight = true;
        m_sizeWidth = false;
    }

    /**
     * @return the nodeID
     */
    public String getNodeID() {
        return m_nodeID;
    }

    /**
     * @param nodeID the nodeID to set
     */
    public void setNodeID(final String nodeID) {
        m_nodeID = nodeID;
    }

    /**
     * @return the resizeMethod
     */
    public ResizeMethod getResizeMethod() {
        return m_resizeMethod;
    }

    /**
     * @param resizeMethod the resizeMethod to set
     */
    public void setResizeMethod(final ResizeMethod resizeMethod) {
        m_resizeMethod = resizeMethod;
    }

    /**
     * @return the maxHeight
     */
    public Integer getMaxHeight() {
        return m_maxHeight;
    }

    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(final Integer maxHeight) {
        this.m_maxHeight = maxHeight;
    }

    /**
     * @return the maxWidth
     */
    public Integer getMaxWidth() {
        return m_maxWidth;
    }

    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(final Integer maxWidth) {
        this.m_maxWidth = maxWidth;
    }

    /**
     * @return the minHeight
     */
    public Integer getMinHeight() {
        return m_minHeight;
    }

    /**
     * @param minHeight the minHeight to set
     */
    public void setMinHeight(final Integer minHeight) {
        this.m_minHeight = minHeight;
    }

    /**
     * @return the minWidth
     */
    public Integer getMinWidth() {
        return m_minWidth;
    }

    /**
     * @param minWidth the minWidth to set
     */
    public void setMinWidth(final Integer minWidth) {
        this.m_minWidth = minWidth;
    }

    /**
     * @return the resizeInterval
     */
    public Integer getResizeInterval() {
        return m_resizeInterval;
    }

    /**
     * @param resizeInterval the resizeInterval to set
     */
    public void setResizeInterval(final Integer resizeInterval) {
        m_resizeInterval = resizeInterval;
    }

    /**
     * @return the resizeTolerance
     */
    public Integer getResizeTolerance() {
        return m_resizeTolerance;
    }

    /**
     * @param resizeTolerance the resizeTolerance to set
     */
    public void setResizeTolerance(final Integer resizeTolerance) {
        m_resizeTolerance = resizeTolerance;
    }

    /**
     * @return the autoResize
     */
    public boolean getAutoResize() {
        return m_autoResize;
    }

    /**
     * @param autoResize the autoResize to set
     */
    public void setAutoResize(final boolean autoResize) {
        m_autoResize = autoResize;
    }

    /**
     * @return the scrolling
     */
    public boolean getScrolling() {
        return m_scrolling;
    }

    /**
     * @param scrolling the scrolling to set
     */
    public void setScrolling(final boolean scrolling) {
        m_scrolling = scrolling;
    }

    /**
     * @return the sizeHeight
     */
    public boolean getSizeHeight() {
        return m_sizeHeight;
    }

    /**
     * @param sizeHeight the sizeHeight to set
     */
    public void setSizeHeight(final boolean sizeHeight) {
        m_sizeHeight = sizeHeight;
    }

    /**
     * @return the sizeWidth
     */
    public boolean getSizeWidth() {
        return m_sizeWidth;
    }

    /**
     * @param sizeWidth the sizeWidth to set
     */
    public void setSizeWidth(final boolean sizeWidth) {
        m_sizeWidth = sizeWidth;
    }

}
