package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.ComponentInstance

import scala.language.implicitConversions
import scala.scalajs.js.JSConverters._

package object html extends internal.tags with internal.tagsApplied with internal.attrs {
  implicit def stringToInstance(s: String): ComponentInstance = {
    s.asInstanceOf[ComponentInstance]
  }

  implicit def seqInstanceToInstance[T](s: Iterable[T])(implicit cv: T => ComponentInstance): ComponentInstance = {
    s.map(cv).toJSArray.asInstanceOf[ComponentInstance]
  }

  implicit def component2Instance[A <: AppliedAttribute](component: HtmlComponent[A]): ComponentInstance = {
    HtmlComponent.create(
      component.name,
      component.attrs.map(m => (m.name, m.value)).toMap.toJSDictionary,
      component.children
    )
  }

  implicit def optionToMod[T, O <: AppliedAttribute](option: Option[T])
                                                    (implicit cvt: T => HtmlComponentMod[O]): HtmlComponentMod[O] = {
    new SeqMod[O](option.map(cvt))
  }

  implicit def instance2Mod[T, O <: AppliedAttribute](instance: T)
                                                     (implicit cvt: T => ComponentInstance): HtmlComponentMod[O] = {
    new ChildMod(cvt(instance))
  }

  implicit def attr2Mod[A <: AppliedAttribute, T](instance: T)
                                                 (implicit cvt: T => A): HtmlComponentMod[A] = {
    new AttrMod[A](cvt(instance))
  }
}
