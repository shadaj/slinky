package slinky.native

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-native", "Clipboard")
object RawClipboard extends js.Object {
  def getString(): js.Promise[String] = js.native
  def setString(content: String): Unit = js.native
}

object Clipboard {
  def getString: Future[String] = RawClipboard.getString().toFuture
  def setString(content: String): Unit = RawClipboard.setString(content)
}