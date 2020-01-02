enablePlugins(ScalaJSPlugin)

name := "slinky-core"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalacOptions -= "-Xfatal-warnings" // Needed by useCallback due to false positive warning on implicit evidence