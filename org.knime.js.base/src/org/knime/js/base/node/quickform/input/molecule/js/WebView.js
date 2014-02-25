org_knime_js_base_node_quickform_input_molecule = function() {
	var moleculeInput = {
			version: "1.0.0"
	};
	moleculeInput.name = "Molecule input";
	var viewValue;
	var input;

	moleculeInput.init = function(representation, value) {
		viewValue = value;
		input = $("body").append("<input></input>").find("input");
		input.attr("type", "text");
		input.val(viewValue.moleculeString);
	};

	moleculeInput.value = function() {
		viewValue.moleculeString = input.val();
		return viewValue;
	};
	
	return moleculeInput;
	
}();
