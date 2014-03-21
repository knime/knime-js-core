knime_row_selector = function() {
	var rowSelector = {
		version : "1.0.0"
	};
	rowSelector.name = "KNIME Row Selector";
	var viewValue;

	rowSelector.init = function(representation, value) {
		var body = $('body');
		if (representation.table == null) {
			body.append("Error: No data available");
		} else {
			viewValue = value;
			var knimeTable = new kt();
			knimeTable.setDataTable(representation.table);
			var table = $('<table>');
			table.css('margin', '0 auto');
			var headerRow = $('<tr>');
			table.append(headerRow);
			var selectAllData = $('<td>');
			headerRow.append(selectAllData);
			var selectAll = $('<input>');
			selectAllData.append(selectAll);
			selectAll.attr('type', 'checkbox');
			selectAll.change(function() {
				var checked = this.checked;
				$("input:checkbox").each(function() {
					this.checked = checked;
				});
				if (checked) {
					$('.row').css('color', 'black');
				} else {
					$('.row').css('color', 'grey');
				}
				callUpdate();
			});
			for ( var i = 0; i < knimeTable.getColumnNames().length; i++) {
				var tableHeader = $('<th>');
				headerRow.append(tableHeader);
				tableHeader.text(knimeTable.getColumnNames()[i]);
			}
			for ( var i = 0; i < knimeTable.getNumRows(); i++) {
				var tableRow = $('<tr>');
				table.append(tableRow);
				tableRow.attr('id', 'row' + i);
				tableRow.attr('class', 'row');
				tableRow.css('color', 'grey');
				var checkboxTD = $('<td>');
				tableRow.append(checkboxTD);
				var checkbox = $('<input>');
				checkboxTD.append(checkbox);
				checkbox.attr('type', 'checkbox');
				checkbox.attr('id', 'checkbox' + i);
				checkbox.val(i);
				checkbox.change(function() {
					var checked = this.checked;
					var val = $(this).val();
					if (checked) {
						$('#row' + val).css('color', 'black');
					} else {
						$('#row' + val).css('color', 'grey');
					}
					callUpdate();
				});
				for ( var j = 0; j < knimeTable.getColumnNames().length; j++) {
					var columnType = knimeTable.getColumnTypes()[j];
					var tableData = $('<td>');
					tableRow.append(tableData);
					if (columnType=="boolean" || columnType=="number" || columnType=="string") {
						tableData.text(knimeTable.getColumn(j)[i]);
					} else if (columnType=="png") {
						var image = $('<img>');
						tableData.append(image);
						image.attr('src', 'data:image/png;base64,' + knimeTable.getColumn(j)[i]);
					}
				}
			}
			body.append(table);
			initFromValue(value);
		}
		callUpdate();
		resize();
	};

	rowSelector.getComponentValue = function() {
		var selected = new Array();
		$("input:checkbox:checked").each(function() {
			var val = $(this).val();
			if (isNumber(val)) {
				selected.push(val);
			}
		});
		viewValue.selections = selected;
		return viewValue;
	};

	function initFromValue(value) {
		var selected = value.selections;
		for ( var i = 0; i < selected.length; i++) {
			$("#checkbox" + selected[i])[0].checked = true;
			$("#row" + selected[i]).css('color', 'black');
		}
	}

	function isNumber(n) {
		return !isNaN(parseFloat(n)) && isFinite(n);
	}
	
	callUpdate = function() {
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.getPageValues();
		}
	};
	
	resize = function() {
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.autoResize(window.frameElement.id);
		}
	};

	return rowSelector;
}();
