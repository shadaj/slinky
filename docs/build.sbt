enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-docs"

npmDependencies in Compile += "react" -> "16.8.1"
npmDependencies in Compile += "react-dom" -> "16.8.1"
npmDependencies in Compile += "react-proxy" -> "1.1.8"

npmDependencies in Compile += "react-router-dom" -> "4.4.0"
npmDependencies in Compile += "react-syntax-highlighter" -> "6.0.4"
npmDependencies in Compile += "remark" -> "8.0.0"
npmDependencies in Compile += "remark-react" -> "4.0.1"
npmDependencies in Compile += "react-helmet" -> "5.2.0"
npmDependencies in Compile += "react-ga" -> "2.5.3"
npmDependencies in Compile += "history" -> "4.7.2"

npmDevDependencies in Compile += "url-loader" -> "0.6.2"
npmDevDependencies in Compile += "css-loader" -> "0.28.7"
npmDevDependencies in Compile += "html-webpack-plugin" -> "3.2.0"
npmDevDependencies in Compile += "copy-webpack-plugin" -> "4.5.1"
npmDevDependencies in Compile += "static-site-generator-webpack-plugin" -> "3.4.1"

version in webpack := "4.5.0"
version in startWebpackDevServer:= "3.1.3"

webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack-fastopt.config.js")
webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack-opt.config.js")

webpackDevServerExtraArgs in fastOptJS := Seq("--inline", "--hot")
webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly()

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
