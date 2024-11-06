import _root_.io.github.davidgregory084._

ThisBuild / organization := "me.shadaj"

addCommandAlias("style", "compile:scalafix; test:scalafix; compile:scalafmt; test:scalafmt; scalafmtSbt")
addCommandAlias(
  "styleCheck",
  "compile:scalafix --check; test:scalafix --check; compile:scalafmtCheck; test:scalafmtCheck; scalafmtSbtCheck"
)

val scala212 = "2.12.19"
val scala213 = "2.13.14"
val scala3   = "3.3.3"

ThisBuild / scalaVersion := scala213
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := "4.9.9"

ThisBuild / tpolecatDefaultOptionsMode := DevMode

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
  crossScalaVersions := Seq(scala212, scala213, scala3),
  scalacOptions += "-Wconf:cat=unused-nowarn:s",
  Compile / unmanagedSourceDirectories ++= {
    val sourceDir = (Compile / sourceDirectory).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _))            => Seq(sourceDir / "scala-2.13+")
      case Some((2, n)) if n >= 13 => Seq(sourceDir / "scala-2.13+")
      case _                       => Seq(sourceDir / "scala-2.13-")
    }
  },
  Test / unmanagedSourceDirectories ++= {
    val sourceDir = (Test / sourceDirectory).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _))            => Seq(sourceDir / "scala-2.13+")
      case Some((2, n)) if n >= 13 => Seq(sourceDir / "scala-2.13+")
      case _                       => Seq(sourceDir / "scala-2.13-")
    }
  },
  scalafixConfig := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Some(file(".scalafix-scala3.conf"))
      case _            => None
    }
  }
)

lazy val crossScala2OnlySettings =
  (crossScalaVersions := Seq(scala212, scala213)) +: crossScalaSettings.tail

lazy val librarySettings = Seq(
  scalacOptions += {
    val origVersion = version.value
    val githubVersion = if (origVersion.contains("-")) {
      origVersion.split('-').last
    } else {
      s"v$origVersion"
    }

    val a   = baseDirectory.value.toURI
    val g   = "https://raw.githubusercontent.com/shadaj/slinky"
    val opt = if (scalaVersion.value == scala3) "-scalajs-mapSourceURI" else "-P:scalajs:mapSourceURI"
    s"$opt:$a->$g/$githubVersion/${baseDirectory.value.getName}/"
  },
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) =>
      Seq(
        "-source:3.0-migration"
      )
    case _ =>
      Seq.empty
  })
)

lazy val macroAnnotationSettings = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
  scalacOptions ++= {
    if (scalaVersion.value == scala213) Seq("-Ymacro-annotations")
    else Seq.empty
  },
  libraryDependencies ++= {
    if (scalaVersion.value == scala212)
      Seq(compilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full)))
    else Seq.empty
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
          .map(_ => (rootFolder / "html" ** "*.scala").get)

        val svg = (generator / Compile / runMain)
          .toTask(
            Seq("slinky.generator.Generator", "web/svg.json", (rootFolder / "svg").getAbsolutePath, "slinky.web.svg")
              .mkString(" ", " ", "")
          )
          .map(_ => (rootFolder / "svg" ** "*.scala").get)

        html.zip(svg).flatMap(t => t._1.flatMap(h => t._2.map(s => h ++ s)))
      }
      .taskValue,
    Compile / packageSrc / mappings ++= {
      val base  = (Compile / sourceManaged).value
      val files = (Compile / managedSources).value
      files.map(f => (f, f.relativeTo(base).get.getPath))
    },
    librarySettings,
    crossScalaSettings
  )
  .dependsOn(core)

lazy val history = project.settings(librarySettings, crossScala2OnlySettings)

lazy val reactrouter =
  project
    .settings(macroAnnotationSettings, librarySettings, crossScala2OnlySettings)
    .dependsOn(core, web, history)

lazy val testRenderer = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core)

lazy val native =
  project
    .settings(macroAnnotationSettings, librarySettings, crossScala2OnlySettings)
    .dependsOn(core, testRenderer % Test)

lazy val vr =
  project
    .settings(macroAnnotationSettings, librarySettings, crossScala2OnlySettings)
    .dependsOn(core, testRenderer % Test)

lazy val hot = project.settings(macroAnnotationSettings, librarySettings, crossScalaSettings).dependsOn(core)

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.6.0")

lazy val scalajsReactInterop = project
  .settings(
    macroAnnotationSettings,
    librarySettings,
    crossScalaSettings
  )
  .dependsOn(core, web % Test)

lazy val tests =
  project.settings(librarySettings, macroAnnotationSettings, crossScalaSettings).dependsOn(core, web, hot)

lazy val docsMacros = project.settings(macroAnnotationSettings).dependsOn(web, hot)

lazy val docs =
  project.settings(librarySettings, macroAnnotationSettings).dependsOn(web, hot, docsMacros, history)

ThisBuild / updateIntellij := {}
val intelliJVersion = "242.20224.300" // 2023.2

lazy val coreIntellijSupport = project.settings(
  org.jetbrains.sbtidea.Keys.buildSettings :+ (
    intellijBuild := intelliJVersion
  ): _*
)

ThisBuild / intellijBuild := intelliJVersion
ThisBuild / packageMethod := PackagingMethod.Skip()
