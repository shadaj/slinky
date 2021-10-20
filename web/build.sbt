enablePlugins(ScalaJSPlugin)

name := "slinky-web"

libraryDependencies += {
  if (scalaJSVersion.startsWith("0.6."))
    "org.scala-js" %%% "scalajs-dom" % "1.0.0"
  else
    "org.scala-js" %%% "scalajs-dom" % "2.0.0-RC1"
}

scalacOptions -= "-Xfatal-warnings"
