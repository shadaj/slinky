package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ReactElement, React}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

class CustomAttribute[T](name: String) {
  def :=(v: T) = new AttrPair[Any](name, v.asInstanceOf[js.Any])
}

class AttrPair[-A](val name: String, val value: js.Any)

trait TagMod[-A] extends Any {
  def applyTo[T <: A](component: TagComponent[T]): TagComponent[T]
}

object TagMod {
  implicit def optionToMod[T, A](option: Option[T])
                                (implicit cvt: T => TagMod[A]): TagMod[A] = {
    new SeqMod[A](option.map(cvt))
  }

  implicit def instance2Mod[T, A](instance: T)
                                 (implicit cvt: T => ReactElement): TagMod[A] = {
    new ChildMod(cvt(instance))
  }

  implicit def attr2Mod[T, A](instance: T)
                             (implicit cvt: T => AttrPair[A]): TagMod[A] = {
    new AttrMod[A](cvt(instance))
  }
}

class SeqMod[-A](val mods: Iterable[TagMod[A]]) extends AnyVal with TagMod[A] {
  def applyTo[T <: A](component: TagComponent[T]): TagComponent[T] = {
    mods.foldLeft(component) { (component, mod) =>
      mod.applyTo(component)
    }
  }
}

class ChildMod[-A](val child: ReactElement) extends AnyVal with TagMod[A] {
  def applyTo[T <: A](component: TagComponent[T]): TagComponent[T] = {
    component.copy(children = component.children :+ child)
  }
}

class AttrMod[-A](val attr: AttrPair[A]) extends AnyVal with TagMod[A] {
  def applyTo[T <: A](component: TagComponent[T]): TagComponent[T] = {
    component.copy(attrs = component.attrs :+ attr)
  }
}

// TagComponent[div] is also a TagComponent[Any]
case class TagComponent[+A](name: String,
                            children: Seq[ReactElement] = Seq.empty,
                            attrs: Seq[AttrPair[_]] = Seq.empty) {
  def apply(newMods: TagMod[A]*): TagComponent[A] = {
    newMods.foldLeft(this) { (c, mod) =>
      mod.applyTo(c)
    }
  }
}

object TagComponent {
  def create(name: String, props: js.Dictionary[js.Any], contents: Seq[ReactElement] = Seq.empty): ReactElement = {
    React.createElement(name, props, contents: _*)
  }

  implicit def component2Instance(component: TagComponent[_]): ReactElement = {
    TagComponent.create(
      component.name,
      component.attrs.map(m => (m.name, m.value)).toMap.toJSDictionary,
      component.children
    )
  }
}
