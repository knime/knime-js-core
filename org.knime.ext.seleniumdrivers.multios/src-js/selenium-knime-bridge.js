seleniumKnimeBridge = function() {
	
	const FRAME_ID = 'knimeViewFrame';
	
	const SIGNAL_NO_ACTION = 'NO_ACTION';
	const SIGNAL_CLOSE_BUTTON_PRESSED = 'CLOSE_BUTTON_PRESSED';
	const SIGNAL_RESET_BUTTON_PRESSED = 'RESET_BUTTON_PRESSED';
	const SIGNAL_APPLY_BUTTON_PRESSED = 'APPLY_BUTTON_PRESSED';
	const SIGNAL_CLOSE_WINDOW = 'CLOSE_WINDOW';
	
	var signalQueue = [];
	
	var bridge = {};
	bridge.version = '1.0.0';
	
	laodJSONFile = function(url, callback) {
		var httpRequest = new XMLHttpRequest();
		httpRequest.onreadystatechange = function() {
			if (httpRequest.readyState === 4) {
				// local requests leave status at 0
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
	
	signal = function(value) {
		// enqueue and call signal, in most cases will be called immediately
		signalQueue.push(value);
		bridge.signalNext();
	}
	
	bridge.signalNext = function() {
		if (window.signal && signalQueue.length > 0) {
			var toSignal = signalQueue.splice(0, 1);
			window.signal(toSignal);
		}
	}
	
	window.addEventListener('beforeunload', function (event) {
		if (window.signal) { 
			window.signal(SIGNAL_CLOSE_WINDOW);
		}
	});
		
	return bridge;
}();