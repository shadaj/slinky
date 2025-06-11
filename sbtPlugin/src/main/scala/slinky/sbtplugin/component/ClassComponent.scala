package slinky.sbtplugin.component

import slinky.sbtplugin.hasReactAnnotation
import slinky.sbtplugin.createChildrenArgFromType
import scala.annotation.nowarn
import scala.meta._

@nowarn
object ClassComponent {

  def matches(cls: Defn.Class): Boolean =
    cls.templ.inits.exists { i =>
      i.tpe.syntax.equals("Component") || i.tpe.syntax.equals("StatelessComponent")
    }

  def transform(cls: Defn.Class, parentStats: List[Stat]): (Defn.Class, Defn.Object) = {
    val Init(baseClass: Type, _, _) = cls.templ.inits.head
    val stats = cls.templ.stats

    val (propsDefn, applyMethods) = extractPropsAndApplyMethods(cls.name.value, stats)
    val stateDefn = extractStateDefinition(stats)
    val snapshotDefn = extractSnapshotDefinition(stats)

    val companion = Term.Name(cls.name.value)
    val clazz = Type.Name(cls.name.value)
    val imports = createImports(cls, companion)

    val definitionClass = q"type Def = $clazz"
    val definitionType = Type.Select(Term.Name(companion.value), Type.Name("Definition"))

    val baseClassTerm = baseClass match {
      case Type.Name(name) => Term.Name(name)
      case Type.Select(qual: Term, Type.Name(name)) => Term.Select(qual, Term.Name(name))
      case Type.Select(Type.Name(qual), Type.Name(name)) => Term.Select(Term.Name(qual), Term.Name(name))
      case _ => throw new Exception(s"Unsupported base class type: $baseClass")
    }
    val wrapperType = Type.Select(baseClassTerm, Type.Name("Wrapper"))

    val existingCompanion = parentStats.collectFirst {
      case obj @ Defn.Object(_, name, _) if name.value == cls.name.value => obj
    }

    val componentClass = q"""
      class ${cls.name}(jsProps: _root_.scala.scalajs.js.Object) extends $definitionType(jsProps) {
        ..$imports
        ..${stats.filterNot(s => isPropsDefinition(s) || stateDefn.contains(s) || snapshotDefn.contains(s))}
      }
    """

    val componentDef = q"""
      val component = ${companion}
    """

    val companionObject = existingCompanion match {
      case Some(obj @ Defn.Object(mods, name, template)) =>
        val newStats = (
            stateDefn.toList ++
            snapshotDefn.toList ++
            List(definitionClass) ++
            applyMethods.toList ++
            List(componentDef)
          )

        Defn.Object(mods, name, Template(
          early = Nil,
          inits = List(Init(wrapperType, Name(""), Nil)),
          self = template.self,
          stats = template.stats ++ newStats
        )
        )

      case None =>
        q"""
        object $companion extends ${Init(wrapperType, Name(""), Nil)} {
          $propsDefn
          ..${stateDefn.toList}
          ..${snapshotDefn.toList}
          $definitionClass
          ..${applyMethods.toList}
          $componentDef
        }
        """
    }

    (componentClass, companionObject)
  }

  def createImports(cls: Defn.Class, companion: Term.Name): List[Stat] = {
    List(
      q"""import $companion.{Props, State, Snapshot}""",
      q"""
      if (false) {
        locally {
          null.asInstanceOf[Props]
          null.asInstanceOf[State]
          null.asInstanceOf[Snapshot]
        }
      }
      """
    )
  }

  def parentsContainsType(parents: Seq[Type], tpe: Type): Boolean =
    parents.exists(p => p.structure == tpe.structure)

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

  def createTypeParams(tparams: Seq[Type.Param]): Seq[Type.Param] =
    tparams.map { tparam =>
      tparam.copy(
        cbounds = tparam.cbounds :+ t"scala.AnyRef"
      )
    }

  def normalizeChildrenParam(param: Term.Param): Term.Param =
    if (param.name.value == "children") {
      param.copy(
        decltpe = Some(t"Seq[_root_.slinky.core.facade.ReactElement]")
      )
    } else {
      param
    }

  def isPropsDefinition(stat: Stat): Boolean = stat match {
    case defn: Defn.Type if defn.name.value == "Props" => true
    case q"case class Props[..$_](...$_)" => true
    case q"case class Props(...$_)" => true
    case _ => false
  }

  def extractPropsAndApplyMethods(className: String, stats: Seq[Stat]): (Stat, Seq[Defn.Def]) =
    stats.flatMap {
      case defn: Defn.Type if defn.name.value == "Props" =>
        Some((defn, Nil))

      case defn @ q"case class Props(...$paramss)" =>
        val normalizedParamss = paramss.map(_.map(normalizeChildrenParam))
        val normalizedDefn = q"case class Props(...$normalizedParamss)"
        val applyMethods = createApplyMethods(List(), normalizedParamss.toList, className)
        Some((normalizedDefn, applyMethods))

      case defn @ q"case class Props[..$tparams](...$paramss)" =>
        val normalizedParamss = paramss.map(_.map(normalizeChildrenParam))
        val normalizedDefn = q"case class Props[..$tparams](...$normalizedParamss)"
        val applyMethods = createApplyMethods(tparams.toList, normalizedParamss.toList, className)
        Some((normalizedDefn, applyMethods))

      case other =>
        None

    }.headOption.getOrElse {
      throw new Exception(s"Props missing for React component: $className")
    }

  def createApplyMethods(tparams: List[Type.Param], paramss: List[List[Term.Param]], className: String = ""): Seq[Defn.Def] = {
    val childrenParam = paramss.flatten.find(_.name.value == "children")

    childrenParam match {
      case Some(children) =>
        val processedChildren = children.copy(
          decltpe = Some(t"_root_.slinky.core.facade.ReactElement*"),
          default = None
        )

        val typeParamClause = Type.ParamClause(tparams)
        val typeArgClause = Type.ArgClause(tparams.map(p => Type.Name(p.name.value)))

        val paramssWithoutChildren = paramss
          .map(_.filterNot(childrenParam.contains))
          .filterNot(_.isEmpty)

        val termParamClauses = paramssWithoutChildren.map(params => Term.ParamClause(params))

        // Create arguments preserving original order, adding .toSeq for children parameter
        val allArgsInOrder = paramss.flatten.map { param =>
          if (param.name.value == "children") {
            q"${Term.Name(param.name.value)}.toSeq"
          } else {
            Term.Name(param.name.value)
          }
        }

        if (tparams.nonEmpty)
          Seq(q"""
            def apply[..$typeParamClause](...$termParamClauses)($processedChildren):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.applyProps(Props.apply[..$typeArgClause](..$allArgsInOrder))
            }
          """)
        else
          Seq(q"""
            def apply(...$termParamClauses)($processedChildren):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.applyProps(Props.apply(..$allArgsInOrder))
            }
          """)

      case None =>
        val typeParamClause = Type.ParamClause(tparams)
        val termParamClauses = paramss.map(params => Term.ParamClause(params))
        val typeArgClause = Type.ArgClause(tparams.map(p => Type.Name(p.name.value)))

        val args = paramss.flatten.map(p => Term.Name(p.name.value))

        if (tparams.nonEmpty)
          Seq(q"""
            def apply[..$typeParamClause](...$termParamClauses):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.applyProps(Props.apply[..$typeArgClause](..$args))
            }
          """)
        else
          Seq(q"""
            def apply(...$termParamClauses):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.applyProps(Props.apply(..$args))
            }
          """)
    }
  }

  def extractStateDefinition(stats: Seq[Stat]): Option[Stat] =
    stats.collectFirst {
      case defn: Defn.Type if defn.name.value == "State" => defn
      case defn @ q"case class State[..$_](...$_)" => defn
      case defn @ q"case class State[..$_](...$_) extends $_ { ..$_ }" => defn
    }

  def extractSnapshotDefinition(stats: Seq[Stat]): Option[Stat] =
    stats.collectFirst {
      case defn: Defn.Type if defn.name.value == "Snapshot" => defn
      case defn @ q"case class Snapshot[..$_](...$_)" => defn
      case defn @ q"case class Snapshot[..$_](...$_) extends $_ { ..$_ }" => defn
    }

  def addNextExportMethods(className: Type.Name): Seq[Defn.Def] = {
    Seq(
      q"def component: _root_.slinky.core.facade.ReactElement = ${Term.Name(className.value)}.component",
      q"def component(): _root_.slinky.core.ReactComponentClass[_] = ${Term.Name(className.value)}.component"
    )
  }
}