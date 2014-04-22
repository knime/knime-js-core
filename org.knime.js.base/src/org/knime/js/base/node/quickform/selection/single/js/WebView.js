org_knime_js_base_node_quickform_selection_single = function() {
	var singleSelection = {
		version : "1.0.0"
	};
	singleSelection.name = "Single selection";
	var viewValue;
	var selector;

	singleSelection.init = function(representation, value) {
		var body = $('body');
		viewValue = value;
		if (representation.possibleChoices.length > 0) {
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
			selector.setChoices(representation.possibleChoices);
			selector.setSelection(representation.defaultvalue);
		}
	};

	singleSelection.value = function() {
		viewValue.value = selector.getSelection();
		return viewValue;
	};

	return singleSelection;

}();
