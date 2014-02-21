org_knime_js_base_node_quickform_input_integer = function() {
	var integerInput = {
			version: "1.0.0"
	};
	integerInput.name = "Integer input";
	var viewValue;
	var input;
	var viewValue;

	integerInput.init = function(representation, value) {
		viewValue = value;
		input = $("body").append("<input></input>").find("input");
		input.attr("type", "text");
		input.val(viewValue.integer);
	};

	integerInput.value = function() {
		viewValue.integer = input.val();
		return viewValue;
	};
	
	return integerInput;
	
}();
