# AP Wrapper for KNIME® Analytics Platform

This folder contains the frontend wrapper to display views inside KNIME Analytics Platform.

## Development

### Prerequisites

* Install [Node.js][node], see version in [.nvmrc](.nvmrc).
* Only for test coverage uploads to SonarQube: you also need [Java]™ 8 or 11.

Newer versions may also work, but have not been tested.

Pull the contained [git submodules](https://stackoverflow.com/a/4438292/5134084) with
```sh
git submodule update --init
```

### Install dependencies

```sh
npm install
```

and then use the following commands. For detailed explanations see [Vue CLI docs]:


### Launch development server
Compiles all JavaScript sources, assets, … and starts a local web server in development mode.
Includes hot-reloading, so code changes will be visible in the browser immediately.

```sh
npm run dev
```

## Build production version

The whole wrapper is bundled into one single file: [dist/index.html](dist/index.html). For now it needs to be committed.

```sh
npm run build
```

[node]: https://knime-com.atlassian.net/wiki/spaces/SPECS/pages/905281540/Node.js+Installation
[java]: https://www.oracle.com/technetwork/java/javase/downloads/index.html
