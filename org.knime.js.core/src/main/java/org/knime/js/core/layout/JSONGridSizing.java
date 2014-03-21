/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2014
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
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
 * Created on 17.03.2014 by Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
package org.knime.js.core.layout;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONGridSizing {

    private int m_height;
    private int m_minHeight;
    private int m_maxHeight;

    private int m_width;
    private int m_minWidth;
    private int m_maxWidth;

    private int m_paddingLeft;
    private int m_paddingRight;
    private int m_paddingTop;
    private int m_paddingBottom;

    /**
     * @return the height
     */
    public int getHeight() {
        return m_height;
    }
    /**
     * @param height the height to set
     */
    public void setHeight(final int height) {
        m_height = height;
    }
    /**
     * @return the minHeight
     */
    public int getMinHeight() {
        return m_minHeight;
    }
    /**
     * @param minHeight the minHeight to set
     */
    public void setMinHeight(final int minHeight) {
        m_minHeight = minHeight;
    }
    /**
     * @return the maxHeight
     */
    public int getMaxHeight() {
        return m_maxHeight;
    }
    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(final int maxHeight) {
        m_maxHeight = maxHeight;
    }
    /**
     * @return the width
     */
    public int getWidth() {
        return m_width;
    }
    /**
     * @param width the width to set
     */
    public void setWidth(final int width) {
        m_width = width;
    }
    /**
     * @return the minWidth
     */
    public int getMinWidth() {
        return m_minWidth;
    }
    /**
     * @param minWidth the minWidth to set
     */
    public void setMinWidth(final int minWidth) {
        m_minWidth = minWidth;
    }
    /**
     * @return the maxWidth
     */
    public int getMaxWidth() {
        return m_maxWidth;
    }
    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(final int maxWidth) {
        m_maxWidth = maxWidth;
    }
    /**
     * @return the paddingLeft
     */
    public int getPaddingLeft() {
        return m_paddingLeft;
    }
    /**
     * @param paddingLeft the paddingLeft to set
     */
    public void setPaddingLeft(final int paddingLeft) {
        m_paddingLeft = paddingLeft;
    }
    /**
     * @return the paddingRight
     */
    public int getPaddingRight() {
        return m_paddingRight;
    }
    /**
     * @param paddingRight the paddingRight to set
     */
    public void setPaddingRight(final int paddingRight) {
        m_paddingRight = paddingRight;
    }
    /**
     * @return the paddingTop
     */
    public int getPaddingTop() {
        return m_paddingTop;
    }
    /**
     * @param paddingTop the paddingTop to set
     */
    public void setPaddingTop(final int paddingTop) {
        m_paddingTop = paddingTop;
    }
    /**
     * @return the paddingBottom
     */
    public int getPaddingBottom() {
        return m_paddingBottom;
    }
    /**
     * @param paddingBottom the paddingBottom to set
     */
    public void setPaddingBottom(final int paddingBottom) {
        m_paddingBottom = paddingBottom;
    }

    /**
     * @param top the top padding
     * @param right the right padding
     * @param bottom the bottom padding
     * @param left the left padding
     */
    @JsonIgnore
    public void setPadding(final int top, final int right, final int bottom, final int left) {
        m_paddingTop = top;
        m_paddingRight = right;
        m_paddingBottom = bottom;
        m_paddingLeft = left;
    }

    /**
     * @param padding the padding on all sides
     */
    @JsonIgnore
    public void setPadding(final int padding) {
        m_paddingTop = padding;
        m_paddingRight = padding;
        m_paddingBottom = padding;
        m_paddingLeft = padding;
    }
}
