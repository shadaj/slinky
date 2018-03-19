package slinky.vr

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object Text extends ExternalComponent {
  case class Props(numberOfLines: js.UndefOr[Int] = js.undefined,
                   onLayout: js.UndefOr[NativeSyntheticEvent[LayoutEvent] => Unit] = js.undefined,
                   onLongPress: js.UndefOr[() => Unit] = js.undefined,
                   onPress: js.UndefOr[() => Unit] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined)

  @js.native
  @JSImport("react-vr", "Text")
  object Component extends js.Object

  override val component = Component
}
