package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.scalajs.js

class BuildingComponent[Props](private[BuildingComponent] val e: ExternalComponent, private[BuildingComponent] val props: Props, writer: Writer[Props]) {
  def withChildren(children: ComponentInstance*): ComponentInstance = {
    React.createElement(e.component, writer.write(props), children: _*)
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

  def apply(p: Props)(implicit writer: Writer[Props]): BuildingComponent[Props] = {
    new BuildingComponent[Props](this, p, writer)
  }
}
