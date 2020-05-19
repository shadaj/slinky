enablePlugins(ScalaJSPlugin)

name := "slinky-web"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0"

scalacOptions -= "-Xfatal-warnings"
