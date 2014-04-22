function dropdownSingleSelection() {
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
	this.getSelection = function() {
		return select.find(':selected').text();
	};
	this.setSelection = function(selection) {
		select.find('option').each(function() {
			var element = $(this);
			if (element.text() == selection) {
				element.prop('selected', true);
			} else {
				element.prop('selected', false);
			}
		});
	};
	select = $('<select>');
}
