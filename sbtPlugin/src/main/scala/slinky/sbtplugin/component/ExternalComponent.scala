package slinky.sbtplugin.component

import scala.annotation.nowarn
import scala.meta._
import slinky.sbtplugin.hasReactAnnotation

@nowarn
object ExternalComponent {

  def matches(obj: Defn.Object): Boolean =
    obj.templ.inits.exists { i =>
      val parentName = i.tpe.syntax
      parentName.equals("ExternalComponent") ||
      parentName.startsWith("ExternalComponentWithAttributes") ||
      parentName.startsWith("ExternalComponentWithAttributesWithRefType") ||
      parentName.startsWith("ExternalComponentWithRefType")
    }


  def transform(obj: Defn.Object): Defn.Object = {
    val (baseInit, stats) = obj match {
      case q"@react object $_ extends $base with ..$mixins { $self => ..$stats }" => (base, stats)
      case q"@react object $_ extends $base { $self => ..$stats }" => (base, stats)
    }

    val (elementType, refType) = determineTypes(baseInit)
    val methods = createExternalMethods(stats, elementType, refType)

    q"""
      object ${obj.name} extends _root_.slinky.core.ExternalComponentWithAttributesWithRefType[$elementType, $refType] {
        ..$stats
        ..${methods.toList}
      }
    """
  }

  private def determineTypes(baseInit: Init): (Type, Type) =
    baseInit match {
      case Init(Type.Name("ExternalComponent"), _, _) =>
        (t"Nothing", t"js.Object")

      case Init(Type.Name("ExternalComponentWithAttributes"), _, List(List(Type.Select(qual, name)))) =>
        (Type.Select(qual, name), t"js.Object")

      case Init(Type.Name("ExternalComponentWithAttributes"), _, List(List(tpe Type))) =>
        (tpe, t"js.Object")

      case Init(Type.Apply(Type.Name("ExternalComponentWithAttributes"), List(Type.Singleton(Term.Select(qual, tag)))), _, _) =>
        (Type.Singleton(Term.Select(qual, tag)), t"js.Object")

      case Init(Type.Apply(Type.Name("ExternalComponentWithRefType"), List(tpe)), _, _) =>
        (t"Nothing", tpe)

      case Init(Type.Name("ExternalComponentWithAttributesWithRefType"), _, List(List(Type.Select(qual1, name1), Type.Select(qual2, name2)))) =>
        (Type.Select(qual1, name1), Type.Select(qual2, name2))

      case Init(Type.Name("ExternalComponentWithAttributesWithRefType"), _, List(List(elemType: Type, refType: Type))) =>
        (elemType, refType)

      case x @ _ =>
        throw new MessageOnlyException("Invalid external component base class")
    }

  private def createExternalMethods(
    stats: Seq[Stat],
    elementType: Type,
    refType: Type
  ): Seq[Defn.Def] =
    stats.flatMap {
      case q"case class Props[..$tparams](...$paramss) extends ..$_" =>
        val applyValues = paramss.map(_.map(p => Term.Name(p.name.value)))

        val basicApply = q"""
          def apply[..${tparams}](...$paramss): _root_.slinky.core.BuildingComponent[$elementType, $refType] =
            this.apply(Props.apply(..${applyValues.flatten}))
              .asInstanceOf[_root_.slinky.core.BuildingComponent[$elementType, $refType]]
        """

        if (paramss.flatten.forall(_.default.isDefined) || paramss.flatten.isEmpty) {
          Seq(
            basicApply,
            q"""
              def apply(mods: _root_.slinky.core.TagMod[$elementType]*): 
                _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                  js.Array(
                    component.asInstanceOf[js.Any],
                    js.Dictionary.empty
                  )
                ).apply(mods: _*)
              }
            """,
            q"""
              def withKey(key: String): _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                  js.Array(
                    component.asInstanceOf[js.Any],
                    js.Dictionary.empty
                  )
                ).withKey(key)
              }
            """,
            q"""
              def withRef(ref: $refType => Unit): _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                  js.Array(
                    component.asInstanceOf[js.Any],
                    js.Dictionary.empty
                  )
                ).withRef(ref)
              }
            """,
            q"""
              def withRef(ref: _root_.slinky.core.facade.ReactRef[$refType]):
                _root_.slinky.core.BuildingComponent[$elementType, $refType] = {
                new _root_.slinky.core.BuildingComponent[$elementType, $refType](
                  js.Array(
                    component.asInstanceOf[js.Any],
                    js.Dictionary.empty
                  )
                ).withRef(ref)
              }
            """
          )
        } else {
          Seq(basicApply)
        }

      case _ => Nil
    }
  }