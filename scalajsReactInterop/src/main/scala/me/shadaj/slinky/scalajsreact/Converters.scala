package me.shadaj.slinky.scalajsreact

import japgolly.scalajs.react.component.Generic.UnmountedRaw
import japgolly.scalajs.react.raw.ReactNode
import japgolly.scalajs.react.vdom.{TagOf, VdomNode}
import japgolly.scalajs.react.vdom.html_<^._

import me.shadaj.slinky.core.TagComponent
import me.shadaj.slinky.core.facade.ComponentInstance

import scala.language.implicitConversions

object Converters {
  implicit def unmountedToInstance(unmounted: UnmountedRaw): ComponentInstance = {
    unmounted.raw.asInstanceOf[ComponentInstance]
  }

  implicit def tagToInstance(tag: TagOf[_]): ComponentInstance = {
    tag.render.rawNode.asInstanceOf[ComponentInstance]
  }

  implicit def vdomToInstance(vdom: VdomElement): ComponentInstance = {
    vdom.rawNode.asInstanceOf[ComponentInstance]
  }

  implicit def htmlToVdom(component: TagComponent[_]): VdomNode = {
    VdomNode((component: ComponentInstance).asInstanceOf[ReactNode])
  }

  implicit def componentInstanceToVdom(component: ComponentInstance): VdomNode = {
    VdomNode(component.asInstanceOf[ReactNode])
  }
}
