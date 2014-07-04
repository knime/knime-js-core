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
	
	var drawingStartTime = 0;
	
	var formatterForType = [];
	
	var additionalDrawFunctions = [];
	
	tableView.draw = function() {
		if (typeof knimeTable == 'undefined' || knimeTable == null) {
			return;
		}
		var table = $('<table>');
		table.attr('class', 'knimeTableView');
		container.append(table);
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
		var initialChunkSize = 20;
		drawingStartTime = new Date().getTime();
		_renderChunk(table, 0, initialChunkSize);
		
		for (var i = 0; i < additionalDrawFunctions.length; i++) {
			additionalDrawFunctions[i]();
		}
		tableDrawn = true;
	};
	
	_renderChunk = function(table, startIndex, chunkSize) {
		var startTime = new Date().getTime();
		var endIndex  = Math.min(knimeTable.getNumRows(), startIndex + chunkSize);
		_renderRows(table, startIndex, endIndex);
		var endTime = new Date().getTime();
		var chunkDuration = endTime - startTime;
		//var timeRemaining = (knimeTable.getNumRows() - endIndex) * totalDrawDuration / endIndex;
		//console.log("Rendered row " + startIndex + " to " + (endIndex-1) + ". Took " + chunkDuration + "ms. Estimated time remaining: " + Math.round(timeRemaining) + "ms.");
		var newChunkSize = chunkSize;
		if (startIndex + chunkSize < knimeTable.getNumRows()) {
			if (chunkDuration > 300) {
				newChunkSize = Math.max(1, Math.floor(chunkSize / 2));
			} else if (chunkDuration < 100) {
				newChunkSize = chunkSize * 2;
			}
			setTimeout((function(t, i, c) {
				return function() {
					_renderChunk(t, i, c);
				};
			})(table, startIndex + chunkSize, newChunkSize), chunkDuration);
		} else {
			//var totalDrawDuration = new Date().getTime() - drawingStartTime;
			//console.log("Total layout time " + totalDrawDuration + "ms.");
		}
	};
	
	_renderRows = function(table, startIndex, endIndex) {
		for ( var i = startIndex; i < endIndex; i++) {
			var tableRow = $('<tr>');
			tableRow.attr('id', 'row' + i);
			tableRow.attr('class', 'knimeTableRow');
			
			if (showRowKeys) {
				var rowKeyCell = $('<td>');
				rowKeyCell.attr('class', 'knimeTableCell knimeTableRowKeyCell');
				var rowKeyWrapper = $('<div>');
				rowKeyWrapper.attr('class', 'knimeTableRowKeyWrapper');
				var colorBox = $('<div>');
				colorBox.attr('class', 'knimeTableRowColor');
				colorBox.css('background-color', knimeTable.getRowColors()[i]);
				colorBox.css('width', 16).css('height', 16);
				rowKeyWrapper.append(colorBox);
				var rowKeyTextBox = $('<div>');
				rowKeyTextBox.attr('class', 'knimeTableRowKeyText');
				rowKeyTextBox.text(knimeTable.getRows()[i].rowKey);
				rowKeyWrapper.append(rowKeyTextBox);
				rowKeyCell.append(rowKeyWrapper);
				tableRow.append(rowKeyCell);
			}
			
			for ( var j = 0; j < knimeTable.getNumColumns(); j++) {
				var columnType = knimeTable.getColumnTypes()[j];
				var cellContent = knimeTable.getRows()[i].data[j];
				var tableData = $('<td>');
				tableData.attr('class', 'knimeTableCell ' + columnType);
				if (columnType === "boolean" || columnType === "number" || columnType === "string") {
					var formatter = tableView.getFormatterForType(columnType);
					var textContent = cellContent;
					if (formatter) {
						textContent = formatter.format(cellContent);
					}
					tableData.text(textContent);
				} else if (columnType === "svg") {
					tableData.append($.parseHTML(cellContent));
				}
				else if (columnType === "png") {
					var image = $('<img>');
					tableData.append(image);
					image.attr('src', 'data:image/png;base64,' + cellContent);
				}
				tableRow.append(tableData);
			}
			table.append(tableRow);
		}
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
	
	tableView.getFormatterForType = function(type) {
		for (var i = 0; i < formatterForType.length; i++) {
			var formatter = formatterForType[i];
			if (formatter.type === type) {
				return formatter;
			}
		}
	};
	
	tableView.setNumberFormatter = function(decimal_places, decimal_separator, thousands_separator) {
		formatterForType.push({type: "number", format: function(value) {
			return $.number(value, decimal_places, decimal_separator, thousands_separator);
		}});
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
