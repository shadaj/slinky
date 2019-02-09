enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.6-SNAP4" % Test

npmDependencies in Test += "react" -> "16.8.1"
npmDependencies in Test += "react-dom" -> "16.8.1"
jsDependencies += RuntimeDOM % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
