org_knime_js_base_node_quickform_filter_value = function() {
	var valueFilter = {
			version: "1.0.0"
	};
	valueFilter.name = "Value filter";
	var viewValue;
	var viewRepresentation;
	var list;
	var colselection;

	valueFilter.init = function(representation, value) {
		list = new twinlist();
		viewValue = value;
		viewRepresentation = representation;
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
			$('body').append(list.getElement());
			list.setAvailableValues(representation.possibleValues[representation.defaultColumn]);
			list.setIncludes(representation.defaultValues);
		}
	};

	valueFilter.value = function() {
		viewValue.values = list.getIncludes();
		if (!viewRepresentation.lockColumn) {
			viewValue.column = colselection.find(':selected').text();
		}
		return viewValue;
	};

	function selectionChanged() {
		var col = colselection.find(':selected').text();
		list.setAvailableValues(viewRepresentation.possibleValues[col]);
	}
	
	return valueFilter;
	
}();
