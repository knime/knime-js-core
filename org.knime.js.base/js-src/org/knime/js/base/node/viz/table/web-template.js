knime_table_viewer = function() {
	
	var table_viewer = {};
	var table_view = null;
	
	table_viewer.init = function(representation, value) {
		var body = $('body');
		if (representation.table == null) {
			body.append("Error: No data available");
		} else {
			var knimeTable = new kt();
			knimeTable.setDataTable(representation.table);
			table_view = new knime_table_view(knimeTable, body);
			table_view.setShowRowKeys(true);
			table_view.draw();
		}
	};
	
	table_viewer.validate = function() {
		return true;
	};
	
	table_viewer.getComponentValue = function() {
		return null;
	};
	
	return table_viewer;
	
}();