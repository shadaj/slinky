package slinky.sbtplugin.component

import slinky.sbtplugin.hasReactAnnotation
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
    println(s"${cls.name.toString} /// State Exists: ${stateDefn.nonEmpty}")

    val companion = Term.Name(cls.name.value)
    val clazz = Type.Name(cls.name.value)
    val imports = createImports(cls, companion)

    val definitionClass = q"type Def = $clazz"
    val definitionType = Type.Select(Term.Name(companion.value), Type.Name("Definition"))

    val baseClassTerm = baseClass match {
      case Type.Name(name) => Term.Name(name)
      case Type.Select(qual: Term, Type.Name(name)) => Term.Select(qual, Term.Name(name))  
      case Type.Select(Type.Name(qual), Type.Name(name)) => Term.Select(Term.Name(qual), Term.Name(name))
      case _ => throw new MessageOnlyException(s"Unsupported base class type: $baseClass")
    }
    val wrapperType = Type.Select(baseClassTerm, Type.Name("Wrapper"))

    val existingCompanion = parentStats.collectFirst {
      case obj @ Defn.Object(_, name, _) if name.value == cls.name.value => obj 
    }

    val componentClass = q"""
      class ${cls.name}(jsProps: _root_.scala.scalajs.js.Object) extends $definitionType(jsProps) {
        ..$imports
        ..${stats.filterNot(s =>
          s == propsDefn || stateDefn.contains(s) || snapshotDefn.contains(s)
        )}
      }
    """

    val companionObject = existingCompanion match {
      case Some(obj @ Defn.Object(mods, name, template)) =>
        val newStats = (
          List(propsDefn) ++
          stateDefn.toList ++
          snapshotDefn.toList ++
          List(definitionClass) ++
          applyMethods.toList
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

  def processChildrenParam(param: Term.Param): Term.Param =
    param.decltpe match {
      case Some(_: Type.Name) if param.decltpe.exists(_.toString == "ReactElement") =>
        param.copy(
          decltpe = Some(t"_root_.slinky.core.facade.ReactElement")
        )
      case Some(Type.Apply(Type.Name("Seq" | "List"), _)) if param.decltpe.exists(_.toString.contains("ReactElement")) =>
        param.copy(
          decltpe = Some(t"_root_.slinky.core.facade.ReactElement_*")
        )
      case Some(tpe) if param.mods.exists(_.isInstanceOf[Mod.VarParam]) =>
        param
      case _ => param
    }

  def createTypeParams(tparams: Seq[Type.Param]): Seq[Type.Param] =
    tparams.map { tparam =>
      tparam.copy(
        cbounds = tparam.cbounds :+ t"scala.AnyRef"
      )
    }

  def extractPropsAndApplyMethods(className: String, stats: Seq[Stat]): (Stat, Seq[Defn.Def]) =
    stats.flatMap {
      case defn: Defn.Type if defn.name.value == "Props" =>
        Some((defn, Nil))

      case defn @ q"case class Props(...$paramss)" =>
        val applyMethods = createApplyMethods(List(), paramss.map(_.map(_.asInstanceOf[Term.Param])).toList)
        Some((defn, applyMethods))

      case defn @ q"case class Props[..$tparams](...$paramss)" =>
        val applyMethods = createApplyMethods(tparams.toList, paramss.map(_.map(_.asInstanceOf[Term.Param])).toList)
        Some((defn, applyMethods))

      case other =>
        None

    }.headOption.getOrElse {
      throw new MessageOnlyException("No props")
    }

  def createApplyMethods(tparams: List[Type.Param], paramss: List[List[Term.Param]]): Seq[Defn.Def] = {
    val childrenParam = paramss.flatten.find(_.name.value == "children")

    val paramssWithoutChildren = paramss
      .map(_.filterNot(childrenParam.contains))
      .filterNot(_.isEmpty)

    childrenParam match {
      case Some(children) if paramssWithoutChildren.isEmpty =>
        val processedChildren = processChildrenParam(children)
        val typeParamClause = Type.ParamClause(tparams)
        val typeArgClause = Type.ArgClause(tparams.map(p => Type.Name(p.name.value)))

        if (tparams.nonEmpty)
          Seq(q"""
            def apply[..$typeParamClause]($processedChildren):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.apply(Props.apply[..$typeArgClause](children: _*))
            }
          """)
        else
          Seq(q"""
            def apply($processedChildren):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.apply(Props.apply(children: _*))
            }
          """)

      case Some(children) =>
        val processedChildren = processChildrenParam(children)
        val typeParamClause = Type.ParamClause(tparams)
        val termParamClauses = paramssWithoutChildren.map(params => Term.ParamClause(params))
        val typeArgClause = Type.ArgClause(tparams.map(p => Type.Name(p.name.value)))

        val allParams = paramssWithoutChildren.flatten
        val regularArgs = allParams.map(p => Term.Name(p.name.value))

        val childrenArg = if (children.mods.exists(_.isInstanceOf[Mod.VarParam])) {
          q"children: _*"
        } else {
          q"children"
        }

        if (tparams.nonEmpty)
          Seq(q"""
            def apply[..$typeParamClause](...$termParamClauses)($processedChildren):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.apply(Props.apply[..$typeArgClause](..$regularArgs, $childrenArg))
            }
          """)
        else
          Seq(q"""
            def apply(...$termParamClauses)($processedChildren):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.apply(Props.apply(..$regularArgs, $childrenArg))
            }
          """)

      case None =>
        val typeParamClause = Type.ParamClause(tparams)
        val termParamClauses = paramssWithoutChildren.map(params => Term.ParamClause(params))
        val typeArgClause = Type.ArgClause(tparams.map(p => Type.Name(p.name.value)))

        val args = paramss.flatten.map { p =>
          val name = Term.Name(p.name.value)
          if (p.mods.exists(_.isInstanceOf[Mod.VarParam])) {
            q"$name: _*"
          } else {
            name
          }
        }

        if (tparams.nonEmpty)
          Seq(q"""
            def apply[..$typeParamClause](...$termParamClauses):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.apply(Props.apply[..$typeArgClause](..$args))
            }
          """)
        else
          Seq(q"""
            def apply(...$termParamClauses):
              _root_.slinky.core.KeyAndRefAddingStage[Def] = {
              this.apply(Props.apply(..$args))
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
}