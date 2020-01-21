enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-scalajsreact-interop"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.6.0"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8" % Test

npmDependencies in Test += "react" -> "16.12.0"
npmDependencies in Test += "react-dom" -> "16.12.0"
jsDependencies += RuntimeDOM % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
