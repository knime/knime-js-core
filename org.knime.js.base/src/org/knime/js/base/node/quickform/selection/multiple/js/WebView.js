org_knime_js_base_node_quickform_selection_multiple = function() {
	var multiSelection = {
		version : "1.0.0"
	};
	multiSelection.name = "Multiple selections";
	var viewValue;
	var viewRepresentation;
	var list;

	multiSelection.init = function(representation, value) {
		var body = $('body');
		viewRepresentation = representation;
		viewValue = value;
		if (viewRepresentation.type == 'Check boxes (vertical)'
				|| viewRepresentation.type == 'Check boxes (horizontal)') {
			var addLinebreak = viewRepresentation.type == 'Check boxes (vertical)';
			for ( var i in viewRepresentation.possibleChoices) {
				var choice = viewRepresentation.possibleChoices[i];
				var button = $('<input type="checkbox" value="' + choice + '">'
						+ choice + '</input>');
				body.append(button);
				if (addLinebreak) {
					body.append('<br>');
				}
				if ($.inArray(choice, viewValue.value) >= 0) {
					button.prop('checked', true);
				}
			}
		} else if (viewRepresentation.type == 'List') {
			var selection = $('<select>');
			selection.prop('multiple', true);
			body.append(selection);
			for ( var i in viewRepresentation.possibleChoices) {
				var choice = viewRepresentation.possibleChoices[i];
				var option = $('<option>' + choice + '</option>');
				option.appendTo(selection);
				if ($.inArray(choice, viewValue.value) >= 0) {
					option.prop('selected', true);
				}
			}
		} else if (viewRepresentation.type == 'Twinlist') {
			list = new twinlist();
			body.append(list.getElement());
			list.setAvailableValues(representation.possibleChoices);
			list.setIncludes(value.value);
		}
	};

	multiSelection.value = function() {
		var values = [];
		if (viewRepresentation.type == 'Check boxes (vertical)'
				|| viewRepresentation.type == 'Check boxes (horizontal)') {
			$(':checked').each(function() {
				values.push($(this).val());
			});
		} else if (viewRepresentation.type == 'List') {
			$(':selected').each(function() {
				values.push($(this).text());
			});
		} else if (viewRepresentation.type == 'Twinlist') {
			values = list.getIncludes();
		}
		viewValue.value = values;
		return viewValue;
	};

	return multiSelection;

}();
