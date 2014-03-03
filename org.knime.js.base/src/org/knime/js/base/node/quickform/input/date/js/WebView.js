org_knime_js_base_node_quickform_input_date = function() {
	var dateInput = {
			version: "1.0.0"
	};
	dateInput.name = "Date input";
	var viewValue;
	var viewRepresentation;
	var dateInput;
	var hourInput;
	var minInput;
	var secInput;
	var milInput;
	var errorMessage;

	dateInput.init = function(representation, value) {
		viewValue = value;
		viewRepresentation = representation;
		var body = $('body');
		dateInput = body.append('<input id="date"></input>').find("#date");
		dateInput.attr("type", "text");
		dateInput.datepicker({
			dateFormat : "yy-mm-dd",
			changeYear : true
		});
		if (viewRepresentation.usemin) {
			dateInput.datepicker('option', 'minDate', new Date(viewRepresentation.min));
		}
		if (viewRepresentation.usemax) {
			dateInput.datepicker('option', 'maxDate', new Date(viewRepresentation.max));
		}
		var date = new Date(viewValue.date);
		dateInput.val(paddedNumber(date.getFullYear(), 4)+'-'+paddedNumber((date.getMonth()+1), 2)+'-'+paddedNumber(date.getDate(), 2));
		
		hourInput = body.append('<input id="hour"></input>').find("#hour");
		hourInput.attr("type", "text");
		hourInput.spinner({
			min : 0,
			max : 23
		});
		hourInput.val(date.getHours());

		minInput = body.append('<input id="min"></input>').find("#min");
		minInput.attr("type", "text");
		minInput.spinner({
			min : 0,
			max : 59
		});
		minInput.val(date.getMinutes());

		secInput = body.append('<input id="sec"></input>').find("#sec");
		secInput.attr("type", "text");
		secInput.spinner({
			min : 0,
			max : 59
		});
		secInput.val(date.getSeconds());

		milInput = body.append('<input id="mil"></input>').find("#mil");
		milInput.attr("type", "text");
		milInput.spinner({
			min : 0,
			max : 999
		});
		milInput.val(date.getMilliseconds());
		body.append($('<br>'));
		errorMessage = $('<span>');
		errorMessage.css('display', 'none');
		errorMessage.css('color', 'red');
		errorMessage.css('font-style', 'italic');
		errorMessage.css('font-size', '75%');
		body.append(errorMessage);
	};
	
	dateInput.validate = function() {
		var valid = true;
		var value = dateInput.datepicker('getDate');
		value.setHours(hourInput.val());
		value.setMinutes(minInput.val());
		value.setSeconds(secInput.val());
		value.setMilliseconds(milInput.val());
		var min = new Date(viewRepresentation.min);
		var max = new Date(viewRepresentation.max);
		if (viewRepresentation.usemin && value<min) {
			valid = false;
			errorMessage.text("The set date " + value + " is before the earliest allowed date " + min);
			errorMessage.css('display', 'inline');
		} else if (viewRepresentation.usemax && value>max) {
			valid = false;
			errorMessage.text("The set date " + value + " is after the latest allowed date " + max);
			errorMessage.css('display', 'inline');
		} else {
			valid = true;
			errorMessage.css('display', 'none');
		}
		return valid;
	}

	dateInput.value = function() {
		var date = dateInput.datepicker('getDate');
		date.setHours(hourInput.val());
		date.setMinutes(minInput.val());
		date.setSeconds(secInput.val());
		date.setMilliseconds(milInput.val());
		viewValue.date = date.getTime();
		return viewValue;
	};
	
	function paddedNumber(number, targetLength) {
	    var output = number + '';
	    while (output.length < targetLength) {
	        output = '0' + output;
	    }
	    return output;
	}
	
	return dateInput;
	
}();
