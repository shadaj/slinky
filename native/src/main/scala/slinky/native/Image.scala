package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.readwrite.ObjectOrWritten

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|
import scala.scalajs.js.JSConverters._

case class ImageURISource(
  uri: js.UndefOr[String] = js.undefined,
  bundle: js.UndefOr[String] = js.undefined,
  method: js.UndefOr[String] = js.undefined,
  headers: js.UndefOr[js.Object] = js.undefined,
  body: js.UndefOr[String] = js.undefined,
  cache: js.UndefOr[String] = js.undefined,
  width: js.UndefOr[Int] = js.undefined,
  height: js.UndefOr[Int] = js.undefined,
  scale: js.UndefOr[Double] = js.undefined
)

@js.native
trait ImageInterface extends js.Object

case class ImageErrorEvent(error: js.Error)

case class ImageProgressEvent(loaded: Int, total: Int)

@react object Image extends ExternalComponent {
  case class Props(
    style: js.UndefOr[js.Object] = js.undefined,
    blurRadius: js.UndefOr[Int] = js.undefined,
    onLayout: js.UndefOr[NativeSyntheticEvent[LayoutChangeEvent] => Unit] = js.undefined,
    onLoad: js.UndefOr[() => Unit] = js.undefined,
    onLoadEnd: js.UndefOr[() => Unit] = js.undefined,
    onLoadStart: js.UndefOr[() => Unit] = js.undefined,
    resizeMode: js.UndefOr[String] = js.undefined,
    source: js.UndefOr[ImageURISource | js.Object | Seq[ImageURISource | js.Object]] = js.undefined,
    loadingIndicatorSource: js.UndefOr[Seq[ImageURISource | Int]] = js.undefined,
    onError: js.UndefOr[NativeSyntheticEvent[ImageErrorEvent] => Unit] = js.undefined,
    testID: js.UndefOr[String] = js.undefined,
    resizeMethod: js.UndefOr[String] = js.undefined,
    accessibilityLabel: js.UndefOr[ReactElement] = js.undefined,
    accessible: js.UndefOr[Boolean] = js.undefined,
    capInsets: js.UndefOr[BoundingBox] = js.undefined,
    defaultSource: js.UndefOr[js.Object | Int] = js.undefined,
    onPartialLoad: js.UndefOr[() => Unit] = js.undefined,
    onProgress: js.UndefOr[NativeSyntheticEvent[ImageProgressEvent] => Unit] = js.undefined
  )

  @js.native
  @JSImport("react-native", "Image")
  object Component extends js.Object {
    def getSize(
      uri: String,
      success: js.Function2[Int, Int, Unit],
      failure: js.UndefOr[js.Function1[js.Error, Unit]]
    ): Unit                                                                   = js.native
    def prefetch(uri: String): Int | Unit                                     = js.native
    def abortPrefetch(requestId: Int): Unit                                   = js.native
    def queryCache(urls: js.Array[String]): js.Promise[js.Dictionary[String]] = js.native
  }

  override val component = Component

  def getSize(uri: String, success: (Int, Int) => Unit, failure: js.UndefOr[(js.Error) => Unit] = js.undefined): Unit =
    Component.getSize(uri, success, failure.map(v => v))

  def prefetch(uri: String): Int | Unit = Component.prefetch(uri)

  def abortPrefetch(requestId: Int): Unit = Component.abortPrefetch(requestId)

  def queryCache(urls: Seq[String]): Future[Map[String, String]] = {
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    Component.queryCache(urls.toJSArray).toFuture.map(_.toMap)
  }
}
