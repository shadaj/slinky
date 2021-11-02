package slinky.core

import slinky.core.facade.{ReactElement, ReactRaw, ReactRef}
import slinky.readwrite.Writer

import scala.scalajs.js

final class BuildingComponent[E, R <: js.Object](private val args: js.Array[js.Any]) extends AnyVal {
  def apply(mods: TagMod[E]*): BuildingComponent[E, R] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    mods.foreach {
      case a: AttrPair[_] =>
        args(1).asInstanceOf[js.Dictionary[js.Any]](a.name) = a.value
      case o: OptionalAttrPair[_] =>
        if (o.value.isDefined) args(1).asInstanceOf[js.Dictionary[js.Any]](o.name) = o.value.get
      case r =>
        args.push(r.asInstanceOf[ReactElementMod])
    }

    this
  }

  def withKey(newKey: String): BuildingComponent[E, R] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    args(1).asInstanceOf[js.Dictionary[js.Any]]("key") = newKey
    this
  }

  // Using union type instead of overloading, as the latter manages to crash scala 3 compiler
  def withRef(newRef: (R => Unit) | ReactRef[R]): BuildingComponent[E, R] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    newRef match {
      case f: (R => Unit) =>
        args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = (f: js.Function1[R, Unit])
      case _ =>  
        args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = newRef.asInstanceOf[ReactRef[R]]
    }
    this
  }
}

object BuildingComponent {
  @inline implicit def make[E, R <: js.Object](comp: BuildingComponent[E, R]): ReactElement = {
    if (comp.args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    val ret = ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, comp.args)
      .asInstanceOf[ReactElement]

    comp.args(0) = null

    ret
  }
}

// TODO: providers need to be by-name because implicit will reference the type inside the object being initialized
//       maybe it could be fixed with a better instance macro for those
abstract class ExternalComponentWithAttributesWithRefType[E <: TagElement, R <: js.Object](
  implicit pw: => ExternalPropsWriterProvider
) {
  type Props
  type Element = E
  type RefType = R

  private[this] val writer = pw.asInstanceOf[Writer[Props]]

  val component: String | js.Object

  def apply(p: Props): BuildingComponent[E, R] =
    // no need to take key or ref here because those can be passed in through attributes
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], writer.write(p)))
}

abstract class ExternalComponentWithAttributes[E <: TagElement](implicit pw: => ExternalPropsWriterProvider)
    extends ExternalComponentWithAttributesWithRefType[E, js.Object]()(pw)

abstract class ExternalComponentWithRefType[R <: js.Object](implicit pw: => ExternalPropsWriterProvider)
    extends ExternalComponentWithAttributesWithRefType[Nothing, R]()(pw)

abstract class ExternalComponent(implicit pw: => ExternalPropsWriterProvider)
    extends ExternalComponentWithAttributes[Nothing]()(pw)

abstract class ExternalComponentNoPropsWithAttributesWithRefType[E <: TagElement, R <: js.Object] {
  val component: String | js.Object

  def apply(mods: TagMod[E]*): BuildingComponent[E, R] =
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], js.Dictionary.empty)).apply(mods: _*)

  def withKey(key: String): BuildingComponent[E, R] =
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], js.Dictionary.empty)).withKey(key)
  def withRef(ref: (R => Unit) | ReactRef[R]): BuildingComponent[E, R] =
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], js.Dictionary.empty)).withRef(ref)
}

abstract class ExternalComponentNoPropsWithAttributes[T <: TagElement]
    extends ExternalComponentNoPropsWithAttributesWithRefType[T, js.Object]

abstract class ExternalComponentNoPropsWithRefType[R <: js.Object]
    extends ExternalComponentNoPropsWithAttributesWithRefType[Nothing, R]

abstract class ExternalComponentNoProps extends ExternalComponentNoPropsWithAttributes[Nothing]
