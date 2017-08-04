package me.shadaj.slinky.core.html

import me.shadaj.slinky.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

class AttrPair[A](val name: String, val value: js.Any)

trait HtmlComponentMod[A] extends Any {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A]
}

object HtmlComponentMod {
  implicit def optionToMod[T, A](option: Option[T])
                                (implicit cvt: T => HtmlComponentMod[A]): HtmlComponentMod[A] = {
    new SeqMod[A](option.map(cvt))
  }

  implicit def instance2Mod[T, A](instance: T)
                                 (implicit cvt: T => ComponentInstance): HtmlComponentMod[A] = {
    new ChildMod(cvt(instance))
  }

  implicit def attr2Mod[T, A](instance: T)
                             (implicit cvt: T => AttrPair[A]): HtmlComponentMod[A] = {
    new AttrMod[A](cvt(instance))
  }
}

class SeqMod[A](val mods: Iterable[HtmlComponentMod[A]]) extends AnyVal with HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    mods.foldLeft(component) { (component, mod) =>
      mod.applyTo(component)
    }
  }
}

class ChildMod[A](val child: ComponentInstance) extends AnyVal with HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    component.copy(children = component.children :+ child)
  }
}

class AttrMod[A](val attr: AttrPair[A]) extends AnyVal with HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    component.copy(attrs = component.attrs :+ attr)
  }
}

case class HtmlComponent[A](name: String,
                            children: Seq[ComponentInstance] = Seq.empty,
                            attrs: Seq[AttrPair[A]] = Seq.empty) {
  def apply(newMods: HtmlComponentMod[A]*): HtmlComponent[A] = {
    newMods.foldLeft(this) { (c, mod) =>
      mod.applyTo(c)
    }
  }
}

object HtmlComponent {
  def create(name: String, props: js.Dictionary[js.Any], contents: Seq[ComponentInstance] = Seq.empty): ComponentInstance = {
    React.createElement(name, props, contents: _*)
  }

  implicit def component2Instance[A](component: HtmlComponent[A]): ComponentInstance = {
    HtmlComponent.create(
      component.name,
      component.attrs.map(m => (m.name, m.value)).toMap.toJSDictionary,
      component.children
    )
  }
}
