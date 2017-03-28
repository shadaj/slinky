package me.shadaj.simple.react.core.fascade

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}

@js.native
@JSImport("react", JSImport.Namespace)
object React extends js.Object {
  type IDK = Any

  def createElement(htmlName: String, properties: js.Any, contents: ComponentInstance*): ComponentInstance = js.native
  def createElement(component: IDK,   properties: js.Any, contents: ComponentInstance*): ComponentInstance = js.native

  @js.native
  class Component(jsProps: js.Object) extends js.Object {
    @JSName("props")
    private[core] var propsR: js.Dynamic = js.native

    @JSName("state")
    private[core] var stateR: js.Dynamic = js.native

    @JSName("refs")
    private[core] val refsR: IDK = js.native

    @JSName("context")
    private[core] val contextR: IDK = js.native

    @JSName("setState")
    private[core] def setStateR(newState: js.Dynamic): Unit = js.native
  }
}

@js.native
trait ComponentInstance extends js.Object {

}

@js.native
trait ComponentConstructor extends js.Object
