const path = require('path');
const svgConfig = require('webapps-common/webpack/webpack.svg.config');
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
        config.resolve.alias.set('knime-ui-extension-service', path.resolve(__dirname, 'knime-ui-extension-service'));

        // apply SVG loader config
        config.module.rule('svg').uses.clear();
        config.merge({ module: { rule: { svg: svgConfig } } });

        // allow easy debugging (but increases file size a lot)
        // config.devtool('eval-source-map');

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