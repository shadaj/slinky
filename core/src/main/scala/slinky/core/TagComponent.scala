package slinky.core

import slinky.core.facade.{React, ReactRaw, ReactElement}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.Dictionary

import scala.language.higherKinds

trait Tag extends Any {
  type tagType <: TagElement
  def apply(mods: TagMod[tagType]*): WithAttrs[tagType]
}

final class CustomTag(@inline private val name: String) extends Tag {
  override type tagType = Nothing

  @inline def apply(mods: TagMod[tagType]*): WithAttrs[tagType] = {
    WithAttrs[tagType](name, mods)
  }
}

trait Attr {
  type attrType
  type supports[T <: Tag] = AttrPair[attrType] => AttrPair[T#tagType]
}

abstract class TagElement {
  type EventTargetType
}

final class CustomAttribute[T](@inline private val name: String) {
  @inline def :=(v: T) = new AttrPair[Any](name, v.asInstanceOf[js.Any])
}

trait TagMod[-A] extends js.Object

object TagMod {
  @inline implicit def elemToTagMod[E](elem: E)(implicit ev: E => ReactElement): TagMod[Any] =
    ev(elem)
}

@js.native trait ReactElementMod extends TagMod[Any]

final class AttrPair[-A](@inline final val name: String,
                         @inline final val value: js.Any) extends TagMod[A]

final class WithAttrs[A](@inline private val args: js.Array[js.Any]) extends AnyVal {
  @inline def apply(children: ReactElement*): ReactElement = {
    if (args(0) == null) {
      throw new IllegalStateException("This tag has already been built into a ReactElement, and cannot be reused")
    }

    children.foreach(c => args.push(c))
    WithAttrs.build(this)
  }
}

object WithAttrs {
  @inline def apply[A](component: js.Any, mods: Seq[TagMod[A]]) = {
    val inst = new WithAttrs[A](js.Array(component, js.Dynamic.literal()))
    mods.foreach { m =>
      m match {
        case a: AttrPair[_] =>
          inst.args(1).asInstanceOf[js.Dictionary[js.Any]](a.name) = a.value
        case r =>
          inst.args.push(r.asInstanceOf[ReactElementMod])
      }
    }
    inst
  }

  @inline implicit def build(withAttrs: WithAttrs[_]): ReactElement = {
    if (withAttrs.args(0) == null) {
      throw new IllegalStateException("This tag has already been built into a ReactElement, and cannot be reused")
    }

    val ret = ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, withAttrs.args).asInstanceOf[ReactElement]

    withAttrs.args(0) = null

    ret
  }
}
