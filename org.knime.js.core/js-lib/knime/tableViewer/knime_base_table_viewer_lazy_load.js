/* eslint-env es6, jquery */
/* eslint no-var: "error" */
window.KnimeBaseTableViewer.prototype._lazyLoadData = function (data, callback, settings) {
    const win = [data.start, data.start + data.length - 1];
    if (this._knimeTable) {
        if (typeof this._lastRequest === 'undefined') {
            this._lastRequest = {
                start: null,
                length: null,
                search: { value: '', regex: false },
                order: [],
                columns: null,
                filter: null
            };
        }
        let api = new $.fn.dataTable.Api(settings);
        data.columns.forEach(col => {
            col.name = api.column(col.data).header().textContent;
        });
        data.order.forEach(o => {
            o.column = api.column(o.column).header().textContent;
        });
        this._invalidateCache(data);
        
        const cacheStart = this._knimeTable.getFragmentFirstRowIndex();
        const cached = [cacheStart, cacheStart + this._knimeTable.getNumRows() - 1];
        const included = cached[0] <= win[0] && cached[1] >= win[1];
        if (included) {
            this._lazyLoadResponse(data, callback);
        } else {
            const request = {
                start: data.start,
                length: data.length,
                search: data.search,
                order: data.order,
                columns: data.columns,
                filter: JSON.parse(JSON.stringify(this._currentFilter))
            };
            const self = this;
            $('#knimePagedTable_processing').text('Processing...').prop('title', '');
            if (this._runningRequest) {
                this._runningRequest.cancel();
            }
            let promise = knimeService.requestViewUpdate(request);
            this._runningRequest = promise;
            promise.progress(monitor => {
                $('#knimePagedTable_processing').prop('title', monitor.progressMessage ? monitor.progressMessage : '');
                if (monitor.progress) {
                    let percent = (monitor.progress * 100).toFixed(0);
                    $('#knimePagedTable_processing').text('Processing... (' + percent + '%)');
                }
            }).then(response => {
                if (response.error) {
                    self._lazyLoadResponse(data, callback, response.error);
                } else {
                    this._lastRequest = request;
                    self._knimeTable.mergeTables(response.table);
                    self._lazyLoadResponse(data, callback);
                }
            }).catch(error => {
                knimeService.logError(error);
                alert('Could not load data: ' + error);
                self._lazyLoadResponse(data, callback, error);
            });
        }
    }
};

window.KnimeBaseTableViewer.prototype._invalidateCache = function (data) {
    if (JSON.stringify(data.order) !== JSON.stringify(this._lastRequest.order)) {
        this._clearCache();
        return;
    }
    if (JSON.stringify(data.search) !== JSON.stringify(this._lastRequest.search)) {
        this._clearCache();
        return;
    }
    if (this._lastRequest.columns === null) {
        this._lastRequest.columns = data.columns;
        for (let col = 0; col < data.columns.length; col++) {
            if (data.columns[col].searchable && data.columns[col].search.value !== '') {
                this._clearCache();
                return;
            }
        }
    }
    if (JSON.stringify(data.columns) !== JSON.stringify(this._lastRequest.columns)) {
        this._clearCache();
        return;
    }
    if (JSON.stringify(this._currentFilter) !== JSON.stringify(this._lastRequest.filter)) {
        this._clearCache();
        return;
    }
    // TODO: evaluation needs to take into account 'show selection only' filter
};

window.KnimeBaseTableViewer.prototype._clearCache = function () {
    if (this._knimeTable) {
        this._knimeTable.clear();
    }
    if (this._rowCountRequest) {
        this._rowCountRequest.cancel();
        this._rowCountRequest = null;
    }
};

window.KnimeBaseTableViewer.prototype._issueRowCountRequest = function () {
    if (this._rowCountRequest) {
        this._rowCountRequest.cancel();
    }
    let request = {
        countRows: true
    };
    this._rowCountRequest = knimeService.requestViewUpdate(request);
    // we don't do anything with the result of the request in the view, just issue the request so it can be cancelled
};

window.KnimeBaseTableViewer.prototype._lazyLoadResponse = function (data, callback, error) {
    this._runningRequest = null;
    let response = {
        draw: data.draw
    };
    response.recordsTotal = this._knimeTable.getTotalRowCount();
    response.recordsFiltered = this._knimeTable.getTotalFilteredRowCount();
    if (response.recordsFiltered < 0) {
        // at this point we only know that there is at least one more record than was requested
        let minimumRecords = this._knimeTable.getFragmentFirstRowIndex() + this._knimeTable.getNumRows() + 1;
        response.recordsFiltered = minimumRecords;
        this._issueRowCountRequest();
    }
    if (error) {
        response.error = error;
        response.data = [];
    } else {
        let firstRow = data.start - this._knimeTable.getFragmentFirstRowIndex();
        let lastRow = firstRow + data.length;
        response.data = this._getDataSlice(firstRow, lastRow);
    }
    callback(response);
};
