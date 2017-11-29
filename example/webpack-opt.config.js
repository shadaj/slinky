var webpack = require("webpack");
var path = require("path");

var HtmlWebpackPlugin = require('html-webpack-plugin');
var CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    "entry": {
        "slinky-example-opt": [ path.resolve(__dirname, "./opt-launcher.js") ]
    },
    "output": {
        "path": path.resolve(__dirname, "../../../../build"),
        "filename": "[name]-bundle.js"
    },
    resolve: {
        alias: {
            "resources": path.resolve(__dirname, "../../../../src/main/resources")
        }
    },
    module: {
        rules: [
            {
                test: /\\.css\$/,
                use: [ 'style-loader', 'css-loader' ]
            },
            // "file" loader for svg
            {
                test: /\\.svg\$/,
                use: [
                    {
                        loader: 'file-loader',
                        query: {
                            name: 'static/media/[name].[hash:8].[ext]'
                        }
                    }
                ]
            }
        ]
    },
    plugins: [
        new CopyWebpackPlugin([
            { from: path.resolve(__dirname, "../../../../public") }
        ]),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, "../../../../public/index.html")
        }),
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: JSON.stringify('production')
            }
        }),
        new webpack.optimize.UglifyJsPlugin()
    ]
}