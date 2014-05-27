/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   13.05.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.scatter;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;
import org.knime.js.core.datasets.JSONKeyedValues3DDataset;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ScatterPlotViewRepresentation extends JSONViewContent {

    private JSONKeyedValues3DDataset m_keyedDataset;
    private boolean m_allowViewConfiguration;
    private boolean m_enableXColumnChange;
    private boolean m_enableYColumnChange;
    private boolean m_enableXAxisLabelEdit;
    private boolean m_enableYAxisLabelEdit;
    private boolean m_allowDotSizeChange;
    private boolean m_allowZooming;
    private boolean m_allowPanning;


    /**
     * @return the keyedDataset
     */
    public JSONKeyedValues3DDataset getKeyedDataset() {
        return m_keyedDataset;
    }

    /**
     * @param keyedDataset the keyedDataset to set
     */
    public void setKeyedDataset(final JSONKeyedValues3DDataset keyedDataset) {
        m_keyedDataset = keyedDataset;
    }

    /**
     * @return the allowViewConfiguration
     */
    public boolean getAllowViewConfiguration() {
        return m_allowViewConfiguration;
    }

    /**
     * @param allowViewConfiguration the allowViewConfiguration to set
     */
    public void setAllowViewConfiguration(final boolean allowViewConfiguration) {
        m_allowViewConfiguration = allowViewConfiguration;
    }

    /**
     * @return the enableXColumnChange
     */
    public boolean getEnableXColumnChange() {
        return m_enableXColumnChange;
    }

    /**
     * @param enableXColumnChange the enableXColumnChange to set
     */
    public void setEnableXColumnChange(final boolean enableXColumnChange) {
        m_enableXColumnChange = enableXColumnChange;
    }

    /**
     * @return the enableYColumnChange
     */
    public boolean getEnableYColumnChange() {
        return m_enableYColumnChange;
    }

    /**
     * @param enableYColumnChange the enableYColumnChange to set
     */
    public void setEnableYColumnChange(final boolean enableYColumnChange) {
        m_enableYColumnChange = enableYColumnChange;
    }

    /**
     * @return the enableXAxisLabelEdit
     */
    public boolean getEnableXAxisLabelEdit() {
        return m_enableXAxisLabelEdit;
    }

    /**
     * @param enableXAxisLabelEdit the enableXAxisLabelEdit to set
     */
    public void setEnableXAxisLabelEdit(final boolean enableXAxisLabelEdit) {
        m_enableXAxisLabelEdit = enableXAxisLabelEdit;
    }

    /**
     * @return the enableYAxisLabelEdit
     */
    public boolean getEnableYAxisLabelEdit() {
        return m_enableYAxisLabelEdit;
    }

    /**
     * @param enableYAxisLabelEdit the enableYAxisLabelEdit to set
     */
    public void setEnableYAxisLabelEdit(final boolean enableYAxisLabelEdit) {
        m_enableYAxisLabelEdit = enableYAxisLabelEdit;
    }

    /**
     * @return the allowDotSizeChange
     */
    public boolean getAllowDotSizeChange() {
        return m_allowDotSizeChange;
    }

    /**
     * @param allowDotSizeChange the allowDotSizeChange to set
     */
    public void setAllowDotSizeChange(final boolean allowDotSizeChange) {
        m_allowDotSizeChange = allowDotSizeChange;
    }

    /**
     * @return the allowZooming
     */
    public boolean getAllowZooming() {
        return m_allowZooming;
    }

    /**
     * @param allowZooming the allowZooming to set
     */
    public void setAllowZooming(final boolean allowZooming) {
        m_allowZooming = allowZooming;
    }

    /**
     * @return the allowPanning
     */
    public boolean getAllowPanning() {
        return m_allowPanning;
    }

    /**
     * @param allowPanning the allowPanning to set
     */
    public void setAllowPanning(final boolean allowPanning) {
        m_allowPanning = allowPanning;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(ScatterPlotViewConfig.ALLOW_CONFIG, getAllowViewConfiguration());
        settings.addBoolean(ScatterPlotViewConfig.ENABLE_X_COL_CHANGE, getEnableXColumnChange());
        settings.addBoolean(ScatterPlotViewConfig.ENABLE_Y_COL_CHANGE, getEnableYColumnChange());
        settings.addBoolean(ScatterPlotViewConfig.ENABLE_X_LABEL_EDIT, getEnableXAxisLabelEdit());
        settings.addBoolean(ScatterPlotViewConfig.ENABLE_Y_LABEL_EDIT, getEnableYAxisLabelEdit());
        settings.addBoolean(ScatterPlotViewConfig.ALLOW_DOT_SIZE_CHANGE, getAllowDotSizeChange());
        settings.addBoolean(ScatterPlotViewConfig.ALLOW_ZOOMING, getAllowZooming());
        settings.addBoolean(ScatterPlotViewConfig.ALLOW_PANNING, getAllowPanning());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setAllowViewConfiguration(settings.getBoolean(ScatterPlotViewConfig.ALLOW_CONFIG));
        setEnableXColumnChange(settings.getBoolean(ScatterPlotViewConfig.ENABLE_X_COL_CHANGE));
        setEnableYColumnChange(settings.getBoolean(ScatterPlotViewConfig.ENABLE_Y_COL_CHANGE));
        setEnableXAxisLabelEdit(settings.getBoolean(ScatterPlotViewConfig.ENABLE_X_LABEL_EDIT));
        setEnableYAxisLabelEdit(settings.getBoolean(ScatterPlotViewConfig.ENABLE_Y_LABEL_EDIT));
        setAllowDotSizeChange(settings.getBoolean(ScatterPlotViewConfig.ALLOW_DOT_SIZE_CHANGE));
        setAllowZooming(settings.getBoolean(ScatterPlotViewConfig.ALLOW_ZOOMING));
        setAllowPanning(settings.getBoolean(ScatterPlotViewConfig.ALLOW_PANNING));
    }
}
