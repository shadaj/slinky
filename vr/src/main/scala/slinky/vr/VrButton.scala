package slinky.vr

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object VrButton extends ExternalComponent {
  case class Props(disabled: js.UndefOr[Boolean] = js.undefined,
                   ignoreLongClick: js.UndefOr[Boolean] = js.undefined,
                   longClickDelayMS: js.UndefOr[Int] = js.undefined,
                   onButtonPress: js.UndefOr[() => Unit] = js.undefined,
                   onButtonRelease: js.UndefOr[() => Unit] = js.undefined,
                   onClick: js.UndefOr[() => Unit] = js.undefined,
                   onClickSound: js.UndefOr[js.Object] = js.undefined,
                   onEnter: js.UndefOr[() => Unit] = js.undefined,
                   onEnterSound: js.UndefOr[js.Object] = js.undefined,
                   onExit: js.UndefOr[() => Unit] = js.undefined,
                   onExitSound: js.UndefOr[js.Object] = js.undefined,
                   onLongClick: js.UndefOr[() => Unit] = js.undefined,
                   onLongClickSound: js.UndefOr[js.Object] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined)

  @js.native
  @JSImport("react-360", "VrButton")
  object Component extends js.Object

  override val component = Component
}
