package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.{MouseEvent, EventTarget}

// https://reactjs.org/docs/events.html#mouse-events
@js.native trait SyntheticMouseEvent[+TargetType] extends SyntheticEvent[TargetType, MouseEvent] {
  val altKey: Boolean = js.native
  val button: Int = js.native
  val buttons: Int = js.native
  val clientX: Double = js.native
  val clientY: Double = js.native
  val ctrlKey: Boolean = js.native
  def getModifierState(key: String): Boolean = js.native
  val metaKey: Boolean = js.native
  val pageX: Double = js.native
  val pageY: Double = js.native
  val relatedTarget: EventTarget = js.native
  val screenX: Double = js.native
  val screenY: Double = js.native
  val shiftKey: Boolean = js.native
}
