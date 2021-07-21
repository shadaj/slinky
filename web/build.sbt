enablePlugins(ScalaJSPlugin)

name := "slinky-web"

libraryDependencies += ("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13)

scalacOptions -= "-Xfatal-warnings"
