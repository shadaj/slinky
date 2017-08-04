package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

class AttrPair[A](val name: String, val value: js.Any)

trait TagMod[A] extends Any {
  def applyTo(component: TagComponent[A]): TagComponent[A]
}

object TagMod {
  implicit def optionToMod[T, A](option: Option[T])
                                (implicit cvt: T => TagMod[A]): TagMod[A] = {
    new SeqMod[A](option.map(cvt))
  }

  implicit def instance2Mod[T, A](instance: T)
                                 (implicit cvt: T => ComponentInstance): TagMod[A] = {
    new ChildMod(cvt(instance))
  }

  implicit def attr2Mod[T, A](instance: T)
                             (implicit cvt: T => AttrPair[A]): TagMod[A] = {
    new AttrMod[A](cvt(instance))
  }
}

class SeqMod[A](val mods: Iterable[TagMod[A]]) extends AnyVal with TagMod[A] {
  def applyTo(component: TagComponent[A]): TagComponent[A] = {
    mods.foldLeft(component) { (component, mod) =>
      mod.applyTo(component)
    }
  }
}

class ChildMod[A](val child: ComponentInstance) extends AnyVal with TagMod[A] {
  def applyTo(component: TagComponent[A]): TagComponent[A] = {
    component.copy(children = component.children :+ child)
  }
}

class AttrMod[A](val attr: AttrPair[A]) extends AnyVal with TagMod[A] {
  def applyTo(component: TagComponent[A]): TagComponent[A] = {
    component.copy(attrs = component.attrs :+ attr)
  }
}

case class TagComponent[A](name: String,
                           children: Seq[ComponentInstance] = Seq.empty,
                           attrs: Seq[AttrPair[A]] = Seq.empty) {
  def apply(newMods: TagMod[A]*): TagComponent[A] = {
    newMods.foldLeft(this) { (c, mod) =>
      mod.applyTo(c)
    }
  }
}

object TagComponent {
  def create(name: String, props: js.Dictionary[js.Any], contents: Seq[ComponentInstance] = Seq.empty): ComponentInstance = {
    React.createElement(name, props, contents: _*)
  }

  implicit def component2Instance[A](component: TagComponent[A]): ComponentInstance = {
    TagComponent.create(
      component.name,
      component.attrs.map(m => (m.name, m.value)).toMap.toJSDictionary,
      component.children
    )
  }
}
