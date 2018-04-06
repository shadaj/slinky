package slinky.core.facade

import slinky.core.{BuildingComponent, ExternalComponent, ExternalPropsWriterProvider}
import slinky.readwrite.{Reader, Writer}

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

  @inline implicit def intToElement(i: Int): ReactElement = {
    i.asInstanceOf[ReactElement]
  }

  @inline implicit def doubleToElement(d: Double): ReactElement = {
    d.asInstanceOf[ReactElement]
  }

  @inline implicit def floatToElement(f: Float): ReactElement = {
    f.asInstanceOf[ReactElement]
  }

  @inline implicit def booleanToElement(b: Boolean): ReactElement = {
    b.asInstanceOf[ReactElement]
  }

  @inline implicit def optionToElement[T](s: Option[T])(implicit cv: T => ReactElement): ReactElement = {
    s match {
      case Some(e) => cv(e)
      case None => null.asInstanceOf[ReactElement]
    }
  }

  @inline implicit def seqElementToElement[T](s: Iterable[T])(implicit cv: T => ReactElement): ReactElement = {
    s.map(cv).toJSArray.asInstanceOf[ReactElement]
  }
}

@js.native
trait ReactInstance extends js.Object

@js.native
trait ReactChildren extends ReactElement

case class ContextProviderProps[T](value: T)
object ContextProviderProps {
  implicit def writer[T]: Writer[ContextProviderProps[T]] = v => js.Dynamic.literal(
    value = Writer.fallback[T].write(v.value)
  )
}

class ContextProvider[T](orig: ReactContext[T]) {
  private object External extends ExternalComponent()(ContextProviderProps.writer[T].asInstanceOf[ExternalPropsWriterProvider]) {
    override type Props = ContextProviderProps[T]
    override val component: |[String, js.Object] = orig._Provider
  }

  def apply(value: T): BuildingComponent[Nothing, js.Object] = External(ContextProviderProps(value))
}

case class ContextConsumerProps[T](children: T => ReactElement)
object ContextConsumerProps {
  implicit def writer[T]: Writer[ContextConsumerProps[T]] = v => js.Dynamic.literal(
    children = Writer.function1[T, ReactElement](
      Reader.fallback[T], Writer.jsAnyWriter[ReactElement]
    ).write(v.children)
  )
}

class ContextConsumer[T](orig: ReactContext[T]) {
  private object External extends ExternalComponent()(ContextConsumerProps.writer[T].asInstanceOf[ExternalPropsWriterProvider]) {
    override type Props = ContextConsumerProps[T]
    override val component: |[String, js.Object] = orig._Consumer
  }

  def apply(children: T => ReactElement): BuildingComponent[Nothing, js.Object] = External(ContextConsumerProps(children))
}


@js.native
trait ReactContext[T] extends js.Object {
  @JSName("Provider") private[slinky] val _Provider: js.Object = js.native
  @JSName("Consumer") private[slinky] val _Consumer: js.Object = js.native
}

object ReactContext {
  final implicit class RichReactContext[T](private val orig: ReactContext[T]) extends AnyVal {
    def Provider: ContextProvider[T] = new ContextProvider[T](orig)
    def Consumer: ContextConsumer[T] = new ContextConsumer[T](orig)
  }
}

@js.native
@JSImport("react", JSImport.Namespace, "React")
object React extends js.Object {
  def createElement(elementName: String | js.Object,
                    properties: js.Dictionary[js.Any],
                    contents: ReactElement*): ReactElement = js.native

  def createContext[T](name: String): ReactContext[T] = js.native

  @js.native
  class Component(jsProps: js.Object) extends js.Object {
    def forceUpdate(): Unit = js.native
    def forceUpdate(callback: js.Function0[Unit]): Unit = js.native
  }

  @js.native
  object Children extends js.Object {
    def map(children: ReactChildren, transformer: js.Function1[ReactElement, ReactElement]): ReactChildren = js.native
    def map(children: ReactChildren, transformer: js.Function2[ReactElement, Int, ReactElement]): ReactChildren = js.native

    def forEach(children: ReactChildren, transformer: js.Function1[ReactElement, Unit]): Unit = js.native
    def forEach(children: ReactChildren, transformer: js.Function2[ReactElement, Int, Unit]): Unit = js.native

    def only(children: ReactChildren): ReactElement = js.native

    def count(children: ReactChildren): Int = js.native

    def toArray(children: ReactChildren): js.Array[ReactElement] = js.native
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
