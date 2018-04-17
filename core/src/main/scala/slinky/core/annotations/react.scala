package slinky.core.annotations

import slinky.core._

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("Enable macro paradise to expand the @react macro annotation")
class react extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ReactMacrosImpl.reactImpl
}

object ReactMacrosImpl {
  private def parentsContainsType(c: whitebox.Context)(parents: Seq[c.Tree], tpe: c.Type) = {
    parents.exists { p =>
      c.typecheck(p, mode = c.TYPEmode).tpe.typeSymbol == tpe.typeSymbol
    }
  }


  def reactImpl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def createComponentBody(c: whitebox.Context)(cls: c.Tree, isStatelessComponent: Boolean): (c.Tree, List[c.Tree]) = {
      import c.universe._
      val q"..$_ class ${className: Name} extends ..$parents { $self => ..$stats}" = cls
      val (propsDefinition, applyMethods) = stats.flatMap {
        case defn@q"type Props = ${_}" =>
          Some((defn, Seq()))

        case defn@q"case class Props[..$tparams](...$caseClassparamss) extends ..$_" =>
          val applyValues = caseClassparamss.map(ps => ps.map(_.name))
          val caseClassApply =
            q"""def apply[..$tparams](...$caseClassparamss): _root_.slinky.core.KeyAndRefAddingStage[Def] =
                  this.apply(Props.apply[..$tparams](...$applyValues))"""

          Some((defn, Seq(caseClassApply)))

        case defn => None
      }.headOption.getOrElse(c.abort(c.enclosingPosition, "Components must define a Props type or case class, but none was found."))

      val stateDefinition = stats.flatMap {
        case defn@q"type State = ${_}" =>
          Some(defn)
        case defn@q"case class State[..$_](...$_) extends ..$_" =>
          Some(defn)
        case _ => None
      }.headOption

      val snapshotDefinition = stats.flatMap {
        case defn@q"type Snapshot = ${_}" =>
          Some(defn)
        case defn@q"case class Snapshot[..$_](...$_) extends ..$_" =>
          Some(defn)
        case _ => None
      }.headOption

      val clazz = TypeName(className.asInstanceOf[Name].toString)
      val companion = TermName(className.asInstanceOf[Name].toString)

      val definitionClass = q"type Def = $clazz"

      val propsAndStateAndSnapshotImport = Seq(
        q"import $companion.Props",
        q"import $companion.State",
        q"import $companion.Snapshot"
      )

      if (stateDefinition.isEmpty && !isStatelessComponent) {
        c.abort(c.enclosingPosition, "There is no State type defined. If you want to create a stateless component, extend the StatelessComponent class instead.")
      }

      val newClazz =
        q"""class $clazz(jsProps: _root_.scala.scalajs.js.Object) extends _root_.slinky.core.DefinitionBase[$companion.Props, $companion.State, $companion.Snapshot](jsProps) {
              ..$propsAndStateAndSnapshotImport
              null.asInstanceOf[Props]
              null.asInstanceOf[State]
              null.asInstanceOf[Snapshot]
              ..${if (stateDefinition.isEmpty) Seq(q"override def initialState: State = ()") else Seq.empty}
              ..${stats.filterNot(s => s == propsDefinition || s == stateDefinition.orNull || s == snapshotDefinition.orNull)}
            }"""

      (newClazz,
        ((q"null.asInstanceOf[${parents.head}]" +:
          propsDefinition +:
          stateDefinition.getOrElse(q"type State = Unit") +:
          snapshotDefinition.toList) ++
          (definitionClass +: applyMethods)).asInstanceOf[List[c.Tree]]
      )
    }

    def createExternalBody(c: whitebox.Context)(obj: c.Tree): List[c.Tree] = {
      val q"..$_ object ${objectName: Name} extends ..$parents { $self => ..$stats}" = obj
      stats.flatMap {
        case q"case class Props[..$tparams](...$caseClassparamss) extends ..$_" =>
          val applyValues = caseClassparamss.map(ps => ps.map(_.name))
          val caseClassApply =
            q"""def apply[..$tparams](...$caseClassparamss): _root_.slinky.core.BuildingComponent[Element, RefType] =
                  this.apply(Props.apply[..$tparams](...$applyValues))"""

          if (caseClassparamss.flatten.forall(_.rhs.nonEmpty) || caseClassparamss.flatten.isEmpty) {
            List(
              caseClassApply,
              q"""def apply(mod: _root_.slinky.core.AttrPair[Element], tagMods: _root_.slinky.core.AttrPair[Element]*): _root_.slinky.core.BuildingComponent[Element, RefType] = {
                    new _root_.slinky.core.BuildingComponent[Element, RefType](component, _root_.scala.scalajs.js.Dynamic.literal(), mods = (mod +: tagMods).asInstanceOf[_root_.scala.collection.immutable.Seq[_root_.slinky.core.AttrPair[Element]]])
                  }""",
              q"""def withKey(key: String): _root_.slinky.core.BuildingComponent[Element, RefType] = new _root_.slinky.core.BuildingComponent(component, _root_.scala.scalajs.js.Dynamic.literal(), key = key)""",
              q"""def withRef(ref: RefType => Unit): _root_.slinky.core.BuildingComponent[Element, RefType] = new _root_.slinky.core.BuildingComponent(component, _root_.scala.scalajs.js.Dynamic.literal(), ref = ref)""",
              q"""def withRef(ref: _root_.slinky.core.facade.ReactRef[RefType]): _root_.slinky.core.BuildingComponent[Element, RefType] = {
                    new _root_.slinky.core.BuildingComponent[Element, RefType](component, _root_.scala.scalajs.js.Dynamic.literal(), ref = ref)
                  }""",
              q"""def apply(children: _root_.slinky.core.facade.ReactElement*): _root_.slinky.core.facade.ReactElement = {
                    _root_.slinky.core.facade.React.createElement(component, _root_.scala.scalajs.js.Dynamic.literal().asInstanceOf[_root_.scala.scalajs.js.Dictionary[js.Any]], children: _*)
                  }"""
            )
          } else {
            List(caseClassApply)
          }
        case _ => List.empty
      }.asInstanceOf[List[c.Tree]]
    }

    val outs: List[Tree] = annottees.map(_.tree).toList match {
      case Seq(cls @ q"..$_ class $className extends ..$parents { $_ => ..$_}")
        if parentsContainsType(c)(parents, typeOf[Component]) || parentsContainsType(c)(parents, typeOf[StatelessComponent]) =>
        val (newCls, companionStats) = createComponentBody(c)(cls, parentsContainsType(c)(parents, typeOf[StatelessComponent]))
        List(newCls, q"object ${TermName(className.decodedName.toString)} extends ${typeOf[ComponentWrapper]} { ..$companionStats }")

      case Seq(cls @ q"..$_ class $className extends ..$parents { $_ => ..$_}", obj @ q"..$_ object $_ extends ..$_ { $_ => ..$objStats }")
        if parentsContainsType(c)(parents, typeOf[Component]) || parentsContainsType(c)(parents, typeOf[StatelessComponent]) =>
        val (newCls, companionStats) = createComponentBody(c)(cls, parentsContainsType(c)(parents, typeOf[StatelessComponent]))
        List(newCls, q"object ${TermName(className.decodedName.toString)} extends ${typeOf[ComponentWrapper]} { ..${objStats ++ companionStats} }")

      case Seq(obj @ q"..$_ object $objName extends ..$parents { $_ => ..$objStats}")
        if parentsContainsType(c)(parents, typeOf[ExternalComponent]) ||
           parentsContainsType(c)(parents, typeOf[ExternalComponentWithAttributes[_]]) ||
           parentsContainsType(c)(parents, typeOf[ExternalComponentWithRefType[_]]) ||
          parentsContainsType(c)(parents, typeOf[ExternalComponentWithAttributesWithRefType[_, _]]) =>
        val companionStats = createExternalBody(c)(obj)
        List(q"object $objName extends ${typeOf[ExternalComponent]} { ..${objStats ++ companionStats} }")

      case defn =>
        c.abort(c.enclosingPosition, s"@react must annotate a class that extends Component or an object that extends ExternalComponent(WithAttributes)(WithRefType), got $defn")
    }

    c.Expr[Any](Block(outs, Literal(Constant(()))))
  }
}