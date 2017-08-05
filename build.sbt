enablePlugins(ScalaJSPlugin)

organization in ThisBuild := "me.shadaj"

version in ThisBuild := "0.1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.3"

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
  sourceGenerators in Compile += Def.taskDyn[Seq[File]] {
    val folderHtml = (sourceManaged in Compile).value / "me/shadaj/slinky/web/html"
    folderHtml.mkdirs()

    val html = (run in Compile in generator).toTask(Seq("me.shadaj.slinky.generator.MDN", (folderHtml / "gen.scala").getAbsolutePath, "me.shadaj.slinky.web.html").mkString(" ", " ", "")).map { _ =>
      Seq(
        folderHtml / "gen.scala"
      )
    }

    val folderSVG = (sourceManaged in Compile).value / "me/shadaj/slinky/web/svg"
    folderSVG.mkdirs()

    val svg = (run in Compile in generator).toTask(Seq("me.shadaj.slinky.generator.SVG", (folderSVG / "gen.scala").getAbsolutePath, "me.shadaj.slinky.web.svg").mkString(" ", " ", "")).map { _ =>
      Seq(
        folderSVG / "gen.scala"
      )
    }

    html.zip(svg).flatMap(t => t._1.flatMap(h => t._2.map(s => h ++ s)))
  }.taskValue,
  mappings in (Compile, packageSrc) ++= {
    val base  = (sourceManaged  in Compile).value
    val files = (managedSources in Compile).value
    files.map { f => (f, f.relativeTo(base).get.getPath) }
  }
).dependsOn(core)

lazy val hot = project.dependsOn(core)

lazy val scalajsReactInterop = project.dependsOn(core)

lazy val tests = project.dependsOn(core, web, hot, scalajsReactInterop)

lazy val example = project.dependsOn(web, hot, scalajsReactInterop)
