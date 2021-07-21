enablePlugins(ScalaJSPlugin)

name := "slinky-core"

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
    case _ => Seq.empty
  }
}

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}

// Needed by useCallback due to false positive warning on implicit evidence
scalacOptions -= "-Ywarn-unused:implicits"
scalacOptions -= "-Wunused:implicits"
