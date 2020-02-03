package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class NativeTouchEvent(
  changedTouches: Seq[Any],
  identifier: String,
  locationX: Int,
  locationY: Int,
  pageX: Int,
  pageY: Int,
  target: String,
  timestamp: Int,
  touches: Seq[Any]
)

case class LayoutRectangle(x: Int, y: Int, width: Int, height: Int)

case class LayoutChangeEvent(layout: LayoutRectangle)

@react object View extends ExternalComponent {
  case class Props(
    onStartShouldSetResponder: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Unit] = js.undefined,
    accessibilityLabel: js.UndefOr[ReactElement] = js.undefined,
    hitSlop: js.UndefOr[BoundingBox] = js.undefined,
    nativeID: js.UndefOr[String] = js.undefined,
    onLayout: js.UndefOr[NativeSyntheticEvent[LayoutChangeEvent] => Unit] = js.undefined,
    onMagicTap: js.UndefOr[() => Unit] = js.undefined,
    onMoveShouldSetResponder: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Boolean] = js.undefined,
    onMoveShouldSetResponderCapture: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Boolean] = js.undefined,
    onResponderGrant: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Unit] = js.undefined,
    onResponderMove: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Unit] = js.undefined,
    onResponderReject: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Unit] = js.undefined,
    onResponderRelease: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Unit] = js.undefined,
    onResponderTerminate: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Unit] = js.undefined,
    onResponderTerminationRequest: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Boolean] = js.undefined,
    accessible: js.UndefOr[Boolean] = js.undefined,
    onStartShouldSetResponderCapture: js.UndefOr[NativeSyntheticEvent[NativeTouchEvent] => Boolean] = js.undefined,
    pointerEvents: js.UndefOr[String] = js.undefined,
    removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
    style: js.UndefOr[js.Object] = js.undefined,
    testID: js.UndefOr[String] = js.undefined,
    accessibilityComponentType: js.UndefOr[String] = js.undefined,
    accessibilityLiveRegion: js.UndefOr[String] = js.undefined,
    collapsable: js.UndefOr[Boolean] = js.undefined,
    importantForAccessibility: js.UndefOr[String] = js.undefined,
    needsOffscreenAlphaCompositing: js.UndefOr[Boolean] = js.undefined,
    renderToHardwareTextureAndroid: js.UndefOr[Boolean] = js.undefined,
    accessibilityTraits: js.UndefOr[String | Seq[String]] = js.undefined,
    accessibilityViewIsModal: js.UndefOr[Boolean] = js.undefined,
    shouldRasterizeIOS: js.UndefOr[Boolean] = js.undefined
  )

  @js.native
  @JSImport("react-native", "View")
  object Component extends js.Object

  override val component = Component
}
