package me.shadaj.slinky.history

import org.scalajs.dom.History

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
trait RichHistory extends History {
  def listen(listener: js.Function0[Unit]): Unit = js.native
}

@JSImport("history", JSImport.Default)
@js.native
object History extends js.Object {
  def createBrowserHistory(): RichHistory = js.native
}
