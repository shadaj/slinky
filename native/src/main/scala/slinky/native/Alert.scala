package slinky.native

import slinky.readwrite.ObjectOrWritten

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

case class AlertButton(text: String, onPress: () => Unit)
case class AlertOptions(cancelable: js.UndefOr[Boolean] = js.undefined)

@js.native
@JSImport("react-native", "Alert")
object Alert extends js.Object {
  def alert(title: String,
            message: js.UndefOr[String] = js.undefined,
            buttons: js.UndefOr[ObjectOrWritten[Seq[AlertButton]]] = js.undefined,
            options: js.UndefOr[ObjectOrWritten[AlertOptions]] = js.undefined,
            `type`: js.UndefOr[String] = js.undefined): Unit = js.native
}
