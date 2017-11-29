package me.shadaj.slinky.core.facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@js.native
trait SyntheticEvent[T] extends js.Object {
  val bubbles: Boolean
  val cancelable: Boolean
  val currentTarget: T
  val defaultPrevented: Boolean
  val eventPhase: Int
  val isTrusted: Boolean
//  val nativeEvent: Event TODO
  def preventDefault(): Unit = js.native
  def isDefaultPrevented: Boolean = js.native
  def stopPropagation(): Unit = js.native
  def isPropagationStopped: Boolean = js.native
  val target: T
  val timeStamp: Int
  @JSName("type") val `type`: String = js.native
}
