module.exports = {
    extends: ['./webapps-common/lint/.eslintrc-vue.js'],
    globals: {
        consola: true
    },
    rules: {
        'no-global-assign': ['error', {
            // `require` is needed for the esm module
            exceptions: ['require']
        }]
    },
    overrides: [{
        files: ['{service-mock,modules,plugins,server,config}/**', '*.config.js'],
        env: {
            node: true
        }
    }]
};
