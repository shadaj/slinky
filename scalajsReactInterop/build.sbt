enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-scalajsreact-interop"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.6.0"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8" % Test
Test / npmDependencies += "react" -> "16.12.0"
Test / npmDependencies += "react-dom" -> "16.12.0"

Test / requireJsDomEnv := true
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
scalacOptions += "-P:scalajs:sjsDefinedByDefault"
