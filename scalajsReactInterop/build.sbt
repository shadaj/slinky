enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-scalajsreact-interop"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.2.0"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8-RC2" % Test

npmDependencies in Test += "react" -> "16.8.1"
npmDependencies in Test += "react-dom" -> "16.8.1"
jsDependencies += RuntimeDOM % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
