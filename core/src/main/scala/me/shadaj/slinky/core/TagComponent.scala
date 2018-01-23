package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}

import scala.language.implicitConversions
import scala.scalajs.js

trait Tag {
  type tagType <: TagElement
  def apply(mod: AttrPair[tagType], remainingMods: AttrPair[tagType]*): WithAttrs
  def apply(elems: ReactElement*): ReactElement
}

abstract class TagElement

final class CustomAttribute[T](private val name: String) {
  @inline def :=(v: T) = new AttrPair[Any](name, v.asInstanceOf[js.Any])
}

class AttrPair[-A](@inline final val name: String, @inline final val value: js.Any)

final class WithAttrs(@inline private[this] val name: String, @inline private[this] val attrs: js.Dictionary[js.Any]) {
  @inline def apply(children: ReactElement*): ReactElement = React.createElement(name, attrs, children: _*)
}

object WithAttrs {
  @inline implicit def shortCut(withAttrs: WithAttrs): ReactElement = withAttrs.apply()
}
