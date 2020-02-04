ThisBuild / organization := "me.shadaj"

Global / onChangedBuildSource := ReloadOnSourceChanges
turbo := true

ThisBuild / libraryDependencies += compilerPlugin(scalafixSemanticdb)
addCommandAlias("style", "compile:scalafix; test:scalafix; compile:scalafmt; test:scalafmt; scalafmtSbt")
addCommandAlias(
  "styleCheck",
  "compile:scalafix --check; test:scalafix --check; compile:scalafmtCheck; test:scalafmtCheck; scalafmtSbtCheck"
)

val scala212 = "2.12.10"
val scala213 = "2.13.1"

ThisBuild / scalaVersion := scala212

lazy val slinky = project
  .in(file("."))
  .aggregate(
    readWrite,
    core,
    web,
    history,
    reactrouter,
    testRenderer,
    native,
    vr,
    hot,
    scalajsReactInterop
  )
  .settings(
    publish := {},
    publishLocal := {}
  )

addCommandAlias(
  "publishSignedAll",
  (slinky: ProjectDefinition[ProjectReference]).aggregate
    .map(p => s"+ ${p.asInstanceOf[LocalProject].project}/publishSigned")
    .mkString(";")
)

lazy val crossScalaSettings = Seq(
  crossScalaVersions := Seq(scala212, scala213),
  Compile / unmanagedSourceDirectories += {
    val sourceDir = (Compile / sourceDirectory).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
      case _                       => sourceDir / "scala-2.13-"
    }
  },
  Test / unmanagedSourceDirectories += {
    val sourceDir = (Test / sourceDirectory).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
      case _                       => sourceDir / "scala-2.13-"
    }
  }
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
  },
  scalacOptions --= Seq(
    "-Ywarn-unused:params",
    "-Ywarn-unused:patvars",
    "-Ywarn-dead-code",
    "-Xcheckinit",
    "-Wunused:params",
    "-Wunused:patvars",
    "-Wdead-code"
  )
)

lazy val macroAnnotationSettings = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
  scalacOptions ++= {
    if (scalaVersion.value == scala213) Seq("-Ymacro-annotations")
    else Seq.empty
  },
  libraryDependencies ++= {
    if (scalaVersion.value == scala213) Seq.empty
    else Seq(compilerPlugin(("org.scalamacros" % "paradise" % "2.1.0").cross(CrossVersion.full)))
  }
)

lazy val generator = project

lazy val readWrite = project.settings(librarySettings, crossScalaSettings)

lazy val core = project
  .settings(
    Compile / resourceGenerators += Def.task {
      val rootFolder = (Compile / resourceManaged).value / "META-INF"
      rootFolder.mkdirs()

      IO.write(
        rootFolder / "intellij-compat.json",
        s"""{
           |  "artifact": "me.shadaj % slinky-core-ijext_2.12 % ${version.value}"
           |}""".stripMargin
      )

      Seq(rootFolder / "intellij-compat.json")
    },
    macroAnnotationSettings,
    librarySettings,
    crossScalaSettings
  )
  .dependsOn(readWrite)

lazy val web = project
  .settings(
    Compile / sourceGenerators += Def
      .taskDyn[Seq[File]] {
        val rootFolder = (Compile / sourceManaged).value / "slinky/web"
        rootFolder.mkdirs()

        val html = (generator / Compile / runMain)
          .toTask(
            Seq("slinky.generator.Generator", "web/html.json", (rootFolder / "html").getAbsolutePath, "slinky.web.html")
              .mkString(" ", " ", "")
          )
          .map { _ =>
            (rootFolder / "html" ** "*.scala").get
          }

        val svg = (generator / Compile / runMain)
          .toTask(
            Seq("slinky.generator.Generator", "web/svg.json", (rootFolder / "svg").getAbsolutePath, "slinky.web.svg")
              .mkString(" ", " ", "")
          )
          .map { _ =>
            (rootFolder / "svg" ** "*.scala").get
          }

        html.zip(svg).flatMap(t => t._1.flatMap(h => t._2.map(s => h ++ s)))
      }
      .taskValue,
    Compile / packageSrc / mappings ++= {
      val base  = (Compile / sourceManaged).value
      val files = (Compile / managedSources).value
      files.map { f =>
        (f, f.relativeTo(base).get.getPath)
      }
    },
    librarySettings,
    crossScalaSettings
  )
  .dependsOn(core)

lazy val history = project.settings(librarySettings, crossScalaSettings)

lazy val reactrouter =
  project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core, web, history)

lazy val testRenderer = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core)

lazy val native =
  project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core, testRenderer % Test)

lazy val vr =
  project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core, testRenderer % Test)

lazy val hot = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core)

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.31")

lazy val scalajsReactInterop = project
  .settings(
    macroAnnotationSettings,
    librarySettings,
    publish / skip := scalaJSVersion != "0.6.31"
  )
  .dependsOn(core, web % Test)

lazy val tests =
  project.settings(librarySettings, macroAnnotationSettings, crossScalaSettings).dependsOn(core, web, hot)

lazy val docsMacros = project.settings(macroAnnotationSettings).dependsOn(web, hot)

lazy val docs =
  project.settings(librarySettings, macroAnnotationSettings).dependsOn(web, hot, docsMacros, reactrouter, history)

ThisBuild / updateIntellij := {}

lazy val coreIntellijSupport = project
  .enablePlugins(SbtIdeaPlugin)
  .settings(
    org.jetbrains.sbtidea.Keys.buildSettings
  )
  .settings(
    intellijBuild := "192.6817.14",
    intellijInternalPlugins += "java",
    intellijExternalPlugins += "org.intellij.scala".toPlugin,
    packageMethod := PackagingMethod.Standalone(),
    intellijMainJars ++= maybeToolsJar
  )
