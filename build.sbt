organization in ThisBuild := "me.shadaj"

scalaVersion in ThisBuild := "2.12.4"

scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")

lazy val slinky = project.in(file(".")).aggregate(
  readWrite,
  core,
  web,
  testRenderer,
  native,
  vr,
  hot,
  scalajsReactInterop
).settings(
  publishArtifact := false
)

lazy val librarySettings = Seq(
  scalacOptions ++= (if (isSnapshot.value && false) Seq.empty else Seq({
    val origVersion = version.value
    val githubVersion = if (origVersion.contains("-")) {
      origVersion.split('-').last
    } else {
      s"v$origVersion"
    }

    val a = baseDirectory.value.toURI
    val g = "https://raw.githubusercontent.com/shadaj/slinky"
    s"-P:scalajs:mapSourceURI:$a->$g/${githubVersion}/${baseDirectory.value.getName}/"
  }))
)

addCommandAlias(
  "publishSignedAll",
  (slinky: ProjectDefinition[ProjectReference])
    .aggregate
    .map(p => s"${p.asInstanceOf[LocalProject].project}/publishSigned")
    .mkString(";", ";", "")
)

lazy val macroAnnotationSettings = Seq(
  // New-style macro annotations are under active development.  As a result, in
  // this build we'll be referring to snapshot versions of both scala.meta and
  // macro paradise.
  resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.bintrayRepo("scalameta", "maven"),
  // A dependency on macro paradise 3.x is required to both write and expand
  // new-style macros.  This is similar to how it works for old-style macro
  // annotations and a dependency on macro paradise 2.x.
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  // temporary workaround for https://github.com/scalameta/paradise/issues/10
  scalacOptions in (Compile, console) := Seq() // macroparadise plugin doesn't work in repl yet.
)

lazy val generator = project

lazy val readWrite = project.settings(librarySettings)

lazy val core = project.settings(macroAnnotationSettings, librarySettings).dependsOn(readWrite)

lazy val web = project.settings(
  sourceGenerators in Compile += Def.taskDyn[Seq[File]] {
    val rootFolder = (sourceManaged in Compile).value / "slinky/web"
    rootFolder.mkdirs()

    val html = (runMain in Compile in generator).toTask(Seq("slinky.generator.Generator", "web/html.json", (rootFolder / "html").getAbsolutePath, "slinky.web.html").mkString(" ", " ", "")).map { _ =>
      (rootFolder / "html" ** "*.scala").get
    }

    val svg = (runMain in Compile in generator).toTask(Seq("slinky.generator.Generator", "web/svg.json", (rootFolder / "svg").getAbsolutePath, "slinky.web.svg").mkString(" ", " ", "")).map { _ =>
      (rootFolder / "svg" ** "*.scala").get
    }

    html.zip(svg).flatMap(t => t._1.flatMap(h => t._2.map(s => h ++ s)))
  }.taskValue,
  mappings in (Compile, packageSrc) ++= {
    val base  = (sourceManaged  in Compile).value
    val files = (managedSources in Compile).value
    files.map { f => (f, f.relativeTo(base).get.getPath) }
  },
  librarySettings
).dependsOn(core)

lazy val testRenderer = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core)

lazy val native = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core, testRenderer % Test)

lazy val vr = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core, testRenderer % Test)

lazy val hot = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core)

lazy val scalajsReactInterop = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core)

lazy val tests = project.settings(macroAnnotationSettings).dependsOn(core, web, hot, scalajsReactInterop)

lazy val example = project.settings(macroAnnotationSettings).dependsOn(web, hot, scalajsReactInterop)

lazy val docsMacros = project.settings(macroAnnotationSettings).dependsOn(web, hot, scalajsReactInterop)

lazy val docs = project.settings(macroAnnotationSettings).dependsOn(web, hot, scalajsReactInterop, docsMacros)
