const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');

module.exports = {
    output: {
        // use absolute paths in sourcemaps (important for debugging via IDE)
        devtoolModuleFilenameTemplate: '[absolute-resource-path]',
        devtoolFallbackModuleFilenameTemplate: '[absolute-resource-path]?[hash]'
    },
    resolve: {
        extensions: ['.js', '.vue', '.json'],
        alias: {
            '~': path.resolve(__dirname, '..')
        }
    },
    module: {
        rules: [{
            test: /\.vue$/,
            loader: 'vue-loader',
            exclude: /node_modules/,
            options: {
                optimizeSSR: false
            }
        }, {
            test: /\.scss$/,
            use: [
                'vue-style-loader',
                'css-loader',
                'sass-loader'
            ]
        }, {
            test: /\.js$/,
            loader: 'babel-loader',
            exclude: /node_modules/
        }]
    },
    devServer: {
        historyApiFallback: true,
        noInfo: true
    },
    performance: {
        hints: false
    },
    externals: [require('webpack-node-externals')()],
    devtool: 'eval',
    target: 'node',
    plugins: [
        new VueLoaderPlugin()
    ],
    mode: 'development'
};
