package slinky.core.annotations

import scala.annotation.compileTimeOnly
import scala.collection.immutable.Seq
import scala.meta._

@compileTimeOnly("enable macro paradise to expand macro annotations")
class react extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    def createBody(clazz: Defn.Class, name: Type.Name, paramss: Seq[Seq[Term.Param]], isStatelessComponent: Boolean): (Defn.Class, Seq[Stat]) = {
      val (propsDefinition, applyMethods) = clazz.templ.stats.getOrElse(Nil).flatMap { t =>
        t match {
          case defn@q"type Props = ${_}" =>
            Some((defn, Seq()))
          case defn@q"case class Props[..$tparams](...$caseClassparamss) extends ${_}" =>
            val applyTypes = tparams.map(t => Type.Name(t.name.value))
            val applyValues = caseClassparamss.map(ps => ps.map(p => Term.Name(p.name.value)))
            val caseClassApply = if (applyTypes.isEmpty) {
              q"""def apply[..$tparams](...$caseClassparamss): _root_.slinky.core.KeyAndRefAddingStage[Def] =
                    this.apply(${Term.Name("Props")}.apply(...$applyValues))"""
            } else {
              q"""def apply[..$tparams](...$caseClassparamss): _root_.slinky.core.KeyAndRefAddingStage[Def] =
                    this.apply(${Term.Name("Props")}.apply[..$applyTypes](...$applyValues))"""
            }

            Some((defn, Seq(caseClassApply)))
          case _ => None
        }
      }.headOption.getOrElse(abort("Components must define a Props type or case class, but none was found."))

      val stateDefinition = clazz.templ.stats.getOrElse(Nil).flatMap { t =>
        t match {
          case defn@q"type State = ${_}" =>
            Some(defn)
          case defn@q"case class State[..${_}](...${_}) extends ${_}" =>
            Some(defn)
          case _ => None
        }
      }.headOption

      val snapshotDefinition = clazz.templ.stats.getOrElse(Nil).flatMap { t =>
        t match {
          case defn@q"type Snapshot = ${_}" =>
            Some(defn)
          case defn@q"case class Snapshot[..${_}](...${_}) extends ${_}" =>
            Some(defn)
          case _ => None
        }
      }.headOption

      val definitionClass = q"type Def = ${clazz.name}"

      val propsSelect = Type.Select(Term.Name(name.value), Type.Name("Props"))
      val stateSelect = Type.Select(Term.Name(name.value), Type.Name("State"))
      val snapshotSelect = Type.Select(Term.Name(name.value), Type.Name("Snapshot"))

      val propsAndStateAndSnapshotImport = Import(Seq(
        Importer(
          Term.Name(name.value),
          Seq(
            Importee.Name(Name.Indeterminate("Props")),
            Importee.Name(Name.Indeterminate("State")),
            Importee.Name(Name.Indeterminate("Snapshot"))
          )
        )
      ))

      if (stateDefinition.isEmpty && !isStatelessComponent) {
        abort("There is no State type defined. If you want to create a stateless component, extend the StatelessComponent class instead.")
      }

      val newClazz =
        q"""class ${clazz.name}(jsProps: _root_.scala.scalajs.js.Object) extends _root_.slinky.core.DefinitionBase[$propsSelect, $stateSelect, $snapshotSelect](jsProps) {
              $propsAndStateAndSnapshotImport
              null.asInstanceOf[${Type.Name("Props")}]
              null.asInstanceOf[${Type.Name("State")}]
              null.asInstanceOf[${Type.Name("Snapshot")}]
              ..${if (stateDefinition.isEmpty) Seq(q"override def initialState: State = ()") else Seq.empty}
              ..${clazz.templ.stats.getOrElse(Nil).filterNot(s => s == propsDefinition || s == stateDefinition.orNull || s == snapshotDefinition.orNull)}
            }"""

      val originalExtends = clazz.templ.parents.head.asInstanceOf[Term.Apply].fun.asInstanceOf[Ctor.Ref.Name].value

      (newClazz,
        (q"null.asInstanceOf[${Type.Name(originalExtends)}]" +:
        propsDefinition +:
          stateDefinition.getOrElse(q"type State = Unit") +:
          snapshotDefinition.toList) ++
          (definitionClass +: applyMethods)
      )
    }

    def createExternalBody(obj: Defn.Object): Seq[Stat] = {
      obj.templ.stats.getOrElse(Nil).flatMap {
        case q"case class $tname[..$tparams](...$caseClassparamss) extends ${_}" if tname.value == "Props" =>
          val applyTypes = tparams.map(t => Type.Name(t.name.value))
          val applyValues = caseClassparamss.map(ps => ps.map(p => Term.Name(p.name.value)))
          val caseClassApply = if (applyTypes.isEmpty) {
            q"""def apply[..$tparams](...$caseClassparamss): _root_.slinky.core.BuildingComponent[Element, RefType] =
                  this.apply(${Term.Name(tname.value)}.apply(...$applyValues))"""
          } else {
            q"""def apply[..$tparams](...$caseClassparamss): _root_.slinky.core.BuildingComponent[Element, RefType] =
                  this.apply(${Term.Name(tname.value)}.apply[..$applyTypes](...$applyValues))"""
          }

          if (caseClassparamss.flatten.forall(_.default.isDefined) || caseClassparamss.flatten.isEmpty) {
            Seq(
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
            Seq(caseClassApply)
          }
        case _ => Seq.empty
      }
    }

    val isIntellij = try {
      Class.forName("scala.meta.intellij.IDEAContext")
      true
    } catch {
      case _: ClassNotFoundException => false
    }

    defn match {
      case Term.Block(Seq(cls @ Defn.Class(_, name, _, ctor, Template(_, Seq(Term.Apply(Ctor.Ref.Name(sc), _)), _, _)), companion: Defn.Object)) if sc == "Component" || sc == "StatelessComponent" =>
        val (newCls, companionStats) = createBody(cls, name, ctor.paramss, sc == "StatelessComponent")
        val templateStats: Seq[Stat] =
          companionStats ++ companion.templ.stats.getOrElse(Nil)
        val newCompanion = q"object ${Term.Name(name.value)} extends slinky.core.ComponentWrapper { ..$templateStats }"

        if (isIntellij) {
          Term.Block(Seq(q"val ${Pat.Var.Term(Term.Name("_" + cls.name.value))} = null", newCompanion))
        } else {
          Term.Block(Seq(newCls, newCompanion))
        }

      // companion object does not exists
      case cls @ Defn.Class(_, name, _, ctor, Template(_, Seq(Term.Apply(Ctor.Ref.Name(sc), _)), _, _)) if sc == "Component" || sc == "StatelessComponent" =>
        val (newCls, companionStats) = createBody(cls, name, ctor.paramss, sc == "StatelessComponent")
        val companion = q"object ${Term.Name(name.value)} extends _root_.slinky.core.ComponentWrapper { ..$companionStats }"

        if (isIntellij) {
          Term.Block(Seq(q"val ${Pat.Var.Term(Term.Name("_" + cls.name.value))} = null", companion))
        } else {
          Term.Block(Seq(newCls, companion))
        }

      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Ctor.Ref.Name("ExternalComponent"), _)), _, _)) =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        obj.copy(templ = obj.templ.copy(stats = Some(objStats)))

      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Term.ApplyType(Ctor.Ref.Name("ExternalComponentWithAttributes"), _), _)), _, _)) =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        obj.copy(templ = obj.templ.copy(stats = Some(objStats)))

      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Term.ApplyType(Ctor.Ref.Name("ExternalComponentWithRefType"), _), _)), _, _)) =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        obj.copy(templ = obj.templ.copy(stats = Some(objStats)))

      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Term.ApplyType(Ctor.Ref.Name("ExternalComponentWithAttributesWithRefType"), _), _)), _, _)) =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        obj.copy(templ = obj.templ.copy(stats = Some(objStats)))

      case Defn.Object(_, _, Template(_, Seq(Term.Apply(Ctor.Ref.Name("ExternalComponentWithAttributes"), _)), _, _)) =>
        abort("ExternalComponentWithAttributes must take a type argument of the target tag type but found none")

      case _ =>
        abort(s"@react must annotate a class that extends Component or an object that extends ExternalComponent(WithAttributes)(WithRefType), got ${defn.structure}")
    }
  }
}
