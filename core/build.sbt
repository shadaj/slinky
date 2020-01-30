enablePlugins(ScalaJSPlugin)

name := "slinky-core"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}

// Needed by useCallback due to false positive warning on implicit evidence
scalacOptions -= "-Ywarn-unused:implicits"
scalacOptions -= "-Wunused:implicits"
