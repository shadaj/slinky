enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-example"

npmDependencies in Compile += "react" -> "15.6.1"

npmDependencies in Compile += "react-dom" -> "15.6.1"

webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack-opt.config.js")
