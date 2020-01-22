enablePlugins(ScalaJSPlugin)

name := "slinky-web"

scalacOptions -= "-Xfatal-warnings"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8"
