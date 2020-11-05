# KNIME Metanode Layout Editor

## Prerequisites

Install [Node.js version 14](https://nodejs.org/en/download/current/).

## Install all dependencies
```
npm install
```

### Launch development server including hot-reload
```
npm run dev
```

### Code linting
```
npm run lint
```

### Running tests
Unit tests are run with:

```
npm run test:unit
```

You can generate a coverage report with

```
npm run coverage
```

The output can be found in the `coverage` folder. It contains a browsable html report as well as raw coverage data in
[LCOV](https://github.com/linux-test-project/lcov) format, which can be used in analysis software.


### Compile and minify for production
Always run build and commit the build (files in `/dist`) after changes because KNIME AP is going to load the built files.

```
npm run build
```

### Debugging inside KNIME AP
Since there are no native development tools available inside the SWT browser, we use Firebug Lite for basic debugging. To enable it, start KNIME AP in debug mode from Eclipse or activate the setting 'Create debug HTML for JavaScript views' in a production build. Now press the Firebug Lite button in the layout editor.