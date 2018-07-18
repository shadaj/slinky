package slinky.vr

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class EdgeInsets(top: Double, bottom: Double, left: Double, right: Double)
case class Layout(x: Double, y: Double, width: Double, height: Double)
case class LayoutEvent(layout: Layout)

@react object View extends ExternalComponent {
  case class Props(billboarding: js.UndefOr[String] = js.undefined,
                   cursorVisibilitySlop: js.UndefOr[Double | EdgeInsets] = js.undefined,
                   hitSlop: js.UndefOr[Double | EdgeInsets] = js.undefined,
                   onEnter: js.UndefOr[() => Unit] = js.undefined,
                   onExit: js.UndefOr[() => Unit] = js.undefined,
                   onHeadPose: js.UndefOr[NativeSyntheticEvent[js.Object] => Unit] = js.undefined,
                   onHeadPoseCaptured: js.UndefOr[() => Unit] = js.undefined,
                   onInput: js.UndefOr[() => Unit] = js.undefined,
                   onInputCaptured: js.UndefOr[() => Unit] = js.undefined,
                   onLayout: js.UndefOr[NativeSyntheticEvent[LayoutEvent] => Unit] = js.undefined,
                   onMove: js.UndefOr[() => Unit] = js.undefined,
                   pointerEvents: js.UndefOr[String] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined)

  @js.native
  @JSImport("react-360", "View")
  object Component extends js.Object

  override val component = Component
}
