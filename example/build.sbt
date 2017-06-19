enablePlugins(ScalaJSBundlerPlugin)

name := "simple-react-example"

skip in packageJSDependencies := false

npmDependencies in Compile += "react" -> "15.4.1"

npmDependencies in Compile += "react-dom" -> "15.4.1"
