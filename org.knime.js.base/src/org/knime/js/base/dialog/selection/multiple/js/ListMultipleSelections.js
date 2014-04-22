function listMultipleSelections() {
	var select;
	this.getComponent = function() {
		return select;
	};
	this.setChoices = function(choices) {
		select.empty();
		for ( var i in choices) {
			var choice = choices[i];
			var option = $('<option>' + choice + '</option>');
			option.appendTo(select);
		}
	};
	this.getSelections = function() {
		var selections = [];
		select.find(':selected').each(function() {
			selections.push($(this).text());
		});
		return selections;
	};
	this.setSelections = function(selections) {
		select.find('option').each(function() {
			var element = $(this);
			if ($.inArray(element.text(), selections) >= 0) {
				element.prop('selected', true);
			} else {
				element.prop('selected', false);
			}
		});
	};
	select = $('<select>');
	select.prop('multiple', true);
}
