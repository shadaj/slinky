package slinky.sbtplugin.component

import scala.annotation.nowarn
import scala.meta._
import slinky.sbtplugin.hasReactAnnotation
import slinky.sbtplugin.createChildrenArgFromType

@nowarn
object FunctionalComponent {

  val standardApply = q"def apply(props: component.Props): _root_.slinky.core.KeyAddingStage = component.apply(props)"

  def matches(obj: Defn.Object): Boolean =
    obj.templ.stats.exists {
      case Defn.Val(_, List(Pat.Var(Term.Name("component"))), _, Term.Apply(Term.ApplyType(Term.Name("FunctionalComponent"), _), _)) => true
      case _ => false
    }

  def transform(obj: Defn.Object): Defn.Object = {
    val stats = obj.templ.stats
    val methods = createFunctionalMethods(stats)
    q"""
      object ${obj.name} {
        ..$stats
        ..${methods.toList}
      }
    """
  }

  private def createFunctionalMethods(stats: Seq[Stat]): Seq[Defn.Def] =
    stats.flatMap {
      case q"case class Props[..$tparams](...$paramss) extends ..$_" =>
        val childrenParam = paramss.flatten.find(_.name.value == "children")
        val paramssWithoutChildren = paramss
          .map(_.filterNot(childrenParam.contains))
          .filterNot(_.isEmpty)

        // Preserve all parameter names for Props.apply call
        val allParamNames = paramss.flatten.map(p => Term.Name(p.name.value))

        val mainApply = childrenParam match {
          case Some(children) =>
            val propsArgs = allParamNames.map { name =>
              if (name.value == "children") createChildrenArgFromType(children)
              else name
            }
            q"""
              def apply[..${tparams}](...$paramssWithoutChildren)($children): _root_.slinky.core.KeyAddingStage =
                component.apply(Props.apply(..$propsArgs))
            """
          case None =>
            if (paramssWithoutChildren.flatten.isEmpty) {
              q"def apply(): _root_.slinky.core.KeyAddingStage = component.apply(Props.apply())"
            } else {
              q"""
                def apply[..${tparams}](...$paramssWithoutChildren): _root_.slinky.core.KeyAddingStage =
                  component.apply(Props.apply(..$allParamNames))
              """
            }
        }

        Seq(mainApply, standardApply)

      case q"type Props = Unit" =>
        Seq(
          q"def apply(): _root_.slinky.core.KeyAddingStage = component.applyProps(())",
        )

      case _ =>
        Seq(standardApply)
    }.distinct

  private def isSeqType(tpe: Type): Boolean = {
    tpe match {
      case Type.Apply(Type.Name("Seq"), _) => true
      case Type.Apply(Type.Select(_, Type.Name("Seq")), _) => true
      case _ => false
    }
  }

  private def isPrimitiveCollection(tpe: Option[Type]): Boolean = {
    val (collType, innerType) = extractCollectionInfo(tpe)
    innerType.exists(inner => slinky.sbtplugin.isPrimitiveType(inner))
  }

  private def isTupleCollection(tpe: Option[Type]): Boolean = {
    val (collType, innerType) = extractCollectionInfo(tpe)
    innerType.exists(inner => slinky.sbtplugin.isPrimitiveTupleType(inner))
  }

  private def extractCollectionInfo(tpe: Option[Type]): (Option[String], Option[String]) =
    slinky.sbtplugin.extractCollectionInfo(tpe)
}