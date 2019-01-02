package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object SafeAreaView extends ExternalComponent {
  case class Props(style: js.UndefOr[js.Object] = js.undefined)

  @js.native
  @JSImport("react-native", "SafeAreaView")
  object Component extends js.Object

  override val component = Component
}
