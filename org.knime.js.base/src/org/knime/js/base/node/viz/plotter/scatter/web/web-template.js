knime_scatter_plotter = function() {
	var scatterPlot = { version: "1.0.0" };
	scatterPlot.name = "KNIME Scatter Plot";
	scatterPlot.extensionNames = [ "hilite" ];
	
	var hiliteHandler;

	var container;
	var margin = {top: 20, right: 20, bottom: 45, left: 50},
	width = 860 - margin.left - margin.right,
	height = 500 - margin.top - margin.bottom;

	var color;
	var valueSet;

	var x;
	var y;

	var xAxis;
	var yAxis;

	var brush;

	var svg;
	var viewValue;

	scatterPlot.init = function(representation, value) {
		if (representation.table == null) {
			document.body.innerHTML = "Error: No data available";
		} else {
			viewValue = value;
			var knimeTable = new kt;
			knimeTable.setDataTable(representation.table);
			knimeTable.registerView(scatterPlot);
			container = "body";
			
			hiliteHandler = knimeTable.getExtension("hilite");
			var colNames = knimeTable.getColumnNames();
			var colTypes = knimeTable.getColumnTypes();
			var possibleValues = knimeTable.getPossibleValues();
			var numberColsIDs = [];
			var numberColNames = [];
			var stringColIDs = [];
			var stringColNames = [];
			var classDomain = [];
			var classColID;
			color = d3.scale.category10();
			
			for (var i = 0; i < colTypes.length; i++) {
				if (colTypes[i] === "number") {
					numberColsIDs.push(i);
					numberColNames.push(colNames[i]);
				} else if (colTypes[i] === "string"){
					stringColIDs.push(i);
					stringColNames.push(colNames[i]);
				}
			}
			for (var i = 0; i < stringColIDs.length; i++) {
				var classes = possibleValues[stringColIDs[i]];
				if (classes != null && classes != undefined) {
					classColID = stringColIDs[i];
					classDomain = classes;
					break;
				}
			}
			color.domain(classDomain);
			
			var xColumn = [];
			var yColumn = [];
			var classColumn = [];
			xColumn = knimeTable.getColumn(numberColsIDs[0]);
			yColumn = knimeTable.getColumn(numberColsIDs[1]);
			classColumn = knimeTable.getColumn(classColID);
		
			//generate d3 data
			valueSet = new Array();
			for (var i = 0; i < knimeTable.getNumRows(); i++) {
				var dataEntry = new Object();
				dataEntry.xValue = xColumn[i];
				dataEntry.yValue = yColumn[i];
				dataEntry.classValue = classColumn[i];
				valueSet.push(dataEntry);
			}
			
			x = d3.scale.linear()
				.domain(d3.extent(valueSet, function(d) { return d.xValue; }))
				.nice()
				.range([0, width]);
			
			/*var yDomain = [d3.min(valueSet, function(s) { return d3.min(s.values); }), 
			               d3.max(valueSet, function(s) { return d3.max(s.values); })];*/
			y = d3.scale.linear()
				.domain(d3.extent(valueSet, function(d) { return d.yValue; }))
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
		    	.call(xAxis)
		    .append("text")
		    	.attr("class", "label")
		    	.attr("x", width)
		    	.attr("y", -6)
		    	.style("text-anchor", "end")
		    	.text(numberColNames[0]);
		
			svg.append("g")
		    	.attr("class", "y axis")
		    	.call(yAxis)
		    .append("text")
		    	.attr("class", "label")
		    	.attr("transform", "rotate(-90)")
		    	.attr("y", 6)
		    	.attr("dy", ".71em")
		    	.style("text-anchor", "end")
		    	.text(numberColNames[1]);
			
			svg.selectAll(".dot")
				.data(valueSet)
			.enter().append("circle")
				.attr("class", "dot")
				.attr("r", 3)
				.attr("cx", function(d) { return x(d.xValue); })
			    .attr("cy", function(d) { return y(d.yValue); })
			    .style("fill", function(d) { return color(d.classValue); });
			
			var legend = svg.selectAll(".legend")
				.data(color.domain())
			.enter().append("g")
				.attr("class", "legend")
				.attr("transform", function(d, i) {return "translate(0," + i*20 + ")"; });
			
			legend.append("rect")
		      .attr("x", width - 18)
		      .attr("width", 18)
		      .attr("height", 18)
		      .style("fill", color);
			
			legend.append("text")
		      .attr("x", width - 24)
		      .attr("y", 9)
		      .attr("dy", ".35em")
		      .style("text-anchor", "end")
		      .text(function(d) { return d; });
		
			svg.call(brush);
			update();
		}
		callUpdate();
		resize();
	};

	scatterPlot.hiliteChangeListener = function(changedRowIDs) {
		update(changedRowIDs);
	};

	scatterPlot.hiliteClearListener = function() {
		update();
	};

	function brushstart() {
		hiliteHandler.fireClearHilite();
	}

	// Set selected dots.
	function brushing() {
		var e = brush.extent();
		var tempSelection = [];
		svg.selectAll(container + " .dot").each(function(d,i) {
			var selected = e[0][0] <= d.xValue && d.xValue <= e[1][0] && e[0][1] <= d.yValue && d.yValue <= e[1][1];
			tempSelection[i] = selected;
		});
		for (var rowIndex = 0; rowIndex < tempSelection.length; rowIndex++) {
			hiliteHandler.setHilited(scatterPlot.name, rowIndex, tempSelection[rowIndex]);
		}
		/*.attr("class", function(d) {
			return e[0][0] <= d.rowIndex && d.rowIndex <= e[1][0] && e[0][1] <= d.value && d.value <= e[1][1] ? "dot selected" : "dot";
		})*/;
		hiliteHandler.fireHiliteChanged();
		callUpdate();
	}


	function brushend() {
		if (brush.empty()) 
			hiliteHandler.fireClearHilite();
	}

	function update() {
		svg.selectAll(container + " .dot").attr("class", function(d, i) {
			var classString = "dot";
			if (hiliteHandler.isHilited(i)) classString += " highlighted";
			//if (hiliteHandler.isSelected(d.rowIndex)) classString += " selected";
			return classString; 
		});
	}

	scatterPlot.validate = function() {
		return true;
	};

	scatterPlot.getComponentValue = function() {
		viewValue.selections = new Array();
		for (var i = 0; i < valueSet.length; i++) {
			if (hiliteHandler.isHilited(i)) {
				viewValue.selections.push(i);
			}
		}
		return viewValue;
	};
	
	callUpdate = function() {
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.getPageValues();
		}
	};
	
	resize = function() {
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.autoResize(window.frameElement.id);
		}
	};
	
	return scatterPlot;
}();