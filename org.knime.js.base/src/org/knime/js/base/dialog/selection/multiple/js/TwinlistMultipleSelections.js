function twinlistMultipleSelections() {
	var list;
	this.getComponent = function() {
		return list.getElement();
	};
	this.setChoices = function(choices) {
		list.setAvailableValues(choices);
	};
	this.getSelections = function() {
		return list.getIncludes();
	};
	this.setSelections = function(selections) {
		list.setIncludes(selections);
	};
	list = new twinlist();
}
