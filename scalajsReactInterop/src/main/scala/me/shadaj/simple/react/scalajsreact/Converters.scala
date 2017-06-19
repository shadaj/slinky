package me.shadaj.simple.react.scalajsreact

import japgolly.scalajs.react.component.Generic.UnmountedRaw
import japgolly.scalajs.react.vdom.VdomNode
import me.shadaj.simple.react.core.fascade.ComponentInstance

object Converters {
  implicit def unmountedToInstance(unmounted: UnmountedRaw): ComponentInstance = {
    unmounted.raw.asInstanceOf[ComponentInstance]
  }

  implicit def vdomToInstance[T](vdom: T)(implicit ev: T => VdomNode): ComponentInstance = {
    vdom.rawNode.asInstanceOf[ComponentInstance]
  }
}
