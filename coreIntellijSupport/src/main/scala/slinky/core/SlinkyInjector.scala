package slinky.core

import com.intellij.psi.{PsiClassType, PsiType}
import com.intellij.psi.impl.source.{PsiClassReferenceType, PsiImmediateClassType}
import com.intellij.psi.util.PsiTypesUtil
import org.jetbrains.plugins.scala.extensions.PsiClassExt
import org.jetbrains.plugins.scala.lang.psi.api.statements.{
  ScPatternDefinition,
  ScTypeAliasDefinition,
  ScValueDeclaration
}
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef._
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.{SyntheticMembersInjector, TypeDefinitionMembers}
import org.jetbrains.plugins.scala.lang.psi.types.ScParameterizedType

class SlinkyInjector extends SyntheticMembersInjector {
  sealed trait InjectType
  case object Function extends InjectType
  case object Type     extends InjectType
  case object Member   extends InjectType

  override def needsCompanionObject(source: ScTypeDefinition): Boolean =
    source.findAnnotationNoAliases("slinky.core.annotations.react") != null

  def createComponentBody(cls: ScTypeDefinition): Seq[(String, InjectType)] = {
    val types = TypeDefinitionMembers.getTypes(cls)
    val (propsDefinition, applyMethods) = types
      .forName("Props")
      .iterator
      .toSeq
      .headOption
      .flatMap { elm =>
        elm.namedElement match {
          case alias: ScTypeAliasDefinition =>
            Some(((alias.getText, Member), Seq.empty[(String, InjectType)]))
          case propsCls: ScClass if propsCls.isCase =>
            Some(((propsCls.getText, Type), {
              val paramList        = propsCls.constructor.get.parameterList
              val caseClassparamss = paramList.params
              val childrenParam    = caseClassparamss.find(_.name == "children")

              val paramssWithoutChildren = caseClassparamss.filterNot(childrenParam.contains)

              if (childrenParam.isDefined) {
                if (paramssWithoutChildren.isEmpty) {
                  Seq(
                    s"def apply(${childrenParam.get.getText}): slinky.core.KeyAndRefAddingStage[${cls.getQualifiedName}] = ???" -> Function
                  )
                } else {
                  val children = childrenParam.get.getType() match {
                    case tpe if isChildrenReactElement(tpe) =>
                      s"${childrenParam.get.getName}: slinky.core.facade.ReactElement*"
                    case _ =>
                      childrenParam.get.getText
                  }

                  Seq(
                    s"def apply(${paramssWithoutChildren.map(_.getText).mkString(",")})($children): slinky.core.KeyAndRefAddingStage[${cls.getQualifiedName}] = ???" -> Function
                  )
                }
              } else {
                Seq(
                  s"def apply(${paramssWithoutChildren.map(_.getText).mkString(",")}): slinky.core.KeyAndRefAddingStage[${cls.getQualifiedName}] = ???" -> Function
                )
              }
            }))
          case _ => None
        }
      }
      .getOrElse((("", Type), Seq.empty))

    val stateDefinition: Option[(String, InjectType)] = types.forName("State").iterator.toSeq.headOption.flatMap {
      elm =>
        elm.namedElement match {
          case alias: ScTypeAliasDefinition =>
            Some((alias.getText, Member))
          case propsCls: ScClass if propsCls.isCase =>
            Some((propsCls.getText, Type))
          case _ => None
        }
    }

    val snapshotDefinition: Option[(String, InjectType)] = types.forName("Snapshot").iterator.toSeq.headOption.flatMap {
      elm =>
        elm.namedElement match {
          case alias: ScTypeAliasDefinition =>
            Some((alias.getText, Member))
          case propsCls: ScClass if propsCls.isCase =>
            Some((propsCls.getText, Type))
          case _ => None
        }
    }

    (s"type Def = ${cls.getQualifiedName}", Member) +:
      propsDefinition +:
      stateDefinition.getOrElse(("type State = Unit", Member)) +:
      (snapshotDefinition.toList ++
      applyMethods)
  }

  def elementAndRefType(external: ScTypeDefinition) = {
    val superType = external.extendsBlock.superTypes.head
    val parameters = superType match {
      case pt: ScParameterizedType =>
        pt.typeArguments
      case _ => Seq.empty
    }

    external.extendsBlock.supers.map(s => s.getQualifiedName).head match {
      case "slinky.core.ExternalComponent" =>
        ("Nothing", "scala.scalajs.js.Object")
      case "slinky.core.ExternalComponentWithAttributes" if parameters.size == 1 =>
        (parameters.head.canonicalText, "scala.scalajs.js.Object")
      case "slinky.core.ExternalComponentWithRefType" if parameters.size == 1 =>
        ("Nothing", parameters.head.canonicalText)
      case "slinky.core.ExternalComponentWithAttributesWithRefType" if parameters.size == 2 =>
        (parameters(0).canonicalText, parameters(1).canonicalText)
      case _ => ("Nothing", "scala.scalajs.js.Object")
    }
  }

  def createExternalBody(cls: ScTypeDefinition): Seq[(String, InjectType)] = {
    val applyMethods = cls.extendsBlock.members.collect {
      case td: ScTypeDefinition => td
    }.find(_.name == "Props")
      .map {
        case propsCls: ScClass if propsCls.isCase =>
          val (element, ref) = elementAndRefType(cls)
          val paramList      = propsCls.constructor.get.parameterList

          if (paramList.params.forall(_.isDefaultParam)) {
            Seq(
              s"def apply${paramList.getText}: _root_.slinky.core.BuildingComponent[$element, $ref] = ???"                                                                          -> Function,
              s"def apply(mod: _root_.slinky.core.AttrPair[$element], tagMods: _root_.slinky.core.AttrPair[$element]*): _root_.slinky.core.BuildingComponent[$element, $ref] = ???" -> Function,
              s"def withKey(key: String): _root_.slinky.core.BuildingComponent[$element, $ref] = ???"                                                                               -> Function,
              s"def withRef(ref: $ref => Unit): _root_.slinky.core.BuildingComponent[$element, $ref] = ???"                                                                         -> Function,
              s"def withRef(ref: _root_.slinky.core.facade.ReactRef[$ref]): _root_.slinky.core.BuildingComponent[$element, $ref] = ???"                                             -> Function,
              s"def apply(children: _root_.slinky.core.facade.ReactElement*): _root_.slinky.core.BuildingComponent[$element, $ref] = ???"                                           -> Function
            )
          } else {
            Seq(
              s"def apply${paramList.getText}: slinky.core.BuildingComponent[$element, $ref] = ???" -> Function
            )
          }

        case _ => Seq.empty
      }
      .getOrElse(Seq.empty)

    applyMethods
  }

  def createFunctionalComponentBody(cls: ScTypeDefinition): Seq[(String, InjectType)] = {
    val applyMethods = cls.extendsBlock.members.collect {
      case td: ScClass               => td
      case td: ScTypeAliasDefinition => td
    }.find(_.name == "Props")
      .flatMap { elm =>
        elm match {
          case _: ScTypeAliasDefinition =>
            Some(
              Seq(
                s"def apply(props: ${cls.name}.Props): ${cls.name}.component.Result = ???" -> Function
              )
            )
          case propsCls: ScClass if propsCls.isCase =>
            Some {
              val paramList        = propsCls.constructor.get.parameterList
              val caseClassparamss = paramList.params
              val childrenParam    = caseClassparamss.find(_.name == "children")

              val paramssWithoutChildren = caseClassparamss.filterNot(childrenParam.contains)

              if (childrenParam.isDefined) {
                if (paramssWithoutChildren.isEmpty) {
                  Seq(
                    s"def apply(${childrenParam.get.getText}): ${cls.name}.component.Result = ???" -> Function
                  )
                } else {
                  Seq(
                    s"def apply(${paramssWithoutChildren.map(_.getText).mkString(",")})(${childrenParam.get.getText}): ${cls.name}.component.Result = ???" -> Function
                  )
                }
              } else {
                Seq(
                  s"def apply(${paramssWithoutChildren.map(_.getText).mkString(",")}): ${cls.name}.component.Result = ???" -> Function
                )
              }
            }
          case _ => None
        }
      }
      .getOrElse(Seq.empty)

    applyMethods
  }

  def isChildrenReactElement(psiType: PsiType): Boolean =
    PsiTypesUtil.getPsiClass(psiType).qualifiedName match {
      case "scala.collection.immutable.Seq" | "scala.collection.immutable.List" =>
        psiType match {
          case pt: PsiClassType =>
            pt.getParameters.length == 1 &&
              pt.getParameters()(0).getCanonicalText() == "slinky.core.facade.ReactElement"
          case _ =>
            false
        }
      case "slinky.core.facade.ReactElement" =>
        psiType.getCanonicalText.endsWith("*")

      case _ =>
        false
    }

  def isSlinky(tpe: ScTypeDefinition): Boolean =
    tpe.findAnnotationNoAliases("slinky.core.annotations.react") != null

  def isExternal(tpe: ScTypeDefinition): Boolean =
    isSlinky(tpe) && tpe.extendsBlock.supers.map(_.getQualifiedName).exists { parent =>
      parent == "slinky.core.ExternalComponent" ||
      parent == "slinky.core.ExternalComponentWithAttributes" ||
      parent == "slinky.core.ExternalComponentWithRefType" ||
      parent == "slinky.core.ExternalComponentWithAttributesWithRefType"
    }

  def isComponent(tpe: ScTypeDefinition): Boolean =
    isSlinky(tpe) && tpe.extendsBlock.supers.map(_.getQualifiedName).exists { parent =>
      parent == "slinky.core.Component" || parent == "slinky.core.StatelessComponent"
    }

  def isFunctionalComponent(tpe: ScTypeDefinition): Boolean =
    isSlinky(tpe) && tpe.extendsBlock.members.exists {
      case td: ScValueDeclaration if td.declaredNames == Seq("component") => true
      case pd: ScPatternDefinition =>
        pd.bindings.exists(_.getName == "component")
      case _ => false
    }

  override def injectFunctions(source: ScTypeDefinition): Seq[String] =
    (source match {
      case obj: ScObject if isExternal(obj) =>
        createExternalBody(obj)

      case obj: ScObject if isFunctionalComponent(obj) =>
        createFunctionalComponentBody(obj)

      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if isComponent(clazz) =>
            createComponentBody(clazz)
          case _ => Seq.empty
        }

      case _ => Seq.empty
    }).filter(_._2 == Function).map(_._1)

  override def injectInners(source: ScTypeDefinition): Seq[String] =
    (source match {
      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if isComponent(clazz) =>
            createComponentBody(clazz)
          case _ => Seq.empty
        }

      case _ => Seq.empty
    }).filter(_._2 == Type).map(_._1)

  override def injectMembers(source: ScTypeDefinition): Seq[String] =
    (source match {
      case obj: ScObject if isExternal(obj) =>
        createExternalBody(obj)

      case obj: ScObject if isFunctionalComponent(obj) =>
        createFunctionalComponentBody(obj)

      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if isComponent(clazz) =>
            createComponentBody(clazz)
          case _ => Seq.empty
        }

      case _ => Seq.empty
    }).filter(_._2 == Member).map(_._1)

  override def injectSupers(source: ScTypeDefinition): Seq[String] =
    source match {
      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if isComponent(clazz) =>
            Seq("slinky.core.ComponentWrapper")
          case _ => Seq.empty
        }

      case _ => Seq.empty
    }
}
