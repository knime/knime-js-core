org_knime_js_base_node_quickform_selection_single = function() {
	var singleSelection = {
		version : "1.0.0"
	};
	singleSelection.name = "Single selection";
	var viewValue;
	var viewRepresentation;

	singleSelection.init = function(representation, value) {
		var body = $('body');
		viewRepresentation = representation;
		viewValue = value;
		if (viewRepresentation.type == 'Radio buttons (vertical)'
				|| viewRepresentation.type == 'Radio buttons (horizontal)') {
			var addLinebreak = viewRepresentation.type == 'Radio buttons (vertical)';
			for ( var i in viewRepresentation.possibleChoices) {
				var choice = viewRepresentation.possibleChoices[i];
				var button = $('<input type="radio" name="singleSelection" value="'
						+ choice + '">' + choice + '</input>');
				body.append(button);
				if (addLinebreak) {
					body.append('<br>');
				}
				if (choice == viewValue.value) {
					button.prop('checked', true);
				}
			}
		} else if (viewRepresentation.type == 'List'
				|| viewRepresentation.type == 'Dropdown') {
			var selection = $('<select>');
			body.append(selection);
			if (viewRepresentation.type == 'List') {
				selection.attr('size',
						viewRepresentation.possibleChoices.length);
			}
			for ( var i in viewRepresentation.possibleChoices) {
				var choice = viewRepresentation.possibleChoices[i];
				var option = $('<option>' + choice + '</option>');
				option.appendTo(selection);
				if (choice == viewValue.value) {
					option.prop('selected', true);
				}
			}
		}
	};

	singleSelection.value = function() {
		if (viewRepresentation.type == 'Radio buttons (vertical)'
				|| viewRepresentation.type == 'Radio buttons (horizontal)') {
			viewValue.value = $(':checked').val();
		} else if (viewRepresentation.type == 'List'
				|| viewRepresentation.type == 'Dropdown') {
			viewValue.value = $(':selected').text();
		}
		return viewValue;
	};

	return singleSelection;

}();
