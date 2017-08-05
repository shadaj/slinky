package me.shadaj.slinky.scalajsreact

import japgolly.scalajs.react.component.Generic.UnmountedRaw
import japgolly.scalajs.react.raw.ReactNode
import japgolly.scalajs.react.vdom.{TagOf, VdomNode}
import japgolly.scalajs.react.vdom.html_<^._

import me.shadaj.slinky.core.TagComponent
import me.shadaj.slinky.core.facade.ReactElement

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

  implicit def htmlToVdom(component: TagComponent[_]): VdomNode = {
    VdomNode((component: ReactElement).asInstanceOf[ReactNode])
  }

  implicit def componentInstanceToVdom(component: ReactElement): VdomNode = {
    VdomNode(component.asInstanceOf[ReactNode])
  }
}
