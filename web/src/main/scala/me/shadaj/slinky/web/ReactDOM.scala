package me.shadaj.slinky.web

import me.shadaj.slinky.core.facade.ComponentInstance
import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-dom", JSImport.Namespace, "ReactDOM")
object ReactDOM extends js.Object {
  def render(component: ComponentInstance, target: Element): js.Object = js.native
  def findDOMNode(component: js.Object): Element = js.native
}
