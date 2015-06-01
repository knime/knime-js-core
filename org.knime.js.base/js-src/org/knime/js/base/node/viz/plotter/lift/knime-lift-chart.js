knime_lift_chart = function() {
    view = {};
    var _representation = null;
    var _value = null;
    var containerID = "lineContainer";
    
    var minWidth = 400;
    var minHeight = 300;
    var defaultFont = "sans-serif";
    var defaultFontSize = 12;
    var xy = {};
    var legendHeight = 0;
    var _maxY = 0;
        
    view.init = function(representation, value) {
        _value = value;
        _representation = representation;
        
        d3.select("html").style("width", "100%").style("height", "100%")/*.style("overflow", "hidden")*/;
            d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");
            var layoutContainer = "layoutContainer";
            d3.select("body").attr("id", "body").append("div").attr("id", layoutContainer)
                .style("width", "100%").style("height", "100%")
                .style("min-width", minWidth + "px").style("min-height", (minHeight + getControlHeight()) + "px");
            
        xy["Lift"] = {color : "red", data : []};
        xy["Cumulative Lift"] = {color : "blue", data : []};  
        xy["Baseline"] = {color : "lime", data : [{x : representation.intervalWidth, y : representation.baseline},
                                                  {x : 100, y : representation.baseline}]};
        
        _maxY = representation.baseline;
        for (var i = 0; i < representation.liftValues.length; i++) {
            var x = (i + 1) * representation.intervalWidth;
            var y = representation.liftValues[i];
            xy["Lift"].data.push({x : x, y : y});
        }
        for (var i = 0; i < representation.cumulativeLift.length; i++) {
            var x = (i + 1) * representation.intervalWidth;
            var y = representation.cumulativeLift[i];
            if (y > _maxY) {
                _maxY = y;
            }
            xy["Cumulative Lift"].data.push({x : x, y : y});
        }
        
        drawChart(layoutContainer);
        if (parent != undefined && parent.KnimePageLoader != undefined) {
            parent.KnimePageLoader.autoResize(window.frameElement.id);
        }
    }
    
    view.getSVG = function() {
        var svg = d3.select("svg")[0][0];
        return (new XMLSerializer()).serializeToString(svg);
    };
    
    
    function drawChart(layoutContainer) {
        var cw = Math.max(minWidth, _representation.imageWidth);
        var ch = Math.max(minHeight, _representation.imageHeight);
        var chartWidth = cw + "px;"
        var chartHeight = ch + "px";

        if (_representation.resizeToWindow) {
            chartWidth = "100%";
            chartHeight = "calc(100% - " + getControlHeight() + "px)";
        }

        var lc = d3.select("#"+layoutContainer);
        lc.selectAll("*").remove();
        
        var div = lc.append("div")
            .attr("id", containerID)
            .style("min-width", minWidth + "px")
            .style("min-height", minHeight + "px")
            .style("box-sizing", "border-box")
            .style("display", "inline-block")
            .style("overflow", "hidden")
            .style("margin", "0")
            .style("height", chartHeight)
            .style("width", chartWidth);
        
        var margin = {top : 10, left : 70, bottom : legendHeight + 10, right : 20};
        var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        document.getElementById(containerID).appendChild(svg1);
        
        var d3svg = d3.select(svg1);

        var svg = d3svg.attr({width : cw, height : ch}).style({width : chartWidth, height : chartHeight})
            .append("g").attr("transform", 
              "translate(" + margin.left + "," + margin.top + ")");

        var w = parseInt(d3svg.style('width')) - margin.left - margin.right;
        var h = parseInt(d3svg.style('height')) - margin.top - margin.bottom;
        
        var bg = parseColor(_representation.backgroundColor);
        var areaColor = parseColor(_representation.dataAreaColor);

        svg.append("rect").attr({fill : bg.rgb, "fill-opacity" : bg.opacity, width : margin.left + w + margin.right,
                                    height : margin.top + margin.bottom + h, x : -margin.left, y : -margin.top});
        svg.append("rect").attr({fill : areaColor.rgb, "fill-opacity" : areaColor.opacity, width : w, height : h});
                    
        var x = d3.scale.linear().domain([0, 100]).range([0, w]);
        var y = d3.scale.linear().domain([0, _maxY]).nice().range([h, 0]);

        var xAxis, yAxis;

        // Define the axes
        if (!_representation.showGrid) {
            xAxis = d3.svg.axis().scale(x)
                .orient("bottom").ticks(5);
            
            yAxis = d3.svg.axis().scale(y)
                .orient("left").ticks(5);
        } else {

            xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom")
                .tickSize(-h, 0)
                .tickPadding(10);
        
            yAxis = d3.svg.axis()
                .scale(y)
                .orient("left")
                .tickSize(-w, 0)
                .tickPadding(10);
        }
        
        var valueline = d3.svg.line()
        .x(function(d) { return x(d.x); })
        .y(function(d) { return y(d.y); });
        
        // Add the X Axis
        var d3XAxis = svg.append("g");
            d3XAxis.attr("class", "x axis")
            .attr("transform", "translate(0," + h + ")")
            .call(xAxis);
    
        // Add the Y Axis
        var d3YAxis = svg.append("g");
            d3YAxis.attr("class", "y axis")
            .call(yAxis);
        
        svg.append("text")
            .attr("class", "x label")
            .attr("text-anchor", "end")
            .attr("x", w - 10)
            .attr("y", h + 45)
            .text("Percentage");
            
        svg.append("text")
            .attr("class", "y label")
            .attr("text-anchor", "end")
            .attr("y", -55)
            .attr("dy", ".75em")
            .attr("transform", "rotate(-90)")
            .text("Lift");
        
        var gridColor = parseColor(_representation.gridColor);
        var stroke = _representation.showGrid ? gridColor.rgb : "#000";
        
        d3YAxis.selectAll("line").attr("stroke", stroke);
        d3XAxis.selectAll("line").attr("stroke", stroke);
        d3YAxis.selectAll("path").attr({"stroke" : stroke, "stroke-width" : 1, "fill" : "none"});
        d3XAxis.selectAll("path").attr({"stroke" : stroke, "stroke-width" : 1, "fill" : "none"});
        
        var xPos = 0;
        var yPos = 70;
        var areaG = svg.append("g");
        var areaCount = 0;
        var maxWidth = 0;
        
        // Add the valueline path.
        for (var key in xy) {
            var p = svg.append("path")
            .attr("class", "line")
            .style({stroke : xy[key].color, fill : "none", "stroke-width" : _representation.lineWidth})
            .attr("d", valueline(xy[key].data));
            
            var g = svg.append("g").attr("transform", "translate(" + xPos + "," + (h + yPos) + ")");
            var l = g.append("text").attr({x : 20}).text(key);
            g.append("circle").attr({"r" : 5, "fill" : xy[key].color, cx : 5, cy : -5});
            xPos += parseInt(l.style("width")) + 20;
            
            if (xPos > w) {
                yPos += 25;
                xPos = 0;
                g.attr("transform", "translate(" + xPos + "," + (h + yPos) + ")");
                xPos += parseInt(l.style("width")) + 30;
            } else {
                xPos += 10;
            }
        }
        
        areaG.attr("transform", "translate(" + (w - maxWidth - 10) + "," + (h - areaCount * 25 + margin.top) + ")");
        
        if (legendHeight == 0) {
            legendHeight = Math.max(yPos, 75);
            drawChart("layoutContainer");
        }

        if (_representation.resizeToWindow) {
            var win = document.defaultView || document.parentWindow;
            win.onresize = resize;
        }
    }
    
    function parseColor(col) {
       var COLOR_REGEX = /rgba\(([0-9]{1,3}),([0-9]{1,3}),([0-9]{1,3}),([0-9]\.[0-9])\)/g;
       var match = COLOR_REGEX.exec(col), rgb, opacity;
       if (match) {
           rgb = "rgb(" + match[1] + "," + match[2] + "," + match[3] + ")";
           opacity = match[4];
       } else {
           rgb = col;
           opacity = "1.0";
       }
       return {rgb : rgb, opacity : opacity};
    }
    
    function resize(event) {
       legendHeight = 0;
        drawChart("layoutContainer");
    };
    
    getControlHeight = function() {
        var height = rows = 0;
        var sizeFactor = 25;
        var padding = 10;
        if (height > 0) height += padding;
        return height;
    };
    
    view.validate = function() {
        return true;
    };
    
    view.getComponentValue = function() {
        return _value;
    };

    return view;
}();