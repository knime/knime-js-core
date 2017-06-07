seleniumKnimeBridge = function() {
	
	var FRAME_ID = 'knimeViewFrame';
	
	var bridge = {};
	bridge.version = '1.0.0';
	
	bridge.laodJSONFile = function(url, callback) {
		var httpRequest = new XMLHttpRequest();
		httpRequest.onreadystatechange = function() {
			if (httpRequest.readyState === 4) {
				if (httpRequest.status === 200 || httpRequest.status === 0) {
					var data = JSON.parse(httpRequest.responseText);
					if (callback) callback(data);
				}
			}
		}
		httpRequest.open('GET', url);
		httpRequest.send();
	}
	
	bridge.initView = function() {
		var initCall = arguments[2];
		var parsedRepresentation, parsedValue;
		laodJSONFile(arguments[0], function(rep){
			parsedRepresentation = rep;
			if (parsedValue) { 
				eval(initCall); 
			}
		});
		laodJSONFile(arguments[1], function(val){
			parsedValue = val;
			if (parsedRepresentation) { 
				eval(initCall);
			}
		});
	}	
		
	return bridge;
}();