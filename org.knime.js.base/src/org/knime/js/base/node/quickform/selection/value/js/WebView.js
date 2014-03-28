org_knime_js_base_node_quickform_selection_value = function() {
	var valueSelection = {
			version: "1.0.0"
	};
	valueSelection.name = "Value selection";
	var viewValue;

	valueSelection.init = function(representation, value) {
		viewValue = value;
		if (representation.possibleValues == null) {
			$('body').append("Error: No data available");
		} else {
			var selection = $('<select>');
			$('body').append(selection);
			for ( var i in representation.possibleValues) {
				var value = representation.possibleValues[i];
				var option = $('<option>' + value + '</option>');
				option.appendTo(selection);
				if (value == representation.defaultvalue) {
					option.prop('selected', true);
				}
			}
		}
	};

	valueSelection.value = function() {
		viewValue.value = $(':selected').text();
		return viewValue;
	};
	
	return valueSelection;
	
}();
