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
	var date;

	dateInput.init = function(representation, value) {
		viewValue = value;
		viewRepresentation = representation;
		date = new Date(viewValue.date);
		var body = $('body');
		
		body.append('Date: ');
		dateInput = $('<input>');
		body.append(dateInput);
		dateInput.datepicker({
			dateFormat : "yy-mm-dd",
			changeYear : true,
			onSelect: function(dateText) {
				var newDate = $(this).datepicker('getDate');
				date.setFullYear(newDate.getFullYear());
				date.setMonth(newDate.getMonth());
				date.setDate(newDate.getDate());
				refreshTime();
				$(this).blur();
			}
		});
		if (viewRepresentation.usemin) {
			dateInput.datepicker('option', 'minDate', new Date(viewRepresentation.min));
		}
		if (viewRepresentation.usemax) {
			dateInput.datepicker('option', 'maxDate', new Date(viewRepresentation.max));
		}

		body.append('<br>Time: ');
		hourInput = $('<input>');
		body.append(hourInput);
		hourInput.spinner({
			spin: function(event, ui) {
				date.setHours(ui.value);
				refreshTime();
				return false;
			}
		});

		body.append(' <b>:</b> ');
		minInput = $('<input>');
		body.append(minInput);
		minInput.spinner({
			spin: function(event, ui) {
				date.setMinutes(ui.value);
				refreshTime();
				return false;
			}
		});

		body.append(' <b>:</b> ');
		secInput = $('<input>');
		body.append(secInput);
		secInput.spinner({
			spin: function(event, ui) {
				date.setSeconds(ui.value)
				refreshTime();
				return false;
			}
		});

		body.append(' <b>.</b> ');
		milInput = $('<input>');
		body.append(milInput);
		milInput.spinner({
			spin: function(event, ui) {
				date.setMilliseconds(ui.value);
				refreshTime();
				return false;
			}
		});
		body.append($('<br>'));
		errorMessage = $('<span>');
		errorMessage.css('display', 'none');
		errorMessage.css('color', 'red');
		errorMessage.css('font-style', 'italic');
		errorMessage.css('font-size', '75%');
		body.append(errorMessage);
		
		var allInputs = $('input');
		allInputs.height(20);
		allInputs.width(40);
		dateInput.width(100);
		dateInput.css('border', '1px solid silver');
		dateInput.css('margin-bottom', '10px');
		allInputs.css('font-size', 'medium');
		allInputs.attr('readonly', 'true');
		allInputs.css('background-color', 'white');
		
		refreshTime();
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
	
	function refreshTime() {
		// If datepicker is not disabled setDate will reopen the picker in IE
		dateInput.datepicker('disable');
		dateInput.datepicker('setDate', date);
		dateInput.datepicker('enable');
		hourInput.val(date.getHours());
		minInput.val(date.getMinutes());
		secInput.val(date.getSeconds());
		milInput.val(date.getMilliseconds());
	}
	
	return dateInput;
	
}();
