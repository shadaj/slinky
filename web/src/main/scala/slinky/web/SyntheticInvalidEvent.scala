package slinky.web

import org.scalajs.dom.Event
import slinky.core.SyntheticEvent

import scala.scalajs.js

@js.native
trait SyntheticInvalidEvent[+TargetType] extends SyntheticEvent[TargetType, Event]
