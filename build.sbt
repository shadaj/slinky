organization in ThisBuild := "me.shadaj"

scalaVersion in ThisBuild := "2.12.7"

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

lazy val crossScalaSettings = Seq(
  crossScalaVersions := Seq("2.12.6", "2.13.0-M4"),
  unmanagedSourceDirectories in Compile += {
    val sourceDir = (sourceDirectory in Compile).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
      case _                       => sourceDir / "scala-2.13-"
    }
  },
  unmanagedSourceDirectories in Test += {
    val sourceDir = (sourceDirectory in Test).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
      case _                       => sourceDir / "scala-2.13-"
    }
  },
  libraryDependencies += "org.scala-lang.modules" %%% "scala-collection-compat" % "0.1.1"
)

lazy val librarySettings = Seq(
  scalacOptions += {
    val origVersion = version.value
    val githubVersion = if (origVersion.contains("-")) {
      origVersion.split('-').last
    } else {
      s"v$origVersion"
    }

    val a = baseDirectory.value.toURI
    val g = "https://raw.githubusercontent.com/shadaj/slinky"
    s"-P:scalajs:mapSourceURI:$a->$g/$githubVersion/${baseDirectory.value.getName}/"
  }
)

addCommandAlias(
  "publishSignedAll",
  (slinky: ProjectDefinition[ProjectReference])
    .aggregate
    .map(p => s"+ ${p.asInstanceOf[LocalProject].project}/publishSigned")
    .mkString(";", ";", "")
)

lazy val macroAnnotationSettings = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
  scalacOptions ++= {
    if (scalaVersion.value == "2.13.0-M4") Seq("-Ymacro-annotations")
    else Seq.empty
  },
  libraryDependencies ++= {
    if (scalaVersion.value == "2.13.0-M4") Seq.empty
    else Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
  }
)

lazy val generator = project.disablePlugins(SbtIdeaPlugin)

lazy val readWrite = project.settings(librarySettings, crossScalaSettings).disablePlugins(SbtIdeaPlugin)

lazy val core = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(readWrite).disablePlugins(SbtIdeaPlugin)

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
  librarySettings,
  crossScalaSettings
).dependsOn(core).disablePlugins(SbtIdeaPlugin)

lazy val testRenderer = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core).disablePlugins(SbtIdeaPlugin)

lazy val native = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core, testRenderer % Test).disablePlugins(SbtIdeaPlugin)

lazy val vr = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core, testRenderer % Test).disablePlugins(SbtIdeaPlugin)

lazy val hot = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core).disablePlugins(SbtIdeaPlugin)

lazy val scalajsReactInterop = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core, web % Test).disablePlugins(SbtIdeaPlugin)

lazy val tests = project.settings(macroAnnotationSettings, crossScalaSettings).dependsOn(core, web, hot).disablePlugins(SbtIdeaPlugin)

lazy val docsMacros = project.settings(macroAnnotationSettings).dependsOn(web, hot).disablePlugins(SbtIdeaPlugin)

lazy val docs = project.settings(macroAnnotationSettings).dependsOn(web, hot, docsMacros).disablePlugins(SbtIdeaPlugin)

lazy val coreIntellijSupport = project
