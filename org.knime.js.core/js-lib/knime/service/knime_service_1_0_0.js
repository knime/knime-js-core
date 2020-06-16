/* globals screenfull:false, Modernizr:false */
/* eslint-disable no-bitwise, no-console */
window.knimeService = (function () {

    /* ! screenfull v3.3.0 - 2017-07-06 (c) Sindre Sorhus; MIT License !, https://github.com/sindresorhus/screenfull.js/pull/105 applied to work in latest Chrome */
    // eslint-disable-next-line
    !function(){"use strict";var a="undefined"!=typeof window&&void 0!==window.document?window.document:{},b="undefined"!=typeof module&&module.exports,c="undefined"!=typeof Element&&"ALLOW_KEYBOARD_INPUT"in Element,d=function(){for(var b,c=[["requestFullscreen","exitFullscreen","fullscreenElement","fullscreenEnabled","fullscreenchange","fullscreenerror"],["webkitRequestFullscreen","webkitExitFullscreen","webkitFullscreenElement","webkitFullscreenEnabled","webkitfullscreenchange","webkitfullscreenerror"],["webkitRequestFullScreen","webkitCancelFullScreen","webkitCurrentFullScreenElement","webkitCancelFullScreen","webkitfullscreenchange","webkitfullscreenerror"],["mozRequestFullScreen","mozCancelFullScreen","mozFullScreenElement","mozFullScreenEnabled","mozfullscreenchange","mozfullscreenerror"],["msRequestFullscreen","msExitFullscreen","msFullscreenElement","msFullscreenEnabled","MSFullscreenChange","MSFullscreenError"]],d=0,e=c.length,f={};d<e;d++)if((b=c[d])&&b[1]in a){for(d=0;d<b.length;d++)f[c[0][d]]=b[d];return f}return!1}(),e={change:d.fullscreenchange,error:d.fullscreenerror},f={request:function(b){var e=d.requestFullscreen;b=b||a.documentElement,/ Version\/5\.1(?:\.\d+)? Safari\//.test(navigator.userAgent)?b[e]():b[e](c?Element.ALLOW_KEYBOARD_INPUT:{})},exit:function(){a[d.exitFullscreen]()},toggle:function(a){this.isFullscreen?this.exit():this.request(a)},onchange:function(a){this.on("change",a)},onerror:function(a){this.on("error",a)},on:function(b,c){var d=e[b];d&&a.addEventListener(d,c,!1)},off:function(b,c){var d=e[b];d&&a.off(d,c,!1)},raw:d};if(!d)return void(b?module.exports=!1:window.screenfull=!1);Object.defineProperties(f,{isFullscreen:{get:function(){return Boolean(a[d.fullscreenElement])}},element:{enumerable:!0,get:function(){return a[d.fullscreenElement]}},enabled:{enumerable:!0,get:function(){return Boolean(a[d.fullscreenEnabled])}}}),b?module.exports=f:window.screenfull=f}();

    /* ! modernizr 3.6.0 (Custom Build) | MIT, https://modernizr.com/download/?-cssanimations-es6object-promises-setclasses ! */
    // eslint-disable-next-line
    !function(e,n,t){function r(e,n){return typeof e===n}function o(){var e,n,t,o,s,i,a;for(var l in w)if(w.hasOwnProperty(l)){if(e=[],n=w[l],n.name&&(e.push(n.name.toLowerCase()),n.options&&n.options.aliases&&n.options.aliases.length))for(t=0;t<n.options.aliases.length;t++)e.push(n.options.aliases[t].toLowerCase());for(o=r(n.fn,"function")?n.fn():n.fn,s=0;s<e.length;s++)i=e[s],a=i.split("."),1===a.length?Modernizr[a[0]]=o:(!Modernizr[a[0]]||Modernizr[a[0]]instanceof Boolean||(Modernizr[a[0]]=new Boolean(Modernizr[a[0]])),Modernizr[a[0]][a[1]]=o),C.push((o?"":"no-")+a.join("-"))}}function s(e){var n=P.className,t=Modernizr._config.classPrefix||"";if(_&&(n=n.baseVal),Modernizr._config.enableJSClass){var r=new RegExp("(^|\\s)"+t+"no-js(\\s|$)");n=n.replace(r,"$1"+t+"js$2")}Modernizr._config.enableClasses&&(n+=" "+t+e.join(" "+t),_?P.className.baseVal=n:P.className=n)}function i(e,n){return!!~(""+e).indexOf(n)}function a(){return"function"!=typeof n.createElement?n.createElement(arguments[0]):_?n.createElementNS.call(n,"http://www.w3.org/2000/svg",arguments[0]):n.createElement.apply(n,arguments)}function l(e){return e.replace(/([a-z])-([a-z])/g,function(e,n,t){return n+t.toUpperCase()}).replace(/^-/,"")}function u(e,n){return function(){return e.apply(n,arguments)}}function f(e,n,t){var o;for(var s in e)if(e[s]in n)return t===!1?e[s]:(o=n[e[s]],r(o,"function")?u(o,t||n):o);return!1}function c(e){return e.replace(/([A-Z])/g,function(e,n){return"-"+n.toLowerCase()}).replace(/^ms-/,"-ms-")}function d(n,t,r){var o;if("getComputedStyle"in e){o=getComputedStyle.call(e,n,t);var s=e.console;if(null!==o)r&&(o=o.getPropertyValue(r));else if(s){var i=s.error?"error":"log";s[i].call(s,"getComputedStyle returning null, its possible modernizr test results are inaccurate")}}else o=!t&&n.currentStyle&&n.currentStyle[r];return o}function p(){var e=n.body;return e||(e=a(_?"svg":"body"),e.fake=!0),e}function m(e,t,r,o){var s,i,l,u,f="modernizr",c=a("div"),d=p();if(parseInt(r,10))for(;r--;)l=a("div"),l.id=o?o[r]:f+(r+1),c.appendChild(l);return s=a("style"),s.type="text/css",s.id="s"+f,(d.fake?d:c).appendChild(s),d.appendChild(c),s.styleSheet?s.styleSheet.cssText=e:s.appendChild(n.createTextNode(e)),c.id=f,d.fake&&(d.style.background="",d.style.overflow="hidden",u=P.style.overflow,P.style.overflow="hidden",P.appendChild(d)),i=t(c,e),d.fake?(d.parentNode.removeChild(d),P.style.overflow=u,P.offsetHeight):c.parentNode.removeChild(c),!!i}function y(n,r){var o=n.length;if("CSS"in e&&"supports"in e.CSS){for(;o--;)if(e.CSS.supports(c(n[o]),r))return!0;return!1}if("CSSSupportsRule"in e){for(var s=[];o--;)s.push("("+c(n[o])+":"+r+")");return s=s.join(" or "),m("@supports ("+s+") { #modernizr { position: absolute; } }",function(e){return"absolute"==d(e,null,"position")})}return t}function v(e,n,o,s){function u(){c&&(delete E.style,delete E.modElem)}if(s=r(s,"undefined")?!1:s,!r(o,"undefined")){var f=y(e,o);if(!r(f,"undefined"))return f}for(var c,d,p,m,v,g=["modernizr","tspan","samp"];!E.style&&g.length;)c=!0,E.modElem=a(g.shift()),E.style=E.modElem.style;for(p=e.length,d=0;p>d;d++)if(m=e[d],v=E.style[m],i(m,"-")&&(m=l(m)),E.style[m]!==t){if(s||r(o,"undefined"))return u(),"pfx"==n?m:!0;try{E.style[m]=o}catch(h){}if(E.style[m]!=v)return u(),"pfx"==n?m:!0}return u(),!1}function g(e,n,t,o,s){var i=e.charAt(0).toUpperCase()+e.slice(1),a=(e+" "+x.join(i+" ")+i).split(" ");return r(n,"string")||r(n,"undefined")?v(a,n,o,s):(a=(e+" "+j.join(i+" ")+i).split(" "),f(a,n,t))}function h(e,n,r){return g(e,t,t,n,r)}var C=[],w=[],S={_version:"3.6.0",_config:{classPrefix:"",enableClasses:!0,enableJSClass:!0,usePrefixes:!0},_q:[],on:function(e,n){var t=this;setTimeout(function(){n(t[e])},0)},addTest:function(e,n,t){w.push({name:e,fn:n,options:t})},addAsyncTest:function(e){w.push({name:null,fn:e})}},Modernizr=function(){};Modernizr.prototype=S,Modernizr=new Modernizr,Modernizr.addTest("es6object",!!(Object.assign&&Object.is&&Object.setPrototypeOf)),Modernizr.addTest("promises",function(){return"Promise"in e&&"resolve"in e.Promise&&"reject"in e.Promise&&"all"in e.Promise&&"race"in e.Promise&&function(){var n;return new e.Promise(function(e){n=e}),"function"==typeof n}()});var P=n.documentElement,_="svg"===P.nodeName.toLowerCase(),b="Moz O ms Webkit",x=S._config.usePrefixes?b.split(" "):[];S._cssomPrefixes=x;var j=S._config.usePrefixes?b.toLowerCase().split(" "):[];S._domPrefixes=j;var z={elem:a("modernizr")};Modernizr._q.push(function(){delete z.elem});var E={style:z.elem.style};Modernizr._q.unshift(function(){delete E.style}),S.testAllProps=g,S.testAllProps=h,Modernizr.addTest("cssanimations",h("animationName","a",!0)),o(),s(C),delete S.addTest,delete S.addAsyncTest;for(var N=0;N<Modernizr._q.length;N++)Modernizr._q[N]();e.Modernizr=Modernizr}(window,document);

    var service = {};
    service.version = '1.0.0';

    // flags for menu items
    service.CLOSE = 1;
    service.OK = 2;
    service.CANCEL = 4;
    service.LINK = 8;
    service.SMALL_ICON = 16;

    var SELECTION = 'selection';
    var FILTER = 'filter';
    var SEPARATOR = '-';

    var header, menu;
    var initialized = false;
    var interactivityAvailable = false;
    var runningInWebportal = false;
    var runningInSeleniumBrowser = false;
    var runningInAPWrapper = false;
    var warnings = {};
    var GLOBAL_SERVICE = null;
    var SVGNS = 'http://www.w3.org/2000/svg';
    var KEY_ENTER = 13;
    
    var messagePageBuilder = function (data) {
        data.nodeId = service.nodeId;
        var messageTarget = window.origin;
        if (typeof messageTarget === 'undefined') {
            messageTarget = window.location.origin;
        } else if (messageTarget === 'null') {
            messageTarget = window;
        }
        parent.postMessage(data, messageTarget);
    };
    
    var initGlobalService = function () {

        GLOBAL_SERVICE = {
            interactivityCallbacks: {}
        };

        var api = 'KnimePageBuilderAPI';
        var pageBuilderWrapper = parent.KnimePageLoader;
        runningInAPWrapper = Boolean(pageBuilderWrapper);
        runningInWebportal = !runningInAPWrapper;
        runningInSeleniumBrowser = runningInAPWrapper ? pageBuilderWrapper.isRunningInSeleniumBrowser() : false;
        
        /* This is always set to true @since 4.2 because some views use this field not just to check for interactivity,
        but also to check for the presence of the PageBuilder. In the AP and new WebPortal, the "new" PageBuilder is
        always present and only other case is if the view is running in the old WebPortal, in which case the old
        PageBuilder will also be present. This should be simplified and reworked in the future it provide separate
        checks for interactivity and other functionality, such as lazy loading, etc. */
        interactivityAvailable = true;

        GLOBAL_SERVICE.isPushSupported = runningInAPWrapper ? pageBuilderWrapper.isPushSupported : function () {
            return false;
        };

        if (runningInAPWrapper) { // lazy loading support
            GLOBAL_SERVICE.requestViewUpdate = pageBuilderWrapper.requestViewUpdate;
            GLOBAL_SERVICE.cancelViewRequest = pageBuilderWrapper.cancelViewRequest;
            GLOBAL_SERVICE.updateRequestStatus = pageBuilderWrapper.updateRequestStatus;
        }
        
        GLOBAL_SERVICE.subscribe = function (id, callback, elementFilter) {
            GLOBAL_SERVICE.interactivityCallbacks[id] = callback;
            messagePageBuilder({
                type: 'interactivitySubscribe',
                id: id,
                elementFilter: elementFilter
            });
        };
        
        GLOBAL_SERVICE.unsubscribe = function (id) {
            delete GLOBAL_SERVICE.interactivityCallbacks[id];
            messagePageBuilder({
                type: 'interactivityUnsubscribe',
                id: id
            });
        };
        
        GLOBAL_SERVICE.publish = function (id, data) {
            messagePageBuilder({
                type: 'interactivityPublish',
                id: id,
                payload: data
            });
        };

        GLOBAL_SERVICE.registerSelectionTranslator = function (translator, translatorID) {
            messagePageBuilder({
                type: 'interactivityRegisterSelectionTranslator',
                id: translatorID,
                translator: translator
            });
        };
        
        GLOBAL_SERVICE.getPublishedData = function (id) {
            if (parent[api] && parent[api].interactivityGetPublishedData) {
                var element = parent[api].interactivityGetPublishedData(id);
                return element;
            } else {
                throw Error('PageBuilder API not available.');
            }
        };
                
        GLOBAL_SERVICE.messageFromPageBuilder = function (event) {
            var dest = !window.origin ? window.location.origin : window.origin;
            
            if (event.origin !== dest) {
                return;
            }
            
            if (event.data.type === 'interactivityEvent' && typeof event.data.id !== 'undefined') {
                var callback = GLOBAL_SERVICE.interactivityCallbacks[event.data.id];
                if (callback) {
                    callback(event.data.payload);
                }
            }
        };
              
        window.addEventListener('message', GLOBAL_SERVICE.messageFromPageBuilder);
    };

    var init = function () {

        if (service.pageBuilderPresent) { // present in AP and new WebPortal since 4.2
            initGlobalService();
        } else { // running in old webportal
            interactivityAvailable = Boolean(parent.KnimePageLoader.publish);
            runningInWebportal = true;
            runningInSeleniumBrowser = false;
            GLOBAL_SERVICE = parent.KnimePageLoader;
        }

        var body = document.getElementsByTagName('body')[0];
        header = document.createElement('nav');
        header.setAttribute('id', 'knime-service-header');
        header.setAttribute('class', 'knime-service-header');
        body.insertBefore(header, body.firstChild);
        initialized = true;
    };
    
    service.getGlobalService = function () {
        if (!initialized) {
            init();
        }
        return GLOBAL_SERVICE;
    };

    service.floatingHeader = function (float) {
        if (!initialized) {
            init();
        }
        header.style.position = float ? 'absolute' : 'static';
    };

    service.headerHeight = function () {
        if (!initialized) {
            init();
        }
        return header.offsetHeight;
    };

    service.isInteractivityAvailable = function () {
        if (!initialized) {
            init();
        }
        return interactivityAvailable;
    };

    service.isRunningInWebportal = function () {
        if (!initialized) { init(); }
        return runningInWebportal;
    };

    service.isRunningInSeleniumBrowser = function () {
        if (!initialized) {
            init();
        }
        return runningInSeleniumBrowser;
    };

    /**
     * @returns {Boolean} - if the view is running in the AP with the new PageBuilder.
     * @since 4.2
     */
    service.isRunningInAPWrapper = function () {
        if (!initialized) { init(); }
        return runningInAPWrapper;
    };

    var enableNav = function () {
        header.style.display = 'block';
    };

    // eslint-disable-next-line max-params
    var addButton = function (id, icon, title, callback, first) {
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
        if (typeof icon === 'number') {
            iContainer.setAttribute('class', 'uicon');
            iContainer.appendChild(document.createTextNode(String.fromCharCode(icon)));
        } else if (typeof icon === 'string') {
            iContainer.className = 'ficon fa fa-fw fa-' + icon;
            iContainer.setAttribute('aria-hidden', 'true');
        }
        if (first === true) {
            header.insertBefore(button, header.firstChild);
        } else if (typeof first === 'string') {
            var inserted = false;
            var c = header.children;
            for (var i = 0; i < c.length; i++) {
                if (c[i].getAttribute('id') === first) {
                    if (i + 1 === c.length) {
                        header.appendChild(button);
                    } else {
                        header.insertBefore(button, c[i + 1]);
                    }
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                header.insertBefore(button, header.firstChild);
            }
        } else {
            header.appendChild(button);
        }
        button.onclick = callback;
        enableNav();
        return button;
    };
    
    var showWarning = function () {
        var message = '';
        var id;
        for (id in warnings) {
            if (warnings[id]) {
                message += warnings[id] + '\n';
            }
        }
        id = 'knime-service-warn-button';
        var button = document.getElementById(id);
        if (message === '') {
            if (button) {
                button.parentNode.removeChild(button);
            }
        } else {
            message = message.substring(0, message.length - 1);
            if (button) {
                button.setAttribute('title', message);
                button.setAttribute('aria-label', message);
            } else {
                if (!initialized) { init(); }
                button = addButton(id, 'exclamation', message, function () {
                    alert(this.getAttribute('title'));
                }, 'knime-service-menu-button');
                button.setAttribute('class', button.getAttribute('class') + ' warn-button');
            }
        }
    };

    service.setWarningMessage = function (message, id) {
        if (typeof id === 'undefined' || id === null) {
            id = 'knimeGenericWarning';
        }
        warnings[id] = message;
        showWarning();
    };

    service.clearWarningMessage = function (id) {
        if (typeof id === 'undefined' || id === null) {
            id = 'knimeGenericWarning';
        }
        if (warnings[id]) {
            delete warnings[id];
        }
        showWarning();
    };

    service.clearAllWarningMessages = function () {
        warnings = {};
        showWarning();
    };
    
    var openMenu = function () {
        var mNav = document.getElementById('knime-service-menu');
        var open = mNav.className === 'open';
        var clazz = open ? '' : 'open';
        mNav.className = clazz;
        var overlay = document.getElementById('knime-service-overlay');
        overlay.className = clazz;
        var button = document.getElementById('knime-service-menu-button');
        button.className = open ? 'service-button' : 'service-button active';
    };

    var initMenu = function () {
        addButton('knime-service-menu-button', 'bars', 'Settings', openMenu, true);
        var overlay = document.createElement('div');
        overlay.setAttribute('id', 'knime-service-overlay');
        overlay.onclick = openMenu;
        header.appendChild(overlay);
        var mNav = document.createElement('nav');
        mNav.setAttribute('id', 'knime-service-menu');
        header.appendChild(mNav);
        menu = document.createElement('ul');
        // menu.setAttribute('class', 'fa-ul');
        mNav.appendChild(menu);
    };

    var publishInteractivityEvent = function (id, data, skip) {
        if (!initialized) {
            init();
        }
        return interactivityAvailable && GLOBAL_SERVICE && GLOBAL_SERVICE.publish(id, data, skip);
    };

    var subscribeToInteractivityEvent = function (id, callback, elementFilter) {
        if (!initialized) {
            init();
        }
        return interactivityAvailable && GLOBAL_SERVICE && GLOBAL_SERVICE.subscribe(id, callback, elementFilter);
    };

    var unsubscribeFromInteractivityEvent = function (id, callback) {
        if (!initialized) {
            init();
        }
        return interactivityAvailable && GLOBAL_SERVICE && GLOBAL_SERVICE.unsubscribe(id, callback);
    };

    var getInteractivityData = function (id) {
        if (!initialized) {
            init();
        }
        if (!interactivityAvailable || !GLOBAL_SERVICE) {
            return null;
        }
        var element = GLOBAL_SERVICE.getPublishedData(id);
        if (!element) {
            element = {};
        }
        if (typeof element.elements === 'undefined') {
            element.elements = [];
        }
        return element;
    };

    service.publishSelection = function (tableId, selection, skip) {
        return publishInteractivityEvent(SELECTION + SEPARATOR + tableId, selection, skip);
    };

    service.registerSelectionTranslator = function (translator, translatorID, callback) {
        if (interactivityAvailable && GLOBAL_SERVICE && GLOBAL_SERVICE.registerSelectionTranslator) {
            GLOBAL_SERVICE.registerSelectionTranslator(translator, translatorID);
        }
    };

    service.subscribeToSelection = function (tableId, callback) {
        return subscribeToInteractivityEvent(SELECTION + SEPARATOR + tableId, callback);
    };

    service.unsubscribeSelection = function (tableId, callback) {
        return unsubscribeFromInteractivityEvent(SELECTION + SEPARATOR + tableId, callback);
    };

    service.publishFilter = function (tableId, filter, skip) {
        return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter, skip);
    };

    service.subscribeToFilter = function (tableId, callback, elementFilter) {
        return subscribeToInteractivityEvent(FILTER + SEPARATOR + tableId, callback, elementFilter);
    };

    service.unsubscribeFilter = function (tableId, callback) {
        return unsubscribeFromInteractivityEvent(FILTER + SEPARATOR + tableId, callback);
    };

    service.addToFilter = function (tableId, filterElement, skip) {
        if (!filterElement || !filterElement.id) {
            return false;
        }
        var filter = getInteractivityData(FILTER + SEPARATOR + tableId);
        if (!filter) {
            return false;
        }
        var existing = false;
        for (var i = 0; i < filter.elements.length; i++) {
            if (filter.elements[i].id === filterElement.id) {
                filter.elements[i] = filterElement;
                existing = true;
                break;
            }
        }
        if (!existing) {
            filter.elements.push(filterElement);
        }
        return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter, skip);
    };

    service.removeFromFilter = function (tableId, elementId, skip) {
        var filter = getInteractivityData(FILTER + SEPARATOR + tableId);
        if (!filter) {
            return false;
        }
        var i = filter.elements.length;
        while (i--) {
            if (filter.elements[i].id === elementId) {
                filter.elements.splice(i, 1);
                return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter, skip);
            }
        }
        return false;
    };
    
    // eslint-disable-next-line max-params
    var addRowsForInteractivityEvent = function (type, tableId, rowKeys, skip, elementId, forceNew) {
        var selection;
        // get or create interactivity element
        var curElement = getInteractivityData(type + SEPARATOR + tableId);
        if (!forceNew) {
            selection = curElement;
        }
        if (!selection) {
            selection = { selectionMethod: type, elements: [] };
        }
        if (!selection.selectionMethod) {
            selection.selectionMethod = type;
        }
        rowKeys = rowKeys || [];
        // create new changeSet
        var updateSelection = { selectionMethod: type, changeSet: {} };
        if (forceNew && curElement && curElement.elements) {
            var curRows = [];
            for (var i = 0; i < curElement.elements.length; i++) {
                if (curElement.elements[i].rows) {
                    curRows = curRows.concat(curElement.elements[i].rows);
                }
            }
            updateSelection.changeSet.added = rowKeys.filter(function (row) {
                return curRows.indexOf(row) < 0;
            });
            updateSelection.changeSet.removed = curRows.filter(function (row) {
                return rowKeys.indexOf(row) < 0;
            });
            updateSelection.changeSet.partialRemoved = curElement.partial;
        } else {
            updateSelection.changeSet.added = rowKeys;
        }
        // only send changeSet
        return publishInteractivityEvent(type + SEPARATOR + tableId, updateSelection, skip);
    };

    service.setSelectedRows = function (tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(SELECTION, tableId, rowKeys, skip, elementId, true);
    };

    service.setFilteredRows = function (tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(FILTER, tableId, rowKeys, skip, elementId, true);
    };

    service.addRowsToSelection = function (tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(SELECTION, tableId, rowKeys, skip, elementId, false);
    };

    service.addRowsToFilter = function (tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(FILTER, tableId, rowKeys, skip, elementId, false);
    };
    
    // eslint-disable-next-line max-params
    var removeRowsFromInteractivityEvent = function (type, tableId, rowKeys, skip, elementId) {
        var selection = getInteractivityData(type + SEPARATOR + tableId);
        if (!selection || !selection.elements && !selection.partial) {
            // nothing to remove
            return false;
        }
        // only send changeSet
        var toRemove = [];
        var partialRemove = [];
        for (var i = 0; i < rowKeys.length; i++) {
            if (selection.partial && selection.partial.indexOf(rowKeys[i]) > -1) {
                partialRemove.push(rowKeys[i]);
            } else {
                toRemove.push(rowKeys[i]);
            }
        }
        var updateSelection = {
            selectionMethod: type,
            changeSet: { removed: toRemove, partialRemoved: partialRemove }
        };
        return publishInteractivityEvent(type + SEPARATOR + tableId, updateSelection, skip);
    };

    service.removeRowsFromSelection = function (tableId, rowKeys, skip, elementId) {
        return removeRowsFromInteractivityEvent(SELECTION, tableId, rowKeys, skip, elementId);
    };

    service.removeRowsFromFilter = function (tableId, rowKeys, skip, elementId) {
        return removeRowsFromInteractivityEvent(FILTER, tableId, rowKeys, skip, elementId);
    };

    service.getAllRowsForSelection = function (tableId, selectionElement) {
        var rows = [];
        var selection = selectionElement;
        if (!selection) {
            selection = getInteractivityData(SELECTION + SEPARATOR + tableId);
        }
        if (selection && selection.elements) {
            for (var i = 0; i < selection.elements.length; i++) {
                if (selection.elements[i].rows) {
                    rows = rows.concat(selection.elements[i].rows);
                }
            }
        }
        return rows;
    };

    service.getAllPartiallySelectedRows = function (tableId) {
        var selection = getInteractivityData(SELECTION + SEPARATOR + tableId);
        return selection.partial || [];
    };

    service.isRowSelected = function (tableId, rowKey) {
        return service.getAllRowsForSelection(tableId).indexOf(rowKey) > -1;
    };

    service.isRowPartiallySelected = function (tableId, rowKey) {
        return service.getAllPartiallySelectedRows(tableId).indexOf(rowKey) > -1;
    };

    // eslint-disable-next-line max-params
    service.addMenuItem = function (title, icon, element, path, flags) {
        if (!initialized) {
            init();
        }
        if (!menu) {
            initMenu();
        }
        var item = document.createElement('li');
        var link = document.createElement('a');
        var useLink = (service.LINK | service.CLOSE | service.OK | service.CANCEL) & flags;
        link.setAttribute('href', '#');
        var leftSpan = document.createElement('span');
        leftSpan.style.float = 'left';
        if (useLink) {
            link.appendChild(leftSpan);
            item.appendChild(link);
        } else {
            item.appendChild(leftSpan);
        }
        if (icon) {
            var iEl = icon;
            if (typeof icon === 'string') {
                iEl = document.createElement('i');
                iEl.className = 'fa fa-fw fa-' + icon;
                iEl.setAttribute('aria-hidden', 'true');
                if (service.SMALL_ICON & flags) {
                    iEl.className += ' small';
                }
            }
            leftSpan.appendChild(iEl);
        } else {
            link.style.marginLeft = '24px';
        }
        if (typeof title === 'string' && title !== '') {
            var text = document.createTextNode(title);
            if (!useLink && typeof element.id !== 'undefined') {
                var label = document.createElement('label');
                label.setAttribute('for', element.id);
                label.appendChild(text);
                leftSpan.appendChild(label);
            } else {
                leftSpan.appendChild(text);
            }
        }
        if (element) {
            // inline element
            if (useLink) {
                item.className = 'menuItem';
            } else {
                element.style.marginLeft = '6px';
                element.style.float = 'right';
                item.appendChild(element);
            }
            // external link element
            if (service.LINK & flags) {
                link.onclick = function (event) {
                    event.preventDefault();
                    openMenu();
                    window.open(element, '_blank');
                };
            }
            /* not implemented yet is:
               - popup element with flags
               - submenus */
        }
        menu.appendChild(item);
        return item;
    };

    service.createStackedIcon = function (firstIcon, secondIcon, firstClasses, secondClasses) {
        var stack = document.createElement('span');
        stack.className = 'fa-fw fa-stack';
        var fEl = document.createElement('i');
        fEl.className = 'fa fa-' + firstIcon + ' fa-stack-1x ' + firstClasses;
        stack.appendChild(fEl);
        var sEl = document.createElement('i');
        sEl.className = 'fa fa-' + secondIcon + ' fa-stack-1x ' + secondClasses;
        stack.appendChild(sEl);
        return stack;
    };
    
    var setFieldDefaults = function (field, id, width) {
        field.setAttribute('id', id);
        field.setAttribute('name', id);
        field.style.fontSize = '12px';
        if (width) {
            field.style.width = width;
        }
        field.style.margin = '0';
        field.style.outlineOffset = '-3px';
        return field;
    };

    service.createMenuTextField = function (id, initialValue, callback, immediate) {
        var textField = document.createElement('input');
        textField.setAttribute('type', 'text');
        setFieldDefaults(textField, id, '150px');
        if (callback) {
            if (immediate) {
                if (typeof textField.oninput === 'undefined') {
                    textField.addEventListener('keyup', callback);
                } else {
                    textField.addEventListener('input', callback);
                }
            } else {
                textField.addEventListener('keypress', function (event) {
                    if (event.keyCode === KEY_ENTER) {
                        callback.apply(this);
                    }
                });
                textField.addEventListener('blur', callback);
            }
        }
        if (initialValue) {
            textField.value = initialValue;
        }
        return textField;
    };

    // eslint-disable-next-line
    service.createMenuNumberField = function (id, initialValue, minimum, maximum, step, callback, immediate) {
        var numberField = document.createElement('input');
        numberField.setAttribute('type', 'number');
        setFieldDefaults(numberField, id, '75px');
        if (typeof minimum === 'number') {
            numberField.setAttribute('min', minimum);
        }
        if (typeof maximum === 'number') {
            numberField.setAttribute('max', maximum);
        }
        if (typeof step === 'number') {
            numberField.setAttribute('step', step);
        }
        if (callback) {
            if (immediate) {
                if (typeof numberField.oninput === 'undefined') {
                    numberField.addEventListener('change', callback);
                } else {
                    numberField.addEventListener('input', callback);
                }
            } else {
                numberField.addEventListener('keypress', function (event) {
                    if (event.keyCode === KEY_ENTER) {
                        callback.apply(this);
                    }
                });
                numberField.addEventListener('blur', callback);
            }
        }
        if (typeof initialValue === 'number') {
            numberField.setAttribute('value', initialValue);
        }
        return numberField;
    };

    service.createMenuCheckbox = function (id, initialState, callback, value) {
        var checkbox = document.createElement('input');
        checkbox.setAttribute('id', id);
        checkbox.setAttribute('type', 'checkbox');
        checkbox.setAttribute('name', id);
        if (typeof value !== 'undefined') {
            checkbox.setAttribute('value', value);
        }
        checkbox.checked = initialState;
        checkbox.style.margin = '4px 0 0';
        checkbox.style.padding = '0';
        if (callback) {
            checkbox.addEventListener('change', callback);
        }
        return checkbox;
    };

    service.createMenuSelect = function (id, initialValue, options, callback) {
        var select = document.createElement('select');
        setFieldDefaults(select, id, '150px');
        for (var oId = 0; oId < options.length; oId++) {
            var option = document.createElement('option');
            option.setAttribute('value', options[oId]);
            option.appendChild(document.createTextNode(options[oId]));
            select.appendChild(option);
        }
        if (callback) {
            select.addEventListener('change', callback);
        }
        if (initialValue) {
            select.value = initialValue;
        }
        return select;
    };

    service.createMenuRadioButton = function (id, name, value, callback) {
        var radio = document.createElement('input');
        radio.setAttribute('type', 'radio');
        radio.setAttribute('name', name);
        radio.setAttribute('id', id);
        radio.setAttribute('value', value);
        if (callback) {
            radio.addEventListener('change', callback);
        }
        return radio;
    };

    // eslint-disable-next-line
    service.createInlineMenuRadioButtons = function (id, name, initialValue, options, callback) {
        var container = document.createElement('span');
        container.setAttribute('id', id);
        container.className = 'radioGroup';
        for (var oId = 0; oId < options.length; oId++) {
            var value = options[oId];
            var rId = name + '_' + value;
            var radio = service.createMenuRadioButton(rId, name, value, callback);
            radio.checked = value === initialValue;
            container.appendChild(radio);
            var label = document.createElement('label');
            label.setAttribute('for', rId);
            label.appendChild(document.createTextNode(value));
            container.appendChild(label);
        }
        return container;
    };

    service.addMenuDivider = function () {
        if (!initialized) {
            init();
        }
        if (!menu) {
            initMenu();
        }
        var divider = document.createElement('li');
        divider.className = 'divider';
        menu.appendChild(divider);
        return divider;
    };

    service.addButton = function (id, icon, title, callback) {
        if (!initialized) {
            init();
        }
        return addButton(id, icon, title, callback, false);
    };

    service.allowFullscreen = function (element) {
        if (!initialized) {
            init();
        }
        var fullScreenAvailable = runningInSeleniumBrowser || runningInWebportal || service.pageBuilderPresent;
        return interactivityAvailable && screenfull.enabled && fullScreenAvailable &&
            addButton('knime-service-fullscreen-button', 'arrows-alt', 'Toggle Fullscreen', function () {
                if (screenfull.enabled) {
                    screenfull.toggle(element);
                }
            });
    };

    service.addNavSpacer = function () {
        if (!initialized) { init(); }
        var spacer = document.createElement('div');
        spacer.className = 'service-nav-spacer';
        header.appendChild(spacer);
    };

    service.isViewRequestsSupported = function () {
        return Modernizr.promises && Modernizr.es6object && parent.KnimePageLoader.isPushSupported();
    };

    /**
     * Escape special characters in a string which results in a valid DOM element id
     *
     * @param {*} id
     * @return {string} the escaped string
     */
    service.cssEscapeId = function (id) {
        var string = id.toString();
        var matchesBeginning = /^[a-z]+/i;
        if (!matchesBeginning.test(string)) {
            // if ID doesn't start with letter add prefix
            string = 'knid_' + string;
        }
        // replace all special characters by underscores
        return string.replace(/^[^a-z]+|[^\w:-]+/gi, '___');
    };
    
    var treatCssIssues = function (rule) {
        var resultRule = rule.cssText;
        // Split rgba into rgb and oppacity, as Batik is not able to handle rgba
        if (rule.cssText.includes('rgba')) {
            var result = [];
            var rgbaString = [];
            var stringsToReplace = [];
            // RegExp to find rgba values
            var findRGBAValues = /(.*?)rgba\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*,\s*([0-9]+\.[0-9]+|\d\s*)\)/g;

            /* eslint-disable no-magic-numbers */
            // Find all rgba values and the according css string to replace it later on
            while ((result = findRGBAValues.exec(rule.cssText)) !== null) {
                stringsToReplace.push(result[0].match(/\w+(?=:\s*rgba):\s*rgba(.*?)\)/g));
                rgbaString.push([[result[0].match(/\w+(?=:\s*rgba)/g)],
                    [result[result.length - 4]],
                    [result[result.length - 3]],
                    [result[result.length - 2]],
                    [result[result.length - 1]]]);
            }
            /* eslint-enable no-magic-numbers */
            
            // Loop over all found rgba values and replace them
            for (var rgbaIndex = 0; rgbaIndex < rgbaString.length; rgbaIndex++) {
                var rgbString = rgbaString[rgbaIndex][0] + ': rgb(' +
                rgbaString[rgbaIndex][1] + ',' +
                rgbaString[rgbaIndex][2] + ',' +
                rgbaString[rgbaIndex][3] + ');';
                var opacityString = rgbaString[rgbaIndex][0] + '-opacity: ' + rgbaString[rgbaIndex][4] + '; ';
                resultRule = resultRule.replace(stringsToReplace[rgbaIndex], rgbString + opacityString);
            }
        } else if (rule.cssText.includes('transparent')) {
            resultRule = rule.cssText.replace(new RegExp('transparent', 'g'), 'none');
        }
        return resultRule;
    };

    /**
     * Inline global style declarations for SVG export
     *
     * @param {element} svg
     * @return {undefined}
     */
    service.inlineSvgStyles = function (svg) {
        var before = svg.firstChild;
        var styles = document.styleSheets;
        var newStyles = [];
        for (var styleIndex = 0; styleIndex < styles.length; styleIndex++) {
            if (!styles[styleIndex].cssRules && styles[styleIndex].rules) {
                styles[styleIndex].cssRules = styles[styleIndex].rules;
            }
            // empty style declaration
            if (!styles[styleIndex].cssRules) { continue; }

            var cssText = [];
            for (var ruleIndex = 0; ruleIndex < styles[styleIndex].cssRules.length; ruleIndex++) {
                try {
                    var rule = styles[styleIndex].cssRules[ruleIndex];
                    if (svg.querySelector(rule.selectorText)) {
                        cssText.push(treatCssIssues(rule));
                    }
                } catch (exception) {
                    continue;
                }
            }
            if (cssText.length > 0) {
                var styleElement = document.createElementNS(SVGNS, 'style');
                styleElement.type = 'text/css';
                styleElement.appendChild(document.createTextNode(cssText.join('\n')));
                newStyles.push(styleElement);
            }
        }
        for (var nsI = 0; nsI < newStyles.length; nsI++) {
            svg.insertBefore(newStyles[nsI], before);
        }
    };
    
    /**
     * Function to wrap text after it exceeds a calculated size. The size depends on the provided maxWidth and
     * maxHeight. Returns the calculated wrapped string.
     *
     * @param {element} textElement - the text element which should be wrappped
     * @param {number} maxWidth - the maximum width the text is allowed to occupy
     * @param {number} maxHeight - the maximum height the text is allowed to occupy
     * @param {number} minimalChars - the minimum amount of characters to be preserved (overrides maxWidth and height)
     * @returns {object} an object containing the wrapped text and a rect structure providing width and height of the
     * wrapped text
     */
    var wrapLabels = function (textElement, maxWidth, maxHeight, minimalChars) {
        var ellipsis = '\u2026';
        var textRect = textElement.getBoundingClientRect();
        var text = textElement.textContent;
        if (textRect.width > maxWidth || textRect.height > maxHeight) {
            var guessFactor = 1;
            text += ellipsis;
            while ((textRect.width > maxWidth || textRect.height > maxHeight) && text.length > minimalChars) {
                var heightDiff = 0;
                var widthDiff = 0;
                if (textRect.width > maxWidth) {
                    widthDiff = textRect.width - maxWidth;
                }
                if (textRect.height > maxHeight) {
                    heightDiff = textRect.height - maxHeight;
                }
                if (widthDiff > heightDiff) {
                    guessFactor = widthDiff / textRect.width;
                } else {
                    guessFactor = heightDiff / textRect.height;
                }

                text = text.slice(0, Math.min(Math.floor(-text.length * guessFactor), -1));
                if (text.length >= minimalChars) {
                    textElement.textContent = text + ellipsis;
                } else {
                    text = textElement.textContent.substring(0, minimalChars);
                    textElement.textContent = text + ellipsis;
                }
                textRect = textElement.getBoundingClientRect();
            }
        }
        text = textElement.textContent;
        return { text: text, rect: textRect };
    };

    /**
     * Function to measure and truncate the sizes of the given data array within an SVG. In the config object a
     * container element must be specified. Additionally there are several optional parameters which are explained in
     * the following: <br>
     *  - container: Mandatory container object, which needs to be inside the SVG element or the SVG element
     * itself classes: CSS-classes of the created text elements (optional) <br>
     *  - attributes: Attributes of the created text elements (optional)<br>
     *  - tempContainer: A temporary container in which the text elements are created in. If undefined
     * a "g" element is created. If an empty string is provided, it will append the text directly to the provided
     * top-level container.<br>
     *  - tempContainerClasses: CSS-classes of the created temporary container element (optional)<br>
     *  - tempContainerAttributes: CSS-attributes of the created temporary container element (optional)<br>
     *  - maxWidth: The maximum width each text element can occupy. If the maxWidth is exceeded, an ellipsis is applied
     * to fit the appropriate space. (optional)<br>
     *  - maxHeight: The maximum height each text element can occupy. If the maxHeight is exceeded, an ellipsis is
     * applied to fit the appropriate space. (optional)<br>
     *  - minimalChars: The minimal amount of chars which should still be displayed even when a text element with
     * ellipsis is larger than the maximum dimensions specified.
     * @param {array} data - an array of data to measure and treat
     * @param {object} config - the config object
     * @return {object} an object with the following fields: values: originalData: The original strings
     * provided truncated: The (potentially) truncated strings of the original data widths: Measured width of each text
     * element heights: Measured height of each text element max: maxWidth: maximum measured width of all text elements
     * maxHeight: maximum measured height of all text elements
     */
    service.measureAndTruncate = function (data, config) {
        var tempTextList = [];
        var values = [];
        var containerClass, group, key;
        var minChars = config.minimalChars ? config.minimalChars : 1;
        if (typeof config.tempContainer  === 'undefined' || config.tempContainer === null) {
            containerClass = 'g';
        } else {
            containerClass = config.tempContainer;
        }
        var maxHeight = 0;
        var maxWidth = 0;
        if (containerClass !== '') {
            group = document.createElementNS(SVGNS, containerClass);
            if (config.tempContainerClasses) {
                group.setAttribute('class', config.tempContainerClasses);
            }
            if (config.tempContainerAttributes) {
                for (key in config.tempContainerAttributes) {
                    group.setAttribute(key, config.tempContainerAttributes[key]);
                }
            }
            config.container.appendChild(group);
        }

        data.forEach(function (value) {
            var tempText = document.createElementNS(SVGNS, 'text');
            tempText.textContent = value;
            if (config.classes) {
                tempText.setAttribute('class', config.classes);
            }
            if (config.attributes) {
                for (key in config.attributes) {
                    tempText.setAttribute(key, config.attributes[key]);
                }
            }

            if (group) {
                group.appendChild(tempText);
            } else {
                config.container.appendChild(tempText);
                tempTextList.push(tempText);
            }
            var wrapResult = wrapLabels(tempText, config.maxWidth, config.maxHeight, minChars);
            var truncated = wrapResult.text;
            var tempWidth = wrapResult.rect.width;
            var tempHeight = wrapResult.rect.height;

            maxWidth = tempWidth > maxWidth ? tempWidth : maxWidth;
            maxHeight = tempHeight > maxHeight ? tempHeight : maxHeight;
            values.push({ originalData: value, truncated: truncated, width: tempWidth, height: tempHeight });
        });

        // Delete all texts which where appended to a temporal container
        if (group) {
            group.parentNode.removeChild(group);
        } else {
            // Delete all texts which where directly appended to the container
            tempTextList.forEach(function (text) {
                text.parentNode.removeChild(text);
            });
        }

        return { values: values, max: { maxWidth: maxWidth, maxHeight: maxHeight } };
    };

    service.log = function (message) {
        if (console && console.log) {
            console.log(message);
        }
    };
    
    service.logError = function (err) {
        if (console && console.error) {
            console.error(err);
        }
    };

    document.addEventListener('DOMContentLoaded', init, false);

    return service;
})();
