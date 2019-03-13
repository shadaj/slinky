package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.{TouchEvent, TouchList}

// https://reactjs.org/docs/events.html?#touch-events
@js.native trait SyntheticTouchEvent[TargetType] extends SyntheticEvent[TargetType, TouchEvent] {
  val altKey: Boolean = js.native
  val changedTouches: TouchList = js.native
  val ctrlKey: Boolean = js.native
  def getModifierState(key: String): Boolean = js.native
  val metaKey: Boolean = js.native
  val shiftKey: Boolean = js.native
  val targetTouches: TouchList = js.native
  val touches: TouchList = js.native
}
