package slinky.core

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@js.native
trait SyntheticEvent[+TargetType, +EventType] extends js.Object {
  val bubbles: Boolean                = js.native
  val cancelable: Boolean             = js.native
  val currentTarget: TargetType       = js.native
  val defaultPrevented: Boolean       = js.native
  val eventPhase: Int                 = js.native
  val isTrusted: Boolean              = js.native
  val nativeEvent: EventType          = js.native
  def preventDefault(): Unit          = js.native
  def isDefaultPrevented(): Boolean   = js.native
  def stopPropagation(): Unit         = js.native
  def isPropagationStopped(): Boolean = js.native
  val target: TargetType              = js.native
  val timeStamp: Int                  = js.native
  @JSName("type") val `type`: String  = js.native
}
