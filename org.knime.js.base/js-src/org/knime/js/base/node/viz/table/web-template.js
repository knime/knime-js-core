knime_table_viewer = function() {
	
	var table_viewer = {};
	var table_view = null;
	
	table_viewer.init = function(representation, value) {
		var body = $('body');
		if (!representation.table) {
			body.append("Error: No data available");
		} else {
			try {
				var knimeTable = new kt();
				knimeTable.setDataTable(representation.table);
				table_view = new knime_table_view(knimeTable, body);
				table_view.setShowRowKeys(true);
				if (representation.numberFormatter) {
					var f = representation.numberFormatter;
					table_view.setNumberFormatter(f.decimalPlaces, f.decimalSeparator, f.thousandsSeparator);
				}
				table_view.draw();
			} catch (err) {
				if (err.stack) {
					alert(err.stack);
				} else {
					alert (err);
				}
			}
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