package slinky.next

import slinky.core.ExternalComponentNoProps

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Head extends ExternalComponentNoProps {
  @JSImport("next/head", JSImport.Default)
  @js.native
  object Component extends js.Object

  override val component = Component
}
