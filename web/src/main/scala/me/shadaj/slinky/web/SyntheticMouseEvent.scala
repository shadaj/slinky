package me.shadaj.slinky.web

import me.shadaj.slinky.core.facade.SyntheticEvent

import scala.scalajs.js

@js.native
trait SyntheticMouseEvent[T] extends SyntheticEvent[T] {
  val altKey: Boolean = js.native
  val button: Int = js.native
  val buttons: Int = js.native
  val clientX: Int = js.native
  val clientY: Int = js.native
  val ctrlKey: Boolean = js.native
  def getModifierState(key: Int): Boolean = js.native
  val metaKey: Boolean = js.native
  val pageX: Int = js.native
  val pageY: Int = js.native
  val relatedTarget: T = js.native
  val screenX: Int = js.native
  val screenY: Int = js.native
  val shiftKey: Boolean = js.native
}
