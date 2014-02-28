org_knime_js_base_node_quickform_input_string = function() {
	var stringInput = {
			version: "1.0.0"
	};
	stringInput.name = "String input";
	var viewValue;
	var input;
	var errorMessage;

	stringInput.init = function(representation, value) {
		var body = $('body');
		viewValue = value;
		input = $('<input>');
		input.attr("type", "text");
		input.attr("pattern", representation.regex);
		input.val(viewValue.string);
		body.append(input);
		body.append($('<br>'));
		errorMessage = $('<span>'+representation.errormessage+'</span>');
		errorMessage.css('display', 'none');
		errorMessage.css('color', 'red');
		errorMessage.css('font-style', 'italic');
		errorMessage.css('font-size', '75%');
		body.append(errorMessage);
	};

	stringInput.validate = function() {
		var regex = input.attr("pattern");
		if (regex != null && regex.length > 0) {
			var valid = matchExact(regex, input.val());
			if (!valid) {
				errorMessage.css('display', 'inline');
			} else {
				errorMessage.css('display', 'none');
			}
			return valid;
		} else {
			return true;
		}
	};

	stringInput.value = function() {
		viewValue.string = input.val();
		return viewValue;
	};
	
	function matchExact(r, str) {
		var match = str.match(r);
		return match != null && str == match[0];
	}
	
	return stringInput;
	
}();
