enablePlugins(ScalaJSPlugin)

organization in ThisBuild := "me.shadaj"

version in ThisBuild := "0.1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.2"

scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")

lazy val slinky = project.in(file(".")).aggregate(
  core,
  web,
  hot,
  scalajsReactInterop
).settings(
  publish := {},
  publishLocal := {}
)

lazy val generator = project

lazy val core = project

lazy val web = project.settings(
  sourceGenerators in Compile += Def.taskDyn {
    val folder = (sourceManaged in Compile).value / "me/shadaj/slinky/web/html/internal"
    folder.mkdirs()

    (run in Compile in generator).toTask(Seq((folder / "gen.scala").getAbsolutePath, "me.shadaj.slinky.web.html").mkString(" ", " ", "")).map { _ =>
      Seq(
        folder / "gen.scala"
      )
    }
  }.taskValue,
  mappings in (Compile, packageSrc) ++= {
    val base  = (sourceManaged  in Compile).value
    val files = (managedSources in Compile).value
    files.map { f => (f, f.relativeTo(base).get.getPath) }
  }
).dependsOn(core)

lazy val hot = project.dependsOn(core)

lazy val scalajsReactInterop = project.dependsOn(core)

lazy val example = project.dependsOn(web, hot, scalajsReactInterop)
