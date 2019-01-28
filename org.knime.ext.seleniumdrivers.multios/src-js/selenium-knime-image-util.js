window.knimeImageUtil = {};

window.knimeImageUtil.loadJSONFile = function (url, callback) {
    var httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === 4) {
            // local requests leave status at 0
            if (httpRequest.status === 200 || httpRequest.status === 0) {
                var data = JSON.parse(httpRequest.responseText);
                if (callback) {
                    callback(data);
                }
            }
        }
    };
    httpRequest.open('GET', url);
    httpRequest.send();
};

window.knimeImageUtil.loadView = function (repURL, valURL, initCall) {
    window.knimeImageUtil.loadJSONFile(repURL, function (rep) {
        window.parsedRepresentation = rep;
        if (window.parsedValue) {
            eval(initCall);
        }
    });
    window.knimeImageUtil.loadJSONFile(valURL, function (val) {
        window.parsedValue = val;
        if (window.parsedRepresentation) {
            eval(initCall);
        }
    });
};
