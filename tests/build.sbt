enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.3" % Test

npmDependencies in Test += "react" -> "16.2.0"
npmDependencies in Test += "react-dom" -> "16.2.0"
jsDependencies += RuntimeDOM % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
scalacOptions += "-Ywarn-unused-import"
