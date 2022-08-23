package slinky.next

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

import slinky.core.annotations.react
import slinky.core.ExternalComponent

@react object Image extends ExternalComponent {
  case class Props(src: js.Object, layout: js.UndefOr[String] = js.undefined, priority: js.UndefOr[Boolean] = js.undefined)

  @JSImport("next/image", JSImport.Default)
  @js.native
  object Component extends js.Object
  
  val component = Component
}
