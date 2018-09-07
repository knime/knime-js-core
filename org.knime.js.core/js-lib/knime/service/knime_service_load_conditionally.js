//sanity check
if (require && knimeService) {
	knimeService.loadConditionally = function(paths, success, failure, config) {
		if (config) {
			requirejs.config(config);
		}
		require(paths, function() {
			if (success) {
				success(arguments);
			}
		}, function(err) {
			knimeService.logError(err);
			if (failure) {
				failure(err);
			}
		});
	}
}