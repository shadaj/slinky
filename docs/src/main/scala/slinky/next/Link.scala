package slinky.next

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import slinky.core.annotations.react
import slinky.core.ExternalComponent

@react object Link extends ExternalComponent {
  case class Props(href: String)

  @JSImport("next/link", JSImport.Default)
  @js.native
  object Component extends js.Object
  
  val component = Component
}
