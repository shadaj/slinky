package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}
import me.shadaj.slinky.readwrite.Writer

import scala.language.implicitConversions
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.scalajs.js
import scala.scalajs.js.|

class NoExternalProps private()
object NoExternalProps {
  implicit val writer: Writer[NoExternalProps] = _ => js.Dynamic.literal()
}

case class BuildingComponent[P, E](c: String | js.Object, props: P, key: String = null, ref: js.Object => Unit = null, mods: Seq[AttrPair[E]] = Seq.empty) {
  def apply(tagMods: AttrPair[E]*): BuildingComponent[P, E] = copy(mods = mods ++ tagMods)

  def withKey(key: String): BuildingComponent[P, E] = copy(key = key)
  def withRef(ref: js.Object => Unit): BuildingComponent[P, E] = copy(ref = ref)

  def apply(children: ReactElement*)(implicit writer: Writer[P]): ReactElement = {
    val written = writer.write(props).asInstanceOf[js.Dictionary[js.Any]]

    if (key != null) {
      written("key") = key
    }

    if (ref != null) {
      written("ref") = ref: js.Function1[js.Object, Unit]
    }

    mods.foreach { m =>
      written(m.name) = m.value
    }

    React.createElement(c, written, children: _*)
  }
}

object BuildingComponent {
  implicit def make[P, E]: BuildingComponent[P, E] => ReactElement = macro BuildingComponentMacros.makeImpl[P, E]
}

final class Maker[P: Writer, E] extends (BuildingComponent[P, E] => ReactElement) {
  override def apply(v1: BuildingComponent[P, E]): ReactElement = {
    v1()
  }
}

object BuildingComponentMacros {
  class Maker[P](implicit writer: Writer[P]) extends (BuildingComponent[P, _] => ReactElement) {
    override def apply(v1: BuildingComponent[P, _]): ReactElement = {
      v1(Seq.empty[ReactElement]: _*)
    }
  }

  // SUPER SKETCHY INTELLIJ HACK: IntellJ is unable to detect implicits that take type parameters
  def makeImpl[P: c.WeakTypeTag, E: c.WeakTypeTag](c: whitebox.Context): c.Expr[BuildingComponent[P, _] => ReactElement] = {
    import c.universe._
    c.Expr[BuildingComponent[P, _] => ReactElement](c.typecheck(
      q"new _root_.me.shadaj.slinky.core.BuildingComponentMacros.Maker[${implicitly[WeakTypeTag[P]]}]"
    ))
  }
}

abstract class ExternalComponent extends ExternalComponentWithAttributes[Nothing]

abstract class ExternalComponentWithAttributes[E <: TagElement] {
  type Props
  type Element = E

  val component: String | js.Object

  def apply(p: Props): BuildingComponent[Props, Element] = {
    // no need to take key or ref here because those can be passed in through attributes
    new BuildingComponent(component, p, null, null, Seq.empty)
  }

  def apply(tagMods: AttrPair[E]*)(implicit ev: NoExternalProps =:= Props): BuildingComponent[Props, E] =
    apply(null.asInstanceOf[NoExternalProps]).apply(tagMods: _*)

  def apply(children: ReactElement*)(implicit ev: NoExternalProps =:= Props): ReactElement =
    apply(null.asInstanceOf[NoExternalProps]).apply(children: _*)
}

object ExternalComponentWithAttributes {
  implicit def skipProps(comp: ExternalComponentWithAttributes[_])(implicit ev: NoExternalProps =:= comp.Props) = {
    comp.apply(null.asInstanceOf[NoExternalProps])
  }
}
