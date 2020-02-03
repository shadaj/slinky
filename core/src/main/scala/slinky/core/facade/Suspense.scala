package slinky.core.facade

import slinky.readwrite.Writer
import slinky.core.{BuildingComponent, ExternalComponent, ExternalPropsWriterProvider}

import scala.scalajs.js
import scala.scalajs.js.|

object Suspense
    extends ExternalComponent()(new Writer[Suspense.Props] {
      override def write(value: Suspense.Props): js.Object =
        js.Dynamic.literal(fallback = value.fallback)
    }.asInstanceOf[ExternalPropsWriterProvider]) {
  case class Props(fallback: ReactElement)
  override val component: |[String, js.Object] = ReactRaw.Suspense

  def apply(fallback: ReactElement): BuildingComponent[Nothing, js.Object] = apply(Props(fallback))
}
