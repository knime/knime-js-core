knime_scatter_plot = function() {
	
	view = {};
	var _representation = null;
	var _value = null;
	var _keyedDataset = null;
	var chart = null;
	
	view.init = function(representation, value) {
		_representation = representation;
		_value = value;
		try {
			_keyedDataset = new noname.KeyedValues3DDataset();
			//_keyedDataset.load(_representation.keyedDataset);
			var seriesKey = _representation.keyedDataset.series[0].seriesKey;
			for (var rowIndex = 0; rowIndex < _representation.keyedDataset.rowKeys.length; rowIndex++) {
				var rowKey = _representation.keyedDataset.rowKeys[rowIndex];
				var row = _representation.keyedDataset.series[0].rows[rowIndex];
				for (var col = 0; col < _representation.keyedDataset.columnKeys.length; col++) {
					var columnKey = _representation.keyedDataset.columnKeys[col];
					_keyedDataset.add(seriesKey, rowKey, columnKey, row.values[col]);
					var properties = row.properties;
					for (var propertyKey in properties) {
						_keyedDataset.setProperty(seriesKey, rowKey, columnKey, propertyKey, properties[propertyKey]);
					}
				}
			}
			
			drawChart();
			if (_representation.allowViewConfiguration) {
				drawControls();
			}
		} catch(err) {
			if (err.stack) {
				alert(err.stack);
			} else {
				alert (err);
			}
		}
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.autoResize(window.frameElement.id);
		}
	};
	
	buildXYDataset = function() {
		var xyDataset = noname.DatasetUtils.extractXYDatasetFromColumns(_keyedDataset, _value.xColumn, _value.yColumn);
		return xyDataset;
	};
	
	drawChart = function() {
		if (!_value.xColumn) {
			alert("No column set for x axis!");
			return;
		}
		if (!_value.yColumn) {
			alert("No column set for y axis!");
			return;
		}
		if (!_value.xAxisLabel) {
			_value.xAxisLabel = _value.xColumn;
		}
		if (!_value.yAxisLabel) {
			_value.yAxisLabel = _value.yColumn;
		}
		var dataset = buildXYDataset();
		chart = noname.Charts.createScatterChart(dataset, _value.xAxisLabel, _value.yAxisLabel);
		var container = "scatterContainer";
		//chart.plot.renderer.toolTipHandler = xyToolTipHandler;
		d3.select("html").style("width", "100%").style("height", "100%").style("overflow", "hidden");
		d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");
		var chartHeight = _representation.allowViewConfiguration ? "80%" : "100%";
		d3.select("body").attr("id", "body").append("div").attr("id", container).style("width", "100%").style("height", chartHeight).style("box-sizing", "border-box");
		
		chart.width(Math.max(400, document.getElementById(container).clientWidth));
		chart.height(Math.max(300, document.getElementById(container).clientHeight));
		chart.build(container);
	};
	
	updateChart = function() {
		var plot = chart.plot();
		plot.setDataset(buildXYDataset());
		plot.autoCalcBounds();
		plot.update(chart);
	};
	
	drawControls = function() {
		/*allowViewConfiguration;
	    enableXColumnChange;
	    enableYColumnChange;
	    enableXAxisLabelEdit;
	    enableYAxisLabelEdit;
	    allowDotSizeChange;
	    allowZooming;
	    allowPanning;*/
	    var controlContainer = d3.select("body").append("table")
	    	.attr("id", "scatterControls")
	    	/*.style("width", "100%")*/
	    	.style("padding", "10px")
	    	.style("margin", "0 auto")
	    	.style("box-sizing", "border-box");
	    
	    if (_representation.enableXColumnChange || _representation.enableYColumnChange) {
	    	var columnChangeContainer = controlContainer.append("tr")/*.style("margin", "5px auto").style("display", "table")*/;
	    	if (_representation.enableXColumnChange) {
	    		columnChangeContainer.append("td").append("label").attr("for", "xColumnSelect").text("X Column:").style("margin-right", "5px");
	    		var xSelect = columnChangeContainer.append("td").append("select").attr("id", "xColumnSelect").attr("name", "xColumnSelect");
	    		var columnKeys = _keyedDataset.columnKeys();
	    		for (var colID = 0; colID < columnKeys.length; colID++) {
	    			xSelect.append("option").attr("value", columnKeys[colID]).text(columnKeys[colID]);
	    		}
	    		document.getElementById("xColumnSelect").value = _value.xColumn;
	    		xSelect.on("change", function() {
	    			_value.xColumn = document.getElementById("xColumnSelect").value;
	    			updateChart();
	    		});
	    		if (_representation.enableYColumnChange) {
	    			xSelect.style("margin-right", "10px");
	    		}
	    	}
	    	if (_representation.enableYColumnChange) {
	    		columnChangeContainer.append("td").append("label").attr("for", "yColumnSelect").text("Y Column:").style("margin-right", "5px");
	    		var ySelect = columnChangeContainer.append("td").append("select").attr("id", "yColumnSelect").attr("name", "yColumnSelect");
	    		var columnKeys = _keyedDataset.columnKeys();
	    		for (var colID = 0; colID < columnKeys.length; colID++) {
	    			ySelect.append("option").attr("value", columnKeys[colID]).text(columnKeys[colID]);
	    		}
	    		document.getElementById("yColumnSelect").value = _value.yColumn;
	    		ySelect.on("change", function() {
	    			_value.yColumn = document.getElementById("yColumnSelect").value;
	    			updateChart();
	    		});
	    	}
	    }
	    if (_representation.enableXAxisLabelEdit || _representation.enableYAxisLabelEdit) {
	    	var axisLabelContainer = controlContainer.append("tr")/*.style("margin", "5px auto").style("display", "table")*/;
	    	if (_representation.enableXAxisLabelEdit) {
	    		axisLabelContainer.append("td").append("label").attr("for", "xAxisText").text("X Axis Label:").style("margin-right", "5px");
	    		var xAxisText = axisLabelContainer.append("td").append("input").attr("type", "text").attr("id", "xAxisText").attr("name", "xAxisText")
	    		.on("blur", function() {
	    			_value.xAxisLabel = document.getElementById("xAxisText").value;
	    			updateChart();
	    		});
	    		if (_representation.enableYAxisLabelEdit) {
	    			xAxisText.style("margin-right", "10px");
	    		}
	    		document.getElementById("xAxisText").value = _value.xAxisLabel;
	    	}
	    	if (_representation.enableYAxisLabelEdit) {
	    		axisLabelContainer.append("td").append("label").attr("for", "yAxisText").text("Y Axis Label:").style("margin-right", "5px");
	    		axisLabelContainer.append("td").append("input").attr("type", "text").attr("id", "yAxisText").attr("name", "yAxisText")
	    		.on("blur", function() {
	    			_value.yAxisLabel = document.getElementById("yAxisText").value;
	    			updateChart();
	    		});
	    		document.getElementById("yAxisText").value = _value.yAxisLabel;
	    	}
	    }
	    if (_representation.allowDotSizeChange) {
	    	var dotSizeContainer = controlContainer.append("tr")/*.style("margin", "5px auto").style("display", "table")*/;
	    	dotSizeContainer.append("td").append("label").attr("for", "dotSizeInput").text("Dot Size:").style("margin-right", "5px");
	    	dotSizeContainer.append("td").append("input").attr("type", "number").attr("id", "dotSizeInput").attr("name", "dotSizeInput").attr("value", _value.dotSize);
	    }
	};
	
	view.validate = function() {
		return true;
	};
	
	view.getComponentValue = function() {
		return _value;
	};	
	
	return view;
}();