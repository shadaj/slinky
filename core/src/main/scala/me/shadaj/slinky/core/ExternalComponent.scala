package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}

import scala.language.implicitConversions
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.scalajs.js
import scala.scalajs.js.|

case class BuildingComponent[P, E](c: String | js.Object, props: P, key: String, ref: js.Object => Unit, mods: Seq[AttrPair[E]]) {
  def apply(tagMods: AttrPair[E]*): BuildingComponent[P, E] = copy(mods = mods ++ tagMods)

  def apply(children: ReactElement*)(implicit writer: Writer[P]): ReactElement = {
    val written = writer.write(props, true).asInstanceOf[js.Dictionary[js.Any]]

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
  class Maker[P] extends (BuildingComponent[P, _] => ReactElement) {
    override def apply(v1: BuildingComponent[P, _]): ReactElement = {
      v1(Seq.empty[ReactElement]: _*)
    }
  }

  // SUPER SKETCHY INTELLIJ HACK: IntellJ is unable to detect implicits that take type parameters
  def makeImpl[P: c.WeakTypeTag, E: c.WeakTypeTag](c: whitebox.Context): c.Expr[BuildingComponent[P, _] => ReactElement] = {
    import c.universe._
    c.Expr[BuildingComponent[P, _] => ReactElement](c.typecheck(
      q"new _root_.me.shadaj.slinky.core.BuildingComponentMacros.Maker[${implicitly[WeakTypeTag[P]]}]()"
    ))
  }
}

abstract class ExternalComponent extends ExternalComponentWithAttributes[Nothing]

abstract class ExternalComponentWithAttributes[E] {
  type Props
  type Element = E

  val component: String | js.Object

  def apply(p: Props): BuildingComponent[Props, Element] = {
    // no need to take key or ref here because those can be passed in through attributes
    new BuildingComponent(component, p, null, null, Seq.empty)
  }
}
