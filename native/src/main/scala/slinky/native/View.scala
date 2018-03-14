package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object View extends ExternalComponent {
  case class Props(hitSlop: js.UndefOr[BoundingBox] = js.undefined,
                   nativeID: js.UndefOr[String] = js.undefined,
                   accessible: js.UndefOr[Boolean] = js.undefined,
                   pointerEvents: js.UndefOr[String] = js.undefined,
                   removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined)

  @js.native
  @JSImport("react-native", "View")
  object Component extends js.Object

  override val component = Component
}
