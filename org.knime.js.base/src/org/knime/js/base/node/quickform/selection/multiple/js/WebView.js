org_knime_js_base_node_quickform_selection_multiple = function() {
	var multiSelection = {
		version : "1.0.0"
	};
	multiSelection.name = "Multiple selections";
	var viewValue;
	var viewRepresentation;
	var excludes;
	var includes;

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
			excludes = $('<select>');
			excludes.css('float', 'left');
			includes = $('<select>');
			includes.css('float', 'left');
			var span = $('<span>');
			span.css('float', 'left');
			var add = $('<button type="button">&gt;</button>');
			add.click(execAdd);
			var addAll = $('<button type="button">&gt;&gt;</button>');
			addAll.click(execAddAll);
			var remove = $('<button type="button">&lt;</button>');
			remove.click(execRemove);
			var removeAll = $('<button type="button">&lt;&lt;</button>');
			removeAll.click(execRemoveAll);
			body.append(excludes);
			body.append(span);
			body.append(includes);
			span.append(add);
			span.append('<br>');
			span.append(addAll);
			span.append('<br>');
			span.append(remove);
			span.append('<br>');
			span.append(removeAll);
			$('select').prop('multiple', true);
			$('select').attr('size', viewRepresentation.possibleChoices.length);

			refreshLists();

			// All buttons get the same width
			var widthMax = Math.max(add.width(), addAll.width(),
					remove.width(), removeAll.width());
			add.width(widthMax);
			addAll.width(widthMax);
			remove.width(widthMax);
			removeAll.width(widthMax);
			// All columns get the same height
			var heightMax = Math.max(excludes.height(), includes.height(), span
					.height());
			excludes.height(heightMax);
			includes.height(heightMax);
			span.height(heightMax);
			// Lists get the same width
			var listWidthMax = Math.max(excludes.width(), includes.width());
			excludes.width(listWidthMax);
			includes.width(listWidthMax);
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
			includes.find('option').each(function() {
				values.push($(this).text());
			});
		}
		viewValue.value = values;
		return viewValue;
	};

	function refreshLists() {
		excludes.empty();
		includes.empty();
		for ( var i in viewRepresentation.possibleChoices) {
			var choice = viewRepresentation.possibleChoices[i];
			var option = $('<option>' + choice + '</option>');
			if ($.inArray(choice, viewValue.value) >= 0) {
				option.appendTo(includes);
			} else {
				option.appendTo(excludes);
			}
		}
		refreshListeners();
	}

	function refreshListeners() {
		excludes.find('option').dblclick(execAdd);
		includes.find('option').dblclick(execRemove);
	}

	function execAdd() {
		excludes.find(':selected').each(function() {
			includes.append($(this));
		});
		multiSelection.value();
		refreshLists();
	}

	function execAddAll() {
		excludes.find('option').each(function() {
			includes.append($(this));
		});
		multiSelection.value();
		refreshLists();
	}

	function execRemove() {
		includes.find(':selected').each(function() {
			excludes.append($(this));
		});
		multiSelection.value();
		refreshLists();
	}

	function execRemoveAll() {
		includes.find('option').each(function() {
			excludes.append($(this));
		});
		multiSelection.value();
		refreshLists();
	}

	return multiSelection;

}();
