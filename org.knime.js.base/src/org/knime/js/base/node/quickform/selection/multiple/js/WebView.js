org_knime_js_base_node_quickform_selection_multiple = function() {
	var multiSelection = {
		version : "1.0.0"
	};
	multiSelection.name = "Multiple selections";
	var viewValue;
	var selector;

	multiSelection.init = function(representation, value) {
		var body = $('body');
		viewValue = value;
		if (representation.possibleChoices.length > 0) {
			if (representation.type == 'Check boxes (vertical)') {
				selector = new checkBoxesMultipleSelections(true);
			} else if (representation.type == 'Check boxes (horizontal)') {
				selector = new checkBoxesMultipleSelections(false);
			} else if (representation.type == 'List') {
				selector = new listMultipleSelections();
			} else {
				selector = new twinlistMultipleSelections();
			}
			body.append(selector.getComponent());
			selector.setChoices(representation.possibleChoices);
			selector.setSelections(representation.defaultvalue);
		}
	};

	multiSelection.value = function() {
		viewValue.value = selector.getSelections();
		return viewValue;
	};

	return multiSelection;

}();
