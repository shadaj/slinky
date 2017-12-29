package me.shadaj.slinky.core.facade

import scala.scalajs.js
import js.|
import scala.scalajs.js.annotation.{JSImport, JSName}
import scala.scalajs.js.JSConverters._

import scala.language.implicitConversions

@js.native
trait ReactElement extends js.Object

object ReactElement {
  @inline implicit def stringToElement(s: String): ReactElement = {
    s.asInstanceOf[ReactElement]
  }

  @inline implicit def optionToElement(s: Option[ReactElement]): ReactElement = {
    s.getOrElse(null.asInstanceOf[ReactElement])
  }

  @inline implicit def seqElementToElement[T](s: Iterable[T])(implicit cv: T => ReactElement): ReactElement = {
    s.map(cv).toJSArray.asInstanceOf[ReactElement]
  }
}

@js.native
trait ReactInstance extends js.Object

@js.native
@JSImport("react", JSImport.Namespace, "React")
object React extends js.Object {
  def createElement(elementName: String | js.Object,
                    properties: js.Dictionary[js.Any],
                    contents: ReactElement*): ReactElement = js.native

  @js.native
  class Component(jsProps: js.Object) extends js.Object {
    def forceUpdate(): Unit = js.native
    def forceUpdate(callback: js.Function0[Unit]): Unit = js.native
  }

  val Fragment: js.Object = js.native
}

@js.native
trait ErrorBoundaryInfo extends js.Object {
  val componentStack: String = js.native
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

  @JSName("setState")
  def setStateR(fn: js.Function2[js.Object, js.Object, js.Object]): Unit = js.native

  @JSName("setState")
  def setStateR(newState: js.Object, callback: js.Function0[Unit]): Unit = js.native

  @JSName("setState")
  def setStateR(fn: js.Function2[js.Object, js.Object, js.Object], callback: js.Function0[Unit]): Unit = js.native
}
