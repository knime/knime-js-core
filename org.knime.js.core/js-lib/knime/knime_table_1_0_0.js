// KNIME Javascript Data Table Version 1.0.0
window.kt = function () {
    var kt = {
        version: '1.0.0'
    };

    var dataTable = {};
    
    var _getDataColumnID = function (columnName) {
        var colID = null;
        for (var i = 0; i < dataTable.spec.numColumns; i++) {
            if (dataTable.spec.colNames[i] === columnName) {
                colID = i;
                break;
            }
        }
        return colID;
    };

    var _getRowID = function (rowKey) {
        var rowID = null;
        for (var i = 0; i < dataTable.spec.numRows; i++) {
            if (dataTable.rows[i].rowKey === rowKey) {
                rowID = i;
                break;
            }
        }
        return rowID;
    };

    kt.setDataTableFromJSON = function (jsonTable) {
        dataTable = JSON.parse(jsonTable);
    };

    kt.setDataTable = function (table) {
        dataTable = table;
    };

    kt.setDataTableSpecFromJSON = function (jsonTableSpec) {
        dataTable.spec = JSON.parse(jsonTableSpec);
    };

    kt.setDataTableSpec = function (dataTableSpec) {
        dataTable.spec = dataTableSpec;
    };
    
    kt.isFragment = function () {
        return dataTable.fragment;
    };

    kt.isFiltered = function () {
        return dataTable.filtered;
    };

    kt.getFragmentFirstRowIndex = function () {
        return dataTable.fragmentFirstRowIndex;
    };

    kt.getRows = function () {
        return dataTable.rows;
    };

    kt.getRow = function (rowID) {
        var id = rowID;
        if (typeof rowID === 'string') {
            id = _getRowID(rowID);
        }
        if (id !== null && id >= 0 && id < kt.getNumRows()) {
            return dataTable.rows[id];
        }
        return null;
    };

    kt.getCell = function (rowID, columnID) {
        var colIndex = columnID;
        if (typeof columnID === 'string') {
            colIndex = _getDataColumnID(columnID);
        }
        if (colIndex !== null && colIndex < dataTable.spec.numColumns) {
            var rowIndex = rowID;
            if (typeof rowIndex === 'string') {
                rowIndex = _getRowID(rowID);
            }
            return dataTable.rows[rowIndex].data[colIndex];
        }
        return null;
    };

    kt.getColumn = function (columnID) {
        var id = columnID;
        if (typeof columnID === 'string') {
            id = _getDataColumnID(columnID);
        }
        if (id !== null && id < dataTable.spec.numColumns) {
            var col = [];

            for (var i = 0; i < kt.getNumRows(); i++) {
                col.push(dataTable.rows[i].data[id]);
            }
            return col;
        }
        return null;
    };

    kt.getColumnNames = function () {
        return dataTable.spec.colNames;
    };

    kt.isColumnHidden = function (index) {
        var columnName = this.getColumnNames()[index];
        var hiddenColumnNames = this.getHiddenColumnNames();
        return hiddenColumnNames.indexOf(columnName) > -1;
    };

    kt.getHiddenColumnNames = function () {
        return dataTable.spec.hiddenColumns || [];
    };

    kt.getColumnTypes = function () {
        return dataTable.spec.colTypes;
    };

    kt.getNumColumns = function () {
        return dataTable.spec.numColumns;
    };

    kt.getNumRows = function () {
        return dataTable.spec.numRows;
    };

    kt.getTotalRowCount = function () {
        return dataTable.totalRows;
    };

    kt.getTotalFilteredRowCount = function () {
        return dataTable.totalFilteredRows;
    };

    kt.getKnimeColumnTypes = function () {
        return dataTable.spec.knimeTypes;
    };

    kt.getPossibleValues = function (columnName) {
        if (columnName) {
            var colID = _getDataColumnID(columnName);
            if (colID && colID < dataTable.spec.numColumns) {
                return dataTable.spec.possibleValues[colID];
            } else {
                return null;
            }
        } else {
            return dataTable.spec.possibleValues;
        }
    };

    kt.getRowColors = function () {
        return dataTable.spec.rowColorValues;
    };

    kt.getTableId = function () {
        return dataTable.id;
    };

    kt.getFilterIds = function () {
        var filters = [];
        var f = dataTable.spec.filterIds;
        for (var i = 0; i < f.length; i++) {
            if (f[i]) {
                filters.push(f[i]);
            }
        }
        return filters;
    };
    
    var isValueIncludedInFilterElementRange = function (rowValue, column) {
        if (typeof rowValue === 'undefined' || rowValue === null) {
            // missing value, can return false immediately
            return false;
        }
        var included = true;
        if (column.type === 'numeric') {
            if (column.minimumInclusive) {
                included = included && rowValue >= column.minimum;
            } else {
                included = included && rowValue > column.minimum;
            }
            if (column.maximumInclusive) {
                included = included && rowValue <= column.maximum;
            } else {
                included = included && rowValue < column.maximum;
            }
        } else if (column.type === 'nominal') {
            included = included && column.values.indexOf(rowValue) >= 0;
        }
        return included;
    };

    kt.isRowIncludedInFilter = function (rowID, filter) {
        if (!filter || !filter.elements) {
            return true;
        }
        var row = kt.getRow(rowID);
        if (!row) {
            return false;
        }
        var included = true;
        for (var i = 0; i < filter.elements.length; i++) {
            var filterElement = filter.elements[i];
            if (filterElement.type === 'range' && filterElement.columns) {
                for (var col = 0; col < filterElement.columns.length; col++) {
                    var column = filterElement.columns[col];
                    var columnIndex = _getDataColumnID(column.columnName);
                    if (columnIndex !== null) {
                        var rowValue = row.data[columnIndex];
                        included = included && isValueIncludedInFilterElementRange(rowValue, column);
                    }
                }
            } else {
                // TODO row filter - currently not possible
            }
        }
        return included;
    };

    kt.mergeTables = function (mergeTable) {
        // TODO: make sure spec etc. validates
        if (dataTable.fragmentFirstRowIndex + dataTable.spec.numRows === mergeTable.fragmentFirstRowIndex) {
            dataTable.rows = dataTable.rows.concat(mergeTable.rows);
            dataTable.spec.rowColorValues = dataTable.spec.rowColorValues.concat(mergeTable.spec.rowColorValues);
            dataTable.spec.numRows += mergeTable.spec.numRows;
        } else {
            dataTable = mergeTable;
        }
    };

    return kt;
};
