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
 *   14.04.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.util.table;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JavaScriptViewCreator;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 * @param <REP>
 * @param <VAL>
 */
public abstract class WebTableNodeModel<REP extends WebTableViewRepresentation, VAL extends WebTableViewValue>
        extends NodeModel implements WizardNode<REP, VAL>, BufferedDataTableHolder {

    /** Config key for the last displayed row. */
    public static final String CFG_END = "end";
    /** Config key if number formatter used. */
    public static final String CFG_USE_NUMBER_FORMATTER = "useNumberFormatter";
    /** Config key for the number of decimal places. */
    public static final String CFG_DECIMAL_PLACES = "decimalPlaces";
    /** Config key for the decimal separator sign. */
    public static final String CFG_DECIMAL_SEPARATOR = "decimalSeparator";
    /** Config key for the thousands separator sign. */
    public static final String CFG_THOUSANDS_SEPARATOR = "thousandsSeparator";
    /** Default end row for table creation. */
    public static final int END = 2500;

    private static final NodeLogger LOGGER = NodeLogger.getLogger(WebTableNodeModel.class);

    private BufferedDataTable m_table;
    private JSONDataTable m_jsonTable;
    private REP m_viewRepresentation;
    private VAL m_viewValue;

    private final SettingsModelIntegerBounded m_maxRows = createLastDisplayedRowModel(END);
    private final SettingsModelBoolean m_useNumberFormatter = createUseNumberFormatterModel();
    private final SettingsModelIntegerBounded m_decimalPlaces = createDecimalPlacesModel(m_useNumberFormatter);
    private String m_viewPath;
//    private final SettingsModelString m_decimalSeparator = createDecimalSeparatorModel();
//    private final SettingsModelString m_thousandsSeparator = createThousandsSeparatorModel();


    /**
     * Creates a new model with the given number (and types!) of input and
     * output types.
     * @param inPortTypes an array of non-null in-port types
     * @param outPortTypes an array of non-null out-port types
     */
    protected WebTableNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
        m_viewRepresentation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
    }

    /** @param end The last row index to display.
     * @return settings model for the max row count property.
     * */
    static SettingsModelIntegerBounded createLastDisplayedRowModel(
            final int end) {
        return new SettingsModelIntegerBounded(
                CFG_END, end, 1, Integer.MAX_VALUE);
    }

    /** @return settings model for the use number formatter property. */
    static SettingsModelBoolean createUseNumberFormatterModel() {
        return new SettingsModelBoolean(CFG_USE_NUMBER_FORMATTER, false);
    }

    /** @param useNumberFormatter for enable/disablement
     * @return settings model for the decimal places property. */
    static SettingsModelIntegerBounded createDecimalPlacesModel(final SettingsModelBoolean useNumberFormatter) {
        final SettingsModelIntegerBounded result =
                new SettingsModelIntegerBounded(CFG_DECIMAL_PLACES, 2, 0, Integer.MAX_VALUE);
        useNumberFormatter.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                result.setEnabled(useNumberFormatter.getBooleanValue());
            }
        });
        result.setEnabled(useNumberFormatter.getBooleanValue());
        return result;
    }

    /** @return settings model for the decimal separator property. */
    static SettingsModelString createDecimalSeparatorModel() {
        @SuppressWarnings("static-access")
        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getDecimalSeparator();
        return new SettingsModelString(CFG_DECIMAL_SEPARATOR, String.valueOf(sep));
    }

    /** @return settings model for the thousands separator property. */
    static SettingsModelString createThousandsSeparatorModel() {
        @SuppressWarnings("static-access")
        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getGroupingSeparator();
        return new SettingsModelString(CFG_THOUSANDS_SEPARATOR, String.valueOf(sep));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        // TODO Auto-generated method stub
        return new PortObjectSpec[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_table = (BufferedDataTable)inObjects[0];
        createJSONTableFromBufferedDataTable(exec);
        m_viewRepresentation.setTable(m_jsonTable);
        setNumberFormatter();
        return new PortObject[0];
    }

    private void createJSONTableFromBufferedDataTable(final ExecutionContext exec) throws CanceledExecutionException {
        m_jsonTable = new JSONDataTable(m_table, 1, m_maxRows.getIntValue(), exec);
        if (m_maxRows.getIntValue() < m_table.getRowCount()) {
            setWarningMessage("Only the first "
                    + m_maxRows.getIntValue() + " rows are displayed.");
        }
    }

    private void setNumberFormatter() {
        if (m_useNumberFormatter.getBooleanValue()) {
            int decimalPlaces = m_decimalPlaces.getIntValue();
//            String decimalSeparator = m_decimalSeparator.getStringValue();
//            String thousandsSeparator = m_thousandsSeparator.getStringValue();
//            JSONNumberFormatter formatter =
//                new JSONNumberFormatter(decimalPlaces, decimalSeparator, thousandsSeparator);
            JSONNumberFormatter formatter = new JSONNumberFormatter(decimalPlaces, ".", ",");
            m_viewRepresentation.setNumberFormatter(formatter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        if (m_viewRepresentation == null) {
            m_viewRepresentation = createEmptyViewRepresentation();
        }
        if (m_table != null && m_jsonTable == null) {
            try {
                createJSONTableFromBufferedDataTable(null);
                m_viewRepresentation.setTable(m_jsonTable);
            } catch (Exception e) {
                LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
            }
        }
        return m_viewRepresentation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getViewValue() {
        if (m_viewValue == null) {
            m_viewValue = createEmptyViewValue();
        }
        return m_viewValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final WebTableViewValue viewContent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewContent, final boolean useAsDefault) {
        m_viewValue = viewContent;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public REP createEmptyViewRepresentation() {
        try {
            return ((Class<REP>)((ParameterizedType)this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0]).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public VAL createEmptyViewValue() {
        try {
            return ((Class<VAL>)((ParameterizedType)this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[1]).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // Don't save table automatically. Lazy init on first use.
        /*NodeSettings settings = new NodeSettings("table");
        m_jsonTable.saveJSONToNodeSettings(settings);
        File f = new File(nodeInternDir, "jsonTable.xml");
        settings.saveToXML(new FileOutputStream(f));*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // Don't load table automatically. Lazy init on first use.
        /*File f = new File(nodeInternDir, "jsonTable.xml");
        NodeSettingsRO settings = NodeSettings.loadFromXML(new FileInputStream(f));
        m_jsonTable = JSONDataTable.loadFromNodeSettings(settings);
        m_viewRepresentation.setTable(m_jsonTable);*/
        setNumberFormatter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_maxRows.saveSettingsTo(settings);
        m_useNumberFormatter.saveSettingsTo(settings);
        m_decimalPlaces.saveSettingsTo(settings);
//        m_decimalSeparator.saveSettingsTo(settings);
//        m_thousandsSeparator.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_maxRows.validateSettings(settings);
        m_useNumberFormatter.validateSettings(settings);
        m_decimalPlaces.validateSettings(settings);
//        SettingsModelString tempDecimalSeparator =
//            (SettingsModelString)m_decimalSeparator.createCloneWithValidatedValue(settings);
//        SettingsModelString tempThousandSeparator =
//            (SettingsModelString)m_thousandsSeparator.createCloneWithValidatedValue(settings);
//        if (Objects.equals(tempDecimalSeparator.getStringValue(), tempThousandSeparator.getStringValue())) {
//            throw new InvalidSettingsException(
//                "Decimal separator and thousands separator cannot be assigned to the same string.");
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_maxRows.loadSettingsFrom(settings);
        m_useNumberFormatter.loadSettingsFrom(settings);
        m_decimalPlaces.loadSettingsFrom(settings);
//        m_decimalSeparator.loadSettingsFrom(settings);
//        m_thousandsSeparator.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_table = null;
        m_jsonTable = null;
        m_viewRepresentation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
    }

    /**
     * @return the last row index
     */
    public int getEndIndex() {
        return m_maxRows.getIntValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[]{m_table};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        m_table = tables[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewHTMLPath() {
        if (m_viewPath == null || m_viewPath.isEmpty()) {
            // view is not created
            m_viewPath = createViewPath();
        } else {
            // check if file still exists, create otherwise
            File viewFile = new File(m_viewPath);
            if (!viewFile.exists()) {
                m_viewPath = createViewPath();
            }
        }
        return m_viewPath;
    }

    private String createViewPath() {
        JavaScriptViewCreator<WebTableNodeModel<WebTableViewRepresentation, WebTableViewValue>, WebTableViewRepresentation, WebTableViewValue> viewCreator =
            new JavaScriptViewCreator<WebTableNodeModel<WebTableViewRepresentation, WebTableViewValue>, WebTableViewRepresentation, WebTableViewValue>(
                getJavascriptObjectID());
        try {
            return viewCreator.createWebResources("View", getViewRepresentation(), getViewValue());
        } catch (IOException e) {
            return null;
        }
    }


}
