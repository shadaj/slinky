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

  def extractCollectionInfo(tpe: Option[Type]): (Option[String], Option[String]) = tpe match {
    case Some(Type.Apply(Type.Name(collName), args)) =>
      val innerType = args.headOption.map(_.toString)
      (Some(collName), innerType)
    case _ => (None, None)
  }
  
  def createChildrenArgFromType(children: Term.Param): Term = {
    val (collType, innerType) = extractCollectionInfo(children.decltpe)

    (collType, innerType) match {

      case (Some("List"), Some(inner)) if isPrimitiveTupleType(inner) =>
        q"children.map(e => (e.toString, e.toString)).toList"

      case (Some("List"), _) =>
        q"children.toList"

      case (Some("Seq"), _) =>
        q"scala.collection.immutable.Seq(children:_*)"

      case (Some("Seq"), Some(inner)) if isPrimitiveType(inner) =>
        q"children.map(_.toString)"

      case _ =>
        q"children"
    }
  }

  def isPrimitiveType(typeStr: String) =
    Set("String", "Int", "Long", "Double", "Float", "Boolean").exists(typeStr.contains)

  def isPrimitiveTupleType(typeStr: String) = {
    val hasTupleForm = typeStr.contains("(") && typeStr.contains(",") && typeStr.contains(")")
    val hasTuple2Form = typeStr.contains("Tuple2[")
    (hasTupleForm || hasTuple2Form) && isPrimitiveType(typeStr)
  }
}