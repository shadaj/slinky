package slinky.sbtplugin

import scala.meta._
import slinky.sbtplugin.component._
import scala.meta.tokens.Token._
import scala.collection.mutable

object SlinkyTransformer {
  val excludedImports = List(
    "slinky.core.annotations.react",
    "slinky.core.Component",
    "slinky.core.ExternalComponent",
    "slinky.core.ExternalComponentWithAttributes",
    "slinky.core.ExternalComponentWithAttributesWithRefType",
    "slinky.core.ExternalComponentWithRefType"
  )

  def transform(tree: Tree): String = {
    val transformedTree = transformTree(tree)
    excludedImports.foldLeft(transformedTree.syntax) { (s, i) =>
      s.replace(s"import $i\n", "")
    }
  }

  private def transformTree(tree: Tree): Tree = tree match {
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

      val processedStats = stats.map { stat =>
        stat match {
          case Pkg(ref, stats) => Pkg(ref, transformStatements(stats))

          case cls @ Defn.Class(mods, name, tparams, ctor, template @ Template(early, inits, self, innerStats)) =>
            if (ClassComponent.matches(cls) && hasReactAnnotation(cls)) {
              val pair = ClassComponent.transform(cls, stats)
              newCompanionObjects += ((name, pair._2))
              pair._1
            } else
              Defn.Class(mods, name, tparams, ctor, Template(early, inits, self, processStats(innerStats)))

          case obj @ Defn.Object(mods, name, template @ Template(early, inits, self, innerStats)) =>
            if (ExternalComponent.matches(obj) && hasReactAnnotation(obj))
              ExternalComponent.transform(obj)
            else
            if (FunctionalComponent.matches(obj) && hasReactAnnotation(obj))
              FunctionalComponent.transform(obj)
            else
              Defn.Object(mods, name, Template(early, inits, self, processStats(innerStats)))

          case other => other
        }
      }

      val filteredStats = processedStats.filterNot {
        case obj: Defn.Object => {
          newCompanionObjects.exists(_._1.value == obj.name.value)
        }
        case _ => false
      }

      filteredStats ++ newCompanionObjects.map(_._2).toList
    }

    processStats(outerStats)
  }
}