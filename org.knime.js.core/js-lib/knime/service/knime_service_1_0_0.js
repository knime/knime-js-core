knimeService = function() {

    /*! screenfull v3.0.0 - 2015-11-24 (c) Sindre Sorhus; MIT License !*/
    !function(){"use strict";var a="undefined"!=typeof module&&module.exports,b="undefined"!=typeof Element&&"ALLOW_KEYBOARD_INPUT"in Element,c=function(){for(var a,b,c=[["requestFullscreen","exitFullscreen","fullscreenElement","fullscreenEnabled","fullscreenchange","fullscreenerror"],["webkitRequestFullscreen","webkitExitFullscreen","webkitFullscreenElement","webkitFullscreenEnabled","webkitfullscreenchange","webkitfullscreenerror"],["webkitRequestFullScreen","webkitCancelFullScreen","webkitCurrentFullScreenElement","webkitCancelFullScreen","webkitfullscreenchange","webkitfullscreenerror"],["mozRequestFullScreen","mozCancelFullScreen","mozFullScreenElement","mozFullScreenEnabled","mozfullscreenchange","mozfullscreenerror"],["msRequestFullscreen","msExitFullscreen","msFullscreenElement","msFullscreenEnabled","MSFullscreenChange","MSFullscreenError"]],d=0,e=c.length,f={};e>d;d++)if(a=c[d],a&&a[1]in document){for(d=0,b=a.length;b>d;d++)f[c[0][d]]=a[d];return f}return!1}(),d={request:function(a){var d=c.requestFullscreen;a=a||document.documentElement,/5\.1[\.\d]* Safari/.test(navigator.userAgent)?a[d]():a[d](b&&Element.ALLOW_KEYBOARD_INPUT)},exit:function(){document[c.exitFullscreen]()},toggle:function(a){this.isFullscreen?this.exit():this.request(a)},raw:c};return c?(Object.defineProperties(d,{isFullscreen:{get:function(){return Boolean(document[c.fullscreenElement])}},element:{enumerable:!0,get:function(){return document[c.fullscreenElement]}},enabled:{enumerable:!0,get:function(){return Boolean(document[c.fullscreenEnabled])}}}),void(a?module.exports=d:window.screenfull=d)):void(a?module.exports=!1:window.screenfull=!1)}();

    /*! modernizr 3.6.0 (Custom Build) | MIT *
     * https://modernizr.com/download/?-cssanimations-es6object-promises-setclasses !*/
    !function(e,n,t){function r(e,n){return typeof e===n}function o(){var e,n,t,o,s,i,a;for(var l in w)if(w.hasOwnProperty(l)){if(e=[],n=w[l],n.name&&(e.push(n.name.toLowerCase()),n.options&&n.options.aliases&&n.options.aliases.length))for(t=0;t<n.options.aliases.length;t++)e.push(n.options.aliases[t].toLowerCase());for(o=r(n.fn,"function")?n.fn():n.fn,s=0;s<e.length;s++)i=e[s],a=i.split("."),1===a.length?Modernizr[a[0]]=o:(!Modernizr[a[0]]||Modernizr[a[0]]instanceof Boolean||(Modernizr[a[0]]=new Boolean(Modernizr[a[0]])),Modernizr[a[0]][a[1]]=o),C.push((o?"":"no-")+a.join("-"))}}function s(e){var n=P.className,t=Modernizr._config.classPrefix||"";if(_&&(n=n.baseVal),Modernizr._config.enableJSClass){var r=new RegExp("(^|\\s)"+t+"no-js(\\s|$)");n=n.replace(r,"$1"+t+"js$2")}Modernizr._config.enableClasses&&(n+=" "+t+e.join(" "+t),_?P.className.baseVal=n:P.className=n)}function i(e,n){return!!~(""+e).indexOf(n)}function a(){return"function"!=typeof n.createElement?n.createElement(arguments[0]):_?n.createElementNS.call(n,"http://www.w3.org/2000/svg",arguments[0]):n.createElement.apply(n,arguments)}function l(e){return e.replace(/([a-z])-([a-z])/g,function(e,n,t){return n+t.toUpperCase()}).replace(/^-/,"")}function u(e,n){return function(){return e.apply(n,arguments)}}function f(e,n,t){var o;for(var s in e)if(e[s]in n)return t===!1?e[s]:(o=n[e[s]],r(o,"function")?u(o,t||n):o);return!1}function c(e){return e.replace(/([A-Z])/g,function(e,n){return"-"+n.toLowerCase()}).replace(/^ms-/,"-ms-")}function d(n,t,r){var o;if("getComputedStyle"in e){o=getComputedStyle.call(e,n,t);var s=e.console;if(null!==o)r&&(o=o.getPropertyValue(r));else if(s){var i=s.error?"error":"log";s[i].call(s,"getComputedStyle returning null, its possible modernizr test results are inaccurate")}}else o=!t&&n.currentStyle&&n.currentStyle[r];return o}function p(){var e=n.body;return e||(e=a(_?"svg":"body"),e.fake=!0),e}function m(e,t,r,o){var s,i,l,u,f="modernizr",c=a("div"),d=p();if(parseInt(r,10))for(;r--;)l=a("div"),l.id=o?o[r]:f+(r+1),c.appendChild(l);return s=a("style"),s.type="text/css",s.id="s"+f,(d.fake?d:c).appendChild(s),d.appendChild(c),s.styleSheet?s.styleSheet.cssText=e:s.appendChild(n.createTextNode(e)),c.id=f,d.fake&&(d.style.background="",d.style.overflow="hidden",u=P.style.overflow,P.style.overflow="hidden",P.appendChild(d)),i=t(c,e),d.fake?(d.parentNode.removeChild(d),P.style.overflow=u,P.offsetHeight):c.parentNode.removeChild(c),!!i}function y(n,r){var o=n.length;if("CSS"in e&&"supports"in e.CSS){for(;o--;)if(e.CSS.supports(c(n[o]),r))return!0;return!1}if("CSSSupportsRule"in e){for(var s=[];o--;)s.push("("+c(n[o])+":"+r+")");return s=s.join(" or "),m("@supports ("+s+") { #modernizr { position: absolute; } }",function(e){return"absolute"==d(e,null,"position")})}return t}function v(e,n,o,s){function u(){c&&(delete E.style,delete E.modElem)}if(s=r(s,"undefined")?!1:s,!r(o,"undefined")){var f=y(e,o);if(!r(f,"undefined"))return f}for(var c,d,p,m,v,g=["modernizr","tspan","samp"];!E.style&&g.length;)c=!0,E.modElem=a(g.shift()),E.style=E.modElem.style;for(p=e.length,d=0;p>d;d++)if(m=e[d],v=E.style[m],i(m,"-")&&(m=l(m)),E.style[m]!==t){if(s||r(o,"undefined"))return u(),"pfx"==n?m:!0;try{E.style[m]=o}catch(h){}if(E.style[m]!=v)return u(),"pfx"==n?m:!0}return u(),!1}function g(e,n,t,o,s){var i=e.charAt(0).toUpperCase()+e.slice(1),a=(e+" "+x.join(i+" ")+i).split(" ");return r(n,"string")||r(n,"undefined")?v(a,n,o,s):(a=(e+" "+j.join(i+" ")+i).split(" "),f(a,n,t))}function h(e,n,r){return g(e,t,t,n,r)}var C=[],w=[],S={_version:"3.6.0",_config:{classPrefix:"",enableClasses:!0,enableJSClass:!0,usePrefixes:!0},_q:[],on:function(e,n){var t=this;setTimeout(function(){n(t[e])},0)},addTest:function(e,n,t){w.push({name:e,fn:n,options:t})},addAsyncTest:function(e){w.push({name:null,fn:e})}},Modernizr=function(){};Modernizr.prototype=S,Modernizr=new Modernizr,Modernizr.addTest("es6object",!!(Object.assign&&Object.is&&Object.setPrototypeOf)),Modernizr.addTest("promises",function(){return"Promise"in e&&"resolve"in e.Promise&&"reject"in e.Promise&&"all"in e.Promise&&"race"in e.Promise&&function(){var n;return new e.Promise(function(e){n=e}),"function"==typeof n}()});var P=n.documentElement,_="svg"===P.nodeName.toLowerCase(),b="Moz O ms Webkit",x=S._config.usePrefixes?b.split(" "):[];S._cssomPrefixes=x;var j=S._config.usePrefixes?b.toLowerCase().split(" "):[];S._domPrefixes=j;var z={elem:a("modernizr")};Modernizr._q.push(function(){delete z.elem});var E={style:z.elem.style};Modernizr._q.unshift(function(){delete E.style}),S.testAllProps=g,S.testAllProps=h,Modernizr.addTest("cssanimations",h("animationName","a",!0)),o(),s(C),delete S.addTest,delete S.addAsyncTest;for(var N=0;N<Modernizr._q.length;N++)Modernizr._q[N]();e.Modernizr=Modernizr}(window,document);

    var service = {};
    service.version = "1.0.0";

    // flags for menu items
    service.CLOSE = 1;
    service.OK = 2;
    service.CANCEL = 4;
    service.LINK = 8;
    service.SMALL_ICON = 16;

    var SELECTION = 'selection', FILTER = 'filter', SEPARATOR = '-';

    var header, menu;
    var initialized = false, interactivityAvailable = false, runningInWebportal = false, runningInSeleniumBrowser = false;
    var warnings = {};
    var viewRequests = [], requestSequence = 0, responseBuffer = [], pushSupported;
    var GLOBAL_SERVICE = this;
    var SVGNS = "http://www.w3.org/2000/svg";

    init = function() {
        if (parent && parent.KnimePageLoader) {
            try {
                runningInWebportal = parent.KnimePageLoader.isRunningInWebportal();
            } catch (err) {
                runningInWebportal = false
            }
            if (parent.KnimePageLoader.publish) {
                interactivityAvailable = true;
                GLOBAL_SERVICE = parent.KnimePageLoader;
            }
            runningInSeleniumBrowser = parent.KnimePageLoader.isRunningInSeleniumBrowser();
        } else {
            runningInSeleniumBrowser = typeof parent.seleniumKnimeBridge !== 'undefined';
        }
        viewRequests = [], requestSequence = 0;

        var body = document.getElementsByTagName('body')[0];
        header = document.createElement('nav');
        header.setAttribute('id', 'knime-service-header');
        header.setAttribute('class', 'knime-service-header');
        body.insertBefore(header, body.firstChild);
        initialized = true;
    }

    service.getGlobalService = function() {
        initialized || init();
        return GLOBAL_SERVICE;
    }

    service.floatingHeader = function(float) {
        initialized || init();
        header.style.position = float ? 'absolute' : 'static';
    }

    service.headerHeight = function() {
        initialized || init();
        return header.offsetHeight;
    }

    service.isInteractivityAvailable = function() {
        initialized || init();
        return interactivityAvailable;
    }

    service.isRunningInWebportal = function() {
        initialized || init();
        return runningInWebportal;
    }

    service.isRunningInSeleniumBrowser = function() {
        initialized || init();
        return runningInSeleniumBrowser;
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
        if (first === true) {
            header.insertBefore(button, header.firstChild);
        } else if (typeof first == 'string') {
            var inserted = false;
            var c = header.children;
            for (var i = 0; i < c.length; i++) {
                if (c[i].getAttribute('id') === first) {
                    if (i + 1 == c.length) {
                        header.appendChild(button);
                    } else {
                        header.insertBefore(button, c[i+1]);
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
    }

    service.setWarningMessage = function(message, id) {
        if (typeof id == 'undefined' || id == null) {
            id = "knimeGenericWarning";
        }
        warnings[id] = message;
        showWarning();
    }

    service.clearWarningMessage = function(id) {
        if (typeof id == 'undefined' || id == null) {
            id = "knimeGenericWarning";
        }
        if (warnings[id]) {
            delete warnings[id];
        }
        showWarning();
    }

    service.clearAllWarningMessages = function() {
        warnings = {};
        showWarning();
    }

    showWarning = function() {
        var message = '';
        for (var id in warnings) {
            if (warnings[id]) {
                message += warnings[id] + '\n';
            }
        }
        var id = 'knime-service-warn-button';
        var button = document.getElementById(id);
        if (message !== '') {
            message = message.substring(0, message.length - 1);
            if (button) {
                button.setAttribute('title', message);
                button.setAttribute('aria-label', message);
            } else {
                initialized || init();
                button = addButton(id, 'exclamation', message, function() {
                    alert(this.getAttribute('title'));
                }, 'knime-service-menu-button');
                button.setAttribute('class', button.getAttribute('class') + ' warn-button');
            }
        } else {
            if (button) {
                button.parentNode.removeChild(button);
            }
        }
    }

    initMenu = function() {
        addButton('knime-service-menu-button', 'bars', 'Settings', openMenu, true);
        var overlay = document.createElement('div');
        overlay.setAttribute('id', 'knime-service-overlay');
        overlay.onclick = openMenu;
        header.appendChild(overlay);
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
        var clazz = open ? '' : 'open';
        mNav.className = clazz;
        var overlay = document.getElementById('knime-service-overlay');
        overlay.className = clazz;
        var button = document.getElementById('knime-service-menu-button');
        button.className = open ? 'service-button' : 'service-button active';
    }

    publishInteractivityEvent = function(id, data, skip) {
        initialized || init();
        return interactivityAvailable && GLOBAL_SERVICE.publish(id, data, skip);
    }

    subscribeToInteractivityEvent = function(id, callback, elementFilter) {
        initialized || init();
        return interactivityAvailable && GLOBAL_SERVICE.subscribe(id, callback, elementFilter);
    }

    unsubscribeFromInteractivityEvent = function(id, callback) {
        initialized || init();
        return interactivityAvailable && GLOBAL_SERVICE.unsubscribe(id, callback);
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
            element.elements = [];
        }
        return element;
    }

    service.publishSelection = function(tableId, selection, skip) {
        return publishInteractivityEvent(SELECTION + SEPARATOR + tableId, selection, skip);
    }

    service.registerSelectionTranslator = function(translator, translatorID, callback) {
        if(parent.KnimePageLoader) {
            parent.KnimePageLoader.registerSelectionTranslator(translator, translatorID);
        }
    }

    service.subscribeToSelection = function(tableId, callback) {
        return subscribeToInteractivityEvent(SELECTION + SEPARATOR + tableId, callback);
    }

    service.unsubscribeSelection = function(tableId, callback) {
        return unsubscribeFromInteractivityEvent(SELECTION + SEPARATOR + tableId, callback);
    }

    service.publishFilter = function(tableId, filter, skip) {
        return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter, skip);
    }

    service.subscribeToFilter = function(tableId, callback, elementFilter) {
        return subscribeToInteractivityEvent(FILTER + SEPARATOR + tableId, callback, elementFilter);
    }

    service.unsubscribeFilter = function(tableId, callback) {
        return unsubscribeFromInteractivityEvent(FILTER + SEPARATOR + tableId, callback);
    }

    service.addToFilter = function(tableId, filterElement, skip) {
        if (!filterElement || !filterElement.id) {
            return false;
        }
        var filter = getInteractivityElement(FILTER + SEPARATOR + tableId);
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
        return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter, skip);		
    }

    service.removeFromFilter = function(tableId, elementId, skip) {
        var filter = getInteractivityElement(FILTER + SEPARATOR + tableId);
        if (!filter) {
            return false;
        }
        var i = filter.elements.length;
        while (i--) {
            if (filter.elements[i].id == filterElement.id) {
                filter.elements.splice(i, 1);
                return publishInteractivityEvent(FILTER + SEPARATOR + tableId, filter, skip);
            }
        }
        return false;
    }

    service.setSelectedRows = function(tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(SELECTION, tableId, rowKeys, skip, elementId, true);
    }

    service.setFilteredRows = function(tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(FILTER, tableId, rowKeys, skip, elementId, true);
    }

    service.addRowsToSelection = function(tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(SELECTION, tableId, rowKeys, skip, elementId, false);
    }

    service.addRowsToFilter = function(tableId, rowKeys, skip, elementId) {
        return addRowsForInteractivityEvent(FILTER, tableId, rowKeys, skip, elementId, false);
    }

    addRowsForInteractivityEvent = function(type, tableId, rowKeys, skip, elementId, forceNew) {
        var selection;
        // get or create interactivity element
        var curElement = getInteractivityElement(type + SEPARATOR + tableId); 
        if (!forceNew) {
            selection = curElement;
        }
        if (!selection) {
            selection = {'selectionMethod': type, 'elements': []};
        }
        if (!selection.selectionMethod) {
            selection.selectionMethod = type; 
        }
        rowKeys = rowKeys || [];
        // create new changeSet
        var updateSelection = {'selectionMethod': type, 'changeSet': {}};
        if (forceNew && curElement && curElement.elements) {
            var curRows = [];
            for (var i = 0; i < curElement.elements.length; i++) {
                if (curElement.elements[i].rows) {
                    curRows = curRows.concat(curElement.elements[i].rows);
                }
            }
            updateSelection.changeSet.added = rowKeys.filter(function(row) {
                return curRows.indexOf(row) < 0;
            });
            updateSelection.changeSet.removed = curRows.filter(function(row) {
                return rowKeys.indexOf(row) < 0;
            });
            updateSelection.changeSet.partialRemoved = curElement.partial;
        } else {
            updateSelection.changeSet.added = rowKeys;
        }
        // only send changeSet
        return publishInteractivityEvent(type + SEPARATOR + tableId, updateSelection, skip);
    }

    service.removeRowsFromSelection = function(tableId, rowKeys, skip, elementId) {
        return removeRowsFromInteractivityEvent(SELECTION, tableId, rowKeys, skip, elementId);
    }

    service.removeRowsFromFilter = function(tableId, rowKeys, skip, elementId) {
        return removeRowsFromInteractivityEvent(FILTER, tableId, rowKeys, skip, elementId);
    }

    removeRowsFromInteractivityEvent = function(type, tableId, rowKeys, skip, elementId) {
        var selection = getInteractivityElement(type + SEPARATOR + tableId);
        if (!selection || (!selection.elements && !selection.partial)) {
            // nothing to remove
            return false;
        }
        // only send changeSet
        var toRemove = [], partialRemove = [];
        for (var i = 0; i < rowKeys.length; i++) {
            if (selection.partial && selection.partial.indexOf(rowKeys[i]) > -1) {
                partialRemove.push(rowKeys[i]);
            } else {
                toRemove.push(rowKeys[i]);
            }
        }
        var updateSelection = {'selectionMethod': type, 
                'changeSet': {'removed': toRemove, 'partialRemoved': partialRemove}};
        return publishInteractivityEvent(type + SEPARATOR + tableId, updateSelection, skip);
    }

    service.getAllRowsForSelection = function(tableId, selectionElement) {
        var rows = [];
        var selection = selectionElement;
        if (!selection) {
            selection = getInteractivityElement(SELECTION + SEPARATOR + tableId);
        }
        if (selection && selection.elements) {
            for (var i = 0; i < selection.elements.length; i++) {
                if (selection.elements[i].rows) {
                    rows = rows.concat(selection.elements[i].rows);
                }
            }
        }
        return rows;
    }

    service.getAllPartiallySelectedRows = function(tableId) {
        var selection = getInteractivityElement(SELECTION + SEPARATOR + tableId);
        return selection.partial || [];
    }

    service.isRowSelected = function(tableId, rowKey) {
        return service.getAllRowsForSelection(tableId).indexOf(rowKey) > -1;
    }

    service.isRowPartiallySelected = function(tableId, rowKey) {
        return service.getAllPartiallySelectedRows(tableId).indexOf(rowKey) > -1;
    }

    service.addMenuItem = function(title, icon, element, path, flags) {
        initialized || init();
        menu || initMenu();
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
            if (typeof icon == 'string') {
                iEl = document.createElement('i');
                iEl.className = 'fa fa-fw fa-' + icon;
                iEl.setAttribute('aria-hidden', 'true');
                if (service.SMALL_ICON & flags) {
                    iEl.className = iEl.className + " small"
                }
            }
            leftSpan.appendChild(iEl);
        } else {
            link.style.marginLeft = '24px';
        }
        if (typeof title == 'string' && title != '') {
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
            if (!useLink) {
                element.style.marginLeft = '6px';
                element.style.float = 'right';
                item.appendChild(element);
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

            //TODO: add submenu items
        }
        menu.appendChild(item);
        return item;
    }

    service.createStackedIcon = function(firstIcon, secondIcon, firstClasses, secondClasses) {
        var stack = document.createElement('span');
        stack.className = 'fa-fw fa-stack';
        var fEl = document.createElement('i');
        fEl.className = 'fa fa-' + firstIcon + ' fa-stack-1x ' + firstClasses;
        stack.appendChild(fEl);
        var sEl = document.createElement('i');
        sEl.className = 'fa fa-' + secondIcon + ' fa-stack-1x ' + secondClasses;
        stack.appendChild(sEl);
        return stack;
    }

    service.createMenuTextField = function(id, initialValue, callback, immediate) {
        var textField = document.createElement('input');
        textField.setAttribute('type', 'text');
        setFieldDefaults(textField, id, '150px');
        if (callback) {
            if (immediate) {
                if (typeof textField.oninput !== 'undefined') {
                    textField.addEventListener('input', callback);
                } else {
                    textField.addEventListener('keyup', callback);
                }
            } else {
                textField.addEventListener('keypress', function(event) {
                    if (event.keyCode == 13) {
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
    }

    service.createMenuNumberField = function(id, initialValue, minimum, maximum, step, callback, immediate) {
        var numberField = document.createElement('input');
        numberField.setAttribute('type', 'number');
        setFieldDefaults(numberField, id, '75px');
        if (typeof minimum == 'number') {
            numberField.setAttribute('min', minimum);
        }
        if (typeof maximum == 'number') {
            numberField.setAttribute('max', maximum);
        }
        if (typeof step == 'number') {
            numberField.setAttribute('step', step);
        }
        if (callback) {
            if (immediate) {
                if (typeof numberField.oninput !== 'undefined') {
                    numberField.addEventListener('input', callback);
                } else {
                    numberField.addEventListener('change', callback);
                }
            } else {
                numberField.addEventListener('keypress', function(event) {
                    if (event.keyCode == 13) {
                        callback.apply(this);
                    }
                });
                numberField.addEventListener('blur', callback);
            }
        }
        if (typeof initialValue == 'number') {
            numberField.setAttribute('value', initialValue);
        }
        return numberField;
    }

    service.createMenuCheckbox = function(id, initialState, callback, value) {
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
    }

    service.createMenuSelect = function(id, initialValue, options, callback) {
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
    }

    service.createMenuRadioButton = function(id, name, value, callback) {
        var radio = document.createElement('input');
        radio.setAttribute('type', 'radio');
        radio.setAttribute('name', name);
        radio.setAttribute('id', id);
        radio.setAttribute('value', value);
        if (callback) {
            radio.addEventListener('change', callback);
        }
        return radio;
    }

    service.createInlineMenuRadioButtons = function(id, name, initialValue, options, callback) {
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
    }

    setFieldDefaults = function(field, id, width) {
        field.setAttribute('id', id);
        field.setAttribute('name', id);
        field.style.fontSize = '12px';
        if (width) {
            field.style.width = width;
        }
        field.style.margin = '0';
        field.style.outlineOffset = '-3px';
        return field;
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
        return interactivityAvailable && screenfull.enabled && (runningInSeleniumBrowser || runningInWebportal) 
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

    service.isViewRequestsSupported = function() {
        return Modernizr.promises && Modernizr.es6object;
    }

    /**
     * Escape special characters in a string which results in a valid DOM element id
     * @param {*} id
     */
    service.cssEscapeId = function(id) {
        var string = id.toString();
        var matchesBeginning = /^[a-z]+/i; 
        if (!matchesBeginning.test(string)) {
            //if ID doesn't start with letter add prefix
            string = 'knid_' + string;
        }
        // replace all special characters by underscores
        return string.replace(/^[^a-z]+|[^\w:-]+/gi, '___');
    }

    /**
     * Inline global style declarations for SVG export
     * @param {*} svg
     */
    service.inlineSvgStyles = function(svg) {
        var before = svg.firstChild;
        var styles = document.styleSheets;
        var newStyles = [];
        for (var i = 0; i < styles.length; i++) {  
            if (!styles[i].cssRules && styles[i].rules) {
                styles[i].cssRules = styles[i].rules;
            }
            // empty style declaration
            if (!styles[i].cssRules) continue;

            var cssText = [];			
            for (var j = 0; j < styles[i].cssRules.length; j++) {
                try {
                    var rule = styles[i].cssRules[j];
                    if (svg.querySelector(rule.selectorText)) {
                        // Split rgba into rgb and oppacity, as Batik is not able to handle rgba
                        if(rule.cssText.includes("rgba")) {
                            var result, rgbaString = [];
                            var stringsToReplace = [];
                            var tempRule = rule.cssText;
                            // RegExp to find rgba values
                            var findRGBAValues = /(.*?)rgba\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*,\s*([0-9]+\.[0-9]+|\d\s*)\)/g;

                            // Find all rgba values and the according css string to replace it later on
                            while(result = findRGBAValues.exec(rule.cssText)) {
                                stringsToReplace.push(result[0].match(/\w+(?=:\s*rgba):\s*rgba(.*?)\)/g));
                                rgbaString.push([[result[0].match(/\w+(?=:\s*rgba)/g)],
                                    [result[result.length-4]],
                                    [result[result.length-3]],
                                    [result[result.length-2]],
                                    [result[result.length-1]]]);
                            }
                            // Loop over all found rgba values and replace them
                            for(var i=0; i<rgbaString.length;i++) {
                                var rgbString = rgbaString[i][0] + ": rgb(" + 
                                rgbaString[i][1] + "," + 
                                rgbaString[i][2] + "," + 
                                rgbaString[i][3] + ");";
                                var opacityString = rgbaString[i][0] + "-opacity: " + rgbaString[i][4] +"; ";
                                var tempRule = tempRule.replace(stringsToReplace[i], rgbString + opacityString);	
                            }
                            cssText.push(tempRule);
                        } else if (rule.cssText.includes("transparent")) {
                            cssText.push(rule.cssText.replace(new RegExp("transparent", "g"), "none"));
                        } else {
                            // use only those styles which are really needed and do not contain any rgba values
                            cssText.push(rule.cssText);
                        }
                    }
                } catch(exception) {
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
        for (var i = 0; i < newStyles.length; i++) {
            svg.insertBefore(newStyles[i], before);
        }
    }

    /**
     * Function to measure and truncate the sizes of the given data array within an SVG.
     * In the config object a container element must be specified. Additionally there are several optional 
     * parameters which are explained in the following:
     * 
     * container: Mandatory container object, which needs to be inside the SVG element or the SVG element itself
     * classes: CSS-classes of the created text elements (optional)
     * attributes: Attributes of the created text elements (optional)
     * tempContainer: A temporary container in which the text elements are created in. If undefined a "g" 
     * 		element is created. If an empty string is provided, it will append the text directly to the provided 
     * 		top-level container.
     * tempContainerClasses: CSS-classes of the created temporary container element (optional)
     * tempContainerAttributes: CSS-attributes of the created temporary container element (optional)
     * maxWidth: The maximum width each text element can occupy. If the maxWidth is exceeded, an ellipsis is applied to  
     * 		fit the appropriate space. (optional)
     * maxHeight: The maximum height each text element can occupy. If the maxWidth is exceeded, an ellipsis is applied   
     * 		to fit the appropriate space. (optional)
     * minimalChars: The minimal amount of chars which should still be displayed even when a text element with ellipsis 
     * 		is larger than the maximum dimensions specified.
     * 
     * returns an object with the following fields:
     * values: 
     * 		originalData: The original strings provided
     * 		truncated: The (potentially) truncated strings of the original data
     * 		widths: Measured width of each text element
     * 		heights: Measured height of each text element
     * max:
     * 		maxWidth: maximum measured width of all text elements
     * 		maxHeight: maximum measured height of all text elements
     */
    service.measureAndTruncate = function(data, config) {
        var tempTextList = [], values = [], containerClass;
        var minChars = config.minimalChars ? config.minimalChars : 1
                if (typeof config.tempContainer  === "undefined" || config.tempContainer === null) {
                    containerClass = "g";
                } else {
                    containerClass = config.tempContainer;
                }
        var group;
        var maxHeight = 0, maxWidth = 0;
        if (containerClass !== "") {
            group = document.createElementNS(SVGNS, containerClass);
            if (config.tempContainerClasses) {
                group.setAttribute("class", config.tempContainerClasses);
            }
            if (config.tempContainerAttributes) {
                for (key in config.tempContainerAttributes) {
                    group.setAttribute(key, config.tempContainerAttributes[key]);
                }
            }
            config.container.appendChild(group);
        }

        data.forEach(function(value) {
            var tempText = document.createElementNS(SVGNS, "text");
            tempText.textContent = value;
            if (config.classes) {
                tempText.setAttribute("class", config.classes);
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
            values.push({originalData: value, truncated: truncated, width: tempWidth, height: tempHeight})
        });

        // Delete all texts which where appended to a temporal container
        if (group) {
            group.parentNode.removeChild(group);
        } else {
            // Delete all texts which where directly appended to the container
            tempTextList.forEach(function(text) {
                text.parentNode.removeChild(text);
            });
        }

        return {values: values, max: {maxWidth: maxWidth, maxHeight: maxHeight}};
    }

    /**
     * Function to wrap text after it exceeds a calculated size. The size
     * depends on the provided maxWidth and maxHeight. Returns the calculated
     * wrapped string.
     * 
     * textElement: The text element which should be wrapped
     * maxWidth: The maximum width the text can have before it gets wrapped
     * maxHeight: The maximum height the text can have before it gets wrapped
     * minimalChars: Minimum amount of characters the text should have 
     */
    wrapLabels = function(textElement, maxWidth, maxHeight, minimalChars) {
        var ellipsis = '\u2026';
        var textRect = textElement.getBoundingClientRect();
        var text = textElement.textContent;
        if (textRect.width > maxWidth || textRect.height > maxHeight) {
            var guessFactor = 1;
            text += ellipsis;
            while ((textRect.width > maxWidth || textRect.height > maxHeight) && text.length > minimalChars) {
                var heightDiff = 0, widthDiff = 0;
                if (textRect.width > maxWidth) {
                    widthDiff = textRect.width - maxWidth;
                }
                if (textRect.height > maxHeight) {
                    heightDiff = textRect.height - maxHeight;
                }
                if (widthDiff > heightDiff) {
                    guessFactor =  (widthDiff / textRect.width);
                } else {
                    guessFactor = (heightDiff / textRect.height);
                }

                text = text.slice(0, Math.min(Math.floor(-text.length * guessFactor), -1));
                if(text.length >= minimalChars){
                    textElement.textContent = text + ellipsis;
                } else {
                    text = textElement.textContent.substring(0,minimalChars);
                    textElement.textContent = text + ellipsis;
                }
                textRect = textElement.getBoundingClientRect();
            }
        }
        text = textElement.textContent;
        return {text: text, rect: textRect};
    }

    service.log = function(message) {
        if (console && console.log) {
            console.log(message);
        }
    };
    service.logError = function(err) {
        if (console && console.error) {
            console.error(err);
        }
    }

    document.addEventListener('DOMContentLoaded', init, false);

    return service;
}();