package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object TouchableHighlight extends ExternalComponent {
  case class Props(onPress: () => Unit,
                   style: js.UndefOr[js.Object] = js.undefined)

  @js.native
  @JSImport("react-native", "TouchableHighlight")
  object Component extends js.Object

  override val component = Component
}