package slinky.core.annotations

import slinky.core._

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.scalajs.js

@compileTimeOnly("Enable macro paradise to expand the @react macro annotation")
class react extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ReactMacrosImpl.reactImpl
}

object react {
  @inline def bump(thunk: => Unit): Unit = {}
}

object ReactMacrosImpl {
  private def parentsContainsType(c: whitebox.Context)(parents: Seq[c.Tree], tpe: c.Type) = {
    import scala.reflect.macros.TypecheckException
    parents.exists { p =>
      try {
        c.typecheck(p, mode = c.TYPEmode).tpe.typeSymbol == tpe.typeSymbol
      } catch {
        case _: TypecheckException =>
          // with local imports, typechecking fails so we just fall back and skip the check
          true
      }
    }
  }

  def createComponentBody(c: whitebox.Context)(cls: c.Tree): (c.Tree, List[c.Tree]) = {
    import c.universe._
    val q"..$_ class ${className: Name} extends ..$parents { $self => ..$stats}" = cls
    val (propsDefinition, applyMethods) = stats.flatMap {
      case defn@q"..$_ type Props = ${_}" =>
        Some((defn, Seq()))

      case defn@q"case class Props[..$tparams](...${caseClassparamssRaw}) extends ..$_ { $_ => ..$_ }" =>
        val caseClassparamss = caseClassparamssRaw.asInstanceOf[Seq[Seq[ValDef]]]
        val childrenParam = caseClassparamss.flatten.find(_.name.toString == "children")

        val paramssWithoutChildren = caseClassparamss.map(_.filterNot(childrenParam.contains))
          .filterNot(_.isEmpty)
        val applyValues = caseClassparamss.map(ps => ps.map(_.name))

        val caseClassApply = if (childrenParam.isDefined) {
          // from https://groups.google.com/forum/#!topic/scala-user/dUOonrP_5K4
          val body = c.typecheck(childrenParam.get.tpt, c.TYPEmode).tpe match {
            case TypeRef(_, sym, _) if sym == definitions.RepeatedParamClass =>
              val applyValuesChildrenVararg = caseClassparamss.map(ps => ps.map { ps =>
                if (ps == childrenParam.get) {
                  q"${ps.name}: _*"
                } else q"${ps.name}"
              })

              q"this.apply(Props.apply[..$tparams](...$applyValuesChildrenVararg))"
            case _ =>
              q"this.apply(Props.apply[..$tparams](...$applyValues))"
          }

          q"""def apply[..$tparams](...$paramssWithoutChildren)(${childrenParam.get}): _root_.slinky.core.KeyAndRefAddingStage[Def] =
                $body"""
        } else {
          q"""def apply[..$tparams](...$paramssWithoutChildren): _root_.slinky.core.KeyAndRefAddingStage[Def] =
                this.apply(Props.apply[..$tparams](...$applyValues))"""
        }

        Some((defn, Seq(caseClassApply)))

      case _ => None
    }.headOption.getOrElse(c.abort(c.enclosingPosition, "Components must define a Props type or case class, but none was found."))

    val stateDefinition = stats.flatMap {
      case defn@q"..$_ type State = ${_}" =>
        Some(defn)
      case defn@q"case class State[..$_](...$_) extends ..$_ { $_ => ..$_ }" =>
        Some(defn)
      case _ => None
    }.headOption

    val snapshotDefinition = stats.flatMap {
      case defn@q"type Snapshot = ${_}" =>
        Some(defn)
      case defn@q"case class Snapshot[..$_](...$_) extends ..$_ { $_ => ..$_ }" =>
        Some(defn)
      case _ => None
    }.headOption

    val clazz = TypeName(className.asInstanceOf[Name].toString)
    val companion = TermName(className.asInstanceOf[Name].toString)

    val definitionClass = q"type Def = $clazz"

    val newClazz =
      q"""class $clazz(jsProps: _root_.scala.scalajs.js.Object) extends ${clazz.toTermName}.Definition(jsProps) {
              import $companion.{Props, State, Snapshot}
              _root_.slinky.core.annotations.react.bump({
                null.asInstanceOf[Props]
                null.asInstanceOf[State]
                null.asInstanceOf[Snapshot]
              })
              ..${stats.filterNot(s => s == propsDefinition || s == stateDefinition.orNull || s == snapshotDefinition.orNull)}
            }"""

    (newClazz,
      ((q"null.asInstanceOf[${parents.head}]" +:
        propsDefinition +:
        stateDefinition.toList) ++
        snapshotDefinition.toList ++
        (definitionClass +: applyMethods)).asInstanceOf[List[c.Tree]]
    )
  }

  def createExternalBody(c: whitebox.Context)(obj: c.Tree): (List[c.Tree], c.Type, c.Type) = {
    import c.universe._

    val q"..$_ object ${objectName: Name} extends ..$parents { $self => ..$stats}" = obj

    val typecheckedParent = c.typecheck(parents.head.asInstanceOf[c.universe.Tree], mode = c.TYPEmode)
    val refType = if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponent])) {
      typeOf[js.Object]
    } else if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponentWithAttributes[_]])) {
      typeOf[js.Object]
    } else if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponentWithRefType[_]])) {
      typecheckedParent.tpe.typeArgs.head
    } else if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponentWithAttributesWithRefType[_, _]])) {
      typecheckedParent.tpe.typeArgs(1)
    } else {
      null
    }

    val elementType = if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponent])) {
      typeOf[Nothing]
    } else if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponentWithAttributes[_]])) {
      typecheckedParent.tpe.typeArgs.head
    } else if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponentWithRefType[_]])) {
      typeOf[Nothing]
    } else if (parentsContainsType(c)(parents.asInstanceOf[Seq[c.Tree]], typeOf[ExternalComponentWithAttributesWithRefType[_, _]])) {
      typecheckedParent.tpe.typeArgs.head
    } else {
      null
    }

    val body = stats.flatMap {
      case q"case class Props[..$tparams](...$caseClassparamss) extends ..$_" =>
        val applyValues = caseClassparamss.map(ps => ps.map(_.name))
        val caseClassApply =
          q"""def apply[..$tparams](...$caseClassparamss): _root_.slinky.core.BuildingComponent[$elementType, $refType] =
                  this.apply(Props.apply[..$tparams](...$applyValues)).asInstanceOf[_root_.slinky.core.BuildingComponent[$elementType, $refType]]"""

        if (caseClassparamss.flatten.forall(_.rhs.nonEmpty) || caseClassparamss.flatten.isEmpty) {
          List(
            caseClassApply,
            q"""def apply(mods: _root_.slinky.core.TagMod[$elementType]*): _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                    new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                      _root_.scala.scalajs.js.Array(component.asInstanceOf[js.Any], _root_.scala.scalajs.js.Dictionary.empty)
                    ).apply(mods: _*)
                  }""",
            q"""def withKey(key: String): _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                  new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                    _root_.scala.scalajs.js.Array(component.asInstanceOf[js.Any], _root_.scala.scalajs.js.Dictionary.empty)
                  ).withKey(key)
                }""",
            q"""def withRef(ref: $refType => Unit): _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                  new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                    _root_.scala.scalajs.js.Array(component.asInstanceOf[js.Any], _root_.scala.scalajs.js.Dictionary.empty)
                  ).withRef(ref)
                }""",
            q"""def withRef(ref: _root_.slinky.core.facade.ReactRef[$refType]): _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                    new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                      _root_.scala.scalajs.js.Array(component.asInstanceOf[js.Any], _root_.scala.scalajs.js.Dictionary.empty)
                    ).withRef(ref)
                  }"""
          )
        } else {
          List(caseClassApply)
        }
      case _ => List.empty
    }.asInstanceOf[List[c.Tree]]

    (body, refType, elementType)
  }


  def reactImpl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val outs: List[Tree] = annottees.map(_.tree).toList match {
      case Seq(cls @ q"..$_ class $className extends ..$parents { $_ => ..$_}")
        if parentsContainsType(c)(parents, typeOf[Component]) ||
           parentsContainsType(c)(parents, typeOf[StatelessComponent]) =>
        val (newCls, companionStats) = createComponentBody(c)(cls)
        val parent = tq"${TermName(parents.head.toString)}.Wrapper"
        List(newCls, q"object ${TermName(className.decodedName.toString)} extends $parent { ..$companionStats }")

      case Seq(cls @ q"..$_ class $className extends ..$parents { $_ => ..$_}", obj @ q"..$_ object $_ extends ..$_ { $_ => ..$objStats }")
        if parentsContainsType(c)(parents, typeOf[Component]) ||
           parentsContainsType(c)(parents, typeOf[StatelessComponent])=>
        val (newCls, companionStats) = createComponentBody(c)(cls)
        val parent = tq"${TermName(parents.head.toString)}.Wrapper"
        List(newCls, q"object ${TermName(className.decodedName.toString)} extends $parent { ..${objStats ++ companionStats} }")

      case Seq(obj @ q"..$_ object $objName extends ..$parents { $_ => ..$objStats}")
        if parentsContainsType(c)(parents, typeOf[ExternalComponent]) ||
           parentsContainsType(c)(parents, typeOf[ExternalComponentWithAttributes[_]]) ||
           parentsContainsType(c)(parents, typeOf[ExternalComponentWithRefType[_]]) ||
          parentsContainsType(c)(parents, typeOf[ExternalComponentWithAttributesWithRefType[_, _]]) =>
        val (companionStats, refType, elementType) = createExternalBody(c)(obj)
        List(q"object $objName extends _root_.slinky.core.ExternalComponentWithAttributesWithRefType[$elementType, $refType] { ..${objStats ++ companionStats} }")

      case Seq(obj @ q"$pre object $objName extends ..$parents { $self => ..$objStats }") if (objStats.exists {
        case q"$_ val component: $_ = $_" => true
        case _ => false
      }) =>
        val applyMethods = objStats.flatMap {
          case defn@q"case class Props[..$tparams](...${caseClassparamssRaw}) extends ..$_ { $_ => ..$_ }" =>
            val caseClassparamss = caseClassparamssRaw.asInstanceOf[Seq[Seq[ValDef]]]
            val childrenParam = caseClassparamss.flatten.find(_.name.toString == "children")

            val paramssWithoutChildren = caseClassparamss.map(_.filterNot(childrenParam.contains))
              .filterNot(_.isEmpty)
            val applyValues = caseClassparamss.map(ps => ps.map(_.name))
    
            val caseClassApply = if (childrenParam.isDefined) {
              // from https://groups.google.com/forum/#!topic/scala-user/dUOonrP_5K4
              val body = c.typecheck(childrenParam.get.tpt, c.TYPEmode).tpe match {
                case TypeRef(_, sym, _) if sym == definitions.RepeatedParamClass =>
                  val applyValuesChildrenVararg = caseClassparamss.map(ps => ps.map { ps =>
                    if (ps == childrenParam.get) {
                      q"${ps.name}: _*"
                    } else q"${ps.name}"
                  })

                  q"component.apply(Props.apply(...$applyValuesChildrenVararg))"
                case _ =>
                  q"component.apply(Props.apply(...$applyValues))"
              }

              q"""def apply[..$tparams](...$paramssWithoutChildren)(${childrenParam.get}) =
                    $body"""
            } else {
              q"""def apply[..$tparams](...$paramssWithoutChildren) =
                    component.apply(Props.apply(...$applyValues))"""
            }

            Some(Seq(caseClassApply))

          case _ => None
        }.headOption.getOrElse[Seq[Tree]] {
          c.warning(c.enclosingPosition, "Props case class was not found. The component's simple apply method will still be added to the object.")
          Seq.empty
        } ++ Seq(
          q"def apply(props: component.Props) = component.apply(props)"
        )

        List(q"$pre object $objName extends ..$parents { $self => ..${objStats ++ applyMethods} }")

      case defn =>
        c.abort(
          c.enclosingPosition,
          """@react must annotate:
            |  - a class that extends (Stateless)Component,
            |  - an object that extends ExternalComponent(WithAttributes)(WithRefType)
            |  - an object defining a `val component = FunctionalComponent(...)`""".stripMargin
        )
    }

    c.Expr[Any](Block(outs, Literal(Constant(()))))
  }
}
