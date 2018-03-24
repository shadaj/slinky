package slinky.vr

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object Pano extends ExternalComponent {
  case class Props(source: Asset)

  @js.native
  @JSImport("react-vr", "Pano")
  object Component extends js.Object

  override val component = Component
}
