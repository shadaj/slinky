package me.shadaj.slinky.core.annotations

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
              q"""def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.KeyAndRefAddingStage[Def] =
                    this.apply(${Term.Name("Props")}.apply(...$applyValues))"""
            } else {
              q"""def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.KeyAndRefAddingStage[Def] =
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

      val definitionClass = q"type Def = ${clazz.name}"

      val propsSelect = Type.Select(Term.Name(name.value), Type.Name("Props"))
      val stateSelect = Type.Select(Term.Name(name.value), Type.Name("State"))

      val propsAndStateImport = Import(Seq(
        Importer(
          Term.Name(name.value),
          Seq(Importee.Name(Name.Indeterminate("Props")), Importee.Name(Name.Indeterminate("State")))
        )
      ))

      if (stateDefinition.isEmpty && !isStatelessComponent) {
        abort("There is no State type defined. If you want to create a stateless component, extend the StatelessComponent class instead.")
      }

      val newClazz =
        q"""class ${clazz.name}(jsProps: scala.scalajs.js.Object) extends me.shadaj.slinky.core.DefinitionBase[$propsSelect, $stateSelect](jsProps) {
              $propsAndStateImport
              ..${if (stateDefinition.isEmpty) Seq(q"override def initialState: State = ()") else Seq.empty}
              ..${clazz.templ.stats.getOrElse(Nil).filterNot(s => s == propsDefinition || s == stateDefinition.orNull)}
            }"""

      (newClazz,
        propsDefinition +:
          stateDefinition.getOrElse(q"type State = Unit") +:
          definitionClass +:
          applyMethods
      )
    }

    def createExternalBody(obj: Defn.Object): Seq[Stat] = {
      obj.templ.stats.getOrElse(Nil).flatMap {
        case q"case class $tname[..$tparams](...$caseClassparamss) extends ${_}" if tname.value == "Props" =>
          val applyTypes = tparams.map(t => Type.Name(t.name.value))
          val applyValues = caseClassparamss.map(ps => ps.map(p => Term.Name(p.name.value)))
          val caseClassApply = if (applyTypes.isEmpty) {
            q"""def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.BuildingComponent[Element] =
                  this.apply(${Term.Name(tname.value)}.apply(...$applyValues))"""
          } else {
            q"""def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.BuildingComponent[Element] =
                  this.apply(${Term.Name(tname.value)}.apply[..$applyTypes](...$applyValues))"""
          }

          Seq(caseClassApply)
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
        val newCompanion = q"object ${Term.Name(name.value)} extends me.shadaj.slinky.core.ComponentWrapper { ..$templateStats }"

        if (isIntellij) {
          Term.Block(Seq(q"val ${Pat.Var.Term(Term.Name("_" + cls.name.value))} = null", newCompanion))
        } else {
          Term.Block(Seq(newCls, newCompanion))
        }

      // companion object does not exists
      case cls @ Defn.Class(_, name, _, ctor, Template(_, Seq(Term.Apply(Ctor.Ref.Name(sc), _)), _, _)) if sc == "Component" || sc == "StatelessComponent" =>
        val (newCls, companionStats) = createBody(cls, name, ctor.paramss, sc == "StatelessComponent")
        val companion = q"object ${Term.Name(name.value)} extends me.shadaj.slinky.core.ComponentWrapper { ..$companionStats }"

        if (isIntellij) {
          Term.Block(Seq(q"val ${Pat.Var.Term(Term.Name("_" + cls.name.value))} = null", companion))
        } else {
          Term.Block(Seq(newCls, companion))
        }

      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Ctor.Ref.Name(sc), _)), _, _)) if sc == "ExternalComponent" =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        obj.copy(templ = obj.templ.copy(stats = Some(objStats)))

      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Term.ApplyType(Ctor.Ref.Name(sc), _), _)), _, _)) if sc == "ExternalComponentWithAttributes" =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        obj.copy(templ = obj.templ.copy(stats = Some(objStats)))
      case _ =>
        abort(s"@react must annotate a class that extends Component or an object that extends ExternalComponent(WithAttributes), got ${defn.structure}")
    }
  }
}
