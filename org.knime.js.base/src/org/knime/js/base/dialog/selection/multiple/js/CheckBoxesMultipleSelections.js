function checkBoxesMultipleSelections(vertical) {
	var span;
	var vert;
	this.getComponent = function() {
		return span;
	};
	this.setChoices = function(choices) {
		span.empty();
		for ( var i in choices) {
			var choice = choices[i];
			var button = $('<input id="choice'+i+'" type="checkbox" name="multipleSelections" value="'
					+ choice + '"></input>');
			var label = $('<label for="choice'+i+'">'+choice+'</label>');
			var nobr = $('<nobr>');
			nobr.append(button);
			nobr.append(label);
			span.append(nobr);
			span.append(' ');
			if (vert) {
				span.append('<br>');
			}
		}
		var elements = span.find('label');
		elements.width(getMaxWidth(elements));
		elements.css('display', 'inline-block');
	};
	this.getSelections = function() {
		var selections = [];
		span.find(':checked').each(function() {
			selections.push($(this).val());
		});
		return selections;
	};
	this.setSelections = function(selections) {
		span.find('input').each(function() {
			var element = $(this);
			if ($.inArray(element.val(), selections) >= 0) {
				element.prop('checked', true);
			} else {
				element.prop('checked', false);
			}
		});
	};
	getMaxWidth = function(elements) {
		return Math.max.apply(null, elements.map(function ()
				{
				    return $(this).width();
				}).get());
	};
	span = $('<span>');
	vert = vertical;
}
