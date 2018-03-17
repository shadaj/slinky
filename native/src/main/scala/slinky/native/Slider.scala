package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@react object Slider extends ExternalComponent {
  case class Props(style: js.UndefOr[js.Object] = js.undefined,
                   disabled: js.UndefOr[Boolean] = js.undefined,
                   maximumValue: js.UndefOr[Double] = js.undefined,
                   minimumTrackTintColor: js.UndefOr[String] = js.undefined,
                   minimumValue: js.UndefOr[Double] = js.undefined,
                   onSlidingComplete: js.UndefOr[Double => Unit] = js.undefined,
                   onValueChange: js.UndefOr[Double => Unit] = js.undefined,
                   step: js.UndefOr[Double] = js.undefined,
                   maximumTrackTintColor: js.UndefOr[String] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined,
                   value: js.UndefOr[Double] = js.undefined,
                   thumbTintColor: js.UndefOr[String] = js.undefined,
                   maximumTrackImage: js.UndefOr[ImageURISource | Int | Seq[ImageURISource]] = js.undefined,
                   minimumTrackImage: js.UndefOr[ImageURISource | Int | Seq[ImageURISource]] = js.undefined,
                   thumbImage: js.UndefOr[ImageURISource | Int | Seq[ImageURISource]] = js.undefined,
                   trackImage: js.UndefOr[ImageURISource | Int | Seq[ImageURISource]] = js.undefined)

  @js.native
  @JSImport("react-native", "Slider")
  object Component extends js.Object

  override val component = Component
}
