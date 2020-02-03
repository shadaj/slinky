package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object Switch extends ExternalComponent {
  case class Props(
    disabled: js.UndefOr[Boolean] = js.undefined,
    onTintColor: js.UndefOr[String] = js.undefined,
    onValueChange: js.UndefOr[Boolean => Unit] = js.undefined,
    testID: js.UndefOr[String] = js.undefined,
    thumbTintColor: js.UndefOr[String] = js.undefined,
    tintColor: js.UndefOr[String] = js.undefined,
    value: js.UndefOr[Boolean] = js.undefined
  )

  @js.native
  @JSImport("react-native", "Switch")
  object Component extends js.Object

  override val component = Component
}
