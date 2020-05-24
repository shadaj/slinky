package slinky.core.facade

import scala.scalajs.js
import slinky.core.ExternalComponent
import slinky.core.BuildingComponent
import slinky.readwrite.Writer
import slinky.core.ExternalPropsWriterProvider

object Profiler extends ExternalComponent()(new Writer[Profiler.Props] {
      override def write(value: Profiler.Props): js.Object =
        js.Dynamic.literal(id = value.id, onRender = value.onRender: js.Function7[String, String, Double, Double, Double, Double, js.Object, Unit])
    }.asInstanceOf[ExternalPropsWriterProvider]) {
  case class Props(id: String, onRender: (String, String, Double, Double, Double, Double, js.Object) => Unit)
  override val component = ReactRaw.Profiler

  def apply(id: String, onRender: (String, String, Double, Double, Double, Double, js.Object) => Unit): BuildingComponent[Nothing, js.Object] = apply(Props(id, onRender))
}
