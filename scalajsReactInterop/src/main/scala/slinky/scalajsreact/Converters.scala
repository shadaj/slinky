package slinky.scalajsreact

import japgolly.scalajs.react.component.Generic.UnmountedRaw
import japgolly.scalajs.react.raw.React.Element
import japgolly.scalajs.react.vdom.{TagOf, VdomNode}
import japgolly.scalajs.react.vdom.html_<^._

import slinky.core.facade.ReactElement

import scala.language.implicitConversions

object Converters {
  implicit def unmountedToInstance(unmounted: UnmountedRaw): ReactElement = {
    unmounted.raw.asInstanceOf[ReactElement]
  }

  implicit def tagToInstance(tag: TagOf[_]): ReactElement = {
    tag.render.rawNode.asInstanceOf[ReactElement]
  }

  implicit def vdomToInstance(vdom: VdomElement): ReactElement = {
    vdom.rawNode.asInstanceOf[ReactElement]
  }

  implicit def componentInstanceToVdom[T](component: T)(implicit ev: T => ReactElement): VdomNode = {
    VdomNode(ev(component).asInstanceOf[Element])
  }
}
