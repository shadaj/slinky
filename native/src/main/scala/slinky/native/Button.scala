package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object Button extends ExternalComponent {
  case class Props(onPress: () => Unit,
                   title: String,
                   accessibilityLabel: js.UndefOr[String] = js.undefined,
                   color: js.UndefOr[String] = js.undefined,
                   disabled: js.UndefOr[Boolean] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined,
                   hasTVPreferredFocus: js.UndefOr[Boolean] = js.undefined)

  @js.native
  @JSImport("react-native", "Button")
  object Component extends js.Object

  override val component = Component
}
