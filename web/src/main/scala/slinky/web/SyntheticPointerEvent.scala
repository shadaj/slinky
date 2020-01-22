package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.PointerEvent

// https://reactjs.org/docs/events.html#pointer-events
@js.native trait SyntheticPointerEvent[+TargetType] extends SyntheticEvent[TargetType, PointerEvent] {
  val pointerId: Int = js.native
  val width: Double = js.native
  val height: Double = js.native
  val pressure: Double = js.native
  val tangentialPressure: Double = js.native
  val tiltX: Double = js.native
  val tiltY: Double = js.native
  val twist: Double = js.native
  val pointerType: String = js.native
  val isPrimary: Boolean = js.native
}
