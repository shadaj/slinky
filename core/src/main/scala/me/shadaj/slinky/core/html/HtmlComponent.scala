package me.shadaj.slinky.core.html

import me.shadaj.slinky.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.scalajs.js

class AttrPair[A](val name: String, val value: js.Any)

trait HtmlComponentMod[A] extends Any {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A]
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
}
