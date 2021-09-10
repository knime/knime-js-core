**This plugin displays single node views based on the UI Extension framework and the Chromium Embedded Framework (CEF) browser. The views are opened in a separate cef window.**

## Debugging

The URL used within the node view window can be controlled via the `org.knime.ui.debug.node.view.url` system property. The specified URL will be used for all nodes having a node view (contributed via the UI Extension framework) unless it is limited to node factories specified via `org.knime.ui.debug.node.view.url.factory-class` which receives a regular expression to match a node factory or factories, e.g. `.*MyNodeFactory`.

Depending on the particular node, the provided URL either serves a html page which is embedded in an iframe or a vue-component (i.e. a js-file) which is loaded at runtime ('internal node view').

