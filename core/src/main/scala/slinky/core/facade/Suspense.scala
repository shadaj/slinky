package slinky.core.facade

import slinky.readwrite.Writer
import slinky.core.{BuildingComponent, ExternalComponent, ExternalPropsWriterProvider}

import scala.scalajs.js
import scala.scalajs.js.|
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}
import concurrent.ExecutionContext.Implicits.global


object Suspense
    extends ExternalComponent()(new Writer[Suspense.Props] {
      override def write(value: Suspense.Props): js.Object =
        js.Dynamic.literal(fallback = value.fallback)
    }.asInstanceOf[ExternalPropsWriterProvider]) {
  case class Props(fallback: ReactElement)
  override val component: |[String, js.Object] = ReactRaw.Suspense

  def apply(fallback: ReactElement): BuildingComponent[Nothing, js.Object] = apply(Props(fallback))
}


object Suspended {

  class PromiseWrap[T](suspend: Future[T]) extends js.Object {
    private val pending = suspend.toJSPromise
    private var data: Option[T] = None
    private var throwable: Option[Throwable] = None

    suspend.onComplete{
      case Success(result) => data = Some(result)
      case Failure(error) => throwable = Some(error)
    }

    def read(): T = (data, throwable) match {
      case (Some(data), _) => data
      case (None, Some(error)) => throw error
      case _ => throw JavaScriptException(pending)
    }

  }
}

