enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-docs"

Compile / npmDependencies += "react" -> "16.13.1"
Compile / npmDependencies += "react-dom" -> "16.13.1"
Compile / npmDependencies += "react-proxy" -> "1.1.8"

Compile / npmDependencies += "react-router" -> "5.2.0"
Compile / npmDependencies += "react-router-dom" -> "5.2.0"
Compile / npmDependencies += "react-syntax-highlighter" -> "9.0.1"
Compile / npmDependencies += "remark" -> "12.0.1"
Compile / npmDependencies += "remark-react" -> "7.0.1"
Compile / npmDependencies += "react-helmet" -> "6.1.0"
Compile / npmDependencies += "history" -> "4.10.1"
Compile / npmDependencies += "react-ga" -> "3.1.2"

Compile / npmDevDependencies += "url-loader" -> "4.1.0"
Compile / npmDevDependencies += "css-loader" -> "0.28.11"
Compile / npmDevDependencies += "html-webpack-plugin" -> "4.3.0"
Compile / npmDevDependencies += "copy-webpack-plugin" -> "6.0.3"
Compile / npmDevDependencies += "static-site-generator-webpack-plugin" -> "3.4.2"


webpack / version := "4.44.1"
startWebpackDevServer / version := "3.11.0"

fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack-fastopt.config.js")
fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack-opt.config.js")

fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot")
fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly()

addCommandAlias("dev", ";fastOptJS::startWebpackDevServer;~fastOptJS")
