package slinky.vr

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@react object Image extends ExternalComponent {
  case class Props(source: js.Object)

  @js.native
  @JSImport("react-360", "Image")
  object Component extends js.Object

  override val component = Component
}
