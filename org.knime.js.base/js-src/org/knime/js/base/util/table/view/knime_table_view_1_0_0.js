knime_table_view = function(table, containerElement) {
	var tableView = {
		version : "1.0.0"
	};
	tableView.name = "KNIME HTML Table View";

	//TODO: sanity check table and container
	var knimeTable = table;
	var container = containerElement;

	var tableDrawn = false;
	
	var showColumnHeader = true;
	var showRowKeys = false;
	var sortable = false;
	var sortColumn = null;
	var sortAscending = true;
	
	var additionalDrawFunctions = [];
	
	tableView.draw = function() {
		if (typeof knimeTable == 'undefined' || knimeTable == null) {
			return;
		}
		var table = $('<table>');
		table.attr('class', 'knimeTableView');
		if (showColumnHeader || sortable) {
			var headerRow = $('<tr>');
			headerRow.attr('class', 'knimeTableRow knimeTableHeaderRow');
			table.append(headerRow);
			for ( var i = -1; i < knimeTable.getColumnNames().length; i++) {
				var tableHeader = $('<th>');
				if(i >= 0) {
					showColumnHeader && tableHeader.text(knimeTable.getColumnNames()[i]);
					tableHeader.attr('class', 'knimeTableCell knimeTableHeaderCell');
					headerRow.append(tableHeader);
				} else if (showRowKeys) {
					showColumnHeader && tableHeader.text("Row ID");
					tableHeader.attr('class', 'knimeTableCell knimeTableHeaderCell knimeTableRowKeyHeaderCell');
					headerRow.append(tableHeader);
				}
			}
		}
		for ( var i = 0; i < knimeTable.getNumRows(); i++) {
			var tableRow = $('<tr>');
			table.append(tableRow);
			tableRow.attr('id', 'row' + i);
			tableRow.attr('class', 'knimeTableRow');
			
			if (showRowKeys) {
				var rowKeyCell = $('<td>');
				rowKeyCell.attr('class', 'knimeTableCell knimeTableRowKeyCell');
				var colorBox = $('<div>');
				colorBox.attr('class', 'knimeTableRowColor');
				colorBox.css('background-color', knimeTable.getRowColors()[i]);
				colorBox.css('width', 16).css('height', 16);
				rowKeyCell.append(colorBox);
				var rowKeyTextBox = $('<div>');
				rowKeyTextBox.attr('class', 'knimeTableRowKeyText');
				rowKeyTextBox.text(knimeTable.getRows()[i].rowKey);
				rowKeyCell.append(rowKeyTextBox);
				
				tableRow.append(rowKeyCell);
			}
			
			for ( var j = 0; j < knimeTable.getNumColumns(); j++) {
				var columnType = knimeTable.getColumnTypes()[j];
				var tableData = $('<td>');
				tableData.attr('class', 'knimeTableCell');
				tableRow.append(tableData);
				if (columnType=="boolean" || columnType=="number" || columnType=="string") {
					tableData.text(knimeTable.getRows()[i].data[j]);
							/*knimeTable.getColumn(j)[i]);*/
				} else if (columnType=="png") {
					var image = $('<img>');
					tableData.append(image);
					image.attr('src', 'data:image/png;base64,' + knimeTable.getRows[i].data[j]);
				}
			}
		}
		container.append(table);
		for (var i = 0; i < additionalDrawFunctions.length; i++) {
			additionalDrawFunctions[i]();
		}
		tableDrawn = true;
	};
	
	tableView.redraw = function() {
		if (tableDrawn) {
			container.empty();
			tableDrawn = false;
			//TODO: check if there is any other cleanup to be done
			tableView.draw();
		}
	};
	
	tableView.setShowColumnHeader = function(show, redraw) {
		showColumnHeader = show;
		redraw && tableView.redraw();
	};
	tableView.isShowColumnHeader = function() {
		return showColumnHeader;
	};
	
	tableView.setShowRowKeys = function(show, redraw) {
		showRowKeys = show;
		redraw && tableView.redraw();
	};
	tableView.isShowRowKeys = function() {
		return showRowKeys;
	};
	
	tableView.setSortable = function(isSortable, redraw) {
		sortable = isSortable;
		redraw && tableView.redraw();
	};
	tableView.isSortable = function() {
		return sortable;
	};
	
	tableView.setSorting = function(column, ascending, redraw) {
		sortColumn = column;
		sortAscending = ascending;
		sortable && knimeTable.sort(column, ascending);
		redraw && tableView.redraw();
	};
	tableView.getSortColumn = function() {
		return sortColumn;
	};
	tableView.getSortAscending = function() {
		return sortAscending;
	};
	
	tableView.addDrawFunction = function(func) {
		$.isFunction(func) && additionalDrawFunctions.push(func);
	};
	tableView.clearDrawFunctions = function() {
		additionalDrawFunctions = [];
	};

	return tableView;
};
