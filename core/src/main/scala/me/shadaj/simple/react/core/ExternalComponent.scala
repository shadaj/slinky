package me.shadaj.simple.react.core

import me.shadaj.simple.react.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.scalajs.js

class BuildingComponent[Props](private[BuildingComponent] val e: ExternalComponent, private[BuildingComponent] val props: Props) {
  def apply(children: ComponentInstance*)(implicit writer: Writer[Props]): ComponentInstance = {
    React.createElement(e.component, writer.write(props), children: _*)
  }
}

object BuildingComponent {
  implicit def shortCut[C <: ExternalComponent, Props](bc: BuildingComponent[Props])(implicit writer: Writer[Props]): ComponentInstance = {
    React.createElement(bc.e.component, writer.write(bc.props))
  }
}

trait ExternalComponent {
  type Props

  val component: js.Object

  def apply(p: Props): BuildingComponent[Props] = {
    new BuildingComponent(this, p)
  }
}
