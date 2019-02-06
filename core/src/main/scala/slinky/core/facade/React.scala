package slinky.core.facade

import slinky.core.{ExternalComponent, ExternalPropsWriterProvider, FunctionalComponent}
import slinky.readwrite.{ObjectOrWritten, Reader, Writer}

import scala.scalajs.js
import js.|
import scala.annotation.unchecked.uncheckedVariance
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

@js.native
trait ReactRef[-T] extends js.Object {
  def current: T @uncheckedVariance  = js.native
}

class ReactForwardRefComponent[P](comp: js.Object) extends ExternalComponent()(Writer.fallback[P].asInstanceOf[ExternalPropsWriterProvider]) {
  type Props = P
  override val component: String | js.Object = comp
}

@js.native
trait ReactForwardRef[P] extends js.Object
object ReactForwardRef {
  implicit def toExternalComponent[P](r: ReactForwardRef[P]): ReactForwardRefComponent[P] = new ReactForwardRefComponent[P](r)
}

@js.native
@JSImport("react", JSImport.Namespace, "React")
private[slinky] object ReactRaw extends js.Object {
  def createElement(elementName: String | js.Object,
                    properties: js.Dictionary[js.Any],
                    contents: ReactElement*): ReactElement = js.native

  def createContext[T](defaultValue: T): ReactContext[T] = js.native

  def createRef[T](): ReactRef[T] = js.native

  def forwardRef[P](fn: js.Function2[js.Object, ReactRef[Any], ReactElement]): ReactForwardRef[P] = js.native

  def memo(fn: js.Object): js.Object = js.native

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
  val StrictMode: js.Object = js.native
  val Suspense: js.Object = js.native
}

object React {
  def createElement(elementName: String | js.Object,
                    properties: js.Dictionary[js.Any],
                    contents: ReactElement*): ReactElement = ReactRaw.createElement(elementName, properties, contents: _*)

  def createContext[T](defaultValue: T): ReactContext[T] = ReactRaw.createContext[T](defaultValue)

  def createRef[T]: ReactRef[T] = ReactRaw.createRef[T]()

  def forwardRef[P](fn: (P, ReactRef[Any]) => ReactElement): ReactForwardRef[P] = {
    ReactRaw.forwardRef[P]((obj, ref) => {
      fn(Reader.fallback[P].read(obj), ref)
    })
  }

  def memo[P](component: FunctionalComponent[P]): FunctionalComponent[P] = {
    new FunctionalComponent(ReactRaw.memo(component.component))
  }

  @JSImport("react", "Component", "React.Component")
  @js.native
  class Component(jsProps: js.Object) extends js.Object {
    def forceUpdate(): Unit = js.native
    def forceUpdate(callback: js.Function0[Unit]): Unit = js.native
  }

  object Children extends js.Object {
    def map(children: ReactChildren, transformer: ReactElement => ReactElement): ReactChildren = {
      ReactRaw.Children.map(children, transformer)
    }

    def map(children: ReactChildren, transformer: (ReactElement, Int) => ReactElement): ReactChildren = {
      ReactRaw.Children.map(children, transformer)
    }

    def forEach(children: ReactChildren, transformer: ReactElement => Unit): Unit = {
      ReactRaw.Children.forEach(children, transformer)
    }

    def forEach(children: ReactChildren, transformer: (ReactElement, Int) => Unit): Unit = {
      ReactRaw.Children.forEach(children, transformer)
    }

    def only(children: ReactChildren): ReactElement = {
      ReactRaw.Children.only(children)
    }

    def count(children: ReactChildren): Int = {
      ReactRaw.Children.count(children)
    }

    def toArray(children: ReactChildren): js.Array[ReactElement] = {
      ReactRaw.Children.toArray(children)
    }
  }
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
