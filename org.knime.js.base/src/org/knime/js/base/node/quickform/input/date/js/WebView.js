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
	var minDate;
	var maxDate;

	dateInput.init = function(representation, value) {
		viewValue = value;
		viewRepresentation = representation;
		date = new Date(viewValue.date);
		minDate = viewRepresentation.usemin ? new Date(viewRepresentation.min) : null;
		maxDate = viewRepresentation.usemax ? new Date(viewRepresentation.max) : null;
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
				if (minDate!=null && date<minDate) {
					date = new Date(minDate.getTime());
				} else if (maxDate!=null && date>maxDate) {
					date = new Date(maxDate.getTime());
				}
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
				if (minDate!=null && date<minDate) {
					date = new Date(minDate.getTime());
				} else if (maxDate!=null && date>maxDate) {
					date = new Date(maxDate.getTime());
				}
				refreshTime();
				return false;
			}
		});

		body.append(' <b>:</b> ');
		secInput = $('<input>');
		body.append(secInput);
		secInput.spinner({
			spin: function(event, ui) {
				date.setSeconds(ui.value);
				if (minDate!=null && date<minDate) {
					date = new Date(minDate.getTime());
				} else if (maxDate!=null && date>maxDate) {
					date = new Date(maxDate.getTime());
				}
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
				if (minDate!=null && date<minDate) {
					date = new Date(minDate.getTime());
				} else if (maxDate!=null && date>maxDate) {
					date = new Date(maxDate.getTime());
				}
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

	dateInput.value = function() {
		viewValue.date = date.getTime();
		return viewValue;
	};
	
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
