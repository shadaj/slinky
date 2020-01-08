enablePlugins(ScalaJSPlugin)

name := "slinky-history"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
