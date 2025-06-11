package slinky.sbtplugin

import sbt._
import sbt.Keys._
import scala.meta._
import scala.meta.parsers.Parsed
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object SlinkyPlugin extends AutoPlugin {
  object autoImport {
    val transformSlinkyFiles = taskKey[Seq[(File, File)]]("Transform React-annotated files")
  }

  import autoImport._

  override def requires = ScalaJSPlugin
  override def trigger = allRequirements

  private def transform(conf: Configuration) = Seq(
    transformSlinkyFiles := {
      val managedDir = (conf / sourceManaged).value
      IO.createDirectory(managedDir)

      val allSources = (conf / unmanagedSources).value
      val transformer = new SlinkyTransformer(scalaVersion.value)

      def processSourceFile(file: File): Option[(File, File)] = {
        val source = IO.read(file)

        dialect(scalaVersion.value)(source).parse[Source] match {
          case Parsed.Success(tree) =>
            if (hasReactAnnotation(tree)) {

              // Create subdirectories in src_managed
              val targetDir = packagePath(tree).foldLeft(managedDir) { (dir, pkg) =>
                val pkgDir = dir / pkg
                IO.createDirectory(pkgDir)
                pkgDir
              }

              // Transform source
              val managedFile = targetDir / file.getName
              val transformedSource = transformer.transform(tree).syntax
              IO.write(managedFile, transformedSource)
              Some((file, managedFile))

            } else {
              None
            }
          case Parsed.Error(_, msg, _) =>
            streams.value.log.error(s"Error parsing ${file.getName}: $msg")
            None
        }
      }

      allSources.flatMap(processSourceFile)
    },

    conf / sources := {
      val origSources = (conf / sources).value
      val transformedSources = transformSlinkyFiles.value.map(_._1)
      origSources.filterNot(f => transformedSources.contains(f))
    },

    conf / sourceGenerators += Def.task {
      transformSlinkyFiles.value.map(_._2)
    }.taskValue,
  )

  override lazy val projectSettings = Seq(
    scalacOptions += "-Wconf:msg=.*vararg splices.*:silent",
    libraryDependencies += "org.scalameta" %% "scalameta" % "4.13.6" cross CrossVersion.for2_13Use3
  ) ++
    inConfig(Compile)(transform(Compile)) ++ inConfig(Test)(transform(Test)) ++
    Seq(
      cleanFiles ++= (Compile / managedSourceDirectories).value ++ (Test / managedSourceDirectories).value
    )
}
