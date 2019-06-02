package slinky.core

import slinky.core.facade.{React, ReactRaw, ReactElement, ReactRef}
import slinky.readwrite.Writer

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.|
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

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

  def withRef(newRef: R => Unit): BuildingComponent[E, R] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = (newRef: js.Function1[R, Unit])
    this
  }

  def withRef(ref: ReactRef[R]): BuildingComponent[E, R] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = ref
    this
  }
}

object BuildingComponent {
  @inline implicit def make[E, R <: js.Object](comp: BuildingComponent[E, R]): ReactElement = {
    if (comp.args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    val ret = ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, comp.args).asInstanceOf[ReactElement]

    comp.args(0) = null

    ret
  }
}

abstract class ExternalComponentWithAttributesWithRefType[E <: TagElement, R <: js.Object](implicit pw: ExternalPropsWriterProvider) {
  type Props
  type Element = E
  type RefType = R

  private[this] final val writer = pw.asInstanceOf[Writer[Props]]

  val component: String | js.Object

  def apply(p: Props): BuildingComponent[E, R] = {
    // no need to take key or ref here because those can be passed in through attributes
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], writer.write(p)))
  }
}

abstract class ExternalComponentWithAttributes[E <: TagElement](implicit pw: ExternalPropsWriterProvider)
  extends ExternalComponentWithAttributesWithRefType[E, js.Object]()(pw)

abstract class ExternalComponentWithRefType[R <: js.Object](implicit pw: ExternalPropsWriterProvider) extends ExternalComponentWithAttributesWithRefType[Nothing, R]()(pw)

abstract class ExternalComponent(implicit pw: ExternalPropsWriterProvider) extends ExternalComponentWithAttributes[Nothing]()(pw)

abstract class ExternalComponentNoPropsWithAttributesWithRefType[E <: TagElement, R <: js.Object] {
  val component: String | js.Object

  def apply(mods: TagMod[E]*): BuildingComponent[E, R] = {
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], js.Dictionary.empty)).apply(mods: _*)
  }

  def withKey(key: String): BuildingComponent[E, R] =
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], js.Dictionary.empty)).withKey(key)
  def withRef(ref: R => Unit): BuildingComponent[E, R] =
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], js.Dictionary.empty)).withRef(ref)
  def withRef(ref: ReactRef[R]): BuildingComponent[E, R] =
    new BuildingComponent(js.Array(component.asInstanceOf[js.Any], js.Dictionary.empty)).withRef(ref)
}

abstract class ExternalComponentNoPropsWithAttributes[T <: TagElement]
  extends ExternalComponentNoPropsWithAttributesWithRefType[T, js.Object]

abstract class ExternalComponentNoPropsWithRefType[R <: js.Object]
  extends ExternalComponentNoPropsWithAttributesWithRefType[Nothing, R]

abstract class ExternalComponentNoProps extends ExternalComponentNoPropsWithAttributes[Nothing]

// same as PropsWriterProvider except it always returns the typeclass instead of nulling it out in fullOpt mode
trait ExternalPropsWriterProvider extends js.Object
object ExternalPropsWriterProvider {
  def impl(c: whitebox.Context): c.Expr[ExternalPropsWriterProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Writer[comp.Props] = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type])
    c.Expr(q"$tpcls.asInstanceOf[_root_.slinky.core.ExternalPropsWriterProvider]")
  }

  implicit def get: ExternalPropsWriterProvider = macro impl
}
