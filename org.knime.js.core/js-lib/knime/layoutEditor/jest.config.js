module.exports = {
    moduleNameMapper: {
        '^~/(.*?)$': '<rootDir>/$1',
        '^vue$': 'vue/dist/vue.common.js'
    },
    moduleFileExtensions: ['js', 'vue', 'json'],
    transform: {
        '\\.js$': 'babel-jest',
        '\\.vue$': 'vue-jest'
    },
    testMatch: [
        '<rootDir>/test/unit/**/*.test.js'
    ],
    watchPathIgnorePatterns: ['<rootDir>/test/(e2e|performance)/.*', '<rootDir>/webapps-common/.*'], // RegExps(!)
    reporters: ['default', ['jest-junit', { outputDirectory: './coverage' }]],
    coverageReporters: ['lcov', 'text'],
    // keep in sync with sonar-project.properties!
    collectCoverageFrom: [
        '<rootDir>/**/*.{js,vue}',
        '!**/config.js',
        '!**/*.config.js',
        '!**/.eslintrc*.js',
        '!<rootDir>/{.tmp,.nuxt,coverage,dist,test,node_modules,service-mock,oidc-mock,webapps-common,buildtools}/**'
    ],
    testURL: 'http://test.example/',
    globals: {
        'vue-jest': {
            hideStyleWarn: true
        }
    }
};
