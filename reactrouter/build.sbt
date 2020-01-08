enablePlugins(ScalaJSPlugin)

name := "slinky-react-router"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
