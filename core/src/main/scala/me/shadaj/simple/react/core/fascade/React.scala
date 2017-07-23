package me.shadaj.simple.react.core.fascade

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}

@js.native
@JSImport("react", JSImport.Namespace, "React")
object React extends js.Object {
  def createElement(htmlName: String,      properties: Any, contents: ComponentInstance*): ComponentInstance = js.native
  def createElement(component: js.Object, properties: Any, contents: ComponentInstance*): ComponentInstance = js.native

  @js.native
  class Component(jsProps: Any) extends js.Object
}

@js.native
trait PrivateComponentClass extends js.Object {
  @JSName("props")
  var propsR: js.Object = js.native

  @JSName("state")
  var stateR: js.Object = js.native

  @JSName("refs")
  val refsR: js.Dynamic = js.native

  @JSName("context")
  val contextR: js.Dynamic = js.native

  @JSName("setState")
  def setStateR(newState: js.Object): Unit = js.native
}

@js.native
trait ComponentInstance extends js.Object
