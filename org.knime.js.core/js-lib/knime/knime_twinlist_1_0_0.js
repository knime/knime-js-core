function twinlist() {

	// Define member variables

	var element;
	var values;
	var excludes;
	var includes;

	// Define inner functions

	setAvailableValues = function(newValues) {
		values = newValues;
		refreshLists();
		refreshSize();
	};

	refreshSize = function() {
		excludes.attr('size', values.length);
		includes.attr('size', values.length);
		// All buttons get the same width
		var widthMax = Math.max(add.width(), addAll.width(), remove.width(),
				removeAll.width());
		add.width(widthMax);
		addAll.width(widthMax);
		remove.width(widthMax);
		removeAll.width(widthMax);
		// All columns get the same height
		var heightMax = Math.max(excludes.height(), includes.height(), span
				.height());
		excludes.height(heightMax);
		includes.height(heightMax);
		span.height(heightMax);
		// Lists get the same width
		var listWidthMax = Math.max(excludes.width(), includes.width());
		excludes.width(listWidthMax);
		includes.width(listWidthMax);
		// Outer element gets overall width and height
		element.width(excludes.outerWidth(true) + span.outerWidth(true)
				+ includes.outerWidth(true));
		element.height(excludes.outerHeight(true));
	};

	getIncludes = function() {
		return getContainedValues(includes);
	};

	getExcludes = function() {
		return getContainedValues(excludes);
	};

	getElement = function() {
		return element;
	};

	setIncludes = function(includeValues) {
		setOptions(true, includeValues);
	};

	setExcludes = function(excludeValues) {
		setOptions(false, excludeValues);
	};

	setOptions = function(isIncludes, vals) {
		excludes.empty();
		includes.empty();
		for ( var i in values) {
			var value = values[i];
			var option = $('<option>' + value + '</option>');
			if (isIncludes) {
				if (contains(vals, value)) {
					option.appendTo(includes);
				} else {
					option.appendTo(excludes);
				}
			} else {
				if (contains(vals, value)) {
					option.appendTo(excludes);
				} else {
					option.appendTo(includes);
				}
			}
		}
		refreshListeners();
	};

	getContainedValues = function(selectElement) {
		var containedValues = [];
		selectElement.find('option').each(function() {
			containedValues.push($(this).text());
		});
		return containedValues;
	};

	refreshLists = function() {
		var inc = getIncludes();
		var exc = getExcludes();
		var excludeAll = inc.length + exc.length < 1;
		excludes.empty();
		includes.empty();
		for ( var i in values) {
			var value = values[i];
			var option = $('<option>' + value + '</option>');
			if (excludeAll || contains(exc, value)) {
				option.appendTo(excludes);
			} else if (contains(inc, value)) {
				option.appendTo(includes);
			}
		}
		refreshListeners();
	};

	contains = function(array, value) {
		return $.inArray(value, array) >= 0;
	};

	refreshListeners = function() {
		excludes.find('option').dblclick(execAdd);
		includes.find('option').dblclick(execRemove);
	};

	execAdd = function() {
		excludes.find(':selected').each(function() {
			includes.append($(this));
		});
		refreshLists();
	};

	execAddAll = function() {
		excludes.find('option').each(function() {
			includes.append($(this));
		});
		refreshLists();
	};

	execRemove = function() {
		includes.find(':selected').each(function() {
			excludes.append($(this));
		});
		refreshLists();
	};

	execRemoveAll = function() {
		includes.find('option').each(function() {
			excludes.append($(this));
		});
		refreshLists();
	};

	// Init DOM

	element = $('<div>')
	excludes = $('<select>');
	excludes.css('float', 'left');
	excludes.prop('multiple', true);
	includes = $('<select>');
	includes.css('float', 'left');
	includes.prop('multiple', true);
	var span = $('<span>');
	span.css('float', 'left');
	var add = $('<button type="button">&gt;</button>');
	add.click(execAdd);
	var addAll = $('<button type="button">&gt;&gt;</button>');
	addAll.click(execAddAll);
	var remove = $('<button type="button">&lt;</button>');
	remove.click(execRemove);
	var removeAll = $('<button type="button">&lt;&lt;</button>');
	removeAll.click(execRemoveAll);
	element.append(excludes);
	element.append(span);
	element.append(includes);
	span.append(add);
	span.append('<br>');
	span.append(addAll);
	span.append('<br>');
	span.append(remove);
	span.append('<br>');
	span.append(removeAll);

	// Define which functions are callable from the outside

	this.setAvailableValues = setAvailableValues;
	this.setIncludes = setIncludes;
	this.setExcludes = setExcludes;
	this.getIncludes = getIncludes;
	this.getExcludes = getExcludes;
	this.getElement = getElement;
	this.refreshSize = refreshSize;

}
