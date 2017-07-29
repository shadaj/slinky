package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.language.experimental.macros

import scala.reflect.macros.blackbox.Context
import scala.scalajs.js

class BuildingComponent[P](e: ExternalComponent, props: P, key: String, ref: js.Object => Unit) {
  def apply(children: ComponentInstance*)(implicit writer: Writer[P]): ComponentInstance = {
    val written = writer.write(props)

    if (key != null) {
      written.asInstanceOf[js.Dynamic].updateDynamic("key")(key)
    }

    if (ref != null) {
      written.asInstanceOf[js.Dynamic].updateDynamic("ref")(ref: js.Function1[js.Object, Unit])
    }

    React.createElement(e.component, written, children: _*)
  }
}

object BuildingComponent {
  implicit def make[P]: BuildingComponent[P] => ComponentInstance = macro BuildingComponentMacros.makeImpl[P]
}

final class Maker[P: Writer] extends (BuildingComponent[P] => ComponentInstance) {
  override def apply(v1: BuildingComponent[P]): ComponentInstance = {
    v1()
  }
}

object BuildingComponentMacros {
  // SUPER SKETCHY INTELLIJ HACK
  def makeImpl[P: c.WeakTypeTag](c: Context): c.Expr[BuildingComponent[P] => ComponentInstance] = {
    import c.universe._
    c.Expr[BuildingComponent[P] => ComponentInstance](q"new _root_.me.shadaj.slinky.core.Maker")
  }
}

abstract class ExternalComponent {
  type Props

  val component: js.Object

  def apply(p: Props, key: String = null, ref: js.Object => Unit = null): BuildingComponent[Props] = {
    new BuildingComponent(this, p, key, ref)
  }
}
