org_knime_js_base_node_quickform_input_date = function() {
	var dateInput = {
			version: "1.0.0"
	};
	dateInput.name = "Date input";
	var viewValue;
	var dateInput;
	var hourInput;
	var minInput;
	var secInput;
	var milInput;
	var viewValue;

	dateInput.init = function(representation, value) {
		viewValue = value;
		dateInput = $("body").append('<input id="date"></input>').find("#date");
		dateInput.attr("type", "text");
		dateInput.datepicker({
			dateFormat : "yy-mm-dd",
			changeYear : true
		});
		var date = new Date(viewValue.date);
		dateInput.val(paddedNumber(date.getFullYear(), 4)+'-'+paddedNumber((date.getMonth()+1), 2)+'-'+paddedNumber(date.getDate(), 2));
		
		hourInput = $("body").append('<input id="hour"></input>').find("#hour");
		hourInput.attr("type", "text");
		hourInput.spinner({
			min : 0,
			max : 23
		});
		hourInput.val(date.getHours());

		minInput = $("body").append('<input id="min"></input>').find("#min");
		minInput.attr("type", "text");
		minInput.spinner({
			min : 0,
			max : 59
		});
		minInput.val(date.getMinutes());

		secInput = $("body").append('<input id="sec"></input>').find("#sec");
		secInput.attr("type", "text");
		secInput.spinner({
			min : 0,
			max : 59
		});
		secInput.val(date.getSeconds());

		milInput = $("body").append('<input id="mil"></input>').find("#mil");
		milInput.attr("type", "text");
		milInput.spinner({
			min : 0,
			max : 999
		});
		milInput.val(date.getMilliseconds());
	};

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
