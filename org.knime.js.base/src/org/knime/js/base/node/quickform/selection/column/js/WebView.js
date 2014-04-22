org_knime_js_base_node_quickform_selection_column = function() {
	var columnSelection = {
			version: "1.0.0"
	};
	columnSelection.name = "Column selection";
	var viewValue;
	var selector

	columnSelection.init = function(representation, value) {
		var body = $('body');
		viewValue = value;
		if (representation.possibleColumns == null) {
			body.append("Error: No data available");
		} else {
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
			selector.setChoices(representation.possibleColumns);
			selector.setSelection(representation.defaultColumn);
		}
	};

	columnSelection.value = function() {
		viewValue.column = selector.getSelection();
		return viewValue;
	};
	
	return columnSelection;
	
}();
