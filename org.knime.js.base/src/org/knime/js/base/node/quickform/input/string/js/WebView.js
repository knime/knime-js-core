var input;
var content;

function matchExact(regex, string) {
	var match = string.match(regex);
	return match != null && string == match[0];
}

function init(viewContent, containerID) {
	content = JSON.parse(viewContent);
	input = $("#" + containerID).append("<input></input>").find("input");
	input.attr("type", "text");
	input.attr("pattern", content.regex);
	input.val(content.value.string);
}

function validate() {
	var valid = matchExact(input.attr("pattern"), input.val());
	if (!valid) {
		// TODO show error message
	}
	return valid;
}

function value(containerID) {
	content.value.string = input.val();
	return JSON.stringify(content.value);
}
