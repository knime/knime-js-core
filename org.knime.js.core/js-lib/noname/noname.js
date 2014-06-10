var noname;
if (!noname) {
  noname = {};
}
noname.Charts = {};
noname.Charts.createTitleElement = function(title, subtitle, anchor) {
  noname.Args.requireString(title);
  var titleFont = new noname.Font("Palatino, serif", 16, true, false);
  var halign = noname.HAlign.LEFT;
  var refPt = anchor ? anchor.refPt() : noname.RefPt2D.TOP_LEFT;
  if (noname.RefPt2D.isHorizontalCenter(refPt)) {
    halign = noname.HAlign.CENTER;
  } else {
    if (noname.RefPt2D.isRight(refPt)) {
      halign = noname.HAlign.RIGHT;
    }
  }
  var titleElement = (new noname.TextElement(title)).font(titleFont).halign(halign);
  if (subtitle) {
    var subtitleFont = new noname.Font("Palatino, serif", 12, false, true);
    var subtitleElement = (new noname.TextElement(subtitle)).font(subtitleFont).halign(halign);
    var composite = new noname.GridElement;
    composite.add(titleElement, "R1", "C1");
    composite.add(subtitleElement, "R2", "C1");
    return composite;
  } else {
    return new noname.TextElement(title);
  }
};
noname.Charts.createPieChart = function(title, subtitle, dataset) {
  noname.Args.requireKeyedValuesDataset(dataset, "dataset");
  var plot = new noname.PiePlot(dataset);
  var chart = new noname.Chart(plot);
  chart.title(noname.Charts.createTitleElement(title, subtitle, chart.titleAnchor()));
  return chart;
};
noname.Charts.createBarChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  noname.Args.requireKeyedValues2DDataset(dataset, "dataset");
  var plot = new noname.CategoryPlot(dataset);
  plot.xAxis.label = xAxisLabel;
  plot.yAxis.label = yAxisLabel;
  plot.renderer = new noname.BarRenderer;
  var chart = new noname.Chart(plot);
  chart.initMargin({top:5, right:5, left:30, bottom:20});
  chart.title(noname.Charts.createTitleElement(title, subtitle, chart.titleAnchor()));
  return chart;
};
noname.Charts.createStackedBarChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  noname.Args.requireKeyedValues2DDataset(dataset, "dataset");
  var plot = new noname.CategoryPlot(dataset);
  plot.xAxis.label = xAxisLabel;
  plot.yAxis.label = yAxisLabel;
  plot.renderer = new noname.StackedBarRenderer;
  var chart = new noname.Chart(plot);
  chart.initMargin({top:5, right:5, left:30, bottom:20});
  chart.title(noname.Charts.createTitleElement(title, subtitle, chart.titleAnchor()));
  return chart;
};
noname.Charts.createStackedAreaChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  noname.Args.requireKeyedValues2DDataset(dataset, "dataset");
  var plot = new noname.CategoryPlot(dataset);
  plot.xAxis.label = xAxisLabel;
  plot.yAxis.label = yAxisLabel;
  plot.renderer = new noname.StackedAreaRenderer;
  var chart = new noname.Chart(plot);
  chart.initMargin({top:5, right:5, left:40, bottom:50});
  chart.title(noname.Charts.createTitleElement(title, subtitle, chart.titleAnchor()));
  return chart;
};
noname.Charts.createLineChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  noname.Args.requireKeyedValues2DDataset(dataset, "dataset");
  var plot = new noname.CategoryPlot(dataset);
  plot.xAxis.label = xAxisLabel;
  plot.yAxis.label = yAxisLabel;
  plot.renderer = new noname.LineRenderer;
  var chart = new noname.Chart(plot);
  chart.initMargin({top:5, right:5, left:30, bottom:20});
  chart.title(noname.Charts.createTitleElement(title, subtitle, chart.titleAnchor()));
  return chart;
};
noname.Charts.createScatterChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  noname.Args.requireXYDataset(dataset, "dataset");
  var plot = new noname.XYPlot(dataset);
  plot.getXAxis().setLabel(xAxisLabel);
  plot.getYAxis().setLabel(yAxisLabel);
  plot.renderer = new noname.ScatterRenderer(plot);
  var chart = new noname.Chart(plot);
  chart.setPadding(5, 5, 5, 5);
  chart.setTitle(title, subtitle, chart.getTitleAnchor());
  return chart;
};
noname.Charts.createXYLineChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  noname.Args.requireXYDataset(dataset, "dataset");
  var plot = new noname.XYPlot(dataset);
  plot.xAxis.label = xAxisLabel;
  plot.yAxis.label = yAxisLabel;
  var renderer = new noname.XYLineRenderer;
  var chart = new noname.Chart(plot);
  chart.initMargin({top:5, right:5, left:30, bottom:20});
  chart.title(noname.Charts.createTitleElement(title, subtitle, chart.titleAnchor()));
  plot.setRenderer(renderer);
  return chart;
};
noname.Charts.createHistogram = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  var plot = new noname.XYPlot(dataset);
  plot.xAxis.label = xAxisLabel;
  plot.yAxis.label = yAxisLabel;
  var renderer = new noname.HistogramRenderer;
  var chart = noname.Chart(plot);
  plot.setRenderer(renderer);
  return chart;
};
noname.Charts.createXYBarChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  var plot = new noname.XYPlot(dataset);
  plot.getXAxis().setLabel(xAxisLabel);
  plot.getYAxis().setLabel(yAxisLabel);
  var renderer = new noname.XYBarRenderer;
  plot.setRenderer(renderer);
  var chart = new noname.Chart(plot);
  var titleAnchor = new noname.Anchor2D(noname.RefPt2D.TOP_LEFT);
  chart.setTitle(title, subtitle, titleAnchor);
  return chart;
};
noname.Chart = function(plot) {
  if (!(this instanceof noname.Chart)) {
    throw new Error("Use 'new' for construction.");
  }
  this._elementId;
  this._size = new noname.Dimension(400, 240);
  var white = new noname.Color(255, 255, 255);
  this._backgroundPainter = new noname.StandardRectanglePainter(white, null);
  this._padding = new noname.Insets(4, 4, 4, 4);
  this._titleElement = null;
  this._titleAnchor = new noname.Anchor2D(noname.RefPt2D.TOP_LEFT);
  this._plot = plot;
  this._legendBuilder = new noname.StandardLegendBuilder;
  this._legendAnchor = new noname.Anchor2D(noname.RefPt2D.BOTTOM_RIGHT);
  this._listeners = [];
  var plotListener = function(c) {
    var chart = c;
    return function(plot) {
      chart.notifyListeners();
    };
  }(this);
  plot.addListener(plotListener);
  plot.chart = this;
};
noname.Chart.prototype.getElementID = function() {
  return this._elementId;
};
noname.Chart.prototype.setElementID = function(id) {
  this._elementId = id;
};
noname.Chart.prototype.getSize = function() {
  return this._size;
};
noname.Chart.prototype.setSize = function(width, height, notify) {
  this._size = new noname.Dimension(width, height);
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.Chart.prototype.getBackground = function() {
  return this._backgroundPainter;
};
noname.Chart.prototype.setBackground = function(painter, notify) {
  this._backgroundPainter = painter;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.Chart.prototype.setBackgroundColor = function(color, notify) {
  var painter = new noname.StandardRectanglePainter(color);
  this.setBackground(painter, notify);
};
noname.Chart.prototype.getPadding = function() {
  return this._padding;
};
noname.Chart.prototype.setPadding = function(top, left, bottom, right, notify) {
  this._padding = new noname.Insets(top, left, bottom, right);
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.Chart.prototype.getTitleElement = function() {
  return this._titleElement;
};
noname.Chart.prototype.setTitleElement = function(title, notify) {
  this._title = title;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.Chart.prototype.setTitle = function(title, subtitle, anchor, notify) {
  var title = noname.Charts.createTitleElement(title, subtitle, anchor);
  this.setTitleElement(title, notify);
};
noname.Chart.prototype.updateTitle = function(title, font, color) {
};
noname.Chart.prototype.updateSubtitle = function(subtitle, font, color) {
};
noname.Chart.prototype.getTitleAnchor = function() {
  return this._titleAnchor;
};
noname.Chart.prototype.setTitleAnchor = function(anchor, notify) {
  this._titleAnchor = anchor;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.Chart.prototype.getPlot = function() {
  return this._plot;
};
noname.Chart.prototype.getLegendBuilder = function() {
  return this._legendBuilder;
};
noname.Chart.prototype.setLegendBuilder = function(builder, notify) {
  this._legendBuilder = builder;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.Chart.prototype.getLegendAnchor = function() {
  return this._legendAnchor;
};
noname.Chart.prototype.setLegendAnchor = function(anchor, notify) {
  this._legendAnchor = anchor;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.Chart.prototype._adjustMargin = function(margin, dim, anchor) {
  if (noname.RefPt2D.isTop(anchor.refPt())) {
    margin.top += dim.height();
  } else {
    if (noname.RefPt2D.isBottom(anchor.refPt())) {
      margin.bottom += dim.height();
    }
  }
};
noname.Chart.prototype.draw = function(ctx, bounds) {
  if (this._backgroundPainter) {
    this._backgroundPainter.paint(ctx, bounds);
  }
  var titleDim = new noname.Dimension(0, 0);
  var legendDim = new noname.Dimension(0, 0);
  if (this._title) {
    titleDim = this._title.preferredSize(ctx, bounds);
  }
  var legend;
  if (this._legendBuilder) {
    legend = this._legendBuilder.createLegend(this._plot, "anchor", "orientation", "style");
    legendDim = legend.preferredSize(ctx, bounds);
  }
  var padding = this.getPadding();
  var px = padding.left();
  var py = padding.top() + titleDim.height();
  var pw = this._size.width() - padding.left() - padding.right();
  var ph = this._size.height() - padding.top() - padding.bottom() - titleDim.height() - legendDim.height();
  this._plotArea = new noname.Rectangle(px, py, pw, ph);
  this._plot.draw(ctx, bounds, this._plotArea);
  if (legend) {
    var fitter = new noname.Fit2D(this._legendAnchor);
    var dest = fitter.fit(legendDim, bounds);
    legend.draw(ctx, dest);
  }
  if (this._title) {
    var fitter = new noname.Fit2D(this._titleAnchor);
    var dest = fitter.fit(titleDim, bounds);
    this._title.draw(ctx, dest);
  }
};
noname.Chart.prototype.plotArea = function() {
  return this._plotArea;
};
noname.Chart.prototype.addListener = function(f) {
  this._listeners.push(f);
};
noname.Chart.prototype.notifyListeners = function() {
  var chart = this;
  this._listeners.forEach(function(f) {
    f(chart);
  });
};
noname.Chart.prototype.title = function(t) {
  throw new Error("Use get/setTitle()");if (!arguments.length) {
    return this._title;
  }
  this._title = t;
  return this;
};
noname.Chart.prototype.titleAnchor = function(anchor) {
  throw new Error("Use get/setTitleAnchor.");if (!arguments.length) {
    return this._titleAnchor;
  }
  this._titleAnchor = anchor;
  return this;
};
noname.Chart.prototype.plot = function(obj) {
  throw new Error("This method is replaced by getPlot().");if (!arguments.length) {
    return this._plot;
  }
  this._plot = obj;
  return this;
};
noname.Chart.prototype.width = function(value) {
  throw new Error("Use get/setSize() instead.");if (!arguments.length) {
    return this._width;
  }
  this._width = value;
  return this;
};
noname.Chart.prototype.height = function(value) {
  throw new Error("Use get/setSize() instead.");if (!arguments.length) {
    return this._height;
  }
  this._height = value;
  return this;
};
noname.Chart.prototype.initMargin = function(obj) {
  throw new Error("Use get/setPadding() instead.");if (!arguments.length) {
    return this._initMargin;
  }
  this._initMargin = obj;
  return this;
};
noname.Chart.prototype.elementId = function(str) {
  throw new Error("Use get/setElementID()");if (!arguments.length) {
    return this._elementId;
  }
  this._elementId = str;
  return this;
};
noname.ChartManager = function(element, chart) {
  if (!(this instanceof noname.ChartManager)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._element = element;
  this._chart = chart;
  this._ctx = new noname.SVGContext2D(element);
  var chartListener = function(c) {
    var manager = c;
    return function(chart) {
      manager.refreshDisplay();
    };
  }(this);
  chart.addListener(chartListener);
  var zoomHandler = new noname.ZoomHandler(this);
  var panHandler = new noname.PanHandler(this);
  this._liveMouseHandler = panHandler;
  this._availableLiveMouseHandlers = [panHandler, zoomHandler];
  this._auxiliaryMouseHandlers = [new noname.LogEventHandler, new noname.WheelHandler(this)];
  this.installMouseDownHandler(this._element);
  this.installMouseMoveHandler(this._element);
  this.installMouseUpHandler(this._element);
  this.installMouseOverHandler(this._element);
  this.installMouseOutHandler(this._element);
  this.installMouseWheelHandler(this._element);
};
noname.ChartManager.prototype.getChart = function() {
  return this._chart;
};
noname.ChartManager.prototype.refreshDisplay = function() {
  var size = this._chart.getSize();
  var bounds = new noname.Rectangle(0, 0, size.width(), size.height());
  this._ctx.clear();
  this._chart.draw(this._ctx, bounds);
};
noname.ChartManager.prototype.installMouseDownHandler = function(element) {
  var my = this;
  element.onmousedown = function(event) {
    if (my._liveMouseHandler !== null) {
      my._liveMouseHandler.mouseDown(event);
    } else {
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      h.mouseDown(event);
    });
  };
};
noname.ChartManager.prototype.installMouseMoveHandler = function(element) {
  var my = this;
  element.onmousemove = function(event) {
    if (my._liveMouseHandler !== null) {
      my._liveMouseHandler.mouseMove(event);
    } else {
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      h.mouseMove(event);
    });
  };
};
noname.ChartManager.prototype.installMouseUpHandler = function(element) {
  var my = this;
  element.onmouseup = function(event) {
    if (my._liveMouseHandler !== null) {
      my._liveMouseHandler.mouseUp(event);
    } else {
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      h.mouseUp(event);
    });
  };
};
noname.ChartManager.prototype.installMouseOverHandler = function(element) {
  var my = this;
  element.onmouseover = function(event) {
    if (my._liveMouseHandler !== null) {
      my._liveMouseHandler.mouseOver(event);
    } else {
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      h.mouseOver(event);
    });
  };
};
noname.ChartManager.prototype.installMouseOutHandler = function(element) {
  var my = this;
  element.onmouseout = function(event) {
    if (my._liveMouseHandler !== null) {
      my._liveMouseHandler.mouseOut(event);
    } else {
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      h.mouseOut(event);
    });
  };
};
noname.ChartManager.prototype.installMouseWheelHandler = function(element) {
  var my = this;
  var linkFunction = function(event) {
    var propogate = true;
    if (my._liveMouseHandler !== null) {
      propogate = my._liveMouseHandler.mouseWheel(event);
    } else {
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      propogate = h.mouseWheel(event) && propogate;
    });
    return propogate;
  };
  var mousewheelevt = /Firefox/i.test(navigator.userAgent) ? "DOMMouseScroll" : "mousewheel";
  element.addEventListener(mousewheelevt, linkFunction, false);
};
noname.Utils = {};
noname.Utils.makeArrayOf = function(value, length) {
  var arr = [], i = length;
  while (i--) {
    arr[i] = value;
  }
  return arr;
};
noname.Utils.findInArray = function(items, matcher) {
  var length = items.length;
  for (var i = 0;i < length;i++) {
    if (matcher(items[i], i)) {
      return i;
    }
  }
  return-1;
};
noname.Args = {};
noname.Args.require = function(arg, label) {
  if (arg === null) {
    throw new Error("Require argument '" + label + "' to be specified.");
  }
  return noname.Args;
};
noname.Args.requireNumber = function(arg, label) {
  if (typeof arg !== "number") {
    throw new Error("Require '" + label + "' to be a number.");
  }
  return noname.Args;
};
noname.Args.requireFinitePositiveNumber = function(arg, label) {
  if (typeof arg !== "number" || arg <= 0) {
    throw new Error("Require '" + label + "' to be a positive number.");
  }
  return noname.Args;
};
noname.Args.requireString = function(arg, label) {
  if (typeof arg !== "string") {
    throw new Error("Require '" + label + "' to be a string.");
  }
  return noname.Args;
};
noname.Args.requireKeyedValuesDataset = function(arg, label) {
  if (!(arg instanceof noname.KeyedValuesDataset)) {
    throw new Error("Require '" + label + "' to be an requireKeyedValuesDataset.");
  }
  return noname.Args;
};
noname.Args.requireKeyedValues2DDataset = function(arg, label) {
  if (!(arg instanceof noname.KeyedValues2DDataset)) {
    throw new Error("Require '" + label + "' to be a KeyedValues2DDataset.");
  }
  return noname.Args;
};
noname.Args.requireXYDataset = function(arg, label) {
  if (!(arg instanceof noname.XYDataset)) {
    throw new Error("Require '" + label + "' to be an XYDataset.");
  }
  return noname.Args;
};
noname.Colors = {};
noname.Colors.fancyLight = function() {
  return["#64E1D5", "#E2D75E", "#F0A4B5", "#E7B16D", "#C2D58D", "#CCBDE4", "#6DE4A8", "#93D2E2", "#AEE377", "#A0D6B5"];
};
noname.Colors.fancyDark = function() {
  return["#3A6163", "#8A553A", "#4A6636", "#814C57", "#675A6F", "#384027", "#373B43", "#59372C", "#306950", "#665D31"];
};
noname.Colors.iceCube = function() {
  return["#4CE4B7", "#45756F", "#C2D9BF", "#58ADAF", "#4EE9E1", "#839C89", "#3E8F74", "#92E5C1", "#99E5E0", "#57BDAB"];
};
noname.Colors.blueOcean = function() {
  return["#6E7094", "#4F76DF", "#292E39", "#2E4476", "#696A72", "#4367A6", "#5E62B7", "#42759A", "#2E3A59", "#4278CA"];
};
noname.Colors.colorsAsObjects = function(colors) {
  return colors.map(function(s) {
    return noname.Color.fromStr(s);
  });
};
noname.SVGContext = function(svg) {
  throw new Error("Move to SVGContext2D");if (!(this instanceof noname.SVGContext)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._svg = svg;
  this.hiddenSVG = document.getElementById("spartacus");
  if (!this.hiddenSVG) {
    this.hiddenSVG = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    this.hiddenSVG.setAttribute("id", "spartacus");
    this.hiddenSVG.setAttribute("width", 600);
    this.hiddenSVG.setAttribute("height", 600);
    this.hiddenSVG.setAttribute("visibility", "hidden");
    document.body.appendChild(this.hiddenSVG);
  }
};
noname.SVGContext.prototype.group = function(id) {
  throw new Error("Move to SVGContext2D");var g = this.element("g");
  g.setAttribute("id", id);
  this.append(g);
  return g;
};
noname.SVGContext.prototype.element = function(elementType) {
  throw new Error("Move to SVGContext2D");return document.createElementNS("http://www.w3.org/2000/svg", elementType);
};
noname.SVGContext.prototype.append = function(element) {
  throw new Error("Move to SVGContext2D");this._svg.appendChild(element);
};
noname.SVGContext.prototype.setFont = function(font) {
  throw new Error("Move to SVGContext2D");this.font = font;
};
noname.SVGContext.prototype.textDim = function(text) {
  throw new Error("Move to SVGContext2D");var svgText = document.createElementNS("http://www.w3.org/2000/svg", "text");
  svgText.setAttribute("style", this.font.styleStr());
  svgText.innerHTML = text;
  this.hiddenSVG.appendChild(svgText);
  var bbox = svgText.getBBox();
  var dim = new noname.Dimension(bbox.width, bbox.height);
  if (bbox.width == 0 && (bbox.height == 0 && text.length > 0)) {
    dim = new noname.Dimension(svgText.scrollWidth, this.font.size);
  }
  this.hiddenSVG.removeChild(svgText);
  return dim;
};
noname.NumberFormat = function(dp, exponential) {
  if (!(this instanceof noname.NumberFormat)) {
    throw new Error("Use 'new' for construction.");
  }
  this._dp = dp;
  this._exponential = exponential || false;
};
noname.NumberFormat.prototype.format = function(n) {
  noname.Args.requireNumber(n, "n");
  if (this._exponential) {
    return n.toExponential(this._dp);
  }
  if (this._dp === Number.POSITIVE_INFINITY) {
    return n.toString();
  }
  return n.toFixed(this._dp);
};
noname.DateFormat = function(style) {
  this._date = new Date;
  this._style = style || "d-mmm-yyyy";
  this._months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
};
noname.DateFormat.prototype.format = function(n) {
  noname.Args.requireNumber(n, "n");
  this._date.setTime(n);
  if (this._style === "yyyy") {
    return this._dateToYYYY(this._date);
  }
  if (this._style === "mmm-yyyy") {
    return this._dateToMMMYYYY(this._date);
  }
  return this._date.toDateString();
};
noname.DateFormat.prototype._dateToYYYY = function(date) {
  var y = date.getFullYear();
  return y + "";
};
noname.DateFormat.prototype._dateToMMMYYYY = function(date) {
  var m = date.getMonth();
  var y = date.getFullYear();
  return this._months[m] + "-" + y;
};
noname.Anchor2D = function(refpt, offset) {
  if (!(this instanceof noname.Anchor2D)) {
    throw new Error("Use 'new' for constructor.");
  }
  noname.Args.requireNumber(refpt);
  this._refpt = refpt;
  this._offset = offset || new noname.Offset2D(0, 0);
};
noname.Anchor2D.prototype.refPt = function() {
  return this._refpt;
};
noname.Anchor2D.prototype.offset = function() {
  return this._offset;
};
noname.Anchor2D.prototype.anchorPoint = function(rect) {
  var x = 0;
  var y = 0;
  if (noname.RefPt2D.isLeft(this._refpt)) {
    x = rect.x() + this._offset.dx();
  } else {
    if (noname.RefPt2D.isHorizontalCenter(this._refpt)) {
      x = rect.centerX();
    } else {
      if (noname.RefPt2D.isRight(this._refpt)) {
        x = rect.maxX() - this._offset.dx();
      }
    }
  }
  if (noname.RefPt2D.isTop(this._refpt)) {
    y = rect.minY() + this._offset.dy();
  } else {
    if (noname.RefPt2D.isVerticalCenter(this._refpt)) {
      y = rect.centerY();
    } else {
      if (noname.RefPt2D.isBottom(this._refpt)) {
        y = rect.maxY() - this._offset.dy();
      }
    }
  }
  return new noname.Point2D(x, y);
};
noname.Anchor2D.prototype.zeroOffsetAnchor = function(refpt) {
  return new noname.Anchor2D(refpt, noname.Offset2D.ZERO_OFFSETS);
};
noname.Shape = function() {
};
noname.Shape.prototype.bounds = function() {
  throw new Error("This method must be overridden");
};
noname.Circle = function(x, y, radius) {
  this.x = x;
  this.y = y;
  this.radius = radius;
};
noname.Circle.prototype = new noname.Shape;
noname.Circle.prototype.bounds = function() {
  return new noname.Rectangle(this.x - this.radius, this.y - this.radius, this.radius * 2, this.radius * 2);
};
noname.Color = function(red, green, blue, alpha) {
  if (!(this instanceof noname.Color)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._red = red;
  this._green = green;
  this._blue = blue;
  this._alpha = alpha === 0 ? 0 : alpha || 255;
};
noname.Color.fromStr = function(s) {
  if (s.length === 4) {
    var rr = s[1] + s[1];
    var gg = s[2] + s[2];
    var bb = s[3] + s[3];
    var r = parseInt(rr, 16);
    var g = parseInt(gg, 16);
    var b = parseInt(bb, 16);
    return new noname.Color(r, g, b);
  }
  if (s.length === 7) {
    var rr = s[1] + s[2];
    var gg = s[3] + s[4];
    var bb = s[5] + s[6];
    var r = parseInt(rr, 16);
    var g = parseInt(gg, 16);
    var b = parseInt(bb, 16);
    return new noname.Color(r, g, b);
  }
};
noname.Color.prototype.rgbaStr = function() {
  var alphaPercent = this._alpha / 255;
  return "rgba(" + this._red + "," + this._green + "," + this._blue + "," + alphaPercent.toFixed(2) + ")";
};
noname.Dimension = function(w, h) {
  if (!(this instanceof noname.Dimension)) {
    throw new Error("Use 'new' for constructor.");
  }
  noname.Args.requireNumber(w, "w");
  noname.Args.requireNumber(h, "h");
  this._width = w;
  this._height = h;
  Object.freeze(this);
};
noname.Dimension.prototype.width = function() {
  return this._width;
};
noname.Dimension.prototype.height = function() {
  return this._height;
};
noname.Fit2D = function(anchor, scale) {
  if (!(this instanceof noname.Fit2D)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._anchor = anchor;
  this._scale = scale || noname.Scale2D.NONE;
};
noname.Fit2D.prototype.anchor = function() {
  return this._anchor;
};
noname.Fit2D.prototype.scale = function() {
  return this._scale;
};
noname.Fit2D.prototype.fit = function(srcDim, target) {
  if (this._scale === noname.Scale2D.SCALE_BOTH) {
    return noname.Rectangle.copy(target);
  }
  var w = srcDim.width();
  if (this._scale === noname.Scale2D.SCALE_HORIZONTAL) {
    w = target.width();
    if (!noname.RefPt2D.isHorizontalCenter(this._anchor.refPt())) {
      w -= 2 * this._anchor.offset().dx();
    }
  }
  var h = srcDim.height();
  if (this._scale === noname.Scale2D.SCALE_VERTICAL) {
    h = target.height();
    if (!noname.RefPt2D.isVerticalCenter(this._anchor.refPt())) {
      h -= 2 * this._anchor.offset().dy();
    }
  }
  var pt = this._anchor.anchorPoint(target);
  var x = Number.NaN;
  if (noname.RefPt2D.isLeft(this._anchor.refPt())) {
    x = pt.x();
  } else {
    if (noname.RefPt2D.isHorizontalCenter(this._anchor.refPt())) {
      x = target.centerX() - w / 2;
    } else {
      if (noname.RefPt2D.isRight(this._anchor.refPt())) {
        x = pt.x() - w;
      }
    }
  }
  var y = Number.NaN;
  if (noname.RefPt2D.isTop(this._anchor.refPt())) {
    y = pt.y();
  } else {
    if (noname.RefPt2D.isVerticalCenter(this._anchor.refPt())) {
      y = target.centerY() - h / 2;
    } else {
      if (noname.RefPt2D.isBottom(this._anchor.refPt())) {
        y = pt.y() - h;
      }
    }
  }
  return new noname.Rectangle(x, y, w, h);
};
noname.Fit2D.prototype.noScalingFitter = function(refPt) {
  var anchor = new noname.Anchor2D(refPt, new noname.Offset2D(0, 0));
  return new noname.Fit2D(anchor, noname.Scale2D.NONE);
};
noname.Font = function(family, size, bold, italic) {
  if (!(this instanceof noname.Font)) {
    throw new Error("Use 'new' for constructors.");
  }
  this.family = family;
  this.size = size;
  this.bold = bold || false;
  this.italic = italic || false;
};
noname.Font.prototype.styleStr = function() {
  var s = "font-family: " + this.family + "; ";
  s += "font-weight: " + (this.bold ? "bold" : "normal") + "; ";
  s += "font-style: " + (this.italic ? "italic" : "normal") + "; ";
  s += "font-size: " + this.size + "px";
  return s;
};
noname.HAlign = {LEFT:1, CENTER:2, RIGHT:3};
if (Object.freeze) {
  Object.freeze(noname.HAlign);
}
;noname.Insets = function(top, left, bottom, right) {
  if (!(this instanceof noname.Insets)) {
    throw new Error("Use 'new' for constructor.");
  }
  noname.Args.requireNumber(top, "top");
  noname.Args.requireNumber(left, "left");
  noname.Args.requireNumber(bottom, "bottom");
  noname.Args.requireNumber(right, "right");
  this._top = top;
  this._left = left;
  this._bottom = bottom;
  this._right = right;
  Object.freeze(this);
};
noname.Insets.prototype.top = function() {
  return this._top;
};
noname.Insets.prototype.left = function() {
  return this._left;
};
noname.Insets.prototype.bottom = function() {
  return this._bottom;
};
noname.Insets.prototype.right = function() {
  return this._right;
};
noname.Insets.prototype.value = function(edge) {
  if (edge === noname.RectangleEdge.TOP) {
    return this._top;
  }
  if (edge === noname.RectangleEdge.BOTTOM) {
    return this._bottom;
  }
  if (edge === noname.RectangleEdge.LEFT) {
    return this._left;
  }
  if (edge === noname.RectangleEdge.RIGHT) {
    return this._right;
  }
  throw new Error("Unrecognised edge code: " + edge);
};
noname.LineCap = {BUTT:"butt", ROUND:"round", SQUARE:"square"};
if (Object.freeze) {
  Object.freeze(noname.LineCap);
}
;noname.LineJoin = {ROUND:"round", BEVEL:"bevel", MITER:"miter"};
if (Object.freeze) {
  Object.freeze(noname.LineJoin);
}
;noname.Offset2D = function(dx, dy) {
  if (!(this instanceof noname.Offset2D)) {
    throw new Error("Use 'new' for constructors.");
  }
  noname.Args.requireNumber(dx, "dx");
  noname.Args.requireNumber(dy, "dy");
  this._dx = dx;
  this._dy = dy;
  Object.freeze(this);
};
noname.Offset2D.prototype.dx = function() {
  return this._dx;
};
noname.Offset2D.prototype.dy = function() {
  return this._dy;
};
noname.Point2D = function(x, y) {
  if (!(this instanceof noname.Point2D)) {
    throw new Error("Use 'new' for constructor.");
  }
  noname.Args.requireNumber(x, "x");
  noname.Args.requireNumber(y, "y");
  this._x = x;
  this._y = y;
  Object.freeze(this);
};
noname.Point2D.prototype.x = function() {
  return this._x;
};
noname.Point2D.prototype.y = function() {
  return this._y;
};
noname.Rectangle = function(x, y, width, height) {
  if (!(this instanceof noname.Rectangle)) {
    throw new Error("Use 'new' for constructor.");
  }
  noname.Args.requireNumber(x, "x");
  noname.Args.requireNumber(y, "y");
  noname.Args.requireNumber(width, "width");
  noname.Args.requireNumber(height, "height");
  this._x = x;
  this._y = y;
  this._width = width;
  this._height = height;
};
noname.Rectangle.prototype.x = function(value) {
  if (!arguments.length) {
    return this._x;
  }
  this._x = value;
  return this;
};
noname.Rectangle.prototype.y = function(value) {
  if (!arguments.length) {
    return this._y;
  }
  this._y = value;
  return this;
};
noname.Rectangle.prototype.width = function(value) {
  if (!arguments.length) {
    return this._width;
  }
  this._width = value;
  return this;
};
noname.Rectangle.prototype.height = function(value) {
  if (!arguments.length) {
    return this._height;
  }
  this._height = value;
  return this;
};
noname.Rectangle.prototype.length = function(edge) {
  if (edge === noname.RectangleEdge.TOP || edge === noname.RectangleEdge.BOTTOM) {
    return this._width;
  } else {
    if (edge === noname.RectangleEdge.LEFT || edge === noname.RectangleEdge.RIGHT) {
      return this._height;
    }
  }
  throw new Error("Unrecognised 'edge' value: " + edge);
};
noname.Rectangle.prototype.centerX = function() {
  return this._x + this._width / 2;
};
noname.Rectangle.prototype.minX = function() {
  return Math.min(this._x, this._x + this._width);
};
noname.Rectangle.prototype.maxX = function() {
  return Math.max(this._x, this._x + this._width);
};
noname.Rectangle.prototype.centerY = function() {
  return this._y + this._height / 2;
};
noname.Rectangle.prototype.minY = function() {
  return Math.min(this._y, this._y + this._height);
};
noname.Rectangle.prototype.maxY = function() {
  return Math.max(this._y, this._y + this._height);
};
noname.Rectangle.prototype.bounds = function() {
  return new noname.Rectangle(this._x, this._y, this._width, this._height);
};
noname.Rectangle.prototype.set = function(x, y, w, h) {
  this._x = x;
  this._y = y;
  this._width = w;
  this._height = h;
  return this;
};
noname.Rectangle.prototype.copy = function(rect) {
  return new noname.Rectangle(rect.x(), rect.y(), rect.width(), rect.height());
};
noname.Rectangle.prototype.constrainedPoint = function(x, y) {
  noname.Args.requireNumber(x);
  noname.Args.requireNumber(y);
  var xx = Math.max(this.minX(), Math.min(x, this.maxX()));
  var yy = Math.max(this.minY(), Math.min(y, this.maxY()));
  return new noname.Point2D(xx, yy);
};
noname.RectangleEdge = {TOP:"TOP", BOTTOM:"BOTTOM", LEFT:"LEFT", RIGHT:"RIGHT"};
noname.RectangleEdge.isTopOrBottom = function(edge) {
  noname.Args.requireString(edge, "edge");
  if (edge === noname.RectangleEdge.TOP || edge === noname.RectangleEdge.BOTTOM) {
    return true;
  }
};
noname.RectangleEdge.isLeftOrRight = function(edge) {
  noname.Args.requireString(edge, "edge");
  if (edge === noname.RectangleEdge.LEFT || edge === noname.RectangleEdge.RIGHT) {
    return true;
  }
};
if (Object.freeze) {
  Object.freeze(noname.RectangleEdge);
}
;noname.RefPt2D = {TOP_LEFT:1, TOP_CENTER:2, TOP_RIGHT:3, CENTER_LEFT:4, CENTER:5, CENTER_RIGHT:6, BOTTOM_LEFT:7, BOTTOM_CENTER:8, BOTTOM_RIGHT:9, isLeft:function(refpt) {
  return refpt === noname.RefPt2D.TOP_LEFT || (refpt === noname.RefPt2D.CENTER_LEFT || refpt === noname.RefPt2D.BOTTOM_LEFT);
}, isRight:function(refpt) {
  return refpt === noname.RefPt2D.TOP_RIGHT || (refpt === noname.RefPt2D.CENTER_RIGHT || refpt === noname.RefPt2D.BOTTOM_RIGHT);
}, isTop:function(refpt) {
  return refpt === noname.RefPt2D.TOP_LEFT || (refpt === noname.RefPt2D.TOP_CENTER || refpt === noname.RefPt2D.TOP_RIGHT);
}, isBottom:function(refpt) {
  return refpt === noname.RefPt2D.BOTTOM_LEFT || (refpt === noname.RefPt2D.BOTTOM_CENTER || refpt === noname.RefPt2D.BOTTOM_RIGHT);
}, isHorizontalCenter:function(refpt) {
  return refpt === noname.RefPt2D.TOP_CENTER || (refpt === noname.RefPt2D.CENTER || refpt === noname.RefPt2D.BOTTOM_CENTER);
}, isVerticalCenter:function(refpt) {
  return refpt === noname.RefPt2D.CENTER_LEFT || (refpt === noname.RefPt2D.CENTER || refpt === noname.RefPt2D.CENTER_RIGHT);
}};
if (Object.freeze) {
  Object.freeze(noname.RefPt2D);
}
;noname.Scale2D = {NONE:1, SCALE_HORIZONTAL:2, SCALE_VERTICAL:3, SCALE_BOTH:4};
if (Object.freeze) {
  Object.freeze(noname.Scale2D);
}
;noname.Stroke = function(lineWidth) {
  if (!(this instanceof noname.Stroke)) {
    throw new Error("Use 'new' for constructors.");
  }
  this.lineWidth = lineWidth || 1;
  this.lineCap = noname.LineCap.ROUND;
  this.lineJoin = noname.LineJoin.ROUND;
  this.miterLimit = 1;
  this.lineDash = [1];
  this.lineDashOffset = 0;
};
noname.Stroke.prototype.getStyleStr = function() {
  var s = "stroke-width: " + this.lineWidth + "; ";
  if (this.lineCap !== "butt") {
    s = s + "stroke-linecap: " + this.lineCap + "; ";
  }
  s = s + "stroke-linejoin: " + this.lineJoin + "; ";
  return s;
};
noname.SVGContext2D = function(svg) {
  this.svg = svg;
  this._defs = this.element("defs");
  this.svg.appendChild(this._defs);
  this._stack = [this.element("g")];
  this.svg.appendChild(this._stack[0]);
  this._hints = {};
  this.strokeStyle = "black";
  this.fillStyle = "black";
  this.pathStr = "";
  this.font = new noname.Font("monospace", 15);
  this.textAlign = "start";
  this.textBaseline = "alphabetic";
  this._stroke = new noname.Stroke;
  this._lineColor = new noname.Color(255, 255, 255);
  this._fillColor = new noname.Color(255, 0, 0);
  this._transform = new noname.Transform;
  this._hiddenGroup = this.svg.getElementById("hiddenGroup");
  if (!this._hiddenGroup) {
    this._hiddenGroup = document.createElementNS("http://www.w3.org/2000/svg", "g");
    this._hiddenGroup.setAttribute("id", "hiddenGroup");
    this._hiddenGroup.setAttribute("width", 60);
    this._hiddenGroup.setAttribute("height", 60);
    this._hiddenGroup.setAttribute("visibility", "hidden");
    this.svg.appendChild(this._hiddenGroup);
  }
};
noname.SVGContext2D.prototype.element = function(elementType) {
  return document.createElementNS("http://www.w3.org/2000/svg", elementType);
};
noname.SVGContext2D.prototype.append = function(element) {
  this._stack[this._stack.length - 1].appendChild(element);
};
noname.SVGContext2D.prototype.beginGroup = function(classStr) {
  var g = this.element("g");
  g.setAttribute("class", classStr);
  var cursor = this.getHint("cursor");
  if (cursor) {
    g.setAttribute("cursor", cursor);
    this.setHint("cursor", null);
  }
  var clip = this.getHint("clip");
  if (clip) {
    var clipPath = this.element("clipPath");
    clipPath.setAttribute("id", "clip-1");
    var rect = this.createRectElement(clip);
    clipPath.appendChild(rect);
    this._defs.appendChild(clipPath);
    g.setAttribute("clip-path", "url(#clip-1)");
    this.setHint("clip", null);
  }
  var glass = this.getHint("glass");
  if (glass) {
    var rect = this.createRectElement(clip);
    rect.setAttribute("fill", "rgba(0, 0, 0, 0)");
    g.appendChild(rect);
  }
  this.append(g);
  this._stack.push(g);
};
noname.SVGContext2D.prototype.endGroup = function() {
  if (this._stack.length === 1) {
    throw new Error("endGroup() does not have a matching beginGroup().");
  }
  this._stack.pop();
};
noname.SVGContext2D.prototype.clear = function() {
  this.svg.removeChild(this._defs);
  this.svg.removeChild(this._stack[0]);
  this._stack = [this.element("g")];
  this._defs = this.element("defs");
  this.svg.appendChild(this._defs);
  this.svg.appendChild(this._stack[0]);
};
noname.SVGContext2D.prototype.getHint = function(key) {
  return this._hints[key];
};
noname.SVGContext2D.prototype.setHint = function(key, value) {
  this._hints[key] = value;
};
noname.SVGContext2D.prototype.clearHints = function() {
  this._hints = {};
};
noname.SVGContext2D.prototype.setLineStroke = function(stroke) {
  noname.Args.require(stroke, "stroke");
  this._stroke = stroke;
};
noname.SVGContext2D.prototype.setLineColor = function(color) {
  noname.Args.require(color, "color");
  this._lineColor = color;
};
noname.SVGContext2D.prototype.setFillColor = function(color) {
  noname.Args.require(color, "color");
  this._fillColor = color;
};
noname.SVGContext2D.prototype.drawLine = function(x0, y0, x1, y1) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "line");
  t.setAttribute("stroke", this._lineColor.rgbaStr());
  t.setAttribute("x1", this._geomDP(x0));
  t.setAttribute("y1", this._geomDP(y0));
  t.setAttribute("x2", this._geomDP(x1));
  t.setAttribute("y2", this._geomDP(y1));
  t.setAttribute("style", this._stroke.getStyleStr());
  t.setAttribute("transform", this._svgTransformStr());
  this.append(t);
};
noname.SVGContext2D.prototype.drawRect = function(x, y, w, h) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "rect");
  t.setAttribute("stroke", this._lineColor.rgbaStr());
  t.setAttribute("fill", this._fillColor.rgbaStr());
  t.setAttribute("x", this._geomDP(x));
  t.setAttribute("y", this._geomDP(y));
  t.setAttribute("width", this._geomDP(w));
  t.setAttribute("height", this._geomDP(h));
  t.setAttribute("style", this._stroke.getStyleStr());
  t.setAttribute("transform", this._svgTransformStr());
  this.append(t);
};
noname.SVGContext2D.prototype.drawCircle = function(cx, cy, r) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "circle");
  t.setAttribute("stroke", this._lineColor.rgbaStr());
  t.setAttribute("stroke-width", this._stroke.lineWidth);
  t.setAttribute("fill", this._fillColor.rgbaStr());
  t.setAttribute("cx", cx);
  t.setAttribute("cy", cy);
  t.setAttribute("r", r);
  this.append(t);
};
noname.SVGContext2D.prototype.setFont = function(font) {
  this.font = font;
};
noname.SVGContext2D.prototype.stringWidth = function(str) {
};
noname.SVGContext2D.prototype.drawString = function(text, x, y) {
  this.fillText(text, x, y);
};
noname.SVGContext2D.prototype.fillText = function(text, x, y, maxWidth) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "text");
  t.setAttribute("x", x);
  t.setAttribute("y", y);
  t.setAttribute("style", this.getTextStyle());
  t.textContent = text;
  this.content.appendChild(t);
};
noname.SVGContext2D.prototype.drawAlignedString = function(text, x, y, anchor) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "text");
  t.setAttribute("x", this._geomDP(x));
  t.setAttribute("font-family", this.font.family);
  t.setAttribute("font-size", this.font.size + "px");
  t.setAttribute("fill", this._fillColor.rgbaStr());
  t.setAttribute("transform", this._svgTransformStr());
  t.textContent = text;
  var anchorStr = "start";
  if (noname.TextAnchor.isHorizontalCenter(anchor)) {
    anchorStr = "middle";
  }
  if (noname.TextAnchor.isRight(anchor)) {
    anchorStr = "end";
  }
  t.setAttribute("text-anchor", anchorStr);
  var adj = this.font.size;
  if (noname.TextAnchor.isBottom(anchor)) {
    adj = 0;
  } else {
    if (noname.TextAnchor.isHalfHeight(anchor)) {
      adj = this.font.size / 2;
    }
  }
  t.setAttribute("y", this._geomDP(y + adj));
  this.append(t);
  return this.textDim(text);
};
noname.SVGContext2D.prototype.drawRotatedString = function(text, x, y, anchor, angle) {
  this.translate(x, y);
  this.rotate(angle);
  this.drawAlignedString(text, 0, 0, anchor);
  this.rotate(-angle);
  this.translate(-x, -y);
};
noname.SVGContext2D.prototype.fillRect = function(x, y, width, height) {
  var rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
  rect.setAttribute("x", x);
  rect.setAttribute("y", y);
  rect.setAttribute("width", width);
  rect.setAttribute("height", height);
  rect.setAttribute("fill", this.fillStyle);
  this.append(rect);
};
noname.SVGContext2D.prototype.getStrokeStyle = function() {
  return "stroke:" + this.strokeStyle;
};
noname.SVGContext2D.prototype.beginPath = function() {
  this.pathStr = "";
};
noname.SVGContext2D.prototype.closePath = function() {
  this.pathStr = this.pathStr + "Z";
};
noname.SVGContext2D.prototype._geomDP = function(x) {
  return x.toFixed(3);
};
noname.SVGContext2D.prototype.moveTo = function(x, y) {
  this.pathStr = this.pathStr + "M " + this._geomDP(x) + " " + this._geomDP(y);
};
noname.SVGContext2D.prototype.lineTo = function(x, y) {
  this.pathStr = this.pathStr + "L " + this._geomDP(x) + " " + this._geomDP(y);
};
noname.SVGContext2D.prototype.arc = function(cx, cy, r, startAngle, endAngle, counterclockwise) {
};
noname.SVGContext2D.prototype.arcTo = function(x1, y1, x2, y2, radius) {
};
noname.SVGContext2D.prototype.fill = function() {
};
noname.SVGContext2D.prototype.stroke = function() {
  var path = document.createElementNS("http://www.w3.org/2000/svg", "path");
  path.setAttribute("style", this._stroke.getStyleStr());
  path.setAttribute("d", this.pathStr);
  this.content.appendChild(path);
};
noname.SVGContext2D.prototype.getTextStyle = function() {
  return this.font.family + " " + this.font.size + "px";
};
noname.SVGContext2D.prototype.translate = function(dx, dy) {
  this._transform.translate(dx, dy);
};
noname.SVGContext2D.prototype.rotate = function(radians) {
  this._transform.rotate(radians);
};
noname.SVGContext2D.prototype._svgTransformStr = function() {
  var t = this._transform;
  var s = "matrix(" + this._geomDP(t.scaleX) + "," + this._geomDP(t.shearY) + "," + this._geomDP(t.shearX) + "," + this._geomDP(t.scaleY) + "," + this._geomDP(t.translateX) + "," + this._geomDP(t.translateY) + ")";
  return s;
};
noname.SVGContext2D.prototype.textDim = function(text) {
  if (arguments.length !== 1) {
    throw new Error("Too many arguments.");
  }
  var svgText = document.createElementNS("http://www.w3.org/2000/svg", "text");
  svgText.setAttribute("style", this.font.styleStr());
  svgText.textContent = text;
  this._hiddenGroup.appendChild(svgText);
  var bbox = svgText.getBBox();
  var dim = new noname.Dimension(bbox.width, bbox.height);
  if (bbox.width == 0 && (bbox.height == 0 && text.length > 0)) {
    var h = svgText.scrollHeight;
    if (h == 0) {
      h = this.font.size;
    }
    dim = new noname.Dimension(svgText.scrollWidth, h);
  }
  this._hiddenGroup.removeChild(svgText);
  return dim;
};
noname.SVGContext2D.prototype.createRectElement = function(rect) {
  noname.Args.require(rect, "rect");
  var r = this.element("rect");
  r.setAttribute("x", rect.minX());
  r.setAttribute("y", rect.minY());
  r.setAttribute("width", rect.width());
  r.setAttribute("height", rect.height());
  return r;
};
noname.TextAnchor = {TOP_LEFT:0, TOP_CENTER:1, TOP_RIGHT:2, HALF_ASCENT_LEFT:3, HALF_ASCENT_CENTER:4, HALF_ASCENT_RIGHT:5, CENTER_LEFT:6, CENTER:7, CENTER_RIGHT:8, BASELINE_LEFT:9, BASELINE_CENTER:10, BASELINE_RIGHT:11, BOTTOM_LEFT:12, BOTTOM_CENTER:13, BOTTOM_RIGHT:14, isLeft:function(anchor) {
  return this === noname.TextAnchor.TOP_LEFT || (this === noname.TextAnchor.CENTER_LEFT || (this === noname.TextAnchor.HALF_ASCENT_LEFT || (this === noname.TextAnchor.BASELINE_LEFT || this === noname.TextAnchor.BOTTOM_LEFT)));
}, isHorizontalCenter:function(anchor) {
  return anchor === noname.TextAnchor.TOP_CENTER || (anchor === noname.TextAnchor.CENTER || (anchor === noname.TextAnchor.HALF_ASCENT_CENTER || (anchor === noname.TextAnchor.BASELINE_CENTER || anchor === noname.TextAnchor.BOTTOM_CENTER)));
}, isRight:function(anchor) {
  return anchor === noname.TextAnchor.TOP_RIGHT || (anchor === noname.TextAnchor.CENTER_RIGHT || (anchor === noname.TextAnchor.HALF_ASCENT_RIGHT || (anchor === noname.TextAnchor.BASELINE_RIGHT || anchor === noname.TextAnchor.BOTTOM_RIGHT)));
}, isTop:function(anchor) {
  return anchor === noname.TextAnchor.TOP_LEFT || (anchor === noname.TextAnchor.TOP_CENTER || anchor === noname.TextAnchor.TOP_RIGHT);
}, isHalfAscent:function(anchor) {
  return anchor === noname.TextAnchor.HALF_ASCENT_LEFT || (anchor === noname.TextAnchor.HALF_ASCENT_CENTER || anchor === noname.TextAnchor.HALF_ASCENT_RIGHT);
}, isHalfHeight:function(anchor) {
  return anchor === noname.TextAnchor.CENTER_LEFT || (anchor === noname.TextAnchor.CENTER || anchor === noname.TextAnchor.CENTER_RIGHT);
}, isBaseline:function(anchor) {
  return anchor === noname.TextAnchor.BASELINE_LEFT || (anchor === noname.TextAnchor.BASELINE_CENTER || anchor === noname.TextAnchor.BASELINE_RIGHT);
}, isBottom:function(anchor) {
  return anchor === noname.TextAnchor.BOTTOM_LEFT || (anchor === noname.TextAnchor.BOTTOM_CENTER || anchor === noname.TextAnchor.BOTTOM_RIGHT);
}};
if (Object.freeze) {
  Object.freeze(noname.TextAnchor);
}
;noname.Transform = function() {
  if (!(this instanceof noname.Transform)) {
    throw new Error("Use 'new' for constructors.");
  }
  this.scaleX = 1;
  this.scaleY = 1;
  this.translateX = 0;
  this.translateY = 0;
  this.shearX = 0;
  this.shearY = 0;
};
noname.Transform.prototype.translate = function(dx, dy) {
  this.translateX = this.translateX + dx;
  this.translateY = this.translateY + dy;
};
noname.Transform.prototype.rotate = function(theta) {
  var c = Math.cos(theta);
  var s = Math.sin(theta);
  var n00 = this.scaleX * c + this.shearX * s;
  var n01 = this.scaleX * -s + this.shearX * c;
  var n10 = this.shearY * c + this.scaleY * s;
  var n11 = this.shearY * -s + this.scaleY * c;
  this.scaleX = n00;
  this.shearX = n01;
  this.shearY = n10;
  this.scaleY = n11;
};
noname.BaseElement = function(instance) {
  if (!(this instanceof noname.BaseElement)) {
    throw new Error("Use 'new' for construction.");
  }
  if (!instance) {
    instance = this;
  }
  noname.BaseElement.init(instance);
};
var class___ = noname.BaseElement;
var proto___ = class___.prototype;
class___.init = function(instance) {
  instance._insets = new noname.Insets(2, 2, 2, 2);
  instance._refPt = noname.RefPt2D.CENTER;
  instance._backgroundPainter = null;
};
proto___.insets = function(obj) {
  if (!arguments.length) {
    return this._insets;
  }
  this._insets = obj;
  return this;
};
proto___.refPt = function(value) {
  if (!arguments.length) {
    return this._refPt;
  }
  this._refPt = value;
  return this;
};
proto___.backgroundPainter = function(painter) {
  if (!arguments.length) {
    return this._backgroundPainter;
  }
  this._backgroundPainter = painter;
  return this;
};
proto___.receive = function(visitor) {
  visitor.visit(this);
};
noname.FlowElement = function() {
  if (!(this instanceof noname.FlowElement)) {
    throw Error("Use 'new' for constructor.");
  }
  this._base = new noname.BaseElement;
  this._elements = [];
  this.halign = noname.HAlign.LEFT;
  this.hgap = 2;
};
noname.FlowElement.prototype = new noname.BaseElement;
noname.FlowElement.prototype.halign = function(align) {
  if (!arguments.length) {
    return this.halign;
  }
  this.halign = align;
  return this;
};
noname.FlowElement.prototype.hgap = function(value) {
  if (!arguments.length) {
    return this.hgap;
  }
  this.hgap = value;
  return this;
};
noname.FlowElement.prototype.add = function(element) {
  this._elements.push(element);
  return this;
};
noname.FlowElement.prototype.receive = function(visitor) {
  this._elements.forEach(function(child) {
    child.receive(visitor);
  });
};
noname.FlowElement.prototype.preferredSize = function(context, bounds) {
  var insets = this.insets();
  var w = insets.left() + insets.right();
  var h = insets.top() + insets.bottom();
  var maxRowWidth = 0;
  var elementCount = this._elements.length;
  var i = 0;
  while (i < elementCount) {
    var elementsInRow = this._rowOfElements(i, context, bounds);
    var rowHeight = this._calcRowHeight(elementsInRow);
    var rowWidth = this._calcRowWidth(elementsInRow, this.hgap);
    maxRowWidth = Math.max(rowWidth, maxRowWidth);
    h += rowHeight;
    i = i + elementsInRow.length;
  }
  w += maxRowWidth;
  return new noname.Dimension(insets.left() + w + insets.right(), insets.top() + h + insets.bottom());
};
noname.FlowElement.prototype._rowOfElements = function(first, context, bounds) {
  var result = [];
  var index = first;
  var full = false;
  var insets = this.insets();
  var w = insets.left() + insets.right();
  while (index < this._elements.length && !full) {
    var element = this._elements[index];
    var dim = element.preferredSize(context, bounds);
    if (w + dim.width() < bounds.width() || index === first) {
      result.push({"element":element, "dim":dim});
      w += dim.width() + this.hgap;
      index++;
    } else {
      full = true;
    }
  }
  return result;
};
noname.FlowElement.prototype._calcRowHeight = function(elements) {
  var height = 0;
  for (var i = 0;i < elements.length;i++) {
    height = Math.max(height, elements[i].dim.height());
  }
  return height;
};
noname.FlowElement.prototype._calcRowWidth = function(elements) {
  var width = 0;
  var count = elements.length;
  for (var i = 0;i < elements.length;i++) {
    width += elements[i].dim.width();
  }
  if (count > 1) {
    width += (count - 1) * this.hgap;
  }
  return width;
};
noname.FlowElement.prototype.layoutElements = function(context, bounds) {
  var result = [];
  var i = 0;
  var insets = this.insets();
  var x = bounds.x() + insets.left();
  var y = bounds.y() + insets.top();
  while (i < this._elements.length) {
    var elementsInRow = this._rowOfElements(i, context, bounds);
    var h = this._calcRowHeight(elementsInRow);
    var w = this._calcRowWidth(elementsInRow);
    if (this.halign === noname.HAlign.CENTER) {
      x = bounds.centerX() - w / 2;
    } else {
      if (this.halign === noname.HAlign.RIGHT) {
        x = bounds.maxX() - insets.right() - w;
      }
    }
    for (var j = 0;j < elementsInRow.length;j++) {
      var position = new noname.Rectangle(x, y, elementsInRow[j].dim.width(), h);
      result.push(position);
      x += position.width() + this.hgap;
    }
    i = i + elementsInRow.length;
    x = bounds.x() + insets.left();
    y += h;
  }
  return result;
};
noname.FlowElement.prototype.draw = function(context, bounds) {
  var dim = this.preferredSize(context, bounds);
  var fitter = new noname.Fit2D(new noname.Anchor2D(this.refPt()), noname.Scale2D.NONE);
  var dest = fitter.fit(dim, bounds);
  var layoutInfo = this.layoutElements(context, dest);
  for (var i = 0;i < this._elements.length;i++) {
    var rect = layoutInfo[i];
    var element = this._elements[i];
    element.draw(context, rect);
  }
};
noname.GridElement = function() {
  if (!(this instanceof noname.GridElement)) {
    throw new Error("Use 'new' for construction.");
  }
  new noname.BaseElement(this);
  this._elements = new noname.KeyedValues2DDataset;
};
noname.GridElement.prototype.insets = noname.BaseElement.prototype.insets;
noname.GridElement.prototype.refPt = noname.BaseElement.prototype.refPt;
noname.GridElement.prototype.add = function(element, rowKey, columnKey) {
  this._elements.add(rowKey, columnKey, element);
  return this;
};
noname.GridElement.prototype._findCellDims = function(context, bounds) {
  var widths = noname.Utils.makeArrayOf(0, this._elements.columnCount());
  var heights = noname.Utils.makeArrayOf(0, this._elements.rowCount());
  for (var r = 0;r < this._elements.rowCount();r++) {
    for (var c = 0;c < this._elements.columnCount();c++) {
      var element = this._elements.valueByIndex(r, c);
      if (element === null) {
        continue;
      }
      var dim = element.preferredSize(context, bounds);
      widths[c] = Math.max(widths[c], dim.width());
      heights[r] = Math.max(heights[r], dim.height());
    }
  }
  return{"widths":widths, "heights":heights};
};
noname.GridElement.prototype.preferredSize = function(context, bounds) {
  var me = this;
  var insets = this.insets();
  var cellDims = this._findCellDims(context, bounds);
  var w = insets.left() + insets.right();
  for (var i = 0;i < cellDims.widths.length;i++) {
    w = w + cellDims.widths[i];
  }
  var h = insets.top() + insets.bottom();
  for (var i = 0;i < cellDims.heights.length;i++) {
    h = h + cellDims.heights[i];
  }
  return new noname.Dimension(w, h);
};
noname.GridElement.prototype.layoutElements = function(context, bounds) {
  var insets = this.insets();
  var cellDims = this._findCellDims(context, bounds);
  var positions = [];
  var y = bounds.y() + insets.top();
  for (var r = 0;r < this._elements.rowCount();r++) {
    var x = bounds.x() + insets.left();
    for (var c = 0;c < this._elements.columnCount();c++) {
      positions.push(new noname.Rectangle(x, y, cellDims.widths[c], cellDims.heights[r]));
      x += cellDims.widths[c];
    }
    y = y + cellDims.heights[r];
  }
  return positions;
};
noname.GridElement.prototype.draw = function(context, bounds) {
  var positions = this.layoutElements(context, bounds);
  for (var r = 0;r < this._elements.rowCount();r++) {
    for (var c = 0;c < this._elements.columnCount();c++) {
      var element = this._elements.valueByIndex(r, c);
      if (element === null) {
        continue;
      }
      var pos = positions[r * this._elements.columnCount() + c];
      element.draw(context, pos);
    }
  }
};
noname.RectangleElement = function(width, height) {
  if (!(this instanceof noname.RectangleElement)) {
    throw new Error("Use 'new' for construction.");
  }
  this._base = new noname.BaseElement;
  this._width = width;
  this._height = height;
  this._fillColor = new noname.Color(255, 255, 255);
  this._backgroundPainter = new noname.StandardRectanglePainter(new noname.Color(255, 255, 255, 0.3), new noname.Color(0, 0, 0, 0));
};
noname.RectangleElement.prototype = new noname.BaseElement;
noname.RectangleElement.prototype.width = function(value) {
  if (!arguments.length) {
    return this._width;
  }
  this._width = value;
  return this;
};
noname.RectangleElement.prototype.height = function(value) {
  if (!arguments.length) {
    return this._height;
  }
  this._height = value;
  return this;
};
noname.RectangleElement.prototype.getFillColor = function() {
  return this._fillColor;
};
noname.RectangleElement.prototype.setFillColor = function(color) {
  if (typeof color === "string") {
    throw new Error("needs to be a color");
  }
  this._fillColor = color;
  return this;
};
noname.RectangleElement.prototype.fillColor = function(str) {
  throw new Error("Deprecated - use get/setFillColor()");if (!arguments.length) {
    return this._fillColor;
  }
  this._fillColor = str;
  return this;
};
noname.RectangleElement.prototype.preferredSize = function(context, bounds) {
  var insets = this.insets();
  var w = insets.left() + this._width + insets.right();
  var h = insets.top() + this._height + insets.bottom();
  var bw = bounds.width();
  var bh = bounds.height();
  return new noname.Dimension(Math.min(w, bw), Math.min(h, bh));
};
noname.RectangleElement.prototype.layoutElements = function(context, bounds) {
  var insets = this.insets();
  var w = Math.min(insets.left() + this._width + insets.right(), bounds.width());
  var h = Math.min(insets.top() + this._height + insets.bottom(), bounds.height());
  var pos = noname.Rectangle(bounds.centerX() - w / 2, bounds.centerY() - h / 2, w, h);
  return[pos];
};
noname.RectangleElement.prototype.draw = function(context, bounds) {
  var backgroundPainter = this.backgroundPainter();
  if (backgroundPainter) {
    backgroundPainter.paint(context, bounds);
  }
  var insets = this.insets();
  var ww = Math.max(bounds.width() - insets.left() - insets.right(), 0);
  var hh = Math.max(bounds.height() - insets.top() - insets.bottom(), 0);
  var w = Math.min(this._width, ww);
  var h = Math.min(this._height, hh);
  var rect = context.element("rect");
  rect.setAttribute("x", bounds.centerX() - w / 2);
  rect.setAttribute("y", bounds.centerY() - h / 2);
  rect.setAttribute("width", w);
  rect.setAttribute("height", h);
  var styleStr = "fill: " + this._fillColor.rgbaStr();
  rect.setAttribute("style", styleStr);
  context.append(rect);
};
noname.ShapeElement = function(shape, color) {
  this.shape = shape;
  this.color = color;
};
noname.ShapeElement.prototype.preferredSize = function(ctx, bounds, constraints) {
  var shapeBounds = this.shape.bounds();
  var insets = this.insets;
  var w = Math.min(bounds.width, shapeBounds.width + insets.left + insets.right);
  var h = Math.min(bounds.height, shapeBounds.height + insets.top + insets.bottom);
  return new noname.Dimension(w, h);
};
noname.ShapeElement.prototype.layoutElements = function(ctx, bounds, constraints) {
  var dim = this.preferredSize(ctx, bounds, constraints);
  var pos = new noname.Rectangle(bounds.getCenterX() - dim.width / 2, bounds.getCenterY() - dim.height / 2, dim.width, dim.height);
  return[pos];
};
noname.ShapeElement.prototype.draw = function(ctx, bounds) {
};
noname.StandardRectanglePainter = function(fillColor, strokeColor) {
  if (!(this instanceof noname.StandardRectanglePainter)) {
    throw new Error("Use 'new' for construction.");
  }
  this._fillColor = fillColor;
  this._strokeColor = strokeColor;
};
noname.StandardRectanglePainter.prototype.paint = function(ctx, bounds) {
  var rect = ctx.element("rect");
  rect.setAttribute("x", bounds.x());
  rect.setAttribute("y", bounds.y());
  rect.setAttribute("width", bounds.width());
  rect.setAttribute("height", bounds.height());
  var styleStr = "";
  if (this._fillColor) {
    styleStr += "fill: " + this._fillColor.rgbaStr() + "; ";
  }
  if (this._strokeColor) {
    styleStr += "stroke: " + this._strokeColor.rgbaStr() + "; ";
  }
  rect.setAttribute("style", styleStr);
  ctx.append(rect);
};
noname.TextElement = function(textStr) {
  if (!(this instanceof noname.TextElement)) {
    throw new Error("Use 'new' for construction.");
  }
  noname.BaseElement.init(this);
  this._text = textStr;
  this._font = new noname.Font("Palatino, serif", 16);
  this._color = "black";
  this._halign = noname.HAlign.LEFT;
  this._backgroundPainter = new noname.StandardRectanglePainter(new noname.Color(255, 255, 255, 0.3), new noname.Color(0, 0, 0, 0));
};
noname.TextElement.prototype = new noname.BaseElement;
noname.TextElement.prototype.text = function(str) {
  if (!arguments.length) {
    return this._text;
  }
  this._text = str;
  return this;
};
noname.TextElement.prototype.color = function(str) {
  if (!arguments.length) {
    return this._color;
  }
  this._color = str;
  return this;
};
noname.TextElement.prototype.font = function(font) {
  if (!arguments.length) {
    return this._font;
  }
  this._font = font;
  return this;
};
noname.TextElement.prototype.halign = function(align) {
  if (!arguments.length) {
    return this._halign;
  }
  this._halign = align;
  return this;
};
noname.TextElement.prototype.preferredSize = function(context, bounds) {
  var insets = this.insets();
  context.setFont(this._font);
  var dim = context.textDim(this._text);
  return new noname.Dimension(insets.left() + dim.width() + insets.right(), insets.top() + dim.height() + insets.bottom());
};
noname.TextElement.prototype.layoutElements = function(context, bounds) {
  var insets = this.insets();
  context.setFont(this._font);
  var dim = context.textDim(this._text);
  var w = dim.width() + insets.left() + insets.right();
  var x = bounds.x();
  switch(this._halign) {
    case noname.HAlign.LEFT:
      x = bounds.x();
      break;
    case noname.HAlign.CENTER:
      x = bounds.centerX() - w / 2;
      break;
    case noname.HAlign.RIGHT:
      x = bounds.maxX() - w;
      break;
  }
  var y = bounds.y();
  var h = Math.min(dim.height() + insets.top() + insets.bottom(), bounds.height());
  return[new noname.Rectangle(x, y, w, h)];
};
noname.TextElement.prototype.draw = function(context, bounds) {
  var backgroundPainter = this.backgroundPainter();
  if (backgroundPainter) {
    backgroundPainter.paint(context, bounds);
  }
  var insets = this.insets();
  var pos = this.layoutElements(context, bounds)[0];
  var t = context.element("text");
  t.setAttribute("x", pos.x() + insets.left());
  var upper = pos.y() + insets.top();
  var lower = pos.maxY() - insets.bottom();
  var span = lower - upper;
  var base = lower - 0.18 * span;
  t.setAttribute("y", base);
  t.textContent = this._text;
  t.setAttribute("style", "fill: " + this._color + "; " + this._font.styleStr());
  context.append(t);
};
noname.LegendBuilder = function() {
};
noname.LegendBuilder.prototype.createLegend = function(plot, anchor, orientation, style) {
};
noname.LegendItemInfo = function(key, color) {
  this.seriesKey = key || "";
  this.label = key || "";
  this.description = "";
  this.shape = null;
  this.color = color;
};
noname.StandardLegendBuilder = function(instance) {
  if (!(this instanceof noname.StandardLegendBuilder)) {
    throw new Error("Use 'new' for constructor.");
  }
  if (!instance) {
    instance = this;
  }
  noname.StandardLegendBuilder.init(instance);
};
class___ = noname.StandardLegendBuilder;
proto___ = class___.prototype;
class___.init = function(instance) {
  instance._font = new noname.Font("Palatino, serif", 12);
};
proto___.getFont = function() {
  return this._font;
};
proto___.setFont = function(font) {
  this._font = font;
};
proto___.createLegend = function(plot, anchor, orientation, style) {
  var info = plot.legendInfo();
  var result = new noname.FlowElement;
  var me = this;
  info.forEach(function(info) {
    var shape = (new noname.RectangleElement(8, 5)).setFillColor(info.color);
    var text = (new noname.TextElement(info.label)).font(me._font);
    var item = new noname.GridElement;
    item.add(shape, "R1", "C1");
    item.add(text, "R1", "C2");
    result.add(item);
  });
  return result;
};
noname.FixedLegendBuilder = function() {
  if (!(this instanceof noname.FixedLegendBuilder)) {
    throw new Error("Use 'new' for constructor.");
  }
  noname.StandardLegendBuilder.init(this);
  this._info = [];
};
noname.FixedLegendBuilder.prototype = new noname.StandardLegendBuilder;
noname.FixedLegendBuilder.prototype.add = function(key, color) {
  this._info.push(new noname.LegendItemInfo(key, color));
};
noname.FixedLegendBuilder.prototype.clear = function() {
  this._info = [];
};
noname.FixedLegendBuilder.prototype.createLegend = function(plot, anchor, orientation, style) {
  var info = this._info;
  var result = new noname.FlowElement;
  var legendBuilder = this;
  info.forEach(function(info) {
    var shape = (new noname.RectangleElement(8, 5)).setFillColor(info.color);
    var text = (new noname.TextElement(info.label)).font(legendBuilder.getFont());
    var item = new noname.GridElement;
    item.add(shape, "R1", "C1");
    item.add(text, "R1", "C2");
    result.add(item);
  });
  return result;
};
noname.AxisSpace = function(top, left, bottom, right) {
  if (!(this instanceof noname.AxisSpace)) {
    throw new Error("Use 'new' for constructor.");
  }
  noname.Args.requireNumber(top, "top");
  noname.Args.requireNumber(left, "left");
  noname.Args.requireNumber(bottom, "bottom");
  noname.Args.requireNumber(right, "right");
  this._top = top;
  this._left = left;
  this._bottom = bottom;
  this._right = right;
};
noname.AxisSpace.prototype.top = function() {
  return this._top;
};
noname.AxisSpace.prototype.left = function() {
  return this._left;
};
noname.AxisSpace.prototype.bottom = function() {
  return this._bottom;
};
noname.AxisSpace.prototype.right = function() {
  return this._right;
};
noname.AxisSpace.prototype.extend = function(space, edge) {
  noname.Args.requireNumber(space, "space");
  if (edge === noname.RectangleEdge.TOP) {
    this._top += space;
  } else {
    if (edge === noname.RectangleEdge.BOTTOM) {
      this._bottom += space;
    } else {
      if (edge === noname.RectangleEdge.LEFT) {
        this._left += space;
      } else {
        if (edge === noname.RectangleEdge.RIGHT) {
          this._right += space;
        } else {
          throw new Error("Unrecognised 'edge' code: " + edge);
        }
      }
    }
  }
};
noname.AxisSpace.prototype.innerRect = function(source) {
  var x = source.x() + this._left;
  var y = source.y() + this._top;
  var w = source.width() - this._left - this._right;
  var h = source.height() - this._top - this._bottom;
  return new noname.Rectangle(x, y, w, h);
};
noname.LabelOrientation = {PERPENDICULAR:"PERPENDICULAR", PARALLEL:"PARALLEL"};
if (Object.freeze) {
  Object.freeze(noname.LabelOrientation);
}
;noname.BaseValueAxis = function(label, instance) {
  if (!(this instanceof noname.BaseValueAxis)) {
    throw new Error("Use 'new' for construction.");
  }
  if (!instance) {
    instance = this;
  }
  noname.BaseValueAxis.init(instance);
  instance._label = label;
};
class___ = noname.BaseValueAxis;
proto___ = class___.prototype;
class___.init = function(instance) {
  instance._listeners = [];
  instance._labelFont = new noname.Font("Palatino;serif", 12, true, false);
  instance._labelColor = new noname.Color(0, 0, 0);
  instance._labelMargin = new noname.Insets(2, 2, 2, 2);
  instance._tickLabelFont = new noname.Font("san-serif", 14);
  instance._tickLabelColor = new noname.Color(0, 0, 0);
  instance._axisLineColor = new noname.Color(100, 100, 100);
  instance._axisLineStroke = new noname.Stroke(0.5);
  instance._gridLinesVisible = true;
  instance._gridLineStroke = new noname.Stroke(1);
  instance._gridLineColor = new noname.Color(255, 255, 255);
};
proto___.getLabel = function() {
  return this._label;
};
proto___.setLabel = function(label, notify) {
  this._label = label;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getLabelFont = function() {
  return this._labelFont;
};
proto___.setLabelFont = function(font, notify) {
  this._labelFont = font;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getLabelColor = function() {
  return this._labelColor;
};
proto___.setLabelColor = function(color, notify) {
  this._labelColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getLabelMargin = function() {
  return this._labelMargin;
};
proto___.setLabelMargin = function(margin, notify) {
  this._labelMargin = margin;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getTickLabelFont = function() {
  return this._tickLabelFont;
};
proto___.setTickLabelFont = function(font, notify) {
  this._tickLabelFont = font;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getTickLabelColor = function() {
  return this._tickLabelColor;
};
proto___.setTickLabelColor = function(color, notify) {
  this._tickLabelColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getAxisLineColor = function() {
  return this._axisLineColor;
};
proto___.setAxisLineColor = function(color, notify) {
  this._axisLineColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getAxisLineStroke = function() {
  return this._axisLineStroke;
};
proto___.setAxisLineStroke = function(stroke, notify) {
  this._axisLineStroke = stroke;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.addListener = function(listener) {
  this._listeners.push(listener);
};
proto___.notifyListeners = function() {
  var axis = this;
  this._listeners.forEach(function(listener) {
    listener(axis);
  });
};
proto___.isGridLinesVisible = function() {
  return this._gridLinesVisible;
};
proto___.setGridLinesVisible = function(visible, notify) {
  this._gridLinesVisible = visible !== false;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getGridLineStroke = function() {
  return this._gridLineStroke;
};
proto___.setGridLineStroke = function(stroke, notify) {
  this._gridLineStroke = stroke;
  if (notify !== false) {
    this.notifyListeners();
  }
};
proto___.getGridLineColor = function() {
  return this._gridLineColor;
};
proto___.setGridLineColor = function(color, notify) {
  this._gridLineColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.LinearAxis = function(label) {
  if (!(this instanceof noname.LinearAxis)) {
    throw new Error("Use 'new' with constructor.");
  }
  noname.BaseValueAxis.init(this);
  this._autoRange = true;
  this._autoRangeIncludesZero = false;
  this._lowerMargin = 0.05;
  this._upperMargin = 0.05;
  this._lowerBound = 0;
  this._upperBound = 1;
  this._tickSelector = new noname.NumberTickSelector;
  this._formatter = new noname.NumberFormat;
  this._tickMarkInnerLength = 0;
  this._tickMarkOuterLength = 2;
  this._tickMarkStroke = new noname.Stroke(0.5);
  this._tickMarkColor = new noname.Color(100, 100, 100);
  this._tickLabelMargin = new noname.Insets(2, 2, 2, 2);
  this._tickLabelFactor = 1.4;
  this._tickLabelOrientation = null;
  this._tickLabelFormatOverride = null;
};
noname.LinearAxis.prototype = new noname.BaseValueAxis;
noname.LinearAxis.prototype.isAutoRange = function() {
  return this._autoRange;
};
noname.LinearAxis.prototype.setAutoRange = function(auto, notify) {
  this._autoRange = auto;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.LinearAxis.prototype.getAutoRangeIncludesZero = function() {
  return this._autoRangeIncludesZero;
};
noname.LinearAxis.prototype.setAutoRangeIncludesZero = function(include, notify) {
  this._autoRangeIncludesZero = include;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.LinearAxis.prototype.getLowerMargin = function() {
  return this._lowerMargin;
};
noname.LinearAxis.prototype.setLowerMargin = function(margin, notify) {
  this._lowerMargin = margin;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.LinearAxis.prototype.getUpperMargin = function() {
  return this._upperMargin;
};
noname.LinearAxis.prototype.setUpperMargin = function(margin, notify) {
  this._upperMargin = margin;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.LinearAxis.prototype.getTickLabelFormatOverride = function() {
  return this._tickLabelFormatOverride;
};
noname.LinearAxis.prototype.setTickLabelFormatOverride = function(formatter, notify) {
  this._tickLabelFormatOverride = formatter;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.LinearAxis.prototype.updateDomain = function(min, max) {
  var xrange = max - min;
  var lowAdj, highAdj;
  if (xrange > 0) {
    lowAdj = this._lowerMargin * xrange;
    highAdj = this._upperMargin * xrange;
  } else {
    lowAdj = 0.5;
    highAdj = 0.5;
  }
  this._lowerBound = min - lowAdj;
  this._upperBound = max + highAdj;
};
noname.LinearAxis.prototype.valueToCoordinate = function(value, r0, r1) {
  noname.Args.requireNumber(r0, "r0");
  noname.Args.requireNumber(r1, "r1");
  var a = this._lowerBound;
  var b = this._upperBound;
  return r0 + (value - a) / (b - a) * (r1 - r0);
};
noname.LinearAxis.prototype.coordinateToValue = function(coordinate, r0, r1) {
  var a = this._lowerBound;
  var b = this._upperBound;
  return a + (coordinate - r0) / (r1 - r0) * (b - a);
};
noname.LinearAxis.prototype._calcTickSize = function(ctx, area, edge) {
  var result = Number.NaN;
  var pixels = area.length(edge);
  var range = this._upperBound - this._lowerBound;
  var orientation = this._resolveTickLabelOrientation(edge);
  var selector = this._tickSelector;
  if (orientation === noname.LabelOrientation.PERPENDICULAR) {
    var textHeight = ctx.textDim("123").height();
    var maxTicks = pixels / (textHeight * this._tickLabelFactor);
    if (maxTicks > 2) {
      var tickSize = selector.select(range / 2);
      var tickCount = Math.floor(range / tickSize);
      while (tickCount < maxTicks) {
        selector.previous();
        tickCount = Math.floor(range / selector.currentTickSize());
      }
      selector.next();
      result = selector.currentTickSize();
      this._formatter = selector.currentTickFormat();
    } else {
    }
  } else {
    if (orientation === noname.LabelOrientation.PARALLEL) {
      selector.select(range);
      ctx.setFont(this._tickLabelFont);
      var done = false;
      while (!done) {
        if (selector.previous()) {
          var f = selector.currentTickFormat();
          this._formatter = f;
          var s0 = f.format(this._lowerBound);
          var s1 = f.format(this._upperBound);
          var w0 = ctx.textDim(s0).width();
          var w1 = ctx.textDim(s1).width();
          var w = Math.max(w0, w1);
          if (w == 0 && (s0.length > 0 && s1.length > 0)) {
            return Number.NaN;
          }
          var n = Math.floor(pixels / (w * this._tickLabelFactor));
          if (n < range / selector.currentTickSize()) {
            selector.next();
            this._formatter = selector.currentTickFormat();
            done = true;
          }
        } else {
          done = true;
        }
      }
      result = selector.currentTickSize();
    }
  }
  return result;
};
noname.LinearAxis.prototype._resolveTickLabelOrientation = function(edge) {
  var result = this._tickLabelOrientation;
  if (!result) {
    if (edge === noname.RectangleEdge.LEFT || edge === noname.RectangleEdge.RIGHT) {
      result = noname.LabelOrientation.PERPENDICULAR;
    } else {
      if (edge === noname.RectangleEdge.TOP || edge === noname.RectangleEdge.BOTTOM) {
        result = noname.LabelOrientation.PARALLEL;
      } else {
        throw new Error("Unrecognised 'edge' code: " + edge);
      }
    }
  }
  return result;
};
noname.LinearAxis.prototype.reserveSpace = function(ctx, plot, bounds, area, edge) {
  var space = this._tickMarkOuterLength;
  if (this._label) {
    ctx.setFont(this._labelFont);
    var dim = ctx.textDim(this._label);
    var lm = this._labelMargin;
    space += dim.height();
    if (noname.RectangleEdge.isTopOrBottom(edge)) {
      space += lm.top() + lm.bottom();
    } else {
      if (noname.RectangleEdge.isLeftOrRight(edge)) {
        space += lm.left() + lm.right();
      } else {
        throw new Error("Unrecognised edge code: " + edge);
      }
    }
  }
  var ticks = this.ticks(ctx, plot, area, edge);
  ctx.setFont(this._tickLabelFont);
  var orientation = this._resolveTickLabelOrientation(edge);
  if (orientation === noname.LabelOrientation.PERPENDICULAR) {
    var max = 0;
    ticks.forEach(function(t) {
      max = Math.max(max, ctx.textDim(t.label).width());
    });
    space += max;
  } else {
    if (orientation === noname.LabelOrientation.PARALLEL) {
      var dim = ctx.textDim("123");
      space += dim.height();
    }
  }
  if (noname.RectangleEdge.isTopOrBottom(edge)) {
    space += this._tickLabelMargin.top() + this._tickLabelMargin.bottom();
  } else {
    if (noname.RectangleEdge.isLeftOrRight(edge)) {
      space += this._tickLabelMargin.left() + this._tickLabelMargin.right();
    } else {
      throw new Error("Unrecognised edge code: " + edge);
    }
  }
  return space;
};
noname.LinearAxis.prototype._measureWidth = function(edge, labelOrientation) {
  if (noname.RectangleEdge.isLeftOrRight(edge)) {
    return labelOrientation === noname.LabelOrientation.PERPENDICULAR;
  }
  if (noname.RectangleEdge.isTopOrBottom(edge)) {
    return labelOrientation === noname.LabelOrientation.PARALLEL;
  }
  throw new Error("Unrecognised edge code: " + edge);
};
noname.LinearAxis.prototype.ticks = function(ctx, plot, area, edge) {
  var tickSize = this._calcTickSize(ctx, area, edge);
  var formatter = this._tickLabelFormatOverride || this._formatter;
  if (!isNaN(tickSize)) {
    var result = [];
    var t = Math.ceil(this._lowerBound / tickSize) * tickSize;
    while (t < this._upperBound) {
      var tm = new noname.TickMark(t, formatter.format(t));
      result.push(tm);
      t += tickSize;
    }
    return result;
  } else {
    var tm0 = new noname.TickMark(this._lowerBound, this._lowerBound + "");
    var tm1 = new noname.TickMark(this._upperBound, this._upperBound + "");
    return[tm0, tm1];
  }
};
noname.LinearAxis.prototype.draw = function(ctx, plot, bounds, dataArea, offset) {
  var edge = plot.axisPosition(this);
  var ticks = this.ticks(ctx, plot, dataArea, edge);
  var x = dataArea.x();
  var y = dataArea.y();
  var w = dataArea.width();
  var h = dataArea.height();
  var isLeft = edge === noname.RectangleEdge.LEFT;
  var isRight = edge === noname.RectangleEdge.RIGHT;
  var isTop = edge === noname.RectangleEdge.TOP;
  var isBottom = edge === noname.RectangleEdge.BOTTOM;
  if (isLeft || isRight) {
    ctx.setFont(this._tickLabelFont);
    ctx.setFillColor(this._tickLabelColor);
    var maxTickLabelWidth = 0;
    for (var i = 0;i < ticks.length;i++) {
      var tick = ticks[i];
      var yy = this.valueToCoordinate(tick.value, y + h, y);
      if (this._gridLinesVisible) {
        ctx.setLineStroke(this._gridLineStroke);
        ctx.setLineColor(this._gridLineColor);
        ctx.drawLine(x, Math.round(yy), x + w, Math.round(yy));
      }
      if (this._tickMarkInnerLength + this._tickMarkOuterLength > 0) {
        ctx.setLineStroke(this._tickMarkStroke);
        ctx.setLineColor(this._tickMarkColor);
        if (isRight) {
          ctx.drawLine(x + w + offset - this._tickMarkInnerLength, yy, x + w + offset + this._tickMarkOuterLength, yy);
        } else {
          ctx.drawLine(x - offset - this._tickMarkOuterLength, yy, x - offset + this._tickMarkInnerLength, yy);
        }
      }
      if (isRight) {
        var adj = offset + this._tickMarkOuterLength + this._tickLabelMargin.left();
        var dim = ctx.drawAlignedString(tick.label, x + w + adj, yy, noname.TextAnchor.CENTER_LEFT);
      } else {
        var adj = offset + this._tickMarkOuterLength + this._tickLabelMargin.right();
        var dim = ctx.drawAlignedString(tick.label, x - adj, yy, noname.TextAnchor.CENTER_RIGHT);
      }
      maxTickLabelWidth = Math.max(maxTickLabelWidth, dim.width());
    }
    ctx.setLineColor(this._axisLineColor);
    ctx.setLineStroke(this._axisLineStroke);
    if (isRight) {
      ctx.drawLine(x + w + offset, y, x + w + offset, y + dataArea.height());
    } else {
      ctx.drawLine(x - offset, y, x - offset, y + dataArea.height());
    }
    if (this._label) {
      ctx.setFont(this._labelFont);
      ctx.setFillColor(this._labelColor);
      if (isRight) {
        var adj = offset + maxTickLabelWidth + this._tickMarkOuterLength + this._tickLabelMargin.left() + this._tickLabelMargin.right() + this._labelMargin.left();
        ctx.drawRotatedString(this._label, x + w + adj, y + h / 2, noname.TextAnchor.BOTTOM_CENTER, Math.PI / 2);
      } else {
        var adj = offset + maxTickLabelWidth + this._tickMarkOuterLength + this._tickLabelMargin.left() + this._tickLabelMargin.right() + this._labelMargin.right();
        ctx.drawRotatedString(this._label, x - adj, y + h / 2, noname.TextAnchor.BOTTOM_CENTER, -Math.PI / 2);
      }
    }
  } else {
    if (isTop || isBottom) {
      ctx.setFont(this._tickLabelFont);
      ctx.setFillColor(this._tickLabelColor);
      var gap = offset + this._tickMarkOuterLength;
      if (isTop) {
        gap += this._tickLabelMargin.bottom();
      } else {
        gap += this._tickLabelMargin.top();
      }
      for (var i = 0;i < ticks.length;i++) {
        var tick = ticks[i];
        var xx = this.valueToCoordinate(tick.value, x, x + w);
        if (this._gridLinesVisible) {
          ctx.setLineStroke(this._gridLineStroke);
          ctx.setLineColor(this._gridLineColor);
          ctx.drawLine(Math.round(xx), y, Math.round(xx), y + h);
        }
        if (this._tickMarkInnerLength + this._tickMarkOuterLength > 0) {
          ctx.setLineStroke(this._tickMarkStroke);
          ctx.setLineColor(this._tickMarkColor);
          if (isTop) {
            ctx.drawLine(xx, y - offset - this._tickMarkOuterLength, xx, y - offset + this._tickMarkInnerLength);
            ctx.drawAlignedString(tick.label, xx, y - gap, noname.TextAnchor.BOTTOM_CENTER);
          } else {
            ctx.drawLine(xx, y + h + offset - this._tickMarkInnerLength, xx, y + h + offset + this._tickMarkOuterLength);
            ctx.drawAlignedString(tick.label, xx, y + h + gap, noname.TextAnchor.TOP_CENTER);
          }
        }
      }
      ctx.setLineColor(this._axisLineColor);
      ctx.setLineStroke(this._axisLineStroke);
      if (isTop) {
        ctx.drawLine(x, y - offset, x + w, y - offset);
      } else {
        ctx.drawLine(x, y + h + offset, x + w, y + h + offset);
      }
      if (this._label) {
        ctx.setFont(this._labelFont);
        ctx.setFillColor(this._labelColor);
        if (isTop) {
          ctx.drawAlignedString(this._label, x + w / 2, y - gap - this._tickLabelMargin.bottom() - this._labelMargin.top() - this._tickLabelFont.size, noname.TextAnchor.BOTTOM_CENTER);
        } else {
          ctx.drawAlignedString(this._label, x + w / 2, y + h + gap + this._tickLabelMargin.bottom() + this._labelMargin.top() + this._tickLabelFont.size, noname.TextAnchor.TOP_CENTER);
        }
      }
    }
  }
};
noname.LinearAxis.prototype.configureAsXAxis = function(plot) {
  if (this._autoRange && plot.getDataset()) {
    var bounds = plot.getDataset().xbounds();
    if (bounds[0] <= bounds[1]) {
      this.updateDomain(bounds[0], bounds[1]);
    }
  }
};
noname.LinearAxis.prototype.configureAsYAxis = function(plot) {
  if (this._autoRange && plot.getDataset()) {
    var bounds = plot.getDataset().ybounds();
    if (bounds[0] <= bounds[1]) {
      this.updateDomain(bounds[0], bounds[1]);
    }
  }
};
noname.LinearAxis.prototype.resizeRange = function(factor, anchorValue, notify) {
  noname.Args.requireNumber(factor);
  if (factor > 0) {
    var left = anchorValue - this._lowerBound;
    var right = this._upperBound - anchorValue;
    this._lowerBound = anchorValue - left * factor;
    this._upperBound = anchorValue + right * factor;
    if (notify) {
      this.notifyListeners();
    }
  } else {
  }
};
noname.LinearAxis.prototype.pan = function(percent, notify) {
  noname.Args.requireNumber(percent);
  var length = this._upperBound - this._lowerBound;
  var adj = percent * length;
  this._lowerBound += adj;
  this._upperBound += adj;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.NumberTickSelector = function(percentage) {
  if (!(this instanceof noname.NumberTickSelector)) {
    throw new Error("Use new for construction.");
  }
  this._power = 0;
  this._factor = 1;
  this._percentage = percentage;
  this._f0 = new noname.NumberFormat(0);
  this._f1 = new noname.NumberFormat(1);
  this._f2 = new noname.NumberFormat(2);
  this._f3 = new noname.NumberFormat(3);
  this._f4 = new noname.NumberFormat(4);
};
noname.NumberTickSelector.prototype.select = function(reference) {
  this._power = Math.ceil(Math.LOG10E * Math.log(reference));
  this._factor = 1;
  return this.currentTickSize();
};
noname.NumberTickSelector.prototype.currentTickSize = function() {
  return this._factor * Math.pow(10, this._power);
};
noname.NumberTickSelector.prototype.currentTickFormat = function() {
  if (this._power === -4) {
    return this._f4;
  }
  if (this._power === -3) {
    return this._f3;
  }
  if (this._power === -2) {
    return this._f2;
  }
  if (this._power === -1) {
    return this._f1;
  }
  if (this._power < -4) {
    return new noname.NumberFormat(Number.POSITIVE_INFINITY);
  }
  if (this._power > 6) {
    return new noname.NumberFormat(1, true);
  }
  return this._f0;
};
noname.NumberTickSelector.prototype.next = function() {
  if (this._factor === 1) {
    this._factor = 2;
    return true;
  }
  if (this._factor === 2) {
    this._factor = 5;
    return true;
  }
  if (this._factor === 5) {
    this._power++;
    this._factor = 1;
    return true;
  }
  throw new Error("Factor should be 1, 2 or 5: " + this._factor);
};
noname.NumberTickSelector.prototype.previous = function() {
  if (this._factor === 1) {
    this._factor = 5;
    this._power--;
    return true;
  }
  if (this._factor === 2) {
    this._factor = 1;
    return true;
  }
  if (this._factor === 5) {
    this._factor = 2;
    return true;
  }
  throw new Error("Factor should be 1, 2 or 5: " + this._factor);
};
var d3;
noname.OrdinalAxis = function(label) {
  if (!(this instanceof noname.OrdinalAxis)) {
    return new noname.OrdinalAxis;
  }
  this.label = label;
  this.lowerMargin = 0.05;
  this.upperMargin = 0.05;
  this.listeners = [];
  this.scaleD3 = d3.scale.ordinal();
};
noname.OrdinalAxis.prototype.addListener = function(listener) {
  this.listeners.push(listener);
};
noname.TickMark = function(value, label) {
  if (!(this instanceof noname.TickMark)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.value = value;
  this.label = label;
};
noname.TickMark.prototype.toString = function() {
  return this.label;
};
noname.CategoryPlot = function(dataset) {
  if (!(this instanceof noname.CategoryPlot)) {
    return new noname.CategoryPlot;
  }
  this.xAxis = new noname.OrdinalAxis;
  this.yAxis = new noname.LinearAxis;
  this.dataset = dataset;
  this.dataset.addListener(this.datasetChanged);
  this.colors = d3.scale.category10();
  this.colors.domain(this.dataset.rowKeys);
  this.listeners = [];
  this.rotateTickLabels = false;
};
noname.CategoryPlot.prototype.addListener = function(listener) {
  this.listeners.push(listener);
};
noname.CategoryPlot.prototype.datasetChanged = function(dataset) {
};
noname.CategoryPlot.prototype.build = function(chart, svg) {
  var plotg = svg.append("g");
  var dx = chart.margin().left;
  var dy = chart.margin().top;
  plotg.attr("transform", "translate(" + dx.toString() + "," + dy.toString() + ")");
  var w = chart.width() - chart.margin().left - chart.margin().right;
  this.xAxis.scaleD3.domain(this.dataset.data.columnKeys).rangeBands([0, w]);
  var bounds = this.renderer.bounds(this.dataset);
  var ymin = bounds[0];
  var ymax = bounds[1];
  var h = chart.height() - chart.margin().top - chart.margin().bottom;
  var lowAdj;
  var highAdj;
  var yrange = ymax - ymin;
  if (yrange > 0) {
    lowAdj = this.yAxis.lowerMargin * yrange;
    highAdj = this.yAxis.upperMargin * yrange;
  } else {
    lowAdj = 0.5;
    highAdj = 0.5;
  }
  this.yAxis.scaleD3.domain([ymin - lowAdj, ymax + highAdj]).nice().range([h, 0]);
  var xAxisD3 = d3.svg.axis().scale(this.xAxis.scaleD3).tickSize(5, 3, 1).tickSubdivide(10).tickPadding(5).orient("bottom");
  var yAxisD3 = d3.svg.axis().scale(this.yAxis.scaleD3).tickSize(5, 3, 1).tickPadding(5).orient("left");
  var xAxisg = plotg.append("g").attr("class", "x axis").attr("transform", "translate(0," + h + ")").call(xAxisD3);
  if (this.xAxis.rotateTickLabels) {
    xAxisg.selectAll("text").attr("y", 0).attr("x", 9).attr("dy", ".35em").attr("transform", "rotate(90)").style("text-anchor", "start");
  }
  plotg.append("g").attr("class", "y axis").call(yAxisD3);
  if (this.xAxis.label) {
    plotg.append("text").attr("class", "x label").attr("text-anchor", "end").attr("x", w).attr("y", h - 6).text(this.xAxis.label);
  }
  if (this.yAxis.label) {
    plotg.append("text").attr("class", "y label").attr("text-anchor", "end").attr("y", 6).attr("dy", ".75em").attr("transform", "rotate(-90)").text(this.yAxis.label);
  }
  this.renderer.build(chart, this, this.xAxis.scaleD3, this.yAxis.scaleD3, plotg);
};
noname.CategoryPlot.prototype.legendInfo = function() {
  var info = [];
  var plot = this;
  this.dataset.rowKeys().forEach(function(key) {
    var item = new noname.LegendItemInfo;
    var dataset = plot._dataset;
    item.key = key;
    item.label = key;
    item.color = plot.renderer.colors(key);
    info.push(item);
  });
  return info;
};
noname.PiePlot = function(dataset) {
  if (!(this instanceof noname.PiePlot)) {
    return new noname.PiePlot;
  }
  this._dataset = dataset;
  this._dataset.addListener(this);
  this.colors = d3.scale.category10();
  this.colors.domain(this._dataset.keys);
  this.radius = 100;
  this.labelAnchorRadius = 1.1;
  this.innerRadius = 0;
  this.toolTipHandler = null;
  this.itemLabelGenerator = new noname.KeyedValueLabels;
  this.itemLabelGenerator.format = "{K}";
  this.listeners = [];
};
noname.PiePlot.prototype.addListener = function(listener) {
  this.listeners.push(listener);
};
noname.PiePlot.prototype.datasetChanged = function(dataset) {
  this.update(this.chart);
};
noname.PiePlot.prototype.setDataset = function(dataset) {
  this._dataset = dataset;
  this._dataset.addListener(this);
};
noname.PiePlot.prototype.update = function(chart) {
  var chartContainer = d3.select("#" + chart.elementId());
  var plotg = chartContainer.select(".plot");
  var arcInfo = this.layout(this._dataset.data.sections);
  var colors = this.colors;
  var toolTipHandler = this.toolTipHandler;
  var arcs = plotg.selectAll(".arc").data(arcInfo);
  arcs.enter().append("g").attr("class", "arc").append("path").attr("d", this.arcGenerator).style("fill", function(d) {
    return colors(d.data.key);
  }).style("stroke", "#2E2E2E");
  arcs.select("path").attr("d", this.arcGenerator).style("fill", function(d) {
    return colors(d.data.key);
  }).on("mouseover", function(datum) {
    if (toolTipHandler !== null) {
      toolTipHandler.mouseover(d3.event.target, datum.data.key, d3.event.target.__data__);
    }
  }).on("mouseout", function(d) {
    if (toolTipHandler !== null) {
    }
  });
  arcs.exit().remove();
  var labels = plotg.selectAll(".labels").data(arcInfo);
  var labelFactor = this.labelAnchorRadius;
  var radius = this.radius;
  labels.enter().append("text").attr("class", "labels").text(function(d) {
    return d.data.key;
  }).attr("dy", ".35em");
  labels.text(function(d) {
    return d.data.key;
  }).attr("transform", function(d) {
    var mid = (d.endAngle + d.startAngle) / 2;
    var xx = radius * Math.sin(mid) * labelFactor;
    var yy = -radius * Math.cos(mid) * labelFactor;
    return "translate(" + [xx, yy] + ")";
  }).style("text-anchor", function(d) {
    var mid = (d.endAngle + d.startAngle) / 2;
    if (mid < Math.PI / 6) {
      return "middle";
    }
    if (mid < Math.PI * 5 / 6) {
      return "start";
    }
    if (mid < Math.PI * 7 / 6) {
      return "middle";
    }
    if (mid < Math.PI * 11 / 6) {
      return "end";
    }
    return "middle";
  });
  labels.exit().remove();
};
noname.PiePlot.prototype.build = function(chart, svg) {
  var plotg = svg.append("g").attr("class", "plot");
  var w = chart.width() - chart.margin().left - chart.margin().right;
  var h = chart.height() - chart.margin().top - chart.margin().bottom;
  var dx = chart.margin().left + Math.round(w / 2);
  var dy = chart.margin().top + Math.round(h / 2);
  plotg.attr("transform", "translate(" + dx + "," + dy + ")");
  this.radius = Math.min(w, h) / 2 - 10;
  this.layout = d3.layout.pie().sort(null).value(function(d) {
    return d.value;
  });
  this.arcGenerator = d3.svg.arc().outerRadius(this.radius).innerRadius(this.innerRadius);
  this.update(chart);
};
noname.PiePlot.prototype.legendInfo = function() {
  var info = [];
  var me = this;
  this._dataset.keys().forEach(function(key) {
    var item = new noname.LegendItemInfo;
    var dataset = me._dataset;
    var index = dataset.indexOf(key);
    item.key = key;
    item.label = me.itemLabelGenerator.itemLabel(dataset, index);
    item.color = noname.Color.fromStr(me.colors(key));
    info.push(item);
  });
  return info;
};
noname.XYPlot = function(dataset) {
  if (!(this instanceof noname.XYPlot)) {
    throw new Error("Use 'new' for construction.");
  }
  this._listeners = [];
  this._plotBackground = null;
  this._dataBackground = new noname.StandardRectanglePainter(new noname.Color(230, 230, 230), new noname.Color(0, 0, 0, 0));
  this._renderer = new noname.ScatterRenderer(this);
  this.updateBounds = false;
  this._axisOffsets = new noname.Insets(0, 0, 0, 0);
  this._xAxis = new noname.LinearAxis;
  this._xAxisPosition = noname.RectangleEdge.BOTTOM;
  this._xAxis.configureAsXAxis(this);
  var xAxisListener = function(p) {
    var plot = p;
    return function(axis) {
      plot.notifyListeners();
    };
  }(this);
  this._xAxis.addListener(xAxisListener);
  this._yAxis = new noname.LinearAxis;
  this._yAxisPosition = noname.RectangleEdge.LEFT;
  this._yAxis.configureAsYAxis(this);
  var yAxisListener = function(p) {
    var plot = p;
    return function(axis) {
      plot.notifyListeners();
    };
  }(this);
  this._yAxis.addListener(yAxisListener);
  this.setDataset(dataset);
  this.itemLabelGenerator = new noname.XYLabels;
};
noname.XYPlot.prototype.getDataset = function() {
  return this._dataset;
};
noname.XYPlot.prototype.setDataset = function(dataset, notify) {
  if (this._datasetListener) {
    this._dataset.removeListener(this._datasetListener);
  }
  this._dataset = dataset;
  this._datasetListener = function(plot) {
    var me = plot;
    return function(dataset) {
      me.updateBounds = true;
      me.update(plot.chart);
    };
  }(this);
  this._dataset.addListener(this._datasetListener);
  this._xAxis.configureAsXAxis(this);
  this._yAxis.configureAsYAxis(this);
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.XYPlot.prototype.getBackground = function() {
  return this._plotBackground;
};
noname.XYPlot.prototype.setBackground = function(painter, notify) {
  this._plotBackground = painter;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.XYPlot.prototype.setBackgroundColor = function(color, notify) {
  var painter = new noname.StandardRectanglePainter(color, null);
  this.setBackground(painter, notify);
};
noname.XYPlot.prototype.getDataBackground = function() {
  return this._dataBackground;
};
noname.XYPlot.prototype.setDataBackground = function(painter, notify) {
  this._dataBackground = painter;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.XYPlot.prototype.setDataBackgroundColor = function(color, notify) {
  var painter = new noname.StandardRectanglePainter(color, null);
  this.setDataBackground(painter, notify);
};
noname.XYPlot.prototype.datasetChanged = function() {
  if (this._xAxis.isAutoRange()) {
  }
  if (this._yAxis.isAutoRange()) {
  }
  this.notifyListeners();
};
noname.XYPlot.prototype.getAxisOffsets = function() {
  return this._axisOffsets;
};
noname.XYPlot.prototype.setAxisOffsets = function(offsets, notify) {
  this._axisOffsets = offsets;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.XYPlot.prototype.getXAxis = function() {
  return this._xAxis;
};
noname.XYPlot.prototype.getXAxisPosition = function() {
  return this._xAxisPosition;
};
noname.XYPlot.prototype.setXAxisPosition = function(edge, notify) {
  this.xAxisPosition = edge;
  if (notify !== false) {
    this.notifyListeners(this);
  }
};
noname.XYPlot.prototype.isXZoomable = function() {
  return true;
};
noname.XYPlot.prototype.getYAxis = function() {
  return this._yAxis;
};
noname.XYPlot.prototype.getYAxisPosition = function() {
  return this._yAxisPosition;
};
noname.XYPlot.prototype.setYAxisPosition = function(edge, notify) {
  this.yAxisPosition = edge;
  if (notify !== false) {
    this.notifyListeners(this);
  }
};
noname.XYPlot.prototype.isYZoomable = function() {
  return true;
};
noname.XYPlot.prototype.zoomX = function(factor, anchor, notify) {
  var x0 = this._dataArea.minX();
  var x1 = this._dataArea.maxX();
  var anchorX = this._xAxis.coordinateToValue(anchor, x0, x1);
  this._xAxis.resizeRange(factor, anchorX, notify !== false);
};
noname.XYPlot.prototype.zoomY = function(factor, anchor, notify) {
  var y0 = this._dataArea.minY();
  var y1 = this._dataArea.maxY();
  var anchorY = this._yAxis.coordinateToValue(anchor, y1, y0);
  this._yAxis.resizeRange(factor, anchorY, notify !== false);
};
noname.XYPlot.prototype.panX = function(percent, notify) {
  this._xAxis.pan(percent, notify !== false);
};
noname.XYPlot.prototype.panY = function(percent, notify) {
  this._yAxis.pan(percent, notify !== false);
};
noname.XYPlot.prototype.addListener = function(f) {
  this._listeners.push(f);
};
noname.XYPlot.prototype.notifyListeners = function() {
  var plot = this;
  this._listeners.forEach(function(f) {
    f(plot);
  });
};
noname.XYPlot.prototype.dataset = function(dataset) {
  throw new Error("Use setDataset().");if (!arguments.length) {
    return this._dataset;
  }
  this._dataset = dataset;
  this._dataset.addListener(this);
};
noname.XYPlot.prototype.setRenderer = function(renderer, notify) {
  this._renderer = renderer;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.XYPlot.prototype.update = function(chart) {
  var chartContainer = d3.select("#" + chart.elementId());
  var plotg = chartContainer.select(".plot");
  if (this.updateBounds) {
    var bounds = this._dataset.bounds();
    var xmin = bounds[0];
    var xmax = bounds[1];
    var ymin = bounds[2];
    var ymax = bounds[3];
    this._xAxis.updateDomain(xmin, xmax);
    this._yAxis.updateDomain(ymin, ymax);
    this.zoomD3.x(this._xAxis.scaleD3).y(this._yAxis.scaleD3).scale(1).translate([0, 0]);
    this.updateBounds = false;
  }
  plotg.selectAll("g.x.axis").call(this.xAxisD3);
  plotg.selectAll("g.y.axis").call(this.yAxisD3);
  this.renderer.update(chart, this, this._xAxis.scaleD3, this._yAxis.scaleD3, plotg);
};
noname.XYPlot.prototype.build = function(chart, svg) {
  var plotg = svg.append("g").attr("class", "plot");
  var dx = chart.margin().left;
  var dy = chart.margin().top;
  plotg.attr("transform", "translate(" + dx.toString() + "," + dy.toString() + ")");
  var bounds = this._dataset.bounds();
  var xmin = bounds[0];
  var xmax = bounds[1];
  var ymin = bounds[2];
  var ymax = bounds[3];
  var w = chart.width() - chart.margin().left - chart.margin().right;
  var h = chart.height() - chart.margin().top - chart.margin().bottom;
  this._xAxis.updateDomain(xmin, xmax);
  this._xAxis.scaleD3.range([0, w]);
  this._yAxis.updateDomain(ymin, ymax);
  this._yAxis.scaleD3.range([h, 0]);
  this.xAxisD3 = d3.svg.axis().scale(this._xAxis.scaleD3).tickSize(5, 3, 1).tickSubdivide(10).tickPadding(5).orient("bottom");
  this.yAxisD3 = d3.svg.axis().scale(this._yAxis.scaleD3).tickSize(5, 3, 1).tickPadding(5).orient("left");
  plotg.append("g").attr("class", "x axis").attr("transform", "translate(0," + h + ")").call(this.xAxisD3);
  plotg.append("g").attr("class", "y axis").call(this.yAxisD3);
  if (this._xAxis._label) {
    plotg.append("text").attr("class", "x label").attr("text-anchor", "end").attr("x", w).attr("y", h - 6).text(this._xAxis._label);
  }
  if (this._yAxis._label) {
    plotg.append("text").attr("class", "y label").attr("text-anchor", "end").attr("y", 6).attr("dy", ".75em").attr("transform", "rotate(-90)").text(this._yAxis._label);
  }
  svg.append("clipPath").attr("id", "clip1").append("rect").attr("width", w).attr("height", h);
  var datag = plotg.append("g").attr("clip-path", "url(#clip1)").attr("pointer-events", "all");
  datag.append("rect").attr("width", w).attr("height", h).attr("visibility", "hidden");
  var xAxisD3 = this.xAxisD3;
  var yAxisD3 = this.yAxisD3;
  var plot = this;
  var zoom = function() {
    plot.update(plot.chart);
  };
  this.zoomD3 = d3.behavior.zoom().x(this._xAxis.scaleD3).y(this._yAxis.scaleD3).on("zoom", zoom);
  datag.call(this.zoomD3);
  this.renderer.build(chart, this, this._xAxis.scaleD3, this._yAxis.scaleD3, datag);
};
noname.XYPlot.prototype.datasetChanged = function(dataset) {
  this.updateBounds = true;
  this.update(this.chart);
};
noname.XYPlot.prototype.autoCalcBounds = function() {
  this.updateBounds = true;
  this.update(this.chart);
};
noname.XYPlot.prototype.draw = function(ctx, bounds, plotArea) {
  if (this._plotBackground) {
    this._plotBackground.paint(ctx, plotArea);
  }
  var space = new noname.AxisSpace(0, 0, 0, 0);
  var edge = this.axisPosition(this._xAxis);
  var xspace = this._xAxis.reserveSpace(ctx, this, bounds, plotArea, edge);
  space.extend(xspace, edge);
  var adjArea = space.innerRect(plotArea);
  edge = this.axisPosition(this._yAxis);
  var yspace = this._yAxis.reserveSpace(ctx, this, bounds, adjArea, edge);
  space.extend(yspace, edge);
  this._dataArea = space.innerRect(plotArea);
  if (this._dataBackground) {
    this._dataBackground.paint(ctx, this._dataArea);
  }
  this.drawAxes(ctx, bounds, this._dataArea);
  ctx.setHint("cursor", "move");
  ctx.setHint("clip", this._dataArea);
  ctx.setHint("glass", this._dataArea);
  ctx.beginGroup("dataArea");
  var passCount = this._renderer.passCount();
  for (var pass = 0;pass < passCount;pass++) {
    for (var s = 0;s < this._dataset.seriesCount();s++) {
      for (var i = 0;i < this._dataset.itemCount(s);i++) {
        this._renderer.drawItem(ctx, this._dataArea, this, this._dataset, s, i, pass);
      }
    }
  }
  ctx.endGroup();
};
noname.XYPlot.prototype.dataArea = function() {
  return this._dataArea;
};
noname.XYPlot.prototype.drawAxes = function(ctx, bounds, dataArea) {
  var offset = this._axisOffsets.value(this._xAxisPosition);
  this._xAxis.draw(ctx, this, bounds, dataArea, offset);
  offset = this._axisOffsets.value(this._yAxisPosition);
  this._yAxis.draw(ctx, this, bounds, dataArea, offset);
};
noname.XYPlot.prototype.axisPosition = function(axis) {
  if (axis === this._xAxis) {
    return this._xAxisPosition;
  } else {
    if (axis === this._yAxis) {
      return this._yAxisPosition;
    }
  }
};
noname.XYPlot.prototype.legendInfo = function() {
  var info = [];
  var plot = this;
  this._dataset.seriesKeys().forEach(function(key) {
    var item = new noname.LegendItemInfo;
    var dataset = plot._dataset;
    var index = dataset.seriesIndex(key);
    item.key = key;
    item.label = key;
    item.color = plot._renderer.getLineColorSource().getLegendColor(index);
    info.push(item);
  });
  return info;
};
noname.ColorSource = function(colors) {
  if (!(this instanceof noname.ColorSource)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._colors = colors;
};
noname.ColorSource.prototype.getColor = function(series, item) {
  return this._colors[series % this._colors.length];
};
noname.ColorSource.prototype.getLegendColor = function(series) {
  return this._colors[series % this._colors.length];
};
noname.BaseXYRenderer = function(instance) {
  if (!(this instanceof noname.BaseXYRenderer)) {
    throw new Error("Use 'new' for constructor.");
  }
  if (!instance) {
    instance = this;
  }
  noname.BaseXYRenderer.init(instance);
};
noname.BaseXYRenderer.init = function(instance) {
  var lineColors = noname.Colors.colorsAsObjects(noname.Colors.fancyLight());
  var fillColors = noname.Colors.colorsAsObjects(noname.Colors.fancyLight());
  instance._lineColorSource = new noname.ColorSource(lineColors);
  instance._fillColorSource = new noname.ColorSource(fillColors);
  instance._listeners = [];
};
noname.BaseXYRenderer.prototype.getLineColorSource = function() {
  return this._lineColorSource;
};
noname.BaseXYRenderer.prototype.setLineColorSource = function(cs, notify) {
  this._lineColorSource = cs;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.BaseXYRenderer.prototype.getFillColorSource = function() {
  return this._fillColorSource;
};
noname.BaseXYRenderer.prototype.setFillColorSource = function(cs, notify) {
  this._fillColorSource = cs;
  if (notify !== false) {
    this.notifyListeners();
  }
};
noname.BaseXYRenderer.prototype.passCount = function() {
  return 1;
};
noname.BaseXYRenderer.prototype.addListener = function(f) {
  this._listeners.push(f);
};
noname.BaseXYRenderer.prototype.notifyListeners = function() {
  var plot = this;
  this._listeners.forEach(function(f) {
    f(plot);
  });
};
noname.BarRenderer = function() {
  if (!(this instanceof noname.BarRenderer)) {
    return new noname.BarRenderer;
  }
  this.colors = d3.scale.category10().range(noname.Colors.blueOcean());
  this.toolTipHandler = null;
  this.listeners = [];
};
noname.BarRenderer.prototype.bounds = function(dataset) {
  return dataset.bounds();
};
noname.BarRenderer.prototype.build = function(chart, plot, xscale, yscale, datag) {
  datag.append("g").attr("class", "bar");
  this.update(chart, plot, xscale, yscale, datag);
};
noname.BarRenderer.prototype.update = function(chart, plot, xscale, yscale, datag) {
  var renderg = datag.select(".bar");
  var colorFunction = this.colors;
  var seriesg = renderg.selectAll(".series").data(plot.dataset.data.rows);
  var toolTipHandler = this.toolTipHandler;
  var seriesCount = plot.dataset.rowCount();
  var barWidth = xscale.rangeBand() / seriesCount;
  seriesg.enter().append("g").attr("class", "series").attr("noname:seriesKey", function(d) {
    return d.key;
  });
  var itemg = seriesg.selectAll("rect").data(function(d) {
    return d.values;
  });
  itemg.enter().append("rect").attr("transform", function(d, i) {
    var rowKey = this.parentElement.__data__.key;
    var row = plot.dataset.rowIndex(rowKey);
    return "translate(" + (i * xscale.rangeBand() + row * barWidth) + ",0)";
  }).style("fill", function(d) {
    var seriesKey = this.parentElement.__data__.key;
    return colorFunction(seriesKey);
  });
  itemg.attr("y", function(d) {
    return Math.min(yscale(0), yscale(d));
  }).attr("height", function(d) {
    return Math.abs(yscale(0) - yscale(d));
  }).attr("width", xscale.rangeBand() / seriesCount);
  itemg.exit().remove();
};
noname.HistogramRenderer = function() {
  if (!(this instanceof noname.HistogramRenderer)) {
    return new noname.HistogramRenderer;
  }
  this.colors = d3.scale.category10();
  this.toolTipHandler = null;
  this.listeners = [];
};
noname.HistogramRenderer.prototype.build = function(chart, plot, xscale, yscale, datag) {
  datag.append("g").attr("class", "HistogramRenderer");
  this.update(chart, plot, xscale, yscale, datag);
};
noname.HistogramRenderer.prototype.update = function(chart, plot, xscale, yscale, datag) {
  var renderg = datag.select(".HistogramRenderer");
  var colorFunction = this.colors;
  var binsg = renderg.selectAll(".bins").data(plot.dataset.bins);
  binsg.enter().append("rect").attr("transform", function(bin, i) {
    return "translate(" + xscale(bin.xmin) + ",0)";
  }).style("fill", function(bin) {
    return colorFunction(bin);
  });
  binsg.attr("y", function(bin) {
    var zero = yscale(0);
    var y = yscale(bin.count);
    return Math.min(yscale(0), yscale(bin.count));
  }).attr("height", function(bin) {
    var height = Math.abs(yscale(0) - yscale(bin.count));
    return height;
  }).attr("width", function(bin) {
    return xscale(bin.xmax) - xscale(bin.xmin);
  });
  binsg.exit().remove();
};
noname.LineRenderer = function() {
  if (!(this instanceof noname.LineRenderer)) {
    return new noname.LineRenderer;
  }
  this.colors = d3.scale.category10();
  this.toolTipHandler = null;
  this.listeners = [];
};
noname.LineRenderer.prototype.bounds = function(dataset) {
  return dataset.bounds();
};
noname.LineRenderer.prototype.build = function(chart, plot, xscale, yscale, datag) {
  datag.append("g").attr("class", "line");
  this.update(chart, plot, xscale, yscale, datag);
};
noname.LineRenderer.prototype.update = function(chart, plot, xscale, yscale, datag) {
  var renderg = datag.select(".line");
  var colorFunction = this.colors;
  var seriesg = renderg.selectAll(".series").data(plot.dataset.data.rows);
  var toolTipHandler = this.toolTipHandler;
  var lineGenerator = d3.svg.line().x(function(d, i) {
    var colkey = plot.dataset.data.columnKeys[i];
    var x0 = xscale(colkey);
    var ww = xscale.rangeBand();
    return x0 + ww / 2;
  }).y(function(d) {
    var yy = yscale(d);
    return yy;
  }).interpolate("linear");
  seriesg.enter().append("g").attr("class", "series").attr("noname:seriesKey", function(d) {
    return d.key;
  }).append("path").attr("d", function(d) {
    return lineGenerator(d.values);
  }).style("stroke", function(d) {
    return colorFunction(d.key);
  }).style("fill", "none");
  seriesg.select("path").attr("d", function(d) {
    return lineGenerator(d.values);
  });
};
noname.ScatterRenderer = function(plot) {
  if (!(this instanceof noname.ScatterRenderer)) {
    throw new Error("Use 'new' for constructors.");
  }
  noname.BaseXYRenderer.init(this);
  this._plot = plot;
  this._radius = 3;
};
noname.ScatterRenderer.prototype = new noname.BaseXYRenderer;
noname.ScatterRenderer.prototype.itemFillColorStr = function(seriesKey, itemKey) {
  var dataset = this._plot.getDataset();
  var c = dataset.getProperty(seriesKey, itemKey, "color");
  if (c) {
    return c;
  }
  var color = this.itemFillColor(seriesKey, itemKey);
  return color.rgbaStr();
};
noname.ScatterRenderer.prototype.itemFillColor = function(seriesKey, itemKey) {
  var dataset = this._plot.getDataset();
  var seriesIndex = dataset.seriesIndex(seriesKey);
  var itemIndex = dataset.itemIndex(seriesKey, itemKey);
  return this._lineColorSource.getColor(seriesIndex, itemIndex);
};
noname.ScatterRenderer.prototype.itemStrokeColor = function(seriesKey, itemKey) {
  if (this._plot._dataset.isSelected("select", seriesKey, itemKey)) {
    return "red";
  }
  return "none";
};
noname.ScatterRenderer.prototype.drawItem = function(ctx, dataArea, plot, dataset, seriesIndex, itemIndex, pass) {
  var seriesKey = dataset.seriesKey(seriesIndex);
  var itemKey = dataset.getItemKey(seriesIndex, itemIndex);
  var x = dataset.x(seriesIndex, itemIndex);
  var y = dataset.y(seriesIndex, itemIndex);
  var xx = plot.getXAxis().valueToCoordinate(x, dataArea.minX(), dataArea.maxX());
  var yy = plot.getYAxis().valueToCoordinate(y, dataArea.maxY(), dataArea.minY());
  var str = dataset.getProperty(seriesKey, itemKey, "color");
  var color;
  if (str) {
    color = noname.Color.fromStr(str);
  } else {
    color = this.itemFillColor(seriesKey, itemKey);
  }
  ctx.setFillColor(color);
  ctx.drawCircle(xx, yy, this._radius);
};
noname.StackedAreaRenderer = function() {
  if (!(this instanceof noname.StackedAreaRenderer)) {
    return new noname.StackedAreaRenderer;
  }
  this.colors = d3.scale.category10().range(noname.Colors.fancyLight());
  this.toolTipHandler = null;
  this.listeners = [];
  this.offset = "zero";
};
noname.StackedAreaRenderer.prototype.bounds = function(dataset) {
  var bounds = dataset.bounds();
  return[bounds[0] - 6, bounds[1] + 6];
};
noname.StackedAreaRenderer.prototype.layers = function(dataset) {
  var layers = [];
  var rowKeys = dataset.rowKeys();
  var rowObjs = [];
  rowKeys.forEach(function(rowKey) {
    rowObjs[rowKey] = {rowKey:rowKey, values:[]};
    layers.push(rowObjs[rowKey]);
  });
  var colKeys = dataset.columnKeys();
  colKeys.forEach(function(colKey) {
    rowKeys.forEach(function(rowKey) {
      rowObjs[rowKey].values.push({colKey:colKey, value:dataset.valueByKey(rowKey, colKey)});
    });
  });
  return layers;
};
noname.StackedAreaRenderer.prototype.build = function(chart, plot, xscale, yscale, datag) {
  datag.append("g").attr("class", "stackedarea");
  this.update(chart, plot, xscale, yscale, datag);
};
noname.StackedAreaRenderer.prototype.update = function(chart, plot, xscale, yscale, datag) {
  var renderg = datag.select(".stackedarea");
  var colorFunction = this.colors;
  var layers = this.layers(plot.dataset);
  var stack = d3.layout.stack().offset(this.offset).values(function(row) {
    return row.values;
  }).x(function(d) {
    return xscale(d.colKey) + xscale.rangeBand() / 2;
  }).y(function(d) {
    return d.value;
  });
  var seriesg = renderg.selectAll(".series").data(stack(layers)).enter().append("g").attr("class", "series");
  yscale.domain([0, d3.max(layers, function(c) {
    return d3.max(c.values, function(d) {
      return d.y0 + d.y;
    });
  })]);
  var toolTipHandler = this.toolTipHandler;
  var area = d3.svg.area().interpolate("cardinal").x(function(d) {
    return xscale(d.colKey) + xscale.rangeBand() / 2;
  }).y0(function(d) {
    return yscale(d.y0);
  }).y1(function(d) {
    return yscale(d.y0 + d.y);
  });
  seriesg.append("path").attr("class", "streamPath").attr("d", function(d) {
    return area(d.values);
  }).style("fill", function(d) {
    return colorFunction(d.rowKey);
  });
};
noname.StackedBarRenderer = function() {
  if (!(this instanceof noname.StackedBarRenderer)) {
    return new noname.StackedBarRenderer;
  }
  this.colors = d3.scale.category10().range(noname.Colors.iceCube());
  this.toolTipHandler = null;
  this.listeners = [];
};
noname.StackedBarRenderer.prototype.bounds = function(dataset) {
  var stackBaseValues = noname.DatasetUtils.extractStackBaseValues(dataset);
  var rowKeys = dataset.rowKeys();
  var columnKeys = dataset.columnKeys();
  var min;
  var max;
  rowKeys.forEach(function(rowKey) {
    columnKeys.forEach(function(columnKey) {
      var base = stackBaseValues.valueByKey(rowKey, columnKey);
      var y = dataset.valueByKey(rowKey, columnKey);
      if (min) {
        min = Math.min(min, base + y);
      } else {
        min = base + y;
      }
      if (max) {
        max = Math.max(max, base + y);
      } else {
        max = base + y;
      }
    });
  });
  return[min, max];
};
noname.StackedBarRenderer.prototype.build = function(chart, plot, xscale, yscale, datag) {
  datag.append("g").attr("class", "stackedbar");
  this.update(chart, plot, xscale, yscale, datag);
};
noname.StackedBarRenderer.prototype.update = function(chart, plot, xscale, yscale, datag) {
  var renderg = datag.select(".stackedbar");
  var colorFunction = this.colors;
  var stackBaseValues = noname.DatasetUtils.extractStackBaseValues(plot.dataset);
  var seriesg = renderg.selectAll(".series").data(plot.dataset.data.rows);
  var toolTipHandler = this.toolTipHandler;
  var barWidth = xscale.rangeBand();
  seriesg.enter().append("g").attr("class", "series").attr("noname:seriesKey", function(d) {
    return d[0];
  });
  var itemg = seriesg.selectAll("rect").data(function(d) {
    return d.values;
  });
  itemg.enter().append("rect").attr("transform", function(d, i) {
    return "translate(" + i * xscale.rangeBand() + ",0)";
  }).style("fill", function(d) {
    var seriesKey = this.parentElement.__data__.key;
    return colorFunction(seriesKey);
  });
  itemg.attr("y", function(d, i) {
    var rowKey = this.parentElement.__data__.key;
    var row = plot.dataset.rowIndex(rowKey);
    var base = stackBaseValues.valueByIndex(row, i);
    return Math.min(yscale(base), yscale(base + d));
  }).attr("height", function(d) {
    return Math.abs(yscale(0) - yscale(d));
  }).attr("width", xscale.rangeBand());
  itemg.exit().remove();
};
noname.XYLineRenderer = function() {
  if (!(this instanceof noname.XYLineRenderer)) {
    return new noname.XYLineRenderer;
  }
  noname.BaseXYRenderer.init(this);
};
noname.XYLineRenderer.prototype = new noname.BaseXYRenderer;
noname.XYLineRenderer.prototype.build = function(chart, plot, xscale, yscale, datag) {
  datag.append("g").attr("class", "line");
  this.update(chart, plot, xscale, yscale, datag);
};
noname.XYLineRenderer.prototype.update = function(chart, plot, xscale, yscale, datag) {
  var renderg = datag.select(".line");
  var colorFunction = this.colors;
  var seriesg = renderg.selectAll(".series").data(plot._dataset.data.series);
  var toolTipHandler = this.toolTipHandler;
  var lineGenerator = d3.svg.line().x(function(d) {
    return xscale(d.x);
  }).y(function(d) {
    return yscale(d.y);
  }).interpolate("linear");
  seriesg.enter().append("g").attr("class", "series").attr("noname:seriesKey", function(d) {
    return d.seriesKey;
  }).append("path").attr("d", function(d) {
    return lineGenerator(d.items);
  }).style("stroke", function(d) {
    return colorFunction(d.seriesKey);
  }).style("fill", "none");
  seriesg.select("path").attr("d", function(d) {
    return lineGenerator(d.items);
  });
};
noname.XYLineRenderer.prototype.passCount = function() {
  return 2;
};
noname.XYLineRenderer.prototype.drawItem = function(ctx, dataArea, plot, dataset, seriesIndex, itemIndex, pass) {
  var x = dataset.x(seriesIndex, itemIndex);
  var y = dataset.y(seriesIndex, itemIndex);
  var xx = plot.getXAxis().valueToCoordinate(x, dataArea.x(), dataArea.x() + dataArea.width());
  var yy = plot.getYAxis().valueToCoordinate(y, dataArea.y() + dataArea.height(), dataArea.y());
  if (pass === 0) {
    if (itemIndex > 0) {
      var x0 = dataset.x(seriesIndex, itemIndex - 1);
      var y0 = dataset.y(seriesIndex, itemIndex - 1);
      var xx0 = plot.getXAxis().valueToCoordinate(x0, dataArea.x(), dataArea.x() + dataArea.width());
      var yy0 = plot.getYAxis().valueToCoordinate(y0, dataArea.y() + dataArea.height(), dataArea.y());
      ctx.setLineColor(this._lineColorSource.getColor(seriesIndex, itemIndex));
      ctx.setLineStroke(new noname.Stroke(3));
      ctx.drawLine(xx0, yy0, xx, yy);
    }
  } else {
    if (pass === 1) {
    }
  }
};
noname.LogEventHandler = function() {
  if (!(this instanceof noname.LogEventHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.modifier = new noname.Modifier;
  this._log = false;
};
noname.LogEventHandler.prototype.mouseDown = function(e) {
  if (!this._log) {
    return;
  }
  console.log("DOWN: clientX = " + e.clientX + ", y = " + e.clientY);
};
noname.LogEventHandler.prototype.mouseMove = function(e) {
  if (!this._log) {
    return;
  }
  console.log("MOVE: clientX = " + e.clientX + ", y = " + e.clientY);
};
noname.LogEventHandler.prototype.mouseUp = function(e) {
  if (!this._log) {
    return;
  }
  console.log("UP: clientX = " + e.clientX + ", y = " + e.clientY);
};
noname.LogEventHandler.prototype.mouseOver = function(e) {
  if (!this._log) {
    return;
  }
  console.log("OVER: clientX = " + e.clientX + ", y = " + e.clientY);
};
noname.LogEventHandler.prototype.mouseOut = function(e) {
  if (!this._log) {
    return;
  }
  console.log("OUT: clientX = " + e.clientX + ", y = " + e.clientY);
};
noname.LogEventHandler.prototype.mouseWheel = function(e) {
  if (!this._log) {
    return false;
  }
  console.log("WHEEL : " + e.wheelDelta);
  return false;
};
noname.Modifier = function(altKey, ctrlKey, metaKey, shiftKey) {
  if (!(this instanceof noname.Modifier)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.altKey = altKey || false;
  this.ctrlKey = ctrlKey || false;
  this.metaKey = metaKey || false;
  this.shiftKey = shiftKey || false;
};
noname.Modifier.prototype.matches = function(other) {
  if (this.altKey !== other.altKey) {
    return false;
  }
  if (this.ctrlKey !== other.ctrlKey) {
    return false;
  }
  if (this.metaKey !== other.metaKey) {
    return false;
  }
  if (this.shiftKey !== other.shiftKey) {
    return false;
  }
};
noname.PanHandler = function(manager, modifier) {
  if (!(this instanceof noname.PanHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.manager = manager;
  this.modifier = modifier;
  this._lastPoint = null;
};
noname.PanHandler.prototype.mouseDown = function(e) {
  var x = e.clientX;
  var y = e.clientY;
  var dataArea = this.manager.getChart().plotArea();
  this._lastPoint = dataArea.constrainedPoint(x, y);
};
noname.PanHandler.prototype.mouseMove = function(e) {
  if (this._lastPoint === null) {
    return;
  }
  var x = e.clientX;
  var y = e.clientY;
  var dx = x - this._lastPoint.x();
  var dy = y - this._lastPoint.y();
  if (dx !== 0 || dy !== 0) {
    this._lastPoint = new noname.Point2D(x, y);
    var plot = this.manager.getChart().getPlot();
    var dataArea = plot.dataArea();
    var wpercent = -dx / dataArea.width();
    var hpercent = dy / dataArea.height();
    plot.panX(wpercent, false);
    plot.panY(hpercent);
  }
};
noname.PanHandler.prototype.mouseUp = function(e) {
  this._lastPoint = null;
};
noname.PanHandler.prototype.mouseOver = function(e) {
};
noname.PanHandler.prototype.mouseOut = function(e) {
};
noname.PanHandler.prototype.mouseWheel = function(e) {
};
noname.WheelHandler = function(manager, modifier) {
  if (!(this instanceof noname.WheelHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.manager = manager;
  this.modifier = modifier;
};
proto___ = noname.WheelHandler.prototype;
proto___.mouseDown = function(e) {
};
proto___.mouseMove = function(e) {
};
proto___.mouseUp = function(e) {
};
proto___.mouseOver = function(e) {
};
proto___.mouseOut = function(e) {
};
proto___.mouseWheel = function(e) {
  var delta;
  if (e.wheelDelta) {
    delta = e.wheelDelta / 720 * 0.2 + 1;
  } else {
    delta = e.detail * -0.05 + 1;
  }
  var plot = this.manager.getChart().getPlot();
  var zoomX = plot.isXZoomable();
  var zoomY = plot.isYZoomable();
  if (zoomX) {
    plot.zoomX(delta, e.clientX, !zoomY);
  }
  if (zoomY) {
    var svg = document.getElementById("chart_svg");
    plot.zoomY(delta, e.clientY - svg.getBoundingClientRect().top);
  }
  return!(zoomX || zoomY);
};
noname.ZoomHandler = function(manager, modifier) {
  if (!(this instanceof noname.ZoomHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.manager = manager;
  this.modifier = modifier;
  this.zoomPoint = null;
  this.zoomRectangle = null;
};
noname.ZoomHandler.prototype.mouseDown = function(e) {
  var x = e.clientX;
  var y = e.clientY;
  var dataArea = this.manager.chart.getPlot().dataArea();
  this.zoomPoint = dataArea.constrainedPoint(x, y);
};
noname.ZoomHandler.prototype.mouseMove = function(e) {
  if (this.zoomPoint === null) {
    return;
  }
  var x = e.clientX;
  var y = e.clientY;
};
noname.ZoomHandler.prototype.mouseUp = function(e) {
};
noname.ZoomHandler.prototype.mouseOver = function(e) {
};
noname.ZoomHandler.prototype.mouseOut = function(e) {
};
noname.ZoomHandler.prototype.mouseWheel = function(e) {
  return true;
};
noname.Bin = function(xmin, xmax, incmin, incmax) {
  this.xmin = xmin;
  this.xmax = xmax;
  this.incMin = incmin !== false;
  this.incMax = incmax !== false;
  this.count = 0;
};
noname.Bin.prototype.includes = function(value) {
  if (value < this.xmin) {
    return false;
  }
  if (value === this.xmin) {
    return this.incMin;
  }
  if (value > this.xmax) {
    return false;
  }
  if (value === this.xmax) {
    return this.incMax;
  }
  return true;
};
noname.Bin.prototype.overlaps = function(bin) {
  if (this.xmax < bin.xmin) {
    return false;
  }
  if (this.xmin > bin.xmax) {
    return false;
  }
  if (this.xmax === bin.xmin) {
    if (!(this.incMax && bin.incMin)) {
      return false;
    }
  }
  if (this.xmin === bin.xmax) {
    if (!(this.incMin && bin.incMax)) {
      return false;
    }
  }
  return true;
};
noname.DatasetUtils = {};
noname.DatasetUtils.extractStackBaseValues = function(dataset, baseline) {
  baseline = typeof baseline !== "undefined" ? baseline : 0;
  var result = new noname.KeyedValues2DDataset;
  var columnCount = dataset.columnCount();
  var rowCount = dataset.rowCount();
  for (var c = 0;c < columnCount;c++) {
    var columnKey = dataset.columnKey(c);
    var posBase = baseline;
    var negBase = baseline;
    for (var r = 0;r < rowCount;r++) {
      var y = dataset.valueByIndex(r, c);
      var rowKey = dataset.rowKey(r);
      if (r > 0) {
        if (y >= 0) {
          result.add(rowKey, columnKey, posBase);
        } else {
          result.add(rowKey, columnKey, negBase);
        }
      } else {
        result.add(rowKey, columnKey, baseline);
      }
      if (y > 0) {
        posBase = posBase + y;
      }
      if (y < 0) {
        negBase = negBase + y;
      }
    }
  }
  return result;
};
noname.DatasetUtils.extractXYDatasetFromColumns2D = function(dataset, xcol, ycol, seriesKey) {
  var result = new noname.XYDataset;
  seriesKey = seriesKey || "series 1";
  for (var r = 0;r < dataset.rowCount();r++) {
    var rowKey = dataset.rowKey(r);
    var x = dataset.valueByKey(rowKey, xcol);
    var y = dataset.valueByKey(rowKey, ycol);
    result.add(seriesKey, x, y);
    var xPropKeys = dataset.propertyKeys(rowKey, xcol);
    var yPropKeys = dataset.propertyKeys(rowKey, ycol);
    var itemKey = result.getItemKey(0, result.itemCount(0) - 1);
    xPropKeys.forEach(function(key) {
      var p = dataset.getProperty(rowKey, xcol, key);
      result.setProperty(seriesKey, itemKey, key, p);
    });
    yPropKeys.forEach(function(key) {
      var p = dataset.getProperty(rowKey, ycol, key);
      result.setProperty(seriesKey, itemKey, key, p);
    });
  }
  return result;
};
noname.DatasetUtils.extractXYDatasetFromRows2D = function(dataset, xrow, yrow, seriesKey) {
  var result = new noname.XYDataset;
  seriesKey = seriesKey || "series 1";
  for (var c = 0;c < dataset.columnCount();c++) {
    var colKey = dataset.columnKey(c);
    var x = dataset.valueByKey(xrow, colKey);
    var y = dataset.valueByKey(yrow, colKey);
    result.add(seriesKey, x, y);
    var xPropKeys = dataset.propertyKeys(xrow, colKey);
    var yPropKeys = dataset.propertyKeys(yrow, colKey);
    var itemKey = result.getItemKey(0, result.itemCount(0) - 1);
    xPropKeys.forEach(function(key) {
      var p = dataset.getProperty(xrow, colKey, key);
      result.setProperty(seriesKey, itemKey, key, p);
    });
    yPropKeys.forEach(function(key) {
      var p = dataset.getProperty(yrow, colKey, key);
      result.setProperty(seriesKey, itemKey, key, p);
    });
  }
  return result;
};
noname.DatasetUtils.extractXYDatasetFromColumns = function(dataset, xcol, ycol) {
  var result = new noname.XYDataset;
  for (var s = 0;s < dataset.seriesCount();s++) {
    var seriesKey = dataset.seriesKey(s);
    for (var r = 0;r < dataset.rowCount();r++) {
      var rowKey = dataset.rowKey(r);
      var x = dataset.valueByKey(seriesKey, rowKey, xcol);
      var xPropKeys = dataset.propertyKeys(seriesKey, rowKey, xcol);
      var y = dataset.valueByKey(seriesKey, rowKey, ycol);
      var yPropKeys = dataset.propertyKeys(seriesKey, rowKey, ycol);
      result.add(seriesKey, x, y);
      var itemKey = result.getItemKey(s, result.itemCount(s) - 1);
      xPropKeys.forEach(function(key) {
        var p = dataset.getProperty(seriesKey, rowKey, xcol, key);
        result.setProperty(seriesKey, itemKey, key, p);
      });
      yPropKeys.forEach(function(key) {
        var p = dataset.getProperty(seriesKey, rowKey, ycol, key);
        result.setProperty(seriesKey, itemKey, key, p);
      });
    }
  }
  return result;
};
noname.DatasetUtils.extractXYDatasetFromRows = function(dataset, xrow, yrow) {
  var result = new noname.XYDataset;
  for (var s = 0;s < dataset.seriesCount();s++) {
    var seriesKey = dataset.seriesKey(s);
    for (var c = 0;c < dataset.columnCount();c++) {
      var colKey = dataset.columnKey(c);
      var x = dataset.valueByKey(seriesKey, xrow, colKey);
      var xPropKeys = dataset.propertyKeys(seriesKey, xrow, colKey);
      var y = dataset.valueByKey(seriesKey, yrow, colKey);
      var yPropKeys = dataset.propertyKeys(seriesKey, yrow, colKey);
      var itemKey = result.getItemKey(s, result.itemCount(s) - 1);
      xPropKeys.forEach(function(key) {
        var p = dataset.getProperty(seriesKey, xrow, colKey, key);
        result.setProperty(seriesKey, itemKey, key, p);
      });
      yPropKeys.forEach(function(key) {
        var p = dataset.getProperty(seriesKey, yrow, colKey, key);
        result.setProperty(seriesKey, itemKey, key, p);
      });
      result.add(seriesKey, x, y);
    }
  }
  return result;
};
noname.HistogramDataset = function(seriesKey) {
  this._seriesKey = seriesKey;
  this.bins = [];
  this.selections = [];
  this._listeners = [];
};
noname.HistogramDataset.prototype.binCount = function() {
  return this.bins.length;
};
noname.HistogramDataset.prototype.isEmpty = function() {
  var result = true;
  this.bins.forEach(function(bin) {
    if (bin.count > 0) {
      result = false;
    }
  });
  return result;
};
noname.HistogramDataset.prototype.addListener = function(listenerObj) {
  this._listeners.push(listenerObj);
  return this;
};
noname.HistogramDataset.prototype.removeListener = function(listenerObj) {
  var i = this._listeners.indexOf(listenerObj);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
noname.HistogramDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
noname.HistogramDataset.prototype.addBin = function(xmin, xmax, incmin, incmax) {
  var incmin_ = incmin !== false;
  var incmax_ = incmax !== false;
  var bin = new noname.Bin(xmin, xmax, incmin_, incmax_);
  this.bins.push(bin);
  return this;
};
noname.HistogramDataset.prototype.isOverlapping = function(bin) {
  for (var i = 0;i < this.bins.length;i++) {
    if (this.bins[i].overlaps(bin)) {
      return true;
    }
  }
  return false;
};
noname.HistogramDataset.prototype.xmid = function(binIndex) {
  var bin = this.bins[binIndex];
  return(bin.xmin + bin.xmax) / 2;
};
noname.HistogramDataset.prototype.xstart = function(binIndex) {
  return this.bins[binIndex].xmin;
};
noname.HistogramDataset.prototype.xend = function(binIndex) {
  return this.bins[binIndex].xmax;
};
noname.HistogramDataset.prototype.count = function(binIndex) {
  return this.bins[binIndex].count;
};
noname.HistogramDataset.prototype.reset = function() {
  this.bins.forEach(function(bin) {
    bin.count = 0;
  });
  return this;
};
noname.HistogramDataset.prototype._binIndex = function(value) {
  for (var i = 0;i < this.bins.length;i++) {
    if (this.bins[i].includes(value)) {
      return i;
    }
  }
  return-1;
};
noname.HistogramDataset.prototype.bounds = function() {
  var xmin = Number.POSITIVE_INFINITY;
  var xmax = Number.NEGATIVE_INFINITY;
  var ymin = 0;
  var ymax = 0;
  for (var i = 0;i < this.binCount();i++) {
    var bin = this.bins[i];
    xmin = Math.min(xmin, bin.xmin);
    xmax = Math.max(xmax, bin.xmax);
    ymin = Math.min(ymin, bin.y);
    ymax = Math.max(ymax, bin.y);
  }
  return[xmin, xmax, ymin, ymax];
};
noname.HistogramDataset.prototype.add = function(value, notify) {
  var notify = notify !== false;
  var binIndex = this._binIndex(value);
  if (binIndex >= 0) {
    this.bins[binIndex].count++;
  } else {
    throw new Error("No bin for the value " + value);
  }
  return this;
};
noname.HistogramDataset.prototype.addAll = function(values, notify) {
  var notify = notify !== false;
  var me = this;
  values.forEach(function(v) {
    me.add(v, false);
  });
  return this;
};
noname.HistogramDataset.prototype.seriesCount = function() {
  return 1;
};
noname.HistogramDataset.prototype.itemCount = function(seriesIndex) {
  return this.binCount();
};
noname.HistogramDataset.prototype.xbounds = function() {
  var xmin = Number.POSITIVE_INFINITY;
  var xmax = Number.NEGATIVE_INFINITY;
  for (var s = 0;s < this.seriesCount();s++) {
    for (var i = 0;i < this.itemCount(s);i++) {
      var xs = this.xmin(s, i);
      var xe = this.xmax(s, i);
      xmin = Math.min(xmin, xs);
      xmax = Math.max(xmax, xe);
    }
  }
  return[xmin, xmax];
};
noname.HistogramDataset.prototype.ybounds = function() {
  var ymin = Number.POSITIVE_INFINITY;
  var ymax = Number.NEGATIVE_INFINITY;
  for (var s = 0;s < this.seriesCount();s++) {
    for (var i = 0;i < this.itemCount(s);i++) {
      var y = this.y(s, i);
      ymin = Math.min(ymin, y);
      ymax = Math.max(ymax, y);
    }
  }
  return[ymin, ymax];
};
noname.HistogramDataset.prototype.seriesKeys = function() {
  return[this._seriesKey];
};
noname.HistogramDataset.prototype.seriesIndex = function(seriesKey) {
  if (seriesKey === this._seriesKey) {
    return 0;
  }
  return-1;
};
noname.HistogramDataset.prototype.seriesKey = function(seriesIndex) {
  if (seriesIndex === 0) {
    return this._seriesKey;
  }
  throw new Error("Invalid seriesIndex: " + seriesIndex);
};
noname.HistogramDataset.prototype.getItemKey = function(seriesIndex, itemIndex) {
  if (seriesIndex === 0) {
    return itemIndex;
  }
  throw new Error("Invalid seriesIndex: " + seriesIndex);
};
noname.HistogramDataset.prototype.itemIndex = function(seriesKey, itemKey) {
  if (seriesKey === this._seriesKey) {
    return itemKey;
  }
  throw new Error("Invalid seriesIndex: " + seriesKey);
};
noname.HistogramDataset.prototype.x = function(seriesIndex, itemIndex) {
  return this.xmid(itemIndex);
};
noname.HistogramDataset.prototype.xmin = function(seriesIndex, itemIndex) {
  return this.xstart(itemIndex);
};
noname.HistogramDataset.prototype.xmax = function(seriesIndex, itemIndex) {
  return this.xend(itemIndex);
};
noname.HistogramDataset.prototype.y = function(seriesIndex, itemIndex) {
  return this.count(itemIndex);
};
noname.HistogramDataset.prototype.getProperty = function(seriesKey, itemKey, propertyKey) {
  return null;
};
noname.KeyedValuesDataset = function() {
  if (!(this instanceof noname.KeyedValuesDataset)) {
    return new noname.KeyedValuesDataset;
  }
  this.data = {"sections":[]};
  this.properties = [];
  this.selections = [];
  this._listeners = [];
};
noname.KeyedValuesDataset.prototype.itemCount = function() {
  return this.data.sections.length;
};
noname.KeyedValuesDataset.prototype.isEmpty = function() {
  return this.data.sections.length === 0;
};
noname.KeyedValuesDataset.prototype.key = function(index) {
  return this.data.sections[index].key;
};
noname.KeyedValuesDataset.prototype.keys = function() {
  return this.data.sections.map(function(d) {
    return d.key;
  });
};
noname.KeyedValuesDataset.prototype.indexOf = function(sectionKey) {
  var arrayLength = this.data.sections.length;
  for (var i = 0;i < arrayLength;i++) {
    if (this.data.sections[i].key === sectionKey) {
      return i;
    }
  }
  return-1;
};
noname.KeyedValuesDataset.prototype.valueByIndex = function(index) {
  return this.data.sections[index].value;
};
noname.KeyedValuesDataset.prototype.valueByKey = function(sectionKey) {
  var sectionIndex = this.indexOf(sectionKey);
  if (sectionIndex < 0) {
    return null;
  }
  return this.valueByIndex(sectionIndex);
};
noname.KeyedValuesDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
noname.KeyedValuesDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
noname.KeyedValuesDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
noname.KeyedValuesDataset.prototype.add = function(sectionKey, value, notify) {
  var notify = notify !== false;
  var i = this.indexOf(sectionKey);
  if (i < 0) {
    this.data.sections.push({"key":sectionKey, "value":value});
    this.properties.push(new noname.Map);
  } else {
    this.data.sections[i].value = value;
  }
  if (notify) {
    this.notifyListeners();
  }
  return this;
};
noname.KeyedValuesDataset.prototype.remove = function(sectionKey, notify) {
  var notify = notify !== false;
  if (!sectionKey) {
    throw new Error("The 'sectionKey' must be defined.");
  }
  var i = this.indexOf(sectionKey);
  if (i < 0) {
    throw new Error("The sectionKey '" + sectionKey.toString() + "' is not recognised.");
  }
  this.data.sections.splice(i, 1);
  this.properties.splice(i, 1);
  if (notify) {
    this.notifyListeners();
  }
  return this;
};
noname.KeyedValuesDataset.prototype.parse = function(jsonStr, notify) {
  var notify = notify !== false;
  this.data.sections = JSON.parse(jsonStr);
  if (notify) {
    this.notifyListeners();
  }
  return this;
};
noname.KeyedValuesDataset.prototype.load = function(data, notify) {
  var notify = notify !== false;
  this.data.sections = data;
  if (notify) {
    this.notifyListeners();
  }
  return this;
};
noname.KeyedValuesDataset.prototype.removeByIndex = function(itemIndex) {
  this.data.sections.splice(itemIndex, 1);
  this.properties.splice(itemIndex, 1);
  return this;
};
noname.KeyedValuesDataset.prototype.totalForDataset = function(dataset) {
  var total = 0;
  var itemCount = dataset.itemCount();
  for (var i = 0;i < itemCount;i++) {
    var v = dataset.valueByIndex(i);
    if (v) {
      total = total + v;
    }
  }
  return total;
};
noname.KeyedValuesDataset.prototype.minForDataset = function(dataset) {
  var min = Number.NaN;
  var itemCount = dataset.itemCount();
  for (var i = 0;i < itemCount;i++) {
    var v = dataset.valueByIndex(i);
    if (v) {
      if (min) {
        min = Math.min(min, v);
      } else {
        min = v;
      }
    }
  }
  return min;
};
noname.KeyedValuesDataset.prototype.maxForDataset = function(dataset) {
  var max = Number.NaN;
  var itemCount = dataset.itemCount();
  for (var i = 0;i < itemCount;i++) {
    var v = dataset.valueByIndex(i);
    if (v) {
      if (max) {
        max = Math.max(max, v);
      } else {
        max = v;
      }
    }
  }
  return max;
};
noname.KeyedValuesDataset.prototype.total = function() {
  return this.totalForDataset(this);
};
noname.KeyedValuesDataset.prototype.min = function() {
  return this.minForDataset(this);
};
noname.KeyedValuesDataset.prototype.max = function() {
  return this.maxForDataset(this);
};
noname.KeyedValuesDataset.prototype.propertyKeys = function(sectionKey) {
  var i = this.indexOf(sectionKey);
  var map = this.properties[i];
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
noname.KeyedValuesDataset.prototype.getProperty = function(sectionKey, propertyKey) {
  var i = this.indexOf(sectionKey);
  return this.properties[i].get(propertyKey);
};
noname.KeyedValuesDataset.prototype.setProperty = function(sectionKey, propertyKey, value) {
  var i = this.indexOf(sectionKey);
  if (i < 0) {
    throw new Error("Did not recognise 'sectionKey' " + sectionKey);
  }
  var map = this.properties[i];
  map.put(propertyKey, value);
};
noname.KeyedValuesDataset.prototype.clearProperties = function(sectionKey) {
  var i = this.indexOf(sectionKey);
  this.properties[i] = new noname.Map;
};
noname.KeyedValuesDataset.prototype.select = function(selectionId, key) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    selection = {"id":selectionId, "items":[]};
    this.selections.push(selection);
  } else {
    selection = this.selections[selectionIndex];
  }
  var i = selection.items.indexOf(key);
  if (i < 0) {
    selection.items.push(key);
  }
  return this;
};
noname.KeyedValuesDataset.prototype.unselect = function(selectionId, key) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    var selection = this.selections[selectionIndex];
    var i = selection.items.indexOf(key);
    if (i >= 0) {
      selection.items.splice(i, 1);
    }
  }
  return this;
};
noname.KeyedValuesDataset.prototype.isSelected = function(selectionId, key) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
    return selection.items.indexOf(key) >= 0;
  }
};
noname.KeyedValuesDataset.prototype.clearSelection = function(selectionId, notify) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
  return this;
};
noname.KeyedValuesDataset.prototype._indexOfSelection = function(selectionId) {
  return noname.Utils.findInArray(this.selections, function(selection) {
    return selection.id === selectionId;
  });
};
noname.KeyedValues2DDataset = function() {
  if (!(this instanceof noname.KeyedValues2DDataset)) {
    return new noname.KeyedValues2DDataset;
  }
  this.data = {"columnKeys":[], "rows":[]};
  this.properties = [];
  this.selections = [];
  this._listeners = [];
};
noname.KeyedValues2DDataset.prototype.rowCount = function() {
  return this.data.rows.length;
};
noname.KeyedValues2DDataset.prototype.columnCount = function() {
  return this.data.columnKeys.length;
};
noname.KeyedValues2DDataset.prototype.isEmpty = function() {
  if (!this.data.hasOwnProperty("columnKeys")) {
    return true;
  }
  return this.data.columnKeys.length === 0 && this.data.rows.length === 0;
};
noname.KeyedValues2DDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
noname.KeyedValues2DDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
noname.KeyedValues2DDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
noname.KeyedValues2DDataset.prototype.add = function(rowKey, columnKey, value, notify) {
  if (this.isEmpty()) {
    this.data.columnKeys.push(columnKey);
    this.data.rows.push({"key":rowKey, "values":[value]});
    this.properties.push({"key":rowKey, "maps":[null]});
    return this;
  }
  var columnIndex = this.columnIndex(columnKey);
  if (columnIndex < 0) {
    this.data.columnKeys.push(columnKey);
    var rowCount = this.data.rows.length;
    for (var r = 0;r < rowCount;r++) {
      this.data.rows[r].values.push(null);
      this.properties[r].maps.push(null);
    }
    columnIndex = this.columnCount() - 1;
  }
  var rowIndex = this.rowIndex(rowKey);
  if (rowIndex < 0) {
    var rowData = new Array(this.columnCount());
    rowData[columnIndex] = value;
    this.data.rows.push({"key":rowKey, "values":rowData});
    var rowProperties = new Array(this.columnCount());
    this.properties.push({"key":rowKey, "maps":rowProperties});
  } else {
    this.data.rows[rowIndex].values[columnIndex] = value;
  }
  return this;
};
noname.KeyedValues2DDataset.prototype.parse = function(jsonStr, notify) {
  var notify = notify !== false;
  this.load(JSON.parse(jsonStr));
  if (notify) {
    this.notifyListeners();
  }
  return this;
};
noname.KeyedValues2DDataset.prototype.load = function(data, notify) {
  var notify = notify !== false;
  this.data = data;
  if (!this.data.hasOwnProperty("rows")) {
    this.data.rows = [];
  }
  if (!this.data.hasOwnProperty("columnKeys")) {
    this.data.columnKeys = [];
  }
  if (notify) {
    this.notifyListeners();
  }
  return this;
};
noname.KeyedValues2DDataset.prototype.valueByIndex = function(rowIndex, columnIndex) {
  return this.data.rows[rowIndex].values[columnIndex];
};
noname.KeyedValues2DDataset.prototype.rowKey = function(rowIndex) {
  return this.data.rows[rowIndex].key;
};
noname.KeyedValues2DDataset.prototype.rowIndex = function(rowKey) {
  var rowCount = this.data.rows.length;
  for (var r = 0;r < rowCount;r++) {
    if (this.data.rows[r].key === rowKey) {
      return r;
    }
  }
  return-1;
};
noname.KeyedValues2DDataset.prototype.rowKeys = function() {
  return this.data.rows.map(function(d) {
    return d.key;
  });
};
noname.KeyedValues2DDataset.prototype.columnKey = function(columnIndex) {
  return this.data.columnKeys[columnIndex];
};
noname.KeyedValues2DDataset.prototype.columnIndex = function(columnKey) {
  var columnCount = this.data.columnKeys.length;
  for (var c = 0;c < columnCount;c++) {
    if (this.data.columnKeys[c] === columnKey) {
      return c;
    }
  }
  return-1;
};
noname.KeyedValues2DDataset.prototype.columnKeys = function() {
  return this.data.columnKeys.map(function(d) {
    return d;
  });
};
noname.KeyedValues2DDataset.prototype.valueByKey = function(rowKey, columnKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  return this.valueByIndex(rowIndex, columnIndex);
};
noname.KeyedValues2DDataset.prototype.minForDataset = function(dataset) {
  var min = Number.NaN;
  var rowCount = dataset.rowCount();
  var columnCount = dataset.columnCount();
  for (var r = 0;r < rowCount;r++) {
    for (var c = 0;c < columnCount;c++) {
      var v = dataset.valueByIndex(r, c);
      if (v) {
        if (min) {
          min = Math.min(min, v);
        } else {
          min = v;
        }
      }
    }
  }
  return min;
};
noname.KeyedValues2DDataset.prototype.maxForDataset = function(dataset) {
  var max = Number.NaN;
  var rowCount = dataset.rowCount();
  var columnCount = dataset.columnCount();
  for (var r = 0;r < rowCount;r++) {
    for (var c = 0;c < columnCount;c++) {
      var v = dataset.valueByIndex(r, c);
      if (v) {
        if (max) {
          max = Math.max(max, v);
        } else {
          max = v;
        }
      }
    }
  }
  return max;
};
noname.KeyedValues2DDataset.prototype.min = function() {
  return this.minForDataset(this);
};
noname.KeyedValues2DDataset.prototype.max = function() {
  return this.maxForDataset(this);
};
noname.KeyedValues2DDataset.prototype.yValues = function(rowIndex) {
  return this.data.rows[rowIndex].values.map(function(d) {
    return d;
  });
};
noname.KeyedValues2DDataset.prototype.bounds = function() {
  var ymin = Number.POSITIVE_INFINITY;
  var ymax = Number.NEGATIVE_INFINITY;
  var rowCount = this.rowCount();
  var columnCount = this.columnCount();
  for (var r = 0;r < rowCount;r++) {
    for (var c = 0;c < columnCount;c++) {
      var v = this.valueByIndex(r, c);
      if (v) {
        ymin = Math.min(ymin, v);
        ymax = Math.max(ymax, v);
      }
    }
  }
  return[ymin, ymax];
};
noname.KeyedValues2DDataset.prototype.getProperty = function(rowKey, columnKey, propertyKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[rowIndex][columnIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
noname.KeyedValues2DDataset.prototype.setProperty = function(rowKey, columnKey, propertyKey, value) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[rowIndex][columnIndex];
  if (!map) {
    map = new noname.Map;
    this.properties[rowIndex][columnIndex] = map;
  }
  map.put(propertyKey, value);
};
noname.KeyedValues2DDataset.prototype.propertyKeys = function(rowKey, columnKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[rowIndex][columnIndex];
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
noname.KeyedValues2DDataset.prototype.clearProperties = function(rowKey, columnKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  this.properties[rowIndex][columnIndex] = null;
};
noname.KeyedValues2DDataset.prototype.select = function(selectionId, rowKey, columnKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    selection = {"id":selectionId, "items":[]};
    this.selections.push(selection);
  } else {
    selection = this.selections[selectionIndex];
  }
  var i = noname.Utils.findInArray(selection.items, function(item) {
    return item.rowKey === rowKey && item.columnKey === columnKey;
  });
  if (i < 0) {
    selection.items.push({"rowKey":rowKey, "columnKey":columnKey});
  }
  return this;
};
noname.KeyedValues2DDataset.prototype.unselect = function(selectionId, rowKey, columnKey) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    var selection = this.selections[selectionIndex];
    var i = noname.Utils.findInArray(selection.items, function(obj, i) {
      return obj.rowKey === rowKey && obj.columnKey === columnKey;
    });
    if (i >= 0) {
      selection.items.splice(i, 1);
    }
  }
  return this;
};
noname.KeyedValues2DDataset.prototype.isSelected = function(selectionId, rowKey, columnKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
  }
  return noname.Utils.findInArray(selection.items, function(obj) {
    return obj.rowKey === rowKey && obj.columnKey === columnKey;
  }) >= 0;
};
noname.KeyedValues2DDataset.prototype.clearSelection = function(selectionId) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
  return this;
};
noname.KeyedValues2DDataset.prototype._indexOfSelection = function(selectionId) {
  return noname.Utils.findInArray(this.selections, function(item) {
    return item.id === selectionId;
  });
};
noname.KeyedValues3DDataset = function() {
  if (!(this instanceof noname.KeyedValues3DDataset)) {
    return new noname.KeyedValues3DDataset;
  }
  this.data = {"columnKeys":[], "rowKeys":[], "series":[]};
  this.properties = [];
  this._listeners = [];
};
noname.KeyedValues3DDataset.prototype.isEmpty = function() {
  return this.data.columnKeys.length === 0 && this.data.rowKeys.length === 0;
};
noname.KeyedValues3DDataset.prototype.seriesCount = function() {
  return this.data.series.length;
};
noname.KeyedValues3DDataset.prototype.rowCount = function() {
  return this.data.rowKeys.length;
};
noname.KeyedValues3DDataset.prototype.columnCount = function() {
  return this.data.columnKeys.length;
};
noname.KeyedValues3DDataset.prototype._fetchRow = function(seriesIndex, rowKey) {
  var rows = this.data.series[seriesIndex].rows;
  for (var r = 0;r < rows.length;r++) {
    if (rows[r].rowKey === rowKey) {
      return rows[r];
    }
  }
  return null;
};
noname.KeyedValues3DDataset.prototype.valueByIndex = function(seriesIndex, rowIndex, columnIndex) {
  var rowKey = this.rowKey(rowIndex);
  var row = this._fetchRow(seriesIndex, rowKey);
  if (row === null) {
    return null;
  } else {
    return row.values[columnIndex];
  }
};
noname.KeyedValues3DDataset.prototype.seriesIndex = function(seriesKey) {
  var seriesCount = this.seriesCount();
  for (var s = 0;s < seriesCount;s++) {
    if (this.data.series[s].seriesKey === seriesKey) {
      return s;
    }
  }
  return-1;
};
noname.KeyedValues3DDataset.prototype.seriesKey = function(seriesIndex) {
  return this.data.series[seriesIndex].seriesKey;
};
noname.KeyedValues3DDataset.prototype.rowKey = function(rowIndex) {
  return this.data.rowKeys[rowIndex];
};
noname.KeyedValues3DDataset.prototype.rowIndex = function(rowKey) {
  var rowCount = this.data.rowKeys.length;
  for (var r = 0;r < rowCount;r++) {
    if (this.data.rowKeys[r] === rowKey) {
      return r;
    }
  }
  return-1;
};
noname.KeyedValues3DDataset.prototype.rowKeys = function() {
  return this.data.rowKeys.map(function(d) {
    return d;
  });
};
noname.KeyedValues3DDataset.prototype.columnKey = function(columnIndex) {
  return this.data.columnKeys[columnIndex];
};
noname.KeyedValues3DDataset.prototype.columnIndex = function(columnKey) {
  var columnCount = this.data.columnKeys.length;
  for (var c = 0;c < columnCount;c++) {
    if (this.data.columnKeys[c] === columnKey) {
      return c;
    }
  }
  return-1;
};
noname.KeyedValues3DDataset.prototype.columnKeys = function() {
  return this.data.columnKeys.map(function(d) {
    return d;
  });
};
noname.KeyedValues3DDataset.prototype.valueByKey = function(seriesKey, rowKey, columnKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var row = this._fetchRow(seriesIndex, rowKey);
  if (row === null) {
    return null;
  } else {
    var columnIndex = this.columnIndex(columnKey);
    return row.values[columnIndex];
  }
};
noname.KeyedValues3DDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
};
noname.KeyedValues3DDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
};
noname.KeyedValues3DDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
noname.KeyedValues3DDataset.prototype.add = function(seriesKey, rowKey, columnKey, value) {
  if (this.isEmpty()) {
    this.data.rowKeys.push(rowKey);
    this.data.columnKeys.push(columnKey);
    this.data.series.push({"seriesKey":seriesKey, "rows":[{"rowKey":rowKey, "values":[value]}]});
    this.properties.push({"seriesKey":seriesKey, "rows":[{"rowKey":rowKey, "maps":[null]}]});
  } else {
    var seriesIndex = this.seriesIndex(seriesKey);
    if (seriesIndex < 0) {
      this.data.series.push({"seriesKey":seriesKey, "rows":[]});
      this.properties.push({"seriesKey":seriesKey, "rows":[]});
      seriesIndex = this.data.series.length - 1;
    }
    var columnIndex = this.columnIndex(columnKey);
    if (columnIndex < 0) {
      this.data.columnKeys.push(columnKey);
      for (var s = 0;s < this.data.series.length;s++) {
        var rows = this.data.series[s].rows;
        for (var r = 0;r < rows.length;r++) {
          rows[r].values.push(null);
        }
      }
      for (var s = 0;s < this.properties.length;s++) {
        var rows = this.properties[s].rows;
        for (var r = 0;r < rows.length;r++) {
          rows[r].maps.push(null);
        }
      }
      columnIndex = this.columnCount() - 1;
    }
    var rowIndex = this.rowIndex(rowKey);
    if (rowIndex < 0) {
      this.data.rowKeys.push(rowKey);
      var rowData = noname.Utils.makeArrayOf(null, this.columnCount());
      rowData[columnIndex] = value;
      this.data.series[seriesIndex].rows.push({"rowKey":rowKey, "values":rowData});
      var rowMaps = noname.Utils.makeArrayOf(null, this.columnCount());
      this.properties[seriesIndex].rows.push({"rowKey":rowKey, "maps":rowMaps});
    } else {
      var row = this._fetchRow(seriesIndex, rowKey);
      if (row !== null) {
        row.values[columnIndex] = value;
      } else {
        var rowData = noname.Utils.makeArrayOf(null, this.columnCount());
        rowData[columnIndex] = value;
        this.data.series[seriesIndex].rows.push({"rowKey":rowKey, "values":rowData});
      }
      var row = this._fetchPropertyRow(seriesIndex, rowKey);
      if (row === null) {
        var rowMaps = noname.Utils.makeArrayOf(null, this.columnCount());
        this.properties[seriesIndex].rows.push({"rowKey":rowKey, "maps":rowMaps});
      }
    }
  }
  return this;
};
noname.KeyedValues3DDataset.prototype.parse = function(jsonStr) {
  this.load(JSON.parse(jsonStr));
  return this;
};
noname.KeyedValues3DDataset.prototype.load = function(dataObj) {
  this.data = dataObj;
  if (!this.data.hasOwnProperty("rowKeys")) {
    this.data.rowKeys = [];
  }
  if (!this.data.hasOwnProperty("columnKeys")) {
    this.data.columnKeys = [];
  }
  if (!this.data.hasOwnProperty("series")) {
    this.data.series = [];
  }
  this.clearAllProperties();
  this.notifyListeners();
  return this;
};
noname.KeyedValues3DDataset.prototype.getProperty = function(seriesKey, rowKey, columnKey, propertyKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[seriesIndex].rows[rowIndex][columnIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
noname.KeyedValues3DDataset.prototype.setProperty = function(seriesKey, rowKey, columnKey, propertyKey, value) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[seriesIndex].rows[rowIndex][columnIndex];
  if (!map) {
    map = new noname.Map;
    this.properties[seriesIndex].rows[rowIndex][columnIndex] = map;
  }
  map.put(propertyKey, value);
};
noname.KeyedValues3DDataset.prototype.propertyKeys = function(seriesKey, rowKey, columnKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[seriesIndex].rows[rowIndex][columnIndex];
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
noname.KeyedValues3DDataset.prototype.clearProperties = function(seriesKey, rowKey, columnKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var row = this._fetchPropertyRow(seriesIndex, rowKey);
  if (row) {
    var columnIndex = this.columnIndex(columnKey);
    row[columnIndex] = null;
  }
};
noname.KeyedValues3DDataset.prototype.clearAllProperties = function() {
  this.properties = [];
  var me = this;
  this.data.series.forEach(function(series) {
    var s = {"seriesKey":series.seriesKey, "rows":[]};
    me.properties.push(s);
    series.rows.forEach(function(row) {
      var maps = noname.Utils.makeArrayOf(null, me.columnCount());
      var r = {"rowKey":row.rowKey, "maps":maps};
      s.rows.push(r);
    });
  });
};
noname.KeyedValues3DDataset.prototype._fetchPropertyRow = function(seriesIndex, rowKey) {
  var rows = this.properties[seriesIndex].rows;
  for (var r = 0;r < rows.length;r++) {
    if (rows[r].rowKey === rowKey) {
      return rows[r];
    }
  }
  return null;
};
noname.Map = function() {
  this.data = [];
};
noname.Map.prototype.keys = function() {
  return this.data.map(function(d) {
    return d.key;
  });
};
noname.Map.prototype._indexOf = function(key) {
  for (var i = 0;i < this.data.length;i++) {
    if (this.data[i].key === key) {
      return i;
    }
  }
  return-1;
};
noname.Map.prototype.put = function(key, value) {
  var i = this._indexOf(key);
  if (i < 0) {
    this.data.push({"key":key, "value":value});
  } else {
    this.data[i].value = value;
  }
};
noname.Map.prototype.get = function(key) {
  var i = this._indexOf(key);
  if (i >= 0) {
    return this.data[i].value;
  } else {
    return undefined;
  }
};
noname.Map.prototype.remove = function(key) {
  var i = this._indexOf(key);
  if (i >= 0) {
    return this.data.splice(i, 1);
  }
};
noname.Range = function(lowerBound, upperBound) {
  this._lowerBound = lowerBound;
  this._upperBound = upperBound;
};
noname.Range.prototype.length = function() {
  return this._upperBound - this._lowerBound;
};
noname.Range.prototype.percent = function(value) {
  return(value - this._lowerBounds) / this.length();
};
noname.XYDataset = function() {
  this.data = {"series":[]};
  this.properties = [];
  this.selections = [];
  this._listeners = [];
};
noname.XYDataset.prototype.seriesCount = function() {
  return this.data.series.length;
};
noname.XYDataset.prototype.seriesKeys = function() {
  return this.data.series.map(function(d) {
    return d.seriesKey;
  });
};
noname.XYDataset.prototype.seriesKey = function(seriesIndex) {
  return this.data.series[seriesIndex].seriesKey;
};
noname.XYDataset.prototype.seriesIndex = function(seriesKey) {
  noname.Args.requireString(seriesKey, "seriesKey");
  var seriesArray = this.data.series;
  var seriesCount = this.data.series.length;
  for (var s = 0;s < seriesCount;s++) {
    if (seriesArray[s].seriesKey === seriesKey) {
      return s;
    }
  }
  return-1;
};
noname.XYDataset.prototype.itemCount = function(seriesIndex) {
  return this.data.series[seriesIndex].items.length;
};
noname.XYDataset.prototype.itemIndex = function(seriesKey, itemKey) {
  noname.Args.require(itemKey, "itemKey");
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return i;
    }
  }
  return-1;
};
noname.XYDataset.prototype.x = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].x;
};
noname.XYDataset.prototype.y = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].y;
};
noname.XYDataset.prototype.item = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex];
};
noname.XYDataset.prototype.itemByKey = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return items[i];
    }
  }
};
noname.XYDataset.prototype.getItemKey = function(seriesIndex, itemIndex) {
  return this.item(seriesIndex, itemIndex).key;
};
noname.XYDataset.prototype.generateItemKey = function(seriesIndex) {
  if (seriesIndex < 0) {
    return 0;
  }
  var candidate = 0;
  var max = Number.MIN_VALUE;
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (typeof items[i].key === "number") {
      max = Math.max(items[i].key, max);
    }
    if (candidate === items[i].key) {
      candidate = max + 1;
    }
  }
  return candidate;
};
noname.XYDataset.prototype.items = function(seriesIndex) {
  return this.data.series[seriesIndex].items;
};
noname.XYDataset.prototype.allItems = function() {
  var result = [];
  for (var s = 0;s < this.data.series.length;s++) {
    result.push(this.items(s));
  }
  return result;
};
noname.XYDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
noname.XYDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
noname.XYDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i](this);
  }
  return this;
};
noname.XYDataset.prototype.add = function(seriesKey, x, y, notify) {
  noname.Args.requireNumber(x, "x");
  var itemKey = this.generateItemKey(this.seriesIndex(seriesKey));
  return this.addByKey(seriesKey, itemKey, x, y, notify);
};
noname.XYDataset.prototype.addByKey = function(seriesKey, itemKey, x, y, notify) {
  noname.Args.requireString(seriesKey, "seriesKey");
  var notify = notify !== false;
  var s = this.seriesIndex(seriesKey);
  if (s < 0) {
    this.addSeries(seriesKey);
    s = this.data.series.length - 1;
  }
  var item = this.itemByKey(seriesKey, itemKey);
  if (item) {
    item.x = x;
    item.y = y;
  } else {
    this.data.series[s].items.push({"x":x, "y":y, "key":itemKey});
    this.properties[s].maps.push(null);
  }
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
noname.XYDataset.prototype.remove = function(seriesIndex, itemIndex, notify) {
  this.data.series[seriesIndex].items.splice(itemIndex, 1);
  this.properties[seriesIndex].maps.splice(itemIndex, 1);
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
noname.XYDataset.prototype.removeByKey = function(seriesKey, itemKey, notify) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(itemKey);
  this.remove(seriesIndex, itemIndex, notify);
};
noname.XYDataset.prototype.addSeries = function(seriesKey) {
  if (!(typeof seriesKey === "string")) {
    throw new Error("The 'seriesKey' must be a string.");
  }
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    throw new Error("There is already a series with the key '" + seriesKey);
  }
  this.data.series.push({"seriesKey":seriesKey, "items":[]});
  this.properties.push({"seriesKey":seriesKey, "maps":[]});
  return this;
};
noname.XYDataset.prototype.removeSeries = function(seriesKey) {
  if (!(typeof seriesKey === "string")) {
    throw new Error("The 'seriesKey' must be a string.");
  }
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    this.data.series.splice(s, 1);
    this.properties.splice(s, 1);
  }
  return this;
};
noname.XYDataset.prototype.bounds = function() {
  var xmin = Number.POSITIVE_INFINITY;
  var xmax = Number.NEGATIVE_INFINITY;
  var ymin = Number.POSITIVE_INFINITY;
  var ymax = Number.NEGATIVE_INFINITY;
  for (var s = 0;s < this.seriesCount();s++) {
    for (var i = 0;i < this.itemCount(s);i++) {
      var xyitem = this.item(s, i);
      xmin = Math.min(xmin, xyitem.x);
      xmax = Math.max(xmax, xyitem.x);
      ymin = Math.min(ymin, xyitem.y);
      ymax = Math.max(ymax, xyitem.y);
    }
  }
  return[xmin, xmax, ymin, ymax];
};
noname.XYDataset.prototype.xbounds = function() {
  var xmin = Number.POSITIVE_INFINITY;
  var xmax = Number.NEGATIVE_INFINITY;
  for (var s = 0;s < this.seriesCount();s++) {
    for (var i = 0;i < this.itemCount(s);i++) {
      var x = this.x(s, i);
      xmin = Math.min(xmin, x);
      xmax = Math.max(xmax, x);
    }
  }
  return[xmin, xmax];
};
noname.XYDataset.prototype.ybounds = function() {
  var ymin = Number.POSITIVE_INFINITY;
  var ymax = Number.NEGATIVE_INFINITY;
  for (var s = 0;s < this.seriesCount();s++) {
    for (var i = 0;i < this.itemCount(s);i++) {
      var y = this.y(s, i);
      ymin = Math.min(ymin, y);
      ymax = Math.max(ymax, y);
    }
  }
  return[ymin, ymax];
};
noname.XYDataset.prototype.getProperty = function(seriesKey, itemKey, propertyKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties[seriesIndex].maps[itemIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
noname.XYDataset.prototype.setProperty = function(seriesKey, itemKey, propertyKey, value) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties[seriesIndex].maps[itemIndex];
  if (!map) {
    map = new noname.Map;
    this.properties[seriesIndex].maps[itemIndex] = map;
  }
  map.put(propertyKey, value);
};
noname.XYDataset.prototype.clearProperties = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  this.properties[seriesIndex].maps[itemIndex] = null;
};
noname.XYDataset.prototype.select = function(selectionId, seriesKey, itemKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    selection = {"id":selectionId, "items":[]};
    this.selections.push(selection);
  } else {
    selection = this.selections[selectionIndex];
  }
  var i = noname.Utils.findInArray(selection.items, function(item) {
    return item.seriesKey === seriesKey && item.itemKey === itemKey;
  });
  if (i < 0) {
    selection.items.push({"seriesKey":seriesKey, "itemKey":itemKey});
  }
  return this;
};
noname.XYDataset.prototype.unselect = function(selectionId, seriesKey, itemKey) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    var selection = this.selections[selectionIndex];
    var i = noname.Utils.findInArray(selection.items, function(obj, i) {
      return obj.seriesKey === seriesKey && obj.itemKey === itemKey;
    });
    if (i >= 0) {
      selection.items.splice(i, 1);
    }
  }
  return this;
};
noname.XYDataset.prototype.isSelected = function(selectionId, seriesKey, itemKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
  }
  return noname.Utils.findInArray(selection.items, function(obj) {
    return obj.seriesKey === seriesKey && obj.itemKey === itemKey;
  }) >= 0;
};
noname.XYDataset.prototype.clearSelection = function(selectionId) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
  return this;
};
noname.XYDataset.prototype._indexOfSelection = function(selectionId) {
  return noname.Utils.findInArray(this.selections, function(item) {
    return item.id === selectionId;
  });
};
noname.XYZDataset = function() {
  this.data = {"series":[]};
  this.properties = [];
  this.selections = [];
  this._listeners = [];
};
noname.XYZDataset.prototype.seriesCount = function() {
  return this.data.series.length;
};
noname.XYZDataset.prototype.seriesKeys = function() {
  return this.data.series.map(function(d) {
    return d.seriesKey;
  });
};
noname.XYZDataset.prototype.seriesKey = function(seriesIndex) {
  return this.data.series[seriesIndex].seriesKey;
};
noname.XYZDataset.prototype.seriesIndex = function(seriesKey) {
  noname.Args.requireString(seriesKey, "seriesKey");
  var seriesArray = this.data.series;
  var seriesCount = this.data.series.length;
  for (var s = 0;s < seriesCount;s++) {
    if (seriesArray[s].seriesKey === seriesKey) {
      return s;
    }
  }
  return-1;
};
noname.XYZDataset.prototype.itemCount = function(seriesIndex) {
  return this.data.series[seriesIndex].items.length;
};
noname.XYZDataset.prototype.itemIndex = function(seriesKey, itemKey) {
  noname.Args.require(itemKey, "itemKey");
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return i;
    }
  }
  return-1;
};
noname.XYZDataset.prototype.x = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].x;
};
noname.XYZDataset.prototype.y = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].y;
};
noname.XYZDataset.prototype.z = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].z;
};
noname.XYZDataset.prototype.item = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex];
};
noname.XYZDataset.prototype.itemByKey = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return items[i];
    }
  }
};
noname.XYZDataset.prototype.getItemKey = function(seriesIndex, itemIndex) {
  return this.item(seriesIndex, itemIndex).key;
};
noname.XYZDataset.prototype.generateItemKey = function(seriesIndex) {
  if (seriesIndex < 0) {
    return 0;
  }
  var candidate = 0;
  var max = Number.MIN_VALUE;
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (typeof items[i].key === "number") {
      max = Math.max(items[i].key, max);
    }
    if (candidate === items[i].key) {
      candidate = max + 1;
    }
  }
  return candidate;
};
noname.XYZDataset.prototype.add = function(seriesKey, x, y, z, notify) {
  noname.Args.requireNumber(x, "x");
  var itemKey = this.generateItemKey(this.seriesIndex(seriesKey));
  return this.addByKey(seriesKey, itemKey, x, y, z, notify);
};
noname.XYZDataset.prototype.addByKey = function(seriesKey, itemKey, x, y, z, notify) {
  noname.Args.requireString(seriesKey, "seriesKey");
  var s = this.seriesIndex(seriesKey);
  if (s < 0) {
    this.addSeries(seriesKey);
    s = this.data.series.length - 1;
  }
  var item = this.itemByKey(seriesKey, itemKey);
  if (item) {
    item.x = x;
    item.y = y;
  } else {
    this.data.series[s].items.push({"x":x, "y":y, "z":z, "key":itemKey});
    this.properties[s].maps.push(null);
  }
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
noname.XYZDataset.prototype.addSeries = function(seriesKey) {
  noname.Args.requireString(seriesKey, "seriesKey");
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    throw new Error("There is already a series with the key '" + seriesKey);
  }
  this.data.series.push({"seriesKey":seriesKey, "items":[]});
  this.properties.push({"seriesKey":seriesKey, "maps":[]});
  return this;
};
noname.XYZDataset.prototype.removeSeries = function(seriesKey) {
  noname.Args.requireString(seriesKey, "seriesKey");
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    this.data.series.splice(s, 1);
  }
  return this;
};
noname.XYZDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
noname.XYZDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
noname.XYZDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i](this);
  }
  return this;
};
noname.XYZDataset.prototype.getProperty = function(seriesKey, itemKey, propertyKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties[seriesIndex].maps[itemIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
noname.XYZDataset.prototype.setProperty = function(seriesKey, itemKey, propertyKey, value) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties[seriesIndex][itemIndex];
  if (!map) {
    map = new noname.Map;
    this.properties[seriesIndex].maps[itemIndex] = map;
  }
  map.put(propertyKey, value);
};
noname.XYZDataset.prototype.clearProperties = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  this.properties[seriesIndex].maps[itemIndex] = null;
};
noname.XYZDataset.prototype.select = function(selectionId, seriesKey, itemIndex) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    selection = {"id":selectionId, "items":[]};
    this.selections.push(selection);
  } else {
    selection = this.selections[selectionIndex];
  }
  var i = noname.Utils.findInArray(selection.items, function(item) {
    return item.seriesKey === seriesKey && item.item === itemIndex;
  });
  if (i < 0) {
    selection.items.push({"seriesKey":seriesKey, "item":itemIndex});
  }
  return this;
};
noname.XYZDataset.prototype.unselect = function(selectionId, seriesKey, itemIndex) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    var selection = this.selections[selectionIndex];
    var i = noname.Utils.findInArray(selection.items, function(obj, i) {
      return obj.seriesKey === seriesKey && obj.item === itemIndex;
    });
    if (i >= 0) {
      selection.items.splice(i, 1);
    }
  }
  return this;
};
noname.XYZDataset.prototype.isSelected = function(selectionId, seriesKey, itemIndex) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
  }
  return noname.Utils.findInArray(selection.items, function(obj) {
    return obj.seriesKey === seriesKey && obj.item === itemIndex;
  }) >= 0;
};
noname.XYZDataset.prototype.clearSelection = function(selectionId) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
};
noname.XYZDataset.prototype._indexOfSelection = function(selectionId) {
  return noname.Utils.findInArray(this.selections, function(item) {
    return item.id === selectionId;
  });
};
noname.KeyedValueLabels = function() {
  if (!(this instanceof noname.KeyedValueLabels)) {
    return new noname.KeyedValueLabels;
  }
  this.format = "{K} = {V}";
  this.valueDP = 2;
  this.percentDP = 2;
};
noname.KeyedValueLabels.prototype.itemLabel = function(keyedValues, itemIndex) {
  var labelStr = new String(this.format);
  var keyStr = keyedValues.key(itemIndex);
  var value = keyedValues.valueByIndex(itemIndex);
  var valueStr = value.toFixed(this.valueDP);
  var total = keyedValues.total();
  var percentStr = (value / total * 100).toFixed(this.percentDP);
  labelStr = labelStr.replace(/{K}/g, keyStr);
  labelStr = labelStr.replace(/{V}/g, valueStr);
  labelStr = labelStr.replace(/{P}/g, percentStr);
  return labelStr;
};
noname.KeyedValue2DLabels = function() {
  if (!(this instanceof noname.KeyedValue2DLabels)) {
    return new noname.KeyedValue2DLabels;
  }
  this.format = "{R}, {C} = {V}";
  this.valueDP = 2;
};
noname.KeyedValue2DLabels.prototype.itemLabel = function(keyedValues2D, rowIndex, columnIndex) {
  var labelStr = new String(this.format);
  var rowKeyStr = keyedValues2D.rowKey(rowIndex);
  var columnKeyStr = keyedValues2D.columnKey(columnIndex);
  var value = keyedValues2D.valueByIndex(rowIndex, columnIndex);
  var valueStr = value.toFixed(this.valueDP);
  labelStr = labelStr.replace(/{R}/g, rowKeyStr);
  labelStr = labelStr.replace(/{C}/g, columnKeyStr);
  labelStr = labelStr.replace(/{V}/g, valueStr);
  return labelStr;
};
noname.KeyedValue3DLabels = function() {
  if (!(this instanceof noname.KeyedValue3DLabels)) {
    return new noname.KeyedValue3DLabels;
  }
  this.format = "{S}, {R}, {C} = {V}";
  this.valueDP = 2;
};
noname.KeyedValue3DLabels.prototype.itemLabel = function(keyedValues3D, seriesIndex, rowIndex, columnIndex) {
  var labelStr = new String(this.format);
  var seriesKeyStr = keyedValues3D.seriesKey(seriesIndex);
  var rowKeyStr = keyedValues3D.rowKey(rowIndex);
  var columnKeyStr = keyedValues3D.columnKey(columnIndex);
  var value = keyedValues3D.valueByIndex(seriesIndex, rowIndex, columnIndex);
  var valueStr = value.toFixed(this.valueDP);
  labelStr = labelStr.replace(/{S}/g, seriesKeyStr);
  labelStr = labelStr.replace(/{R}/g, rowKeyStr);
  labelStr = labelStr.replace(/{C}/g, columnKeyStr);
  labelStr = labelStr.replace(/{V}/g, valueStr);
  return labelStr;
};
noname.XYLabels = function() {
  if (!(this instanceof noname.XYLabels)) {
    return new noname.XYLabels;
  }
  this.format = "{X}, {Y} / {S}";
  this.xDP = 2;
  this.yDP = 2;
};
noname.XYLabels.prototype.itemLabel = function(dataset, seriesKey, itemIndex) {
  var labelStr = new String(this.format);
  var seriesKeyStr = seriesKey;
  var seriesIndex = dataset.seriesIndex(seriesKey);
  var item = dataset.item(seriesIndex, itemIndex);
  var xStr = item.x.toFixed(this.xDP);
  var yStr = item.y.toFixed(this.yDP);
  labelStr = labelStr.replace(/{X}/g, xStr);
  labelStr = labelStr.replace(/{Y}/g, yStr);
  labelStr = labelStr.replace(/{S}/g, seriesKeyStr);
  return labelStr;
};
noname.XYZLabels = function() {
  if (!(this instanceof noname.XYZLabels)) {
    return new noname.XYZLabels;
  }
  this.format = "{X}, {Y}, {Z} / {S}";
  this.xDP = 2;
  this.yDP = 2;
  this.zDP = 2;
};
noname.XYZLabels.prototype.itemLabel = function(dataset, seriesKey, itemIndex) {
  var labelStr = new String(this.format);
  var seriesKeyStr = seriesKey;
  var seriesIndex = dataset.seriesIndex(seriesKey);
  var item = dataset.item(seriesIndex, itemIndex);
  var xStr = item.x.toFixed(this.xDP);
  var yStr = item.y.toFixed(this.yDP);
  var zStr = item.z.toFixed(this.zDP);
  labelStr = labelStr.replace(/{X}/g, xStr);
  labelStr = labelStr.replace(/{Y}/g, yStr);
  labelStr = labelStr.replace(/{Z}/g, zStr);
  labelStr = labelStr.replace(/{S}/g, seriesKeyStr);
  return labelStr;
};

