/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 */

/**
 * Provides a common functionality for table-like views such as Table View, Table Editor, Data Explorer.
 *
 * @constructor
 */
KnimeBaseTableViewer = function() {
	// read-only settings coming from backend
	this._representation = null;
	// read-and-write settings which come from backend and send back
	this._value = null;
	// table data in internal format
	this._knimeTable = null;
	// DataTables object to visualize the table
	this._dataTable = null;
	// info of selected rows
	this._selection = {};
	// ids of partially selected rows
	this._partialSelectedRows = [];
	//var allCheckboxes = [];
	// currently applied filter
	this._currentFilter = null;
	// whether the view has been initialized
	this._initialized = false;
	// config object for DataTables
	this._dataTableConfig = null;
	// count of info columns (row id, color etc.)
	this._infoColsCount = 0;
	// count of non-selectable columns (row selection)
	this._nonSelectableColsCount = 0;
	// index of the RowID column
	this._rowIdColInd = null;
	// Map dataTable column indexes to knime table indexes.
	this._nonHiddenDataIndexes = [];

	// register neutral ordering method for clear selection button
	$.fn.dataTable.Api.register('order.neutral()', function () {
	    return this.iterator('table', function (s) {
	        s.aaSorting.length = 0;
	        s.aiDisplay.sort( function (a,b) {
	            return a-b;
	        });
	        s.aiDisplayMaster.sort( function (a,b) {
	            return a-b;
	        } );
	    } );
	});
}

/**
 * Initialize the table viewer and draw the view. Framework method.
 *
 * @param representation  the representation input object from the backend
 * @param value  the value input object from the backend
 */
KnimeBaseTableViewer.prototype.init = function(representation, value) {
	if (!representation || !representation.table || !value) {
		$('body').append("<p>Error: No data available</p>");
		return;
	}
	this._representation = representation;
	this._value = value;

	if (parent && parent.KnimePageLoader) {
		this._drawTable();
	} else {
		var self = this;
		$(document).ready(function() {
			self._drawTable();
		});
	}
}

/**
 * Validates the view state. Framework method.
 */
KnimeBaseTableViewer.prototype.validate = function() {
	return true;
}

/**
 * Fills the value output object for the backend. Framework method.
 */
KnimeBaseTableViewer.prototype.getComponentValue = function() {
	if (!this._value) {
		return null;
	}
	this._value.selection = [];
	for (var id in this._selection) {
		if (this._selection[id]) {
			this._value.selection.push(id);
		}
	}
	if (this._value.selection.length == 0) {
		this._value.selection = null;
	}
	var pageNumber = this._dataTable.page();
	if (pageNumber > 0) {
		this._value.currentPage = pageNumber;
	}
	var pageSize = this._dataTable.page.len();
	if (pageSize != this._representation.initialPageSize) {
		this._value.pageSize = pageSize;
	}
	var searchString = this._dataTable.search();
	if (searchString.length) {
		this._value.filterString = searchString;
	}
	var order = this._dataTable.order();
	if (order.length > 0) {
		this._value.currentOrder = order;
	}
	if (this._representation.enableColumnSearching) {
		this._value.columnFilterStrings = [];
		var filtered = false;
		var self = this;
		this._dataTable.columns().every(function (index) {
	        var input = $('input', this.footer());
	        if (input.length) {
	        	var filterString = input.val();
	        	self._value.columnFilterStrings.push(filterString);
	        	filtered |= filterString.length;
	        } else {
	        	self._value.columnFilterStrings.push("");
	        }
	    });
		if (!filtered) {
			this._value.columnFilterStrings = null;
		}
	}
	var selSub = document.getElementById('subscribeSelectionCheckbox');
	if (selSub) {
		this._value.subscribeSelection = selSub.checked;
	}
	return this._value;
}

/**
 * Get dataTable index from column index
 * @param {Number} colIndex
 * @return {Number|undefined} data index of the given column. Can be undefined if the column index of synthesized
 *     columns like RowID or Selection Checkbox Column is passed.
 */
KnimeBaseTableViewer.prototype._dataIndexFromColIndex = function(colIndex) {
	return this._nonHiddenDataIndexes[colIndex - this._infoColsCount];
};

/**
 * Main function to draw the table view
 */
KnimeBaseTableViewer.prototype._drawTable = function() {
	try {
		this._prepare();
		this._createHtmlTableContainer();

		this._knimeTable = new kt();
		this._knimeTable.setDataTable(this._representation.table);

		this._buildColumnSearching();

		this._buildDataTableConfig();
		this._dataTable = $('#knimePagedTable').DataTable(this._dataTableConfig);

		this._addTableListeners();

		this._addSortButtons();

		$('#knimePagedTable_paginate').css('display', 'none');

		$('#knimePagedTable_info').html(
			'<strong>Loading data</strong> - Displaying '
			+ 1 + ' to ' + Math.min(this._knimeTable.getNumRows(), this._representation.initialPageSize)
			+ ' of ' + this._knimeTable.getNumRows() + ' entries.');

		this._buildMenu();
		this._setSelectionHandlers();
		this._processColumnSearching();

		this._setControlCssStyles();

		// load all data
		var self = this;
		setTimeout(function() {
			var initialChunkSize = 100;
			self._addDataToTable(self._representation.initialPageSize, initialChunkSize);
		}, 0);

	} catch (err) {
		if (err.stack) {
			alert(err.stack);
		} else {
			alert (err);
		}
	}
}

/**
 * Actions which have to be done before building the table
 */
KnimeBaseTableViewer.prototype._prepare = function() {
	// Set locale for moment.js.
	if (this._representation.dateTimeFormats.globalDateTimeLocale !== 'en') {
		moment.locale(this._representation.dateTimeFormats.globalDateTimeLocale);
	}

	// Set selected rows
	if (this._representation.enableSelection && this._value.selection) {
		for (var i = 0; i < this._value.selection.length; i++) {
			this._selection[this._value.selection[i]] = true;
		}
	}
}


/**
 * Creates an HTML wrapper for the table view
 */
KnimeBaseTableViewer.prototype._createHtmlTableContainer = function() {
	var body = $('body');
	var wrapper = $('<div id="knimePagedTableContainer" class="knime-table-container" data-iframe-height data-iframe-width>');
	body.append(wrapper);
	if (this._representation.title != null && this._representation.title != '') {
		wrapper.append('<h1 class="knime-title">' + this._representation.title + '</h1>')
	}
	if (this._representation.subtitle != null && this._representation.subtitle != '') {
		wrapper.append('<h2 class="knime-subtitle">' + this._representation.subtitle + '</h2>')
	}
	var table = $('<table id="knimePagedTable" class="table table-striped table-bordered knime-table" width="100%">');
	wrapper.append(table);
}

/**
 * Builds controls for searching through the columns
 */
KnimeBaseTableViewer.prototype._buildColumnSearching = function() {
	if (this._representation.enableColumnSearching) {
		$('#knimePagedTable').append('<tfoot class="knime-table-footer"><tr class="knime-table-row knime-table-footer"></tr></tfoot>');
		var footerRow = $('#knimePagedTable tfoot tr');
		if (this._representation.enableSelection) {
			footerRow.append('<th class="knime-table-cell knime-table-footer"></th>');
		}
		if (this._representation.displayRowIndex) {
			footerRow.append('<th class="knime-table-cell knime-table-footer"></th>');
		}
		if (this._representation.displayRowColors || this._representation.displayRowIds) {
			footerRow.append('<th class="knime-table-cell knime-table-footer"></th>');
		}
		for (var i = 0; i < this._knimeTable.getColumnNames().length; i++) {
			if (this._knimeTable.isColumnHidden(i)) {
				continue;
			}
			if (this._isColumnSearchable(this._knimeTable.getColumnTypes()[i])) {
				footerRow.append('<th class="knime-table-cell knime-table-footer">' + this._knimeTable.getColumnNames()[i] + '</th>')
			} else {
				footerRow.append('<th class="knime-table-cell knime-table-footer"></th>');
			}
		}

		$('#knimePagedTable tfoot th').each(function() {
	        var title = $(this).text();
	        if (title == '') {
	        	return;
	        }
	        $(this).html('<input class="knime-table-control-text knime-filter knime-single-line" type="text" placeholder="Search ' + title + '" />' );
	    });
	}
}

/**
 * Configure controls for rows selection
 */
KnimeBaseTableViewer.prototype._buildSelection = function() {
	var self = this;
	if (this._representation.enableSelection) {
		if (this._representation.singleSelection) {
			var titleElement = this._representation.enableClearSelectionButton
				? ('<button type="button" id="clear-selection-button" class="btn btn-default btn-xs knime-control-text" title="Clear selection">'
					+ '<span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span></button>')
				: '';
			this._dataTableConfig.columns.push({
				'title': titleElement,
				'searchable': false,
				'orderable': false,
				'className': 'dt-body-center selection-cell knime-table-cell',
				'render': function (data, type, full, meta) {
					return '<input type="radio" class="knime-boolean" name="radio_single_select"'
					+ (self._selection[data] ? ' checked' : '')
					+' value="' + $('<div/>').text(data).html() + '">';
				}
			});
		} else {
			var all = this._value.selectAll;
			this._dataTableConfig.columns.push({
				'title': '<input name="select_all" value="1" id="checkbox-select-all" type="checkbox" class="knime-boolean"' + (all ? ' checked' : '')  + ' />',
				'searchable': false,
				'orderable': false,
				'className': 'dt-body-center selection-cell knime-table-cell',
				'render': function (data, type, full, meta) {
					//var selected = selection[data] ? !all : all;
					setTimeout(function(){
						var el = $('#checkbox-select-all').get(0);
						/*if (all && selection[data] && el && ('indeterminate' in el)) {
						el.indeterminate = true;
					}*/
					}, 0);
					return '<input type="checkbox" class="knime-boolean" name="id[]"'
					+ (self._selection[data] ? ' checked' : '')
					+' value="' + $('<div/>').text(data).html() + '">';
				}
			});
		}
		this._infoColsCount++;
		this._nonSelectableColsCount++;
	}
}

/**
 * Configure display of rows indices, ids and colors
 */
KnimeBaseTableViewer.prototype._buildRowComponents = function() {
	if (this._representation.displayRowIndex) {
		this._dataTableConfig.columns.push({
			'title': "Row Index",
			'searchable': false,
			'className': 'knime-table-cell'
		});
		this._infoColsCount++;
	}

	if (this._representation.displayRowIds || this._representation.displayRowColors) {
		var title = this._representation.displayRowIds ? 'RowID' : '';
		var orderable = this._representation.displayRowIds;
		this._dataTableConfig.columns.push({
			'title': title,
			'orderable': orderable,
			'className': 'no-break knime-table-cell'
		});
		this._rowIdColInd = this._infoColsCount;
		this._infoColsCount++;
	}
};

/**
 * Configures columns for DataTables
 */
KnimeBaseTableViewer.prototype._buildColumnDefinitions = function() {
	var self = this;
	var columnNames = this._knimeTable.getColumnNames();
	for (var i = 0; i < columnNames.length; i++) {
		if (this._knimeTable.isColumnHidden(i)) {
			continue;
		}
		var title = columnNames[i];
		var colType = this._knimeTable.getColumnTypes()[i];
		var knimeColType = this._knimeTable.getKnimeColumnTypes()[i];

		var colDef = {
			'title': title,
			'orderable' : this._isColumnSortable(colType),
			'searchable': this._isColumnSearchable(colType),
			'className': 'knime-table-cell'
		}
		if (this._representation.displayMissingValueAsQuestionMark) {
			colDef.defaultContent = '<span class="knime-missing-value-cell">?</span>';
		}
		if (knimeColType == 'Date and Time' && this._representation.dateTimeFormats.globalDateTimeFormat) {
			colDef.render = function (data, type, full, meta) {
				// Check if date is given as ISO-string or time stamp (legacy).
				if (isNaN(data)) {
					// ISO-string:
					// date is parsed and rendered in local time.
					return moment(data).format(type === 'sort' || type === 'type' ? 'x' : self._representation.dateTimeFormats.globalDateTimeFormat);
				} else {
					// time stamp (legacy):
					// date is parsed and rendered in UTC.
					return moment(data).utc().format(type === 'sort' || type === 'type' ? 'x' : self._representation.dateTimeFormats.globalDateTimeFormat);
				}
			}
			colDef.className += ' knime-datetime';
		}
		if (knimeColType == 'Local Date' && this._representation.dateTimeFormats.globalLocalDateFormat) {
			colDef.render = function (data, type, full, meta) {
				return moment(data).format(type === 'sort' || type === 'type' ? 'x' : self._representation.dateTimeFormats.globalLocalDateFormat);
			}
			colDef.className += ' knime-datetime knime-date';
		}

		if (knimeColType == 'Local Date Time' && this._representation.dateTimeFormats.globalLocalDateTimeFormat) {
			colDef.render = function (data, type, full, meta) {
				return moment(data).format(type === 'sort' || type === 'type' ? 'x' : self._representation.dateTimeFormats.globalLocalDateTimeFormat);
			}
			colDef.className += ' knime-datetime';
		}

		if (knimeColType == 'Local Time' && this._representation.dateTimeFormats.globalLocalTimeFormat) {
			colDef.render = function (data, type, full, meta) {
				return moment(data, "hh:mm:ss.SSSSSSSSS").format(type === 'sort' || type === 'type' ? 'x' : self._representation.dateTimeFormats.globalLocalTimeFormat);
			}
			colDef.className += ' knime-datetime knime-time';
		}

		if (knimeColType == 'Zoned Date Time' && this._representation.dateTimeFormats.globalZonedDateTimeFormat) {
			colDef.render = function (data, type, full, meta) {
				var regex = /(.*)\[(.*)\]$/
				var match = regex.exec(data);

				if (match == null) {
					var date = moment.tz(data, "");
				} else {
					dateTimeOffset = match[1];
					zone = match[2];

					if (moment.tz.zone(zone) == null) {
						var date = moment.tz(dateTimeOffset, "");
					} else {
						var date = moment.tz(dateTimeOffset, zone);
					}
				}

				return date.format(type === 'sort' || type === 'type' ? 'x' : self._representation.dateTimeFormats.globalZonedDateTimeFormat);
			}
			colDef.className += ' knime-datetime knime-timezone';
		}
		if (colType == 'number') {
			if (this._knimeTable.getKnimeColumnTypes()[i].indexOf('double') > -1) {
				if (this._representation.enableGlobalNumberFormat) {
					colDef.render = function (data, type, full, meta) {
						if (!$.isNumeric(data)) {
							return data;
						}
						return Number(data).toFixed(self._representation.globalNumberFormatDecimals);
					}
				}
				colDef.className += ' knime-double';
			} else {
				colDef.className += ' knime-integer';
			}
		}
		if (colType == 'png') {
			colDef.render = function (data, type, full, meta) {
				return '<img src="data:image/png;base64,' + data + '" />';
			}
			colDef.className += ' knime-image knime-png';
		}
		if (colType == 'svg') {
			colDef.className += ' knime-image knime-svg';
		}
		if (colType == 'boolean') {
			colDef.className += ' knime-boolean';
		}
		if (colType == 'string') {
			colDef.className += ' knime-string';
		}

		this._dataTableConfig.columns.push(colDef);
		this._nonHiddenDataIndexes.push(i);
	}
}

/**
 * Callback function after the table has been drawn
 */
KnimeBaseTableViewer.prototype._dataTableDrawCallback = function() {
	var self = this;
	if (!this._representation.displayColumnHeaders) {
		$("#knimePagedTable thead").remove();
  	}
	if (this._dataTableConfig.searching && !this._representation.enableSearching) {
		$('#knimePagedTable_filter').remove();
	}
	if (this._dataTable) {
		this._curCells = this._dataTable.cells(function(ind) {
			return ind.column >= self._nonSelectableColsCount;
		}, {page: 'current'}).nodes().flatten().to$();
		this._curCells.on('mousedown', this._bindCellMouseDownHandler = this._cellMouseDownHandler.bind(this));
	}
	this._setDynamicCssStyles();
}

/**
 * Callback function before the table has been drawn
 */
KnimeBaseTableViewer.prototype._dataTablePreDrawCallback = function() {
	if (this._dataTable) {
		if (this._curCells) {
			// for uknown reason even in preDraw current page contains already new cells,
			// therefore to clean the old page, we need to save them in a separate variable
			this._curCells.off('mousedown', this._bindCellMouseDownHandler);
		}
		this._clearSelection();
	}
}

/**
 * Builds a config object for DataTables
 */
KnimeBaseTableViewer.prototype._buildDataTableConfig = function() {
	this._dataTableConfig = {
		'columns': [],
		'columnDefs': [],
		'order': [],
		'paging': this._representation.enablePaging,
		'pageLength': this._representation.initialPageSize,
		'lengthMenu': this._representation.allowedPageSizes,
		'lengthChange': this._representation.enablePageSizeChange,
		'ordering': this._representation.enableSorting,
		'processing': true,
		'deferRender': !this._representation.enableSelection,
		'buttons': [],
		'fnDrawCallback': this._dataTableDrawCallback.bind(this),
		'preDrawCallback': this._dataTablePreDrawCallback.bind(this),
		'select': {
			items: 'cell',
			style: 'api',
			info: false
		}
	};

	this._buildSelection();
	this._buildRowComponents();
	this._buildColumnDefinitions();

	if (this._value.pageSize) {
		this._dataTableConfig.pageLength = this._value.pageSize;
	}

	if (this._representation.pageSizeShowAll) {
		var first = this._dataTableConfig.lengthMenu.slice(0);
		first.push(-1);
		var second = this._dataTableConfig.lengthMenu.slice(0);
		second.push("All");
		this._dataTableConfig.lengthMenu = [first, second];
	}

	if (this._value.currentOrder) {
		this._dataTableConfig.order = this._value.currentOrder;
	}

	if (this._representation.enableSorting && this._representation.enableClearSortButton) {
		var unsortButton = {
				'text': "Clear Sorting",
				'action': function (e, dt, node, config) {
					dt.order.neutral();
					dt.draw();
				},
				'enabled': (this._dataTableConfig.order.length > 0)
		}
		this._dataTableConfig.buttons.push(unsortButton);
	}

	this._dataTableConfig.data = this._getDataSlice(0, this._representation.initialPageSize);  // load only the first chunk

	// search is also used for filtering, so consider all possible options
	this._dataTableConfig.searching = this._representation.enableSearching || this._representation.enableColumnSearching
		|| (this._representation.enableSelection && (this._value.hideUnselected || this._representation.enableHideUnselected))
		|| (knimeService && knimeService.isInteractivityAvailable());
}

/**
 * Registers table listeners
 */
KnimeBaseTableViewer.prototype._addTableListeners = function() {
	var $table = $('#knimePagedTable');
	$table.attr('tabindex', 0);
	$table.on('keydown', this._keyDownHandler.bind(this));
	// we need to listen to copy event on body level as user-select:none prevents copy event
	// see more at https://stackoverflow.com/questions/45627286/disable-text-selection-but-allow-cut-copy-and-paste
	$(document.body).on('copy', this._copyHandler.bind(this));
}

/**
 * Adds controls for sorting buttons
 */
KnimeBaseTableViewer.prototype._addSortButtons = function() {
	// Clear sorting button placement and enable/disable on order change
	if (this._representation.enableSorting && this._representation.enableClearSortButton) {
		this._dataTable.buttons().container().appendTo('#knimePagedTable_wrapper .col-sm-6:eq(0)');
		$('#knimePagedTable_length').css({'display': 'inline-block', 'margin-right': '10px'});
		var self = this;
		this._dataTable.on('order.dt', function () {
			var order = self._dataTable.order();
			self._dataTable.button(0).enable(order.length > 0);
		});
	}
}

/**
 * Builds the view menu
 */
KnimeBaseTableViewer.prototype._buildMenu = function() {
	var self = this;
	if (knimeService) {
	    var paging = this._representation.enablePaging;
	    var sizeChange = this._representation.enablePageSizeChange;
	    var searching = this._representation.enableSearching;
	    var title = this._representation.title;
	    var hasTitle = typeof title !== 'undefined' && title !== null && title !== '';
	    var subtitle = this._representation.subtitle;
	    var hasSubtitle = typeof subtitle !== 'undefined' && subtitle !== null && subtitle !== '';
	    
		if ((searching && !hasTitle) || (!hasTitle && !hasSubtitle && !searching && (!paging || !sizeChange))) {
			knimeService.floatingHeader(false);
		}

		if (this._representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}

		if (this._representation.enableSelection) {
			$.fn.dataTable.ext.search.push(function(settings, searchData, index, rowData, counter) {
				if (self._value.hideUnselected) {
					return self._selection[rowData[0]] || self._partialSelectedRows.indexOf(rowData[0]) > -1;
				}
				return true;
			});
			if (this._representation.enableHideUnselected && !this._representation.singleSelection) {
				var hideUnselectedCheckbox = knimeService.createMenuCheckbox('showSelectedOnlyCheckbox', this._value.hideUnselected, function() {
					var prev = self._value.hideUnselected;
					self._value.hideUnselected = this.checked;
					if (prev !== self._value.hideUnselected) {
						self._dataTable.draw();
					}
				});
				knimeService.addMenuItem('Show selected rows only', 'filter', hideUnselectedCheckbox);
				if (knimeService.isInteractivityAvailable()) {
					knimeService.addMenuDivider();
				}
			}
		}

		if (knimeService.isInteractivityAvailable()) {
			if (this._representation.enableSelection) {
				var pubSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
				var pubSelCheckbox = knimeService.createMenuCheckbox('publishSelectionCheckbox', this._value.publishSelection, function() {
					if (this.checked) {
						self._value.publishSelection = true;
						self._publishCurrentSelection();
					} else {
						self._value.publishSelection = false;
					}
				});
				knimeService.addMenuItem('Publish selection', pubSelIcon, pubSelCheckbox);
				if (this._value.publishSelection && this._selection && Object.keys(this._selection).length > 0) {
					this._publishCurrentSelection();
				}
				if (!this._representation.singleSelection) {
					var subSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold');
					var subSelCheckbox = knimeService.createMenuCheckbox('subscribeSelectionCheckbox', this._value.subscribeSelection, function() {
						if (this.checked) {
							knimeService.subscribeToSelection(self._representation.table.id, self._selectionChanged.bind(self));
						} else {
							knimeService.unsubscribeSelection(self._representation.table.id, self._selectionChanged.bind(self));
						}
					});
					knimeService.addMenuItem('Subscribe to selection', subSelIcon, subSelCheckbox);
					if (this._value.subscribeSelection) {
						knimeService.subscribeToSelection(this._representation.table.id, this._selectionChanged.bind(this));
					}
				}
			}
			if (this._representation.subscriptionFilterIds && this._representation.subscriptionFilterIds.length > 0) {
				if (this._representation.enableSelection) {
					knimeService.addMenuDivider();
				}

				/*var pubFilIcon = knimeService.createStackedIcon('filter', 'angle-right', 'faded left sm', 'right bold');
				var pubFilCheckbox = knimeService.createMenuCheckbox('publishFilterCheckbox', _value.publishFilter, function() {
					if (this.checked) {
						//publishFilter = true;
					} else {
						//publishFilter = false;
					}
				});
				knimeService.addMenuItem('Publish filter', pubFilIcon, pubFilCheckbox);
				if (_value.publishFilter) {
					//TODO
				}*/
				$.fn.dataTable.ext.search.push(function(settings, searchData, index, rowData, counter) {
					if (self._currentFilter) {
						return self._knimeTable.isRowIncludedInFilter(index, self._currentFilter);
					}
					return true;
				});
				var boundFilterChanged = self._filterChanged.bind(self);
				var subFilIcon = knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold');
				var subFilCheckbox = knimeService.createMenuCheckbox('subscribeFilterCheckbox', this._value.subscribeFilter, function() {
					if (this.checked) {
						knimeService.subscribeToFilter(self._representation.table.id, boundFilterChanged, self._representation.subscriptionFilterIds);
					} else {
						knimeService.unsubscribeFilter(self._representation.table.id, boundFilterChanged);
					}
				});
				knimeService.addMenuItem('Subscribe to filter', subFilIcon, subFilCheckbox);
				if (this._value.subscribeFilter) {
					knimeService.subscribeToFilter(this._representation.table.id, boundFilterChanged, this._representation.subscriptionFilterIds);
				}
			}
		}
	}
}

/**
 * Sets rows selection handlers
 */
KnimeBaseTableViewer.prototype._setSelectionHandlers = function() {
	if (!this._representation.enableSelection) {
		return;
	}
	var self = this;
	if (this._representation.singleSelection) {
		// Handle click on clear selection button
		var clearSelectionButton = $('#clear-selection-button').get(0);
		if (clearSelectionButton) {
			clearSelectionButton.addEventListener('click', function() {
				self._selectAll(false);
			});
		}
		// Handle click on radio button to set selection and publish event
		$('#knimePagedTable tbody').on('change', 'input[type="radio"]', function() {
			$('.selection-cell').parent().removeClass('knime-selected');  // set tr style
			self._selection = {};
			self._selection[this.value] = this.checked;
			if (this.checked) {
				$(this).parent().parent().addClass('knime-selected');  // set tr style
			}
			if (knimeService && knimeService.isInteractivityAvailable() && self._value.publishSelection) {
				if (this.checked) {
					knimeService.setSelectedRows(self._representation.table.id, [this.value], self._selectionChanged.bind(self));
				}
			}
		});
	} else {
		// Handle click on "Select all" control
		var selectAllCheckbox = $('#checkbox-select-all').get(0);
		if (selectAllCheckbox) {
			if (selectAllCheckbox.checked && ('indeterminate' in selectAllCheckbox)) {
				selectAllCheckbox.indeterminate = this._value.selectAllIndeterminate;
			}
			selectAllCheckbox.addEventListener('click', function() {
				self._selectAll(this.checked);
			});
		}

		// Handle click on checkbox to set state of "Select all" control
		$('#knimePagedTable tbody').on('change', 'input[type="checkbox"]', function() {
			//var el = $('#checkbox-select-all').get(0);
			//var selected = el.checked ? !this.checked : this.checked;
			// we could call delete _value.selection[this.value], but the call is very slow
			// and we can assume that a user doesn't click on a lot of checkboxes
			self._selection[this.value] = this.checked;
			// in either case the row is not partially selected
			var partialIndex = self._partialSelectedRows.indexOf(this.value);
			if (partialIndex > -1) {
				self._partialSelectedRows.splice(partialIndex, 1);
			}

			if (this.checked) {
				if (knimeService && knimeService.isInteractivityAvailable() && self._value.publishSelection) {
					knimeService.addRowsToSelection(self._representation.table.id, [this.value], self._selectionChanged.bind(self));
				}
				$(this).parent().parent().addClass('knime-selected');  // set tr style
			} else {
				if (self._value.hideUnselected) {
					self._dataTable.draw('full-hold');
				}
				if (knimeService && knimeService.isInteractivityAvailable() && self._value.publishSelection) {
					knimeService.removeRowsFromSelection(self._representation.table.id, [this.value], self._selectionChanged.bind(self));
				}
				$(this).parent().parent().removeClass('knime-selected');  // set tr style
			}
			self._checkSelectAllState();
		});
		if (knimeService && this._representation.enableClearSelectionButton) {
			knimeService.addButton('pagedTableClearSelectionButton', 'minus-square-o', 'Clear Selection', function() {
				self._selectAll(false, true);
			});
		}
		this._dataTable.on('search.dt', function () {
			self._checkSelectAllState();
		});
	}
	self._dataTable.on('draw.dt', function () {
		self._setSelectionOnPage();
	});
};

/**
 * Adds a handler for column searching and processes the event
 */
KnimeBaseTableViewer.prototype._processColumnSearching = function() {
	if (this._representation.enableColumnSearching) {
		this._dataTable.columns().every(function () {
	        var that = this;
	        $('input', this.footer()).on('keyup change', function () {
	            if (that.search() !== this.value) {
	                that.search(this.value).draw();
	            }
	        });
	    });
	}
}

/**
 * Loads data into the table in chunks until everything is loaded
 *
 * @param startIndex  index of the first row in the chunk
 * @param chunkSize  number of rows in a chunk
 */
KnimeBaseTableViewer.prototype._addDataToTable = function(startIndex, chunkSize) {
	var startTime = new Date().getTime();
	var tableSize = this._knimeTable.getNumRows();
	var endIndex  = Math.min(tableSize, startIndex + chunkSize);
	var chunk = this._getDataSlice(startIndex, endIndex);
	this._dataTable.rows.add(chunk);
	var endTime = new Date().getTime();
	var chunkDuration = endTime - startTime;
	var newChunkSize = chunkSize;
	if (startIndex + chunkSize < tableSize) {
		$('#knimePagedTable_info').html(
			'<strong>Loading data ('
			+ endIndex + ' of ' + tableSize + ' records)</strong> - Displaying '
			+ 1 + ' to ' + Math.min(tableSize, this._representation.initialPageSize)
			+ ' of ' + tableSize + ' entries.');
		if (chunkDuration > 300) {
			newChunkSize = Math.max(1, Math.floor(chunkSize / 2));
		} else if (chunkDuration < 100) {
			newChunkSize = chunkSize * 2;
		}
		var self = this;
		setTimeout((function(i, c) {
			return function() {
				self._addDataToTable(i, c);
			};
		})(startIndex + chunkSize, newChunkSize), chunkDuration);
	} else {
		$('#knimePagedTable_paginate').css('display', 'block');
		this._applyViewValue();
		this._dataTable.draw();
		this._finishInit();
	}
}

/**
 * Gets the rows whose indices are in the interval [start, end)
 */
KnimeBaseTableViewer.prototype._getDataSlice = function(start, end) {
	if (typeof end == 'undefined') {
		end = this._knimeTable.getNumRows();
	}
	var data = [];
	for (var i = start; i < Math.min(end, this._knimeTable.getNumRows()); i++) {
		var row = this._knimeTable.getRows()[i];
		var dataRow = [];
		if (this._representation.enableSelection) {
			dataRow.push(row.rowKey);
		}
		if (this._representation.displayRowIndex) {
			dataRow.push(i);
		}
		if (this._representation.displayRowIds || this._representation.displayRowColors) {
			var string = '';
			if (this._representation.displayRowColors) {
				string += '<div class="knimeTableRowColor" style="background-color: '
						+ this._knimeTable.getRowColors()[i]
						+ '; width: 16px; height: 16px; '
						+ 'display: inline-block; margin-right: 5px; vertical-align: text-bottom;"></div>'
			}
			if (this._representation.displayRowIds) {
				string += '<span class="rowKey">' + row.rowKey + '</span>';
			}
			dataRow.push(string);
		}
		var unfilteredData = [];
		for (var j = 0; j < row.data.length; j++) {
			if (!this._knimeTable.isColumnHidden(j)) {
				unfilteredData.push(row.data[j]);
			}
		}
		dataRow = dataRow.concat(unfilteredData);
		data.push(dataRow);
	}
	return data;
}

/**
 * Applies the existing settings from the value
 */
KnimeBaseTableViewer.prototype._applyViewValue = function() {
	if (this._representation.enableSearching && this._value.filterString) {
		this._dataTable.search(this._value.filterString);
	}
	if (this._representation.enableColumnSearching && this._value.columnFilterStrings) {
		for (var i = 0; i < this._value.columnFilterStrings.length; i++) {
			var curValue = this._value.columnFilterStrings[i];
			if (curValue.length > 0) {
				var column = this._dataTable.column(i);
				$('input', column.footer()).val(curValue);
				column.search(curValue);
			}
		}
	}
	if (this._representation.enablePaging && this._value.currentPage) {
		var self = this;
		setTimeout(function() {
			self._dataTable.page(self._value.currentPage).draw('page');
		}, 0);
	}
}

/**
 * Actions which have to be done after the table has been initialized
 */
KnimeBaseTableViewer.prototype._finishInit = function() {
	//Used to collect all checkboxes here,
	//but now keeping selection and checkbox state separate and applying checked state on every call of draw()
	/*allCheckboxes = dataTable.column(0).nodes().to$().children();*/
	this._initialized = true;
}

/**
 * Processes the 'select all rows' action
 *
 * @param all
 * @param ignoreSearch
 */
KnimeBaseTableViewer.prototype._selectAll = function(all, ignoreSearch) {
	// cannot select all rows before all data is loaded
	if (!this._initialized) {
		var self = this;
		setTimeout(function() {
			self._selectAll(all);
		}, 500);
	}

	if (ignoreSearch) {
		this._selection = {};
		this._partialSelectedRows = [];
	}
	if (all || !ignoreSearch) {
		var selIndices = this._dataTable.column(0, { 'search': 'applied' }).data();
		for (var i = 0; i < selIndices.length; i++) {
			this._selection[selIndices[i]] = all;
			var pIndex = this._partialSelectedRows.indexOf(selIndices[i]);
			if (pIndex > -1) {
				this._partialSelectedRows.splice(pIndex, 1);
			}
		}
	}
	this._checkSelectAllState();
	this._setSelectionOnPage();

	if (this._value.hideUnselected) {
		this._dataTable.draw();
	}
	this._publishCurrentSelection();
}

/**
 * Checks whether all the rows have been selected
 */
KnimeBaseTableViewer.prototype._checkSelectAllState = function() {
	var selectAllCheckbox = $('#checkbox-select-all').get(0);
	if (!selectAllCheckbox) { return; }
	var someSelected = false;
	var allSelected = true;
	var selIndices = this._dataTable.column(0, { 'search': 'applied' }).data();
	if (selIndices.length < 1) {
		allSelected = false;
	}
	for (var i = 0; i < selIndices.length; i++) {
		if (this._selection[selIndices[i]]) {
			someSelected = true;
		} else {
			allSelected = false;
		}
		if (this._partialSelectedRows.indexOf(selIndices[i]) > -1) {
			someSelected = true;
			allSelected = false;
		}
		if (someSelected && !allSelected) {
			break;
		}
	}
	this._value.selectAll = allSelected;
    selectAllCheckbox.checked = allSelected;
    selectAllCheckbox.disabled = (selIndices.length < 1);
    var indeterminate = someSelected && !allSelected;

    if('indeterminate' in selectAllCheckbox){
		// Set visual state of "Select all" control as 'indeterminate'
		selectAllCheckbox.indeterminate = indeterminate;
	}
    this._value.selectAllIndeterminate = indeterminate;
}

/**
 * Applies the selection to the currently display page
 */
KnimeBaseTableViewer.prototype._setSelectionOnPage = function() {
	var curCheckboxes = this._dataTable.column(0, {page:'current'}).nodes().to$().children();
	for (var i = 0; i < curCheckboxes.length; i++) {
		var checkbox = curCheckboxes[i];
		checkbox.checked = this._selection[checkbox.value];
		// set tr style
		var $tr = $(checkbox).parent().parent();
		if (checkbox.checked) {
			$tr.addClass('knime-selected');
		} else {
			$tr.removeClass('knime-selected');
		}
		if ('indeterminate' in checkbox) {
			if (!checkbox.checked && this._partialSelectedRows.indexOf(checkbox.value) > -1) {
				checkbox.indeterminate = true;
			} else {
				checkbox.indeterminate = false;
			}
		}
	}
}

/**
 * Publishes the current selection for other interactive views
 */
KnimeBaseTableViewer.prototype._publishCurrentSelection = function() {
	if (knimeService && knimeService.isInteractivityAvailable() && this._value.publishSelection) {
		var selArray = [];
		for (var rowKey in this._selection) {
			if (!this._selection.hasOwnProperty(rowKey)) {
		        continue;
		    }
			if (this._selection[rowKey]) {
				selArray.push(rowKey);
			}
		}
		knimeService.setSelectedRows(this._representation.table.id, selArray, this._selectionChanged.bind(this));
	}
}

/**
 * Handler on the selection change event
 *
 * @data data  information of how the selection has been changed
 */
KnimeBaseTableViewer.prototype._selectionChanged = function(data) {
	// cannot apply selection changed event before all data is loaded
	if (!this._initialized) {
		var self = this;
		setTimeout(function() {
			self._selectionChanged(data);
		}, 500);
	}

	// apply changeSet
	if (data.changeSet) {
		if (data.changeSet.removed) {
			for (var i = 0; i < data.changeSet.removed.length; i++) {
				this._selection[data.changeSet.removed[i]] = false;
			}
		}
		if (data.changeSet.added) {
			for (var i = 0; i < data.changeSet.added.length; i++) {
				this._selection[data.changeSet.added[i]] = true;
			}
		}
	}
	this._partialSelectedRows = knimeService.getAllPartiallySelectedRows(this._representation.table.id);
	this._checkSelectAllState();
	this._setSelectionOnPage();
	if (this._value.hideUnselected) {
		this._dataTable.draw();
	}
}

/**
 * Handler on the filter changed event
 *
 * @data data  info of how the filter has been changed
 */
KnimeBaseTableViewer.prototype._filterChanged = function(data) {
	// cannot apply selection changed event before all data is loaded
	if (!this._initialized) {
		var self = this;
		setTimeout(function() {
			self._filterChanged(data);
		}, 500);
	}
	this._currentFilter = data;
	this._dataTable.draw();
}

/**
 * Returns whether a column of the specified type is available for sorting
 *
 * @param colType  column type
 */
KnimeBaseTableViewer.prototype._isColumnSortable = function (colType) {
	var allowedTypes = ['boolean', 'string', 'number', 'dateTime'];
	return allowedTypes.indexOf(colType) >= 0;
}

/**
 * Returns whether a column of the specified type is available for searching
 *
 * @param colType  column type
 */
KnimeBaseTableViewer.prototype._isColumnSearchable = function (colType) {
	var allowedTypes = ['boolean', 'string', 'number', 'dateTime', 'undefined'];
	return allowedTypes.indexOf(colType) >= 0;
}

/**
 * Mouse down handler for table cell
 *
 * @param e event
 */
KnimeBaseTableViewer.prototype._cellMouseDownHandler = function(e) {
	// init selection or set it, if Shift key was pressed
	var self = this;
	var td = e.currentTarget;
	var cell = this._dataTable.cell(td);
	if (e.shiftKey && this._firstCorner) {
		this._selectSecondCorner(cell);
	} else {
		this._selectFirstCorner(cell);
	}
	this._dataTable.cells(function(ind) {
		return ind.column >= self._nonSelectableColsCount;
	}, {page: 'current'}).nodes().flatten().to$().on('mouseover', this._bindCellMouseOverHandler = this._cellMouseOverHandler.bind(this));
	$(document).on('mouseup', this._bindCellMouseUpHandler = this._cellMouseUpHandler.bind(this));
}

/**
 * Mouse over handler for table cell
 *
 * @param e event
 */

KnimeBaseTableViewer.prototype._cellMouseOverHandler = function(e) {
	// update selection that the current cell forms a rectangle
	var td = e.currentTarget;
	var cell = this._dataTable.cell(td);
	this._selectSecondCorner(cell);
}

/**
 * Mouse up handler for table cell
 *
 * @param e event
 */

KnimeBaseTableViewer.prototype._cellMouseUpHandler = function(e) {
	// stop listening to mouse events for selection
	var self = this;
	this._dataTable.cells(function(ind) {
		return ind.column >= self._nonSelectableColsCount;
	}, {page: 'current'}).nodes().flatten().to$().off('mouseover', this._bindCellMouseOverHandler);
	$(document).off('mouseup', this._bindCellMouseUpHandler);
}


// -- BEGIN "Selection" (highlighting) of individual cells. This has nothing to do with KNIME's row "selection" and is
// used internally inside the view for copy and paste operations only.


/**
 * Make the cell to be the first corner of rectangular selection.
 *
 * @param cell cell to be the first corner
 */
KnimeBaseTableViewer.prototype._selectFirstCorner = function(cell) {
	this._firstCorner = cell;
	// Make the selection to be of this cell only at the beginning
	this._selectSecondCorner(cell);
}

/**
 * Make the cell to be the second corner of rectangular selection.
 * And then forms the selection
 *
 * @param cell cell to be the second corner
 */
KnimeBaseTableViewer.prototype._selectSecondCorner = function(cell) {
	this._deselectCells();
	this._secondCorner = cell;
	this._selectRectangle();
}

/**
 * Forms a rectangular selection
 */
KnimeBaseTableViewer.prototype._selectRectangle = function() {
	var index1 = this._firstCorner.index();
	var index2 = this._secondCorner.index();

	// select columns
	var left = Math.min(index1.column, index2.column);
	var right = Math.max(index1.column, index2.column);
	var colIndices = [];
	for (var i = left; i <= right; i++) {
		colIndices.push(i);
	}

	// select rows
	// here we need to take into account the actual order of rows (because of sorting or filtering)
	var indexes = this._dataTable.rows( { page: 'current', search: 'applied' } ).indexes().toArray();
	var top = Math.min(indexes.indexOf(index1.row), indexes.indexOf(index2.row));
	var bottom = Math.max(indexes.indexOf(index1.row), indexes.indexOf(index2.row));
	var rowIndices = indexes.slice(top, bottom + 1);

	this._dataTable.cells(rowIndices, colIndices).select();
	$('td.selected').addClass('knime-selected');
}

/**
 * Unselect currently selected cells, but don't reset the rectangle
 */
KnimeBaseTableViewer.prototype._deselectCells = function() {
	$('td.selected').removeClass('knime-selected');
	this._dataTable.cells({selected: true}).deselect();
}

/**
 * Unselect currently selected cells, and reset the rectangle
 */
KnimeBaseTableViewer.prototype._clearSelection = function() {
	this._deselectCells();
	this._firstCorner = this._secondCorner = null;
}

/**
 * Key down handler for the table
 *
 * @param e event
 */
KnimeBaseTableViewer.prototype._keyDownHandler = function(e) {
	if (e.key == 'Escape') {
		this._clearSelection();
	}
}

/**
 * Copy handler for the table
 *
 * @param e event
 */
KnimeBaseTableViewer.prototype._copyHandler = function(e) {
	if (!$('#knimePagedTable').is(':focus')) {
		// since we trigger copy event on the body level, then copy event happens from the table only when it's in focus
		// check comment in _addTableListeners method
		return;
	}
	var cellIndices = this._dataTable.cells({selected: true}).flatten();

	var nCols = Math.abs(this._firstCorner.index().column - this._secondCorner.index().column) + 1;

	var buffer = [];
	for (var i = 0, col = 1; i < cellIndices.length; i++) {
		var data = null;
		if (cellIndices[i].column === this._rowIdColInd) {
			// we need to filter the content of RowID cell to leave only RowID, but only if it's present, otherwise we skip the cell
			if (this._representation.displayRowIds) {
				data = this._knimeTable.getRow(cellIndices[i].row).rowKey;
			}
		} else {
			data = this._dataTable.data()[cellIndices[i].row][cellIndices[i].column];
		}
		if (data !== null) {
			buffer.push(data);
			if (col % nCols !== 0) {
				buffer.push('\t');
			}
		}
		if (col % nCols === 0) {
			// remove tailing \t or \n if there
			var tail = buffer[buffer.length - 1];
			if (tail === '\t' || tail === '\n') {
				buffer.pop();
			}
			buffer.push('\n');
		}
		col++;
	}
	buffer.pop();  // remove last '\n'
	buffer = buffer.join('');

	e.originalEvent.clipboardData.setData('text/plain', buffer);
    e.preventDefault();
}


// -- END "selection" (highlighting)


/**
 * Set CSS styles for table controls
 */
KnimeBaseTableViewer.prototype._setControlCssStyles = function() {
	$('#knimePagedTable_length').addClass('knime-table-length');
	$('#knimePagedTable_length label').addClass('knime-table-control-text');
	$('#knimePagedTable_length select').addClass('knime-table-control-text knime-single-line');
	$('.dt-buttons').addClass('knime-table-buttons');
	$('.dt-buttons span').addClass('knime-table-control-text');
	$('#knimePagedTable_filter').addClass('knime-table-search');
	$('#knimePagedTable_filter label').addClass('knime-table-control-text');
	$('#knimePagedTable_filter input').addClass('knime-filter knime-single-line');
	$('#knimePagedTable_paginate').addClass('knime-table-paging');
	$('#knimePagedTable_info').addClass('knime-table-info knime-table-control-text');
}

/**
 * Set CSS styles for dynamically loaded objects controls
 */
KnimeBaseTableViewer.prototype._setDynamicCssStyles = function() {
	$('#knimePagedTable tr').addClass('knime-table-row');
	$('#knimePagedTable_paginate ul').addClass('knime-table-control-text');
	$('#knimePagedTable thead tr').addClass('knime-table-header');
	$('#knimePagedTable thead th').addClass('knime-table-header');
}
