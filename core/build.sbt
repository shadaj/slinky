enablePlugins(ScalaJSPlugin)

name := "slinky-core"

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) =>
      Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value
      )
    case _ => Seq.empty
  }
}

// Needed by useCallback due to false positive warning on implicit evidence
scalacOptions -= "-Ywarn-unused:implicits"
scalacOptions -= "-Wunused:implicits"
