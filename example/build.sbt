enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-example"

npmDependencies in Compile += "react" -> "15.6.1"

npmDependencies in Compile += "react-dom" -> "15.6.1"

npmDependencies in Compile += "react-proxy" -> "1.1.8"

webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack-fastopt.config.js")
webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack-opt.config.js")

emitSourceMaps := false

webpackDevServerExtraArgs := Seq("--inline", "--hot")
