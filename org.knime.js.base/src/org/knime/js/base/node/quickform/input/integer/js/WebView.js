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
		input = $('<input>');
		body.append(input);
		input.spinner();
		if (viewRepresentation.usemin) {
			input.spinner('option', 'min', viewRepresentation.min);
		}
		if (viewRepresentation.usemax) {
			input.spinner('option', 'max', viewRepresentation.max);
		}
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
		var min = viewRepresentation.min;
		var max = viewRepresentation.max;
		var value = input.val();
		if (!isInteger(value)) {
			errorMessage.text('The set value is not an integer');
			errorMessage.css('display', 'inline');
			return false;
		}
		value = parseInt(value);
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
		viewValue.integer = parseInt(input.val());
		return viewValue;
	};
	
	function isInteger(value) {
		return $.isNumeric(value) && value%1===0;
	}
	
	return integerInput;
	
}();
