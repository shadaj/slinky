package slinky.web

import slinky.core.SyntheticEvent

import scala.scalajs.js
import org.scalajs.dom.{DataTransfer, DragEvent}

@js.native
trait SyntheticDragEvent[+TargetType] extends SyntheticEvent[TargetType, DragEvent] {
  def dataTransfer: DataTransfer = js.native
}
