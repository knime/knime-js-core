org_knime_js_base_node_quickform_input_listbox = function() {
	var listboxInput = {
			version: "1.0.0"
	};
	listboxInput.name = "Listbox input";
	var viewValue;
	var input;
	var errorMessageLine1;
	var errorMessageLine2;
	var separator;

	listboxInput.init = function(representation, value) {
		viewValue = value;
		var body = $('body');
		input = $('<textarea>');
		body.append(input);
		input.css('white-space', 'nowrap');
		input.attr('wrap', 'off');
		input.attr('rows', '5');
		input.attr('cols', '20');
		input.attr("pattern", representation.regex);
		input.val(viewValue.string);
		body.append($('<br>'));
		errorMessageLine1 = $('<span>');
		errorMessageLine2 = $('<span>'+representation.errormessage+'</span>');
		errorMessages = errorMessageLine1.add(errorMessageLine2);
		errorMessages.css('display', 'none');
		errorMessages.css('color', 'red');
		errorMessages.css('font-style', 'italic');
		errorMessages.css('font-size', '75%');
		body.append(errorMessageLine1);
		body.append($('<br>'));
		body.append(errorMessageLine2);
		separator = new RegExp(representation.separator);
	};

	listboxInput.validate = function() {
		var index;
		var regex = input.attr("pattern");
		if (regex != null && regex.length > 0) {
			var valid = true;
			var values = input.val().split(separator);
			for (var i=0; i<values.length; i++) {
				valid &= matchExact(regex, values[i]);
				if (!valid) {
					index = i+1;
					break;
				}
			}
			if (!valid) {
				errorMessageLine1.text('Value ' + index + ' is not valid:');
				errorMessageLine1.add(errorMessageLine2).css('display', 'inline');
			} else {
				errorMessageLine1.add(errorMessageLine2).css('display', 'none');
			}
			return valid;
		} else {
			return true;
		}
	};

	listboxInput.value = function() {
		viewValue.string = input.val();
		return viewValue;
	};
	
	function matchExact(r, str) {
		var match = str.match(r);
		return match != null && str == match[0];
	}
	
	return listboxInput;
	
}();
