**This plugin enables node views (single and composite) and node dialogs to be displayed using the Chromium Embedded Framework (CEF) browser. The views are opened in a separate CEF window.**

## Debugging and Development

### Developer tools 

In order to open the developer tools for a node view (or dialog), the AP needs to be started with
```
-Dchromium.remote_debugging_port=8888
```
As a result, a debug-button (`</>`) is shown at the bottom right corner of every opened view (or dialog) in the AP which opens the developer tools when pressed.
Alternatively the URL `http://localhost:8888` can also directly be opened with a browser of your choice.

The type of browser which is being opened when pressing the debug-button (`</>`) can be controlled via
```
-Dorg.knime.ui.debug.button.browser=[cef|system]
```
If not specified, the developer tools will be opened in the system browser.

### Controlling the URL used in a single node view

The URL used within the node view window can be controlled via the 
```
-Dorg.knime.ui.debug.node.view.url=...
```
 system property. 
 The specified URL will be used for all nodes having a node view (contributed via the UI Extension framework) unless it is limited to node factories specified via 
```
-Dorg.knime.ui.debug.node.view.url.factory-class=...
```
which receives a regular expression to match a node factory or factories, e.g. `.*MyNodeFactory`.

Depending on the particular node, the provided URL either needs to serve a html page which is embedded in an iframe or a vue-component (i.e. a js-file) which is loaded at runtime ('internal node view').


