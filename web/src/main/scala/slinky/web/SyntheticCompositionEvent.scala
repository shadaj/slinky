package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.CompositionEvent

// https://reactjs.org/docs/events.html?#composition-events
@js.native
trait SyntheticCompositionEvent[+TargetType] extends SyntheticEvent[TargetType, CompositionEvent] {
  val data: String = js.native
}
