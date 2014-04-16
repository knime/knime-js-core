org_knime_js_base_node_quickform_selection_value = function() {
	var valueSelection = {
		version : "1.0.0"
	};
	valueSelection.name = "Value selection";
	var viewValue;
	var viewRepresentation;
	var selection;
	var colselection;

	valueSelection.init = function(representation, value) {
		viewRepresentation = representation;
		viewValue = value;
		if (representation.possibleValues == null) {
			$('body').append("Error: No data available");
		} else {
			if (!representation.lockColumn) {
				colselection = $('<select>');
				$('body').append(colselection);
				$('body').append($('<br>'));
				for ( var key in representation.possibleValues) {
					var option = $('<option>' + key + '</option>');
					option.appendTo(colselection);
					if (key == representation.defaultColumn) {
						option.prop('selected', true);
					}
				}
				colselection.change(selectionChanged);
			}
			selection = $('<select>');
			$('body').append(selection);
			for (var i in representation.possibleValues[representation.defaultColumn]) {
				var value = representation.possibleValues[representation.defaultColumn][i];
				var option = $('<option>' + value + '</option>');
				option.appendTo(selection);
				if (value == representation.defaultValue) {
					option.prop('selected', true);
				}
			}
		}
	};

	valueSelection.value = function() {
		viewValue.value = selection.find(':selected').text();
		if (!viewRepresentation.lockColumn) {
			viewValue.column = colselection.find(':selected').text();
		}
		return viewValue;
	};

	function selectionChanged() {
		selection.empty();
		var col = colselection.find(':selected').text();
		var first = true;
		for (var i in viewRepresentation.possibleValues[col]) {
			var value = viewRepresentation.possibleValues[col][i];
			var option = $('<option>' + value + '</option>');
			option.appendTo(selection);
			if (first) {
				option.prop('selected', true);
				first = false;
			}
		}
	}

	return valueSelection;

}();
