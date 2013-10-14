org_knime_js_base_node_quickform_input_string = function() {
	
	var stringInput = { version: "1.0.0" };
	stringInput.name = "KNIME Quickforms String Input";
	var className = "qf_input_string";
	
	stringInput.init = function(viewContent, containerID) {
		var parsedContent = JSON.parse(viewContent);
		jQuery("#" + containerID).appendChild("input")
			.attr("type", "text").attr("class", className)
			.attr("pattern", parsedContent.pattern).value(parsedContent.value);
	};
	
	stringInput.validate = function() {
		//TODO: match pattern
	};
	
	stringInput.getComponentValue = function(containerID) {
		var value = jQuery("#" + containerID + " input." + className).value();
		return JSON.stringify(value);
	};
	
	return stringInput;
}();