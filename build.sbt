enablePlugins(ScalaJSPlugin)

organization in ThisBuild := "me.shadaj"

version in ThisBuild := "0.1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.2"

lazy val generator = project

lazy val core = project.settings(
  sourceGenerators in Compile += Def.task {
    val folder = (sourceManaged in Compile).value / "me/shadaj/slinky/core/html"

    (run in Compile in generator).toTask("").value

    Seq(
      folder / "tagsApplied.scala",
      folder / "tags.scala",
      folder / "attrs.scala"
    )
  }.taskValue,
  mappings in (Compile, packageSrc) ++= {
    val base  = (sourceManaged  in Compile).value
    val files = (managedSources in Compile).value
    files.map { f => (f, f.relativeTo(base).get.getPath) }
  }
)

lazy val scalajsReactInterop = project.dependsOn(core)

lazy val example = project.dependsOn(core, scalajsReactInterop)