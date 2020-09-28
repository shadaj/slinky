package slinky.native

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
trait ScaledSize extends js.Object {
  var fontScale: Double = js.native
  var height: Double    = js.native
  var scale: Double     = js.native
  var width: Double     = js.native
}

@JSImport("react-native", "useWindowDimensions")
@js.native
object useWindowDimensions extends js.Object {
  def apply(): ScaledSize = js.native
}
