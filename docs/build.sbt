enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-docs"

Compile / npmDependencies += "react" -> "16.12.0"
Compile / npmDependencies += "react-dom" -> "16.12.0"
Compile / npmDependencies += "react-proxy" -> "1.1.8"

Compile / npmDependencies += "react-router-dom" -> "5.0.0"
Compile / npmDependencies += "react-syntax-highlighter" -> "6.0.4"
Compile / npmDependencies += "remark" -> "8.0.0"
Compile / npmDependencies += "remark-react" -> "4.0.1"
Compile / npmDependencies += "react-helmet" -> "5.2.0"
Compile / npmDependencies += "react-ga" -> "2.5.3"
Compile / npmDependencies += "history" -> "4.7.2"

Compile / npmDevDependencies += "url-loader" -> "0.6.2"
Compile / npmDevDependencies += "css-loader" -> "0.28.7"
Compile / npmDevDependencies += "html-webpack-plugin" -> "3.2.0"
Compile / npmDevDependencies += "copy-webpack-plugin" -> "4.5.1"
Compile / npmDevDependencies += "static-site-generator-webpack-plugin" -> "3.4.1"

webpack / version := "4.5.0"
startWebpackDevServer / version := "3.3.0"

fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack-fastopt.config.js")
fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack-opt.config.js")

fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot")
fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly()

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}
