import com.typesafe.sbt.pgp.PgpKeys.publishSigned

enablePlugins(ScalaJSPlugin)

organization in ThisBuild := "me.shadaj"

version in ThisBuild := "0.2.0-SNAPSHOT"

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

lazy val core = project.settings(publishSettings: _*)

lazy val web = project.settings(
  sourceGenerators in Compile += Def.taskDyn[Seq[File]] {
    val rootFolder = (sourceManaged in Compile).value / "me/shadaj/slinky/web"
    rootFolder.mkdirs()

    val html = (run in Compile in generator).toTask(Seq("me.shadaj.slinky.generator.MDN", (rootFolder / "html").getAbsolutePath, "me.shadaj.slinky.web.html").mkString(" ", " ", "")).map { _ =>
      (rootFolder / "html" ** "*.scala").get
    }

    val svg = (run in Compile in generator).toTask(Seq("me.shadaj.slinky.generator.SVG", (rootFolder / "svg").getAbsolutePath, "me.shadaj.slinky.web.svg").mkString(" ", " ", "")).map { _ =>
      (rootFolder / "svg" ** "*.scala").get
    }

    html.zip(svg).flatMap(t => t._1.flatMap(h => t._2.map(s => h ++ s)))
  }.taskValue,
  mappings in (Compile, packageSrc) ++= {
    val base  = (sourceManaged  in Compile).value
    val files = (managedSources in Compile).value
    files.map { f => (f, f.relativeTo(base).get.getPath) }
  },
  publishSettings
).dependsOn(core)

lazy val hot = project.dependsOn(core).settings(publishSettings: _*)

lazy val scalajsReactInterop = project.dependsOn(core).settings(publishSettings: _*)

lazy val tests = project.dependsOn(core, web, hot, scalajsReactInterop)

lazy val example = project.dependsOn(web, hot, scalajsReactInterop)

// Publish setup
lazy val ciPublish = taskKey[Unit]("CI Publish")

/**
  * Convert the given command string to a release step action, preserving and      invoking remaining commands
  * Note: This was copied from https://github.com/sbt/sbt-release/blob/663cfd426361484228a21a1244b2e6b0f7656bdf/src/main/scala/ReleasePlugin.scala#L99-L115
  */
def runCommandAndRemaining(command: String): State => State = { st: State =>
  import sbt.complete.Parser
  @annotation.tailrec
  def runCommand(command: String, state: State): State = {
    val nextState = Parser.parse(command, state.combinedParser) match {
      case Right(cmd) => cmd()
      case Left(msg) => throw sys.error(s"Invalid programmatic input:\n$msg")
    }
    nextState.remainingCommands.toList match {
      case Nil => nextState
      case head :: tail => runCommand(head, nextState.copy(remainingCommands = tail))
    }
  }
  runCommand(command, st.copy(remainingCommands = Nil)).copy(remainingCommands = st.remainingCommands)
}

lazy val publishSettings = Seq(
  ciPublish := Def.taskDyn {
    if (isSnapshot.value && sys.env.get("TRAVIS_BRANCH").exists(_ == "master") && sys.env.get("TRAVIS_PULL_REQUEST").exists(_ == "false")) {
      publishSigned.toTask
    } else if (sys.env.get("TRAVIS_PULL_REQUEST").exists(_ == "false") && sys.env.get("TRAVIS_TAG").exists(_.nonEmpty) && !isSnapshot.value) {
      publishSigned.toTask.map { _ =>
        runCommandAndRemaining("sonatypeRelease")(state.value)
        ()
      }
    } else {
      Def.task[Unit] {
        println("Not publishing " + name.value)
      }
    }
  }.tag(Tags.Publish).value
)
