package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.WheelEvent

// https://reactjs.org/docs/events.html?#wheel-events
@js.native trait SyntheticWheelEvent[TargetType] extends SyntheticEvent[TargetType, WheelEvent] {
  val deltaMode: Int = js.native
  val deltaX: Double = js.native
  val deltaY: Double = js.native
  val deltaZ: Double = js.native
}
