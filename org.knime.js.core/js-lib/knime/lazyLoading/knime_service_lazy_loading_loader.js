//sanity check
if (knimeService && knimeService.loadConditionally) {
	// knimeService includes custom Modernizr to detect browser support for features required for lazy loading implementation
	if (knimeService.isViewRequestsSupported()) {
		knimeService.loadConditionally(["js-lib/knime/lazyLoading/knime_service_lazy_loading_1_0_0"], function() {
			try {
				initLazyLoading();
			} catch (ex) {
				knimeService.logError(ex);
			}
		});
	} else {
		knimeService.logError("Lazy loading is not supported by the current browser.");
	}
}