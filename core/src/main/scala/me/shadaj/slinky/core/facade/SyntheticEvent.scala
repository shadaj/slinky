package me.shadaj.slinky.core.facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@js.native
trait SyntheticEvent[T] extends js.Object {
  val bubbles: Boolean = js.native
  val cancelable: Boolean = js.native
  val currentTarget: T = js.native
  val defaultPrevented: Boolean = js.native
  val eventPhase: Int = js.native
  val isTrusted: Boolean = js.native
//  val nativeEvent: Event TODO
  def preventDefault(): Unit = js.native
  def isDefaultPrevented: Boolean = js.native
  def stopPropagation(): Unit = js.native
  def isPropagationStopped: Boolean = js.native
  val target: T = js.native
  val timeStamp: Int = js.native
  @JSName("type") val `type`: String = js.native
}
