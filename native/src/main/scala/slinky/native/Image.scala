package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class ImageURISource(uri: js.UndefOr[String] = js.undefined,
                          bundle: js.UndefOr[String] = js.undefined,
                          method: js.UndefOr[String] = js.undefined,
                          headers: js.UndefOr[js.Object] = js.undefined,
                          body: js.UndefOr[String] = js.undefined,
                          cache: js.UndefOr[String] = js.undefined,
                          width: js.UndefOr[Int] = js.undefined,
                          height: js.UndefOr[Int] = js.undefined,
                          scale: js.UndefOr[Double] = js.undefined)

@react object Image extends ExternalComponent {
  case class Props(accessible: js.UndefOr[Boolean] = js.undefined,
                   accessibilityLabel: js.UndefOr[String] = js.undefined,
                   blurRadius: js.UndefOr[Int] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   onLoad: js.UndefOr[() => Unit] = js.undefined,
                   onLoadEnd: js.UndefOr[() => Unit] = js.undefined,
                   onLoadStart: js.UndefOr[() => Unit] = js.undefined,
                   resizeMethod: js.UndefOr[String] = js.undefined,
                   resizeMode: js.UndefOr[String] = js.undefined,
                   source: js.UndefOr[ImageURISource | Int | Seq[ImageURISource]] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined)

  @js.native
  @JSImport("react-native", "Image")
  object Component extends js.Object

  override val component = Component
}
