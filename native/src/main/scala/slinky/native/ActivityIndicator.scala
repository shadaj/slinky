package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@react object ActivityIndicator extends ExternalComponent {
  case class Props(
    animating: js.UndefOr[Boolean] = js.undefined,
    color: js.UndefOr[String] = js.undefined,
    size: js.UndefOr[String | Int] = js.undefined,
    hidesWhenStopped: js.UndefOr[Boolean] = js.undefined
  )

  @js.native
  @JSImport("react-native", "ActivityIndicator")
  object Component extends js.Object

  override val component = Component
}
