package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.{ClipboardEvent, DataTransfer}

// https://reactjs.org/docs/events.html#clipboard-events
@js.native trait SyntheticClipboardEvent[+TargetType] extends SyntheticEvent[TargetType, ClipboardEvent] {
  val clipboardData: DataTransfer = js.native
}
