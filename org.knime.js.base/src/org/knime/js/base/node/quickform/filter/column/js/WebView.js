org_knime_js_base_node_quickform_filter_column = function() {
	var columnFilter = {
			version: "1.0.0"
	};
	columnFilter.name = "Column filter";
	var viewValue;
	var selector;

	columnFilter.init = function(representation, value) {
		var body = $('body');
		viewValue = value;
		if (representation.possibleColumns == null) {
			body.append("Error: No data available");
		} else {
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
			selector.setChoices(representation.possibleColumns);
			selector.setSelections(representation.defaultColumns);
		}
	};

	columnFilter.value = function() {
		viewValue.columns = selector.getSelections();
		return viewValue;
	};

	return columnFilter;
	
}();
