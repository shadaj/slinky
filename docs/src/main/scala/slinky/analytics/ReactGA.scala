package slinky.analytics

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-ga", JSImport.Default)
@js.native
object ReactGA extends js.Object {
  def initialize(id: String): Unit = js.native
  def pageview(path: String): Unit = js.native
}
