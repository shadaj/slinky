package me.shadaj.slinky.core.html

import me.shadaj.slinky.core.facade.ComponentInstance
import me.shadaj.slinky.core.facade.{ComponentInstance, React}

import scala.language.implicitConversions
import scala.scalajs.js

abstract class Attr[V, P <: AttrPair[V]](val name: String) {
  def :=(v: V): P
}

abstract class AttrPair[V](val name: String, val value: V)

abstract class AppliedAttribute {
  val name: String
  val value: js.Any
}

trait HtmlComponentMod[A <: AppliedAttribute] extends Any {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A]
}

class SeqMod[A <: AppliedAttribute](val mods: Iterable[HtmlComponentMod[A]]) extends AnyVal with HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    mods.foldLeft(component) { (component, mod) =>
      mod.applyTo(component)
    }
  }
}

class ChildMod[A <: AppliedAttribute](val child: ComponentInstance) extends AnyVal with HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    component.copy(children = component.children :+ child)
  }
}

class AttrMod[A <: AppliedAttribute](val attr: A) extends AnyVal with HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    component.copy(attrs = component.attrs :+ attr)
  }
}

case class HtmlComponent[A <: AppliedAttribute](name: String,
                                                children: Seq[ComponentInstance] = Seq.empty,
                                                attrs: Seq[A] = Seq.empty) {
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
