/**
 *
 */
package org.knime.js.core.node.table;

import java.util.List;

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
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTable.Builder;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractWizardNodeModel;
import org.knime.js.core.settings.table.TableRepresentationSettings;
import org.knime.js.core.settings.table.TableSettings;
import org.knime.js.core.settings.table.TableValueSettings;

/**
 * Abstract table node model which implements the common functionality for all table-based nodes
 *
 * @param <REP> The concrete class of the {@link AbstractTableRepresentation} acting as representation of the view.
 * @param <VAL> The concrete class of the {@link AbstractTableValue} acting as value of the view.
 *
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 * @since 3.6
 *
 */
public abstract class AbstractTableNodeModel<REP extends AbstractTableRepresentation, VAL extends AbstractTableValue>
    extends AbstractWizardNodeModel<REP, VAL>
    implements BufferedDataTableHolder, CSSModifiable, LayoutTemplateProvider {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AbstractTableNodeModel.class);

    /**
     * Config object of the concrete view
     */
    protected final TableConfig m_config;

    /**
     * Data table to be displayed in the view
     */
    protected BufferedDataTable m_table;

    /**
     * @param viewName The name of the interactive view
     * @param config The config to set up
     */
    protected AbstractTableNodeModel(final String viewName, final TableConfig config) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE}, viewName);
        m_config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];
        TableRepresentationSettings settings = m_config.getSettings().getRepresentationSettings();
        if (settings.getEnableSelection() && !settings.getEnableLazyLoading()) {
            ColumnRearranger rearranger = createColumnAppender(tableSpec, null);
            tableSpec = rearranger.createSpec();
        }
        return new PortObjectSpec[]{tableSpec};
    }

    /**
     * Creates {@link ColumnRearranger} with a selection coming from the view.
     * @param spec
     * @param selectionList
     * @return corresponding {@link ColumnRearranger} object
     */
    protected ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_config.getSettings().getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = TableSettings.DEFAULT_SELECTION_COLUMN_NAME;
        }
        newColName = DataTableSpec.getUniqueColumnName(spec, newColName);
        DataColumnSpec outColumnSpec =
                new DataColumnSpecCreator(newColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new SingleCellFactory(outColumnSpec) {

            private int m_rowIndex = 0;

            @Override
            public DataCell getCell(final DataRow row) {
                if (++m_rowIndex > m_config.getSettings().getRepresentationSettings().getMaxRows()) {
                    return DataType.getMissingCell();
                }
                if (selectionList != null) {
                    if (selectionList.contains(row.getKey().toString())) {
                            /*return selectAll ? BooleanCell.FALSE : BooleanCell.TRUE;*/
                        return BooleanCell.TRUE;
                    } else {
                        return BooleanCell.FALSE;
                    }
                }
                /*return selectAll ? BooleanCell.TRUE : BooleanCell.FALSE;*/
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
    public void saveCurrentValue(final NodeSettingsWO content) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        REP rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getSettings().getTable() == null && m_table != null) {
                // set internal table
                try {
                    JSONDataTable jT = createJSONTableFromBufferedDataTable(m_table, null);
                    rep.getSettings().setTable(jT);
                } catch (Exception e) {
                    LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
                }
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getSettings().getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.getSettings().setHideInWizard(hide);
    }

    @Override
    public String getCssStyles() {
        return m_config.getSettings().getCustomCSS();
    }

    @Override
    public void setCssStyles(final String styles) {
        m_config.getSettings().setCustomCSS(styles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final VAL value) {
        // no validation done here
        return null;
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
     * Converts the data table into {@link JSONDataTable} format
     * @param table
     * @param exec
     * @return corresponding {@link JSONDataTable} object
     * @throws CanceledExecutionException
     */
    protected JSONDataTable createJSONTableFromBufferedDataTable(final BufferedDataTable table, final ExecutionContext exec) throws CanceledExecutionException {
        JSONDataTable jsonTable = getJsonDataTableBuilder(table).build(exec);
        TableRepresentationSettings repSettings = m_config.getSettings().getRepresentationSettings();
        int maxRows = repSettings.getMaxRows();
        if (!repSettings.getEnableLazyLoading() && maxRows < table.size()) {
            setWarningMessage("Only the first " + maxRows + " rows are displayed.");
        }
        return jsonTable;
    }

    /**
     * Gets a builder for the concrete view
     * @param table
     * @return corresponding builder object
     */
    protected JSONDataTable.Builder getJsonDataTableBuilder(final BufferedDataTable table) {
        TableRepresentationSettings repSettings = m_config.getSettings().getRepresentationSettings();
        TableValueSettings valSettings = m_config.getSettings().getValueSettings();
        Builder tableBuilder = JSONDataTable.newBuilder()
                .setDataTable(table)
                .setId(getTableId(0))
                .keepFilterColumns(true)
                .setExcludeColumns(this.determineExcludedColumns(table));
        if (repSettings.getEnableLazyLoading()) {
            //TODO: create filtered and sorted table?
            int page = Math.max(1, valSettings.getCurrentPage());
            int pageSize = valSettings.getPageSize();
            if (pageSize <= 0) {
                pageSize = repSettings.getInitialPageSize();
            }
            int firstRow = (page - 1) * pageSize + 1;
            tableBuilder.setFirstRow(firstRow)
                .setMaxRows(pageSize)
                .setPartialTableRows(table.size(), table.size());
        } else {
            tableBuilder.setFirstRow(1)
            .setMaxRows(repSettings.getMaxRows());
        }
        return tableBuilder;
    }

    /**
     * Get the list of excluded columns from the settings, for use in the JSONDataTable.Builder
     * @param table
     * @return list of excluded columns
     * @since 3.7
     */
    protected String[] determineExcludedColumns(final BufferedDataTable table) {
        FilterResult filter = m_config.getSettings().getColumnFilterConfig().applyTo(table.getDataTableSpec());
        return filter.getExcludes();
    }

    /**
     * Copies the settings from dialog into representation and values objects.
     */
    protected void copyConfigToRepresentation() {
        synchronized(getLock()) {
            REP viewRepresentation = getViewRepresentation();
            viewRepresentation.setSettingsFromDialog(m_config.getSettings().getRepresentationSettings());

            /* TODO temporary disable selection options when lazy loading is active */
            if (m_config.getSettings().getRepresentationSettings().getEnableLazyLoading()) {
                TableRepresentationSettings settings = viewRepresentation.getSettings();
                settings.setEnablePaging(true);
                settings.setPageSizeShowAll(false);
                settings.setEnableSelection(false);
                settings.setEnableHideUnselected(false);
            }

            VAL viewValue = getViewValue();
            if (isViewValueEmpty()) {
                viewValue.setSettings(m_config.getSettings().getValueSettings());
                viewValue.getSettings().setHideUnselected(m_config.getSettings().getValueSettings().getHideUnselected() && !m_config.getSettings().getRepresentationSettings().getSingleSelection());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        VAL viewValue = getViewValue();
        m_config.getSettings().setValueSettings(viewValue.getSettings());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     * @since 3.8
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent layout = new JSONLayoutViewContent();
        layout.setMinHeight(200);
        layout.setResizeMethod(ResizeMethod.VIEW_LOWEST_ELEMENT);
        return layout;
    }
}
