enablePlugins(ScalaJSBundlerPlugin)

name := "docs"

libraryDependencies += "com.lihaoyi" %%% "sourcecode" % "0.1.4"

npmDependencies in Compile += "react" -> "16.1.0"
npmDependencies in Compile += "react-dom" -> "16.1.0"
npmDependencies in Compile += "react-proxy" -> "1.1.8"

npmDependencies in Compile += "react-router-dom" -> "4.2.2"
npmDependencies in Compile += "react-syntax-highlighter" -> "6.0.4"
npmDependencies in Compile += "remark" -> "8.0.0"
npmDependencies in Compile += "remark-react" -> "4.0.1"

npmDevDependencies in Compile += "file-loader" -> "1.1.5"
npmDevDependencies in Compile += "style-loader" -> "0.19.0"
npmDevDependencies in Compile += "css-loader" -> "0.28.7"
npmDevDependencies in Compile += "html-webpack-plugin" -> "2.30.1"
npmDevDependencies in Compile += "copy-webpack-plugin" -> "4.2.0"

webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack-fastopt.config.js")
webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack-opt.config.js")

webpackDevServerExtraArgs in fastOptJS := Seq("--inline", "--hot")

webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly()
