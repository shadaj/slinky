enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-scalajsreact-interop"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.2.0"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.6-SNAP4" % Test

npmDependencies in Test += "react" -> "16.4.0"
npmDependencies in Test += "react-dom" -> "16.4.0"
jsDependencies += RuntimeDOM % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
