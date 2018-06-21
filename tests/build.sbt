enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.6-SNAP1" % Test

npmDependencies in Test += "react" -> "16.4.0"
npmDependencies in Test += "react-dom" -> "16.4.0"
jsDependencies += RuntimeDOM % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
scalacOptions += "-Ywarn-unused-import"
