enablePlugins(ScalaJSPlugin)

name := "slinky-web"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.3.0"

scalacOptions -= "-Xfatal-warnings"
