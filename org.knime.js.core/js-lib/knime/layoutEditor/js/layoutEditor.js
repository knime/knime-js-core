var layout = { "rows" : [] };
var nodesArray;	// JS version of the current Knime list of nodes

var selectionId;
var selection = null;
var containerID = "knimeWebNode";
var splitNumber;
var parentRectangleHeight;
var splitHorizontalDone;

var webNodes = []; // Internal list of nodes for this view

var isDebug = true;
var layoutValues = [];


function splitHorizontally() {
	splitNumber = parseInt(document.getElementById("horizontalSplitNumber").value);

	// Identifying the selection in the view
	selectionId = $('.rectangle.selected').attr('id');

	parentRectangleHeight = $('.rectangle.selected').height();

	// Identifying the selection in the layout
	if (selectionId === undefined) {
		selection = layout.rows;
	} else {
		var objects = getObjects(layout, "id", selectionId);
		selection = objects[0].content;
	}

	// Modifying the layout
	for (var i = 0; i < splitNumber ; i++) {
		var row = {
			"id" : chance.integer({ min: 0, max: 1000 }), 
			"type" : "row",
			"columns" : []
		}
		row.columns.push({
			"id" : chance.integer({ min: 0, max: 1000 }),
			"parent" : row.id,
			"content" : [],
			"widthMD" : 12
		});
		selection.push(row);		
	}

	// Removing former container for the view
	$('#knimeBSLayoutContainer').remove();

	// Rebuilding the dom structure
	_buildBSLayout(layout, null);
	$('.rectangle.new').click(selectRectangle);
	$('.rectangle').removeClass('new');

	$('#horizontalSplitModal').modal('hide');
}

function updateColumnWidthText() {
	var splitNumber = parseInt(document.getElementById("verticalSplitNumber").value);
	var columnSize = Math.floor(12 / splitNumber);
	document.getElementById("columnWidthText").innerHTML = "Width: " + columnSize + "/12";
}

function splitVertically() {
	var splitNumber = parseInt(document.getElementById("verticalSplitNumber").value);

	// Identifying the selection in the view
	selectionId = $('.rectangle.selected').attr('id');
	var rowId;
	var colWidth;

	// Identifying the selection in the  
	if (selectionId === undefined) {
		var row = {
			"id" : chance.integer({ min: 0, max: 1000 }), 
			"type" : "row",
			"columns" : []
		}
		layout.rows.push(row);
		colWidth = 12;
		selection = layout.rows[0].columns;
	} else {
		var objects = getObjects(layout, "id", selectionId);
		colWidth = objects[0].widthMD;
		rowId = objects[0].parent;
		objects = getObjects(layout, "id", rowId);
		selection = objects[0].columns;
	}

	// Modifying the layout
	selection.splice(0, selection.length);
	for (var i = 0; i < splitNumber ; i++) {
		var column = {
			"id" : chance.integer({ min: 0, max: 1000 }),
			"parent" : rowId, 
			"content" : [],
			"widthMD" : colWidth / splitNumber
		}
		selection.push(column);		
	}

	// Removing former container for the view
	$('#knimeBSLayoutContainer').remove();

	// Rebuilding the dom structure
	_buildBSLayout(layout, null);
	$('.rectangle.new').click(selectRectangle);
	$('.rectangle').removeClass('new');

	$('#verticalSplitModal').modal('hide');	
}

function selectRectangle() {
	isFirstSelection = true;
	event.stopPropagation();
	document.querySelectorAll('.rectangle').forEach(function (rectangle) {
		rectangle.classList.remove('selected');
	});
	this.classList.add('selected');
}

function insertNode() {
	var nodeID = document.getElementById("nodeSelection").value;
	var nodeType = nodesArray[parseInt(nodeID)].name;

	webNodes[parseInt(nodeID)] = {
		nodeType: nodeType,
		nodeInfo: {
			displayPossible: true,
		},
	};

	// Identifying the selection on the page
	selectionId = $('.rectangle.selected').attr('id');

	// Identifying the insertion point in the layout
	if (selectionId === undefined) {
		console.log("Please select a rectangle in the canvas.");
	} else {
		var objects = getObjects(layout, "id", selectionId);
		selection = objects[0].content;
	}

	// Modifying the layout
	var content = {
        "type" : "view",
        "nodeID" : nodeID,
        "scrolling" : nodesArray[parseInt(nodeID)].layout.scrolling,
        "resizeMethod" : nodesArray[parseInt(nodeID)].layout.resizeMethod,
        "sizeHeight" : nodesArray[parseInt(nodeID)].layout.sizeHeight,
        "sizeWidth" : nodesArray[parseInt(nodeID)].layout.sizeWidth,
        "autoResize" : nodesArray[parseInt(nodeID)].layout.autoResize,
        "availableInView" : nodesArray[parseInt(nodeID)].layout.availableInView,
    }
	selection.push(content);

	$('#knimeBSLayoutContainer').remove();
	_buildBSLayout(layout, null);
	$('.rectangle.new').click(selectRectangle);
	$('.rectangle').removeClass('new');
}

function deleteRectangle() {

}

function loadConfiguration() {
	// Identifying the selection on the page
	selectionId = $('.rectangle.selected').attr('id');

	// Identifying the selection in the layout
	if (selectionId === undefined) {	
		console.log("Please select a rectangle in the canvas.");
	} else {
		var objects = getObjects(layout, "id", selectionId);
		selection = objects[0].content[0];
	}

	// Launching the modal
	$('#loadConfigurationModal').modal();

	// Displaying the modal contents with current layout
	$('#loadConfigurationModal').on('shown.bs.modal', function (e) {
		$('#configModalTitle').append(selection.nodeID);
		$('#resizeMethod').val(selection.resizeMethod);
		$('#autoResize').prop("checked", selection.autoResize);
		$('#enableScrolling').prop("checked", selection.scrolling);
		$('#sizeHeight').prop("checked", selection.sizeHeight);
		$('#sizeWidth').prop("checked", selection.sizeWidth);
		$('#resizeInterval').val(selection.resizeInterval);
		$('#resizeTolerance').val(selection.resizeTolerance);
		$('#minimumWidth').val(selection.minWidth);
		$('#maximumWidth').val(selection.maxWidth);
		$('#minimumHeight').val(selection.minHeight);
		$('#maximumHeight').val(selection.maxHeight);
	})
}

function saveConfiguration() {
	// Updating the modal contents with current layout
	selection.resizeMethod = $('#resizeMethod').val();
	selection.autoResize = $('#autoResize').is(':checked');
	selection.scrolling = $('#enableScrolling').is(':checked')
	selection.sizeHeight = $('#sizeHeight').is(':checked');
	selection.sizeWidth = $('#sizeWidth').is(':checked');
	selection.resizeInterval = $('#resizeInterval').val();
	selection.resizeTolerance = $('#resizeTolerance').val();
	selection.minWidth = $('#minimumWidth').val();
	selection.maxWidth = $('#maximumWidth').val();
	selection.minHeight = $('#minimumHeight').val();
	selection.maxHeight = $('#maximumHeight').val();

	// Closing the modal
 	$('#loadConfigurationModal').modal('hide');	
}

function loadLayout(layoutData) {
	$('#knimeBSLayoutContainer').remove();
	layout = JSON.parse(layoutData);
	if (isEmpty(layout)) layout = { "rows" : [] };
	layout = enrichLayout(layout);
	_buildBSLayout(layout, null);
	$('.rectangle.new').click(selectRectangle);
	$('.rectangle').removeClass('new');
}

function sendLayout() {
	return JSON.stringify(cleanLayout(layout));
}

// Function to be called by Java for providing to JS the list of nodes
function loadNodes(nodesData) {
	// Converting nodesMap to an array 
	nodesArray = Array.prototype.slice.call(nodesData.nodes);

	//Updating the webNodes
	for (var i = 0; i < nodesArray.length; i++) {
		if (nodesArray[i] != undefined) {
			webNodes[i] = {
				nodeType: nodesArray[i].name,
				nodeInfo: {
					displayPossible: true,
				},
			};
		}
	}

	// Adding options to the nodeSelection
	for (var i = 0; i < nodesArray.length; i++) {
		if (nodesArray[i] != undefined) {
			$('#nodeSelection').append('<option value="' + i + '">' + nodesArray[i].name + '</option>');
		}
	}
}

// Adding to the layout internal ids and parent ids
function enrichLayout(layout) {
	if (layout.rows) {
		for (var i = 0; i < layout.rows.length; i++) {
			enrichLayout(layout.rows[i]);
		}
	}

	if (layout.type === "row") {
		var row = layout;
		if (row.id == undefined) {
			row.id = chance.integer({ min: 0, max: 1000 });
		}
		if (row.columns) {
			for (var j = 0; j < row.columns.length ; j++) {
				var column = row.columns[j];
				if (column.id == undefined) {
					column.id = chance.integer({ min: 0, max: 1000 });
				}
				if (column.parent == undefined) {
					column.parent = row.id;
				}
				if (column.content) {
					for (var k = 0; k < column.content.length; k++) {
						enrichLayout(column.content[k]);
					}
				}
			}
		}	
	}

	return layout;
}

// Cleaning the layout (removing the ids and parent fields)
function cleanLayout(layout) {
	if (layout.rows) {
		for (var i = 0; i < layout.rows.length; i++) {
			cleanLayout(layout.rows[i]);
		}
	}

	if (layout.type === "row") {
		var row = layout;
		delete row.id;
		for (var j = 0; j < row.columns.length ; j++) {
			var column = row.columns[j];
			delete column.id;
			delete column.parent;
			for (var k = 0; k < column.content.length; k++) {
				var content = column.content[k];
				cleanLayout(content);
			}
		}
	}	

	return layout;
}

function reset() {
	$('#knimeBSLayoutContainer').remove();
	layout = { "rows" : [] };
	_buildBSLayout(layout, null);
}

function getObjects(obj, key, val) {
    var objects = [];
    for (var i in obj) {
        if (!obj.hasOwnProperty(i)) continue;
        if (typeof obj[i] == 'object') {
            objects = objects.concat(getObjects(obj[i], key, val));    
        } else 
        if (i == key && obj[i] == val) { //
            objects.push(obj);
        }
    }
    return objects;
}

function isEmpty(obj) {
    for(var key in obj) {
        if(obj.hasOwnProperty(key))
            return false;
    }
    return true;
}

_getContainerElement = function() {
	var cont = document.getElementById(containerID);
	if (!cont) {
		cont = document.getElementsByTagName("body")[0];
	}
	return cont;
}

_buildBSLayout = function(layout, parentContainer) {
	var parent = parentContainer;
	if (typeof parentContainer == "undefined" || parentContainer == null) {
		var wrapperContainer = document.createElement("div");
		wrapperContainer.id = "knimeBSLayoutContainer";
		wrapperContainer.setAttribute("class", "container-fluid");
		if (document.getElementById(containerID)) { 
			wrapperContainer.style.padding = 0;
		}
		_getContainerElement().appendChild(wrapperContainer);
		if (layout.rows) {
			for (var i = 0; i < layout.rows.length; i++) {
				_buildBSLayout(layout.rows[i], wrapperContainer);
			}
		}
	}
	if (!layout.type) {
		return;
	}
	
	if (layout.type === "row" || layout.type === "JSONLayoutRow") {
		var row = document.createElement("div");
		var rowClass = "row";
		if (layout.additionalClasses) {
			rowClass += " " + layout.additionalClasses.join(" ");
		}
		row.setAttribute("class", rowClass);
		if (layout.additionalStyles) {
			row.setAttribute("style", layout.additionalStyles.join(" "));
		}
		row.setAttribute("id", layout.id);
		parent.appendChild(row);
		if (!layout.columns) {
			return;
		}
		for (var i = 0; i < layout.columns.length; i++) {
			var col = layout.columns[i];
			var cEl = document.createElement("div");
			if (!col.content) {
				var spaceBox = document.createElement("div");
				var height;
				if (parentRectangleHeight === null) {
					// height = ( $(window).height() - (62 + 61 + 4 * 48) ) / splitNumber;
					height = ( $(window).height() - (30 + 3 * 48) ) / splitNumber;
				} else {
					if (!splitHorizontalDone) {
						console.log('splitHorizontalDone: ' + splitHorizontalDone + '\n');
						height = parentRectangleHeight / splitNumber;
					} else {
						console.log('splitHorizontalDone: ' + splitHorizontalDone + '\n');
						height = parentRectangleHeight;
					}	
				}
				// spaceBox.setAttribute("style", "height: " + height + "px;")
				spaceBox.setAttribute("style", "height: " + height + "px;")
				cEl.appendChild(spaceBox);
			}
			var wString = "";
			if (col.widthXS > 0 && col.widthXS <= 12) {
				wString += "col-xs-" + col.widthXS + " ";
			}
			if (col.widthSM > 0 && col.widthSM <= 12) {
				wString += "col-sm-" + col.widthSM + " ";
			}
			if (col.widthMD > 0 && col.widthMD <= 12) {
				wString += "col-md-" + col.widthMD + " ";
			}
			if (col.widthLG > 0 && col.widthLG <= 12) {
				wString += "col-lg-" + col.widthLG + " ";
			}
			if (col.widthXL > 0 && col.widthXL <= 12) {
				wString += "col-xl-" + col.widthXL;
			}
			if (wString.length == 0) {
				wString = "col-md-12";
			}
			if (col.additionalClasses) {
				wString += " " + col.additionalClasses.join(" ");
			}
			wString += " rectangle new";
 			cEl.setAttribute("class", wString);
			if (col.additionalStyles) {
				cEl.setAttribute("style", col.additionalStyles.join(" "));
			}
			cEl.setAttribute("id", col.id);
			row.appendChild(cEl);
			if (col.content) {
				splitHorizontalDone = false;
				for (var j = 0; j < col.content.length; j++) {
					_buildBSLayout(col.content[j], cEl);
					splitHorizontalDone = false;
				}
				splitHorizontalDone = true;
			}
		}

	} else if (layout.type === "view" || layout.type === "JSONLayoutViewContent") {
		var wn = webNodes[layout.nodeID];
		if (!wn || (wn.nodeInfo && !wn.nodeInfo.displayPossible)) {
			//don't create iframe for missing or not displayable nodes
			// parent.id = "node" + layout.nodeID.replace(/:/g, "-");
			return;
		}
		
		// for aspect ratio set sizes, set appropriate classes on parent
		if (layout.resizeMethod.substring( 0, "aspectRatio".length ) === "aspectRatio") {
			var embed = document.createElement("div");
			var cString = "embed-responsive";
			if (layout.resizeMethod === "aspectRatio16by9") {
				cString += " embed-responsive-16by9";
			} else if (layout.resizeMethod === "aspectRatio4by3") {
				cString += " embed-responsive-4by3";
			}
			embed.setAttribute("class", cString);
			parent.appendChild(embed);
			parent = embed;
		}
		
		// Create iframe
		var frame = document.createElement('iframe');
		if (layout.additionalStyles) {
			frame.setAttribute("style", layout.additionalStyles.join(" "));
		}
		if (!frame.style.border) {
			frame.style.border = "none"; /*"thin solid darkgrey"*/
		}
		if (!frame.style.backgroundColor) {
			frame.style.backgroundColor = "white";
		}
		frame.style.display = "block";
		frame.style.maxWidth = "100%";
		frame.setAttribute("width", "100%");
		frame.setAttribute("allowfullscreen", "");
		frame.id = "node" + layout.nodeID.replace(/:/g, "-");
		var frameClass = "";
		if (layout.additionalClasses) {
			frameClass += layout.additionalClasses.join(" ");
		}
		if (layout.resizeMethod.substring( 0, "view".length ) === "view") {
			frame.setAttribute("class", frameClass + "resizable-frame");
			var method = layout.resizeMethod.substring(4, 5).toLowerCase() + layout.resizeMethod.substring(5);
			if (method === "lowestElementIEMax") {
				var isOldIE = (navigator.userAgent.indexOf("MSIE") !== -1);
				method = isOldIE ? 'max' : 'lowestElement';
			}
			var layoutSettings = {
					log : isDebug,
					enablePublicMethods : true,
					checkOrigin : false,
					resizeFrom : "child",
					
					autoResize : layout.autoResize,
					scrolling : layout.scrolling,
					heightCalculationMethod : method,
					sizeHeight : layout.sizeHeight,
					sizeWidth : layout.sizeWidth
			}
			if (layout.minWidth) {
				layoutSettings.minWidth = layout.minWidth;
			}
			if (layout.maxWidth) {
				layoutSettings.maxWidth = layout.maxWidth;
			}
			if (layout.minHeight) {
				layoutSettings.minHeight = layout.minHeight;
			}
			if (layout.maxHeight) {
				layoutSettings.maxHeight = layout.maxHeight;
			}
			if (layout.resizeInterval) {
				layoutSettings.interval = layout.resizeInterval;
			}
			if (layout.resizeTolerance) {
				layoutSettings.tolerance = layout.resizeTolerance;
			}
			layoutValues[frame.id] = layoutSettings;
		} else if (layout.resizeMethod.substring( 0, "aspectRatio".length ) === "aspectRatio") {
			frame.setAttribute("class", frameClass + "embed-responsive-item");
		} else if (layout.resizeMethod === "manual") {
			manualSizing[frame.id] = true;
		}

		// Add iframe to cell
		parent.appendChild(frame);

		//Adding some informations on the inserted node along with an image (node logo)
		var frameBody = $('#' + frame.id).contents().find('body');
		h3 = document.createElement('h3');
		h3.innerHTML = webNodes[layout.nodeID].nodeType;
		frameBody.append(h3);
		h3 = document.createElement('h3');
		h3.innerHTML = 'Node ' + layout.nodeID;
		frameBody.append(h3);

	} else  if (layout.type === "html" || layout.type === "JSONLayoutHTMLContent") {
		parent.innerHTML = layout.value;
	}
}
