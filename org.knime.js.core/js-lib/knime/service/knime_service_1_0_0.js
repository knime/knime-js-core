knimeService = function() {
	
	/*!
	* screenfull
	* v3.0.0 - 2015-11-24
	* (c) Sindre Sorhus; MIT License
	*/
	!function(){"use strict";var a="undefined"!=typeof module&&module.exports,b="undefined"!=typeof Element&&"ALLOW_KEYBOARD_INPUT"in Element,c=function(){for(var a,b,c=[["requestFullscreen","exitFullscreen","fullscreenElement","fullscreenEnabled","fullscreenchange","fullscreenerror"],["webkitRequestFullscreen","webkitExitFullscreen","webkitFullscreenElement","webkitFullscreenEnabled","webkitfullscreenchange","webkitfullscreenerror"],["webkitRequestFullScreen","webkitCancelFullScreen","webkitCurrentFullScreenElement","webkitCancelFullScreen","webkitfullscreenchange","webkitfullscreenerror"],["mozRequestFullScreen","mozCancelFullScreen","mozFullScreenElement","mozFullScreenEnabled","mozfullscreenchange","mozfullscreenerror"],["msRequestFullscreen","msExitFullscreen","msFullscreenElement","msFullscreenEnabled","MSFullscreenChange","MSFullscreenError"]],d=0,e=c.length,f={};e>d;d++)if(a=c[d],a&&a[1]in document){for(d=0,b=a.length;b>d;d++)f[c[0][d]]=a[d];return f}return!1}(),d={request:function(a){var d=c.requestFullscreen;a=a||document.documentElement,/5\.1[\.\d]* Safari/.test(navigator.userAgent)?a[d]():a[d](b&&Element.ALLOW_KEYBOARD_INPUT)},exit:function(){document[c.exitFullscreen]()},toggle:function(a){this.isFullscreen?this.exit():this.request(a)},raw:c};return c?(Object.defineProperties(d,{isFullscreen:{get:function(){return Boolean(document[c.fullscreenElement])}},element:{enumerable:!0,get:function(){return document[c.fullscreenElement]}},enabled:{enumerable:!0,get:function(){return Boolean(document[c.fullscreenEnabled])}}}),void(a?module.exports=d:window.screenfull=d)):void(a?module.exports=!1:window.screenfull=!1)}();
	
	/*!
	 * Array filter polyfill
	 */
	// TODO do we need support for IE8?
	/*if (!Array.prototype.filter) {
		Array.prototype.filter = function(fun) {
			'use strict';

			if (this === void 0 || this === null) {
				throw new TypeError();
		    }

		    var t = Object(this);
		    var len = t.length >>> 0;
		    if (typeof fun !== 'function') {
		    	throw new TypeError();
		    }

		    var res = [];
		    var thisArg = arguments.length >= 2 ? arguments[1] : void 0;
		    for (var i = 0; i < len; i++) {
		    	if (i in t) {
		    		var val = t[i];

		        // NOTE: Technically this should Object.defineProperty at
		        // the next index, as push can be affected by
		        // properties on Object.prototype and Array.prototype.
		        // But that method's new, and collisions should be
		        // rare, so use the more-compatible alternative.
		    		if (fun.call(thisArg, val, i, t)) {
		    			res.push(val);
		    		}
		    	}
		    }
		    return res;
		};
	}*/
	
	var service = {};
	service.version = "1.0.0";
	
	// flags for menu items
	service.CLOSE = 1;
	service.OK = 2;
	service.CANCEL = 4;
	service.LINK = 8;
	
	var SELECTION = 'selection', FILTER = 'filter', SEPARATOR = '-';
	
	var header, menu;
	var initialized = false, interactivityAvailable = false;
	var GLOBAL_SERVICE = this;
	
	init = function() {
		if (parent && parent.KnimePageLoader && parent.KnimePageLoader.publish) {
			interactivityAvailable = true;
			GLOBAL_SERVICE = parent.KnimePageLoader;
		}
		
		var body = document.getElementsByTagName('body')[0];
		header = document.createElement('nav');
		header.setAttribute('id', 'knime-service-header');
		body.insertBefore(header, body.firstChild);
		initialized = true;
	}
	
	service.noFloatingHeader = function() {
		initialized || init();
		header.style.position = 'initial';
	}
	
	enableNav = function() {
		header.style.display = 'block';
	}
	
	addButton = function(id, icon, title, callback, first) {
		if (document.getElementById(id)) {
			return false;
		}
		var button = document.createElement('div');
		button.setAttribute('id', id);
		button.className = 'service-button';
		button.setAttribute('title', title);
		button.setAttribute('aria-label', title);
		var iContainer = document.createElement('i');
		iContainer.setAttribute('aria-hidden', true);
		button.appendChild(iContainer);
		if (typeof icon == 'number') {
			iContainer.setAttribute('class', 'uicon');
			iContainer.appendChild(document.createTextNode(String.fromCharCode(icon)));
		} else if (typeof icon == 'string') {
			iContainer.className = 'ficon fa fa-fw fa-' + icon;
			iContainer.setAttribute('aria-hidden', 'true');
		}
		if (first) {
			header.insertBefore(button, header.firstChild);
		} else {
			header.appendChild(button);
		}
		button.onclick = callback;
		enableNav();
		return button;
	}
	
	initMenu = function() {
		addButton('knime-service-menu-button', 'bars', 'Settings', openMenu, true);
		var mNav = document.createElement('nav');
		mNav.setAttribute('id', 'knime-service-menu');
		header.appendChild(mNav);
		menu = document.createElement('ul');
		//menu.setAttribute('class', 'fa-ul');
		mNav.appendChild(menu);
	}
	
	openMenu = function() {
		var mNav = document.getElementById('knime-service-menu');
		var open = (mNav.className == 'open');
		mNav.className = open ? '' : 'open';
		var button = document.getElementById('knime-service-menu-button');
		button.className = open ? 'service-button' : 'service-button active';
	}
	
	publishInteractivityEvent = function(id, data) {
		initialized || init();
		return interactivityAvailable && GLOBAL_SERVICE.publish(id, data);
	}
	
	subscribeToInteractivityEvent = function(id, callback, elementFilter) {
		initialized || init();
		return interactivityAvailable && GLOBAL_SERVICE.subscribe(id, callback, elementFilter);
	}
	
	getInteractivityElement = function(id) {
		initialized || init();
		if (!interactivityAvailable) {
			return null;
		}
		var element = GLOBAL_SERVICE.getPublishedElement(id);
		if (typeof element == 'undefined') {
			element = {};
		}
		if (typeof element.elements == 'undefined') {
			filter.elements = [];
		}
		return element;
	}
	
	service.publishSelection = function(tableId, selection) {
		return publishInteractivityEvent(SELECTION + SEPARATOR + tableId, selection);
	}
	
	service.subscribeToSelection = function(tableId, callback) {
		return subscribeToInteractivityEvent(SELECTION + SEPARATOR + tableId, callback);
	}
	
	service.publishFilter = function(tableId, filter) {
		return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter);
	}
	
	service.subscribeToFilter = function(tableId, callback, elementFilter) {
		subscribeToInteractivityEvent(FILTER + SEPARATOR + tableId, callback, elementFilter);
	}
	
	service.addToFilter = function(tableId, filterElement) {
		if (!filterElement || !filterElement.id) {
			return false;
		}
		var filter = getInteractivityElement(tableId);
		if (!filter) {
			return false;
		}
		var existing = false;
		for (var i = 0; i < filter.elements.length; i++) {
			if (filter.elements[i].id == filterElement.id) {
				filter.elements[i] = filterElement;
				existing = true;
				break;
			}
		}
		if (!existing) {
			filter.elements.push(filterElement);
		}
		return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter);		
	}
	
	service.removeFromFilter = function(tableId, elementId) {
		var filter = getInteractivityElement(tableId);
		if (!filter) {
			return false;
		}
		for (var i = 0; i < filter.elements.length; i++) {
			if (filter.elements[i].id == filterElement.id) {
				filter.elements.splice(i, 1);
				return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter);
			}
		}
		return false;
	}
	
	service.addRowsToFilter = function(tableId, elementId, rowKeys) {
		var filter = getInteractivityElement(tableId);
		if (!filter) {
			return false;
		}
		var existing = false;
		for (var i = 0; i < filter.elements.length; i++) {
			if (filter.elements[i].id == elementId) {
				filter.elements[i].rows.concat(rowKeys);
				existing = true;
			}
		}
		if (!existing) {
			var filterElement = {
					'id': elementId,
					'type': 'row',
					'rows': rowKeys
			}
			filter.elements.push(filterElement);
		}
		return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter);
	}
	
	service.removeRowsFromFilter = function(tableId, elementId, rowKeys) {
		var filter = getInteractivityElement(tableId);
		if (!filter) {
			return false;
		}
		for (var i = 0; i < filter.elements.length; i++) {
			if (filter.elements[i].id == elementId) {
				var filteredRows = filter.elements[i].rows.filter(function(value) {
					return rowKeys.indexOf(value) == -1;
				});
				if (filteredRows.length == 0) {
					filter.elements.splice(i, 1);
				} else {
					filter.elements[i].rows = filteredRows;
				}
				return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter);
			}
		}
		return false;
	}
	
	service.addMenuItem = function(title, icon, element, path, flags) {
		initialized || init();
		menu || initMenu();
		var item = document.createElement('li');
		var link = document.createElement('a');
		link.setAttribute('href', '#');
		item.appendChild(link);
		if (icon) {
			var iEl = document.createElement('i');
			iEl.className = 'fa fa-fw fa-' + icon;
			iEl.setAttribute('aria-hidden', 'true');
			link.appendChild(iEl);
		} else {
			link.style.marginLeft = '24px';
		}
		if (typeof title == 'string' && title != '') {
			link.appendChild(document.createTextNode(title));
		}
		if (element) {
			// inline element
			if (!flags) {
				link.onclick = function(event) {
					event.preventDefault();
				}
				element.style.margin = '0 0 0 6px';
				element.style.float = 'right';
				link.appendChild(element);
			} else {
				item.className = 'menuItem';
			}
			// external link element
			if (service.LINK & flags) {
				link.onclick = function(event) {
					event.preventDefault();
					openMenu();
					window.open(element, '_blank');
				}
			}
			//TODO: popup element with flags
		}
		menu.appendChild(item);
		return item;
	}
	
	service.addMenuDivider = function() {
		initialized || init();
		menu || initMenu();
		var divider = document.createElement('li');
		divider.className = 'divider';
		menu.appendChild(divider);
		return divider;
	}
	
	service.addButton = function(id, icon, title, callback) {
		initialized || init();
		return addButton(id, icon, title, callback, false);
	}
	
	service.allowFullscreen = function(element) {
		initialized || init();
		return interactivityAvailable && screenfull.enabled 
			&& addButton('knime-service-fullscreen-button', 'arrows-alt', 'Toggle Fullscreen', function() {
			if (screenfull.enabled) {
				screenfull.toggle(element);
			}
		});
	}
	
	service.addNavSpacer = function() {
		initialized || init();
		var spacer = document.createElement('div');
		spacer.className = 'service-nav-spacer';
		header.appendChild(spacer);
	}
	
	document.addEventListener('DOMContentLoaded', init, false);
	
	return service;
	
}();