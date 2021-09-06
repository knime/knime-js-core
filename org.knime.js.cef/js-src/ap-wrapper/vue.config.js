const path = require('path');
const LimitChunkCountPlugin = require('webpack/lib/optimize/LimitChunkCountPlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const HtmlWebpackInlineSourcePlugin = require('html-webpack-inline-source-plugin');

module.exports = {
    css: {
        extract: false
    },
    chainWebpack: config => {
        // allow tilde imports
        config.resolve.alias.set('~', path.resolve(__dirname));

        config.resolve.alias.set('webapps-common', path.resolve(__dirname, 'webapps-common'));

        // allow easy debugging
        config.devtool('eval-source-map');

        // needed to create single output js resource
        config.optimization.delete('splitChunks');
        config.plugin('LimitChunkCountPlugin').use(LimitChunkCountPlugin, [{ maxChunks: 1 }]);

        // needed to build one single HTML file as output
        config.plugin('HtmlWebpackPlugin').use(new HtmlWebpackPlugin({
            filename: 'index.html',
            template: 'public/index.html',
            inlineSource: '.(js)$' // inline all matching resources
        }));
        config.plugin('HtmlWebpackInlineSourcePlugin').use(new HtmlWebpackInlineSourcePlugin(HtmlWebpackPlugin));
    }
};
