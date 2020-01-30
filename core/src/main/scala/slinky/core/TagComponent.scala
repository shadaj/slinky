package slinky.core

import slinky.core.facade.{ReactElement, ReactRaw}

import scala.scalajs.js

trait Tag {
  type tagType <: TagElement
  def apply(mods: TagMod[tagType]*): WithAttrs[tagType]
}

final class CustomTag(@inline private val name: String) extends Tag {
  override type tagType = Nothing

  @inline def apply(mods: TagMod[tagType]*): WithAttrs[tagType] = {
    WithAttrs[tagType](name, mods)
  }
}
object CustomTag {
  def apply(name: String): CustomTag = new CustomTag(name)
}

trait Attr {
  type attrType
  type supports[T <: Tag] = AttrPair[attrType] => AttrPair[T#tagType]
}

abstract class TagElement {
  type RefType
}

final class CustomAttribute[T](@inline private val name: String) {
  @inline def :=(v: T) = new AttrPair[Any](name, v.asInstanceOf[js.Any])
  @inline def :=(v: Option[T]) = new OptionalAttrPair[Any](name, v.asInstanceOf[Option[js.Any]])
}
object CustomAttribute {
  def apply[T](name: String): CustomAttribute[T] = new CustomAttribute[T](name)
}

trait TagMod[-A] extends js.Object

object TagMod {
  @inline implicit def elemToTagMod[E](elem: E)(implicit ev: E => ReactElement): TagMod[Any] =
    ev(elem)
}

@js.native trait ReactElementMod extends TagMod[Any]

@js.native trait RefAttr[-T] extends js.Object
object RefAttr {
  @inline implicit def fromReact[T](in: slinky.core.facade.ReactRef[T]): RefAttr[T] = in.asInstanceOf[RefAttr[T]]
}

final class AttrPair[-A](@inline final val name: String,
                         @inline final val value: js.Any) extends TagMod[A]

final class OptionalAttrPair[-A](@inline final val name: String,
                                 @inline final val value: Option[js.Any]) extends TagMod[A]

object OptionalAttrPair {
  @inline def optionToJsOption[T](o: Option[T])(implicit a: T => js.Any): Option[js.Any] =
    o.map(a(_))

  implicit val identity: js.Any => js.Any = Predef.identity
}

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
    mods.foreach {
      case a: AttrPair[_] =>
        inst.args(1).asInstanceOf[js.Dictionary[js.Any]](a.name) = a.value
      case o: OptionalAttrPair[_] =>
        if (o.value.isDefined) inst.args(1).asInstanceOf[js.Dictionary[js.Any]](o.name) = o.value.get
      case r =>
        inst.args.push(r.asInstanceOf[ReactElementMod])
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

  @inline implicit def buildContainer[F[_]](withAttrs: F[WithAttrs[_]])(implicit f: ReactElementContainer[F]): F[ReactElement] = {
    f.map(withAttrs)(build)
  }
}