package me.shadaj.simple.react.core

import me.shadaj.simple.react.core.fascade.ComponentInstance

import scala.language.implicitConversions

import scala.scalajs.js.JSConverters._

import scala.scalajs.js

package object html extends attrs with tags with tagsApplied {
  implicit def stringToMod[O <: AppliedAttribute](s: String): HtmlComponentMod[O] = {
    ChildMod(s.asInstanceOf[ComponentInstance])
  }

  implicit def seqToMod[T, C <% Iterable[T], O <: AppliedAttribute](s: C)(implicit cv: T => HtmlComponentMod[O]): HtmlComponentMod[O] = {
    SeqMod(s.toSeq.map(cv))
  }

  implicit def instance2Mod[T, O <: AppliedAttribute](instance: T)(implicit cvt: T => ComponentInstance): HtmlComponentMod[O] = ChildMod(instance)
  implicit def attr2Mod[A <: AppliedAttribute, T](instance: T)(implicit cvt: T => A): HtmlComponentMod[A] = AttrMod[A](instance)

  implicit def component2Instance[A <: AppliedAttribute, T <: AppliedAttribute](component: HtmlComponent[A]): ComponentInstance = {
    HtmlComponent.create(component.name, js.Dictionary(component.attrs.map(m => m.name -> m.value): _*), component.children)
  }
}
