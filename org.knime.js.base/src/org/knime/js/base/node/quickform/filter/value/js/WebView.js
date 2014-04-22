org_knime_js_base_node_quickform_filter_value = function() {
	var valueFilter = {
			version: "1.0.0"
	};
	valueFilter.name = "Value filter";
	var viewValue;
	var viewRepresentation;
	var colselection;
	var selector;

	valueFilter.init = function(representation, value) {
		var body = $('body');
		viewValue = value;
		viewRepresentation = representation;
		if (representation.possibleValues == null) {
			body.append("Error: No data available");
		} else {
			if (!representation.lockColumn) {
				colselection = $('<select>');
				body.append(colselection);
				body.append($('<br>'));
				for ( var key in representation.possibleValues) {
					var option = $('<option>' + key + '</option>');
					option.appendTo(colselection);
					if (key == representation.defaultColumn) {
						option.prop('selected', true);
					}
				}
				colselection.change(selectionChanged);
			}
			if (viewRepresentation.type == 'Check boxes (vertical)') {
				selector = new checkBoxesMultipleSelections(true);
			} else if (viewRepresentation.type == 'Check boxes (horizontal)') {
				selector = new checkBoxesMultipleSelections(false);
			} else if (viewRepresentation.type == 'List') {
				selector = new listMultipleSelections();
			} else {
				selector = new twinlistMultipleSelections();
			}
			body.append(selector.getComponent());
			selector.setChoices(viewRepresentation.possibleValues[representation.defaultColumn]);
			selector.setSelections(representation.defaultValues);
		}
	};

	valueFilter.value = function() {
		viewValue.values = selector.getSelections();
		if (!viewRepresentation.lockColumn) {
			viewValue.column = colselection.find(':selected').text();
		}
		return viewValue;
	};

	function selectionChanged() {
		var col = colselection.find(':selected').text();
		selector.setChoices(viewRepresentation.possibleValues[col]);
		selector.setSelection([]);
	}
	
	return valueFilter;
	
}();
