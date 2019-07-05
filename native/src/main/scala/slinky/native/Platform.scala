package slinky.native

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-native", "Platform")
@js.native
object Platform extends js.Object {
  val OS: String = js.native
}
