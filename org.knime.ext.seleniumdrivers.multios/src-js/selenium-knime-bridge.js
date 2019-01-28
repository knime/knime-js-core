/* eslint-env jquery, es6 */
/* eslint no-var: "error" */
window.seleniumKnimeBridge = (function () {

    const FRAME_ID = 'knimeViewFrame';

    const SIGNAL_NO_ACTION = 'NO_ACTION';
    const SIGNAL_VIEW_REQUEST = 'VIEW_REQUEST';
    const UPDATE_REQUEST_STATUS = 'UPDATE_REQUEST_STATUS';
    const CANCEL_REQUEST = 'CANCEL_REQUEST';
    const SIGNAL_CLOSE_BUTTON_PRESSED = 'CLOSE_BUTTON_PRESSED';
    const SIGNAL_CLOSE_DISCARD_BUTTON_PRESSED = 'CLOSE_DISCARD_BUTTON_PRESSED';
    const SIGNAL_CLOSE_APPLY_BUTTON_PRESSED = 'CLOSE_APPLY_BUTTON_PRESSED';
    const SIGNAL_CLOSE_APPLY_DEFAULT_BUTTON_PRESSED = 'CLOSE_APPLY_DEFAULT_BUTTON_PRESSED';
    const SIGNAL_RESET_BUTTON_PRESSED = 'RESET_BUTTON_PRESSED';
    const SIGNAL_APPLY_BUTTON_PRESSED = 'APPLY_BUTTON_PRESSED';
    const SIGNAL_APPLY_DEFAULT_BUTTON_PRESSED = 'APPLY_DEFAULT_BUTTON_PRESSED';
    const SIGNAL_CLOSE_WINDOW = 'CLOSE_WINDOW';

    // const BUTTON_BAR_HEIGHT = 46;

    let signalQueue = [];
    let signalFunction, signalTimeout;

    let bridge = {};
    bridge.version = '1.0.0';
    
    let loadJSONFile = function (url, callback, parse) {
        let httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState === 4) {
                // local requests leave status at 0
                if (httpRequest.status === 200 || httpRequest.status === 0) {
                    let data = httpRequest.responseText;
                    if (parse) {
                        data = JSON.parse(data);
                    }
                    if (callback) {
                        callback(data);
                    }
                }
            }
        };
        httpRequest.open('GET', url);
        httpRequest.send();
    };
    
    let emptyModal = function () {
        let dialog = $('#seleniumKnimeBridge_modal');
        dialog.find('.modal-title').empty();
        dialog.find('.modal-description').empty();
        dialog.find('.modal-options').empty();
    };
    
    let signalNext = function () {
        if (signalFunction && signalQueue.length > 0) {
            if (signalTimeout) {
                window.clearTimeout(signalTimeout);
            }
            let toSignal = signalQueue.splice(0, 1);
            signalFunction(toSignal[0]);
            signalFunction = null;
        }
    };
    
    let signal = function (value) {
        // enqueue and call signal, in most cases will be called immediately
        signalQueue.push(value);
        signalNext();
    };
    
    let showButtons = function (show) {
        document.getElementById('seleniumKnimeBridge_buttonBar').style.display = show ? 'block' : 'none';
    };

    let handleButtonClick = function (event, toSignal) {
        event.preventDefault();
        signal(toSignal);
    };
    
    let initFrame = function (viewURL, repURL, valURL, title, initCall) { // eslint-disable-line max-params
        let frame = document.getElementById(FRAME_ID);
        frame.addEventListener('load', function () {
            let frameWindow = document.getElementById(FRAME_ID).contentWindow;

            // registering view request functions to forward these to the bridge
            frameWindow.eval('function knimeViewRequest(request)' +
                '{parent.seleniumKnimeBridge.knimeViewRequest(request);}');
            frameWindow.eval('function knimeUpdateRequestStatus(id)' +
                '{parent.seleniumKnimeBridge.knimeUpdateRequestStatus(id);}');
            frameWindow.eval('function knimeCancelRequest(id)' +
                '{parent.seleniumKnimeBridge.knimeCancelRequest(id);}');

            // defaulting push-enabled to true (no need to loop back through Java)
            frameWindow.eval('function knimePushSupported() {return true;}');

            // loading representation and value
            loadJSONFile(repURL, function (rep) {
                frameWindow.parsedRepresentation = rep;
                if (frameWindow.parsedValue) {
                    frameWindow.eval(initCall);
                }
            }, true);
            loadJSONFile(valURL, function (val) {
                frameWindow.parsedValue = val;
                if (frameWindow.parsedRepresentation) {
                    frameWindow.eval(initCall);
                }
            }, true);
        }, true);
        window.document.title = title;
        frame.setAttribute('src', viewURL);
    };

    let initButtons = function () {
        document.getElementById('knimeSeleniumBridge_resetButton').onclick = function (e) {
            handleButtonClick(e, SIGNAL_RESET_BUTTON_PRESSED);
        };
        document.getElementById('knimeSeleniumBridge_applyButton').onclick = function (e) {
            handleButtonClick(e, SIGNAL_APPLY_BUTTON_PRESSED);
        };
        document.getElementById('knimeSeleniumBridge_applyDefaultButton').onclick = function (e) {
            handleButtonClick(e, SIGNAL_APPLY_DEFAULT_BUTTON_PRESSED);
        };
        document.getElementById('knimeSeleniumBridge_closeButton').onclick = function (e) {
            handleButtonClick(e, SIGNAL_CLOSE_BUTTON_PRESSED);
        };
        document.getElementById('knimeSeleniumBridge_closeDiscardButton').onclick = function (e) {
            handleButtonClick(e, SIGNAL_CLOSE_DISCARD_BUTTON_PRESSED);
        };
        document.getElementById('knimeSeleniumBridge_closeApplyButton').onclick = function (e) {
            handleButtonClick(e, SIGNAL_CLOSE_APPLY_BUTTON_PRESSED);
        };
        document.getElementById('knimeSeleniumBridge_closeApplyDefaultButton').onclick = function (e) {
            handleButtonClick(e, SIGNAL_CLOSE_APPLY_DEFAULT_BUTTON_PRESSED);
        };
        $('#seleniumKnimeBridge_modal').on('hidden.bs.modal', function (e) {
            emptyModal();
        });
    };

    bridge.initView = function (viewURL, repURL, valURL, title, initCall) { // eslint-disable-line max-params
        showButtons(Boolean(repURL));
        initFrame(viewURL, repURL, valURL, title, initCall);
        initButtons();
    };

    bridge.registerCometRequest = function (args) {
        signalFunction = args[args.length - 1];
        signalTimeout = window.setTimeout(function () {
            if (signalFunction) {
                signalFunction(SIGNAL_NO_ACTION);
                signalFunction = null;
            }
        }, args[0]);
        signalNext();
    };

    bridge.executeOnFrame = function (toExecute) {
        let frame = document.getElementById(FRAME_ID);
        // escape possible inner string escapes
        toExecute = toExecute.replace(/\\/g, '\\\\');
        return frame.contentWindow.eval('(function() {' + toExecute + '})();');
    };

    bridge.showModal = function (title, description, choicesString) {
        let choices = JSON.parse(choicesString);
        let dialog = $('#seleniumKnimeBridge_modal');
        dialog.find('.modal-title').append(title);
        dialog.find('.modal-description').append(description);
        let optionDiv = dialog.find('.modal-options');
        for (let i = 0; i < choices.length; i++) {
            let radio = $('<div class="radio"><label><input type="radio" name="modalradio" data-signal="' +
                choices[i].signal + '" style="margin-right: 5px;" ' + (i === 0 ? 'checked' : '') + '>' +
                choices[i].label + '</label></div>');
            optionDiv.append(radio);
            optionDiv.append('<div class="modal-option-description">' + choices[i].description + '</div>');
        }
        dialog.modal('show');
    };

    bridge.modalOKPressed = function (e) {
        e.preventDefault();
        let option = $('#seleniumKnimeBridge_modal .modal-options input:radio:checked');
        let toSignal = option.attr('data-signal');
        signal(toSignal);
        $('#seleniumKnimeBridge_modal').modal('hide');
        emptyModal();
    };

    bridge.modalCancelPressed = function (e) {
        e.preventDefault();
        $('#seleniumKnimeBridge_modal').modal('hide');
        emptyModal();
    };

    bridge.knimeViewRequest = function (request) {
        signal(SIGNAL_VIEW_REQUEST + '-' + request);
    };

    bridge.knimeUpdateRequestStatus = function (id) {
        signal(UPDATE_REQUEST_STATUS + '-' + id);
    };

    bridge.knimeCancelRequest = function (id) {
        signal(CANCEL_REQUEST + '-' + id);
    };

    bridge.respondToViewRequest = function (responseURL) {
        loadJSONFile(responseURL, function (response) {
            bridge.executeOnFrame('KnimeInteractivity.respondToViewRequest(JSON.parse(\'' + response + '\'));');
        }, false);
    };

    bridge.updateResponseMonitor = function (monitorURL) {
        loadJSONFile(monitorURL, function (monitor) {
            bridge.executeOnFrame('KnimeInteractivity.updateResponseMonitor(JSON.parse(\'' + monitor + '\'));');
        }, false);
    };

    bridge.clearView = function () {
        signalFunction = null;
        if (signalTimeout) {
            window.clearTimeout(signalTimeout);
        }
        window.location.reload();
        showButtons(false);
    };

    window.addEventListener('beforeunload', function (event) {
        if (window.signal) {
            /* This doesn't work properly, Selenium seems to close the connection before and the script returns
             * null, which is also fine. We close windows without a window handle in Java. */
            window.signal(SIGNAL_CLOSE_WINDOW);
        }
    });

    return bridge;
})();
