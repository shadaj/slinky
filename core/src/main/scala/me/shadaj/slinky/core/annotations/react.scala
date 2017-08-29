package me.shadaj.slinky.core.annotations

import scala.annotation.compileTimeOnly
import scala.collection.immutable.Seq
import scala.meta._

@compileTimeOnly("enable macro paradise to expand macro annotations")
class react extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    def createBody(clazz: Defn.Class, name: Type.Name, paramss: Seq[Seq[Term.Param]]): (Seq[Stat], Seq[Stat]) = {
      val (propsDefinition, applyMethods) = clazz.templ.stats.getOrElse(Nil).flatMap { t =>
        t match {
          case defn@q"type Props = $props" =>
            Some((defn, Seq()))
          case defn@q"case class $tname[..$tparams](...$caseClassparamss) extends $template" if tname.value == "Props" =>
            val applyTypes = tparams.map(t => Type.Name(t.name.value))
            val applyValues = caseClassparamss.map(ps => ps.map(p => Term.Name(p.name.value)))
            val caseClassApply = if (applyTypes.isEmpty) {
              q"""
                 def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.KeyAndRefAddingStage[Def] = this.apply(${Term.Name(tname.value)}.apply(...$applyValues))
               """
            } else {
              q"""
                 def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.KeyAndRefAddingStage[Def] = this.apply(${Term.Name(tname.value)}.apply[..$applyTypes](...$applyValues))
               """
            }

            Some((defn, Seq(caseClassApply)))
          case _ => None
        }
      }.head

      val stateDefinition = clazz.templ.stats.getOrElse(Nil).flatMap { t =>
        t match {
          case defn@q"type State = $props" =>
            Some(defn)
          case defn@q"case class $tname[..$tparams](...$caseClassparamss) extends $template" if tname.value == "State" =>
            Some(defn)
          case _ => None
        }
      }.head

      val definitionClass =
        q"""
           @__SJSDefined
           class Def(jsProps: scala.scalajs.js.Object) extends Definition(jsProps) {
             ..${clazz.templ.stats.getOrElse(Nil).filterNot(s => s == propsDefinition || s == stateDefinition)}
           }
         """

      val propsSelect = Type.Select(Term.Name(name.value), Type.Name("Props"))
      val stateSelect = Type.Select(Term.Name(name.value), Type.Name("State"))
      (q"type Props = $propsSelect" +:
        q"type State = $stateSelect" +:
        clazz.templ.stats.getOrElse(Nil).filterNot(s => s == propsDefinition || s == stateDefinition),
        q"import scala.scalajs.js.annotation.{ScalaJSDefined => __SJSDefined}" +: propsDefinition +: stateDefinition +: definitionClass +: applyMethods)
    }

    def createExternalBody(obj: Defn.Object): Seq[Stat] = {
      obj.templ.stats.getOrElse(Nil).flatMap { t =>
        t match {
          case defn@q"type Props = $props" =>
            Some(Seq())
          case defn@q"case class $tname[..$tparams](...$caseClassparamss) extends $template" if tname.value == "Props" =>
            val applyTypes = tparams.map(t => Type.Name(t.name.value))
            val applyValues = caseClassparamss.map(ps => ps.map(p => Term.Name(p.name.value)))
            val caseClassApply = if (applyTypes.isEmpty) {
              q"""
                 def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.BuildingComponent[Props, Element] = this.apply(${Term.Name(tname.value)}.apply(...$applyValues))
               """
            } else {
              q"""
                 def apply[..$tparams](...$caseClassparamss): me.shadaj.slinky.core.BuildingComponent[Props, Element] = this.apply(${Term.Name(tname.value)}.apply[..$applyTypes](...$applyValues))
               """
            }

            Some(Seq(caseClassApply))
          case _ => None
        }
      }.headOption.getOrElse(Seq.empty)
    }

    defn match {
      case Term.Block(Seq(cls @ Defn.Class(_, name, _, ctor, Template(_, Seq(Term.Apply(Ctor.Ref.Name(sc), _)), _, _)), companion: Defn.Object)) if sc == "Component" =>
        val (clsStats, companionStats) = createBody(cls, name, ctor.paramss)
        val templateStats: Seq[Stat] =
          companionStats ++ companion.templ.stats.getOrElse(Nil)
        val newCompanion = q"object ${Term.Name(name.value)} extends me.shadaj.slinky.core.ComponentWrapper { ..$templateStats }"
        Term.Block(Seq(cls.copy(templ = cls.templ.copy(stats = Some(clsStats))), newCompanion))
      // companion object does not exists
      case cls @ Defn.Class(_, name, _, ctor, Template(_, Seq(Term.Apply(Ctor.Ref.Name(sc), _)), _, _)) if sc == "Component" =>
        val (clsStats, companionStats) = createBody(cls, name, ctor.paramss)
        val companion   = q"object ${Term.Name(name.value)} extends me.shadaj.slinky.core.ComponentWrapper { ..$companionStats }"
        Term.Block(Seq(cls.copy(templ = cls.templ.copy(stats = Some(clsStats))), companion))
      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Ctor.Ref.Name(sc), _)), _, _)) if sc == "ExternalComponent" =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        q"object ${obj.name} extends ${Ctor.Ref.Name(sc)} { ..$objStats }"
      case obj@Defn.Object(_, _, Template(_, Seq(Term.Apply(Term.ApplyType(Ctor.Ref.Name(sc), _), _)), _, _)) if sc == "ExternalComponentWithAttributes" =>
        val objStats = createExternalBody(obj) ++ obj.templ.stats.getOrElse(Nil)
        q"object ${obj.name} extends ${Ctor.Ref.Name(sc)} { ..$objStats }"
      case _ =>
        println(defn.structure)
        abort(s"@react must annotate a class that extends Component ${defn.structure}")
    }
  }
}
