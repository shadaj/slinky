package me.shadaj.simple.react.core

import me.shadaj.simple.react.core.fascade.ComponentInstance

import scala.language.implicitConversions

import scala.scalajs.js.JSConverters._

package object html extends attrs with tags with tagsApplied {
  implicit def stringToMod[O <: AppliedAttribute](s: String): HtmlComponentMod[O] = {
    new ChildMod(s.asInstanceOf[ComponentInstance])
  }

  implicit def seqToMod[T, C, O <: AppliedAttribute](s: C)
                                                    (implicit iev: C => Iterable[T],
                                                     cv: T => HtmlComponentMod[O]): HtmlComponentMod[O] = {
    new SeqMod(iev(s).map(cv))
  }

  implicit def instance2Mod[T, O <: AppliedAttribute](instance: T)
                                                     (implicit cvt: T => ComponentInstance): HtmlComponentMod[O] = {
    new ChildMod(cvt(instance))
  }

  implicit def attr2Mod[A <: AppliedAttribute, T](instance: T)
                                                 (implicit cvt: T => A): HtmlComponentMod[A] = {
    new AttrMod[A](cvt(instance))
  }

  implicit def component2Instance[A <: AppliedAttribute](component: HtmlComponent[A]): ComponentInstance = {
    HtmlComponent.create(
      component.name,
      component.attrs.map(m => (m.name, m.value)).toMap.toJSDictionary,
      component.children
    )
  }
}
