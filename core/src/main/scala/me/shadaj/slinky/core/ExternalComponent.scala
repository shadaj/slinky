package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}

import scala.language.implicitConversions
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

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
  def makeImpl[P: c.WeakTypeTag, E: c.WeakTypeTag](c: Context): c.Expr[BuildingComponent[P, E] => ReactElement] = {
    import c.universe._
    c.Expr[BuildingComponent[P, E] => ReactElement](c.typecheck(q"new _root_.me.shadaj.slinky.core.Maker[${implicitly[WeakTypeTag[P]]}]"))
  }
}

abstract class ExternalComponent {
  type Props

  val component: String | js.Object

  def apply(p: Props, key: String = null, ref: js.Object => Unit = null): BuildingComponent[Props, Nothing] = {
    new BuildingComponent(component, p, key, ref, Seq.empty)
  }
}

abstract class ExternalComponentWithTagMods {
  type Props
  type Element

  val component: String | js.Object

  def apply(p: Props, tagMods: AttrPair[Element]*): BuildingComponent[Props, Element] = {
    new BuildingComponent(component, p, null, null, tagMods)
  }
}
