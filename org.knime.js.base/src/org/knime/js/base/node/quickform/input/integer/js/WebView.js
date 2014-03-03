org_knime_js_base_node_quickform_input_integer = function() {
	var integerInput = {
			version: "1.0.0"
	};
	integerInput.name = "Integer input";
	var viewValue;
	var viewRepresentation;
	var input;
	var errorMessage;

	integerInput.init = function(representation, value) {
		viewValue = value;
		viewRepresentation = representation;
		var body = $('body');
		body.append($('<span>' + infoText() + '</span>'));
		input = $('<input>');
		body.append(input);
		input.attr("type", "text");
		input.val(viewValue.integer);
		body.append($('<br>'));
		errorMessage = $('<span>');
		errorMessage.css('display', 'none');
		errorMessage.css('color', 'red');
		errorMessage.css('font-style', 'italic');
		errorMessage.css('font-size', '75%');
		body.append(errorMessage);
	};
	
	integerInput.validate = function() {
		var valid;
		var value = input.val();
		var min = viewRepresentation.min;
		var max = viewRepresentation.max;
		if (viewRepresentation.usemin && value<min) {
			valid = false;
			errorMessage.text("The set integer " + value + " is smaller than the required minimum " + min);
			errorMessage.css('display', 'inline');
		} else if (viewRepresentation.usemax && value>max) {
			valid = false;
			errorMessage.text("The set integer " + value + " is bigger than the required maximum " + max);
			errorMessage.css('display', 'inline');
		} else {
			valid = true;
			errorMessage.css('display', 'none');
		}
		return valid;
	};

	integerInput.value = function() {
		viewValue.integer = input.val();
		return viewValue;
	};
	
	function infoText() {
		var text;
		var usemin = viewRepresentation.usemin;
		var usemax = viewRepresentation.usemax;
		var min = viewRepresentation.min;
		var max = viewRepresentation.max;
		if (usemin && usemax) {
			text = 'Integer between ' + min + ' and ' + max + ': ';
		} else if (usemin) {
			text = 'Integer bigger than ' + min + ': ';
		} else if (usemax) {
			text = 'Integer smaller than ' + max + ': ';
		} else {
			text = '';
		}
		return text;
	}
	
	return integerInput;
	
}();
