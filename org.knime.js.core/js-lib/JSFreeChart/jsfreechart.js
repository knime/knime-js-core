var jsfc = {};
jsfc.JSFreeChart = {version:"0.5"};
jsfc.Charts = {};
jsfc.Charts.createTitleElement = function(title, subtitle, anchor) {
  jsfc.Args.requireString(title, "title");
  var titleFont = new jsfc.Font("Palatino, serif", 16, true, false);
  var halign = jsfc.HAlign.LEFT;
  var refPt = anchor ? anchor.refPt() : jsfc.RefPt2D.TOP_LEFT;
  if (jsfc.RefPt2D.isHorizontalCenter(refPt)) {
    halign = jsfc.HAlign.CENTER;
  } else {
    if (jsfc.RefPt2D.isRight(refPt)) {
      halign = jsfc.HAlign.RIGHT;
    }
  }
  var titleElement = new jsfc.TextElement(title);
  titleElement.setFont(titleFont);
  titleElement.halign(halign);
  titleElement.isTitle = true;
  if (subtitle) {
    var subtitleFont = new jsfc.Font("Palatino, serif", 12, false, true);
    var subtitleElement = new jsfc.TextElement(subtitle);
    subtitleElement.setFont(subtitleFont);
    subtitleElement.halign(halign);
    subtitleElement.isSubtitle = true;
    var composite = new jsfc.GridElement;
    composite.setInsets(new jsfc.Insets(0, 0, 0, 0));
    composite.add(titleElement, "R1", "C1");
    composite.add(subtitleElement, "R2", "C1");
    return composite;
  } else {
    return titleElement;
  }
};
jsfc.Charts.createPieChart = function(title, subtitle, dataset) {
};
jsfc.Charts.createBarChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
};
jsfc.Charts.createStackedBarChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
};
jsfc.Charts.createStackedAreaChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
};
jsfc.Charts.createLineChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
};
jsfc.Charts.createScatterChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  jsfc.Args.requireXYDataset(dataset, "dataset");
  var plot = new jsfc.XYPlot(dataset);
  plot.getXAxis().setLabel(xAxisLabel);
  plot.getYAxis().setLabel(yAxisLabel);
  plot.setRenderer(new jsfc.ScatterRenderer(plot));
  var chart = new jsfc.Chart(plot);
  chart.setPadding(5, 5, 5, 5);
  chart.setTitle(title, subtitle, chart.getTitleAnchor());
  return chart;
};
jsfc.Charts.createXYLineChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  jsfc.Args.requireXYDataset(dataset, "dataset");
  var plot = new jsfc.XYPlot(dataset);
  plot.getXAxis().setLabel(xAxisLabel);
  plot.getYAxis().setLabel(yAxisLabel);
  var renderer = new jsfc.XYLineRenderer;
  var chart = new jsfc.Chart(plot);
  chart.setTitleElement(jsfc.Charts.createTitleElement(title, subtitle, chart.getTitleAnchor()));
  plot.setRenderer(renderer);
  return chart;
};
jsfc.Charts.createXYBarChart = function(title, subtitle, dataset, xAxisLabel, yAxisLabel) {
  var plot = new jsfc.XYPlot(dataset);
  plot.getXAxis().setLabel(xAxisLabel);
  plot.getYAxis().setLabel(yAxisLabel);
  var renderer = new jsfc.XYBarRenderer;
  plot.setRenderer(renderer);
  var chart = new jsfc.Chart(plot);
  var titleAnchor = new jsfc.Anchor2D(jsfc.RefPt2D.TOP_LEFT);
  chart.setTitle(title, subtitle, titleAnchor);
  return chart;
};
jsfc.Chart = function(plot) {
  if (!(this instanceof jsfc.Chart)) {
    throw new Error("Use 'new' for construction.");
  }
  this._size = new jsfc.Dimension(400, 240);
  var white = new jsfc.Color(255, 255, 255);
  this._backgroundPainter = new jsfc.StandardRectanglePainter(white, null);
  this._padding = new jsfc.Insets(4, 4, 4, 4);
  this._titleElement = null;
  this._titleAnchor = new jsfc.Anchor2D(jsfc.RefPt2D.TOP_LEFT);
  this._plot = plot;
  this._legendBuilder = new jsfc.StandardLegendBuilder;
  this._legendAnchor = new jsfc.Anchor2D(jsfc.RefPt2D.BOTTOM_RIGHT);
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
jsfc.Chart.prototype.getElementID = function() {
  return this._elementId;
};
jsfc.Chart.prototype.setElementID = function(id) {
  this._elementId = id;
};
jsfc.Chart.prototype.getSize = function() {
  return this._size;
};
jsfc.Chart.prototype.setSize = function(width, height, notify) {
  this._size = new jsfc.Dimension(width, height);
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.Chart.prototype.getBackground = function() {
  return this._backgroundPainter;
};
jsfc.Chart.prototype.setBackground = function(painter, notify) {
  this._backgroundPainter = painter;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.Chart.prototype.setBackgroundColor = function(color, notify) {
  var painter = new jsfc.StandardRectanglePainter(color);
  this.setBackground(painter, notify);
};
jsfc.Chart.prototype.getPadding = function() {
  return this._padding;
};
jsfc.Chart.prototype.setPadding = function(top, left, bottom, right, notify) {
  this._padding = new jsfc.Insets(top, left, bottom, right);
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.Chart.prototype.getTitleElement = function() {
  return this._titleElement;
};
jsfc.Chart.prototype.setTitleElement = function(title, notify) {
  this._titleElement = title;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.Chart.prototype.setTitle = function(title, subtitle, anchor, notify) {
  var element = jsfc.Charts.createTitleElement(title, subtitle, anchor);
  this.setTitleElement(element, notify);
};
jsfc.Chart.prototype.updateTitle = function(title, font, color) {
  if (!this._titleElement) {
    return;
  }
  this._titleElement.receive(function(e) {
    if (e instanceof jsfc.TextElement && e.isTitle) {
      if (title) {
        e.setText(title);
      }
      if (font) {
        e.setFont(font);
      }
      if (color) {
        e.setColor(color);
      }
    }
  });
};
jsfc.Chart.prototype.updateSubtitle = function(subtitle, font, color) {
  if (!this._titleElement) {
    return;
  }
  this._titleElement.receive(function(e) {
    if (e instanceof jsfc.TextElement && e.isSubtitle) {
      if (subtitle) {
        e.setText(subtitle);
      }
      if (font) {
        e.setFont(font);
      }
      if (color) {
        e.setColor(color);
      }
    }
  });
};
jsfc.Chart.prototype.getTitleAnchor = function() {
  return this._titleAnchor;
};
jsfc.Chart.prototype.setTitleAnchor = function(anchor, notify) {
  this._titleAnchor = anchor;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.Chart.prototype.getPlot = function() {
  return this._plot;
};
jsfc.Chart.prototype.getLegendBuilder = function() {
  return this._legendBuilder;
};
jsfc.Chart.prototype.setLegendBuilder = function(builder, notify) {
  this._legendBuilder = builder;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.Chart.prototype.getLegendAnchor = function() {
  return this._legendAnchor;
};
jsfc.Chart.prototype.setLegendAnchor = function(anchor, notify) {
  this._legendAnchor = anchor;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.Chart.prototype._adjustMargin = function(margin, dim, anchor) {
  if (jsfc.RefPt2D.isTop(anchor.refPt())) {
    margin.top += dim.height();
  } else {
    if (jsfc.RefPt2D.isBottom(anchor.refPt())) {
      margin.bottom += dim.height();
    }
  }
};
jsfc.Chart.prototype.draw = function(ctx, bounds) {
  if (this._backgroundPainter) {
    this._backgroundPainter.paint(ctx, bounds);
  }
  var titleDim = new jsfc.Dimension(0, 0);
  var legendDim = new jsfc.Dimension(0, 0);
  if (this._titleElement) {
    titleDim = this._titleElement.preferredSize(ctx, bounds);
  }
  var legend;
  if (this._legendBuilder) {
    legend = this._legendBuilder.createLegend(this._plot, this._legendAnchor, "orientation", {});
    legendDim = legend.preferredSize(ctx, bounds);
  }
  var padding = this.getPadding();
  var px = padding.left();
  var py = padding.top() + titleDim.height();
  var pw = this._size.width() - padding.left() - padding.right();
  var ph = this._size.height() - padding.top() - padding.bottom() - titleDim.height() - legendDim.height();
  this._plotArea = new jsfc.Rectangle(px, py, pw, ph);
  this._plot.draw(ctx, bounds, this._plotArea);
  if (legend) {
    var fitter = new jsfc.Fit2D(this._legendAnchor);
    var dest = fitter.fit(legendDim, bounds);
    legend.draw(ctx, dest);
  }
  if (this._titleElement) {
    var fitter = new jsfc.Fit2D(this._titleAnchor);
    var dest = fitter.fit(titleDim, bounds);
    this._titleElement.draw(ctx, dest);
  }
};
jsfc.Chart.prototype.plotArea = function() {
  return this._plotArea;
};
jsfc.Chart.prototype.addListener = function(f) {
  this._listeners.push(f);
};
jsfc.Chart.prototype.notifyListeners = function() {
  var chart = this;
  this._listeners.forEach(function(f) {
    f(chart);
  });
};
jsfc.ChartManager = function(element, chart, dragZoomEnabled, wheelZoomEnabled, panEnabled) {
  if (!(this instanceof jsfc.ChartManager)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._element = element;
  this._chart = chart;
  this._ctx = new jsfc.SVGContext2D(element);
  var chartListener = function(c) {
    var manager = c;
    return function(chart) {
      manager.refreshDisplay();
    };
  }(this);
  chart.addListener(chartListener);
  this._liveMouseHandler = null;
  this._availableLiveMouseHandlers = [];
  if (dragZoomEnabled !== false) {
    var zoomModifier = new jsfc.Modifier(false, false, false, true);
    var zoomHandler = new jsfc.ZoomHandler(this, zoomModifier);
    this._availableLiveMouseHandlers.push(zoomHandler);
  }
  if (panEnabled !== false) {
    var panHandler = new jsfc.PanHandler(this);
    this._availableLiveMouseHandlers.push(panHandler);
  }
  this._auxiliaryMouseHandlers = [];
  if (wheelZoomEnabled !== false) {
    this._auxiliaryMouseHandlers.push(new jsfc.WheelHandler(this));
  }
  this.installMouseDownHandler(this._element);
  this.installMouseMoveHandler(this._element);
  this.installMouseUpHandler(this._element);
  this.installMouseOverHandler(this._element);
  this.installMouseOutHandler(this._element);
  this.installMouseWheelHandler(this._element);
};
jsfc.ChartManager.prototype.getChart = function() {
  return this._chart;
};
jsfc.ChartManager.prototype.getElement = function() {
  return this._element;
};
jsfc.ChartManager.prototype.getContext = function() {
  return this._ctx;
};
jsfc.ChartManager.prototype.refreshDisplay = function() {
  var size = this._chart.getSize();
  var bounds = new jsfc.Rectangle(0, 0, size.width(), size.height());
  this._ctx.clear();
  this._chart.draw(this._ctx, bounds);
};
jsfc.ChartManager.prototype._matchLiveHandler = function(alt, ctrl, meta, shift) {
  var handlers = this._availableLiveMouseHandlers;
  for (var i = 0;i < handlers.length;i++) {
    var h = handlers[i];
    if (h.modifier.match(alt, ctrl, meta, shift)) {
      return h;
    }
  }
  return null;
};
jsfc.ChartManager.prototype.installMouseDownHandler = function(element) {
  var my = this;
  element.onmousedown = function(event) {
    if (my._liveMouseHandler !== null) {
      my._liveMouseHandler.mouseDown(event);
    } else {
      var h = my._matchLiveHandler(event.altKey, event.ctrlKey, event.metaKey, event.shiftKey);
      if (h) {
        my._liveMouseHandler = h;
        my._liveMouseHandler.mouseDown(event);
      }
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      h.mouseDown(event);
    });
  };
};
jsfc.ChartManager.prototype.installMouseMoveHandler = function(element) {
  var my = this;
  element.onmousemove = function(event) {
    if (my._liveMouseHandler !== null) {
      my._liveMouseHandler.mouseMove(event);
    } else {
    }
    my._auxiliaryMouseHandlers.forEach(function(h) {
      h.mouseMove(event);
    });
    event.stopPropagation();
    return false;
  };
};
jsfc.ChartManager.prototype.installMouseUpHandler = function(element) {
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
jsfc.ChartManager.prototype.installMouseOverHandler = function(element) {
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
jsfc.ChartManager.prototype.installMouseOutHandler = function(element) {
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
jsfc.ChartManager.prototype.installMouseWheelHandler = function(element) {
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
jsfc.Utils = {};
jsfc.Utils.makeArrayOf = function(value, length) {
  var arr = [], i = length;
  while (i--) {
    arr[i] = value;
  }
  return arr;
};
jsfc.Utils.findInArray = function(items, matcher) {
  var length = items.length;
  for (var i = 0;i < length;i++) {
    if (matcher(items[i], i)) {
      return i;
    }
  }
  return-1;
};
jsfc.Args = {};
jsfc.Args.require = function(arg, label) {
  if (arg === null) {
    throw new Error("Require argument '" + label + "' to be specified.");
  }
  return jsfc.Args;
};
jsfc.Args.requireNumber = function(arg, label) {
  if (typeof arg !== "number") {
    throw new Error("Require '" + label + "' to be a number.");
  }
  return jsfc.Args;
};
jsfc.Args.requireFinitePositiveNumber = function(arg, label) {
  if (typeof arg !== "number" || arg <= 0) {
    throw new Error("Require '" + label + "' to be a positive number.");
  }
  return jsfc.Args;
};
jsfc.Args.requireString = function(arg, label) {
  if (typeof arg !== "string") {
    throw new Error("Require '" + label + "' to be a string.");
  }
  return jsfc.Args;
};
jsfc.Args.requireKeyedValuesDataset = function(arg, label) {
  if (!(arg instanceof jsfc.KeyedValuesDataset)) {
    throw new Error("Require '" + label + "' to be an requireKeyedValuesDataset.");
  }
  return jsfc.Args;
};
jsfc.Args.requireKeyedValues2DDataset = function(arg, label) {
  if (!(arg instanceof jsfc.KeyedValues2DDataset)) {
    throw new Error("Require '" + label + "' to be a KeyedValues2DDataset.");
  }
  return jsfc.Args;
};
jsfc.Args.requireXYDataset = function(arg, label) {
  if (!(arg instanceof jsfc.XYDataset)) {
    throw new Error("Require '" + label + "' to be an XYDataset.");
  }
  return jsfc.Args;
};
jsfc.Colors = {};
jsfc.Colors.fancyLight = function() {
  return["#64E1D5", "#E2D75E", "#F0A4B5", "#E7B16D", "#C2D58D", "#CCBDE4", "#6DE4A8", "#93D2E2", "#AEE377", "#A0D6B5"];
};
jsfc.Colors.fancyDark = function() {
  return["#3A6163", "#8A553A", "#4A6636", "#814C57", "#675A6F", "#384027", "#373B43", "#59372C", "#306950", "#665D31"];
};
jsfc.Colors.iceCube = function() {
  return["#4CE4B7", "#45756F", "#C2D9BF", "#58ADAF", "#4EE9E1", "#839C89", "#3E8F74", "#92E5C1", "#99E5E0", "#57BDAB"];
};
jsfc.Colors.blueOcean = function() {
  return["#6E7094", "#4F76DF", "#292E39", "#2E4476", "#696A72", "#4367A6", "#5E62B7", "#42759A", "#2E3A59", "#4278CA"];
};
jsfc.Colors.colorsAsObjects = function(colors) {
  return colors.map(function(s) {
    return jsfc.Color.fromStr(s);
  });
};
jsfc.Format = function() {
  throw new Error("Documents an interface only.");
};
jsfc.Format.prototype.format = function(n) {
};
jsfc.NumberFormat = function(dp, exponential) {
  if (!(this instanceof jsfc.NumberFormat)) {
    throw new Error("Use 'new' for construction.");
  }
  this._dp = dp;
  this._exponential = exponential || false;
};
jsfc.NumberFormat.prototype.format = function(n) {
  jsfc.Args.requireNumber(n, "n");
  if (this._exponential) {
    return n.toExponential(this._dp);
  }
  if (this._dp === Number.POSITIVE_INFINITY) {
    return n.toString();
  }
  return n.toFixed(this._dp);
};
jsfc.DateFormat = function(style) {
  this._date = new Date;
  this._style = style || "d-mmm-yyyy";
  this._months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
};
jsfc.DateFormat.prototype.format = function(n) {
  jsfc.Args.requireNumber(n, "n");
  this._date.setTime(n);
  if (this._style === "yyyy") {
    return this._dateToYYYY(this._date);
  }
  if (this._style === "mmm-yyyy") {
    return this._dateToMMMYYYY(this._date);
  }
  return this._date.toDateString();
};
jsfc.DateFormat.prototype._dateToYYYY = function(date) {
  var y = date.getFullYear();
  return y + "";
};
jsfc.DateFormat.prototype._dateToMMMYYYY = function(date) {
  var m = date.getMonth();
  var y = date.getFullYear();
  return this._months[m] + "-" + y;
};
jsfc.Anchor2D = function(refpt, offset) {
  if (!(this instanceof jsfc.Anchor2D)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.Args.requireNumber(refpt, "refpt");
  this._refpt = refpt;
  this._offset = offset || new jsfc.Offset2D(0, 0);
};
jsfc.Anchor2D.prototype.refPt = function() {
  return this._refpt;
};
jsfc.Anchor2D.prototype.offset = function() {
  return this._offset;
};
jsfc.Anchor2D.prototype.anchorPoint = function(rect) {
  var x = 0;
  var y = 0;
  if (jsfc.RefPt2D.isLeft(this._refpt)) {
    x = rect.x() + this._offset.dx();
  } else {
    if (jsfc.RefPt2D.isHorizontalCenter(this._refpt)) {
      x = rect.centerX();
    } else {
      if (jsfc.RefPt2D.isRight(this._refpt)) {
        x = rect.maxX() - this._offset.dx();
      }
    }
  }
  if (jsfc.RefPt2D.isTop(this._refpt)) {
    y = rect.minY() + this._offset.dy();
  } else {
    if (jsfc.RefPt2D.isVerticalCenter(this._refpt)) {
      y = rect.centerY();
    } else {
      if (jsfc.RefPt2D.isBottom(this._refpt)) {
        y = rect.maxY() - this._offset.dy();
      }
    }
  }
  return new jsfc.Point2D(x, y);
};
jsfc.Anchor2D.prototype.zeroOffsetAnchor = function(refpt) {
  return new jsfc.Anchor2D(refpt, jsfc.Offset2D.ZERO_OFFSETS);
};
jsfc.Shape = function() {
};
jsfc.Shape.prototype.bounds = function() {
};
jsfc.Circle = function(x, y, radius) {
  this.x = x;
  this.y = y;
  this.radius = radius;
};
jsfc.Circle.prototype.bounds = function() {
  return new jsfc.Rectangle(this.x - this.radius, this.y - this.radius, this.radius * 2, this.radius * 2);
};
jsfc.Color = function(red, green, blue, alpha) {
  if (!(this instanceof jsfc.Color)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._red = red;
  this._green = green;
  this._blue = blue;
  this._alpha = alpha === 0 ? 0 : alpha || 255;
};
jsfc.Color.fromStr = function(s) {
  if (s.length === 4) {
    var rr = s[1] + s[1];
    var gg = s[2] + s[2];
    var bb = s[3] + s[3];
    var r = parseInt(rr, 16);
    var g = parseInt(gg, 16);
    var b = parseInt(bb, 16);
    return new jsfc.Color(r, g, b);
  }
  if (s.length === 7) {
    var rr = s[1] + s[2];
    var gg = s[3] + s[4];
    var bb = s[5] + s[6];
    var r = parseInt(rr, 16);
    var g = parseInt(gg, 16);
    var b = parseInt(bb, 16);
    return new jsfc.Color(r, g, b);
  }
  return undefined;
};
jsfc.Color.prototype.rgbaStr = function() {
  var alphaPercent = this._alpha / 255;
  return "rgba(" + this._red + "," + this._green + "," + this._blue + "," + alphaPercent.toFixed(2) + ")";
};
jsfc.Dimension = function(w, h) {
  if (!(this instanceof jsfc.Dimension)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.Args.requireNumber(w, "w");
  jsfc.Args.requireNumber(h, "h");
  this._width = w;
  this._height = h;
  Object.freeze(this);
};
jsfc.Dimension.prototype.width = function() {
  return this._width;
};
jsfc.Dimension.prototype.height = function() {
  return this._height;
};
jsfc.Fit2D = function(anchor, scale) {
  if (!(this instanceof jsfc.Fit2D)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._anchor = anchor;
  this._scale = scale || jsfc.Scale2D.NONE;
};
jsfc.Fit2D.prototype.anchor = function() {
  return this._anchor;
};
jsfc.Fit2D.prototype.scale = function() {
  return this._scale;
};
jsfc.Fit2D.prototype.fit = function(srcDim, target) {
  if (this._scale === jsfc.Scale2D.SCALE_BOTH) {
    return jsfc.Rectangle.copy(target);
  }
  var w = srcDim.width();
  if (this._scale === jsfc.Scale2D.SCALE_HORIZONTAL) {
    w = target.width();
    if (!jsfc.RefPt2D.isHorizontalCenter(this._anchor.refPt())) {
      w -= 2 * this._anchor.offset().dx();
    }
  }
  var h = srcDim.height();
  if (this._scale === jsfc.Scale2D.SCALE_VERTICAL) {
    h = target.height();
    if (!jsfc.RefPt2D.isVerticalCenter(this._anchor.refPt())) {
      h -= 2 * this._anchor.offset().dy();
    }
  }
  var pt = this._anchor.anchorPoint(target);
  var x = Number.NaN;
  if (jsfc.RefPt2D.isLeft(this._anchor.refPt())) {
    x = pt.x();
  } else {
    if (jsfc.RefPt2D.isHorizontalCenter(this._anchor.refPt())) {
      x = target.centerX() - w / 2;
    } else {
      if (jsfc.RefPt2D.isRight(this._anchor.refPt())) {
        x = pt.x() - w;
      }
    }
  }
  var y = Number.NaN;
  if (jsfc.RefPt2D.isTop(this._anchor.refPt())) {
    y = pt.y();
  } else {
    if (jsfc.RefPt2D.isVerticalCenter(this._anchor.refPt())) {
      y = target.centerY() - h / 2;
    } else {
      if (jsfc.RefPt2D.isBottom(this._anchor.refPt())) {
        y = pt.y() - h;
      }
    }
  }
  return new jsfc.Rectangle(x, y, w, h);
};
jsfc.Fit2D.prototype.noScalingFitter = function(refPt) {
  var anchor = new jsfc.Anchor2D(refPt, new jsfc.Offset2D(0, 0));
  return new jsfc.Fit2D(anchor, jsfc.Scale2D.NONE);
};
jsfc.Font = function(family, size, bold, italic) {
  if (!(this instanceof jsfc.Font)) {
    throw new Error("Use 'new' for constructors.");
  }
  this.family = family;
  this.size = size;
  this.bold = bold || false;
  this.italic = italic || false;
};
jsfc.Font.prototype.styleStr = function() {
  var s = "font-family: " + this.family + "; ";
  s += "font-weight: " + (this.bold ? "bold" : "normal") + "; ";
  s += "font-style: " + (this.italic ? "italic" : "normal") + "; ";
  s += "font-size: " + this.size + "px";
  return s;
};
jsfc.HAlign = {LEFT:1, CENTER:2, RIGHT:3};
if (Object.freeze) {
  Object.freeze(jsfc.HAlign);
}
;jsfc.Insets = function(top, left, bottom, right) {
  if (!(this instanceof jsfc.Insets)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.Args.requireNumber(top, "top");
  jsfc.Args.requireNumber(left, "left");
  jsfc.Args.requireNumber(bottom, "bottom");
  jsfc.Args.requireNumber(right, "right");
  this._top = top;
  this._left = left;
  this._bottom = bottom;
  this._right = right;
  Object.freeze(this);
};
jsfc.Insets.prototype.top = function() {
  return this._top;
};
jsfc.Insets.prototype.left = function() {
  return this._left;
};
jsfc.Insets.prototype.bottom = function() {
  return this._bottom;
};
jsfc.Insets.prototype.right = function() {
  return this._right;
};
jsfc.Insets.prototype.value = function(edge) {
  if (edge === jsfc.RectangleEdge.TOP) {
    return this._top;
  }
  if (edge === jsfc.RectangleEdge.BOTTOM) {
    return this._bottom;
  }
  if (edge === jsfc.RectangleEdge.LEFT) {
    return this._left;
  }
  if (edge === jsfc.RectangleEdge.RIGHT) {
    return this._right;
  }
  throw new Error("Unrecognised edge code: " + edge);
};
jsfc.LineCap = {BUTT:"butt", ROUND:"round", SQUARE:"square"};
if (Object.freeze) {
  Object.freeze(jsfc.LineCap);
}
;jsfc.LineJoin = {ROUND:"round", BEVEL:"bevel", MITER:"miter"};
if (Object.freeze) {
  Object.freeze(jsfc.LineJoin);
}
;jsfc.Offset2D = function(dx, dy) {
  if (!(this instanceof jsfc.Offset2D)) {
    throw new Error("Use 'new' for constructors.");
  }
  jsfc.Args.requireNumber(dx, "dx");
  jsfc.Args.requireNumber(dy, "dy");
  this._dx = dx;
  this._dy = dy;
  Object.freeze(this);
};
jsfc.Offset2D.prototype.dx = function() {
  return this._dx;
};
jsfc.Offset2D.prototype.dy = function() {
  return this._dy;
};
jsfc.Point2D = function(x, y) {
  if (!(this instanceof jsfc.Point2D)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.Args.requireNumber(x, "x");
  jsfc.Args.requireNumber(y, "y");
  this._x = x;
  this._y = y;
  Object.freeze(this);
};
jsfc.Point2D.prototype.x = function() {
  return this._x;
};
jsfc.Point2D.prototype.y = function() {
  return this._y;
};
jsfc.Rectangle = function(x, y, width, height) {
  if (!(this instanceof jsfc.Rectangle)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.Args.requireNumber(x, "x");
  jsfc.Args.requireNumber(y, "y");
  jsfc.Args.requireNumber(width, "width");
  jsfc.Args.requireNumber(height, "height");
  this._x = x;
  this._y = y;
  this._width = width;
  this._height = height;
};
jsfc.Rectangle.copy = function(rect) {
  return new jsfc.Rectangle(rect.x(), rect.y(), rect.width(), rect.height());
};
jsfc.Rectangle.prototype.x = function() {
  return this._x;
};
jsfc.Rectangle.prototype.y = function() {
  return this._y;
};
jsfc.Rectangle.prototype.width = function() {
  return this._width;
};
jsfc.Rectangle.prototype.height = function() {
  return this._height;
};
jsfc.Rectangle.prototype.length = function(edge) {
  if (edge === jsfc.RectangleEdge.TOP || edge === jsfc.RectangleEdge.BOTTOM) {
    return this._width;
  } else {
    if (edge === jsfc.RectangleEdge.LEFT || edge === jsfc.RectangleEdge.RIGHT) {
      return this._height;
    }
  }
  throw new Error("Unrecognised 'edge' value: " + edge);
};
jsfc.Rectangle.prototype.centerX = function() {
  return this._x + this._width / 2;
};
jsfc.Rectangle.prototype.minX = function() {
  return Math.min(this._x, this._x + this._width);
};
jsfc.Rectangle.prototype.maxX = function() {
  return Math.max(this._x, this._x + this._width);
};
jsfc.Rectangle.prototype.centerY = function() {
  return this._y + this._height / 2;
};
jsfc.Rectangle.prototype.minY = function() {
  return Math.min(this._y, this._y + this._height);
};
jsfc.Rectangle.prototype.maxY = function() {
  return Math.max(this._y, this._y + this._height);
};
jsfc.Rectangle.prototype.bounds = function() {
  return new jsfc.Rectangle(this._x, this._y, this._width, this._height);
};
jsfc.Rectangle.prototype.set = function(x, y, w, h) {
  this._x = x;
  this._y = y;
  this._width = w;
  this._height = h;
  return this;
};
jsfc.Rectangle.prototype.constrainedPoint = function(x, y) {
  jsfc.Args.requireNumber(x, "x");
  jsfc.Args.requireNumber(y, "y");
  var xx = Math.max(this.minX(), Math.min(x, this.maxX()));
  var yy = Math.max(this.minY(), Math.min(y, this.maxY()));
  return new jsfc.Point2D(xx, yy);
};
jsfc.RectangleEdge = {TOP:"TOP", BOTTOM:"BOTTOM", LEFT:"LEFT", RIGHT:"RIGHT"};
jsfc.RectangleEdge.isTopOrBottom = function(edge) {
  jsfc.Args.requireString(edge, "edge");
  if (edge === jsfc.RectangleEdge.TOP || edge === jsfc.RectangleEdge.BOTTOM) {
    return true;
  }
  return false;
};
jsfc.RectangleEdge.isLeftOrRight = function(edge) {
  jsfc.Args.requireString(edge, "edge");
  if (edge === jsfc.RectangleEdge.LEFT || edge === jsfc.RectangleEdge.RIGHT) {
    return true;
  }
  return false;
};
if (Object.freeze) {
  Object.freeze(jsfc.RectangleEdge);
}
;jsfc.RefPt2D = {TOP_LEFT:1, TOP_CENTER:2, TOP_RIGHT:3, CENTER_LEFT:4, CENTER:5, CENTER_RIGHT:6, BOTTOM_LEFT:7, BOTTOM_CENTER:8, BOTTOM_RIGHT:9, isLeft:function(refpt) {
  return refpt === jsfc.RefPt2D.TOP_LEFT || (refpt === jsfc.RefPt2D.CENTER_LEFT || refpt === jsfc.RefPt2D.BOTTOM_LEFT);
}, isRight:function(refpt) {
  return refpt === jsfc.RefPt2D.TOP_RIGHT || (refpt === jsfc.RefPt2D.CENTER_RIGHT || refpt === jsfc.RefPt2D.BOTTOM_RIGHT);
}, isTop:function(refpt) {
  return refpt === jsfc.RefPt2D.TOP_LEFT || (refpt === jsfc.RefPt2D.TOP_CENTER || refpt === jsfc.RefPt2D.TOP_RIGHT);
}, isBottom:function(refpt) {
  return refpt === jsfc.RefPt2D.BOTTOM_LEFT || (refpt === jsfc.RefPt2D.BOTTOM_CENTER || refpt === jsfc.RefPt2D.BOTTOM_RIGHT);
}, isHorizontalCenter:function(refpt) {
  return refpt === jsfc.RefPt2D.TOP_CENTER || (refpt === jsfc.RefPt2D.CENTER || refpt === jsfc.RefPt2D.BOTTOM_CENTER);
}, isVerticalCenter:function(refpt) {
  return refpt === jsfc.RefPt2D.CENTER_LEFT || (refpt === jsfc.RefPt2D.CENTER || refpt === jsfc.RefPt2D.CENTER_RIGHT);
}};
if (Object.freeze) {
  Object.freeze(jsfc.RefPt2D);
}
;jsfc.Scale2D = {NONE:1, SCALE_HORIZONTAL:2, SCALE_VERTICAL:3, SCALE_BOTH:4};
if (Object.freeze) {
  Object.freeze(jsfc.Scale2D);
}
;jsfc.Stroke = function(lineWidth) {
  if (!(this instanceof jsfc.Stroke)) {
    throw new Error("Use 'new' for constructors.");
  }
  this.lineWidth = lineWidth || 1;
  this.lineCap = jsfc.LineCap.ROUND;
  this.lineJoin = jsfc.LineJoin.ROUND;
  this.miterLimit = 1;
  this.lineDash = [1];
  this.lineDashOffset = 0;
};
jsfc.Stroke.prototype.getStyleStr = function() {
  var s = "stroke-width: " + this.lineWidth + "; ";
  if (this.lineCap !== "butt") {
    s = s + "stroke-linecap: " + this.lineCap + "; ";
  }
  s = s + "stroke-linejoin: " + this.lineJoin + "; ";
  return s;
};
jsfc.SVGContext2D = function(svg) {
  this.svg = svg;
  this._defs = this.element("defs");
  this.svg.appendChild(this._defs);
  this._defaultLayer = new jsfc.SVGLayer("default");
  this.svg.appendChild(this._defaultLayer.getContainer());
  this._defs.appendChild(this._defaultLayer.getDefsContainer());
  this._layers = [this._defaultLayer];
  this._currentLayer = this._defaultLayer;
  this._hints = {};
  this.strokeStyle = "black";
  this.fillStyle = "black";
  this.pathStr = "";
  this.font = new jsfc.Font("monospace", 15);
  this.textAlign = "start";
  this.textBaseline = "alphabetic";
  this._stroke = new jsfc.Stroke;
  this._lineColor = new jsfc.Color(255, 255, 255);
  this._fillColor = new jsfc.Color(255, 0, 0);
  this._transform = new jsfc.Transform;
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
jsfc.SVGContext2D.prototype.addLayer = function(layer) {
  this._defs.appendChild(layer.getDefsContainer());
  this.svg.appendChild(layer.getContainer());
  this._layers.push(layer);
};
jsfc.SVGContext2D.prototype.removeLayer = function(layer) {
  var index = this._indexOfLayer(layer);
  if (index < 0) {
    throw new Error("The layer is not present in this SVGContext2D.");
  }
  this._layers.splice(index, 1);
  this._defs.removeChild(layer.getDefsContainer());
  this.svg.removeChild(layer.getContainer());
};
jsfc.SVGContext2D.prototype._indexOfLayer = function(layer) {
  for (var i = 0;i < this._layers.length;i++) {
    if (this._layers[i] === layer) {
      return i;
    }
  }
  return-1;
};
jsfc.SVGContext2D.prototype._findLayer = function(id) {
  for (var i = 0;i < this._layers.length;i++) {
    if (this._layers[i].getID() === id) {
      return this._layers[i];
    }
  }
  return undefined;
};
jsfc.SVGContext2D.prototype.element = function(elementType) {
  return document.createElementNS("http://www.w3.org/2000/svg", elementType);
};
jsfc.SVGContext2D.prototype.append = function(element) {
  var stack = this._currentLayer.getStack();
  stack[stack.length - 1].appendChild(element);
};
jsfc.SVGContext2D.prototype.beginGroup = function(classStr) {
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
  this._currentLayer.getStack().push(g);
};
jsfc.SVGContext2D.prototype.endGroup = function() {
  var stack = this._currentLayer.getStack();
  if (stack.length === 1) {
    throw new Error("endGroup() does not have a matching beginGroup().");
  }
  stack.pop();
};
jsfc.SVGContext2D.prototype.clear = function() {
  this._currentLayer.clear();
};
jsfc.SVGContext2D.prototype.getHint = function(key) {
  return this._hints[key];
};
jsfc.SVGContext2D.prototype.setHint = function(key, value) {
  if (key === "layer") {
    var layer = this._findLayer(value);
    if (!layer) {
      layer = new jsfc.SVGLayer(value, this.svg);
      this.addLayer(layer);
    }
    this._currentLayer = layer;
    return;
  }
  this._hints[key] = value;
};
jsfc.SVGContext2D.prototype.clearHints = function() {
  this._hints = {};
};
jsfc.SVGContext2D.prototype.setLineStroke = function(stroke) {
  jsfc.Args.require(stroke, "stroke");
  this._stroke = stroke;
};
jsfc.SVGContext2D.prototype.setLineColor = function(color) {
  jsfc.Args.require(color, "color");
  this._lineColor = color;
};
jsfc.SVGContext2D.prototype.setFillColor = function(color) {
  jsfc.Args.require(color, "color");
  this._fillColor = color;
};
jsfc.SVGContext2D.prototype.drawLine = function(x0, y0, x1, y1) {
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
jsfc.SVGContext2D.prototype.drawRect = function(x, y, w, h) {
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
jsfc.SVGContext2D.prototype.drawCircle = function(cx, cy, r) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "circle");
  t.setAttribute("stroke", this._lineColor.rgbaStr());
  t.setAttribute("stroke-width", this._stroke.lineWidth);
  t.setAttribute("fill", this._fillColor.rgbaStr());
  t.setAttribute("cx", cx);
  t.setAttribute("cy", cy);
  t.setAttribute("r", r);
  this.append(t);
};
jsfc.SVGContext2D.prototype.setFont = function(font) {
  this.font = font;
};
jsfc.SVGContext2D.prototype.stringWidth = function(str) {
};
jsfc.SVGContext2D.prototype.drawString = function(text, x, y) {
  this.fillText(text, x, y);
};
jsfc.SVGContext2D.prototype.fillText = function(text, x, y, maxWidth) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "text");
  t.setAttribute("x", x);
  t.setAttribute("y", y);
  t.setAttribute("style", this.getTextStyle());
  t.textContent = text;
  this.append(t);
};
jsfc.SVGContext2D.prototype.drawAlignedString = function(text, x, y, anchor) {
  var t = document.createElementNS("http://www.w3.org/2000/svg", "text");
  t.setAttribute("x", this._geomDP(x));
  t.setAttribute("font-family", this.font.family);
  t.setAttribute("font-size", this.font.size + "px");
  t.setAttribute("fill", this._fillColor.rgbaStr());
  t.setAttribute("transform", this._svgTransformStr());
  t.textContent = text;
  var anchorStr = "start";
  if (jsfc.TextAnchor.isHorizontalCenter(anchor)) {
    anchorStr = "middle";
  }
  if (jsfc.TextAnchor.isRight(anchor)) {
    anchorStr = "end";
  }
  t.setAttribute("text-anchor", anchorStr);
  var adj = this.font.size;
  if (jsfc.TextAnchor.isBottom(anchor)) {
    adj = 0;
  } else {
    if (jsfc.TextAnchor.isHalfHeight(anchor)) {
      adj = this.font.size / 2;
    }
  }
  t.setAttribute("y", this._geomDP(y + adj));
  this.append(t);
  return this.textDim(text);
};
jsfc.SVGContext2D.prototype.drawRotatedString = function(text, x, y, anchor, angle) {
  this.translate(x, y);
  this.rotate(angle);
  this.drawAlignedString(text, 0, 0, anchor);
  this.rotate(-angle);
  this.translate(-x, -y);
};
jsfc.SVGContext2D.prototype.fillRect = function(x, y, width, height) {
  var rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
  rect.setAttribute("x", x);
  rect.setAttribute("y", y);
  rect.setAttribute("width", width);
  rect.setAttribute("height", height);
  rect.setAttribute("fill", this.fillStyle);
  this.append(rect);
};
jsfc.SVGContext2D.prototype.getStrokeStyle = function() {
  return "stroke:" + this.strokeStyle;
};
jsfc.SVGContext2D.prototype.beginPath = function() {
  this.pathStr = "";
};
jsfc.SVGContext2D.prototype.closePath = function() {
  this.pathStr = this.pathStr + "Z";
};
jsfc.SVGContext2D.prototype._geomDP = function(x) {
  return x.toFixed(3);
};
jsfc.SVGContext2D.prototype.moveTo = function(x, y) {
  this.pathStr = this.pathStr + "M " + this._geomDP(x) + " " + this._geomDP(y);
};
jsfc.SVGContext2D.prototype.lineTo = function(x, y) {
  this.pathStr = this.pathStr + "L " + this._geomDP(x) + " " + this._geomDP(y);
};
jsfc.SVGContext2D.prototype.arc = function(cx, cy, r, startAngle, endAngle, counterclockwise) {
};
jsfc.SVGContext2D.prototype.arcTo = function(x1, y1, x2, y2, radius) {
};
jsfc.SVGContext2D.prototype.fill = function() {
};
jsfc.SVGContext2D.prototype.stroke = function() {
  var path = document.createElementNS("http://www.w3.org/2000/svg", "path");
  path.setAttribute("style", this._stroke.getStyleStr());
  path.setAttribute("d", this.pathStr);
  this.append(path);
};
jsfc.SVGContext2D.prototype.getTextStyle = function() {
  return this.font.family + " " + this.font.size + "px";
};
jsfc.SVGContext2D.prototype.translate = function(dx, dy) {
  this._transform.translate(dx, dy);
};
jsfc.SVGContext2D.prototype.rotate = function(radians) {
  this._transform.rotate(radians);
};
jsfc.SVGContext2D.prototype._svgTransformStr = function() {
  var t = this._transform;
  var s = "matrix(" + this._geomDP(t.scaleX) + "," + this._geomDP(t.shearY) + "," + this._geomDP(t.shearX) + "," + this._geomDP(t.scaleY) + "," + this._geomDP(t.translateX) + "," + this._geomDP(t.translateY) + ")";
  return s;
};
jsfc.SVGContext2D.prototype.textDim = function(text) {
  if (arguments.length !== 1) {
    throw new Error("Too many arguments.");
  }
  var svgText = document.createElementNS("http://www.w3.org/2000/svg", "text");
  svgText.setAttribute("style", this.font.styleStr());
  svgText.textContent = text;
  this._hiddenGroup.appendChild(svgText);
  var bbox = svgText.getBBox();
  var dim = new jsfc.Dimension(bbox.width, bbox.height);
  if (bbox.width == 0 && (bbox.height == 0 && text.length > 0)) {
    var h = svgText.scrollHeight;
    if (h == 0) {
      h = this.font.size;
    }
    dim = new jsfc.Dimension(svgText.scrollWidth, h);
  }
  this._hiddenGroup.removeChild(svgText);
  return dim;
};
jsfc.SVGContext2D.prototype.createRectElement = function(rect) {
  jsfc.Args.require(rect, "rect");
  var r = this.element("rect");
  r.setAttribute("x", rect.minX());
  r.setAttribute("y", rect.minY());
  r.setAttribute("width", rect.width());
  r.setAttribute("height", rect.height());
  return r;
};
jsfc.SVGLayer = function(id) {
  if (!(this instanceof jsfc.SVGLayer)) {
    throw new Error("Use 'new' for constructors.");
  }
  this._id = id;
  this._container = this.createElement("g");
  this._content = this.createElement("g");
  this._container.appendChild(this._content);
  this._stack = [this._content];
  this._defsContainer = this.createElement("g");
  this._defsContent = this.createElement("g");
  this._defsContainer.appendChild(this._defsContent);
};
jsfc.SVGLayer.prototype.getID = function() {
  return this._id;
};
jsfc.SVGLayer.prototype.getContainer = function() {
  return this._container;
};
jsfc.SVGLayer.prototype.getContent = function() {
  return this._content;
};
jsfc.SVGLayer.prototype.getStack = function() {
  return this._stack;
};
jsfc.SVGLayer.prototype.getDefsContainer = function() {
  return this._defsContainer;
};
jsfc.SVGLayer.prototype.getDefsContent = function() {
  return this._defsContent;
};
jsfc.SVGLayer.prototype.clear = function() {
  this._container.removeChild(this._content);
  this._defsContainer.removeChild(this._defsContent);
  this._content = this.createElement("g");
  this._container.appendChild(this._content);
  this._stack = [this._content];
  this._defsContent = this.createElement("g");
  this._defsContainer.appendChild(this._defsContent);
};
jsfc.SVGLayer.prototype.createElement = function(elementType) {
  return document.createElementNS("http://www.w3.org/2000/svg", elementType);
};
jsfc.TextAnchor = {TOP_LEFT:0, TOP_CENTER:1, TOP_RIGHT:2, HALF_ASCENT_LEFT:3, HALF_ASCENT_CENTER:4, HALF_ASCENT_RIGHT:5, CENTER_LEFT:6, CENTER:7, CENTER_RIGHT:8, BASELINE_LEFT:9, BASELINE_CENTER:10, BASELINE_RIGHT:11, BOTTOM_LEFT:12, BOTTOM_CENTER:13, BOTTOM_RIGHT:14, isLeft:function(anchor) {
  return this === jsfc.TextAnchor.TOP_LEFT || (this === jsfc.TextAnchor.CENTER_LEFT || (this === jsfc.TextAnchor.HALF_ASCENT_LEFT || (this === jsfc.TextAnchor.BASELINE_LEFT || this === jsfc.TextAnchor.BOTTOM_LEFT)));
}, isHorizontalCenter:function(anchor) {
  return anchor === jsfc.TextAnchor.TOP_CENTER || (anchor === jsfc.TextAnchor.CENTER || (anchor === jsfc.TextAnchor.HALF_ASCENT_CENTER || (anchor === jsfc.TextAnchor.BASELINE_CENTER || anchor === jsfc.TextAnchor.BOTTOM_CENTER)));
}, isRight:function(anchor) {
  return anchor === jsfc.TextAnchor.TOP_RIGHT || (anchor === jsfc.TextAnchor.CENTER_RIGHT || (anchor === jsfc.TextAnchor.HALF_ASCENT_RIGHT || (anchor === jsfc.TextAnchor.BASELINE_RIGHT || anchor === jsfc.TextAnchor.BOTTOM_RIGHT)));
}, isTop:function(anchor) {
  return anchor === jsfc.TextAnchor.TOP_LEFT || (anchor === jsfc.TextAnchor.TOP_CENTER || anchor === jsfc.TextAnchor.TOP_RIGHT);
}, isHalfAscent:function(anchor) {
  return anchor === jsfc.TextAnchor.HALF_ASCENT_LEFT || (anchor === jsfc.TextAnchor.HALF_ASCENT_CENTER || anchor === jsfc.TextAnchor.HALF_ASCENT_RIGHT);
}, isHalfHeight:function(anchor) {
  return anchor === jsfc.TextAnchor.CENTER_LEFT || (anchor === jsfc.TextAnchor.CENTER || anchor === jsfc.TextAnchor.CENTER_RIGHT);
}, isBaseline:function(anchor) {
  return anchor === jsfc.TextAnchor.BASELINE_LEFT || (anchor === jsfc.TextAnchor.BASELINE_CENTER || anchor === jsfc.TextAnchor.BASELINE_RIGHT);
}, isBottom:function(anchor) {
  return anchor === jsfc.TextAnchor.BOTTOM_LEFT || (anchor === jsfc.TextAnchor.BOTTOM_CENTER || anchor === jsfc.TextAnchor.BOTTOM_RIGHT);
}};
if (Object.freeze) {
  Object.freeze(jsfc.TextAnchor);
}
;jsfc.Transform = function() {
  if (!(this instanceof jsfc.Transform)) {
    throw new Error("Use 'new' for constructors.");
  }
  this.scaleX = 1;
  this.scaleY = 1;
  this.translateX = 0;
  this.translateY = 0;
  this.shearX = 0;
  this.shearY = 0;
};
jsfc.Transform.prototype.translate = function(dx, dy) {
  this.translateX = this.translateX + dx;
  this.translateY = this.translateY + dy;
};
jsfc.Transform.prototype.rotate = function(theta) {
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
jsfc.BaseElement = function(instance) {
  if (!(this instanceof jsfc.BaseElement)) {
    throw new Error("Use 'new' for construction.");
  }
  if (!instance) {
    instance = this;
  }
  jsfc.BaseElement.init(instance);
};
jsfc.BaseElement.init = function(instance) {
  instance._insets = new jsfc.Insets(2, 2, 2, 2);
  instance._refPt = jsfc.RefPt2D.CENTER;
  instance._backgroundPainter = null;
};
jsfc.BaseElement.prototype.getInsets = function() {
  return this._insets;
};
jsfc.BaseElement.prototype.setInsets = function(insets) {
  this._insets = insets;
};
jsfc.BaseElement.prototype.refPt = function(value) {
  if (!arguments.length) {
    return this._refPt;
  }
  this._refPt = value;
  return this;
};
jsfc.BaseElement.prototype.backgroundPainter = function(painter) {
  if (!arguments.length) {
    return this._backgroundPainter;
  }
  this._backgroundPainter = painter;
  return this;
};
jsfc.BaseElement.prototype.receive = function(visitor) {
  visitor(this);
};
jsfc.FlowElement = function() {
  if (!(this instanceof jsfc.FlowElement)) {
    throw Error("Use 'new' for constructor.");
  }
  jsfc.BaseElement.init(this);
  this._elements = [];
  this._halign = jsfc.HAlign.LEFT;
  this._hgap = 2;
};
jsfc.FlowElement.prototype = new jsfc.BaseElement;
jsfc.FlowElement.prototype.halign = function(align) {
  if (!arguments.length) {
    return this._halign;
  }
  this._halign = align;
  return this;
};
jsfc.FlowElement.prototype.hgap = function(value) {
  if (!arguments.length) {
    return this._hgap;
  }
  this._hgap = value;
  return this;
};
jsfc.FlowElement.prototype.add = function(element) {
  this._elements.push(element);
  return this;
};
jsfc.FlowElement.prototype.receive = function(visitor) {
  this._elements.forEach(function(child) {
    child.receive(visitor);
  });
};
jsfc.FlowElement.prototype.preferredSize = function(ctx, bounds) {
  var insets = this.getInsets();
  var w = insets.left() + insets.right();
  var h = insets.top() + insets.bottom();
  var maxRowWidth = 0;
  var elementCount = this._elements.length;
  var i = 0;
  while (i < elementCount) {
    var elementsInRow = this._rowOfElements(i, ctx, bounds);
    var rowHeight = this._calcRowHeight(elementsInRow);
    var rowWidth = this._calcRowWidth(elementsInRow, this._hgap);
    maxRowWidth = Math.max(rowWidth, maxRowWidth);
    h += rowHeight;
    i = i + elementsInRow.length;
  }
  w += maxRowWidth;
  return new jsfc.Dimension(insets.left() + w + insets.right(), insets.top() + h + insets.bottom());
};
jsfc.FlowElement.prototype._rowOfElements = function(first, ctx, bounds) {
  var result = [];
  var index = first;
  var full = false;
  var insets = this.getInsets();
  var w = insets.left() + insets.right();
  while (index < this._elements.length && !full) {
    var element = this._elements[index];
    var dim = element.preferredSize(ctx, bounds);
    if (w + dim.width() < bounds.width() || index === first) {
      result.push({"element":element, "dim":dim});
      w += dim.width() + this._hgap;
      index++;
    } else {
      full = true;
    }
  }
  return result;
};
jsfc.FlowElement.prototype._calcRowHeight = function(elements) {
  var height = 0;
  for (var i = 0;i < elements.length;i++) {
    height = Math.max(height, elements[i].dim.height());
  }
  return height;
};
jsfc.FlowElement.prototype._calcRowWidth = function(elements) {
  var width = 0;
  var count = elements.length;
  for (var i = 0;i < elements.length;i++) {
    width += elements[i].dim.width();
  }
  if (count > 1) {
    width += (count - 1) * this._hgap;
  }
  return width;
};
jsfc.FlowElement.prototype.layoutElements = function(context, bounds) {
  var result = [];
  var i = 0;
  var insets = this.getInsets();
  var x = bounds.x() + insets.left();
  var y = bounds.y() + insets.top();
  while (i < this._elements.length) {
    var elementsInRow = this._rowOfElements(i, context, bounds);
    var h = this._calcRowHeight(elementsInRow);
    var w = this._calcRowWidth(elementsInRow);
    if (this._halign === jsfc.HAlign.CENTER) {
      x = bounds.centerX() - w / 2;
    } else {
      if (this._halign === jsfc.HAlign.RIGHT) {
        x = bounds.maxX() - insets.right() - w;
      }
    }
    for (var j = 0;j < elementsInRow.length;j++) {
      var position = new jsfc.Rectangle(x, y, elementsInRow[j].dim.width(), h);
      result.push(position);
      x += position.width() + this._hgap;
    }
    i = i + elementsInRow.length;
    x = bounds.x() + insets.left();
    y += h;
  }
  return result;
};
jsfc.FlowElement.prototype.draw = function(context, bounds) {
  var dim = this.preferredSize(context, bounds);
  var fitter = new jsfc.Fit2D(new jsfc.Anchor2D(this.refPt()), jsfc.Scale2D.NONE);
  var dest = fitter.fit(dim, bounds);
  var layoutInfo = this.layoutElements(context, dest);
  for (var i = 0;i < this._elements.length;i++) {
    var rect = layoutInfo[i];
    var element = this._elements[i];
    element.draw(context, rect);
  }
};
jsfc.GridElement = function() {
  if (!(this instanceof jsfc.GridElement)) {
    throw new Error("Use 'new' for construction.");
  }
  jsfc.BaseElement.init(this);
  this._elements = new jsfc.KeyedValues2DDataset;
};
jsfc.GridElement.prototype = new jsfc.BaseElement;
jsfc.GridElement.prototype.add = function(element, rowKey, columnKey) {
  this._elements.add(rowKey, columnKey, element);
  return this;
};
jsfc.GridElement.prototype._findCellDims = function(context, bounds) {
  var widths = jsfc.Utils.makeArrayOf(0, this._elements.columnCount());
  var heights = jsfc.Utils.makeArrayOf(0, this._elements.rowCount());
  for (var r = 0;r < this._elements.rowCount();r++) {
    for (var c = 0;c < this._elements.columnCount();c++) {
      var element = this._elements.valueByIndex(r, c);
      if (!element) {
        continue;
      }
      var dim = element.preferredSize(context, bounds);
      widths[c] = Math.max(widths[c], dim.width());
      heights[r] = Math.max(heights[r], dim.height());
    }
  }
  return{"widths":widths, "heights":heights};
};
jsfc.GridElement.prototype.preferredSize = function(ctx, bounds) {
  var me = this;
  var insets = this.getInsets();
  var cellDims = this._findCellDims(ctx, bounds);
  var w = insets.left() + insets.right();
  for (var i = 0;i < cellDims.widths.length;i++) {
    w = w + cellDims.widths[i];
  }
  var h = insets.top() + insets.bottom();
  for (var i = 0;i < cellDims.heights.length;i++) {
    h = h + cellDims.heights[i];
  }
  return new jsfc.Dimension(w, h);
};
jsfc.GridElement.prototype.layoutElements = function(ctx, bounds) {
  var insets = this.getInsets();
  var cellDims = this._findCellDims(ctx, bounds);
  var positions = [];
  var y = bounds.y() + insets.top();
  for (var r = 0;r < this._elements.rowCount();r++) {
    var x = bounds.x() + insets.left();
    for (var c = 0;c < this._elements.columnCount();c++) {
      positions.push(new jsfc.Rectangle(x, y, cellDims.widths[c], cellDims.heights[r]));
      x += cellDims.widths[c];
    }
    y = y + cellDims.heights[r];
  }
  return positions;
};
jsfc.GridElement.prototype.draw = function(ctx, bounds) {
  var positions = this.layoutElements(ctx, bounds);
  for (var r = 0;r < this._elements.rowCount();r++) {
    for (var c = 0;c < this._elements.columnCount();c++) {
      var element = this._elements.valueByIndex(r, c);
      if (!element) {
        continue;
      }
      var pos = positions[r * this._elements.columnCount() + c];
      element.draw(ctx, pos);
    }
  }
};
jsfc.GridElement.prototype.receive = function(visitor) {
  for (var r = 0;r < this._elements.rowCount();r++) {
    for (var c = 0;c < this._elements.columnCount();c++) {
      var element = this._elements.valueByIndex(r, c);
      if (element === null) {
        continue;
      }
      element.receive(visitor);
    }
  }
};
jsfc.RectangleElement = function(width, height) {
  if (!(this instanceof jsfc.RectangleElement)) {
    throw new Error("Use 'new' for construction.");
  }
  jsfc.BaseElement.init(this);
  this._width = width;
  this._height = height;
  this._fillColor = new jsfc.Color(255, 255, 255);
  this._backgroundPainter = new jsfc.StandardRectanglePainter(new jsfc.Color(255, 255, 255, 0.3), new jsfc.Color(0, 0, 0, 0));
};
jsfc.RectangleElement.prototype = new jsfc.BaseElement;
jsfc.RectangleElement.prototype.width = function(value) {
  if (!arguments.length) {
    return this._width;
  }
  this._width = value;
  return this;
};
jsfc.RectangleElement.prototype.height = function(value) {
  if (!arguments.length) {
    return this._height;
  }
  this._height = value;
  return this;
};
jsfc.RectangleElement.prototype.getFillColor = function() {
  return this._fillColor;
};
jsfc.RectangleElement.prototype.setFillColor = function(color) {
  if (typeof color === "string") {
    throw new Error("needs to be a color");
  }
  this._fillColor = color;
  return this;
};
jsfc.RectangleElement.prototype.preferredSize = function(ctx, bounds) {
  var insets = this.getInsets();
  var w = insets.left() + this._width + insets.right();
  var h = insets.top() + this._height + insets.bottom();
  var bw = bounds.width();
  var bh = bounds.height();
  return new jsfc.Dimension(Math.min(w, bw), Math.min(h, bh));
};
jsfc.RectangleElement.prototype.layoutElements = function(ctx, bounds) {
  var insets = this.getInsets();
  var w = Math.min(insets.left() + this._width + insets.right(), bounds.width());
  var h = Math.min(insets.top() + this._height + insets.bottom(), bounds.height());
  var pos = new jsfc.Rectangle(bounds.centerX() - w / 2, bounds.centerY() - h / 2, w, h);
  return[pos];
};
jsfc.RectangleElement.prototype.draw = function(ctx, bounds) {
  var backgroundPainter = this.backgroundPainter();
  if (backgroundPainter) {
    backgroundPainter.paint(ctx, bounds);
  }
  var insets = this.getInsets();
  var ww = Math.max(bounds.width() - insets.left() - insets.right(), 0);
  var hh = Math.max(bounds.height() - insets.top() - insets.bottom(), 0);
  var w = Math.min(this._width, ww);
  var h = Math.min(this._height, hh);
  var rect = ctx.element("rect");
  rect.setAttribute("x", bounds.centerX() - w / 2);
  rect.setAttribute("y", bounds.centerY() - h / 2);
  rect.setAttribute("width", w);
  rect.setAttribute("height", h);
  var styleStr = "fill: " + this._fillColor.rgbaStr();
  rect.setAttribute("style", styleStr);
  ctx.append(rect);
};
jsfc.ShapeElement = function(shape, color) {
  jsfc.BaseElement.init(this);
  this.shape = shape;
  this.color = color;
};
jsfc.ShapeElement.prototype = new jsfc.BaseElement;
jsfc.ShapeElement.prototype.preferredSize = function(ctx, bounds, constraints) {
  var shapeBounds = this.shape.bounds();
  var insets = this.getInsets();
  var w = Math.min(bounds.width, shapeBounds.width + insets.left + insets.right);
  var h = Math.min(bounds.height, shapeBounds.height + insets.top + insets.bottom);
  return new jsfc.Dimension(w, h);
};
jsfc.ShapeElement.prototype.layoutElements = function(ctx, bounds, constraints) {
  var dim = this.preferredSize(ctx, bounds, constraints);
  var pos = new jsfc.Rectangle(bounds.centerX() - dim.width() / 2, bounds.centerY() - dim.height() / 2, dim.width(), dim.height());
  return[pos];
};
jsfc.ShapeElement.prototype.draw = function(ctx, bounds) {
};
jsfc.StandardRectanglePainter = function(fillColor, strokeColor) {
  if (!(this instanceof jsfc.StandardRectanglePainter)) {
    throw new Error("Use 'new' for construction.");
  }
  this._fillColor = fillColor;
  this._strokeColor = strokeColor;
};
jsfc.StandardRectanglePainter.prototype.paint = function(ctx, bounds) {
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
jsfc.TableElement = function() {
};
jsfc.TableElement.prototype.preferredSize = function(ctx, bounds) {
};
jsfc.TableElement.prototype.layoutElements = function(ctx, bounds) {
};
jsfc.TableElement.prototype.draw = function(ctx, bounds) {
};
jsfc.TableElement.prototype.receive = function(visitor) {
};
jsfc.TextElement = function(textStr) {
  if (!(this instanceof jsfc.TextElement)) {
    throw new Error("Use 'new' for construction.");
  }
  jsfc.BaseElement.init(this);
  this._text = textStr;
  this._font = new jsfc.Font("Palatino, serif", 16);
  this._color = new jsfc.Color(0, 0, 0);
  this._halign = jsfc.HAlign.LEFT;
  this._backgroundPainter = new jsfc.StandardRectanglePainter(new jsfc.Color(255, 255, 255, 0.3), new jsfc.Color(0, 0, 0, 0));
};
jsfc.TextElement.prototype = new jsfc.BaseElement;
jsfc.TextElement.prototype.getText = function() {
  return this._text;
};
jsfc.TextElement.prototype.setText = function(text) {
  this._text = text;
  return this;
};
jsfc.TextElement.prototype.getFont = function() {
  return this._font;
};
jsfc.TextElement.prototype.setFont = function(font) {
  this._font = font;
  return this;
};
jsfc.TextElement.prototype.getColor = function() {
  return this._color;
};
jsfc.TextElement.prototype.setColor = function(color) {
  this._color = color;
  return this;
};
jsfc.TextElement.prototype.text = function(str) {
  throw new Error("Use get/setText()");
};
jsfc.TextElement.prototype.color = function(str) {
  throw new Error("Use get/setColor()");
};
jsfc.TextElement.prototype.font = function(font) {
  throw new Error("Use get/setFont().");
};
jsfc.TextElement.prototype.halign = function(align) {
  if (!arguments.length) {
    return this._halign;
  }
  this._halign = align;
  return this;
};
jsfc.TextElement.prototype.preferredSize = function(context, bounds) {
  var insets = this.getInsets();
  context.setFont(this._font);
  var dim = context.textDim(this._text);
  return new jsfc.Dimension(insets.left() + dim.width() + insets.right(), insets.top() + dim.height() + insets.bottom());
};
jsfc.TextElement.prototype.layoutElements = function(ctx, bounds) {
  var insets = this.getInsets();
  ctx.setFont(this._font);
  var dim = ctx.textDim(this._text);
  var w = dim.width() + insets.left() + insets.right();
  var x = bounds.x();
  switch(this._halign) {
    case jsfc.HAlign.LEFT:
      x = bounds.x();
      break;
    case jsfc.HAlign.CENTER:
      x = bounds.centerX() - w / 2;
      break;
    case jsfc.HAlign.RIGHT:
      x = bounds.maxX() - w;
      break;
  }
  var y = bounds.y();
  var h = Math.min(dim.height() + insets.top() + insets.bottom(), bounds.height());
  return[new jsfc.Rectangle(x, y, w, h)];
};
jsfc.TextElement.prototype.draw = function(ctx, bounds) {
  var backgroundPainter = this.backgroundPainter();
  if (backgroundPainter) {
    backgroundPainter.paint(ctx, bounds);
  }
  var insets = this.getInsets();
  var pos = this.layoutElements(ctx, bounds)[0];
  var t = ctx.element("text");
  t.setAttribute("x", pos.x() + insets.left());
  var upper = pos.y() + insets.top();
  var lower = pos.maxY() - insets.bottom();
  var span = lower - upper;
  var base = lower - 0.18 * span;
  t.setAttribute("y", base);
  t.textContent = this._text;
  t.setAttribute("style", "fill: " + this._color.rgbaStr() + "; " + this._font.styleStr());
  ctx.append(t);
};
jsfc.LegendBuilder = function() {
};
jsfc.LegendBuilder.prototype.createLegend = function(plot, anchor, orientation, style) {
};
jsfc.LegendItemInfo = function(key, color) {
  this.seriesKey = key || "";
  this.label = key || "";
  this.description = "";
  this.shape = null;
  this.color = color;
};
jsfc.StandardLegendBuilder = function(instance) {
  if (!(this instanceof jsfc.StandardLegendBuilder)) {
    throw new Error("Use 'new' for constructor.");
  }
  if (!instance) {
    instance = this;
  }
  jsfc.StandardLegendBuilder.init(instance);
};
jsfc.StandardLegendBuilder.init = function(instance) {
  instance._font = new jsfc.Font("Palatino, serif", 12);
};
jsfc.StandardLegendBuilder.prototype.getFont = function() {
  return this._font;
};
jsfc.StandardLegendBuilder.prototype.setFont = function(font) {
  this._font = font;
};
jsfc.StandardLegendBuilder.prototype.createLegend = function(plot, anchor, orientation, style) {
  var info = plot.legendInfo();
  var result = new jsfc.FlowElement;
  var me = this;
  info.forEach(function(info) {
    var shape = (new jsfc.RectangleElement(8, 5)).setFillColor(info.color);
    var text = (new jsfc.TextElement(info.label)).setFont(me._font);
    var item = new jsfc.GridElement;
    item.add(shape, "R1", "C1");
    item.add(text, "R1", "C2");
    result.add(item);
  });
  return result;
};
jsfc.FixedLegendBuilder = function() {
  if (!(this instanceof jsfc.FixedLegendBuilder)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.StandardLegendBuilder.init(this);
  this._info = [];
};
jsfc.FixedLegendBuilder.prototype = new jsfc.StandardLegendBuilder;
jsfc.FixedLegendBuilder.prototype.add = function(key, color) {
  this._info.push(new jsfc.LegendItemInfo(key, color));
};
jsfc.FixedLegendBuilder.prototype.clear = function() {
  this._info = [];
};
jsfc.FixedLegendBuilder.prototype.createLegend = function(plot, anchor, orientation, style) {
  var info = this._info;
  var result = new jsfc.FlowElement;
  var legendBuilder = this;
  info.forEach(function(info) {
    var shape = (new jsfc.RectangleElement(8, 5)).setFillColor(info.color);
    var text = (new jsfc.TextElement(info.label)).setFont(legendBuilder.getFont());
    var item = new jsfc.GridElement;
    item.add(shape, "R1", "C1");
    item.add(text, "R1", "C2");
    result.add(item);
  });
  return result;
};
jsfc.Bin = function(xmin, xmax, incmin, incmax) {
  this.xmin = xmin;
  this.xmax = xmax;
  this.incMin = incmin !== false;
  this.incMax = incmax !== false;
  this.count = 0;
};
jsfc.Bin.prototype.includes = function(value) {
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
jsfc.Bin.prototype.overlaps = function(bin) {
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
jsfc.DatasetUtils = {};
jsfc.DatasetUtils.extractStackBaseValues = function(dataset, baseline) {
  baseline = typeof baseline !== "undefined" ? baseline : 0;
  var result = new jsfc.KeyedValues2DDataset;
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
jsfc.DatasetUtils.extractXYDatasetFromColumns2D = function(dataset, xcol, ycol, seriesKey) {
  jsfc.Args.requireString(xcol, "xcol");
  jsfc.Args.requireString(ycol, "ycol");
  var result = new jsfc.StandardXYDataset;
  seriesKey = seriesKey || "series 1";
  for (var r = 0;r < dataset.rowCount();r++) {
    var rowKey = dataset.rowKey(r);
    var x = dataset.valueByKey(rowKey, xcol);
    var y = dataset.valueByKey(rowKey, ycol);
    result.add(seriesKey, x, y);
    var rowPropKeys = dataset.getRowPropertyKeys(rowKey);
    var xPropKeys = dataset.getItemPropertyKeys(rowKey, xcol);
    var yPropKeys = dataset.getItemPropertyKeys(rowKey, ycol);
    var itemKey = result.getItemKey(0, result.itemCount(0) - 1);
    rowPropKeys.forEach(function(key) {
      var p = dataset.getRowProperty(rowKey, key);
      result.setItemProperty(seriesKey, itemKey, key, p);
    });
    xPropKeys.forEach(function(key) {
      var p = dataset.getItemProperty(rowKey, xcol, key);
      result.setItemProperty(seriesKey, itemKey, key, p);
    });
    yPropKeys.forEach(function(key) {
      var p = dataset.getItemProperty(rowKey, ycol, key);
      result.setItemProperty(seriesKey, itemKey, key, p);
    });
  }
  var xsymbols = dataset.getColumnProperty(xcol, "symbols");
  if (xsymbols) {
    result.setProperty("x-symbols", xsymbols);
  }
  var ysymbols = dataset.getColumnProperty(ycol, "symbols");
  if (ysymbols) {
    result.setProperty("y-symbols", ysymbols);
  }
  return result;
};
jsfc.DatasetUtils.extractXYDatasetFromRows2D = function(dataset, xrow, yrow, seriesKey) {
  var result = new jsfc.StandardXYDataset;
  seriesKey = seriesKey || "series 1";
  for (var c = 0;c < dataset.columnCount();c++) {
    var colKey = dataset.columnKey(c);
    var x = dataset.valueByKey(xrow, colKey);
    var y = dataset.valueByKey(yrow, colKey);
    result.add(seriesKey, x, y);
    var colPropKeys = dataset.getColumnPropertyKeys(colKey);
    var xPropKeys = dataset.getItemPropertyKeys(xrow, colKey);
    var yPropKeys = dataset.getItemPropertyKeys(yrow, colKey);
    var itemKey = result.getItemKey(0, result.itemCount(0) - 1);
    colPropKeys.forEach(function(key) {
      var p = dataset.getColumnProperty(colKey, key);
      result.setItemProperty(seriesKey, itemKey, key, p);
    });
    xPropKeys.forEach(function(key) {
      var p = dataset.getItemProperty(xrow, colKey, key);
      result.setItemProperty(seriesKey, itemKey, key, p);
    });
    yPropKeys.forEach(function(key) {
      var p = dataset.getItemProperty(yrow, colKey, key);
      result.setItemProperty(seriesKey, itemKey, key, p);
    });
  }
  var xsymbols = dataset.getRowProperty(xrow, "symbols");
  if (xsymbols) {
    result.setProperty("x-symbols", xsymbols);
  }
  var ysymbols = dataset.getRowProperty(yrow, "symbols");
  if (ysymbols) {
    result.setProperty("y-symbols", ysymbols);
  }
  return result;
};
jsfc.DatasetUtils.extractXYDatasetFromColumns = function(dataset, xcol, ycol) {
  var result = new jsfc.StandardXYDataset;
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
        result.setItemProperty(seriesKey, itemKey, key, p);
      });
      yPropKeys.forEach(function(key) {
        var p = dataset.getProperty(seriesKey, rowKey, ycol, key);
        result.setItemProperty(seriesKey, itemKey, key, p);
      });
    }
  }
  return result;
};
jsfc.DatasetUtils.extractXYDatasetFromRows = function(dataset, xrow, yrow) {
  var result = new jsfc.StandardXYDataset;
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
        result.setItemProperty(seriesKey, itemKey, key, p);
      });
      yPropKeys.forEach(function(key) {
        var p = dataset.getProperty(seriesKey, yrow, colKey, key);
        result.setItemProperty(seriesKey, itemKey, key, p);
      });
      result.add(seriesKey, x, y);
    }
  }
  return result;
};
jsfc.HistogramDataset = function(seriesKey) {
  this._seriesKey = seriesKey;
  this.bins = [];
  this.selections = [];
  this._listeners = [];
};
jsfc.HistogramDataset.prototype.binCount = function() {
  return this.bins.length;
};
jsfc.HistogramDataset.prototype.isEmpty = function() {
  var result = true;
  this.bins.forEach(function(bin) {
    if (bin.count > 0) {
      result = false;
    }
  });
  return result;
};
jsfc.HistogramDataset.prototype.addListener = function(listenerObj) {
  this._listeners.push(listenerObj);
  return this;
};
jsfc.HistogramDataset.prototype.removeListener = function(listenerObj) {
  var i = this._listeners.indexOf(listenerObj);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
jsfc.HistogramDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
jsfc.HistogramDataset.prototype.addBin = function(xmin, xmax, incmin, incmax) {
  var incmin_ = incmin !== false;
  var incmax_ = incmax !== false;
  var bin = new jsfc.Bin(xmin, xmax, incmin_, incmax_);
  this.bins.push(bin);
  return this;
};
jsfc.HistogramDataset.prototype.isOverlapping = function(bin) {
  for (var i = 0;i < this.bins.length;i++) {
    if (this.bins[i].overlaps(bin)) {
      return true;
    }
  }
  return false;
};
jsfc.HistogramDataset.prototype.xmid = function(binIndex) {
  var bin = this.bins[binIndex];
  return(bin.xmin + bin.xmax) / 2;
};
jsfc.HistogramDataset.prototype.xstart = function(binIndex) {
  return this.bins[binIndex].xmin;
};
jsfc.HistogramDataset.prototype.xend = function(binIndex) {
  return this.bins[binIndex].xmax;
};
jsfc.HistogramDataset.prototype.count = function(binIndex) {
  return this.bins[binIndex].count;
};
jsfc.HistogramDataset.prototype.reset = function() {
  this.bins.forEach(function(bin) {
    bin.count = 0;
  });
  return this;
};
jsfc.HistogramDataset.prototype._binIndex = function(value) {
  for (var i = 0;i < this.bins.length;i++) {
    if (this.bins[i].includes(value)) {
      return i;
    }
  }
  return-1;
};
jsfc.HistogramDataset.prototype.bounds = function() {
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
jsfc.HistogramDataset.prototype.add = function(value, notify) {
  var binIndex = this._binIndex(value);
  if (binIndex >= 0) {
    this.bins[binIndex].count++;
  } else {
    throw new Error("No bin for the value " + value);
  }
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.HistogramDataset.prototype.addAll = function(values, notify) {
  var me = this;
  values.forEach(function(v) {
    me.add(v, false);
  });
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.HistogramDataset.prototype.seriesCount = function() {
  return 1;
};
jsfc.HistogramDataset.prototype.itemCount = function(seriesIndex) {
  return this.binCount();
};
jsfc.HistogramDataset.prototype.xbounds = function() {
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
jsfc.HistogramDataset.prototype.ybounds = function() {
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
jsfc.HistogramDataset.prototype.seriesKeys = function() {
  return[this._seriesKey];
};
jsfc.HistogramDataset.prototype.seriesIndex = function(seriesKey) {
  if (seriesKey === this._seriesKey) {
    return 0;
  }
  return-1;
};
jsfc.HistogramDataset.prototype.seriesKey = function(seriesIndex) {
  if (seriesIndex === 0) {
    return this._seriesKey;
  }
  throw new Error("Invalid seriesIndex: " + seriesIndex);
};
jsfc.HistogramDataset.prototype.getItemKey = function(seriesIndex, itemIndex) {
  if (seriesIndex === 0) {
    return itemIndex;
  }
  throw new Error("Invalid seriesIndex: " + seriesIndex);
};
jsfc.HistogramDataset.prototype.itemIndex = function(seriesKey, itemKey) {
  if (seriesKey === this._seriesKey) {
    return itemKey;
  }
  throw new Error("Invalid seriesIndex: " + seriesKey);
};
jsfc.HistogramDataset.prototype.x = function(seriesIndex, itemIndex) {
  return this.xmid(itemIndex);
};
jsfc.HistogramDataset.prototype.xmin = function(seriesIndex, itemIndex) {
  return this.xstart(itemIndex);
};
jsfc.HistogramDataset.prototype.xmax = function(seriesIndex, itemIndex) {
  return this.xend(itemIndex);
};
jsfc.HistogramDataset.prototype.y = function(seriesIndex, itemIndex) {
  return this.count(itemIndex);
};
jsfc.HistogramDataset.prototype.getProperty = function(seriesKey, itemKey, propertyKey) {
  return null;
};
jsfc.KeyedValuesDataset = function() {
  if (!(this instanceof jsfc.KeyedValuesDataset)) {
    return new jsfc.KeyedValuesDataset;
  }
  this.data = {"sections":[]};
  this.properties = [];
  this.selections = [];
  this._listeners = [];
};
jsfc.KeyedValuesDataset.prototype.itemCount = function() {
  return this.data.sections.length;
};
jsfc.KeyedValuesDataset.prototype.isEmpty = function() {
  return this.data.sections.length === 0;
};
jsfc.KeyedValuesDataset.prototype.key = function(index) {
  return this.data.sections[index].key;
};
jsfc.KeyedValuesDataset.prototype.keys = function() {
  return this.data.sections.map(function(d) {
    return d.key;
  });
};
jsfc.KeyedValuesDataset.prototype.indexOf = function(sectionKey) {
  var arrayLength = this.data.sections.length;
  for (var i = 0;i < arrayLength;i++) {
    if (this.data.sections[i].key === sectionKey) {
      return i;
    }
  }
  return-1;
};
jsfc.KeyedValuesDataset.prototype.valueByIndex = function(index) {
  return this.data.sections[index].value;
};
jsfc.KeyedValuesDataset.prototype.valueByKey = function(sectionKey) {
  var sectionIndex = this.indexOf(sectionKey);
  if (sectionIndex < 0) {
    return null;
  }
  return this.valueByIndex(sectionIndex);
};
jsfc.KeyedValuesDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
jsfc.KeyedValuesDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
jsfc.KeyedValuesDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
jsfc.KeyedValuesDataset.prototype.add = function(sectionKey, value, notify) {
  var i = this.indexOf(sectionKey);
  if (i < 0) {
    this.data.sections.push({"key":sectionKey, "value":value});
    this.properties.push(new jsfc.Map);
  } else {
    this.data.sections[i].value = value;
  }
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.KeyedValuesDataset.prototype.remove = function(sectionKey, notify) {
  if (!sectionKey) {
    throw new Error("The 'sectionKey' must be defined.");
  }
  var i = this.indexOf(sectionKey);
  if (i < 0) {
    throw new Error("The sectionKey '" + sectionKey.toString() + "' is not recognised.");
  }
  this.data.sections.splice(i, 1);
  this.properties.splice(i, 1);
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.KeyedValuesDataset.prototype.parse = function(jsonStr, notify) {
  this.data.sections = JSON.parse(jsonStr);
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.KeyedValuesDataset.prototype.load = function(data, notify) {
  this.data.sections = data;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.KeyedValuesDataset.prototype.removeByIndex = function(itemIndex) {
  this.data.sections.splice(itemIndex, 1);
  this.properties.splice(itemIndex, 1);
  return this;
};
jsfc.KeyedValuesDataset.prototype.totalForDataset = function(dataset) {
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
jsfc.KeyedValuesDataset.prototype.minForDataset = function(dataset) {
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
jsfc.KeyedValuesDataset.prototype.maxForDataset = function(dataset) {
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
jsfc.KeyedValuesDataset.prototype.total = function() {
  return this.totalForDataset(this);
};
jsfc.KeyedValuesDataset.prototype.min = function() {
  return this.minForDataset(this);
};
jsfc.KeyedValuesDataset.prototype.max = function() {
  return this.maxForDataset(this);
};
jsfc.KeyedValuesDataset.prototype.propertyKeys = function(sectionKey) {
  var i = this.indexOf(sectionKey);
  var map = this.properties[i];
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
jsfc.KeyedValuesDataset.prototype.getProperty = function(sectionKey, propertyKey) {
  var i = this.indexOf(sectionKey);
  return this.properties[i].get(propertyKey);
};
jsfc.KeyedValuesDataset.prototype.setProperty = function(sectionKey, propertyKey, value) {
  var i = this.indexOf(sectionKey);
  if (i < 0) {
    throw new Error("Did not recognise 'sectionKey' " + sectionKey);
  }
  var map = this.properties[i];
  map.put(propertyKey, value);
};
jsfc.KeyedValuesDataset.prototype.clearProperties = function(sectionKey) {
  var i = this.indexOf(sectionKey);
  this.properties[i] = new jsfc.Map;
};
jsfc.KeyedValuesDataset.prototype.select = function(selectionId, key) {
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
jsfc.KeyedValuesDataset.prototype.unselect = function(selectionId, key) {
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
jsfc.KeyedValuesDataset.prototype.isSelected = function(selectionId, key) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
    return selection.items.indexOf(key) >= 0;
  }
};
jsfc.KeyedValuesDataset.prototype.clearSelection = function(selectionId, notify) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
  return this;
};
jsfc.KeyedValuesDataset.prototype._indexOfSelection = function(selectionId) {
  return jsfc.Utils.findInArray(this.selections, function(selection) {
    return selection.id === selectionId;
  });
};
jsfc.KeyedValues2DDataset = function() {
  if (!(this instanceof jsfc.KeyedValues2DDataset)) {
    return new jsfc.KeyedValues2DDataset;
  }
  this.data = {"columnKeys":[], "rows":[]};
  this.properties = {"columns":[], "rows":[]};
  this.selections = [];
  this._listeners = [];
};
jsfc.KeyedValues2DDataset.prototype.rowCount = function() {
  return this.data.rows.length;
};
jsfc.KeyedValues2DDataset.prototype.columnCount = function() {
  return this.data.columnKeys.length;
};
jsfc.KeyedValues2DDataset.prototype.isEmpty = function() {
  if (!this.data.hasOwnProperty("columnKeys")) {
    return true;
  }
  return this.data.columnKeys.length === 0 && this.data.rows.length === 0;
};
jsfc.KeyedValues2DDataset.prototype.add = function(rowKey, columnKey, value, notify) {
  if (this.isEmpty()) {
    this.data.columnKeys.push(columnKey);
    this.data.rows.push({"key":rowKey, "values":[value]});
    this.properties.columns.push(null);
    this.properties.rows.push({"key":rowKey, "rowProperties":null, "maps":[null]});
    return this;
  }
  var columnIndex = this.columnIndex(columnKey);
  if (columnIndex < 0) {
    this.data.columnKeys.push(columnKey);
    this.properties.columns.push(null);
    var rowCount = this.data.rows.length;
    for (var r = 0;r < rowCount;r++) {
      this.data.rows[r].values.push(null);
      this.properties.rows[r].maps.push(null);
    }
    columnIndex = this.columnCount() - 1;
  }
  var rowIndex = this.rowIndex(rowKey);
  if (rowIndex < 0) {
    var rowData = new Array(this.columnCount());
    rowData[columnIndex] = value;
    this.data.rows.push({"key":rowKey, "values":rowData});
    var rowItemProperties = new Array(this.columnCount());
    this.properties.rows.push({"key":rowKey, "maps":rowItemProperties});
  } else {
    this.data.rows[rowIndex].values[columnIndex] = value;
  }
  return this;
};
jsfc.KeyedValues2DDataset.prototype.parse = function(jsonStr, notify) {
  this.load(JSON.parse(jsonStr));
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.KeyedValues2DDataset.prototype.load = function(data, notify) {
  this.data = data;
  if (!this.data.hasOwnProperty("rows")) {
    this.data.rows = [];
  }
  if (!this.data.hasOwnProperty("columnKeys")) {
    this.data.columnKeys = [];
  }
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.KeyedValues2DDataset.prototype.valueByIndex = function(rowIndex, columnIndex) {
  return this.data.rows[rowIndex].values[columnIndex];
};
jsfc.KeyedValues2DDataset.prototype.rowKey = function(rowIndex) {
  return this.data.rows[rowIndex].key;
};
jsfc.KeyedValues2DDataset.prototype.rowIndex = function(rowKey) {
  var rowCount = this.data.rows.length;
  for (var r = 0;r < rowCount;r++) {
    if (this.data.rows[r].key === rowKey) {
      return r;
    }
  }
  return-1;
};
jsfc.KeyedValues2DDataset.prototype.rowKeys = function() {
  return this.data.rows.map(function(d) {
    return d.key;
  });
};
jsfc.KeyedValues2DDataset.prototype.columnKey = function(columnIndex) {
  return this.data.columnKeys[columnIndex];
};
jsfc.KeyedValues2DDataset.prototype.columnIndex = function(columnKey) {
  var columnCount = this.data.columnKeys.length;
  for (var c = 0;c < columnCount;c++) {
    if (this.data.columnKeys[c] === columnKey) {
      return c;
    }
  }
  return-1;
};
jsfc.KeyedValues2DDataset.prototype.columnKeys = function() {
  return this.data.columnKeys.map(function(d) {
    return d;
  });
};
jsfc.KeyedValues2DDataset.prototype.valueByKey = function(rowKey, columnKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  return this.valueByIndex(rowIndex, columnIndex);
};
jsfc.KeyedValues2DDataset.prototype.minForDataset = function(dataset) {
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
jsfc.KeyedValues2DDataset.prototype.maxForDataset = function(dataset) {
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
jsfc.KeyedValues2DDataset.prototype.min = function() {
  return this.minForDataset(this);
};
jsfc.KeyedValues2DDataset.prototype.max = function() {
  return this.maxForDataset(this);
};
jsfc.KeyedValues2DDataset.prototype.yValues = function(rowIndex) {
  return this.data.rows[rowIndex].values.map(function(d) {
    return d;
  });
};
jsfc.KeyedValues2DDataset.prototype.bounds = function() {
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
jsfc.KeyedValues2DDataset.prototype.getRowPropertyKeys = function(rowKey) {
  var rowIndex = this.rowIndex(rowKey);
  var map = this.properties.rows[rowIndex].rowProperties;
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
jsfc.KeyedValues2DDataset.prototype.getRowProperty = function(rowKey, propertyKey) {
  var rowIndex = this.rowIndex(rowKey);
  var map = this.properties.rows[rowIndex].rowProperties;
  if (map) {
    return map.get(propertyKey);
  } else {
    return undefined;
  }
};
jsfc.KeyedValues2DDataset.prototype.setRowProperty = function(rowKey, propertyKey, value) {
  var rowIndex = this.rowIndex(rowKey);
  var map = this.properties.rows[rowIndex].rowProperties;
  if (!map) {
    map = new jsfc.Map;
    this.properties.rows[rowIndex].rowProperties = map;
  }
  map.put(propertyKey, value);
};
jsfc.KeyedValues2DDataset.prototype.clearRowProperties = function(rowKey) {
  var rowIndex = this.rowIndex(rowKey);
  this.properties.rows[rowIndex].rowProperties = null;
};
jsfc.KeyedValues2DDataset.prototype.getColumnPropertyKeys = function(columnKey) {
  var index = this.columnIndex(columnKey);
  var map = this.properties.columns[index];
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
jsfc.KeyedValues2DDataset.prototype.getColumnProperty = function(columnKey, propertyKey) {
  var index = this.columnIndex(columnKey);
  var map = this.properties.columns[index];
  if (map) {
    return map.get(propertyKey);
  } else {
    return undefined;
  }
};
jsfc.KeyedValues2DDataset.prototype.setColumnProperty = function(columnKey, propertyKey, value) {
  var index = this.columnIndex(columnKey);
  var map = this.properties.columns[index];
  if (!map) {
    map = new jsfc.Map;
    this.properties.columns[index] = map;
  }
  map.put(propertyKey, value);
};
jsfc.KeyedValues2DDataset.prototype.clearColumnProperties = function(columnKey) {
  var index = this.columnIndex(columnKey);
  this.properties.columns[index] = null;
};
jsfc.KeyedValues2DDataset.prototype.getItemProperty = function(rowKey, columnKey, propertyKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties.rows[rowIndex][columnIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
jsfc.KeyedValues2DDataset.prototype.setItemProperty = function(rowKey, columnKey, propertyKey, value) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties.rows[rowIndex][columnIndex];
  if (!map) {
    map = new jsfc.Map;
    this.properties.rows[rowIndex][columnIndex] = map;
  }
  map.put(propertyKey, value);
};
jsfc.KeyedValues2DDataset.prototype.getItemPropertyKeys = function(rowKey, columnKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties.rows[rowIndex][columnIndex];
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
jsfc.KeyedValues2DDataset.prototype.clearItemProperties = function(rowKey, columnKey) {
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  this.properties.rows[rowIndex][columnIndex] = null;
};
jsfc.KeyedValues2DDataset.prototype.select = function(selectionId, rowKey, columnKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    selection = {"id":selectionId, "items":[]};
    this.selections.push(selection);
  } else {
    selection = this.selections[selectionIndex];
  }
  var i = jsfc.Utils.findInArray(selection.items, function(item) {
    return item.rowKey === rowKey && item.columnKey === columnKey;
  });
  if (i < 0) {
    selection.items.push({"rowKey":rowKey, "columnKey":columnKey});
  }
  return this;
};
jsfc.KeyedValues2DDataset.prototype.unselect = function(selectionId, rowKey, columnKey) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    var selection = this.selections[selectionIndex];
    var i = jsfc.Utils.findInArray(selection.items, function(obj, i) {
      return obj.rowKey === rowKey && obj.columnKey === columnKey;
    });
    if (i >= 0) {
      selection.items.splice(i, 1);
    }
  }
  return this;
};
jsfc.KeyedValues2DDataset.prototype.isSelected = function(selectionId, rowKey, columnKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
  }
  return jsfc.Utils.findInArray(selection.items, function(obj) {
    return obj.rowKey === rowKey && obj.columnKey === columnKey;
  }) >= 0;
};
jsfc.KeyedValues2DDataset.prototype.clearSelection = function(selectionId) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
  return this;
};
jsfc.KeyedValues2DDataset.prototype._indexOfSelection = function(selectionId) {
  return jsfc.Utils.findInArray(this.selections, function(item) {
    return item.id === selectionId;
  });
};
jsfc.KeyedValues2DDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
jsfc.KeyedValues2DDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
jsfc.KeyedValues2DDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
jsfc.KeyedValues3DDataset = function() {
  if (!(this instanceof jsfc.KeyedValues3DDataset)) {
    return new jsfc.KeyedValues3DDataset;
  }
  this.data = {"columnKeys":[], "rowKeys":[], "series":[]};
  this.properties = [];
  this._listeners = [];
};
jsfc.KeyedValues3DDataset.prototype.isEmpty = function() {
  return this.data.columnKeys.length === 0 && this.data.rowKeys.length === 0;
};
jsfc.KeyedValues3DDataset.prototype.seriesCount = function() {
  return this.data.series.length;
};
jsfc.KeyedValues3DDataset.prototype.rowCount = function() {
  return this.data.rowKeys.length;
};
jsfc.KeyedValues3DDataset.prototype.columnCount = function() {
  return this.data.columnKeys.length;
};
jsfc.KeyedValues3DDataset.prototype._fetchRow = function(seriesIndex, rowKey) {
  var rows = this.data.series[seriesIndex].rows;
  for (var r = 0;r < rows.length;r++) {
    if (rows[r].rowKey === rowKey) {
      return rows[r];
    }
  }
  return null;
};
jsfc.KeyedValues3DDataset.prototype.valueByIndex = function(seriesIndex, rowIndex, columnIndex) {
  var rowKey = this.rowKey(rowIndex);
  var row = this._fetchRow(seriesIndex, rowKey);
  if (row === null) {
    return null;
  } else {
    return row.values[columnIndex];
  }
};
jsfc.KeyedValues3DDataset.prototype.seriesIndex = function(seriesKey) {
  var seriesCount = this.seriesCount();
  for (var s = 0;s < seriesCount;s++) {
    if (this.data.series[s].seriesKey === seriesKey) {
      return s;
    }
  }
  return-1;
};
jsfc.KeyedValues3DDataset.prototype.seriesKey = function(seriesIndex) {
  return this.data.series[seriesIndex].seriesKey;
};
jsfc.KeyedValues3DDataset.prototype.rowKey = function(rowIndex) {
  return this.data.rowKeys[rowIndex];
};
jsfc.KeyedValues3DDataset.prototype.rowIndex = function(rowKey) {
  var rowCount = this.data.rowKeys.length;
  for (var r = 0;r < rowCount;r++) {
    if (this.data.rowKeys[r] === rowKey) {
      return r;
    }
  }
  return-1;
};
jsfc.KeyedValues3DDataset.prototype.rowKeys = function() {
  return this.data.rowKeys.map(function(d) {
    return d;
  });
};
jsfc.KeyedValues3DDataset.prototype.columnKey = function(columnIndex) {
  return this.data.columnKeys[columnIndex];
};
jsfc.KeyedValues3DDataset.prototype.columnIndex = function(columnKey) {
  var columnCount = this.data.columnKeys.length;
  for (var c = 0;c < columnCount;c++) {
    if (this.data.columnKeys[c] === columnKey) {
      return c;
    }
  }
  return-1;
};
jsfc.KeyedValues3DDataset.prototype.columnKeys = function() {
  return this.data.columnKeys.map(function(d) {
    return d;
  });
};
jsfc.KeyedValues3DDataset.prototype.valueByKey = function(seriesKey, rowKey, columnKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var row = this._fetchRow(seriesIndex, rowKey);
  if (row === null) {
    return null;
  } else {
    var columnIndex = this.columnIndex(columnKey);
    return row.values[columnIndex];
  }
};
jsfc.KeyedValues3DDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
};
jsfc.KeyedValues3DDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
};
jsfc.KeyedValues3DDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i].datasetChanged(this);
  }
  return this;
};
jsfc.KeyedValues3DDataset.prototype.add = function(seriesKey, rowKey, columnKey, value) {
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
      var rowData = jsfc.Utils.makeArrayOf(null, this.columnCount());
      rowData[columnIndex] = value;
      this.data.series[seriesIndex].rows.push({"rowKey":rowKey, "values":rowData});
      var rowMaps = jsfc.Utils.makeArrayOf(null, this.columnCount());
      this.properties[seriesIndex].rows.push({"rowKey":rowKey, "maps":rowMaps});
    } else {
      var row = this._fetchRow(seriesIndex, rowKey);
      if (row !== null) {
        row.values[columnIndex] = value;
      } else {
        var rowData = jsfc.Utils.makeArrayOf(null, this.columnCount());
        rowData[columnIndex] = value;
        this.data.series[seriesIndex].rows.push({"rowKey":rowKey, "values":rowData});
      }
      var propRow = this._fetchPropertyRow(seriesIndex, rowKey);
      if (propRow === null) {
        var rowMaps = jsfc.Utils.makeArrayOf(null, this.columnCount());
        this.properties[seriesIndex].rows.push({"rowKey":rowKey, "maps":rowMaps});
      }
    }
  }
  return this;
};
jsfc.KeyedValues3DDataset.prototype.parse = function(jsonStr) {
  this.load(JSON.parse(jsonStr));
  return this;
};
jsfc.KeyedValues3DDataset.prototype.load = function(dataObj) {
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
jsfc.KeyedValues3DDataset.prototype.getProperty = function(seriesKey, rowKey, columnKey, propertyKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[seriesIndex].rows[rowIndex][columnIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
jsfc.KeyedValues3DDataset.prototype.setProperty = function(seriesKey, rowKey, columnKey, propertyKey, value) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var rowIndex = this.rowIndex(rowKey);
  var columnIndex = this.columnIndex(columnKey);
  var map = this.properties[seriesIndex].rows[rowIndex][columnIndex];
  if (!map) {
    map = new jsfc.Map;
    this.properties[seriesIndex].rows[rowIndex][columnIndex] = map;
  }
  map.put(propertyKey, value);
};
jsfc.KeyedValues3DDataset.prototype.propertyKeys = function(seriesKey, rowKey, columnKey) {
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
jsfc.KeyedValues3DDataset.prototype.clearProperties = function(seriesKey, rowKey, columnKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var row = this._fetchPropertyRow(seriesIndex, rowKey);
  if (row) {
    var columnIndex = this.columnIndex(columnKey);
    row[columnIndex] = null;
  }
};
jsfc.KeyedValues3DDataset.prototype.clearAllProperties = function() {
  this.properties = [];
  var me = this;
  this.data.series.forEach(function(series) {
    var s = {"seriesKey":series.seriesKey, "rows":[]};
    me.properties.push(s);
    series.rows.forEach(function(row) {
      var maps = jsfc.Utils.makeArrayOf(null, me.columnCount());
      var r = {"rowKey":row.rowKey, "maps":maps};
      s.rows.push(r);
    });
  });
  return this;
};
jsfc.KeyedValues3DDataset.prototype._fetchPropertyRow = function(seriesIndex, rowKey) {
  var rows = this.properties[seriesIndex].rows;
  for (var r = 0;r < rows.length;r++) {
    if (rows[r].rowKey === rowKey) {
      return rows[r];
    }
  }
  return null;
};
jsfc.Map = function() {
  this.data = [];
};
jsfc.Map.prototype.keys = function() {
  return this.data.map(function(d) {
    return d.key;
  });
};
jsfc.Map.prototype._indexOf = function(key) {
  for (var i = 0;i < this.data.length;i++) {
    if (this.data[i].key === key) {
      return i;
    }
  }
  return-1;
};
jsfc.Map.prototype.put = function(key, value) {
  var i = this._indexOf(key);
  if (i < 0) {
    this.data.push({"key":key, "value":value});
  } else {
    this.data[i].value = value;
  }
};
jsfc.Map.prototype.get = function(key) {
  var i = this._indexOf(key);
  if (i >= 0) {
    return this.data[i].value;
  } else {
    return undefined;
  }
};
jsfc.Map.prototype.remove = function(key) {
  var i = this._indexOf(key);
  if (i >= 0) {
    return this.data.splice(i, 1);
  }
};
jsfc.Range = function(lowerBound, upperBound) {
  this._lowerBound = lowerBound;
  this._upperBound = upperBound;
};
jsfc.Range.prototype.length = function() {
  return this._upperBound - this._lowerBound;
};
jsfc.Range.prototype.percent = function(value) {
  return(value - this._lowerBound) / this.length();
};
jsfc.Range.prototype.value = function(percent) {
  return this._lowerBound + percent * this.length();
};
jsfc.Range.prototype.contains = function(n) {
  return n >= this._lowerBound && n <= this._upperBound;
};
jsfc.Range.prototype.toString = function() {
  return "[Range: " + this._lowerBound + ", " + this._upperBound + "]";
};
jsfc.StandardXYDataset = function() {
  this.data = {"series":[]};
  this.properties = {"dataset":null, "series":[]};
  this.selections = [];
  this._listeners = [];
};
jsfc.StandardXYDataset.prototype.seriesCount = function() {
  return this.data.series.length;
};
jsfc.StandardXYDataset.prototype.seriesKeys = function() {
  return this.data.series.map(function(d) {
    return d.seriesKey;
  });
};
jsfc.StandardXYDataset.prototype.seriesKey = function(seriesIndex) {
  return this.data.series[seriesIndex].seriesKey;
};
jsfc.StandardXYDataset.prototype.seriesIndex = function(seriesKey) {
  jsfc.Args.requireString(seriesKey, "seriesKey");
  var seriesArray = this.data.series;
  var seriesCount = this.data.series.length;
  for (var s = 0;s < seriesCount;s++) {
    if (seriesArray[s].seriesKey === seriesKey) {
      return s;
    }
  }
  return-1;
};
jsfc.StandardXYDataset.prototype.itemCount = function(seriesIndex) {
  return this.data.series[seriesIndex].items.length;
};
jsfc.StandardXYDataset.prototype.itemIndex = function(seriesKey, itemKey) {
  jsfc.Args.require(itemKey, "itemKey");
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return i;
    }
  }
  return-1;
};
jsfc.StandardXYDataset.prototype.x = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].x;
};
jsfc.StandardXYDataset.prototype.y = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].y;
};
jsfc.StandardXYDataset.prototype.item = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex];
};
jsfc.StandardXYDataset.prototype.itemByKey = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return items[i];
    }
  }
  return null;
};
jsfc.StandardXYDataset.prototype.getItemKey = function(seriesIndex, itemIndex) {
  return this.item(seriesIndex, itemIndex).key;
};
jsfc.StandardXYDataset.prototype.generateItemKey = function(seriesIndex) {
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
jsfc.StandardXYDataset.prototype.items = function(seriesIndex) {
  return this.data.series[seriesIndex].items;
};
jsfc.StandardXYDataset.prototype.allItems = function() {
  var result = [];
  for (var s = 0;s < this.data.series.length;s++) {
    result.push(this.items(s));
  }
  return result;
};
jsfc.StandardXYDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
jsfc.StandardXYDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
jsfc.StandardXYDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i](this);
  }
  return this;
};
jsfc.StandardXYDataset.prototype.add = function(seriesKey, x, y, notify) {
  jsfc.Args.requireNumber(x, "x");
  var itemKey = this.generateItemKey(this.seriesIndex(seriesKey));
  return this.addByKey(seriesKey, itemKey, x, y, notify);
};
jsfc.StandardXYDataset.prototype.addByKey = function(seriesKey, itemKey, x, y, notify) {
  jsfc.Args.requireString(seriesKey, "seriesKey");
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
    this.properties.series[s].maps.push(null);
  }
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.StandardXYDataset.prototype.remove = function(seriesIndex, itemIndex, notify) {
  this.data.series[seriesIndex].items.splice(itemIndex, 1);
  this.properties.series[seriesIndex].maps.splice(itemIndex, 1);
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.StandardXYDataset.prototype.removeByKey = function(seriesKey, itemKey, notify) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  this.remove(seriesIndex, itemIndex, notify);
};
jsfc.StandardXYDataset.prototype.addSeries = function(seriesKey) {
  if (!(typeof seriesKey === "string")) {
    throw new Error("The 'seriesKey' must be a string.");
  }
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    throw new Error("There is already a series with the key '" + seriesKey);
  }
  this.data.series.push({"seriesKey":seriesKey, "items":[]});
  this.properties.series.push({"seriesKey":seriesKey, "seriesProperties":null, "maps":[]});
  return this;
};
jsfc.StandardXYDataset.prototype.removeSeries = function(seriesKey) {
  if (!(typeof seriesKey === "string")) {
    throw new Error("The 'seriesKey' must be a string.");
  }
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    this.data.series.splice(s, 1);
    this.properties.series.splice(s, 1);
  }
  return this;
};
jsfc.StandardXYDataset.prototype.bounds = function() {
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
jsfc.StandardXYDataset.prototype.xbounds = function() {
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
jsfc.StandardXYDataset.prototype.ybounds = function() {
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
jsfc.StandardXYDataset.prototype.getProperty = function(key) {
  var map = this.properties.dataset;
  if (map) {
    return map.get(key);
  }
  return undefined;
};
jsfc.StandardXYDataset.prototype.setProperty = function(key, value, notify) {
  if (!this.properties.dataset) {
    this.properties.dataset = new jsfc.Map;
  }
  this.properties.dataset.put(key, value);
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.StandardXYDataset.prototype.getPropertyKeys = function() {
  if (this.properties.dataset) {
    return this.properties.dataset.keys();
  }
  return[];
};
jsfc.StandardXYDataset.prototype.clearProperties = function(notify) {
  this.properties.dataset = null;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.StandardXYDataset.prototype.getSeriesPropertyKeys = function(seriesKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var map = this.properties.series[seriesIndex].seriesProperties;
  if (map) {
    return map.keys();
  } else {
    return[];
  }
};
jsfc.StandardXYDataset.prototype.getSeriesProperty = function(seriesKey, propertyKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var map = this.properties.series[seriesIndex].seriesProperties;
  if (map) {
    return map.get(propertyKey);
  } else {
    return undefined;
  }
};
jsfc.StandardXYDataset.prototype.setSeriesProperty = function(seriesKey, propertyKey, value) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var map = this.properties.series[seriesIndex].seriesProperties;
  if (!map) {
    map = new jsfc.Map;
    this.properties.series[seriesIndex].seriesProperties = map;
  }
  map.put(propertyKey, value);
};
jsfc.StandardXYDataset.prototype.clearSeriesProperties = function(seriesKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  this.properties.series[seriesIndex].seriesProperties = null;
};
jsfc.StandardXYDataset.prototype.getItemProperty = function(seriesKey, itemKey, propertyKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties.series[seriesIndex].maps[itemIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
jsfc.StandardXYDataset.prototype.setItemProperty = function(seriesKey, itemKey, propertyKey, value) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties.series[seriesIndex].maps[itemIndex];
  if (!map) {
    map = new jsfc.Map;
    this.properties.series[seriesIndex].maps[itemIndex] = map;
  }
  map.put(propertyKey, value);
};
jsfc.StandardXYDataset.prototype.clearItemProperties = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  this.properties.series[seriesIndex].maps[itemIndex] = null;
};
jsfc.StandardXYDataset.prototype.select = function(selectionId, seriesKey, itemKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    selection = {"id":selectionId, "items":[]};
    this.selections.push(selection);
  } else {
    selection = this.selections[selectionIndex];
  }
  var i = jsfc.Utils.findInArray(selection.items, function(item) {
    return item.seriesKey === seriesKey && item.itemKey === itemKey;
  });
  if (i < 0) {
    selection.items.push({"seriesKey":seriesKey, "itemKey":itemKey});
  }
  return this;
};
jsfc.StandardXYDataset.prototype.unselect = function(selectionId, seriesKey, itemKey) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    var selection = this.selections[selectionIndex];
    var i = jsfc.Utils.findInArray(selection.items, function(obj, i) {
      return obj.seriesKey === seriesKey && obj.itemKey === itemKey;
    });
    if (i >= 0) {
      selection.items.splice(i, 1);
    }
  }
  return this;
};
jsfc.StandardXYDataset.prototype.isSelected = function(selectionId, seriesKey, itemKey) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
  }
  return jsfc.Utils.findInArray(selection.items, function(obj) {
    return obj.seriesKey === seriesKey && obj.itemKey === itemKey;
  }) >= 0;
};
jsfc.StandardXYDataset.prototype.clearSelection = function(selectionId) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
  return this;
};
jsfc.StandardXYDataset.prototype._indexOfSelection = function(selectionId) {
  return jsfc.Utils.findInArray(this.selections, function(item) {
    return item.id === selectionId;
  });
};
jsfc.XYDataset = function() {
  throw new Error("Interface only.");
};
jsfc.XYDataset.prototype.getProperty = function(key) {
};
jsfc.XYDataset.prototype.setProperty = function(key, value, notify) {
};
jsfc.XYDataset.prototype.getPropertyKeys = function() {
};
jsfc.XYDataset.prototype.clearProperties = function(notify) {
};
jsfc.XYDataset.prototype.seriesCount = function() {
};
jsfc.XYDataset.prototype.seriesKeys = function() {
};
jsfc.XYDataset.prototype.seriesKey = function(seriesIndex) {
};
jsfc.XYDataset.prototype.seriesIndex = function(seriesKey) {
};
jsfc.XYDataset.prototype.itemCount = function(seriesIndex) {
};
jsfc.XYDataset.prototype.itemIndex = function(seriesKey, itemKey) {
};
jsfc.XYDataset.prototype.x = function(seriesIndex, itemIndex) {
};
jsfc.XYDataset.prototype.y = function(seriesIndex, itemIndex) {
};
jsfc.XYDataset.prototype.item = function(seriesIndex, itemIndex) {
};
jsfc.XYDataset.prototype.addListener = function(listener) {
};
jsfc.XYDataset.prototype.removeListener = function(listener) {
};
jsfc.XYDataset.prototype.bounds = function() {
};
jsfc.XYDataset.prototype.xbounds = function() {
};
jsfc.XYDataset.prototype.ybounds = function() {
};
jsfc.XYDataset.prototype.getItemProperty = function(seriesKey, itemKey, propertyKey) {
};
jsfc.XYDataset.prototype.select = function(selectionId, seriesKey, itemKey) {
};
jsfc.XYDataset.prototype.unselect = function(selectionId, seriesKey, itemKey) {
};
jsfc.XYDataset.prototype.isSelected = function(selectionId, seriesKey, itemKey) {
};
jsfc.XYDataset.prototype.clearSelection = function(selectionId) {
};
jsfc.XYZDataset = function() {
  this.data = {"series":[]};
  this.properties = [];
  this.selections = [];
  this._listeners = [];
};
jsfc.XYZDataset.prototype.seriesCount = function() {
  return this.data.series.length;
};
jsfc.XYZDataset.prototype.seriesKeys = function() {
  return this.data.series.map(function(d) {
    return d.seriesKey;
  });
};
jsfc.XYZDataset.prototype.seriesKey = function(seriesIndex) {
  return this.data.series[seriesIndex].seriesKey;
};
jsfc.XYZDataset.prototype.seriesIndex = function(seriesKey) {
  jsfc.Args.requireString(seriesKey, "seriesKey");
  var seriesArray = this.data.series;
  var seriesCount = this.data.series.length;
  for (var s = 0;s < seriesCount;s++) {
    if (seriesArray[s].seriesKey === seriesKey) {
      return s;
    }
  }
  return-1;
};
jsfc.XYZDataset.prototype.itemCount = function(seriesIndex) {
  return this.data.series[seriesIndex].items.length;
};
jsfc.XYZDataset.prototype.itemIndex = function(seriesKey, itemKey) {
  jsfc.Args.require(itemKey, "itemKey");
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return i;
    }
  }
  return-1;
};
jsfc.XYZDataset.prototype.x = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].x;
};
jsfc.XYZDataset.prototype.y = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].y;
};
jsfc.XYZDataset.prototype.z = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex].z;
};
jsfc.XYZDataset.prototype.item = function(seriesIndex, itemIndex) {
  return this.data.series[seriesIndex].items[itemIndex];
};
jsfc.XYZDataset.prototype.itemByKey = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var items = this.data.series[seriesIndex].items;
  for (var i = 0;i < items.length;i++) {
    if (items[i].key === itemKey) {
      return items[i];
    }
  }
  return null;
};
jsfc.XYZDataset.prototype.getItemKey = function(seriesIndex, itemIndex) {
  return this.item(seriesIndex, itemIndex).key;
};
jsfc.XYZDataset.prototype.generateItemKey = function(seriesIndex) {
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
jsfc.XYZDataset.prototype.add = function(seriesKey, x, y, z, notify) {
  jsfc.Args.requireNumber(x, "x");
  var itemKey = this.generateItemKey(this.seriesIndex(seriesKey));
  return this.addByKey(seriesKey, itemKey, x, y, z, notify);
};
jsfc.XYZDataset.prototype.addByKey = function(seriesKey, itemKey, x, y, z, notify) {
  jsfc.Args.requireString(seriesKey, "seriesKey");
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
jsfc.XYZDataset.prototype.addSeries = function(seriesKey) {
  jsfc.Args.requireString(seriesKey, "seriesKey");
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    throw new Error("There is already a series with the key '" + seriesKey);
  }
  this.data.series.push({"seriesKey":seriesKey, "items":[]});
  this.properties.push({"seriesKey":seriesKey, "maps":[]});
  return this;
};
jsfc.XYZDataset.prototype.removeSeries = function(seriesKey) {
  jsfc.Args.requireString(seriesKey, "seriesKey");
  var s = this.seriesIndex(seriesKey);
  if (s >= 0) {
    this.data.series.splice(s, 1);
  }
  return this;
};
jsfc.XYZDataset.prototype.addListener = function(listener) {
  this._listeners.push(listener);
  return this;
};
jsfc.XYZDataset.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
jsfc.XYZDataset.prototype.notifyListeners = function() {
  for (var i = 0;i < this._listeners.length;i++) {
    this._listeners[i](this);
  }
  return this;
};
jsfc.XYZDataset.prototype.getProperty = function(seriesKey, itemKey, propertyKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties[seriesIndex].maps[itemIndex];
  if (map) {
    return map.get(propertyKey);
  }
};
jsfc.XYZDataset.prototype.setProperty = function(seriesKey, itemKey, propertyKey, value) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  var map = this.properties[seriesIndex][itemIndex];
  if (!map) {
    map = new jsfc.Map;
    this.properties[seriesIndex].maps[itemIndex] = map;
  }
  map.put(propertyKey, value);
};
jsfc.XYZDataset.prototype.clearProperties = function(seriesKey, itemKey) {
  var seriesIndex = this.seriesIndex(seriesKey);
  var itemIndex = this.itemIndex(seriesKey, itemKey);
  this.properties[seriesIndex].maps[itemIndex] = null;
};
jsfc.XYZDataset.prototype.select = function(selectionId, seriesKey, itemIndex) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    selection = {"id":selectionId, "items":[]};
    this.selections.push(selection);
  } else {
    selection = this.selections[selectionIndex];
  }
  var i = jsfc.Utils.findInArray(selection.items, function(item) {
    return item.seriesKey === seriesKey && item.item === itemIndex;
  });
  if (i < 0) {
    selection.items.push({"seriesKey":seriesKey, "item":itemIndex});
  }
  return this;
};
jsfc.XYZDataset.prototype.unselect = function(selectionId, seriesKey, itemIndex) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    var selection = this.selections[selectionIndex];
    var i = jsfc.Utils.findInArray(selection.items, function(obj, i) {
      return obj.seriesKey === seriesKey && obj.item === itemIndex;
    });
    if (i >= 0) {
      selection.items.splice(i, 1);
    }
  }
  return this;
};
jsfc.XYZDataset.prototype.isSelected = function(selectionId, seriesKey, itemIndex) {
  var selection;
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex < 0) {
    return false;
  } else {
    selection = this.selections[selectionIndex];
  }
  return jsfc.Utils.findInArray(selection.items, function(obj) {
    return obj.seriesKey === seriesKey && obj.item === itemIndex;
  }) >= 0;
};
jsfc.XYZDataset.prototype.clearSelection = function(selectionId) {
  var selectionIndex = this._indexOfSelection(selectionId);
  if (selectionIndex >= 0) {
    this.selections.splice(selectionIndex, 1);
  }
};
jsfc.XYZDataset.prototype._indexOfSelection = function(selectionId) {
  return jsfc.Utils.findInArray(this.selections, function(item) {
    return item.id === selectionId;
  });
};
jsfc.AxisSpace = function(top, left, bottom, right) {
  if (!(this instanceof jsfc.AxisSpace)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.Args.requireNumber(top, "top");
  jsfc.Args.requireNumber(left, "left");
  jsfc.Args.requireNumber(bottom, "bottom");
  jsfc.Args.requireNumber(right, "right");
  this._top = top;
  this._left = left;
  this._bottom = bottom;
  this._right = right;
};
jsfc.AxisSpace.prototype.top = function() {
  return this._top;
};
jsfc.AxisSpace.prototype.left = function() {
  return this._left;
};
jsfc.AxisSpace.prototype.bottom = function() {
  return this._bottom;
};
jsfc.AxisSpace.prototype.right = function() {
  return this._right;
};
jsfc.AxisSpace.prototype.extend = function(space, edge) {
  jsfc.Args.requireNumber(space, "space");
  if (edge === jsfc.RectangleEdge.TOP) {
    this._top += space;
  } else {
    if (edge === jsfc.RectangleEdge.BOTTOM) {
      this._bottom += space;
    } else {
      if (edge === jsfc.RectangleEdge.LEFT) {
        this._left += space;
      } else {
        if (edge === jsfc.RectangleEdge.RIGHT) {
          this._right += space;
        } else {
          throw new Error("Unrecognised 'edge' code: " + edge);
        }
      }
    }
  }
};
jsfc.AxisSpace.prototype.innerRect = function(source) {
  var x = source.x() + this._left;
  var y = source.y() + this._top;
  var w = source.width() - this._left - this._right;
  var h = source.height() - this._top - this._bottom;
  return new jsfc.Rectangle(x, y, w, h);
};
jsfc.LabelOrientation = {PERPENDICULAR:"PERPENDICULAR", PARALLEL:"PARALLEL"};
if (Object.freeze) {
  Object.freeze(jsfc.LabelOrientation);
}
;jsfc.TickMark = function(value, label) {
  if (!(this instanceof jsfc.TickMark)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.value = value;
  this.label = label;
};
jsfc.TickMark.prototype.toString = function() {
  return this.label;
};
jsfc.ValueAxis = function() {
  throw new Error("This object documents an interface.");
};
jsfc.ValueAxis.prototype.setLabel = function(label, notify) {
};
jsfc.ValueAxis.prototype.configureAsXAxis = function(plot) {
};
jsfc.ValueAxis.prototype.configureAsYAxis = function(plot) {
};
jsfc.ValueAxis.prototype.valueToCoordinate = function(value, r0, r1) {
};
jsfc.ValueAxis.prototype.coordinateToValue = function(coordinate, r0, r1) {
};
jsfc.ValueAxis.prototype.resizeRange = function(factor, anchorValue, notify) {
};
jsfc.ValueAxis.prototype.pan = function(percent, notify) {
};
jsfc.ValueAxis.prototype.reserveSpace = function(ctx, plot, bounds, area, edge) {
};
jsfc.ValueAxis.prototype.draw = function(ctx, plot, bounds, dataArea, offset) {
};
jsfc.ValueAxis.prototype.addListener = function(listener) {
};
jsfc.NumberTickSelector = function(percentage) {
  if (!(this instanceof jsfc.NumberTickSelector)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._power = 0;
  this._factor = 1;
  this._percentage = percentage;
  this._f0 = new jsfc.NumberFormat(0);
  this._f1 = new jsfc.NumberFormat(1);
  this._f2 = new jsfc.NumberFormat(2);
  this._f3 = new jsfc.NumberFormat(3);
  this._f4 = new jsfc.NumberFormat(4);
};
jsfc.NumberTickSelector.prototype.select = function(reference) {
  this._power = Math.ceil(Math.LOG10E * Math.log(reference));
  this._factor = 1;
  return this.currentTickSize();
};
jsfc.NumberTickSelector.prototype.currentTickSize = function() {
  return this._factor * Math.pow(10, this._power);
};
jsfc.NumberTickSelector.prototype.currentTickFormat = function() {
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
    return new jsfc.NumberFormat(Number.POSITIVE_INFINITY);
  }
  if (this._power > 6) {
    return new jsfc.NumberFormat(1, true);
  }
  return this._f0;
};
jsfc.NumberTickSelector.prototype.next = function() {
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
jsfc.NumberTickSelector.prototype.previous = function() {
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
jsfc.BaseValueAxis = function(label, instance) {
  if (!(this instanceof jsfc.BaseValueAxis)) {
    throw new Error("Use 'new' for construction.");
  }
  if (!instance) {
    instance = this;
  }
  if (!label) {
    label = null;
  }
  jsfc.BaseValueAxis.init(label, instance);
};
jsfc.BaseValueAxis.init = function(label, instance) {
  instance._label = label;
  instance._listeners = [];
  instance._labelFont = new jsfc.Font("Palatino;serif", 12, true, false);
  instance._labelColor = new jsfc.Color(0, 0, 0);
  instance._labelMargin = new jsfc.Insets(2, 2, 2, 2);
  instance._tickLabelFont = new jsfc.Font("Palatino;serif", 12);
  instance._tickLabelColor = new jsfc.Color(0, 0, 0);
  instance._axisLineColor = new jsfc.Color(100, 100, 100);
  instance._axisLineStroke = new jsfc.Stroke(0.5);
  instance._gridLinesVisible = true;
  instance._gridLineStroke = new jsfc.Stroke(1);
  instance._gridLineColor = new jsfc.Color(255, 255, 255);
};
jsfc.BaseValueAxis.prototype.getLabel = function() {
  return this._label;
};
jsfc.BaseValueAxis.prototype.setLabel = function(label, notify) {
  this._label = label;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getLabelFont = function() {
  return this._labelFont;
};
jsfc.BaseValueAxis.prototype.setLabelFont = function(font, notify) {
  this._labelFont = font;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getLabelColor = function() {
  return this._labelColor;
};
jsfc.BaseValueAxis.prototype.setLabelColor = function(color, notify) {
  this._labelColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getLabelMargin = function() {
  return this._labelMargin;
};
jsfc.BaseValueAxis.prototype.setLabelMargin = function(margin, notify) {
  this._labelMargin = margin;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getTickLabelFont = function() {
  return this._tickLabelFont;
};
jsfc.BaseValueAxis.prototype.setTickLabelFont = function(font, notify) {
  this._tickLabelFont = font;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getTickLabelColor = function() {
  return this._tickLabelColor;
};
jsfc.BaseValueAxis.prototype.setTickLabelColor = function(color, notify) {
  this._tickLabelColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getAxisLineColor = function() {
  return this._axisLineColor;
};
jsfc.BaseValueAxis.prototype.setAxisLineColor = function(color, notify) {
  this._axisLineColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getAxisLineStroke = function() {
  return this._axisLineStroke;
};
jsfc.BaseValueAxis.prototype.setAxisLineStroke = function(stroke, notify) {
  this._axisLineStroke = stroke;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.addListener = function(listener) {
  this._listeners.push(listener);
};
jsfc.BaseValueAxis.prototype.removeListener = function(listener) {
  var i = this._listeners.indexOf(listener);
  if (i >= 0) {
    this._listeners.splice(i, 1);
  }
  return this;
};
jsfc.BaseValueAxis.prototype.notifyListeners = function() {
  var axis = this;
  this._listeners.forEach(function(listener) {
    listener(axis);
  });
};
jsfc.BaseValueAxis.prototype.isGridLinesVisible = function() {
  return this._gridLinesVisible;
};
jsfc.BaseValueAxis.prototype.setGridLinesVisible = function(visible, notify) {
  this._gridLinesVisible = visible !== false;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getGridLineStroke = function() {
  return this._gridLineStroke;
};
jsfc.BaseValueAxis.prototype.setGridLineStroke = function(stroke, notify) {
  this._gridLineStroke = stroke;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseValueAxis.prototype.getGridLineColor = function() {
  return this._gridLineColor;
};
jsfc.BaseValueAxis.prototype.setGridLineColor = function(color, notify) {
  this._gridLineColor = color;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.LinearAxis = function(label, instance) {
  if (!(this instanceof jsfc.LinearAxis)) {
    throw new Error("Use 'new' with constructor.");
  }
  if (!instance) {
    instance = this;
  }
  jsfc.LinearAxis.init(label, instance);
};
jsfc.LinearAxis.init = function(label, instance) {
  jsfc.BaseValueAxis.init(label, instance);
  instance._lowerBound = 0;
  instance._upperBound = 1;
  instance._autoRange = true;
  instance._autoRangeIncludesZero = false;
  instance._lowerMargin = 0.05;
  instance._upperMargin = 0.05;
  instance._defaultRange = new jsfc.Range(0, 1);
  instance._tickSelector = new jsfc.NumberTickSelector(false);
  instance._formatter = new jsfc.NumberFormat(2);
  instance._tickMarkInnerLength = 0;
  instance._tickMarkOuterLength = 2;
  instance._tickMarkStroke = new jsfc.Stroke(0.5);
  instance._tickMarkColor = new jsfc.Color(100, 100, 100);
  instance._tickLabelMargin = new jsfc.Insets(2, 2, 2, 2);
  instance._tickLabelFactor = 1.4;
  instance._tickLabelOrientation = null;
  instance._tickLabelFormatOverride = null;
  this._symbols = [];
};
jsfc.LinearAxis.prototype = new jsfc.BaseValueAxis;
jsfc.LinearAxis.prototype.getLowerBound = function() {
  return this._lowerBound;
};
jsfc.LinearAxis.prototype.getUpperBound = function() {
  return this._upperBound;
};
jsfc.LinearAxis.prototype.setBounds = function(lower, upper, notify) {
  this._lowerBound = lower;
  this._upperBound = upper;
  this._autoRange = false;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LinearAxis.prototype.setBoundsByPercent = function(lower, upper, notify) {
  var v0 = this._lowerBound;
  var v1 = this._upperBound;
  var len = v1 - v0;
  this._lowerBound = v0 + lower * len;
  this._upperBound = v0 + upper * len;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LinearAxis.prototype.isAutoRange = function() {
  return this._autoRange;
};
jsfc.LinearAxis.prototype.setAutoRange = function(auto, notify) {
  this._autoRange = auto;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LinearAxis.prototype.getAutoRangeIncludesZero = function() {
  return this._autoRangeIncludesZero;
};
jsfc.LinearAxis.prototype.setAutoRangeIncludesZero = function(include, notify) {
  this._autoRangeIncludesZero = include;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LinearAxis.prototype.getLowerMargin = function() {
  return this._lowerMargin;
};
jsfc.LinearAxis.prototype.setLowerMargin = function(margin, notify) {
  this._lowerMargin = margin;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LinearAxis.prototype.getUpperMargin = function() {
  return this._upperMargin;
};
jsfc.LinearAxis.prototype.setUpperMargin = function(margin, notify) {
  this._upperMargin = margin;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LinearAxis.prototype.getTickLabelFormatOverride = function() {
  return this._tickLabelFormatOverride;
};
jsfc.LinearAxis.prototype.setTickLabelFormatOverride = function(formatter, notify) {
  this._tickLabelFormatOverride = formatter;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LinearAxis.prototype._applyAutoRange = function(min, max) {
  var xrange = max - min;
  var lowAdj, highAdj;
  if (xrange > 0) {
    lowAdj = this._lowerMargin * xrange;
    highAdj = this._upperMargin * xrange;
  } else {
    lowAdj = 0.5 * this._defaultRange.length();
    highAdj = 0.5 * this._defaultRange.length();
  }
  this.setBounds(min - lowAdj, max + highAdj, false);
};
jsfc.LinearAxis.prototype.valueToCoordinate = function(value, r0, r1) {
  jsfc.Args.requireNumber(r0, "r0");
  jsfc.Args.requireNumber(r1, "r1");
  var a = this._lowerBound;
  var b = this._upperBound;
  return r0 + (value - a) / (b - a) * (r1 - r0);
};
jsfc.LinearAxis.prototype.coordinateToValue = function(coordinate, r0, r1) {
  var a = this._lowerBound;
  var b = this._upperBound;
  return a + (coordinate - r0) / (r1 - r0) * (b - a);
};
jsfc.LinearAxis.prototype._calcTickSize = function(ctx, area, edge) {
  var result = Number.NaN;
  var pixels = area.length(edge);
  var range = this._upperBound - this._lowerBound;
  var orientation = this._resolveTickLabelOrientation(edge);
  var selector = this._tickSelector;
  if (orientation === jsfc.LabelOrientation.PERPENDICULAR) {
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
    if (orientation === jsfc.LabelOrientation.PARALLEL) {
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
jsfc.LinearAxis.prototype._resolveTickLabelOrientation = function(edge) {
  var result = this._tickLabelOrientation;
  if (!result) {
    if (edge === jsfc.RectangleEdge.LEFT || edge === jsfc.RectangleEdge.RIGHT) {
      result = jsfc.LabelOrientation.PERPENDICULAR;
    } else {
      if (edge === jsfc.RectangleEdge.TOP || edge === jsfc.RectangleEdge.BOTTOM) {
        result = jsfc.LabelOrientation.PARALLEL;
      } else {
        throw new Error("Unrecognised 'edge' code: " + edge);
      }
    }
  }
  return result;
};
jsfc.LinearAxis.prototype.reserveSpace = function(ctx, plot, bounds, area, edge) {
  var space = this._tickMarkOuterLength;
  if (this._label) {
    ctx.setFont(this._labelFont);
    var dim = ctx.textDim(this._label);
    var lm = this._labelMargin;
    space += dim.height();
    if (jsfc.RectangleEdge.isTopOrBottom(edge)) {
      space += lm.top() + lm.bottom();
    } else {
      if (jsfc.RectangleEdge.isLeftOrRight(edge)) {
        space += lm.left() + lm.right();
      } else {
        throw new Error("Unrecognised edge code: " + edge);
      }
    }
  }
  var tickSize = this._calcTickSize(ctx, area, edge);
  var ticks = this.ticks(tickSize, ctx, area, edge);
  ctx.setFont(this._tickLabelFont);
  var orientation = this._resolveTickLabelOrientation(edge);
  if (orientation === jsfc.LabelOrientation.PERPENDICULAR) {
    var max = 0;
    ticks.forEach(function(t) {
      max = Math.max(max, ctx.textDim(t.label).width());
    });
    space += max;
  } else {
    if (orientation === jsfc.LabelOrientation.PARALLEL) {
      var dim = ctx.textDim("123");
      space += dim.height();
    }
  }
  if (jsfc.RectangleEdge.isTopOrBottom(edge)) {
    space += this._tickLabelMargin.top() + this._tickLabelMargin.bottom();
  } else {
    if (jsfc.RectangleEdge.isLeftOrRight(edge)) {
      space += this._tickLabelMargin.left() + this._tickLabelMargin.right();
    } else {
      throw new Error("Unrecognised edge code: " + edge);
    }
  }
  return space;
};
jsfc.LinearAxis.prototype._symbolCount = function(range) {
  var c = 0;
  this._symbols.forEach(function(s) {
    if (range.contains(s.value)) {
      c++;
    }
  });
  return c;
};
jsfc.LinearAxis.prototype.ticks = function(tickSize, ctx, area, edge) {
  var r = new jsfc.Range(this._lowerBound, this._upperBound);
  if (this._symbolCount(r) > 0) {
    var result = [];
    var axis = this;
    this._symbols.forEach(function(s) {
      if (s.value > axis._lowerBound && s.value < axis._upperBound) {
        result.push(new jsfc.TickMark(s.value, s.symbol));
      }
    });
    return result;
  }
  var formatter = this._tickLabelFormatOverride || this._formatter;
  if (!isNaN(tickSize)) {
    var result = [];
    var t = Math.ceil(this._lowerBound / tickSize) * tickSize;
    while (t < this._upperBound) {
      var tm = new jsfc.TickMark(t, formatter.format(t));
      result.push(tm);
      t += tickSize;
    }
    return result;
  } else {
    var tm0 = new jsfc.TickMark(this._lowerBound, formatter.format(this._lowerBound));
    var tm1 = new jsfc.TickMark(this._upperBound, formatter.format(this._upperBound));
    return[tm0, tm1];
  }
};
jsfc.LinearAxis.prototype.draw = function(ctx, plot, bounds, dataArea, offset) {
  var edge = plot.axisPosition(this);
  var tickSize = this._calcTickSize(ctx, dataArea, edge);
  var ticks = this.ticks(tickSize, ctx, dataArea, edge);
  var x = dataArea.x();
  var y = dataArea.y();
  var w = dataArea.width();
  var h = dataArea.height();
  var isLeft = edge === jsfc.RectangleEdge.LEFT;
  var isRight = edge === jsfc.RectangleEdge.RIGHT;
  var isTop = edge === jsfc.RectangleEdge.TOP;
  var isBottom = edge === jsfc.RectangleEdge.BOTTOM;
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
        var dim = ctx.drawAlignedString(tick.label, x + w + adj, yy, jsfc.TextAnchor.CENTER_LEFT);
      } else {
        var adj = offset + this._tickMarkOuterLength + this._tickLabelMargin.right();
        var dim = ctx.drawAlignedString(tick.label, x - adj, yy, jsfc.TextAnchor.CENTER_RIGHT);
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
        ctx.drawRotatedString(this._label, x + w + adj, y + h / 2, jsfc.TextAnchor.BOTTOM_CENTER, Math.PI / 2);
      } else {
        var adj = offset + maxTickLabelWidth + this._tickMarkOuterLength + this._tickLabelMargin.left() + this._tickLabelMargin.right() + this._labelMargin.right();
        ctx.drawRotatedString(this._label, x - adj, y + h / 2, jsfc.TextAnchor.BOTTOM_CENTER, -Math.PI / 2);
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
            ctx.drawAlignedString(tick.label, xx, y - gap, jsfc.TextAnchor.BOTTOM_CENTER);
          } else {
            ctx.drawLine(xx, y + h + offset - this._tickMarkInnerLength, xx, y + h + offset + this._tickMarkOuterLength);
            ctx.drawAlignedString(tick.label, xx, y + h + gap, jsfc.TextAnchor.TOP_CENTER);
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
          ctx.drawAlignedString(this._label, x + w / 2, y - gap - this._tickLabelMargin.bottom() - this._labelMargin.top() - this._tickLabelFont.size, jsfc.TextAnchor.BOTTOM_CENTER);
        } else {
          ctx.drawAlignedString(this._label, x + w / 2, y + h + gap + this._tickLabelMargin.bottom() + this._labelMargin.top() + this._tickLabelFont.size, jsfc.TextAnchor.TOP_CENTER);
        }
      }
    }
  }
};
jsfc.LinearAxis.prototype.configureAsXAxis = function(plot) {
  var dataset = plot.getDataset();
  if (this._autoRange && dataset) {
    var bounds = plot.getDataset().xbounds();
    if (bounds[0] <= bounds[1]) {
      this._applyAutoRange(bounds[0], bounds[1]);
    }
  }
  if (dataset) {
    var s = plot.getDataset().getProperty("x-symbols");
    if (s) {
      this._symbols = s.map(function(e) {
        return e;
      });
    } else {
      this._symbols = [];
    }
  }
};
jsfc.LinearAxis.prototype.configureAsYAxis = function(plot) {
  var dataset = plot.getDataset();
  if (this._autoRange && dataset) {
    var bounds = plot.getDataset().ybounds();
    if (bounds[0] <= bounds[1]) {
      this._applyAutoRange(bounds[0], bounds[1]);
    }
  }
  if (dataset) {
    var s = plot.getDataset().getProperty("y-symbols");
    if (s) {
      this._symbols = s.map(function(e) {
        return e;
      });
    } else {
      this._symbols = [];
    }
  }
};
jsfc.LinearAxis.prototype.resizeRange = function(factor, anchorValue, notify) {
  jsfc.Args.requireNumber(factor, "factor");
  if (factor > 0) {
    var left = anchorValue - this._lowerBound;
    var right = this._upperBound - anchorValue;
    this._lowerBound = anchorValue - left * factor;
    this._upperBound = anchorValue + right * factor;
    if (notify !== false) {
      this.notifyListeners();
    }
  } else {
    this.setAutoRange(true);
  }
};
jsfc.LinearAxis.prototype.pan = function(percent, notify) {
  jsfc.Args.requireNumber(percent, "percent");
  var length = this._upperBound - this._lowerBound;
  var adj = percent * length;
  this._lowerBound += adj;
  this._upperBound += adj;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.LogAxis = function(label) {
  if (!(this instanceof jsfc.LogAxis)) {
    throw new Error("Use 'new' with constructor.");
  }
  jsfc.LinearAxis.init(label, this);
  this._base = 10;
  this._baseLog = Math.log(this._base);
  this._smallestValue = 1E-100;
  var lb = Math.log(Math.max(this._lowerBound, this._smallestValue)) / this._baseLog;
  var ub = Math.log(Math.max(this._upperBound, this._smallestValue)) / this._baseLog;
  this._logRange = new jsfc.Range(lb, ub);
};
jsfc.LogAxis.prototype = new jsfc.LinearAxis;
jsfc.LogAxis.prototype.calculateLog = function(value) {
  return Math.log(value) / this._baseLog;
};
jsfc.LogAxis.prototype.calculateValue = function(log) {
  return Math.pow(this._base, log);
};
jsfc.LogAxis.prototype.setBounds = function(lower, upper, notify) {
  this._lowerBound = lower;
  this._upperBound = upper;
  this._logRange = new jsfc.Range(this.calculateLog(lower), this.calculateLog(upper));
  this._autoRange = false;
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.LogAxis.prototype.valueToCoordinate = function(value, r0, r1) {
  jsfc.Args.requireNumber(r0, "r0");
  jsfc.Args.requireNumber(r1, "r1");
  var log = this.calculateLog(value);
  var percent = this._logRange.percent(log);
  return r0 + percent * (r1 - r0);
};
jsfc.LogAxis.prototype.coordinateToValue = function(coordinate, r0, r1) {
  var percent = (coordinate - r0) / (r1 - r0);
  var logValue = this._logRange.value(percent);
  return this.calculateValue(logValue);
};
jsfc.LogAxis.prototype._calcTickSize = function(ctx, area, edge) {
  var result = Number.NaN;
  var pixels = area.length(edge);
  var range = this._logRange.length();
  var orientation = this._resolveTickLabelOrientation(edge);
  var selector = this._tickSelector;
  if (orientation === jsfc.LabelOrientation.PERPENDICULAR) {
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
    if (orientation === jsfc.LabelOrientation.PARALLEL) {
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
jsfc.LogAxis.prototype.ticks = function(tickSize, ctx, area, edge) {
  var formatter = this._tickLabelFormatOverride || this._formatter;
  if (!isNaN(tickSize)) {
    var result = [];
    var t = Math.ceil(this._logRange._lowerBound / tickSize) * tickSize;
    while (t < this._logRange._upperBound) {
      var v = this.calculateValue(t);
      var tm = new jsfc.TickMark(v, formatter.format(v));
      result.push(tm);
      t += tickSize;
    }
    return result;
  } else {
    var tm0 = new jsfc.TickMark(this._lowerBound, formatter.format(this._logRange._lowerBound));
    var tm1 = new jsfc.TickMark(this._upperBound, formatter.format(this._logRange._upperBound));
    return[tm0, tm1];
  }
};
jsfc.SymbolAxis = function(label) {
  if (!(this instanceof jsfc.SymbolAxis)) {
    throw new Error("Use 'new' with constructor.");
  }
  jsfc.LinearAxis.init(label, this);
  this._symbols = [];
  this._showEndPointValuesIfNoSymbols = true;
};
jsfc.SymbolAxis.prototype = new jsfc.LinearAxis;
jsfc.SymbolAxis.prototype.configureAsXAxis = function(plot) {
  if (this._symbols.length === 0) {
    var s = plot.getDataset().getProperty("x-symbols");
    if (s) {
      this._symbols = s.map(function(e) {
        return e;
      });
    }
  }
  jsfc.LinearAxis.prototype.configureAsXAxis.call(this, plot);
};
jsfc.SymbolAxis.prototype.configureAsYAxis = function(plot) {
  if (this._symbols.length === 0) {
    var s = plot.getDataset().getProperty("y-symbols");
    if (s) {
      this._symbols = s.map(function(e) {
        return e;
      });
    }
  }
  jsfc.LinearAxis.prototype.configureAsXAxis.call(this, plot);
};
jsfc.SymbolAxis.prototype.addSymbol = function(symbol, value, notify) {
  this._symbols.push({"symbol":symbol, "value":value});
  this._symbols.sort(function(a, b) {
    return a.value - b.value;
  });
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.SymbolAxis.prototype.clearSymbols = function(notify) {
  this._symbols = [];
  if (notify !== false) {
    this.notifyListeners();
  }
  return this;
};
jsfc.SymbolAxis.prototype._symbolCount = function(range) {
  var c = 0;
  this._symbols.forEach(function(s) {
    if (range.contains(s.value)) {
      c++;
    }
  });
  return c;
};
jsfc.SymbolAxis.prototype.ticks = function(tickSize, ctx, area, edge) {
  var r = new jsfc.Range(this._lowerBound, this._upperBound);
  if (this._symbolCount(r) > 0) {
    var result = [];
    var axis = this;
    this._symbols.forEach(function(s) {
      if (s.value > axis._lowerBound && s.value < axis._upperBound) {
        result.push(new jsfc.TickMark(s.value, s.symbol));
      }
    });
    return result;
  } else {
    if (this._showEndPointValuesIfNoSymbols) {
      var formatter = this._tickLabelFormatOverride || this._formatter;
      var tm0 = new jsfc.TickMark(this._lowerBound, formatter.format(this._lowerBound));
      var tm1 = new jsfc.TickMark(this._upperBound, formatter.format(this._upperBound));
      return[tm0, tm1];
    } else {
      return[];
    }
  }
};
jsfc.XYPlot = function(dataset) {
  if (!(this instanceof jsfc.XYPlot)) {
    throw new Error("Use 'new' for construction.");
  }
  this._listeners = [];
  this._plotBackground = null;
  this._dataBackground = new jsfc.StandardRectanglePainter(new jsfc.Color(230, 230, 230), new jsfc.Color(0, 0, 0, 0));
  this._renderer = new jsfc.ScatterRenderer(this);
  this.updateBounds = false;
  this._axisOffsets = new jsfc.Insets(0, 0, 0, 0);
  this._xAxis = new jsfc.LinearAxis;
  this._xAxisPosition = jsfc.RectangleEdge.BOTTOM;
  this._xAxis.configureAsXAxis(this);
  this._xAxisListener = function(p) {
    var plot = p;
    return function(axis) {
      if (axis.isAutoRange()) {
        axis.configureAsXAxis(plot);
      }
      plot.notifyListeners();
    };
  }(this);
  this._xAxis.addListener(this._xAxisListener);
  this._yAxis = new jsfc.LinearAxis;
  this._yAxisPosition = jsfc.RectangleEdge.LEFT;
  this._yAxis.configureAsYAxis(this);
  this._yAxisListener = function(p) {
    var plot = p;
    return function(axis) {
      if (axis.isAutoRange()) {
        axis.configureAsYAxis(plot);
      }
      plot.notifyListeners();
    };
  }(this);
  this._yAxis.addListener(this._yAxisListener);
  this.setDataset(dataset);
  this.itemLabelGenerator = new jsfc.XYLabels;
};
jsfc.XYPlot.prototype.getDataset = function() {
  return this._dataset;
};
jsfc.XYPlot.prototype.setDataset = function(dataset, notify) {
  if (this._datasetListener) {
    this._dataset.removeListener(this._datasetListener);
  }
  this._dataset = dataset;
  this._datasetListener = function(plot) {
    var me = plot;
    return function(dataset) {
      me.datasetChanged();
    };
  }(this);
  this._dataset.addListener(this._datasetListener);
  this._xAxis.configureAsXAxis(this);
  this._yAxis.configureAsYAxis(this);
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.getBackground = function() {
  return this._plotBackground;
};
jsfc.XYPlot.prototype.setBackground = function(painter, notify) {
  this._plotBackground = painter;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.setBackgroundColor = function(color, notify) {
  var painter = new jsfc.StandardRectanglePainter(color, null);
  this.setBackground(painter, notify);
};
jsfc.XYPlot.prototype.getDataBackground = function() {
  return this._dataBackground;
};
jsfc.XYPlot.prototype.setDataBackground = function(painter, notify) {
  this._dataBackground = painter;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.setDataBackgroundColor = function(color, notify) {
  var painter = new jsfc.StandardRectanglePainter(color, null);
  this.setDataBackground(painter, notify);
};
jsfc.XYPlot.prototype.datasetChanged = function() {
  if (this._xAxis.isAutoRange()) {
    this._xAxis.configureAsXAxis(this);
  }
  if (this._yAxis.isAutoRange()) {
    this._yAxis.configureAsYAxis(this);
  }
  this.notifyListeners();
};
jsfc.XYPlot.prototype.getAxisOffsets = function() {
  return this._axisOffsets;
};
jsfc.XYPlot.prototype.setAxisOffsets = function(offsets, notify) {
  this._axisOffsets = offsets;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.getXAxis = function() {
  return this._xAxis;
};
jsfc.XYPlot.prototype.setXAxis = function(axis, notify) {
  this._xAxis.removeListener(this._xAxisListener);
  this._xAxis = axis;
  this._xAxis.addListener(this._xAxisListener);
  this._xAxis.configureAsXAxis(this);
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.getXAxisPosition = function() {
  return this._xAxisPosition;
};
jsfc.XYPlot.prototype.setXAxisPosition = function(edge, notify) {
  this.xAxisPosition = edge;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.isXZoomable = function() {
  return true;
};
jsfc.XYPlot.prototype.getYAxis = function() {
  return this._yAxis;
};
jsfc.XYPlot.prototype.setYAxis = function(axis, notify) {
  this._yAxis.removeListener(this._yAxisListener);
  this._yAxis = axis;
  this._yAxis.addListener(this._yAxisListener);
  this._yAxis.configureAsYAxis(this);
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.getYAxisPosition = function() {
  return this._yAxisPosition;
};
jsfc.XYPlot.prototype.setYAxisPosition = function(edge, notify) {
  this.yAxisPosition = edge;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.isYZoomable = function() {
  return true;
};
jsfc.XYPlot.prototype.zoomXAboutAnchor = function(factor, anchor, notify) {
  var x0 = this._dataArea.minX();
  var x1 = this._dataArea.maxX();
  var anchorX = this._xAxis.coordinateToValue(anchor, x0, x1);
  this._xAxis.resizeRange(factor, anchorX, notify !== false);
};
jsfc.XYPlot.prototype.zoomX = function(lowpc, highpc, notify) {
  this._xAxis.setBoundsByPercent(lowpc, highpc, notify);
};
jsfc.XYPlot.prototype.zoomYAboutAnchor = function(factor, anchor, notify) {
  var y0 = this._dataArea.minY();
  var y1 = this._dataArea.maxY();
  var anchorY = this._yAxis.coordinateToValue(anchor, y1, y0);
  this._yAxis.resizeRange(factor, anchorY, notify !== false);
};
jsfc.XYPlot.prototype.zoomY = function(lowpc, highpc, notify) {
  this._yAxis.setBoundsByPercent(lowpc, highpc, notify);
};
jsfc.XYPlot.prototype.panX = function(percent, notify) {
  this._xAxis.pan(percent, notify !== false);
};
jsfc.XYPlot.prototype.panY = function(percent, notify) {
  this._yAxis.pan(percent, notify !== false);
};
jsfc.XYPlot.prototype.addListener = function(f) {
  this._listeners.push(f);
};
jsfc.XYPlot.prototype.notifyListeners = function() {
  var plot = this;
  this._listeners.forEach(function(f) {
    f(plot);
  });
};
jsfc.XYPlot.prototype.setRenderer = function(renderer, notify) {
  this._renderer = renderer;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.XYPlot.prototype.draw = function(ctx, bounds, plotArea) {
  if (this._plotBackground) {
    this._plotBackground.paint(ctx, plotArea);
  }
  var space = new jsfc.AxisSpace(0, 0, 0, 0);
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
jsfc.XYPlot.prototype.dataArea = function() {
  return this._dataArea;
};
jsfc.XYPlot.prototype.drawAxes = function(ctx, bounds, dataArea) {
  var offset = this._axisOffsets.value(this._xAxisPosition);
  this._xAxis.draw(ctx, this, bounds, dataArea, offset);
  offset = this._axisOffsets.value(this._yAxisPosition);
  this._yAxis.draw(ctx, this, bounds, dataArea, offset);
};
jsfc.XYPlot.prototype.axisPosition = function(axis) {
  if (axis === this._xAxis) {
    return this._xAxisPosition;
  } else {
    if (axis === this._yAxis) {
      return this._yAxisPosition;
    }
  }
  throw new Error("The axis does not belong to this plot.");
};
jsfc.XYPlot.prototype.legendInfo = function() {
  var info = [];
  var plot = this;
  this._dataset.seriesKeys().forEach(function(key) {
    var dataset = plot._dataset;
    var index = dataset.seriesIndex(key);
    var color = plot._renderer.getLineColorSource().getLegendColor(index);
    var item = new jsfc.LegendItemInfo(key, color);
    item.label = key;
    info.push(item);
  });
  return info;
};
jsfc.XYPlot.prototype.dataset = function(dataset) {
  throw new Error("Use setDataset().");
};
jsfc.ColorSource = function(colors) {
  if (!(this instanceof jsfc.ColorSource)) {
    throw new Error("Use 'new' for constructor.");
  }
  this._colors = colors;
};
jsfc.ColorSource.prototype.getColor = function(series, item) {
  return this._colors[series % this._colors.length];
};
jsfc.ColorSource.prototype.getLegendColor = function(series) {
  return this._colors[series % this._colors.length];
};
jsfc.BaseXYRenderer = function(instance) {
  if (!(this instanceof jsfc.BaseXYRenderer)) {
    throw new Error("Use 'new' for constructor.");
  }
  if (!instance) {
    instance = this;
  }
  jsfc.BaseXYRenderer.init(instance);
};
jsfc.BaseXYRenderer.init = function(instance) {
  var lineColors = jsfc.Colors.colorsAsObjects(jsfc.Colors.fancyLight());
  var fillColors = jsfc.Colors.colorsAsObjects(jsfc.Colors.fancyLight());
  instance._lineColorSource = new jsfc.ColorSource(lineColors);
  instance._fillColorSource = new jsfc.ColorSource(fillColors);
  instance._listeners = [];
};
jsfc.BaseXYRenderer.prototype.getLineColorSource = function() {
  return this._lineColorSource;
};
jsfc.BaseXYRenderer.prototype.setLineColorSource = function(cs, notify) {
  this._lineColorSource = cs;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseXYRenderer.prototype.getFillColorSource = function() {
  return this._fillColorSource;
};
jsfc.BaseXYRenderer.prototype.setFillColorSource = function(cs, notify) {
  this._fillColorSource = cs;
  if (notify !== false) {
    this.notifyListeners();
  }
};
jsfc.BaseXYRenderer.prototype.passCount = function() {
  return 1;
};
jsfc.BaseXYRenderer.prototype.addListener = function(f) {
  this._listeners.push(f);
};
jsfc.BaseXYRenderer.prototype.notifyListeners = function() {
  var plot = this;
  this._listeners.forEach(function(f) {
    f(plot);
  });
};
jsfc.XYBarRenderer = function() {
  if (!(this instanceof jsfc.XYBarRenderer)) {
    throw new Error("Use 'new' for constructor.");
  }
  jsfc.BaseXYRenderer.init(this);
};
jsfc.XYBarRenderer.prototype = new jsfc.BaseXYRenderer;
jsfc.XYBarRenderer.prototype.drawItem = function(ctx, dataArea, plot, dataset, seriesIndex, itemIndex, pass) {
  var xmin = dataset.xmin(seriesIndex, itemIndex);
  var xmax = dataset.xmax(seriesIndex, itemIndex);
  var y = dataset.y(seriesIndex, itemIndex);
  var xAxis = plot.getXAxis();
  var yAxis = plot.getYAxis();
  var w = dataArea.width();
  var h = dataArea.height();
  var xxmin = xAxis.valueToCoordinate(xmin, dataArea.x(), dataArea.x() + w);
  var xxmax = xAxis.valueToCoordinate(xmax, dataArea.x(), dataArea.x() + w);
  var yy = yAxis.valueToCoordinate(y, dataArea.y() + h, dataArea.y());
  var zz = yAxis.valueToCoordinate(0, dataArea.y() + h, dataArea.y());
  ctx.setLineColor(this._lineColorSource.getColor(seriesIndex, itemIndex));
  ctx.setLineStroke(new jsfc.Stroke(1));
  ctx.setFillColor(this._fillColorSource.getColor(seriesIndex, itemIndex));
  ctx.drawRect(xxmin, Math.min(yy, zz), xxmax - xxmin, Math.abs(yy - zz));
};
jsfc.XYLineRenderer = function() {
  if (!(this instanceof jsfc.XYLineRenderer)) {
    return new jsfc.XYLineRenderer;
  }
  jsfc.BaseXYRenderer.init(this);
};
jsfc.XYLineRenderer.prototype = new jsfc.BaseXYRenderer;
jsfc.XYLineRenderer.prototype.passCount = function() {
  return 2;
};
jsfc.XYLineRenderer.prototype.drawItem = function(ctx, dataArea, plot, dataset, seriesIndex, itemIndex, pass) {
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
      ctx.setLineStroke(new jsfc.Stroke(3));
      ctx.drawLine(xx0, yy0, xx, yy);
    }
  } else {
    if (pass === 1) {
    }
  }
};
jsfc.ScatterRenderer = function(plot) {
  if (!(this instanceof jsfc.ScatterRenderer)) {
    throw new Error("Use 'new' for constructors.");
  }
  jsfc.BaseXYRenderer.init(this);
  this._plot = plot;
  this._radius = 3;
};
jsfc.ScatterRenderer.prototype = new jsfc.BaseXYRenderer;
jsfc.ScatterRenderer.prototype.itemFillColorStr = function(seriesKey, itemKey) {
  var dataset = this._plot.getDataset();
  var c = dataset.getProperty(seriesKey, itemKey, "color");
  if (c) {
    return c;
  }
  var color = this.itemFillColor(seriesKey, itemKey);
  return color.rgbaStr();
};
jsfc.ScatterRenderer.prototype.itemFillColor = function(seriesKey, itemKey) {
  var dataset = this._plot.getDataset();
  var seriesIndex = dataset.seriesIndex(seriesKey);
  var itemIndex = dataset.itemIndex(seriesKey, itemKey);
  return this._lineColorSource.getColor(seriesIndex, itemIndex);
};
jsfc.ScatterRenderer.prototype.itemStrokeColor = function(seriesKey, itemKey) {
  if (this._plot._dataset.isSelected("select", seriesKey, itemKey)) {
    return "red";
  }
  return "none";
};
jsfc.ScatterRenderer.prototype.drawItem = function(ctx, dataArea, plot, dataset, seriesIndex, itemIndex, pass) {
  var seriesKey = dataset.seriesKey(seriesIndex);
  var itemKey = dataset.getItemKey(seriesIndex, itemIndex);
  var x = dataset.x(seriesIndex, itemIndex);
  var y = dataset.y(seriesIndex, itemIndex);
  var xx = plot.getXAxis().valueToCoordinate(x, dataArea.minX(), dataArea.maxX());
  var yy = plot.getYAxis().valueToCoordinate(y, dataArea.maxY(), dataArea.minY());
  var str = dataset.getItemProperty(seriesKey, itemKey, "color");
  var color;
  if (typeof str === "string") {
    color = jsfc.Color.fromStr(str);
  } else {
    color = this.itemFillColor(seriesKey, itemKey);
  }
  ctx.setFillColor(color);
  ctx.drawCircle(xx, yy, this._radius);
};
jsfc.LogEventHandler = function() {
  if (!(this instanceof jsfc.LogEventHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.modifier = new jsfc.Modifier;
  this._log = false;
};
jsfc.LogEventHandler.prototype.mouseDown = function(e) {
  if (!this._log) {
    return;
  }
  console.log("DOWN: clientX = " + e.clientX + ", y = " + e.clientY);
};
jsfc.LogEventHandler.prototype.mouseMove = function(e) {
  if (!this._log) {
    return;
  }
  console.log("MOVE: clientX = " + e.clientX + ", y = " + e.clientY);
};
jsfc.LogEventHandler.prototype.mouseUp = function(e) {
  if (!this._log) {
    return;
  }
  console.log("UP: clientX = " + e.clientX + ", y = " + e.clientY);
};
jsfc.LogEventHandler.prototype.mouseOver = function(e) {
  if (!this._log) {
    return;
  }
  console.log("OVER: clientX = " + e.clientX + ", y = " + e.clientY);
};
jsfc.LogEventHandler.prototype.mouseOut = function(e) {
  if (!this._log) {
    return;
  }
  console.log("OUT: clientX = " + e.clientX + ", y = " + e.clientY);
};
jsfc.LogEventHandler.prototype.mouseWheel = function(e) {
  if (!this._log) {
    return false;
  }
  console.log("WHEEL : " + e.wheelDelta);
  return false;
};
jsfc.Modifier = function(altKey, ctrlKey, metaKey, shiftKey) {
  if (!(this instanceof jsfc.Modifier)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.altKey = altKey || false;
  this.ctrlKey = ctrlKey || false;
  this.metaKey = metaKey || false;
  this.shiftKey = shiftKey || false;
};
jsfc.Modifier.prototype.match = function(alt, ctrl, meta, shift) {
  return this.altKey === alt && (this.ctrlKey === ctrl && (this.metaKey === meta && this.shiftKey === shift));
};
jsfc.Modifier.prototype.matches = function(other) {
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
  return true;
};
jsfc.PanHandler = function(manager, modifier) {
  if (!(this instanceof jsfc.PanHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.manager = manager;
  this.modifier = modifier || new jsfc.Modifier(false, false, false, false);
  this._lastPoint = null;
};
jsfc.PanHandler.prototype.mouseDown = function(e) {
  var x = e.clientX;
  var y = e.clientY;
  var dataArea = this.manager.getChart().plotArea();
  this._lastPoint = dataArea.constrainedPoint(x, y);
};
jsfc.PanHandler.prototype.mouseMove = function(e) {
  if (this._lastPoint === null) {
    return;
  }
  var x = e.clientX;
  var y = e.clientY;
  var dx = x - this._lastPoint.x();
  var dy = y - this._lastPoint.y();
  if (dx !== 0 || dy !== 0) {
    this._lastPoint = new jsfc.Point2D(x, y);
    var plot = this.manager.getChart().getPlot();
    var dataArea = plot.dataArea();
    var wpercent = -dx / dataArea.width();
    var hpercent = dy / dataArea.height();
    plot.panX(wpercent, false);
    plot.panY(hpercent);
  }
};
jsfc.PanHandler.prototype.mouseUp = function(e) {
  this._lastPoint = null;
  this.manager._liveMouseHandler = null;
};
jsfc.PanHandler.prototype.mouseOver = function(e) {
};
jsfc.PanHandler.prototype.mouseOut = function(e) {
};
jsfc.PanHandler.prototype.mouseWheel = function(e) {
};
jsfc.WheelHandler = function(manager, modifier) {
  if (!(this instanceof jsfc.WheelHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.manager = manager;
  this.modifier = modifier || new jsfc.Modifier(false, false, false, false);
};
jsfc.WheelHandler.prototype.mouseDown = function(e) {
};
jsfc.WheelHandler.prototype.mouseMove = function(e) {
};
jsfc.WheelHandler.prototype.mouseUp = function(e) {
};
jsfc.WheelHandler.prototype.mouseOver = function(e) {
};
jsfc.WheelHandler.prototype.mouseOut = function(e) {
};
jsfc.WheelHandler.prototype.mouseWheel = function(e) {
  var delta;
  if (e.wheelDelta) {
    delta = e.wheelDelta / 720 * -0.2 + 1;
  } else {
    delta = e.detail * 0.05 + 1;
  }
  var plot = this.manager.getChart().getPlot();
  var zoomX = plot.isXZoomable();
  var zoomY = plot.isYZoomable();
  if (zoomX) {
    plot.zoomXAboutAnchor(delta, e.clientX, !zoomY);
  }
  if (zoomY) {
    var svg = this.manager.getElement();
    plot.zoomYAboutAnchor(delta, e.clientY - svg.getBoundingClientRect().top);
  }
  return!(zoomX || zoomY);
};
jsfc.ZoomHandler = function(manager, modifier) {
  if (!(this instanceof jsfc.ZoomHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.manager = manager;
  this.modifier = modifier || new jsfc.Modifier(false, false, false, false);
  this.zoomPoint = null;
  this.zoomRectangle = null;
  this._fillColor = new jsfc.Color(255, 0, 0, 50);
};
jsfc.ZoomHandler.prototype.mouseDown = function(e) {
  var r = this.manager.getElement().getBoundingClientRect();
  var x = e.clientX - r.left;
  var y = e.clientY - r.top;
  var dataArea = this.manager.getChart().getPlot().dataArea();
  this.zoomPoint = dataArea.constrainedPoint(x, y);
};
jsfc.ZoomHandler.prototype.mouseMove = function(e) {
  if (this.zoomPoint === null) {
    return;
  }
  var r = this.manager.getElement().getBoundingClientRect();
  var x = e.clientX - r.left;
  var y = e.clientY - r.top;
  var dataArea = this.manager.getChart().getPlot().dataArea();
  var endPoint = dataArea.constrainedPoint(x, y);
  var ctx = this.manager.getContext();
  ctx.setHint("layer", "zoom");
  ctx.clear();
  var x = this.zoomPoint.x();
  var y = this.zoomPoint.y();
  var width = endPoint.x() - x;
  var height = endPoint.y() - y;
  if (width > 0 && height > 0) {
    ctx.setFillColor(this._fillColor);
    ctx.setLineStroke(new jsfc.Stroke(0.1));
    ctx.drawRect(x, y, width, height);
  }
  ctx.setHint("layer", "default");
};
jsfc.ZoomHandler.prototype.mouseUp = function(e) {
  if (this.zoomPoint === null) {
    return;
  }
  var r = this.manager.getElement().getBoundingClientRect();
  var x = e.clientX - r.left;
  var y = e.clientY - r.top;
  var plot = this.manager.getChart().getPlot();
  var dataArea = plot.dataArea();
  var endPoint = dataArea.constrainedPoint(x, y);
  var x = this.zoomPoint.x();
  var y = this.zoomPoint.y();
  var width = endPoint.x() - x;
  var height = endPoint.y() - y;
  if (width > 0 && height > 0) {
    var xAxis = plot.getXAxis();
    var yAxis = plot.getYAxis();
    var p0 = (x - dataArea.minX()) / dataArea.width();
    var p1 = (x + width - dataArea.minX()) / dataArea.width();
    var p3 = (dataArea.maxY() - y) / dataArea.height();
    var p2 = (dataArea.maxY() - y - height) / dataArea.height();
    xAxis.setBoundsByPercent(p0, p1, false);
    yAxis.setBoundsByPercent(p2, p3);
  } else {
    plot.getXAxis().setAutoRange(true);
    plot.getYAxis().setAutoRange(true);
  }
  this.zoomPoint = null;
  var ctx = this.manager.getContext();
  ctx.setHint("layer", "zoom");
  ctx.clear();
  ctx.setHint("layer", "default");
  this.manager._liveMouseHandler = null;
};
jsfc.ZoomHandler.prototype.mouseOver = function(e) {
};
jsfc.ZoomHandler.prototype.mouseOut = function(e) {
};
jsfc.ZoomHandler.prototype.mouseWheel = function(e) {
  return true;
};
jsfc.ClickSelectionHandler = function() {
  if (!(this instanceof jsfc.ClickSelectionHandler)) {
    throw new Error("Use 'new' for constructor.");
  }
  this.modifier = new jsfc.Modifier;
};
jsfc.ClickSelectionHandler.prototype.mouseDown = function(e) {
};
jsfc.LogEventHandler.prototype.mouseMove = function(e) {
};
jsfc.LogEventHandler.prototype.mouseUp = function(e) {
};
jsfc.LogEventHandler.prototype.mouseWheel = function(e) {
};
jsfc.KeyedValueLabels = function() {
  if (!(this instanceof jsfc.KeyedValueLabels)) {
    return new jsfc.KeyedValueLabels;
  }
  this.format = "{K} = {V}";
  this.valueDP = 2;
  this.percentDP = 2;
};
jsfc.KeyedValueLabels.prototype.itemLabel = function(keyedValues, itemIndex) {
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
jsfc.KeyedValue2DLabels = function() {
  if (!(this instanceof jsfc.KeyedValue2DLabels)) {
    return new jsfc.KeyedValue2DLabels;
  }
  this.format = "{R}, {C} = {V}";
  this.valueDP = 2;
};
jsfc.KeyedValue2DLabels.prototype.itemLabel = function(keyedValues2D, rowIndex, columnIndex) {
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
jsfc.KeyedValue3DLabels = function() {
  if (!(this instanceof jsfc.KeyedValue3DLabels)) {
    return new jsfc.KeyedValue3DLabels;
  }
  this.format = "{S}, {R}, {C} = {V}";
  this.valueDP = 2;
};
jsfc.KeyedValue3DLabels.prototype.itemLabel = function(keyedValues3D, seriesIndex, rowIndex, columnIndex) {
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
jsfc.XYLabels = function() {
  if (!(this instanceof jsfc.XYLabels)) {
    return new jsfc.XYLabels;
  }
  this.format = "{X}, {Y} / {S}";
  this.xDP = 2;
  this.yDP = 2;
};
jsfc.XYLabels.prototype.itemLabel = function(dataset, seriesKey, itemIndex) {
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
jsfc.XYZLabels = function() {
  if (!(this instanceof jsfc.XYZLabels)) {
    return new jsfc.XYZLabels;
  }
  this.format = "{X}, {Y}, {Z} / {S}";
  this.xDP = 2;
  this.yDP = 2;
  this.zDP = 2;
};
jsfc.XYZLabels.prototype.itemLabel = function(dataset, seriesKey, itemIndex) {
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

