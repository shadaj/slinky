package slinky.sbtplugin

import sbt._
import sbt.Keys._
import scala.meta._
import scala.meta.parsers.Parsed
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object SlinkyPlugin extends AutoPlugin {
  object autoImport {
    val transformSlinkyFiles = taskKey[Seq[File]]("Transform React-annotated files")
  }

  import autoImport._

  override def requires = ScalaJSPlugin
  override def trigger = allRequirements

  private def transform(conf: Configuration) = Seq(
    transformSlinkyFiles := {
      val managedDir = (conf / sourceManaged).value
      val allSources = (conf / unmanagedSources).value

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
              val transformedSource = SlinkyTransformer.transform(tree)
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

      allSources.flatMap(processSourceFile).map(_._2)
    },

    // Exclude the original sources from being compiled
    conf / sources := {
      val origSources = (conf / sources).value
      val transformed = transformSlinkyFiles.value
      val originalFiles = transformed.map(_.getName).toSet
      origSources.filterNot(f => originalFiles.contains(f.getName)) ++ transformed
    },

    conf / sourceGenerators += Def.task {
      transformSlinkyFiles.value
    }.taskValue,
  )

  override lazy val projectSettings = Seq(
    libraryDependencies += "org.scalameta" %%% "scalameta" % "4.12.1"
  ) ++
  inConfig(Compile)(transform(Compile)) ++ inConfig(Test)(transform(Test)) ++
  Seq(
    cleanFiles ++= (Compile / managedSourceDirectories).value ++ (Test / managedSourceDirectories).value
  )
}
