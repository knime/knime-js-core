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

import java.lang.reflect.ParameterizedType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 * @param <REP>
 * @param <VAL>
 */
public abstract class WebTableNodeModel<REP extends WebTableViewRepresentation, VAL extends WebTableViewValue>
        extends AbstractWizardNodeModel<REP, VAL> implements BufferedDataTableHolder {

    /** Config key for hide in wizard. */
    public static final String CFG_HIDE_IN_WIZARD = "hideInWizard";
    /** Config key for the last displayed row. */
    public static final String CFG_END = "end";
    /** Config key if number formatter used. */
    public static final String CFG_USE_NUMBER_FORMATTER = "useNumberFormatter";
    /** Config key if selection is enabled. */
    public static final String CFG_ENABLE_SELECTION = "enableSelection";
    /** Config key for selection column name. */
    public static final String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    /** Config key for enlarging the frame to fit the entire contents of the table. */
    public static final String CFG_FULL_FRAME = "fullFrame";
    /** Config key for the number of decimal places. */
    public static final String CFG_DECIMAL_PLACES = "decimalPlaces";
    /** Config key for the decimal separator sign. */
    public static final String CFG_DECIMAL_SEPARATOR = "decimalSeparator";
    /** Config key for the thousands separator sign. */
    public static final String CFG_THOUSANDS_SEPARATOR = "thousandsSeparator";
    /** Default end row for table creation. */
    public static final int END = 2500;
    /** Default selection column name. */
    public static final String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Table View)";

    private static final NodeLogger LOGGER = NodeLogger.getLogger(WebTableNodeModel.class);

    private BufferedDataTable m_table;
    private JSONDataTable m_jsonTable;

    private final SettingsModelBoolean m_hideInWizard = createHideInWizardModel();
    private final SettingsModelIntegerBounded m_maxRows = createLastDisplayedRowModel(END);
    private final SettingsModelBoolean m_useNumberFormatter = createUseNumberFormatterModel();
    private final SettingsModelIntegerBounded m_decimalPlaces = createDecimalPlacesModel(m_useNumberFormatter);
    private final SettingsModelBoolean m_enableSelection = createEnableSelectionModel();
    private final SettingsModelString m_selectionColumnName = createSelectionColumnNameModel();
    private final SettingsModelBoolean m_fullFrame = createFullFrameModel();
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
    }

    /** @return Settings model for the hide in wizard property. */
    static SettingsModelBoolean createHideInWizardModel() {
        return new SettingsModelBoolean(CFG_HIDE_IN_WIZARD, false);
    }

    /** @param end The last row index to display.
     * @return Settings model for the max row count property.
     * */
    static SettingsModelIntegerBounded createLastDisplayedRowModel(
            final int end) {
        return new SettingsModelIntegerBounded(
                CFG_END, end, 1, Integer.MAX_VALUE);
    }

    /** @return Settings model for the use number formatter property. */
    static SettingsModelBoolean createUseNumberFormatterModel() {
        return new SettingsModelBoolean(CFG_USE_NUMBER_FORMATTER, false);
    }

    /** @return Settings model for the selection enabled property. */
    static SettingsModelBoolean createEnableSelectionModel() {
        return new SettingsModelBoolean(CFG_ENABLE_SELECTION, true);
    }

    static SettingsModelString createSelectionColumnNameModel() {
        return new SettingsModelString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
    }

    /** @return Settings model for the hide in wizard property. */
    static SettingsModelBoolean createFullFrameModel() {
        return new SettingsModelBoolean(CFG_FULL_FRAME, false);
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
        DecimalFormat format = (DecimalFormat)NumberFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getDecimalSeparator();
        return new SettingsModelString(CFG_DECIMAL_SEPARATOR, String.valueOf(sep));
    }

    /** @return settings model for the thousands separator property. */
    static SettingsModelString createThousandsSeparatorModel() {
        @SuppressWarnings("static-access")
        DecimalFormat format = (DecimalFormat)NumberFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getGroupingSeparator();
        return new SettingsModelString(CFG_THOUSANDS_SEPARATOR, String.valueOf(sep));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {

        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];
        if (m_enableSelection.getBooleanValue()) {
            ColumnRearranger rearranger = createColumnAppender(tableSpec, null);
            tableSpec = rearranger.createSpec();
        }

        return new PortObjectSpec[]{tableSpec};
    }

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_selectionColumnName.getStringValue();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = DEFAULT_SELECTION_COLUMN_NAME;
        }
        newColName = DataTableSpec.getUniqueColumnName(spec, newColName);
        DataColumnSpec outColumnSpec =
                new DataColumnSpecCreator(newColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new SingleCellFactory(outColumnSpec) {

            private int m_rowIndex = 0;

            @Override
            public DataCell getCell(final DataRow row) {
                if (++m_rowIndex > m_maxRows.getIntValue()) {
                    return DataType.getMissingCell();
                }
                if (selectionList != null) {
                    if (selectionList.contains(row.getKey().toString())) {
                            return BooleanCell.TRUE;
                    }
                }
                return BooleanCell.FALSE;
            }
        };
        rearranger.append(fac);
        return rearranger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out = (BufferedDataTable)inObjects[0];
        synchronized (getLock()) {
            REP viewRepresentation = getViewRepresentation();
            if (viewRepresentation.getTable() == null) {
                m_table = (BufferedDataTable)inObjects[0];
                createJSONTableFromBufferedDataTable(exec.createSubExecutionContext(0.5));
                viewRepresentation.setTable(m_jsonTable);
                setNumberFormatter();
                viewRepresentation.setEnableSelection(m_enableSelection.getBooleanValue());
                viewRepresentation.setFullFrame(m_fullFrame.getBooleanValue());
            }

            if (m_enableSelection.getBooleanValue()) {
                VAL viewValue = getViewValue();
                List<String> selectionList = null;
                if (viewValue != null && viewValue.getSelection() != null) {
                    selectionList = Arrays.asList(viewValue.getSelection());
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec.createSubExecutionContext(0.5));
            }
        }
        exec.setProgress(1);
        return new PortObject[]{out};
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
            getViewRepresentation().setNumberFormatter(formatter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        REP viewRepresentation = super.getViewRepresentation();
        synchronized (getLock()) {
            // set internal table
            if (m_table != null && m_jsonTable == null) {
                try {
                    createJSONTableFromBufferedDataTable(null);
                    viewRepresentation.setTable(m_jsonTable);
                    setNumberFormatter();
                    viewRepresentation.setEnableSelection(m_enableSelection.getBooleanValue());
                    viewRepresentation.setFullFrame(m_fullFrame.getBooleanValue());
                } catch (Exception e) {
                    LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
                }
            }
        }
        return viewRepresentation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final WebTableViewValue viewContent) {
        // nothing to do?
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_hideInWizard.getBooleanValue();
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
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_hideInWizard.saveSettingsTo(settings);
        m_maxRows.saveSettingsTo(settings);
        m_fullFrame.saveSettingsTo(settings);
        m_useNumberFormatter.saveSettingsTo(settings);
        m_decimalPlaces.saveSettingsTo(settings);
        m_enableSelection.saveSettingsTo(settings);
        m_selectionColumnName.saveSettingsTo(settings);
//        m_decimalSeparator.saveSettingsTo(settings);
//        m_thousandsSeparator.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard.validateSettings(settings);
        m_maxRows.validateSettings(settings);
        m_useNumberFormatter.validateSettings(settings);
        m_decimalPlaces.validateSettings(settings);
        m_enableSelection.validateSettings(settings);
        m_selectionColumnName.validateSettings(settings);
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
        m_hideInWizard.loadSettingsFrom(settings);
        m_maxRows.loadSettingsFrom(settings);
        m_useNumberFormatter.loadSettingsFrom(settings);
        m_decimalPlaces.loadSettingsFrom(settings);
        m_enableSelection.loadSettingsFrom(settings);
        m_selectionColumnName.loadSettingsFrom(settings);

        //added in 2.12
        boolean fullFrame = settings.getBoolean(CFG_FULL_FRAME, false);
        m_fullFrame.loadSettingsFrom(settings);
//        m_decimalSeparator.loadSettingsFrom(settings);
//        m_thousandsSeparator.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
        m_jsonTable = null;
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

}
