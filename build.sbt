organization in ThisBuild := "me.shadaj"

scalaVersion in ThisBuild := "2.12.8"

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
)

lazy val crossScalaSettings = Seq(
  crossScalaVersions := Seq("2.12.8", "2.13.0-RC1"),
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
  }
)

def commonScalacOptions(scalaVersion: String) = {
  Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    "-unchecked",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard"
  ) ++ (if (priorTo2_13(scalaVersion)) {
    Seq(
      "-Xfuture",
      "-Yno-adapted-args",
      "-deprecation",
      "-Xfatal-warnings" // fails Scaladoc compilation on 2.13
    )
  } else {
    Seq(
      "-Ymacro-annotations"
    )
  })
}

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _                              => false
  }


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
  },
  scalacOptions ++= commonScalacOptions(scalaVersion.value)
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
    if (scalaVersion.value == "2.13.0-RC1") Seq("-Ymacro-annotations")
    else Seq.empty
  },
  libraryDependencies ++= {
    if (scalaVersion.value == "2.13.0-RC1") Seq.empty
    else Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
  }
)

lazy val generator = project

lazy val readWrite = project.settings(librarySettings, crossScalaSettings)

lazy val core = project.settings(
  resourceGenerators in Compile += Def.task {
    val rootFolder = (resourceManaged in Compile).value / "META-INF"
    rootFolder.mkdirs()

    IO.write(
      rootFolder / "intellij-compat.json",
      s"""{
         |  "artifact": "me.shadaj % slinky-core-ijext_2.12 % ${version.value}"
         |}""".stripMargin
    )

    Seq(rootFolder / "intellij-compat.json")
  },
  macroAnnotationSettings, librarySettings, crossScalaSettings
).dependsOn(readWrite)

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
).dependsOn(core)

lazy val testRenderer = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core)

lazy val native = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core, testRenderer % Test)

lazy val vr = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core, testRenderer % Test)

lazy val hot = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core)

lazy val scalajsReactInterop = project.settings(macroAnnotationSettings, librarySettings).dependsOn(core, web % Test)

lazy val tests = project.settings(macroAnnotationSettings, crossScalaSettings).dependsOn(core, web, hot)

lazy val docsMacros = project.settings(macroAnnotationSettings).dependsOn(web, hot)

lazy val docs = project.settings(macroAnnotationSettings).dependsOn(web, hot, docsMacros)

dumpDependencyStructure in ThisBuild := null
ideaExternalPlugins in ThisBuild := Seq.empty
updateIdea in ThisBuild := {}

lazy val coreIntellijSupport = project.enablePlugins(SbtIdeaPlugin).settings(
  org.jetbrains.sbtidea.Keys.buildSettings
).settings(
  ideaBuild := "191.6183.87",
  ideaExternalPlugins += IdeaPlugin.Id("Scala", "org.intellij.scala", None)
)

lazy val coreIntellijSupportRunner = createRunnerProject(coreIntellijSupport, "coreIntellijSupportRunner").settings(
  org.jetbrains.sbtidea.Keys.projectSettings
)
