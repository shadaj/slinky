package slinky.core

import slinky.core.facade.{React, ReactElement}

import scala.language.implicitConversions
import scala.scalajs.js

trait Tag extends Any {
  type tagType <: TagElement

  def apply(mod: AttrPair[tagType], remainingMods: AttrPair[tagType]*): WithAttrs[tagType]
  
  def apply(elems: ReactElement*): ReactElement
}

final class CustomTag(private val name: String) extends Tag {
  override type tagType = Nothing

  @inline override def apply(mod: AttrPair[Nothing], remainingMods: AttrPair[Nothing]*): WithAttrs[tagType] = {
    new WithAttrs(name, js.Dictionary((mod +: remainingMods).map(m => m.name -> m.value): _*))
  }

  @inline override def apply(elems: ReactElement*): ReactElement = {
    React.createElement(name, js.Dictionary.empty[js.Any], elems: _*)
  }
}

trait Attr {
  type attrType
  type supports[T <: Tag] = AttrPair[attrType] => AttrPair[T#tagType]
}

abstract class TagElement

final class CustomAttribute[T](private val name: String) {
  @inline def :=(v: T) = new AttrPair[Any](name, v.asInstanceOf[js.Any])
}

class AttrPair[-A](@inline final val name: String, @inline final val value: js.Any)

final class WithAttrs[T <: TagElement](@inline private[this] val name: String, @inline private[this] val attrs: js.Dictionary[js.Any]) {
  @inline def withDynamicAttributes(dynamicAttributes: AttrPair[T]*): WithAttrs[T] = {
    dynamicAttributes.foreach { a =>
      attrs(a.name) = a.value
    }

    new WithAttrs(name, attrs)
  }

  @inline def apply(children: ReactElement*): ReactElement = React.createElement(name, attrs, children: _*)
}

object WithAttrs {
  @inline implicit def shortCut(withAttrs: WithAttrs[_]): ReactElement = withAttrs.apply()
}
