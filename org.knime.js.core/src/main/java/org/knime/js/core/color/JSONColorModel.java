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
 *   03.02.2017 (Oleg Yasnev): created
 */
package org.knime.js.core.color;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.data.DataCell;
import org.knime.core.data.property.ColorAttr;
import org.knime.core.data.property.ColorModel;
import org.knime.core.data.property.ColorModelNominal;
import org.knime.core.data.property.ColorModelRange;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.CSSUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Oleg Yasnev, KNIME.com GmbH, Berlin, Germany
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @Type(value = JSONColorModelNominal.class, name = "nominal"),
    @Type(value = JSONColorModelRange.class, name = "range")
    })
public abstract class JSONColorModel {
    private static final String CFG_TITLE = "title";
    private String m_title;

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        m_title = title;
    }

    /**
     * Creates a {@link JSONColorModel} instance from a {@link ColorModel}
     * @param model the model to create element from, may be null
     * @param title the title of the model to display
     * @return a new {@link JSONColorModel} with all settings representing the given {@link ColorModel}. Returns null when the model is null.
     * @throws IllegalArgumentException If concrete color model class is not supported by this method
     */
    @JsonIgnore
    public static final JSONColorModel createFromColorModel(final ColorModel model, final String title) throws IllegalArgumentException {
        if (model == null) {
            return null;
        }
        if (!(model instanceof ColorModelNominal || model instanceof ColorModelRange)) {
            throw new IllegalArgumentException("Color model class " + model.getClass().getName() + " not supported.");
        }
        JSONColorModel jsonModel = null;
        if (model instanceof ColorModelNominal) {
            jsonModel = new JSONColorModelNominal();
            jsonModel.setTitle(title);
            ColorModelNominal modelNominal = (ColorModelNominal) model;
            List<String> labels = new ArrayList<>();
            List<String> colors = new ArrayList<>();
            for (DataCell dc : modelNominal.getValues()) {
                labels.add(dc.toString());
                ColorAttr cAttr = modelNominal.getColorAttr(dc);
                colors.add(CSSUtils.cssHexStringFromColor(cAttr.getColor()));
            }
            ((JSONColorModelNominal)jsonModel).setLabels(labels.toArray(new String[labels.size()]));
            ((JSONColorModelNominal)jsonModel).setColors(colors.toArray(new String[colors.size()]));
        } else if (model instanceof ColorModelRange) {
            jsonModel = new JSONColorModelRange();
            jsonModel.setTitle(title);
            ColorModelRange modelRange = (ColorModelRange) model;
            double[] values = new double[2];
            values[0] = modelRange.getMinValue();
            values[1] = modelRange.getMaxValue();
            String[] colors = new String[2];
            colors[0] = CSSUtils.cssHexStringFromColor(modelRange.getMinColor());
            colors[1] = CSSUtils.cssHexStringFromColor(modelRange.getMaxColor());
            ((JSONColorModelRange)jsonModel).setRangeValues(values);
            ((JSONColorModelRange)jsonModel).setColors(colors);
        }

        return jsonModel;
    }

    /**
     * Creates a {@link JSONColorModel} instance from a {@link ColorModel} with default title value
     * @param model the model to create element from, may be null
     * @return a new {@link JSONColorModel} with all settings representing the given {@link ColorModel}. Returns null when the model is null.
     * @throws IllegalArgumentException If concrete color model class is not supported by this method
     */
    @JsonIgnore
    public static final JSONColorModel createFromColorModel(final ColorModel model) {
        return createFromColorModel(model, null);
    }

    /**
     * Saves the current state to the given settings object.
     * @param settings the settings to save to
     */
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_TITLE, m_title);
    }

    /**
     * Loads the configuration from the given settings object.
     * @param settings the settings to load from
     * @throws InvalidSettingsException on load error
     */
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setTitle(settings.getString(CFG_TITLE));
    }

    /**
     * Loads the configuration from the given settings object for a dialog.
     * @param settings the settings to load from
     */
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        setTitle(settings.getString(CFG_TITLE, null));
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
        JSONColorModel other = (JSONColorModel)obj;
        return new EqualsBuilder()
                .append(m_title, other.m_title)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_title)
                .toHashCode();
    }
}
