package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.TransitionEvent

// https://reactjs.org/docs/events.html?#transition-events
@js.native trait SyntheticTransitionEvent[+TargetType] extends SyntheticEvent[TargetType, TransitionEvent] {
  val propertyName: String = js.native
  val pseudoElement: String = js.native
  val elapsedTime: Float = js.native
}
