package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

case class BoundingBox(top: Double, left: Double, bottom: Double, right: Double)

@react object Text extends ExternalComponent {
  case class Props(selectable: js.UndefOr[Boolean] = js.undefined,
                   accessible: js.UndefOr[Boolean] = js.undefined,
                   ellipsizeMode: js.UndefOr[String] = js.undefined,
                   nativeID: js.UndefOr[String] = js.undefined,
                   numberOfLines: js.UndefOr[Int] = js.undefined,
                   pressRetentionOffset: js.UndefOr[BoundingBox] = js.undefined,
                   allowFontScaling: js.UndefOr[Boolean] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined)

  @js.native
  @JSImport("react-native", "Text")
  object Component extends js.Object

  override val component = Component
}
