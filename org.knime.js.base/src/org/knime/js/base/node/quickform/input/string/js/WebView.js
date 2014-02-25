org_knime_js_base_node_quickform_input_string = function() {
	var stringInput = {
			version: "1.0.0"
	};
	stringInput.name = "String input";
	var viewValue;
	var input;

	stringInput.init = function(representation, value) {
		viewValue = value;
		input = $("body").append("<input></input>").find("input");
		input.attr("type", "text");
		input.attr("pattern", representation.regex);
		input.val(viewValue.string);
	};

	stringInput.validate = function() {
		alert("Validate");
		var valid = matchExact(input.attr("pattern"), input.val());
		if (!valid) {
			// TODO show error message
		}
		return valid;
	};

	stringInput.value = function() {
		viewValue.string = input.val();
		return viewValue;
	};
	
	return stringInput;
	
}();
