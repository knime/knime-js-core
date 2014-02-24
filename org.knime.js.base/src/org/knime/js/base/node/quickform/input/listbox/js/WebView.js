org_knime_js_base_node_quickform_input_listbox = function() {
	var listboxInput = {
			version: "1.0.0"
	};
	listboxInput.name = "Listbox input";
	var viewValue;
	var input;
	var viewValue;

	listboxInput.init = function(representation, value) {
		viewValue = value;
		input = $("body").append("<input></input>").find("input");
		input.attr("type", "text");
		input.attr("pattern", representation.regex);
		input.val(viewValue.string);
	};

	listboxInput.value = function() {
		viewValue.string = input.val();
		return viewValue;
	};
	
	return listboxInput;
	
}();
