var webpack = require('webpack');

module.exports = {
  "entry": {
    "simple-react-example-opt": "/Users/shadaj/open-source/simple-react/example/target/scala-2.12/scalajs-bundler/main/opt-launcher.js"
  },
  "output": {
    "path": "/Users/shadaj/open-source/simple-react/example/target/scala-2.12/scalajs-bundler/main",
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