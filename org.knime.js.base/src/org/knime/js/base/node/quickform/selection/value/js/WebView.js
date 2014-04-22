org_knime_js_base_node_quickform_selection_value = function() {
	var valueSelection = {
		version : "1.0.0"
	};
	valueSelection.name = "Value selection";
	var viewValue;
	var viewRepresentation;
	var colselection;
	var selector;

	valueSelection.init = function(representation, value) {
		var body = $('body');
		viewRepresentation = representation;
		viewValue = value;
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
			if (representation.type == 'Radio buttons (vertical)') {
				selector = new radioButtonSingleSelection(true);
			} else if (representation.type == 'Radio buttons (horizontal)') {
				selector = new radioButtonSingleSelection(false);
			} else if (representation.type == 'List') {
				selector = new listSingleSelection();
			} else {
				selector = new dropdownSingleSelection();
			}
			body.append(selector.getComponent());
			selector.setChoices(viewRepresentation.possibleValues[representation.defaultColumn]);
			selector.setSelection(representation.defaultValue);
		}
	};

	valueSelection.value = function() {
		viewValue.value = selector.getSelection();
		if (!viewRepresentation.lockColumn) {
			viewValue.column = colselection.find(':selected').text();
		}
		return viewValue;
	};

	function selectionChanged() {
		var col = colselection.find(':selected').text();
		var possibleValues = viewRepresentation.possibleValues[col];
		selector.setChoices(possibleValues);
		if (possibleValues.length > 0) {
			selector.setSelection(possibleValues[0]);
		}
	}

	return valueSelection;

}();
