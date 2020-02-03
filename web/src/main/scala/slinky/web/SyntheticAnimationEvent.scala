package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.AnimationEvent

// https://reactjs.org/docs/events.html?#animation-events
@js.native
trait SyntheticAnimationEvent[+TargetType] extends SyntheticEvent[TargetType, AnimationEvent] {
  val animationName: String = js.native
  val pseudoElement: String = js.native
  val elapsedTime: Float    = js.native
}
