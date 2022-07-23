enablePlugins(ScalaJSPlugin)

name := "slinky-web"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.0.0"

scalacOptions -= "-Xfatal-warnings"
