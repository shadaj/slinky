package slinky.native

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-native", "Keyboard")
object Keyboard extends js.Object {
  def addListener(eventName: String, callBack: js.Function0[Unit]): Unit = js.native

  def removeListener(eventName: String, callBack: js.Function0[Unit]): Unit = js.native

  def removeAllListeners(eventName: String): Unit = js.native

  def dismiss(): Unit = js.native
}
