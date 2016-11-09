enablePlugins(ScalaJSPlugin)

organization := "me.shadaj"
name := "simple-react"

version := "0.1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.1"

lazy val generator = project

lazy val core = project.settings(
  sourceGenerators in Compile += Def.task {
    val dir = baseDirectory.value
    val fileToWrite = dir / "src" / "gen" / "scala" / "me/shadaj/simple/react/core/html" / "gen.scala"

    (run in Compile in generator).toTask("").value

    Seq(fileToWrite)
  }.taskValue
)

lazy val example = project.dependsOn(core)
