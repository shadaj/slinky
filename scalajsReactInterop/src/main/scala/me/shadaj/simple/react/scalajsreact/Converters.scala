package me.shadaj.simple.react.scalajsreact

import japgolly.scalajs.react.component.Generic.UnmountedRaw
import japgolly.scalajs.react.raw.ReactNode
import japgolly.scalajs.react.vdom.{TagOf, VdomNode}
import me.shadaj.simple.react.core.fascade.ComponentInstance
import me.shadaj.simple.react.core.html.{AppliedAttribute, HtmlComponent}

import japgolly.scalajs.react.vdom.html_<^._

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

  implicit def htmlToVdom[A <: AppliedAttribute](component: HtmlComponent[A]): VdomNode = {
    VdomNode((component: ComponentInstance).asInstanceOf[ReactNode])
  }

  implicit def componentInstanceToVdom(component: ComponentInstance): VdomNode = {
    VdomNode(component.asInstanceOf[ReactNode])
  }
}
