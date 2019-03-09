package slinky.core

import slinky.core.facade.{React, ReactElement}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.JSName

@js.native trait SyntheticEvent[TargetType, EventType] extends js.Object {
  val bubbles: Boolean = js.native
  val cancelable: Boolean = js.native
  val currentTarget: TargetType = js.native
  val defaultPrevented: Boolean = js.native
  val eventPhase: Int = js.native
  val isTrusted: Boolean = js.native
  val nativeEvent: EventType = js.native
  def preventDefault(): Unit = js.native
  def isDefaultPrevented(): Boolean = js.native
  def stopPropagation(): Unit = js.native
  def isPropagationStopped(): Boolean = js.native
  val target: TargetType = js.native
  val timeStamp: Int = js.native
  @JSName("type") val `type`: String = js.native
}

trait Tag extends Any {
  type tagType <: TagElement
  def apply(mods: TagMod[tagType]*): WithAttrs[tagType]
}

object Tag {
  implicit object ApplyReactElements
}

final class CustomTag(private val name: String) extends Tag {
  override type tagType = Nothing

  def apply(mods: TagMod[tagType]*): WithAttrs[tagType] = {
    new WithAttrs[tagType](name, js.Dictionary.empty, js.Array()).apply(mods: _*)
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

trait TagMod[-A] extends Any {
  def applyTo(dictionary: js.Dictionary[js.Any], children: js.Array[ReactElement]): Unit
}

final class ReactElementMod(private val elem: ReactElement) extends AnyVal with TagMod[Any] {
  @inline def applyTo(dictionary: Dictionary[js.Any], children: js.Array[ReactElement]): Unit = {
    children.push(elem)
  }
}

object TagMod {
  @inline implicit def elemToTagMod[E](elem: E)(implicit ev: E => ReactElement): TagMod[Any] =
    new ReactElementMod(ev(elem))

  @inline implicit def elemsToTagMods(elems: Seq[ReactElement]): Seq[TagMod[Any]] =
    elems.map(elemToTagMod(_)(identity))
}

class AttrPair[-A](@inline final val name: String, @inline final val value: js.Any) extends TagMod[A] {
  @inline def applyTo(dictionary: Dictionary[js.Any], children: js.Array[ReactElement]): Unit = {
    dictionary(name) = value
  }
}

final class WithAttrs[A](@inline private[WithAttrs] val name: String,
                         @inline private[WithAttrs] val attrs: js.Dictionary[js.Any],
                         @inline private[WithAttrs] val children: js.Array[ReactElement]) {
  @inline def apply(mods: TagMod[A]*): WithAttrs[A] = {
    mods.foreach(_.applyTo(attrs, children))
    this
  }
}

object WithAttrs {
  @inline implicit def shortCut(withAttrs: WithAttrs[_]): ReactElement = {
    React.createElement(withAttrs.name, withAttrs.attrs, withAttrs.children: _*)
  }
}
