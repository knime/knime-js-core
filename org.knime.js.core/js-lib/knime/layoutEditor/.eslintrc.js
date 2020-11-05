module.exports = {
    extends: ['./webapps-common/lint/.eslintrc-vue.js'],
    rules: {
        'no-global-assign': ['error', {
            // `require` is needed for the esm module
            exceptions: ['require']
        }]
    },
    overrides: [{
        files: ['{modules,plugins,server,config}/**', '*.config.js'],
        env: {
            node: true
        }
    }]
};
