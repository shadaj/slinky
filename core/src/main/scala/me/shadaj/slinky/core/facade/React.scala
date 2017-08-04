package me.shadaj.slinky.core.facade

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}
import scala.scalajs.js.JSConverters._

@js.native
@JSImport("react", JSImport.Namespace, "React")
object React extends js.Object {
  def createElement(htmlName:  String,    properties: Any, contents: ComponentInstance*): ComponentInstance = js.native
  def createElement(component: js.Object, properties: Any, contents: ComponentInstance*): ComponentInstance = js.native

  @js.native
  class Component(jsProps: js.Object) extends js.Object
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

object ComponentInstance {
  implicit def stringToInstance(s: String): ComponentInstance = {
    s.asInstanceOf[ComponentInstance]
  }

  implicit def seqInstanceToInstance[T](s: Iterable[T])(implicit cv: T => ComponentInstance): ComponentInstance = {
    s.map(cv).toJSArray.asInstanceOf[ComponentInstance]
  }
}