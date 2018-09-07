initLazyLoading = function() {
	
	var viewRequests = [], requestSequence = 0, responseBuffer = [], pushSupported;
	if (!knimeService) {
		throw new Error("KNIME service is not defined.");
	}
	
	function MonitorablePromise(executor, promise) {
		this.progressListeners = [];
		this.monitor = {};
		if (promise) {
			this.realPromise = promise;
		} else {
			this.realPromise = new Promise(executor);
		}
	}
	
	MonitorablePromise.prototype = {
		progress: function(onProgress) {
			this.progressListeners.push(onProgress);
			//immediately call progress listener if progress is already available
			if (this.monitor) {
				onProgress(this.monitor);
			}
			return this;
		},
		then: function(onResolve, onReject) {
			let newPromise = new MonitorablePromise(null, this.realPromise.then(onResolve, onReject));
			newPromise.progressListeners = this.progressListeners;
			newPromise.monitor = this.monitor;
			return newPromise;
		},
		catch: function(onReject) {
			let newPromise = new MonitorablePromise(null, this.realPromise.catch(onReject));
			newPromise.progressListeners = this.progressListeners;
			newPromise.monitor = this.monitor;
			return newPromise;
		},
		finally: function(onFinally) {
			let newPromise = new MonitorablePromise(null, this.realPromise.finally(onFinally));
			newPromise.progressListeners = this.progressListeners;
			newPromise.monitor = this.monitor;
			return newPromise;
		},
		updateProgress: function(mon) {
			this.monitor = mon;
			this.progressListeners.forEach(function(onProgress) {
				onProgress(mon);
			});
		},
		cancel: function(invokeCatch) {
			if (!this.monitor || !this.monitor.requestSequence) {
				return;
			}
			for (let i = 0; i < viewRequests.length; i++) {
				let request = viewRequests[i];
				if (request.sequence == this.monitor.requestSequence) {
					if (!request.notCancelable) {
						let id = request.sequence;
						if (request.monitor && request.monitor.id) {
							id = request.monitor.id;
						}
						id = '' + id;
						if (knimeService.isInteractivityAvailable()) {
							monitor = knimeService.getGlobalService().cancelViewRequest(window.frameElement.id, id, invokeCatch);
						} else {
							knimeCancelRequest(id);
						}
						if (!invokeCatch) {
							viewRequests.splice(i, 1);
						}
					}
					break;
				}
			}
		}
	}
	
	getNextRequestSequence = function(sequence) {
		let mod = (typeof Number.MAX_SAFE_INTEGER !== 'undefined') ? 
				Number.MAX_SAFE_INTEGER : Number.MAX_VALUE;
		return ++sequence % mod;
	}
	
	getPreviousRequestSequence = function(sequence) {
		let mod = (typeof Number.MAX_SAFE_INTEGER !== 'undefined') ? 
				Number.MAX_SAFE_INTEGER : Number.MAX_VALUE;
		return --sequence % mod;
	}
	
	getAndSetNextRequestSequence = function() {
		requestSequence = getNextRequestSequence(requestSequence);
		return requestSequence;
	}
	
	async function _requestViewUpdate(request, resolvable) {
		try {
			let monitor;
			if (knimeService.isInteractivityAvailable()) {
				monitor = knimeService.getGlobalService()
					.requestViewUpdate(window.frameElement.id, JSON.stringify(request), request.sequence);
			} else {
				monitor = knimeViewRequest(JSON.stringify(request));
			}
			if (!monitor) {
				monitor = {};
			}
			if (typeof monitor === 'string') {
				monitor = JSON.parse(monitor);
			}
			if (!monitor.requestSequence) {
				monitor.requestSequence = request.sequence;
			}
			resolvable.monitor = monitor;
			if (resolvable.promise) {
				resolvable.promise.updateProgress(monitor);
			}
			if (typeof pushSupported === 'undefined') {
				initPushSupported();
			}
			if (!pushSupported) {
				initUpdateMonitorPolling(resolvable);
			}
		} catch (exception) {
			for (let i = 0; i < viewRequests.length; i++) {
				if (viewRequests[i].requestSequence === request.sequence) {
					viewRequests.splice(i, 1);
					break;
				}
			}
			if (resolvable.reject) {
				resolvable.reject(exception);
			}
		}
	}
	
	initPushSupported = function() {
		if (knimeService.isInteractivityAvailable()) {
			pushSupported = knimeService.getGlobalService().isPushSupported();
		} else {
			pushSupported = knimePushSupported();
		}
	}
	
	knimeService.requestViewUpdate = function(request, preserveOrder, notCancelable) {
		let prevSequence = requestSequence;
		request.sequence = getAndSetNextRequestSequence();
		let resolvable = {
				'sequence': request.sequence,
				'monitor': {},
				'notCancelable': notCancelable,
				'preserveOrder': preserveOrder
		};
		let promise = new MonitorablePromise(function(res, rej) {
			resolvable.resolve = res;
			resolvable.reject = rej;
		});
		resolvable.promise = promise;
		viewRequests.push(resolvable);
		_requestViewUpdate(request, resolvable);
		return promise;
	}
	
	initUpdateMonitorPolling = function(resolvable) {
		//initialize first update poll with a default of 500ms
		setTimeout(pollMonitorUpdate, 500, resolvable, 500);
	}
	
	pollMonitorUpdate = function(resolvable, time) {
		let updatedMonitor, id = resolvable.monitor.id;
		if (knimeService.isInteractivityAvailable()) {
			updatedMonitor = knimeService.getGlobalService().updateRequestStatus(window.frameElement.id, id);
		} else {
			updatedMonitor = knimeUpdateRequestStatus(id);
		}
		if (typeof updatedMonitor === 'string') {
			updatedMonitor = JSON.parse(updatedMonitor);
		}
		if (!compareResponseMonitor(resolvable.monitor, updatedMonitor)) {
			knimeService.updateResponseMonitor(updatedMonitor);
		}
		
		if (!((updatedMonitor.executionFinished && updatedMonitor.responseAvailable) 
			|| updatedMonitor.executionFailed || updatedMonitor.cancelled)) {
			//slightly increase timeout every time up to a maximum of 5s, long running requests will
			//have fewer update calls
			let newTime = Math.min(5000, 1.05 * time);
			setTimeout(pollMonitorUpdate, newTime, resolvable, newTime);
		}
	}
	
	compareResponseMonitor = function(mon, otherMon) {
		//naive string comparison for performance reasons 
		return JSON.stringify(mon) === JSON.stringify(otherMon);
	}
	
	knimeService.updateResponseMonitor = function(updatedMonitor) {
		let sequence = updatedMonitor.requestSequence;
		if (typeof sequence === 'undefined') {
			return;
		}
		if (updatedMonitor.executionFinished && updatedMonitor.responseAvailable) {
			knimeService.respondToViewRequest(updatedMonitor.response);
		}
		for (let i = 0; i < viewRequests.length; i++) {
			let resolvable = viewRequests[i];
			if (resolvable.sequence === sequence) {
				if (updatedMonitor.executionFailed || updatedMonitor.cancelled) {
					resolvable.reject(updatedMonitor.errorMessage);
					viewRequests.splice(i, 1);
				} else {
					resolvable.monitor = updatedMonitor;
					if (resolvable.promise) {
						resolvable.promise.updateProgress(updatedMonitor);
					}
				}
				break;
			}
		}
	}
	
	knimeService.respondToViewRequest = function(response) {
		let sequence = response.sequence;
		if (typeof sequence === 'undefined') {
			return;
		}
		for (var i = 0; i < viewRequests.length; i++) {
			let request = viewRequests[i];
			if (request.sequence === sequence) {
				if (request.preserveOrder) {
					request.response = response;
					for (let j = 0; j < viewRequests.length; j++) {
						if (typeof viewRequests[j].response !== 'undefined') {
							resolveResponse(j, viewRequests[j].response);
							j--;
						} else {
							break;
						}
					}
				} else {
					resolveResponse(i, response);
				}
				break;
			}
		}
	}
	
	resolveResponse = function(index, response) {
		let resolvable = viewRequests[index];
		if (resolvable.sequence !== response.sequence) {
			return;
		}
		viewRequests.splice(index, 1);
		try {
			resolvable.resolve(response);
		} catch (exception) {
			resolvable.reject(exception);
		}
	}	
	
};
if (typeof KnimeInteractivity === 'undefined') {
	KnimeInteractivity = {
		respondToViewRequest: function(response) {
			return knimeService.respondToViewRequest(response);
		},
		updateResponseMonitor: function(monitor) {
			return knimeService.updateResponseMonitor(monitor);
		}
	}
}