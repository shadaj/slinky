package slinky.web

import slinky.core.SyntheticEvent
import scala.scalajs.js
import org.scalajs.dom.InputEvent

//https://react.dev/reference/react-dom/components/common#inputevent-handler
@js.native
trait SyntheticInputEvent[+TargetType] extends SyntheticEvent[TargetType, InputEvent] {
  val data: String = js.native
}
