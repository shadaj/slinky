package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.{UIEvent, Window}

// https://reactjs.org/docs/events.html?#ui-events
@js.native
trait SyntheticUIEvent[+TargetType] extends SyntheticEvent[TargetType, UIEvent] {
  val detail: Double = js.native
  val view: Window   = js.native
}
