package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.scalajs.js

class BuildingComponent[Props](e: ExternalComponent, props: Props, key: String, ref: js.Object => Unit, writer: Writer[Props]) {
  def withChildren(children: ComponentInstance*): ComponentInstance = {
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
  implicit def shortcut[Props](bc: BuildingComponent[Props]): ComponentInstance = {
    bc.withChildren()
  }
}

trait ExternalComponent {
  type Props

  val component: js.Object

  def apply(p: Props, key: String = null, ref: js.Object => Unit = null)(implicit writer: Writer[Props]): BuildingComponent[Props] = {
    new BuildingComponent[Props](this, p, key, ref, writer)
  }
}
