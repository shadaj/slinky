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
                   onLayout: js.UndefOr[NativeSyntheticEvent[LayoutChangeEvent] => Unit] = js.undefined,
                   onLongPress: js.UndefOr[() => Unit] = js.undefined,
                   onPress: js.UndefOr[() => Unit] = js.undefined,
                   pressRetentionOffset: js.UndefOr[BoundingBox] = js.undefined,
                   allowFontScaling: js.UndefOr[Boolean] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined,
                   disabled: js.UndefOr[Boolean] = js.undefined,
                   selectionColor: js.UndefOr[String] = js.undefined,
                   textBreakStrategy: js.UndefOr[String] = js.undefined,
                   adjustsFontSizeToFit: js.UndefOr[Boolean] = js.undefined,
                   minimumFontScale: js.UndefOr[Double] = js.undefined,
                   suppressHighlighting: js.UndefOr[Boolean] = js.undefined)

  @js.native
  @JSImport("react-native", "Text")
  object Component extends js.Object

  override val component = Component
}
