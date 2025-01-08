package slinky.sbtplugin.component

import scala.annotation.nowarn
import scala.meta._
import slinky.sbtplugin.hasReactAnnotation

@nowarn
object FunctionalComponent {

  val standardApply = q"def apply(props: component.Props): _root_.slinky.core.facade.ReactElement = component.apply(props)"

  def matches(obj: Defn.Object): Boolean =
    obj.templ.stats.exists {
      case Defn.Val(_, List(Pat.Var(Term.Name("component"))), _, Term.Apply(Term.ApplyType(Term.Name("FunctionalComponent"), _), _)) => true
      case _ => false
    }

  def transform(obj: Defn.Object): Defn.Object = {
    val q"@react object $_ { $self => ..$stats }" = obj

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

        val applyValues = paramss.map(_.map(p => Term.Name(p.name.value)))

        val mainApply = childrenParam match {
          case Some(children) =>

            val body = children.decltpe match {
              case Some(_ @ Type.Name("ReactElement")) =>
                q"""component.apply(Props.apply(..${applyValues.flatten}))"""

              case Some(Type.Apply(Type.Name("Seq" | "List"), Seq(Type.Name("ReactElement")))) =>
                val applyValuesWithVararg = paramss.map { ps =>
                  ps.map { p =>
                    if (p == children) q"${Term.Name(p.name.value)}: _*"
                    else Term.Name(p.name.value)
                  }
                }
                q"""component.apply(Props.apply(..${applyValuesWithVararg.flatten}))"""

              case _ =>
                q"""component.apply(Props.apply(..${applyValues.flatten}))"""
            }

            q"""
              def apply[..${tparams}](...$paramssWithoutChildren)(${children}): _root_.slinky.core.facade.ReactElement =
                $body
            """

          case None =>
            if (paramssWithoutChildren.flatten.isEmpty) {
              q"def apply(): _root_.slinky.core.facade.ReactElement = component.apply(Props.apply())"
            } else {
              q"""
                def apply[..${tparams}](...$paramssWithoutChildren): _root_.slinky.core.facade.ReactElement =
                  component.apply(Props.apply(..${applyValues.flatten}))
              """
            }
        }

        Seq(mainApply, standardApply)

      case q"type Props = Unit" =>
        Seq(q"def apply(): _root_.slinky.core.facade.ReactElement = component.apply(())")

      case _ =>
        Seq(standardApply)

    }.distinct
}