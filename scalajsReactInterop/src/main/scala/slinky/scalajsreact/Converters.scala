package slinky.scalajsreact

import japgolly.scalajs.react.component.Generic.UnmountedRaw
import japgolly.scalajs.react.vdom.{TagOf, VdomNode}
import japgolly.scalajs.react.vdom.html_<^._

import ScalaJSReactCompat._

import slinky.core.facade.ReactElement

object Converters {
  implicit class UnmountedToInstance(unmounted: UnmountedRaw) {
    def toSlinky: ReactElement =
      unmounted.raw.asInstanceOf[ReactElement]
  }

  implicit class TagToInstance(tag: TagOf[_]) {
    def toSlinky: ReactElement =
      tag.rawNode.asInstanceOf[ReactElement]
  }

  implicit class VdomToInstance(vdom: VdomElement) {
    def toSlinky: ReactElement =
      vdom.rawNode.asInstanceOf[ReactElement]
  }

  implicit class ComponentInstanceToVdom[T](component: T)(implicit ev: T => ReactElement) {
    def toScalaJSReact: VdomNode =
      VdomNode(ev(component).asInstanceOf[Element])
  }
}
