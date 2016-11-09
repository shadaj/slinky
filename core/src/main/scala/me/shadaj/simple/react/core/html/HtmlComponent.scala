package me.shadaj.simple.react.core.html

import me.shadaj.simple.react.core.fascade.{ComponentInstance, React}
import scala.language.implicitConversions

import scala.scalajs.js

abstract class Attr[V, P <: AttrPair[V]](val name: String) {
  def :=(v: V): P
}

class AttrPair[V](val name: String, val value: V)

abstract class AppliedAttribute {
  val name: String
  val value: js.Any
}

trait HtmlComponentMod[A <: AppliedAttribute] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A]
}

case class SeqMod[A <: AppliedAttribute](mods: Seq[HtmlComponentMod[A]]) extends HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    mods.foldLeft(component) { (component, mod) =>
      mod.applyTo(component)
    }
  }
}

case class ChildMod[A <: AppliedAttribute](child: ComponentInstance) extends HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    component.copy(children = component.children :+ child)
  }
}

case class AttrMod[A <: AppliedAttribute](attr: A) extends HtmlComponentMod[A] {
  def applyTo(component: HtmlComponent[A]): HtmlComponent[A] = {
    component.copy(attrs = component.attrs :+ attr)
  }
}

case class HtmlComponent[A <: AppliedAttribute](name: String, children: Seq[ComponentInstance] = Seq.empty, attrs: Seq[A] = Seq.empty) {
  def apply(newMods: HtmlComponentMod[A]*): HtmlComponent[A] = {
    newMods.foldLeft(this) { (c, mod) =>
      mod.applyTo(c)
    }
  }
}

object HtmlComponent {
  def create(name: String, props: js.Dictionary[js.Any]) = {
    React.createElement(name, props)
  }

  def create(name: String, props: js.Dictionary[js.Any], contents: Seq[ComponentInstance]) = {
    React.createElement(name, props, contents: _*)
  }
}
