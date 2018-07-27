package me.shadaj.slinky.core

import org.jetbrains.plugins.scala.lang.psi.types.ScParameterizedType
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.{SyntheticMembersInjector, TypeDefinitionMembers}
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef._
import org.jetbrains.plugins.scala.lang.psi.api.statements.ScTypeAliasDefinition

class SlinkyInjector extends SyntheticMembersInjector {
  sealed trait InjectType
  case object Function extends InjectType
  case object Type extends InjectType
  case object Member extends InjectType

  override def needsCompanionObject(source: ScTypeDefinition): Boolean = {
    source.findAnnotationNoAliases("slinky.core.annotations.react") != null
  }

  def createComponentBody(cls: ScTypeDefinition): Seq[(String, InjectType)] = {
    val types = TypeDefinitionMembers.getTypes(cls)
    val (propsDefinition, applyMethods) = types.get("Props").map { buf =>
      buf.head._1 match {
        case alias: ScTypeAliasDefinition =>
          ((alias.getText, Member), Seq.empty[(String, InjectType)])
        case propsCls: ScClass if propsCls.isCase =>
          ((propsCls.getText, Type), {
            val paramList = propsCls.constructor.get.parameterList
            Seq(
              s"def apply${paramList.getText}: slinky.core.KeyAndRefAddingStage[${cls.getQualifiedName}] = ???" -> Function
            )
          })
      }
    }.getOrElse(("", Type), Seq.empty)

    val stateDefinition: Option[(String, InjectType)] = types.get("State").map { buf =>
      buf.head._1 match {
        case alias: ScTypeAliasDefinition =>
          (alias.getText, Member)
        case propsCls: ScClass if propsCls.isCase =>
          (propsCls.getText, Type)
      }
    }

    val snapshotDefinition: Option[(String, InjectType)] = types.get("Snapshot").map { buf =>
      buf.head._1 match {
        case alias: ScTypeAliasDefinition =>
          (alias.getText, Member)
        case propsCls: ScClass if propsCls.isCase =>
          (propsCls.getText, Type)
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
    }.find(_.name == "Props").map {
      case propsCls: ScClass if propsCls.isCase =>
        val (element, ref) = elementAndRefType(cls)
        val paramList = propsCls.constructor.get.parameterList

        if (paramList.params.forall(_.isDefaultParam)) {
          Seq(
            s"def apply${paramList.getText}: _root_.slinky.core.BuildingComponent[$element, $ref] = ???" -> Function,
            s"def apply(mod: _root_.slinky.core.AttrPair[$element], tagMods: _root_.slinky.core.AttrPair[$element]*): _root_.slinky.core.BuildingComponent[$element, $ref] = ???" -> Function,
            s"def withKey(key: String): _root_.slinky.core.BuildingComponent[$element, $ref] = ???" -> Function,
            s"def withRef(ref: $ref => Unit): _root_.slinky.core.BuildingComponent[$element, $ref] = ???" -> Function,
            s"def withRef(ref: _root_.slinky.core.facade.ReactRef[$ref]): _root_.slinky.core.BuildingComponent[$element, $ref] = ???" -> Function,
            s"def apply(children: _root_.slinky.core.facade.ReactElement*): _root_.slinky.core.BuildingComponent[$element, $ref] = ???" -> Function
          )
        } else {
          Seq(
            s"def apply${paramList.getText}: slinky.core.BuildingComponent[$element, $ref] = ???" -> Function
          )
        }

      case _ => Seq.empty
    }.getOrElse(Seq.empty)

    applyMethods
  }

  def isSlinky(tpe: ScTypeDefinition): Boolean = {
    tpe.findAnnotationNoAliases("slinky.core.annotations.react") != null
  }

  def isExternal(tpe: ScTypeDefinition): Boolean = {
    isSlinky(tpe) && tpe.extendsBlock.supers.map(_.getQualifiedName).exists { parent =>
      parent == "slinky.core.ExternalComponent" ||
        parent == "slinky.core.ExternalComponentWithAttributes" ||
        parent == "slinky.core.ExternalComponentWithRefType" ||
        parent == "slinky.core.ExternalComponentWithAttributesWithRefType"
    }
  }

  def isComponent(tpe: ScTypeDefinition): Boolean = {
    isSlinky(tpe) && tpe.extendsBlock.supers.map(_.getQualifiedName).exists { parent =>
      parent == "slinky.core.Component" || parent == "slinky.core.StatelessComponent"
    }
  }

  override def injectFunctions(source: ScTypeDefinition): Seq[String] = {
    (source match {
      case obj: ScObject if isExternal(obj) =>
        createExternalBody(obj)

      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if isComponent(clazz) =>
            createComponentBody(clazz)
          case _ => Seq.empty
        }

      case _ => Seq.empty
    }).filter(_._2 == Function).map(_._1)
  }

  override def injectInners(source: ScTypeDefinition): Seq[String] = {
    (source match {
      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if isComponent(clazz) =>
            createComponentBody(clazz)
          case _ => Seq.empty
        }

      case _ => Seq.empty
    }).filter(_._2 == Type).map(_._1)
  }

  override def injectMembers(source: ScTypeDefinition): Seq[String] = {
    (source match {
      case obj: ScObject if isExternal(obj) =>
        createExternalBody(obj)

      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if isComponent(clazz) =>
            createComponentBody(clazz)
          case _ => Seq.empty
        }

      case _ => Seq.empty
    }).filter(_._2 == Member).map(_._1)
  }

  override def injectSupers(source: ScTypeDefinition): Seq[String] = {
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
}
