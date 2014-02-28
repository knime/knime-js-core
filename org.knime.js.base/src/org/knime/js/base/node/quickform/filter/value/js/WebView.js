org_knime_js_base_node_quickform_filter_value = function() {
	var valueFilter = {
			version: "1.0.0"
	};
	valueFilter.name = "Value filter";
	var viewValue;
	var list;

	valueFilter.init = function(representation, value) {
		list = new twinlist();
		viewValue = value;
		if (representation.possibleValues == null) {
			$('body').append("Error: No data available");
		} else {
			$('body').append(list.getElement());
			list.setAvailableValues(representation.possibleValues);
			list.setIncludes(value.values);
		}
	};

	valueFilter.value = function() {
		viewValue.values = list.getIncludes();
		return viewValue;
	};
	
	return valueFilter;
	
}();
