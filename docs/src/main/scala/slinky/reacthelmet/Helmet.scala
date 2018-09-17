package slinky.reacthelmet

import slinky.core.ExternalComponentNoProps

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-helmet", JSImport.Namespace)
@js.native
object ReactHelmet extends js.Object {
  val Helmet: HelmetStatic = js.native
}

@js.native
trait HelmetStatic extends js.Object {
  def renderStatic(): HelmetRendered = js.native
}

@js.native trait HelmetRendered extends js.Object {
  val title: js.Object = js.native
  val meta: js.Object = js.native
  val link: js.Object = js.native
  val style: js.Object = js.native
}

object Helmet extends ExternalComponentNoProps {
  override val component = ReactHelmet.Helmet
}
