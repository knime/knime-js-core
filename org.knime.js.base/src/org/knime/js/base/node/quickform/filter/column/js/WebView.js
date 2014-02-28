org_knime_js_base_node_quickform_filter_column = function() {
	var columnFilter = {
			version: "1.0.0"
	};
	columnFilter.name = "Column filter";
	var viewValue;
	var list;

	columnFilter.init = function(representation, value) {
		list = new twinlist();
		viewValue = value;
		if (representation.possibleColumns == null) {
			$('body').append("Error: No data available");
		} else {
			$('body').append(list.getElement());
			list.setAvailableValues(representation.possibleColumns);
			list.setIncludes(value.columns);
		}
	};

	columnFilter.value = function() {
		viewValue.columns = list.getIncludes();
		return viewValue;
	};

	return columnFilter;
	
}();
