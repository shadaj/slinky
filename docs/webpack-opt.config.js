const webpack = require("webpack");
const path = require("path");

const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const StaticSiteGeneratorPlugin = require('static-site-generator-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

const jsdom = require('jsdom');
const { JSDOM } = jsdom;
const dom = new JSDOM();

module.exports = {
  mode: "production",
  entry: {
    "docs-opt": [ path.resolve(__dirname, "./opt-launcher.js") ]
  },
  output: {
    "path": path.resolve(__dirname, "../../../../build"),
    publicPath: '/',
    "filename": "[name]-bundle.js",
    libraryTarget: 'umd'
  },
  resolve: {
    alias: {
      "resources": path.resolve(__dirname, "../../../../src/main/resources")
    }
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [ 'style-loader', 'css-loader' ]
      },
      // url loader for svg
      {
        test: /\.svg$/,
        use: [ 'url-loader' ]
      }
    ]
  },
  plugins: [
    new CopyWebpackPlugin([
      {
        from: path.resolve(__dirname, "../../../../public"),
        ignore: ["404.html", "404-fastopt.html"]
      }
    ]),
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, "../../../../public/404.html"),
      filename: "404.html"
    }),
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify('production')
      }
    }),
    new StaticSiteGeneratorPlugin({
      crawl: true,
      globals: {
        window: dom.window,
        document: dom.window.document,
        navigator: dom.window.navigator,
        ssr: true,
        fs: require('fs'),
        __dirname: __dirname
      }
    })
  ],
  node: {
    console: true,
    fs: 'empty',
    net: 'empty',
    tls: 'empty'
  }
};
