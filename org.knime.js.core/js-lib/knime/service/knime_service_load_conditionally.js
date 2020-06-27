/* global require:false, requirejs:false */
if (require && knimeService) {
    knimeService.loadConditionally = function (paths, success, failure, config) {
        if (window.knimeResourceBaseUrl || knimeService.resourceBaseUrl) {
            if (!config) {
                config = {};
            }
            if (!config.baseUrl) {
                config.baseUrl = window.knimeResourceBaseUrl || knimeService.resourceBaseUrl;
            }
        }
        if (config) {
            config.waitSeconds = 30; // added 4.2
            requirejs.config(config);
        }
        require(paths, function () {
            if (success) {
                success(arguments);
            }
        }, function (err) {
            knimeService.logError(err);
            if (failure) {
                failure(err);
            }
        });
    };
}
