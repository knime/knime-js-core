knime_line_plotter = function() {
	var lineChart = { version: "1.0.0" };
	lineChart.name = "KNIME Line Plot";
	lineChart.extensionNames = [ "hilite" ];
	
	var hiliteHandler;

	var container;
	var margin = {top: 20, right: 20, bottom: 45, left: 50},
	width = 910 - margin.left - margin.right,
	height = 500 - margin.top - margin.bottom;

	var color;
	var valueSet;

	var x;
	var y;

	var xAxis;
	var yAxis;

	var brush;

	var svg;
	var line;
	var valueGroup;

	lineChart.init = function(viewContent, containerID) {
		var knimeTable = new kt;
		var parsedContent = JSON.parse(viewContent);
		knimeTable.setDataTable(parsedContent.table);
		container = "#" + containerID;
		
		hiliteHandler = knimeTable.getExtension("hilite");
		var colNames = knimeTable.getColumnNames();
		var colTypes = knimeTable.getColumnTypes();
		var numberColsIDs = [];
		var numberColNames = [];
		color = d3.scale.category10();
		
		for (var i = 0; i < colTypes.length; i++) {
			if (colTypes[i] === "number") {
				numberColsIDs.push(i);
				numberColNames.push(colNames[i]);
			}
		}
		color.domain(numberColNames);
	
		//generate d3 data
		valueSet = color.domain().map(function(name, i) {
			return {
				name: name,
				values: knimeTable.getColumn(numberColsIDs[i])
			};
		});
		x = d3.scale.linear()
			.domain([0, knimeTable.getNumRows()])
			.nice()
			.range([0, width]);
		var yDomain = [d3.min(valueSet, function(s) { return d3.min(s.values); }), 
		               d3.max(valueSet, function(s) { return d3.max(s.values); })];
		y = d3.scale.linear()
			.domain(yDomain)
			.nice()
		    .range([height, 0]);
		
		xAxis = d3.svg.axis()
	    	.scale(x)
	    	.tickSize(5, 3, 5)
	    	.tickSubdivide(10)
	    	.tickPadding(5)
	    	.orient("bottom");
		
		yAxis = d3.svg.axis()
	    	.scale(y)
	    	.tickSize(5, 3, 0)
	    	.tickPadding(5)
	    	.orient("left");
		
		brush = d3.svg.brush()
			.x(x).y(y)
			.on("brushstart", brushstart)
			.on("brush", brushing)
			.on("brushend", brushend);
		
		svg = d3.select(container).append("svg")
	    	.attr("width", width + margin.left + margin.right)
	    	.attr("height", height + margin.top + margin.bottom)
	    	.append("g")
	    		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
		svg.append("g")
	    	.attr("class", "x axis")
	    	.attr("transform", "translate(0," + height + ")")
	    	.call(xAxis);
	
		svg.append("g")
	    	.attr("class", "y axis")
	    	.call(yAxis);
	
		line = d3.svg.line()
	    	.x(function(d, i) { return x(i); })
	    	.y(function(d) { return y(d); })
	    	.interpolate("linear");
	
		valueGroup = svg.selectAll(".valueSet")
			.data(valueSet)
			.enter().append("g")
				.attr("class", "valueSet");
	
		valueGroup.append("path")
			.attr("class", "line")
			.attr("d", function(d) { return line(d.values); })
			.style("stroke", function(d) { return color(d.name); });
	
		valueGroup.selectAll(".dot")
	    	.data(function(d) { return d.values; })
	    	.enter().append("circle")
	    		.attr("class", "dot")
	    		.attr("r", 3)
				.attr("stroke-width", 3)
	    		.attr("cx", function(d, i) { return x(i); })
	    		.attr("cy", function(d) { return y(d); })
	    		.style("fill", function(d) { return color(this.parentNode.__data__.name); });
		
		svg.call(brush);
		update();
	}

	lineChart.hiliteChangeListener = function(changedRowIDs) {
		update(changedRowIDs);
	}

	lineChart.hiliteClearListener = function() {
		update();
	}

	function brushstart() {
		hiliteHandler.fireClearHilite();
	}

	// Set selected dots.
	function brushing() {
		var e = brush.extent();
		var tempSelection = [];
		svg.selectAll(container + " .valueSet").selectAll(container + " .dot").each(function(d,i) {
			var selected = e[0][0] <= i && i <= e[1][0] && e[0][1] <= d && d <= e[1][1];
			tempSelection[i] |= selected;
		});
		for (var rowIndex = 0; rowIndex < tempSelection.length; rowIndex++) {
			hiliteHandler.setHilited(lineChart.name, rowIndex, tempSelection[rowIndex]);
		}
		/*.attr("class", function(d) {
			return e[0][0] <= d.rowIndex && d.rowIndex <= e[1][0] && e[0][1] <= d.value && d.value <= e[1][1] ? "dot selected" : "dot";
		})*/;
		hiliteHandler.fireHiliteChanged();
	}


	function brushend() {
		if (brush.empty()) 
			hiliteHandler.fireClearHilite();
	}

	function update() {
		svg.selectAll(container + " .valueSet").selectAll(container + " .dot").attr("class", function(d, i) {
			var classString = "dot";
			if (hiliteHandler.isHilited(i)) classString += " highlighted";
			if (hiliteHandler.isSelected(d.rowIndex)) classString += " selected";
			return classString; 
		});
	}

	lineChart.pullViewContent = function(container) {
		// do nothing
	}
	
	return lineChart;
}();