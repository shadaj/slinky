package slinky

import scala.meta._

package object sbtplugin {

  def dialect(scalaVersion: String) =
    scalaVersion match {
      case v if v.startsWith("3") => dialects.Scala3
      case v if v.startsWith("2.13") => dialects.Scala213
      case v if v.startsWith("2.12") => dialects.Scala212
      case _ => dialects.Scala213
    }

  def packagePath(tree: Tree) = tree.collect {
    case pkg: Pkg => pkg.ref.syntax.split('.').toSeq
  }.headOption.getOrElse(Seq.empty)

  def hasReactAnnotation[T <: Tree](tree: T) =
    tree.collect {
      case Mod.Annot(Init(Type.Name("react"), _, _)) => true
    }.exists(identity)

}