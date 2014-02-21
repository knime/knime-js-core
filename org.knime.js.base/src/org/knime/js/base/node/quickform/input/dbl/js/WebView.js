org_knime_js_base_node_quickform_input_dbl = function() {
	var doubleInput = {
			version: "1.0.0"
	};
	doubleInput.name = "Double input";
	var viewValue;
	var input;
	var viewValue;

	doubleInput.init = function(representation, value) {
		viewValue = value;
		input = $("body").append("<input></input>").find("input");
		input.attr("type", "text");
		input.val(viewValue.double);
	};

	doubleInput.value = function() {
		viewValue.double = input.val();
		return viewValue;
	};
	
	return doubleInput;
	
}();
