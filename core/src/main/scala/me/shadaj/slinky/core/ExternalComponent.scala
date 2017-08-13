package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}

import scala.language.implicitConversions
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.scalajs.js
import scala.scalajs.js.|

class BuildingComponent[P, E](c: String | js.Object, props: P, key: String, ref: js.Object => Unit, mods: Seq[AttrPair[E]]) {
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
  // SUPER SKETCHY INTELLIJ HACK
  def makeImpl[P: c.WeakTypeTag, E: c.WeakTypeTag](c: whitebox.Context): c.Expr[BuildingComponent[P, E] => ReactElement] = {
    import c.universe._
    c.Expr[BuildingComponent[P, E] => ReactElement](c.typecheck(q"new _root_.me.shadaj.slinky.core.Maker[${implicitly[WeakTypeTag[P]]}, ${implicitly[WeakTypeTag[E]]}]"))
  }
}

abstract class ExternalComponent {
  type Props

  val component: String | js.Object

  def apply(p: Props, key: String = null, ref: js.Object => Unit = null): BuildingComponent[Props, Any] = {
    new BuildingComponent(component, p, key, ref, Seq.empty)
  }
}

object ExternalComponent {
  implicit class ProplessExternalComponent(val c: ExternalComponent { type Props = Unit }) {
    def apply(key: String = null, ref: js.Object => Unit = null): BuildingComponent[c.Props, Any] = {
      c.apply((), key, ref)
    }
  }
}

abstract class ExternalComponentWithTagMods[E] {
  type Props
  type Element = E

  val component: String | js.Object

  def apply(p: Props, tagMods: AttrPair[Element]*): BuildingComponent[Props, Element] = {
    // no need to take key or ref here because those can be passed in through attributes
    new BuildingComponent(component, p, null, null, tagMods)
  }
}

object ExternalComponentWithTagMods {
  implicit class ProplessExternalComponentWithTagMods[C <: ExternalComponentWithTagMods[_] { type Props = Unit }](val c: C) {
    def apply(tagMods: AttrPair[c.Element]*): BuildingComponent[c.Props, c.Element] = {
      c.apply((), tagMods: _*)
    }
  }
}