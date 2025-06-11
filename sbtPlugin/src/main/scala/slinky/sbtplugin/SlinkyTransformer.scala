package slinky.sbtplugin

import scala.meta._
import slinky.sbtplugin.component._
import scala.meta.tokens.Token._
import scala.collection.mutable

class SlinkyTransformer(scalaVersion: String) {

  def transform(tree: Tree): Tree = tree match {
    case Source(stats) => Source(transformStatements(stats))
    case Template(early, inits, self, stats) =>
      val transformedStats = stats match {
        case Term.Block(blockStats) => transformStatements(blockStats)
        case stats: List[Stat] => transformStatements(stats)
        case _ => List.empty[Stat]
      }
      Template(early, inits, self, transformedStats)
    case Pkg(ref, stats) => Pkg(ref, transformStatements(stats))
    case other => other
  }

  private def transformStatements(outerStats: List[Stat]): List[Stat] = {

    def processStats(stats: List[Stat]): List[Stat] = {
      val newCompanionObjects = mutable.Set[(Type.Name, Defn.Object)]()

      def transformReactObject(obj: Defn.Object): Defn.Object = {
        val transformed = if (ExternalComponent.matches(obj)) {
          ExternalComponent.transform(obj, scalaVersion)
        } else if (FunctionalComponent.matches(obj)) {
          FunctionalComponent.transform(obj)
        } else obj

        val Template(tEarly, tInits, tSelf, tStats) = transformed.templ
        transformed.copy(templ = Template(tEarly, tInits, tSelf, processInnerStats(tStats)))
      }

      def processInnerStats(innerStats: List[Stat]): List[Stat] = {
        val processedInner = processStats(innerStats)
        processedInner.flatMap {
          case obj @ Defn.Object(_, _, _) if hasReactAnnotation(obj) &&
              (ExternalComponent.matches(obj) || FunctionalComponent.matches(obj)) =>
            List(transformReactObject(obj))
          case obj @ Defn.Object(mods, name, template @ Template(early, inits, self, stats)) =>
            List(Defn.Object(mods, name, Template(early, inits, self, processStats(stats))))
          case other => List(other)
        }
      }

      val processedStats = stats.map {
        case Pkg(ref, pkgStats) =>
          Pkg(ref, transformStatements(pkgStats))

        case cls @ Defn.Class(mods, name, tparams, ctor, template @ Template(early, inits, self, innerStats)) =>
          if (ClassComponent.matches(cls) && hasReactAnnotation(cls)) {
            val pair = ClassComponent.transform(cls, stats)
            newCompanionObjects += ((name, pair._2))
            pair._1
          } else
            Defn.Class(mods, name, tparams, ctor, Template(early, inits, self, processInnerStats(innerStats)))

        case obj @ Defn.Object(_, _, _) if hasReactAnnotation(obj) &&
            (ExternalComponent.matches(obj) || FunctionalComponent.matches(obj)) =>
          transformReactObject(obj)

        case obj @ Defn.Object(mods, name, template @ Template(early, inits, self, innerStats)) =>
          Defn.Object(mods, name, Template(early, inits, self, processInnerStats(innerStats)))

        case other => other
      }

      val filteredStats = processedStats.filterNot {
        case obj: Defn.Object => newCompanionObjects.exists(_._1.value == obj.name.value)
        case _ => false
      }

      filteredStats ++ newCompanionObjects.map(_._2).toList
    }

    processStats(outerStats)
  }
}