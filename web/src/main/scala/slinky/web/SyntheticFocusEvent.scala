package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.{EventTarget, FocusEvent}

// https://reactjs.org/docs/events.html?#focus-events
@js.native
trait SyntheticFocusEvent[+TargetType] extends SyntheticEvent[TargetType, FocusEvent] {
  val relatedTarget: EventTarget = js.native
}
