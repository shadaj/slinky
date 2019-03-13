package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.KeyboardEvent

// https://reactjs.org/docs/events.html?#keyboard-events
@js.native trait SyntheticKeyboardEvent[+TargetType] extends SyntheticEvent[TargetType, KeyboardEvent] {
  val altKey: Boolean = js.native
  val charCode: Int = js.native
  val ctrlKey: Boolean = js.native
  def getModifierState(key: String): Boolean = js.native
  val key: String = js.native
  val keyCode: Int = js.native
  val locale: String = js.native
  val location: Int = js.native
  val metaKey: Boolean = js.native
  val repeat: Boolean = js.native
  val shiftKey: Boolean = js.native
  val which: Int = js.native
}
