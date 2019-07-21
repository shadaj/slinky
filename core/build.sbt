enablePlugins(ScalaJSPlugin)

name := "slinky-core"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

scalacOptions += "-P:scalajs:sjsDefinedByDefault"