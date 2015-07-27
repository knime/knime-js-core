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
 *   Jun 12, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.input.molecule;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

/**
 * The config for the molecule string input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class MoleculeStringInputQuickFormConfig extends QuickFormFlowVariableConfig<MoleculeStringInputQuickFormValue> {

    private static final String CFG_FORMAT = "format";
    private static final String DEFAULT_FORMAT = MoleculeStringInputQuickFormNodeModel.DEFAULT_FORMATS[0];
    private String m_format = DEFAULT_FORMAT;
    private static final String CFG_GENERATE_IMAGE = "generateImage";
    private static final boolean DEFAULT_GENERATE = true;
    private boolean m_generateImage = DEFAULT_GENERATE;

    private static final String CFG_WIDTH = "width";
    private static final int DEFAULT_WIDTH = 600;
    private int m_width = DEFAULT_WIDTH;
    private static final String CFG_HEIGHT = "height";
    private static final int DEFAULT_HEIGHT = 400;
    private int m_height = DEFAULT_HEIGHT;

    /**
     * @return the format
     */
    String getFormat() {
        return m_format;
    }

    /**
     * @param format the format to set
     */
    void setFormat(final String format) {
        m_format = format;
    }

    /**
     * @return the generateImage
     */
    boolean getGenerateImage() {
        return m_generateImage;
    }

    /**
     * @param generateImage the generateImage to set
     */
    void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
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
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addBoolean(CFG_GENERATE_IMAGE, m_generateImage);
        settings.addString(CFG_FORMAT, m_format);
        settings.addInt(CFG_WIDTH, m_width);
        settings.addInt(CFG_HEIGHT, m_height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_format = settings.getString(CFG_FORMAT);

        //added with 2.12
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE);
        m_width = settings.getInt(CFG_WIDTH, DEFAULT_WIDTH);
        m_height = settings.getInt(CFG_HEIGHT, DEFAULT_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_format = settings.getString(CFG_FORMAT, DEFAULT_FORMAT);

        //added with 2.12
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE);
        m_width = settings.getInt(CFG_WIDTH, DEFAULT_WIDTH);
        m_height = settings.getInt(CFG_HEIGHT, DEFAULT_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeStringInputQuickFormValue createEmptyValue() {
        return new MoleculeStringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("format=");
        sb.append(m_format);
        sb.append(", ");
        sb.append("width=");
        sb.append(m_width);
        sb.append(", ");
        sb.append("height=");
        sb.append(m_height);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_generateImage)
                .append(m_format)
                .append(m_width)
                .append(m_height)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        MoleculeStringInputQuickFormConfig other = (MoleculeStringInputQuickFormConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_generateImage, other.m_generateImage)
                .append(m_format, other.m_format)
                .append(m_width, other.m_width)
                .append(m_height, other.m_height)
                .isEquals();
    }

}
