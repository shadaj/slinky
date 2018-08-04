organization in ThisBuild := "me.shadaj"

scalaVersion in ThisBuild := "2.12.6"

scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")

lazy val slinky = project.in(file(".")).aggregate(
  readWrite,
  core,
  web,
  testRenderer,
  native,
  vr,
  hot,
  scalajsReactInterop,
  coreIntellijSupport
).settings(
  publish := {},
  publishLocal := {}
).disablePlugins(SbtIdeaPlugin)

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
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val generator = project.disablePlugins(SbtIdeaPlugin)

lazy val readWrite = project.settings(librarySettings).disablePlugins(SbtIdeaPlugin)

lazy val core = project.settings(macroAnnotationSettings, librarySettings).dependsOn(readWrite).disablePlugins(SbtIdeaPlugin)

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
).dependsOn(core).disablePlugins(SbtIdeaPlugin)

lazy val testRenderer = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core).disablePlugins(SbtIdeaPlugin)

lazy val native = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core, testRenderer % Test).disablePlugins(SbtIdeaPlugin)

lazy val vr = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core, testRenderer % Test).disablePlugins(SbtIdeaPlugin)

lazy val hot = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core).disablePlugins(SbtIdeaPlugin)

lazy val scalajsReactInterop = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core).disablePlugins(SbtIdeaPlugin)

lazy val tests = project.settings(macroAnnotationSettings).dependsOn(core, web, hot, scalajsReactInterop).disablePlugins(SbtIdeaPlugin)

lazy val example = project.settings(macroAnnotationSettings).dependsOn(web, hot, scalajsReactInterop).disablePlugins(SbtIdeaPlugin)

lazy val docsMacros = project.settings(macroAnnotationSettings).dependsOn(web, hot, scalajsReactInterop).disablePlugins(SbtIdeaPlugin)

lazy val docs = project.settings(macroAnnotationSettings).dependsOn(web, hot, scalajsReactInterop, docsMacros).disablePlugins(SbtIdeaPlugin)

lazy val coreIntellijSupport = project
