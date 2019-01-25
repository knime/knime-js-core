/* eslint-env es6, jquery */
/* eslint no-var: "error" */
window.KnimeBaseTableViewer.prototype._lazyLoadData = function (data, callback, settings) {
    // TODO: evaluation needs to take into account order, filter, search
    const win = [data.start, data.start + data.length - 1];
    if (this._knimeTable) {
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
                columns: data.columns
            };
            const tableViewer = this;
            let promise = knimeService.requestViewUpdate(request);
            
            promise.progress(monitor => {
                if (monitor.progress) {
                    let percent = (monitor.progress * 100).toFixed(0);
                    $('knimePagedTable_processing').text('Processing... (' + percent + '%)');
                }
            }).then(response => {
                if (response.error) {
                    tableViewer._lazyLoadResponse(data, callback, response.error);
                } else {
                    tableViewer._knimeTable.mergeTables(response.table); tableViewer._lazyLoadResponse(data, callback);
                }
            }).catch(error => tableViewer._lazyLoadResponse(data, callback, error));
        }
    }
};

window.KnimeBaseTableViewer.prototype._lazyLoadResponse = function (data, callback, error) {
    let response = {
        draw: data.draw
    };
    response.recordsTotal = this._knimeTable.getTotalRowCount();
    response.recordsFiltered = this._knimeTable.getTotalFilteredRowCount();
    if (error) {
        response.error = error;
        response.data = [];
    } else {
        let firstRow = data.start - this._knimeTable.getFragmentFirstRowIndex();
        let lastRow = data.start + data.length;
        response.data = this._getDataSlice(firstRow, lastRow);
    }
    callback(response);
};
