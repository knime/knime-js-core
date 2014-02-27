org_knime_js_base_node_quickform_selection_column = function() {
	var columnSelection = {
			version: "1.0.0"
	};
	columnSelection.name = "Column selection";
	var viewValue;

	columnSelection.init = function(representation, value) {
		viewValue = value;
		if (representation.possibleColumns == null) {
			$('body').append("Error: No data available");
		} else {
			var selection = $('<select>');
			$('body').append(selection);
			for ( var i in representation.possibleColumns) {
				var column = representation.possibleColumns[i];
				var option = $('<option>' + column + '</option>');
				option.appendTo(selection);
				if (column == viewValue.column) {
					option.prop('selected', true);
				}
			}
		}
	};

	columnSelection.value = function() {
		viewValue.column = $(':selected').text();
		return viewValue;
	};
	
	return columnSelection;
	
}();
