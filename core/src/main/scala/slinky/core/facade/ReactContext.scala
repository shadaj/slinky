package slinky.core.facade

import slinky.core.{BuildingComponent, ExternalComponent, ExternalPropsWriterProvider}
import slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName
import scala.scalajs.js.|

@js.native
trait ReactContextRaw extends js.Object {
  val Provider: js.Object = js.native
  val Consumer: js.Object = js.native
}

case class ContextProviderProps[T](value: T)
object ContextProviderProps {
  implicit def writer[T]: Writer[ContextProviderProps[T]] = v => js.Dynamic.literal(
    value = v.value.asInstanceOf[js.Any]
  )
}

class ContextProvider[T](orig: ReactContext[T]) {
  private object External extends ExternalComponent()(ContextProviderProps.writer[T].asInstanceOf[ExternalPropsWriterProvider]) {
    override type Props = ContextProviderProps[T]
    override val component: |[String, js.Object] = orig.asInstanceOf[ReactContextRaw].Provider
  }

  def apply(value: T): BuildingComponent[Nothing, js.Object] = External(ContextProviderProps(value))
}

case class ContextConsumerProps[T](children: T => ReactElement)
object ContextConsumerProps {
  implicit def writer[T]: Writer[ContextConsumerProps[T]] = v => js.Dynamic.literal(
    children = Writer.function1[T, ReactElement](
      _.asInstanceOf[T], Writer.jsAnyWriter[ReactElement]
    ).write(v.children)
  )
}

class ContextConsumer[T](orig: ReactContext[T]) {
  private object External extends ExternalComponent()(ContextConsumerProps.writer[T].asInstanceOf[ExternalPropsWriterProvider]) {
    override type Props = ContextConsumerProps[T]
    override val component: |[String, js.Object] = orig.asInstanceOf[ReactContextRaw].Consumer
  }

  def apply(children: T => ReactElement): BuildingComponent[Nothing, js.Object] = External(ContextConsumerProps(children))
}

@js.native
trait ReactContext[T] extends js.Object
object ReactContext {
  final implicit class RichReactContext[T](private val orig: ReactContext[T]) extends AnyVal {
    def Provider: ContextProvider[T] = new ContextProvider[T](orig)
    def Consumer: ContextConsumer[T] = new ContextConsumer[T](orig)
  }
}
