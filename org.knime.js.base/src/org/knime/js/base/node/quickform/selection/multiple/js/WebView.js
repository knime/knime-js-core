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
		if (viewRepresentation.possibleChoices.length > 0) {
			if (viewRepresentation.type == 'Check boxes (vertical)'
					|| viewRepresentation.type == 'Check boxes (horizontal)') {
				var addLinebreak = viewRepresentation.type == 'Check boxes (vertical)';
				for ( var i in viewRepresentation.possibleChoices) {
					var choice = viewRepresentation.possibleChoices[i];
					var button = $('<input id="choice'+i+'" type="checkbox" name="singleSelection" value="'
							+ choice + '"></input>');
					var label = $('<label for="choice'+i+'">'+choice+'</label>');
					var nobr = $('<nobr>');
					nobr.append(button);
					nobr.append(label);
					body.append(nobr);
					body.append(' ');
					if (addLinebreak) {
						body.append('<br>');
					}
					if ($.inArray(choice, viewValue.value) >= 0) {
						button.prop('checked', true);
					}
				}
				elements = $('label');
				elements.width(getMaxWidth(elements));
				elements.css('display', 'inline-block');
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
		}
	};

	multiSelection.value = function() {
		if (viewRepresentation.possibleChoices.length > 0) {
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
		}
		return viewValue;
	};
	
	function getMaxWidth(elements) {
		return Math.max.apply(null, elements.map(function ()
				{
				    return $(this).width();
				}).get());
	}

	return multiSelection;

}();
