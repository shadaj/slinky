package slinky.web

import org.scalajs.dom.Event
import slinky.core.SyntheticEvent

import scala.scalajs.js

//https://legacy.reactjs.org/docs/events.html#form-events
@js.native
trait SyntheticFormEvent[+TargetType] extends SyntheticEvent[TargetType, Event]
