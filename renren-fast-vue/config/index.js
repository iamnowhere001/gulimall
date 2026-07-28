'use strict'
const path = require('path')

module.exports = {
  dev: {
    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    // 代理列表, 是否开启代理通过[./dev.env.js]配置
    proxyTable: {
      '/api': {
        target: 'http://localhost:88',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    },

    host: 'localhost',
    port: 8001,
    autoOpenBrowser: true,
    errorOverlay: true,
    useEslint: false,
    showEslintErrorsInOverlay: false,
    devtool: 'eval-source-map',
    cssSourceMap: false
  },

  build: {
    index: path.resolve(__dirname, '../dist/index.html'),
    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: './',

    productionSourceMap: false,
    devtool: 'source-map',

    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],

    bundleAnalyzerReport: process.env.npm_config_report
  }
}
