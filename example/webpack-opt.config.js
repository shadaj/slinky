var webpack = require('webpack');

module.exports = {
  "entry": {
    "simple-react-example-opt": "./opt-launcher.js"
  },
  "output": {
    "path": ".",
    "filename": "[name]-bundle.js"
  },
  "module": {
    "preLoaders": [{
      "test": new RegExp("\\.js$"),
      "loader": "source-map-loader"
    }]
  },
  plugins: [
      new webpack.DefinePlugin({
        'process.env': {
          NODE_ENV: JSON.stringify('production')
        }
      }),
      new webpack.optimize.UglifyJsPlugin()
  ]
}