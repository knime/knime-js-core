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
 *   29.05.2015 (Alexander): created
 */
package org.knime.js.base.node.viz.plotter.roc;

import org.knime.base.node.viz.roc.ROCCurve;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.datasets.JSONDataset;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Alexander Fillbrunn
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSONROCCurve extends ROCCurve implements JSONDataset {

    /**
     * Copy constructor.
     * @param curve the base curve
     */
    public JSONROCCurve(final ROCCurve curve) {
        super(curve.getName(), curve.getX(), curve.getY(), curve.getArea(), curve.getMaxPoints());
    }

    /**
     * Creates a new ROC curve container. The data points may be downsampled by providing a maximum number
     * of points. This should speed up the view for curves with many data points
     *
     * @param name the curve's name
     * @param x the curve's x-values
     * @param y the curve's y-values
     * @param area the curve's area
     * @param maxPoints the maximum number of points to store when downsampling the curve; -1 disabled downsampling
     */
    public JSONROCCurve(final String name, final double[] x, final double[] y,
        final double area, final int maxPoints) {
        super(name, x, y, area, maxPoints);
    }

    /**
     * Only for deserializing from node settings.
     */
    public JSONROCCurve() {

    }

    /**
     * Config key for the area.
     */
    private static final String AREA_CFG = "area";

    /**
     * Config key for the name.
     */
    private static final String NAME_CFG = "name";

    /**
     * Config key for the y values.
     */
    private static final String Y_VALUES_CFG = "y_values";

    /**
     * Config key for the x values.
     */
    private static final String X_VALUES_CFG = "x_values";

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addDoubleArray(X_VALUES_CFG, getX());
        settings.addDoubleArray(Y_VALUES_CFG, getY());
        settings.addString(NAME_CFG, getName());
        settings.addDouble(AREA_CFG, getArea());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setX(settings.getDoubleArray(X_VALUES_CFG));
        setY(settings.getDoubleArray(Y_VALUES_CFG));
        setName(settings.getString(NAME_CFG));
        setArea(settings.getDouble(AREA_CFG));
    }
}
