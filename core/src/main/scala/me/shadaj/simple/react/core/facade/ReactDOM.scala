package me.shadaj.simple.react.core.facade

import scala.scalajs.js
import org.scalajs.dom.Element

import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-dom", JSImport.Namespace, "ReactDOM")
object ReactDOM extends js.Object {
  def render(component: ComponentInstance, target: Element): js.Object = js.native
}
